package com.ticxo.modelengine.api.utils;

import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class ReflectionUtils {
   private static final Map<Class<?>, Class<?>> PRIMITIVE_EQUIV = new HashMap<Class<?>, Class<?>>() {
      {
         this.put(byte.class, Byte.class);
         this.put(short.class, Short.class);
         this.put(int.class, Integer.class);
         this.put(long.class, Long.class);
         this.put(float.class, Float.class);
         this.put(double.class, Double.class);
         this.put(boolean.class, Boolean.class);
         this.put(char.class, Character.class);
      }
   };
   private static final Map<Class<?>, ConcurrentHashMap<String, ReflectionUtils.ReflectionEnum>> DYNAMIC_FIELDS = Maps.newConcurrentMap();
   private static final Map<Class<?>, ConcurrentHashMap<ReflectionUtils.ReflectionEnum, Field>> FIELD_MAP = Maps.newConcurrentMap();
   private static final Map<Class<?>, ConcurrentHashMap<ReflectionUtils.MethodEnum, Method>> METHOD_MAP = Maps.newConcurrentMap();
   private static final boolean IS_MAPPED = Boolean.getBoolean("modelengine.mapped");

   public static Field unlockField(ReflectionUtils.ReflectionEnum field) {
      try {
         Field f = field.getTarget().getDeclaredField(field.get(IS_MAPPED));
         f.setAccessible(true);
         return f;
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
         try {
            Field f = field.getTarget().getDeclaredField(field.get(!IS_MAPPED));
            f.setAccessible(true);
            return f;
         } catch (NoSuchFieldException | SecurityException | IllegalArgumentException e2) {
            throw new RuntimeException("An error occurred while unlocking field: " + field.getMapped(), e);
         }
      }
   }

   public static Method unlockMethod(ReflectionUtils.MethodEnum method) {
      try {
         Method m = method.getTarget().getDeclaredMethod(method.get(IS_MAPPED), method.getParameterClasses());
         m.setAccessible(true);
         return m;
      } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
         try {
            Method m = method.getTarget().getDeclaredMethod(method.get(!IS_MAPPED), method.getParameterClasses());
            m.setAccessible(true);
            return m;
         } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e2) {
            throw new RuntimeException("An error occurred while unlocking method: " + method.getMapped(), e);
         }
      }
   }

   public static Field getField(Class<?> clazz, String field) {
      return getField(
         DYNAMIC_FIELDS.computeIfAbsent(clazz, aClass -> new ConcurrentHashMap<>())
            .computeIfAbsent(field, s -> new ReflectionUtils.RuntimeReflection(clazz, field))
      );
   }

   public static Field getField(ReflectionUtils.ReflectionEnum field) {
      return FIELD_MAP.computeIfAbsent(field.getTarget(), aClass -> new ConcurrentHashMap<>()).computeIfAbsent(field, ReflectionUtils::unlockField);
   }

   public static Method getMethod(ReflectionUtils.MethodEnum method) {
      return METHOD_MAP.computeIfAbsent(method.getTarget(), aClass -> new ConcurrentHashMap<>()).computeIfAbsent(method, ReflectionUtils::unlockMethod);
   }

   @Nullable
   public static <T> T get(Object object, ReflectionUtils.ReflectionEnum field) {
      try {
         return (T)getField(field).get(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Nullable
   public static <T> T get(Object object, ReflectionUtils.ReflectionEnum field, T def) {
      try {
         return (T)getField(field).get(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return def;
      }
   }

   @Nullable
   public static <T> T get(ReflectionUtils.ReflectionEnum field) {
      return get(null, field);
   }

   public static boolean set(Object object, ReflectionUtils.ReflectionEnum field, Object val) {
      try {
         getField(field).set(object, val);
         return true;
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return false;
      }
   }

   public static boolean set(ReflectionUtils.ReflectionEnum field, Object val) {
      return set(null, field, val);
   }

   public static <T> T call(Object object, ReflectionUtils.MethodEnum method, Object... parameters) {
      try {
         Class<?>[] paramClasses = method.getParameterClasses();
         if (paramClasses.length > parameters.length) {
            throw new RuntimeException(String.format("Invalid method call: Missing parameters. Expected %s, got %s.", paramClasses.length, parameters.length));
         }

         for (int i = 0; i < paramClasses.length; i++) {
            Class<?> givenClass = parameters[i].getClass();
            Class<?> expectClass = paramClasses[i];
            if (!expectClass.isAssignableFrom(givenClass) && isDifferentPrimitive(givenClass, expectClass)) {
               throw new RuntimeException(
                  String.format(
                     "Invalid method call: Invalid parameter at position %s. Expected %s, got %s.", i, expectClass.getSimpleName(), givenClass.getSimpleName()
                  )
               );
            }
         }

         Method m = getMethod(method);
         return (T)m.invoke(object, parameters);
      } catch (IllegalAccessException | InvocationTargetException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static <T> T call(ReflectionUtils.MethodEnum method, Object... parameters) {
      return call(null, method, parameters);
   }

   private static boolean isDifferentPrimitive(Class<?> givenClass, Class<?> expectClass) {
      if (!expectClass.isPrimitive()) {
         return true;
      }

      Class<?> boxed = PRIMITIVE_EQUIV.get(expectClass);
      return !boxed.isAssignableFrom(givenClass);
   }

   public interface MethodEnum extends ReflectionUtils.ReflectionEnum {
      Class<?>[] getParameterClasses();
   }

   public interface ReflectionEnum {
      Class<?> getTarget();

      String getObfuscated();

      String getMapped();

      default String get(boolean mapped) {
         return mapped ? this.getMapped() : this.getObfuscated();
      }
   }

   @Getter
   static class RuntimeReflection implements ReflectionUtils.ReflectionEnum {
      private final Class<?> target;
      private final String field;

      @Override
      public String getObfuscated() {
         return this.field;
      }

      @Override
      public String getMapped() {
         return this.field;
      }

      @Generated
      public RuntimeReflection(Class<?> target, String field) {
         this.target = target;
         this.field = field;
      }
   }
}
