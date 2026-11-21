package io.lumine.mythic.core.utils.annotations;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AnnotationUtil {
   public static ClassLoader classLoader = AnnotationUtil.class.getClassLoader();
   private static final Map<String, Collection<Class<?>>> ANNOTATED_CLASS_CACHE = new ConcurrentHashMap<>();

   public static <T extends Annotation> Collection<Class<?>> getAnnotatedClasses(JavaPlugin plugin, String path, Class<T> clazz) {
      String filename = resolveJarPath(plugin);
      if (filename == null) {
         throw new IllegalStateException("Failed to load jar entries");
      } else {
         String cacheKey = filename + " " + path + " " + clazz.getName();
         Collection<Class<?>> cached = ANNOTATED_CLASS_CACHE.computeIfAbsent(cacheKey, k -> scanAnnotatedClasses(filename, path, clazz));
         return Lists.newArrayList(cached);
      }
   }

   private static String resolveJarPath(JavaPlugin plugin) {
      String location = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

      try {
         return URLDecoder.decode(location, StandardCharsets.UTF_8.toString());
      } catch (UnsupportedEncodingException e) {
         try {
            return URLDecoder.decode(location, StandardCharsets.ISO_8859_1.toString());
         } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
         }
      }
   }

   private static <T extends Annotation> Collection<Class<?>> scanAnnotatedClasses(String filename, String path, Class<T> clazz) {
      Collection<Class<?>> classesToReturn = Lists.newArrayList();

      try (JarFile jarFile = new JarFile(filename)) {
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
               String s1 = jarEntry.getName();
               String cn1 = s1.substring(0, s1.length() - 6).replace("/", ".");

               try {
                  if (cn1.startsWith(path)) {
                     SimpleEntry<T, Class<?>> entry = getClazz(cn1, clazz);
                     if (entry.getKey() == null) {
                        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Annotation not found for {0}", cn1);
                     } else {
                        classesToReturn.add(entry.getValue());
                     }
                  }
               } catch (Throwable e) {
                  Log.info("Failed to parse class {0}", cn1);
                  e.printStackTrace();
               }
            }
         }
      } catch (FileNotFoundException ex) {
         ex.printStackTrace();
      } catch (IOException ex2) {
         ex2.printStackTrace();
      }

      return classesToReturn;
   }

   private static <T extends Annotation> SimpleEntry<T, Class<?>> getClazz(String cn1, Class<T> clazz) throws ClassNotFoundException {
      Class<?> c1 = null;

      try {
         c1 = Class.forName(cn1, false, classLoader);
      } catch (Throwable e) {
         return new SimpleEntry<>(null, null);
      }

      return new SimpleEntry<>(c1.getAnnotation(clazz), c1);
   }
}
