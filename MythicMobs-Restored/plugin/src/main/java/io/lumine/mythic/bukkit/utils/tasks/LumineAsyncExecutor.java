package io.lumine.mythic.bukkit.utils.tasks;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public final class LumineAsyncExecutor extends AbstractExecutorService implements ScheduledExecutorService {
   private ExecutorService taskService;
   private final ScheduledExecutorService timerExecutionService;
   private final Set<ScheduledFuture<?>> tasks = Collections.newSetFromMap(new WeakHashMap<>());

   LumineAsyncExecutor() {
      this.taskService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("lumine-scheduler-%d").build());
      this.timerExecutionService = Executors.newSingleThreadScheduledExecutor(
         new ThreadFactoryBuilder().setDaemon(true).setNameFormat("lumine-scheduler-timer").build()
      );
   }

   public synchronized void setThreadLimit(int limit) {
      this.taskService.shutdown();

      try {
         if (!this.taskService.awaitTermination(5L, TimeUnit.SECONDS)) {
            this.taskService.shutdownNow();
         }
      } catch (InterruptedException e) {
         this.taskService.shutdownNow();
         Thread.currentThread().interrupt();
      }

      this.taskService = Executors.newFixedThreadPool(limit, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("lumine-fscheduler-%d").build());
   }

   public synchronized void useVirtualThreads() {
      Method[] threadMethods = Thread.class.getMethods();
      Optional<Method> ofVirtual = Arrays.stream(threadMethods)
         .filter(method -> method.getName().equals("ofVirtual") && method.getParameterCount() == 0)
         .findAny();
      Method[] executorsMethods = Executors.class.getMethods();
      Optional<Method> newThreadPerTaskExecutor = Arrays.stream(executorsMethods)
         .filter(method -> method.getName().equals("newThreadPerTaskExecutor") && method.getParameterCount() == 1)
         .findAny();
      if (!ofVirtual.isEmpty() && !newThreadPerTaskExecutor.isEmpty()) {
         Method ofVirtualMethod = ofVirtual.get();

         ExecutorService threadPerTaskExecutor;
         try {
            Object ofVirtualObject = ofVirtualMethod.invoke(null);
            Class<?> ofVirtualClass = ofVirtualMethod.getReturnType();
            Object ofVirtualMethod2 = ofVirtualClass.getMethod("name", String.class, long.class).invoke(ofVirtualObject, "lumine-vscheduler-", 1L);
            ThreadFactory virtualThreadFactory = (ThreadFactory)ofVirtualClass.getMethod("factory").invoke(ofVirtualMethod2);
            threadPerTaskExecutor = (ExecutorService)newThreadPerTaskExecutor.get().invoke(null, virtualThreadFactory);
         } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new UnsupportedOperationException("Virtual threads are not supported on this JDK");
         }

         this.taskService.shutdown();

         try {
            if (!this.taskService.awaitTermination(5L, TimeUnit.SECONDS)) {
               this.taskService.shutdownNow();
            }
         } catch (InterruptedException ex) {
            this.taskService.shutdownNow();
            Thread.currentThread().interrupt();
         }

         this.taskService = threadPerTaskExecutor;
      } else {
         throw new UnsupportedOperationException("Virtual threads are not supported on this JDK");
      }
   }

   private ScheduledFuture<?> consumeTask(ScheduledFuture<?> future) {
      synchronized (this.tasks) {
         this.tasks.add(future);
         return future;
      }
   }

   public void cancelRepeatingTasks() {
      synchronized (this.tasks) {
         for (ScheduledFuture<?> task : this.tasks) {
            task.cancel(false);
         }
      }
   }

   @Override
   public void execute(Runnable runnable) {
      this.taskService.execute(LumineExecutors.wrapRunnable(runnable));
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      Runnable delegate = LumineExecutors.wrapRunnable(command);
      return this.consumeTask(this.timerExecutionService.schedule(() -> this.taskService.execute(delegate), delay, unit));
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      if (period < 1L) {
         period = 1L;
      }

      return this.consumeTask(
         this.timerExecutionService
            .scheduleAtFixedRate(new LumineAsyncExecutor.FixedRateWorker(LumineExecutors.wrapRunnable(command)), initialDelay, period, unit)
      );
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.scheduleAtFixedRate(command, initialDelay, delay, unit);
   }

   @Override
   public void shutdown() {
   }

   @Override
   public List<Runnable> shutdownNow() {
      return Collections.emptyList();
   }

   @Override
   public boolean isShutdown() {
      return false;
   }

   @Override
   public boolean isTerminated() {
      return false;
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) {
      throw new IllegalStateException("Not shutdown");
   }

   private final class FixedRateWorker implements Runnable {
      private final Runnable delegate;
      private final ReentrantLock lock = new ReentrantLock();
      private final AtomicInteger running = new AtomicInteger(0);

      private FixedRateWorker(Runnable delegate) {
         this.delegate = delegate;
      }

      @Override
      public void run() {
         if (this.running.incrementAndGet() > 2) {
            this.running.decrementAndGet();
         } else {
            LumineAsyncExecutor.this.taskService.execute(() -> {
               this.lock.lock();

               try {
                  this.delegate.run();
               } finally {
                  this.lock.unlock();
                  this.running.decrementAndGet();
               }
            });
         }
      }
   }
}
