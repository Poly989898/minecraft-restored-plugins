package me.TechsCode.UltraPermissions.base.loader;

import java.lang.reflect.Modifier;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.reflection.indexer.ClasspathIndexer;
import me.TechsCode.UltraPermissions.base.reflection.indexer.Node;

public class LoaderUtil {
   public LoaderUtil() {
   }

   public static Optional<Class<?>> getMainClass(Class<? extends TechPlugin<?>> type, ClassLoader classLoader) {
      Optional first = ClasspathIndexer.index().classes().stream().filter(node -> {
         if (!node.getClassName().startsWith("me.TechsCode")) {
            return false;
         }

         try {
            Class clazz = classLoader.loadClass(node.getClassName());
            return type.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers());
         } catch (NoClassDefFoundError | ClassNotFoundException exception) {
            return false;
         }
      }).findFirst();
      return first.isPresent() ? ((Node)first.get()).getAsClass() : Optional.empty();
   }
}
