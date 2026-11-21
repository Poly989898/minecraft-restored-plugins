package litebans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArchiveHelper4 extends ArchiveHelper2 {
   private final Gson gson2 = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(ConfigSection.class, new ArchiveJsonSerializerImpl(this)).create();

   @Override
   public void handleConfigSection(ConfigSection configSection, Writer writer) {
      this.gson2.toJson(configSection.keys, writer);
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader) {
      return this.computeConfigSection(reader, null);
   }

   @Override
   public ConfigSection computeConfigSection(Reader reader, ConfigSection configSection) {
      Map fromJson2 = (Map)this.gson2.fromJson(reader, LinkedHashMap.class);
      if (fromJson2 == null) {
         fromJson2 = new LinkedHashMap();
      }

      return new ConfigSection(fromJson2, configSection);
   }
}
