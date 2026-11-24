package me.TechsCode.UltraPermissions.base.reflection;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class ClassInstanceCreator {
   static Unsafe unsafe;

   public ClassInstanceCreator() {
   }

   public static <T> T create(Class<T> type) {
      try {
         return (T)unsafe.allocateInstance(type);
      } catch (InstantiationException exception) {
         return null;
      }
   }

   static {
      try {
         Field declaredField = Unsafe.class.getDeclaredField("theUnsafe");
         declaredField.setAccessible(true);
         unsafe = (Unsafe)declaredField.get(null);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
