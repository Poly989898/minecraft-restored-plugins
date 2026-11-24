package me.TechsCode.UltraPermissions.base.loader;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.TechPlugin;

public class Loader {
   private TechPlugin<?> techPlugin;

   public Loader(Object obj, Class<? extends TechPlugin<?>> type, ClassLoader classLoader) {
      Optional mainClass = LoaderUtil.getMainClass(type, classLoader);
      if (mainClass.isPresent()) {
         try {
            this.techPlugin = (TechPlugin<?>)((Class)mainClass.get()).getDeclaredConstructors()[0].newInstance(obj);
         } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
         }
      }
   }

   public void unload() {
      if (this.techPlugin != null) {
         this.techPlugin.disable();
      }
   }
}
