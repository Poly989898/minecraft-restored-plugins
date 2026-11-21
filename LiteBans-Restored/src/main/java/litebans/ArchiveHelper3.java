package litebans;

import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ArchiveHelper3 extends ArchiveHelper2 {
   private final ThreadLocal threadLocal = new YamlThreadLocal(this);

   @Override
   public void handleConfigSection(ConfigSection configSection, Writer writer) {
      ((Yaml)this.threadLocal.get()).dump(configSection.keys, writer);
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader) {
      return this.computeConfigSection(reader, null);
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader, ConfigSection configSection) {
      Map as = (Map)((Yaml)this.threadLocal.get()).loadAs(reader, LinkedHashMap.class);
      if (as == null) {
         as = new LinkedHashMap();
      }

      return new ConfigSection(as, configSection);
   }
}
