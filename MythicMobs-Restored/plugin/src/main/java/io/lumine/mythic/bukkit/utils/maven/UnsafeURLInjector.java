package io.lumine.mythic.bukkit.utils.maven;

import io.lumine.mythic.bukkit.utils.logging.Log;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import sun.misc.Unsafe;

final class UnsafeURLInjector implements URLInjector {
   private final ArrayDeque<URL> unopenedURLs;
   private final ArrayList<URL> pathURLs;

   public UnsafeURLInjector(ArrayDeque<URL> unopenedURLs, ArrayList<URL> pathURLs) {
      this.unopenedURLs = unopenedURLs;
      this.pathURLs = pathURLs;
   }

   public static URLInjector create(URLClassLoader classLoader) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         Object ucp = fetchField(unsafe, URLClassLoader.class, classLoader, "ucp");
         ArrayDeque<URL> unopenedURLs = (ArrayDeque<URL>)fetchField(unsafe, ucp, "unopenedUrls");
         ArrayList<URL> pathURLs = (ArrayList<URL>)fetchField(unsafe, ucp, "path");
         Log.info("Injecting dependencies into {0}", classLoader.getClass().toString());
         return new UnsafeURLInjector(unopenedURLs, pathURLs);
      } catch (Throwable e) {
         e.printStackTrace();
         return (loader, url) -> {
            throw new UnsupportedOperationException();
         };
      }
   }

   private static Object fetchField(Unsafe unsafe, Object object, String name) throws NoSuchFieldException {
      return fetchField(unsafe, object.getClass(), object, name);
   }

   private static Object fetchField(Unsafe unsafe, Class<?> clazz, Object object, String name) throws NoSuchFieldException {
      Field field = clazz.getDeclaredField(name);
      long offset = unsafe.objectFieldOffset(field);
      return unsafe.getObject(object, offset);
   }

   @Override
   public void addURL(@Nonnull ClassLoader classLoader, @Nonnull URL url) {
      this.unopenedURLs.addLast(url);
      this.pathURLs.add(url);
   }
}
