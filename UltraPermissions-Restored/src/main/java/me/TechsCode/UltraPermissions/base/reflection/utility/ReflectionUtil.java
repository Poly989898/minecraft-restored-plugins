package me.TechsCode.UltraPermissions.base.reflection.utility;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ReflectionUtil {
   public static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
   public static final String SERVER_PACKAGE = CRAFTBUKKIT_PACKAGE.split("\\.").length > 3 ? CRAFTBUKKIT_PACKAGE.split("\\.")[3] : "";
   public static final int MINOR_VERSION = getMinorVersion();
   public static final int PATCH_VERSION = getPatchVersion();

   public ReflectionUtil() {
   }

   private static int getPatchVersion() {
      return Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[2]);
   }

   private static int getMinorVersion() {
      String[] texts = CRAFTBUKKIT_PACKAGE.split("\\.");
      return texts.length < 3 ? Integer.parseInt(texts[1]) : Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
   }

   public static <T extends AccessibleObject> T setAccessible(@NotNull T object) {
      object.setAccessible(true);
      return (T)object;
   }

   public static Class<?> getClass(boolean lenient, String... classNames) throws ClassNotFoundException {
      for (String className : classNames) {
         if (lenient) {
            try {
               return Class.forName(CRAFTBUKKIT_PACKAGE + "." + className);
            } catch (ClassNotFoundException exception) {
            }
         } else {
            String[] names;
            if (MINOR_VERSION >= 17) {
               names = new String[]{"net.minecraft." + className, "net.minecraft.server." + SERVER_PACKAGE + "." + className};
            } else {
               names = new String[]{"net.minecraft.server." + SERVER_PACKAGE + "." + className, "net.minecraft." + className};
            }

            for (String candidate : names) {
               try {
                  return Class.forName(candidate);
               } catch (ClassNotFoundException exception) {
               }
            }
         }
      }

      throw new ClassNotFoundException("No class found with possible names " + Arrays.toString(classNames));
   }

   public static List<Field> getFields(Class<?> type, Class<?> fieldType) {
      return Arrays.stream(type.getDeclaredFields())
         .filter(field -> field.getType().equals(fieldType))
         .map(ReflectionUtil::setAccessible)
         .collect(Collectors.toList());
   }

   public static Field getField(@NotNull Class<?> type, @NotNull String... texts) {
      for (String text : texts) {
         try {
            return setAccessible(type.getDeclaredField(text));
         } catch (NoSuchFieldException exception) {
         }
      }

      throw new IllegalArgumentException("Class " + type.getName() + " does not contain a field with potential names " + Arrays.toString(texts));
   }

   public static List<Method> getMethods(@NotNull Class<?> type, @NotNull Class<?> returnType, @NotNull Class<?>... types) {
      ArrayList list = new ArrayList();
      Arrays.stream(type.getMethods())
         .filter(method -> returnType.isAssignableFrom(method.getReturnType()) && method.getParameterCount() == types.length)
         .forEachOrdered(method -> {
            Class[] parameterTypes = method.getParameterTypes();
            boolean flag = IntStream.range(0, parameterTypes.length).noneMatch(index -> parameterTypes[index] != types[index]);
            if (flag) {
               list.add(method);
            }
         });
      return list;
   }

   public static Method getMethod(@NotNull Class<?> type, @NotNull String[] texts, @NotNull Class<?>... types) throws NoSuchMethodException {
      ArrayList list = new ArrayList();

      for (Method method : type.getMethods()) {
         if (method.getParameterCount() == types.length) {
            Class[] parameterTypes = method.getParameterTypes();
            boolean flag = IntStream.range(0, parameterTypes.length).noneMatch(index -> parameterTypes[index] != types[index]);
            if (flag) {
               for (String text : texts) {
                  if (method.getName().equals(text)) {
                     return method;
                  }

                  String[] parts = method.getName().split("_");
                  if (parts.length > 2 && parts[2].equals(text)) {
                     return method;
                  }
               }

               list.add(method.getName());
            }
         }
      }

      throw new NoSuchMethodException(
         "No method found with possible names "
            + Arrays.toString(texts)
            + " with parameters "
            + Arrays.toString(types)
            + " in class "
            + type.getName()
            + ". Methods with matching parameters: "
            + list
      );
   }

   public static Constructor<?> getConstructor(@NotNull Class<?> type, @NotNull Class<?>... types) {
      for (Constructor declaredConstructor : type.getDeclaredConstructors()) {
         if (declaredConstructor.getParameterCount() == types.length) {
            Class[] parameterTypes = declaredConstructor.getParameterTypes();
            boolean flag = IntStream.range(0, parameterTypes.length).noneMatch(index -> parameterTypes[index] != types[index]);
            if (flag) {
               return setAccessible(declaredConstructor);
            }
         }
      }

      throw new IllegalArgumentException("Class " + type.getName() + " does not contain a constructor with parameters " + Arrays.toString(types));
   }

   public static List<Field> getInstanceFields(@NotNull Class<?> type, @NotNull Class<?> fieldType) {
      return Arrays.stream(type.getDeclaredFields())
         .filter(field -> field.getType() == fieldType && !Modifier.isStatic(field.getModifiers()))
         .map(ReflectionUtil::setAccessible)
         .collect(Collectors.toList());
   }

   public static Constructor<?> getOnlyConstructor(@NotNull Class<?> type) {
      Constructor[] constructors = type.getConstructors();
      assert constructors.length == 1 : "Class "
         + type.getName()
         + " is expected to have 1 constructor, but has "
         + constructors.length
         + ": \n"
         + (String)Arrays.stream(constructors).map(Constructor::toString).collect(Collectors.joining("\n"));
      return constructors[0];
   }

   public static Field getOnlyField(@NotNull Class<?> type, @NotNull Class<?> fieldType) {
      List<Field> fields = getFields(type, fieldType);
      assert fields.size() == 1 : "Class "
         + type.getName()
         + " is expected to have 1 field of type "
         + fieldType.getName()
         + ", but has "
         + fields.size()
         + ": "
         + fields.stream().map(Field::getName).toList();
      return (Field)fields.get(0);
   }

   public static Method getOnlyMethod(@NotNull Class<?> type, @NotNull Class<?> returnType, @NotNull Class<?>... types) {
      List<Method> methods = getMethods(type, returnType, types);
      assert methods.size() == 1 : "Class "
         + type.getName()
         + " is expected to have 1 method with return type "
         + returnType.getName()
         + " and parameters "
         + Arrays.toString(types)
         + ", but has "
         + methods.size()
         + ": \n"
         + (String)methods.stream().map(Method::toString).collect(Collectors.joining("\n"));
      return (Method)methods.get(0);
   }
}
