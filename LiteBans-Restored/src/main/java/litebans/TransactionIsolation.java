package litebans;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TransactionIsolation {
   private TransactionIsolation() {
   }

   public static String computeText(String text) {
      return text == null ? null : (text.trim().isEmpty() ? null : text.trim());
   }

   public static void handleLongValue(long value) {
      try {
         Thread.sleep(value);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   public static Object computeObj(String text, Class class2, Object[] objs) {
      if (text == null) {
         return null;
      }

      try {
         Class class3 = TransactionIsolation.class.getClassLoader().loadClass(text);
         if (objs.length == 0) {
            return class2.cast(class3.getDeclaredConstructor().newInstance());
         }

         Class[] classArray = new Class[objs.length];

         for (int i = 0; i < objs.length; i++) {
            classArray[i] = objs[i].getClass();
         }

         Constructor constructor = class3.getConstructor(classArray);
         return class2.cast(constructor.newInstance(objs));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static ThreadPoolExecutor computeThreadPoolExecutor(int value, String text, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
      if (threadFactory == null) {
         threadFactory = new PoolThreadFactory(text, true);
      }

      LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(value);
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, linkedBlockingQueue, threadFactory, rejectedExecutionHandler);
      threadPoolExecutor.allowCoreThreadTimeOut(true);
      return threadPoolExecutor;
   }

   public static ThreadPoolExecutor computeThreadPoolExecutor(BlockingQueue blockingQueue, String text, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
      if (threadFactory == null) {
         threadFactory = new PoolThreadFactory(text, true);
      }

      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, blockingQueue, threadFactory, rejectedExecutionHandler);
      threadPoolExecutor.allowCoreThreadTimeOut(true);
      return threadPoolExecutor;
   }

   public static int computeIntValue(String text) {
      if (text != null) {
         try {
            String upperCase = text.toUpperCase(Locale.ENGLISH);
            return IsolationLevel.computeIsolationLevel(upperCase).getIntValue();
         } catch (IllegalArgumentException e) {
            try {
               int int2 = Integer.parseInt(text);

               for (IsolationLevel isolationLevel : IsolationLevel.values()) {
                  if (isolationLevel.getIntValue() == int2) {
                     return isolationLevel.getIntValue();
                  }
               }

               throw new IllegalArgumentException("Invalid transaction isolation value: " + text);
            } catch (NumberFormatException e2) {
               throw new IllegalArgumentException("Invalid transaction isolation value: " + text, e2);
            }
         }
      } else {
         return -1;
      }
   }
}
