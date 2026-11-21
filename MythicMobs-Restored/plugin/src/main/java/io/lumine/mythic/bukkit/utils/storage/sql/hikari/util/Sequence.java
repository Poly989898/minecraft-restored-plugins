package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public interface Sequence {
   void increment();

   long get();

   public static final class DropwizardSequence implements Sequence {
      private final Object longAdder;
      private final Method increment;
      private final Method sum;

      public DropwizardSequence(Class<?> longAdderClass) throws Exception {
         Constructor<?> constructor = longAdderClass.getDeclaredConstructors()[0];
         constructor.setAccessible(true);
         this.increment = longAdderClass.getMethod("increment");
         this.increment.setAccessible(true);
         this.sum = longAdderClass.getMethod("sum");
         this.sum.setAccessible(true);
         this.longAdder = constructor.newInstance();
      }

      @Override
      public void increment() {
         try {
            this.increment.invoke(this.longAdder);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public long get() {
         try {
            return (Long)this.sum.invoke(this.longAdder);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static final class Factory {
      public static Sequence create() {
         try {
            if (Sequence.class.getClassLoader().loadClass("java.util.concurrent.atomic.LongAdder") != null
               && !Boolean.getBoolean("io.lumine.mythic.bukkit.utils.storage.sql.hikari.useAtomicLongSequence")) {
               return new Sequence.Java8Sequence();
            }
         } catch (ClassNotFoundException e) {
            try {
               Class<?> longAdderClass = Sequence.class.getClassLoader().loadClass("com.codahale.metrics.LongAdder");
               return new Sequence.DropwizardSequence(longAdderClass);
            } catch (Exception ex) {
            }
         }

         return new Sequence.Java7Sequence();
      }
   }

   public static final class Java7Sequence extends AtomicLong implements Sequence {
      @Override
      public void increment() {
         this.incrementAndGet();
      }
   }

   public static final class Java8Sequence extends LongAdder implements Sequence {
      @Override
      public long get() {
         return this.sum();
      }
   }
}
