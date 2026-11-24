package me.TechsCode.UltraPermissions.base.source;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;

public class JarClassLoader {
   public static Method ADD_URL_METHOD;
   public final URLClassLoader classLoader;

   public JarClassLoader(SpigotTechPlugin plugin) {
      ClassLoader classLoader = plugin.getBootstrap().getClass().getClassLoader();
      this.classLoader = (URLClassLoader)classLoader;
   }

   public void load(File file) {
      try {
         ADD_URL_METHOD.invoke(this.classLoader, file.toPath().toUri().toURL());
      } catch (IllegalAccessException | InvocationTargetException | MalformedURLException malformedURLException) {
         malformedURLException.printStackTrace();
      }
   }

   static {
      try {
         ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
         ADD_URL_METHOD.setAccessible(true);
      } catch (Exception ex) {
      }
   }
}
