package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyElf {
   private static final Logger LOGGER = LoggerFactory.getLogger(PropertyElf.class);
   private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");

   public static void setTargetFromProperties(Object target, Properties properties) {
      if (target != null && properties != null) {
         List<Method> methods = Arrays.asList(target.getClass().getMethods());
         Enumeration<?> propertyNames = properties.propertyNames();

         while (propertyNames.hasMoreElements()) {
            Object key = propertyNames.nextElement();
            String propName = key.toString();
            Object propValue = properties.getProperty(propName);
            if (propValue == null) {
               propValue = properties.get(key);
            }

            if (target instanceof HikariConfig && propName.startsWith("dataSource.")) {
               ((HikariConfig)target).addDataSourceProperty(propName.substring("dataSource.".length()), propValue);
            } else {
               setProperty(target, propName, propValue, methods);
            }
         }
      }
   }

   public static Set<String> getPropertyNames(Class<?> targetClass) {
      HashSet<String> set = new HashSet<>();
      Matcher matcher = GETTER_PATTERN.matcher("");

      for (Method method : targetClass.getMethods()) {
         String name = method.getName();
         if (method.getParameterTypes().length == 0 && matcher.reset(name).matches()) {
            name = name.replaceFirst("(get|is)", "");

            try {
               if (targetClass.getMethod("set" + name, method.getReturnType()) != null) {
                  name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                  set.add(name);
               }
            } catch (Exception e) {
            }
         }
      }

      return set;
   }

   public static Object getProperty(String propName, Object target) {
      try {
         String capitalized = "get" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
         Method method = target.getClass().getMethod(capitalized);
         return method.invoke(target);
      } catch (Exception e) {
         try {
            String capitalizedx = "is" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
            Method methodx = target.getClass().getMethod(capitalizedx);
            return methodx.invoke(target);
         } catch (Exception ex) {
            return null;
         }
      }
   }

   public static Properties copyProperties(Properties props) {
      Properties copy = new Properties();

      for (Entry<Object, Object> entry : props.entrySet()) {
         copy.setProperty(entry.getKey().toString(), entry.getValue().toString());
      }

      return copy;
   }

   private static void setProperty(Object target, String propName, Object propValue, List<Method> methods) {
      Method writeMethod = null;
      String methodName = "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1);

      for (Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
            writeMethod = method;
            break;
         }
      }

      if (writeMethod == null) {
         methodName = "set" + propName.toUpperCase();

         for (Method methodx : methods) {
            if (methodx.getName().equals(methodName) && methodx.getParameterTypes().length == 1) {
               writeMethod = methodx;
               break;
            }
         }
      }

      if (writeMethod == null) {
         LOGGER.error("Property {} does not exist on target {}", propName, target.getClass());
         throw new RuntimeException(String.format("Property %s does not exist on target %s", propName, target.getClass()));
      } else {
         try {
            Class<?> paramClass = writeMethod.getParameterTypes()[0];
            if (paramClass == int.class) {
               writeMethod.invoke(target, Integer.parseInt(propValue.toString()));
            } else if (paramClass == long.class) {
               writeMethod.invoke(target, Long.parseLong(propValue.toString()));
            } else if (paramClass == boolean.class || paramClass == Boolean.class) {
               writeMethod.invoke(target, Boolean.parseBoolean(propValue.toString()));
            } else if (paramClass == String.class) {
               writeMethod.invoke(target, propValue.toString());
            } else {
               writeMethod.invoke(target, propValue);
            }
         } catch (Exception e) {
            LOGGER.error("Failed to set property {} on target {}", propName, target.getClass(), e);
            throw new RuntimeException(e);
         }
      }
   }
}
