package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentBag<T extends ConcurrentBag.IConcurrentBagEntry> implements AutoCloseable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
   private final QueuedSequenceSynchronizer synchronizer;
   private final CopyOnWriteArrayList<T> sharedList;
   private final boolean weakThreadLocals;
   private final ThreadLocal<List<Object>> threadList;
   private final ConcurrentBag.IBagStateListener listener;
   private final AtomicInteger waiters;
   private volatile boolean closed;

   public ConcurrentBag(ConcurrentBag.IBagStateListener listener) {
      this.listener = listener;
      this.weakThreadLocals = this.useWeakThreadLocals();
      this.waiters = new AtomicInteger();
      this.sharedList = new CopyOnWriteArrayList<>();
      this.synchronizer = new QueuedSequenceSynchronizer();
      if (this.weakThreadLocals) {
         this.threadList = new ThreadLocal<>();
      } else {
         this.threadList = new ThreadLocal<List<Object>>() {
            protected List<Object> initialValue() {
               return new FastList<>(ConcurrentBag.IConcurrentBagEntry.class, 16);
            }
         };
      }
   }

   public T borrow(long timeout, TimeUnit timeUnit) throws InterruptedException {
      List<Object> list = this.threadList.get();
      if (this.weakThreadLocals && list == null) {
         list = new ArrayList<>(16);
         this.threadList.set(list);
      }

      for (int i = list.size() - 1; i >= 0; i--) {
         Object entry = list.remove(i);
         T bagEntry = (T)(this.weakThreadLocals ? ((WeakReference)entry).get() : entry);
         if (bagEntry != null && bagEntry.compareAndSet(0, 1)) {
            return bagEntry;
         }
      }

      timeout = timeUnit.toNanos(timeout);
      Future<Boolean> addItemFuture = null;
      long startScan = System.nanoTime();
      long originTimeout = timeout;
      this.waiters.incrementAndGet();

      whileLoop:
      while (true) {
         ConcurrentBag.IConcurrentBagEntry iConcurrentBagEntry;
         try {
            long startSeq = this.synchronizer.currentSequence();
            Iterator iterator = this.sharedList.iterator();

            T bagEntry;
            do {
               if (!iterator.hasNext()) {
                  if (startSeq < this.synchronizer.currentSequence()) {
                     continue whileLoop;
                  }

                  if (addItemFuture == null || addItemFuture.isDone()) {
                     addItemFuture = this.listener.addBagItem();
                  }

                  timeout = originTimeout - (System.nanoTime() - startScan);
                  if (timeout > 10000L && this.synchronizer.waitUntilSequenceExceeded(startSeq, timeout)) {
                     continue whileLoop;
                  }

                  return null;
               }

               bagEntry = (T)iterator.next();
            } while (!bagEntry.compareAndSet(0, 1));

            if (this.waiters.get() > 1 && addItemFuture == null) {
               this.listener.addBagItem();
            }

            iConcurrentBagEntry = bagEntry;
         } finally {
            this.waiters.decrementAndGet();
         }

         return (T)iConcurrentBagEntry;
      }
   }

   public void requite(T bagEntry) {
      bagEntry.lazySet(0);
      List<Object> threadLocalList = this.threadList.get();
      if (threadLocalList != null) {
         threadLocalList.add(this.weakThreadLocals ? new WeakReference(bagEntry) : bagEntry);
      }

      this.synchronizer.signal();
   }

   public void add(T bagEntry) {
      if (this.closed) {
         LOGGER.info("ConcurrentBag has been closed, ignoring add()");
         throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
      } else {
         this.sharedList.add(bagEntry);
         this.synchronizer.signal();
      }
   }

   public boolean remove(T bagEntry) {
      if (!bagEntry.compareAndSet(1, -1) && !bagEntry.compareAndSet(-2, -1) && !this.closed) {
         LOGGER.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", bagEntry);
         return false;
      } else {
         boolean removed = this.sharedList.remove(bagEntry);
         if (!removed && !this.closed) {
            LOGGER.warn("Attempt to remove an object from the bag that does not exist: {}", bagEntry);
         }

         return removed;
      }
   }

   @Override
   public void close() {
      this.closed = true;
   }

   public List<T> values(int state) {
      ArrayList<T> list = new ArrayList<>(this.sharedList.size());

      for (T entry : this.sharedList) {
         if (entry.getState() == state) {
            list.add(entry);
         }
      }

      return list;
   }

   public List<T> values() {
      return (List<T>)this.sharedList.clone();
   }

   public boolean reserve(T bagEntry) {
      return bagEntry.compareAndSet(0, -2);
   }

   public void unreserve(T bagEntry) {
      if (bagEntry.compareAndSet(-2, 0)) {
         this.synchronizer.signal();
      } else {
         LOGGER.warn("Attempt to relinquish an object to the bag that was not reserved: {}", bagEntry);
      }
   }

   public int getPendingQueue() {
      return this.synchronizer.getQueueLength();
   }

   public int getCount(int state) {
      int count = 0;

      for (T entry : this.sharedList) {
         if (entry.getState() == state) {
            count++;
         }
      }

      return count;
   }

   public int size() {
      return this.sharedList.size();
   }

   public void dumpState() {
      for (T bagEntry : this.sharedList) {
         LOGGER.info(bagEntry.toString());
      }
   }

   private boolean useWeakThreadLocals() {
      try {
         return System.getProperty("io.lumine.mythic.bukkit.utils.storage.sql.hikari.useWeakReferences") != null
            ? Boolean.getBoolean("io.lumine.mythic.bukkit.utils.storage.sql.hikari.useWeakReferences")
            : this.getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
      } catch (SecurityException e) {
         return true;
      }
   }

   public interface IBagStateListener {
      Future<Boolean> addBagItem();
   }

   public interface IConcurrentBagEntry {
      int STATE_NOT_IN_USE = 0;
      int STATE_IN_USE = 1;
      int STATE_REMOVED = -1;
      int STATE_RESERVED = -2;

      boolean compareAndSet(int value, int value2);

      void lazySet(int value);

      int getState();
   }
}
