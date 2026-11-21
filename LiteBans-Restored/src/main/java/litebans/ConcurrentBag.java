package litebans;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

public class ConcurrentBag implements AutoCloseable {
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(ConcurrentBag.class);
   private final CopyOnWriteArrayList count;
   private final boolean flag2;
   private final ThreadLocal threadLocal;
   private final IBagStateListener poolAInterface;
   private final AtomicInteger waitingThreadCount;
   private volatile boolean flag3;
   private final SynchronousQueue synchronousQueue;

   public ConcurrentBag(IBagStateListener poolAInterface) {
      this.poolAInterface = poolAInterface;
      this.flag2 = this.useWeakReferences();
      this.synchronousQueue = new SynchronousQueue(true);
      this.waitingThreadCount = new AtomicInteger();
      this.count = new CopyOnWriteArrayList();
      if (this.flag2) {
         this.threadLocal = ThreadLocal.withInitial(() -> new ArrayList(16));
      } else {
         this.threadLocal = ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
      }
   }

   public IConcurrentBagEntry borrow(long value, TimeUnit timeUnit) throws InterruptedException {
      List list = (List)this.threadLocal.get();

      for (int i = list.size() - 1; i >= 0; i--) {
         Object obj = list.remove(i);
         IConcurrentBagEntry iConcurrentBagEntry = this.flag2 ? (IConcurrentBagEntry)((WeakReference)obj).get() : (IConcurrentBagEntry)obj;
         if (iConcurrentBagEntry != null && iConcurrentBagEntry.compareAndSet(0, 1)) {
            return iConcurrentBagEntry;
         }
      }

      int incrementAndGet2 = this.waitingThreadCount.incrementAndGet();

      try {
         for (IConcurrentBagEntry iConcurrentBagEntry2 : (Iterable<IConcurrentBagEntry>) this.count) {
            if (iConcurrentBagEntry2.compareAndSet(0, 1)) {
               if (incrementAndGet2 > 1) {
                  this.poolAInterface.addBagItem(incrementAndGet2 - 1);
               }

               return iConcurrentBagEntry2;
            }
         }

         this.poolAInterface.addBagItem(incrementAndGet2);
         long nanos = timeUnit.toNanos(value);

         do {
            long currentTime2 = ClockSource.currentTime();
            IConcurrentBagEntry poll2 = (IConcurrentBagEntry)this.synchronousQueue.poll(nanos, TimeUnit.NANOSECONDS);
            if (poll2 == null || poll2.compareAndSet(0, 1)) {
               return poll2;
            }

            nanos -= ClockSource.elapsedNanos(currentTime2);
         } while (nanos > 10000L);

         return null;
      } finally {
         this.waitingThreadCount.decrementAndGet();
      }
   }

   public void requite(IConcurrentBagEntry iConcurrentBagEntry) {
      iConcurrentBagEntry.setState(0);

      for (int i = 0; this.waitingThreadCount.get() > 0; i++) {
         if (iConcurrentBagEntry.getState() != 0 || this.synchronousQueue.offer(iConcurrentBagEntry)) {
            return;
         }

         if ((i & 0xFF) == 255) {
            LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
         } else {
            Thread.yield();
         }
      }

      List list = (List)this.threadLocal.get();
      if (list.size() < 50) {
         list.add(this.flag2 ? new WeakReference<>(iConcurrentBagEntry) : iConcurrentBagEntry);
      }
   }

   public void add(IConcurrentBagEntry iConcurrentBagEntry) {
      if (this.flag3) {
         POOL_LOGGER.info("ConcurrentBag has been closed, ignoring add()");
         throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
      }

      this.count.add(iConcurrentBagEntry);

      while (this.waitingThreadCount.get() > 0 && iConcurrentBagEntry.getState() == 0 && !this.synchronousQueue.offer(iConcurrentBagEntry)) {
         Thread.yield();
      }
   }

   public boolean remove(IConcurrentBagEntry iConcurrentBagEntry) {
      if (!iConcurrentBagEntry.compareAndSet(1, -1) && !iConcurrentBagEntry.compareAndSet(-2, -1) && !this.flag3) {
         POOL_LOGGER.handleText4("Attempt to remove an object from the bag that was not borrowed or reserved: %s", new Object[]{iConcurrentBagEntry.toString()});
         return false;
      }

      boolean flag = this.count.remove(iConcurrentBagEntry);
      if (!flag && !this.flag3) {
         POOL_LOGGER.handleText4("Attempt to remove an object from the bag that does not exist: %s", new Object[]{iConcurrentBagEntry.toString()});
      }

      ((List)this.threadLocal.get()).remove(iConcurrentBagEntry);
      return flag;
   }

   @Override
   public void close() {
      this.flag3 = true;
   }

   public List values(int value) {
      List list = (java.util.List) this.count.stream().filter(obj -> ((IConcurrentBagEntry) obj).getState() == value).collect(Collectors.toList());
      Collections.reverse(list);
      return list;
   }

   public List values() {
      return (List)this.count.clone();
   }

   public boolean reserve(IConcurrentBagEntry iConcurrentBagEntry) {
      return iConcurrentBagEntry.compareAndSet(0, -2);
   }

   public void unreserve(IConcurrentBagEntry iConcurrentBagEntry) {
      if (iConcurrentBagEntry.compareAndSet(-2, 0)) {
         while (this.waitingThreadCount.get() > 0 && !this.synchronousQueue.offer(iConcurrentBagEntry)) {
            Thread.yield();
         }
      } else {
         POOL_LOGGER.handleText4("Attempt to relinquish an object to the bag that was not reserved: {}", new Object[]{iConcurrentBagEntry});
      }
   }

   public int getWaitingThreadCount() {
      return this.waitingThreadCount.get();
   }

   public int getCount(int value) {
      int value2 = 0;

      for (IConcurrentBagEntry iConcurrentBagEntry : (Iterable<IConcurrentBagEntry>) this.count) {
         if (iConcurrentBagEntry.getState() == value) {
            value2++;
         }
      }

      return value2;
   }

   public int size() {
      return this.count.size();
   }

   private final boolean useWeakReferences() {
      try {
         return System.getProperty("com.zaxxer.hikari.useWeakReferences") != null
            ? Boolean.getBoolean("com.zaxxer.hikari.useWeakReferences")
            : this.getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
      } catch (SecurityException e) {
         return true;
      }
   }

   private static final void dumpState(IConcurrentBagEntry iConcurrentBagEntry) {
      POOL_LOGGER.info(iConcurrentBagEntry.toString());
   }

}
