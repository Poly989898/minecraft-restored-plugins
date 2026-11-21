package io.lumine.mythic.bukkit.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public class Reflector<E> {
   Map<String, Field> fields = new HashMap<>();

   public Reflector(Class<? extends E> type, String... fields) {
      for (String fieldName : fields) {
         try {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            this.fields.put(fieldName, field);
         } catch (NoSuchFieldException e) {
            e.printStackTrace();
         }
      }
   }

   public int getInt(E object, String field) {
      try {
         return this.fields.get(field).getInt(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return 0;
      }
   }

   public void setInt(E object, String field, int val) {
      try {
         this.fields.get(field).setInt(object, val);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public byte getByte(E object, String field) {
      try {
         return this.fields.get(field).getByte(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return 0;
      }
   }

   public void setByte(E object, String field, byte val) {
      try {
         this.fields.get(field).setByte(object, val);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public String getString(E object, String field) {
      try {
         return (String)this.fields.get(field).get(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return null;
      }
   }

   public void setString(E object, String field, String val) {
      try {
         this.fields.get(field).set(object, val);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public Object get(E object, String field) {
      try {
         return this.fields.get(field).get(object);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return null;
      }
   }

   public void set(E object, String field, Object val) {
      try {
         this.fields.get(field).set(object, val);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
      try {
         Class<?>[] primitiveTypes = Reflector.DataType.getPrimitive(parameterTypes);

         for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Reflector.DataType.compare(Reflector.DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
               constructor.setAccessible(true);
               return constructor;
            }
         }

         throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Constructor<?> getConstructor(String className, Reflector.PackageType packageType, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
      return getConstructor(packageType.getClass(className), parameterTypes);
   }

   public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      return getConstructor(clazz, Reflector.DataType.getPrimitive(arguments)).newInstance(arguments);
   }

   public static Object instantiateObject(String className, Reflector.PackageType packageType, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
      return instantiateObject(packageType.getClass(className), arguments);
   }

   public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
      try {
         Class<?>[] primitiveTypes = Reflector.DataType.getPrimitive(parameterTypes);

         for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && Reflector.DataType.compare(Reflector.DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
               method.setAccessible(true);
               return method;
            }
         }

         throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Method getMethod(String className, Reflector.PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
      return getMethod(packageType.getClass(className), methodName, parameterTypes);
   }

   public static Object invokeMethod(Object instance, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      return getMethod(instance.getClass(), methodName, Reflector.DataType.getPrimitive(arguments)).invoke(instance, arguments);
   }

   public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      return getMethod(clazz, methodName, Reflector.DataType.getPrimitive(arguments)).invoke(instance, arguments);
   }

   public static Object invokeMethod(Object instance, String className, Reflector.PackageType packageType, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
      return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
   }

   public static Field getField(Class<?> clazz, String fieldName) {
      try {
         Field field = clazz.getDeclaredField(fieldName);
         field.setAccessible(true);
         return field;
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Field getField(Class<?> clazz, boolean declared, String fieldName) {
      try {
         Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
         field.setAccessible(true);
         return field;
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Field getField(String className, Reflector.PackageType packageType, boolean declared, String fieldName) {
      try {
         return getField(packageType.getClass(className), declared, fieldName);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Object getValue(Class clazz, String fieldName) {
      try {
         return getField(clazz, fieldName).get(null);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static <T> T getValue(Object instance, Class<T> clazz, String fieldName) {
      try {
         return (T)getField(clazz, fieldName).get(instance);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static <T> T getValue(Object instance, Class<T> clazz, boolean declared, String fieldName) {
      try {
         return (T)getField(clazz, declared, fieldName).get(instance);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Object getValue(Object instance, String className, Reflector.PackageType packageType, boolean declared, String fieldName) {
      try {
         return getValue(instance, packageType.getClass(className), declared, fieldName);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static Object getValue(Object instance, boolean declared, String fieldName) {
      try {
         return getValue(instance, instance.getClass(), declared, fieldName);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      getField(clazz, declared, fieldName).set(instance, value);
   }

   public static void setValue(Object instance, String className, Reflector.PackageType packageType, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
      setValue(instance, packageType.getClass(className), declared, fieldName, value);
   }

   public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      setValue(instance, instance.getClass(), declared, fieldName, value);
   }

   public static enum DataType {
      BYTE(byte.class, Byte.class),
      SHORT(short.class, Short.class),
      INTEGER(int.class, Integer.class),
      LONG(long.class, Long.class),
      CHARACTER(char.class, Character.class),
      FLOAT(float.class, Float.class),
      DOUBLE(double.class, Double.class),
      BOOLEAN(boolean.class, Boolean.class);

      private static final Map<Class<?>, Reflector.DataType> CLASS_MAP = new HashMap<>();
      private final Class<?> primitive;
      private final Class<?> reference;

      private DataType(Class<?> primitive, Class<?> reference) {
         this.primitive = primitive;
         this.reference = reference;
      }

      public Class<?> getPrimitive() {
         return this.primitive;
      }

      public Class<?> getReference() {
         return this.reference;
      }

      public static Reflector.DataType fromClass(Class<?> clazz) {
         return CLASS_MAP.get(clazz);
      }

      public static Class<?> getPrimitive(Class<?> clazz) {
         Reflector.DataType type = fromClass(clazz);
         return type == null ? clazz : type.getPrimitive();
      }

      public static Class<?> getReference(Class<?> clazz) {
         Reflector.DataType type = fromClass(clazz);
         return type == null ? clazz : type.getReference();
      }

      public static Class<?>[] getPrimitive(Class<?>[] classes) {
         int length = classes == null ? 0 : classes.length;
         Class<?>[] types = new Class[length];

         for (int index = 0; index < length; index++) {
            types[index] = getPrimitive(classes[index]);
         }

         return types;
      }

      public static Class<?>[] getReference(Class<?>[] classes) {
         int length = classes == null ? 0 : classes.length;
         Class<?>[] types = new Class[length];

         for (int index = 0; index < length; index++) {
            types[index] = getReference(classes[index]);
         }

         return types;
      }

      public static Class<?>[] getPrimitive(Object[] objects) {
         int length = objects == null ? 0 : objects.length;
         Class<?>[] types = new Class[length];

         for (int index = 0; index < length; index++) {
            types[index] = getPrimitive(objects[index].getClass());
         }

         return types;
      }

      public static Class<?>[] getReference(Object[] objects) {
         int length = objects == null ? 0 : objects.length;
         Class<?>[] types = new Class[length];

         for (int index = 0; index < length; index++) {
            types[index] = getReference(objects[index].getClass());
         }

         return types;
      }

      public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
         if (primary != null && secondary != null && primary.length == secondary.length) {
            for (int index = 0; index < primary.length; index++) {
               Class<?> primaryClass = primary[index];
               Class<?> secondaryClass = secondary[index];
               if (!primaryClass.equals(secondaryClass) && !primaryClass.isAssignableFrom(secondaryClass)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      static {
         for (Reflector.DataType type : values()) {
            CLASS_MAP.put(type.primitive, type);
            CLASS_MAP.put(type.reference, type);
         }
      }
   }

   public static enum PackageType {
      MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
      CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
      CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"),
      CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"),
      CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"),
      CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
      CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"),
      CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"),
      CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"),
      CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"),
      CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"),
      CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
      CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"),
      CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"),
      CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
      CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"),
      CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"),
      CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
      CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"),
      CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

      private final String path;

      private PackageType(String path) {
         this.path = path;
      }

      private PackageType(Reflector.PackageType parent, String path) {
         this(parent + "." + path);
      }

      public String getPath() {
         return this.path;
      }

      public Class<?> getClass(String className) throws ClassNotFoundException {
         return Class.forName(this + "." + className);
      }

      @Override
      public String toString() {
         return this.path;
      }

      public static String getServerVersion() {
         return Bukkit.getServer().getClass().getPackage().getName().substring(23);
      }
   }
}
