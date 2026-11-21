package litebans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PropertyElf {
   private static final Pattern PATTERN = Pattern.compile("(get|is)[A-Z].+");

   private PropertyElf() {
   }

   public static void handleObj(Object obj, Properties properties) {
      if (obj != null && properties != null) {
         List list = Arrays.asList(obj.getClass().getMethods());
         properties.forEach((obj2, obj3) -> {
            if (obj instanceof HikariConfig && obj2.toString().startsWith("dataSource.")) {
               ((HikariConfig)obj).addDataSourceProperty(obj2.toString().substring("dataSource.".length()), obj3);
            } else {
               handleObj(obj, obj2.toString(), obj3, list);
            }
         });
      }
   }

   public static Set computeSet(Class class2) {
      HashSet hashSet = new HashSet();
      Matcher matcher2 = PATTERN.matcher("");

      for (Method method : class2.getMethods()) {
         String name = method.getName();
         if (method.getParameterTypes().length == 0 && matcher2.reset(name).matches()) {
            String replaceFirst2 = name.replaceFirst("(get|is)", "");

            try {
               if (class2.getMethod("set" + replaceFirst2, method.getReturnType()) != null) {
                  String text = Character.toLowerCase(replaceFirst2.charAt(0)) + replaceFirst2.substring(1);
                  hashSet.add(text);
               }
            } catch (Exception e) {
            }
         }
      }

      return hashSet;
   }

   public static Object computeObj(String text, Object obj) {
      try {
         String text2 = "get" + text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
         Method method = obj.getClass().getMethod(text2);
         return method.invoke(obj);
      } catch (Exception e) {
         try {
            String text3 = "is" + text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
            Method method2 = obj.getClass().getMethod(text3);
            return method2.invoke(obj);
         } catch (Exception e2) {
            return null;
         }
      }
   }

   public static Properties computeProperties(Properties properties) {
      Properties properties2 = new Properties();
      properties.forEach((obj, obj2) -> {
         properties2.setProperty(obj.toString(), obj2.toString());
      });
      return properties2;
   }

   private static final void handleObj(Object obj, String text, Object obj2, List list) {
      PoolLogger poolLogger = PoolLogger.computePoolLogger(PropertyElf.class);
      String text2 = "set" + text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
      Method orElse2 = (java.lang.reflect.Method) list.stream().filter(obj3 -> {
         Method method = (Method) obj3;
         return method.getName().equals(text2) && method.getParameterCount() == 1;
      }).findFirst().orElse(null);
      if (orElse2 == null) {
         String text3 = "set" + text.toUpperCase(Locale.ENGLISH);
         orElse2 = (java.lang.reflect.Method) list.stream().filter(obj4 -> {
            Method method = (java.lang.reflect.Method) obj4;
            return method.getName().equals(text3) && method.getParameterCount() == 1;
         }).findFirst().orElse(null);
      }

      if (orElse2 == null) {
         poolLogger.handleText2("Property %s does not exist on target %s", new Object[]{text, obj.getClass().toString()});
         throw new RuntimeException(String.format("Property %s does not exist on target %s", text, obj.getClass()));
      }

      try {
         Class class2 = orElse2.getParameterTypes()[0];
         if (class2 == int.class) {
            orElse2.invoke(obj, Integer.parseInt(obj2.toString()));
         } else if (class2 == long.class) {
            orElse2.invoke(obj, Long.parseLong(obj2.toString()));
         } else if (class2 == short.class) {
            orElse2.invoke(obj, Short.parseShort(obj2.toString()));
         } else if (class2 == boolean.class || class2 == Boolean.class) {
            orElse2.invoke(obj, Boolean.parseBoolean(obj2.toString()));
         } else if (class2 == String.class) {
            orElse2.invoke(obj, obj2.toString());
         } else {
            try {
               poolLogger.handleText("Try to create a new instance of \"{}\"", new Object[]{obj2});
               orElse2.invoke(obj, Class.forName(obj2.toString()).getDeclaredConstructor().newInstance());
            } catch (InstantiationException | ClassNotFoundException e) {
               poolLogger.handleText("Class \"{}\" not found or could not instantiate it (Default constructor)", new Object[]{obj2});
               orElse2.invoke(obj, obj2);
            }
         }
      } catch (Exception e2) {
         poolLogger.handleText2("Failed to set property %s on target %s", new Object[]{text, obj.getClass().toString(), e2});
         throw new RuntimeException(e2);
      }
   }

}
