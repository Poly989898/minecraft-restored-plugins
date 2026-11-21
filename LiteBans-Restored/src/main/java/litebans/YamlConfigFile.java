package litebans;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 1)
public class YamlConfigFile extends ConfigFile {
   private ConfigSection configSection3;
   private final ArchiveHelper2 archiveHelper3;

   public YamlConfigFile(Platform platform) {
      super(platform);
      this.archiveHelper3 = this.obtainArchiveHelper();
   }

   public YamlConfigFile(Platform platform, ArchiveHelper2 archiveHelper) {
      super(platform);
      this.archiveHelper3 = archiveHelper;
   }

   @Override
   public boolean contains(String text) {
      return this.configSection3.computeObj(text) != null;
   }

   @Override
   public Object get(String text) {
      return this.configSection3.computeObj(text);
   }

   @Override
   public Object get(String text, Object obj) {
      return this.configSection3.computeObj(text, obj);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.configSection3.getBoolean(text);
   }

   @Override
   public boolean getBoolean(String text, boolean flag) {
      Object obj = this.get(text, flag);
      return obj instanceof Boolean ? (Boolean)obj : flag;
   }

   @Override
   public double getDouble(String text) {
      return this.configSection3.getDouble(text);
   }

   @Override
   public int getInt(String text, int value) {
      return this.configSection3.getInt(text, value);
   }

   @Override
   public Collection getKeys(boolean flag) {
      return this.configSection3.getKeys();
   }

   @Override
   public long getLong(String text, long value) {
      return this.configSection3.getLong(text, value);
   }

   public ArchiveHelper2 obtainArchiveHelper() {
      ArchiveHelper2 archiveHelper = this.getFile() != null && !this.getFile().getName().endsWith(".yml") ? obtainArchiveHelper2() : obtainArchiveHelper3();
      if (archiveHelper == null) {
         ArchiveHelper2 archiveHelper2 = obtainArchiveHelper2();
         if (archiveHelper2 == null) {
            throw new AssertionError("No configuration provider (yaml/json)");
         } else {
            return archiveHelper2;
         }
      } else {
         return archiveHelper;
      }
   }

   public static ArchiveHelper2 obtainArchiveHelper2() {
      return ArchiveHelper2.computeArchiveHelper(ArchiveHelper4.class);
   }

   public static ArchiveHelper2 obtainArchiveHelper3() {
      return ArchiveHelper2.computeArchiveHelper(ArchiveHelper3.class);
   }

   @Override
   public String getString(String text) {
      return this.configSection3.getString(text);
   }

   @Override
   public String getString(String text, String text2) {
      try {
         return this.configSection3.getString(text, text2);
      } catch (ClassCastException e) {
         this.platform.getLogger().severe("CCE getString(\"" + text + "\")");
         throw e;
      }
   }

   @Override
   public List getStringList(String text) {
      return this.configSection3.computeList(text);
   }

   @Override
   public boolean isSet(String text) {
      return this.contains(text);
   }

   @Override
   public ConfigFile load(File file) {
      this.setError((Exception) null);
      File parentFile = file.getParentFile();
      if (file.getName().endsWith(".yml") && this.archiveHelper3 == obtainArchiveHelper2()) {
         return this;
      }

      this.setFile(file);

      try {
         if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Could not create folder: " + parentFile.getAbsolutePath());
         }

         if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create file: " + file.getAbsolutePath());
         }

         this.load(new FileInputStream(file));
      } catch (Exception e) {
         this.setError(e);
         this.perform();
      }

      return this;
   }

   private final void perform() {
      this.configSection3 = null;
   }

   @Override
   public ConfigFile load(InputStream inputStream) {
      this.setError((Exception) null);

      try {
         this.clear();
         this.configSection3 = this.archiveHelper3.computeConfigSection(new InputStreamReader(inputStream, Charsets.UTF_8));
      } catch (Exception e) {
         this.setError(e);
         this.perform();
      }

      return this;
   }

   @Override
   public ConfigFile reload() {
      this.load(this.getFile());
      return this;
   }

   @Override
   public ConfigFile save() {
      return this.save(this.getFile());
   }

   @Override
   public ConfigFile save(File file) {
      this.setError((Exception) null);

      try {
         StringWriter stringWriter = new StringWriter();
         this.archiveHelper3.handleConfigSection(this.configSection3, stringWriter);
         String string = stringWriter.toString();
         if (string.isEmpty()) {
            return this;
         }

         this.writeFile(file, string);
      } catch (Exception e) {
         this.setError(e);
      }

      return this;
   }

   @Override
   public @Nullable ConfigKeyReader getSection(String text) {
      ConfigSection configSection;
      try {
         configSection = this.configSection3.getSection(text);
      } catch (ClassCastException e) {
         this.handleText(text, e);
         return null;
      }

      return configSection == null ? null : new YamlKeyReader(this, configSection);
   }

   void handleText(String text, ClassCastException classCastException) {
      if (classCastException.getMessage() != null && classCastException.getMessage().length() >= 8) {
         String substring2 = classCastException.getMessage().substring(6);
         String replace2 = substring2.substring(0, substring2.indexOf(32)).replace("java.lang.", "");
         this.platform
            .getLogger()
            .warning(
               "Expected a config section at \""
                  + text
                  + "\" but found a "
                  + replace2
                  + " (in "
                  + this.getFile().getName()
                  + "). This section will be treated as empty. Define it as \""
                  + text
                  + ": {}\" if you want an empty node list."
            );
      }
   }

   @Override
   public ConfigFile set(String text, Object obj) {
      this.configSection3.set(text, obj);
      return this;
   }

   @Override
   public ConfigFile clear() {
      this.perform();
      this.setError((Exception) null);
      return this;
   }
}
