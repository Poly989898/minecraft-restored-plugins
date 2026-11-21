package io.lumine.mythic.bukkit.utils.particles.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReflectionAccessor {
   @NotNull
   ReflectionAccessor.ClassAccessor getClass(@NotNull String str) throws ClassNotFoundException;

   default Class<?> getClassInstance(@NotNull String name) throws ClassNotFoundException {
      return this.getClass(name).getClassInstance();
   }

   static boolean areSameParameters(@NotNull Type[] types1, @NotNull Type[] types2) {
      if (types1.length != types2.length) {
         return false;
      } else {
         for (int i = 0; i < types1.length; i++) {
            if (!types1[i].getTypeName().equals(types2[i].getTypeName())) {
               return false;
            }
         }

         return true;
      }
   }

   public interface ClassAccessor extends Type {
      @NotNull
      @Override
      String getTypeName();

      @NotNull
      Type getArrayType();

      @NotNull
      Class<?> getClassInstance() throws ClassNotFoundException;

      @NotNull
      ReflectionAccessor.ClassAccessor.FieldAccessor getField(@NotNull String str) throws NoSuchFieldException;

      @NotNull
      default Field getFieldInstance(@NotNull String original) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
         return this.getField(original).getFieldInstance();
      }

      @NotNull
      ReflectionAccessor.ClassAccessor.MethodAccessor getMethod(@NotNull String str, @NotNull Type... types) throws NoSuchMethodException, ClassNotFoundException;

      @NotNull
      default Method getMethodInstance(@NotNull String original, @NotNull Type... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
         return this.getMethod(original, parameterTypes).getMethodInstance();
      }

      @NotNull
      ReflectionAccessor.ClassAccessor.ConstructorAccessor getConstructor(@NotNull Type... types) throws NoSuchMethodException, SecurityException, ClassNotFoundException;

      @NotNull
      default Constructor<?> getConstructorInstance(@NotNull Type... parameterTypes) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
         return this.getConstructor(parameterTypes).getConstructorInstance();
      }

      public interface ConstructorAccessor {
         Constructor<?> getConstructorInstance();

         Object newInstance(@Nullable Object... objs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
      }

      public interface FieldAccessor {
         Field getFieldInstance() throws NoSuchFieldException, SecurityException, ClassNotFoundException;

         Object get(@Nullable Object obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException;

         void set(@Nullable Object obj, Object obj2) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException;
      }

      public interface MethodAccessor {
         Method getMethodInstance() throws NoSuchMethodException, SecurityException, ClassNotFoundException;

         Object invoke(@Nullable Object obj, @Nullable Object... objs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;
      }
   }
}
