package io.lumine.mythic.bukkit.utils.maven;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import javax.annotation.Nonnull;

public abstract class URLClassLoaderAccess {
   private final URLClassLoader classLoader;

   static URLClassLoaderAccess create(URLClassLoader classLoader) {
      if (URLClassLoaderAccess.Reflection.isSupported()) {
         return new URLClassLoaderAccess.Reflection(classLoader);
      } else {
         return (URLClassLoaderAccess.Unsafe.isSupported()
            ? new URLClassLoaderAccess.Unsafe(classLoader)
            : URLClassLoaderAccess.Noop.INSTANCE);
      }
   }

   protected URLClassLoaderAccess(URLClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public abstract void addURL(@Nonnull URL url);

   private static class Noop extends URLClassLoaderAccess {
      private static final URLClassLoaderAccess.Noop INSTANCE = new URLClassLoaderAccess.Noop();

      private Noop() {
         super(null);
      }

      @Override
      public void addURL(@Nonnull URL url) {
         throw new UnsupportedOperationException();
      }
   }

   private static class Reflection extends URLClassLoaderAccess {
      private static final Method ADD_URL_METHOD;

      private static boolean isSupported() {
         return ADD_URL_METHOD != null;
      }

      Reflection(URLClassLoader classLoader) {
         super(classLoader);
      }

      @Override
      public void addURL(@Nonnull URL url) {
         try {
            ADD_URL_METHOD.invoke(super.classLoader, url);
         } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
         }
      }

      static {
         Method addUrlMethod;
         try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
         } catch (Exception e) {
            addUrlMethod = null;
         }

         ADD_URL_METHOD = addUrlMethod;
      }
   }

   private static class Unsafe extends URLClassLoaderAccess {
      private static final sun.misc.Unsafe UNSAFE;
      private final Collection<URL> unopenedURLs;
      private final Collection<URL> pathURLs;

      private static boolean isSupported() {
         return UNSAFE != null;
      }

      Unsafe(URLClassLoader classLoader) {
         super(classLoader);

         Collection<URL> unopenedURLs;
         Collection<URL> pathURLs;
         try {
            Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
            unopenedURLs = (Collection<URL>)fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>)fetchField(ucp.getClass(), ucp, "path");
         } catch (Throwable e) {
            unopenedURLs = null;
            pathURLs = null;
         }

         this.unopenedURLs = unopenedURLs;
         this.pathURLs = pathURLs;
      }

      private static Object fetchField(Class<?> clazz, Object object, String name) throws NoSuchFieldException {
         Field field = clazz.getDeclaredField(name);
         long offset = UNSAFE.objectFieldOffset(field);
         return UNSAFE.getObject(object, offset);
      }

      @Override
      public void addURL(@Nonnull URL url) {
         this.unopenedURLs.add(url);
         this.pathURLs.add(url);
      }

      static {
         sun.misc.Unsafe unsafe;
         try {
            Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (sun.misc.Unsafe)unsafeField.get(null);
         } catch (Throwable e) {
            unsafe = null;
         }

         UNSAFE = unsafe;
      }
   }
}
