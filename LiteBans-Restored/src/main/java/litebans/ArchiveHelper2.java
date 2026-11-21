package litebans;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class ArchiveHelper2 {
   public static final Map MAP = new HashMap();

   public static ArchiveHelper2 computeArchiveHelper(Class class2) {
      return (ArchiveHelper2)MAP.get(class2);
   }

   public abstract void handleConfigSection(ConfigSection configSection, Writer writer);

   public abstract ConfigSection computeConfigSection(Reader reader);

   public abstract ConfigSection computeConfigSection(Reader reader, ConfigSection configSection);

   static {
      try {
         MAP.put(ArchiveHelper3.class, new ArchiveHelper3());
      } catch (NoClassDefFoundError e) {
      }

      try {
         MAP.put(ArchiveHelper4.class, new ArchiveHelper4());
      } catch (NoClassDefFoundError e2) {
      }
   }
}
