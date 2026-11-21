package litebans;

import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Supplier;

public final class ArchiveHelper extends ArchiveHelper2 {
   final File file2;
   final Class class3;

   ArchiveHelper(File file, Class class2) {
      this.file2 = file;
      this.class3 = class2;
   }

   @Override
   public void handleConfigSection(ConfigSection configSection, Writer writer) {
      if (StringsKt.checkText2(this.file2.getName(), ".json", false, 2, null)) {
         ArchiveHelper2.computeArchiveHelper(ArchiveHelper4.class).handleConfigSection(configSection, writer);
      }
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader) {
      return this.computeConfigSection(reader, null);
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader, ConfigSection configSection) {
      try {
         try {
            try {
               Constructor constructor = this.class3.getConstructors()[0];
               Object[] objs = new Object[]{reader};
               Object instance = constructor.newInstance(objs);
               Intrinsics.checkNotNullExpressionValue(instance, "");
               Supplier supplier = (Supplier)instance;
               Map map = (Map)supplier.get();
               return new ConfigSection(map, configSection);
            } catch (InstantiationException e) {
               throw SneakyThrow.<RuntimeException>rethrow(e);
            }
         } catch (IllegalAccessException e) {
            throw SneakyThrow.<RuntimeException>rethrow(e);
         }
      } catch (InvocationTargetException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
