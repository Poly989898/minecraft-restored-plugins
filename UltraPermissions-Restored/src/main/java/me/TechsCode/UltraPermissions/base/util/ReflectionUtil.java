package me.TechsCode.UltraPermissions.base.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
   public ReflectionUtil() {
   }

   public static boolean isClassAvailable(String text) {
      try {
         Class.forName(text);
         return true;
      } catch (ClassNotFoundException exception) {
         return false;
      }
   }

   public static <T> T instantiate(String text) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
      Class type = Class.forName(text);
      Constructor declaredConstructor = type.getDeclaredConstructor();
      declaredConstructor.setAccessible(true);
      return (T)declaredConstructor.newInstance();
   }

   public static <T> T instantiate(Constructor<?> constructor, Object... objs) {
      constructor.setAccessible(true);

      try {
         return (T)constructor.newInstance(objs);
      } catch (InstantiationException exception) {
         throw new RuntimeException(exception);
      } catch (IllegalAccessException exception) {
         throw new RuntimeException(exception);
      } catch (InvocationTargetException exception) {
         throw new RuntimeException(exception);
      }
   }

   public static void invoke(String text, Object obj, Object... objs) {
      Class clazz = obj.getClass();
      Method method = null;

      try {
         method = clazz.getDeclaredMethod(text, getParameterTypes(objs));
      } catch (NoSuchMethodException exception) {
         throw new RuntimeException(exception);
      }

      method.setAccessible(true);

      try {
         method.invoke(obj, objs);
      } catch (IllegalAccessException | InvocationTargetException exception) {
         throw new RuntimeException(exception);
      }
   }

   public static <T> T invoke(Method method, Object obj, Object... objs) {
      method.setAccessible(true);

      try {
         return (T)method.invoke(obj, objs);
      } catch (IllegalAccessException | InvocationTargetException exception) {
         throw new RuntimeException(exception);
      }
   }

   public static Constructor<?> getConstructor(String text, Class<?>... types) {
      Class type = null;

      try {
         type = Class.forName(text);
      } catch (ClassNotFoundException exception) {
         throw new RuntimeException(exception);
      }

      try {
         return type.getDeclaredConstructor(types);
      } catch (NoSuchMethodException exception) {
         throw new RuntimeException(exception);
      }
   }

   private static Class<?>[] getParameterTypes(Object... objs) {
      Class[] types = new Class[objs.length];

      for (int i = 0; i < objs.length; i++) {
         types[i] = objs[i].getClass();
      }

      return types;
   }
}
