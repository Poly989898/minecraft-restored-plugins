package litebans;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 0)
public class BukkitConfigFile extends ConfigFile {
   private FileConfiguration fileConfiguration;

   public BukkitConfigFile(Platform platform) {
      super(platform);
   }

   @Override
   public ConfigFile load(File file) {
      try {
         this.setError((Exception) null);
         this.setFile(file);
         File parentFile = file.getParentFile();
         if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Could not create folder: " + parentFile.getAbsolutePath());
         }

         if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create file: " + file.getAbsolutePath());
         }

         try {
            this.load(new FileInputStream(file));
         } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
               this.setError(e);
            }
         }

         return this;
      } catch (Throwable e2) {
         throw SneakyThrow.<RuntimeException>rethrow(e2);
      }
   }

   @Override
   public ConfigFile load(InputStream inputStream) {
      this.setError((Exception) null);

      try {
         this.fileConfiguration = new YamlConfiguration();
         this.handleReader(new InputStreamReader(inputStream, Charsets.UTF_8));
      } catch (Exception e) {
         this.setError(e);
      }

      return this;
   }

   @Override
   public ConfigFile reload() {
      try {
         return this.load(this.getFile());
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ConfigFile save() {
      return this.save(this.getFile());
   }

   @Override
   public ConfigFile save(File file) {
      this.setError((Exception) null);

      try {
         this.writeFile(file, this.fileConfiguration.saveToString());
      } catch (Exception e) {
         this.setError(e);
      }

      return this;
   }

   @Override
   public @Nullable ConfigKeyReader getSection(String text) {
      ConfigurationSection configurationSection = this.fileConfiguration.getConfigurationSection(text);
      return configurationSection == null ? null : new BukkitKeyReader(this, configurationSection);
   }

   @Override
   public Collection getKeys(boolean flag) {
      return this.fileConfiguration.getKeys(flag);
   }

   @Override
   public ConfigFile set(String text, Object obj) {
      this.fileConfiguration.set(text, obj);
      return this;
   }

   @Override
   public ConfigFile clear() {
      try {
         this.fileConfiguration.loadFromString("");
         this.setError((Exception) null);
         return this;
      } catch (Throwable e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleReader(Reader reader) {
      try {
         StringBuilder stringBuilder = new StringBuilder();
         BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);

         String text;
         try {
            while ((text = bufferedReader.readLine()) != null) {
               stringBuilder.append(text);
               stringBuilder.append('\n');
            }
         } catch (Throwable e) {
            if (bufferedReader != null) {
               try {
                  bufferedReader.close();
               } catch (Throwable e2) {
                  e.addSuppressed(e2);
               }
            }

            throw e;
         }

         if (bufferedReader != null) {
            bufferedReader.close();
         }

         this.fileConfiguration.loadFromString(stringBuilder.toString());
      } catch (Throwable e3) {
         throw SneakyThrow.<RuntimeException>rethrow(e3);
      }
   }

   @Override
   public Object get(String text) {
      return this.fileConfiguration.get(text);
   }

   @Override
   public Object get(String text, Object obj) {
      return this.fileConfiguration.get(text, obj);
   }

   @Override
   public boolean getBoolean(String text) {
      return this.fileConfiguration.getBoolean(text);
   }

   @Override
   public boolean getBoolean(String text, boolean flag) {
      return this.fileConfiguration.getBoolean(text, flag);
   }

   @Override
   public double getDouble(String text) {
      return this.fileConfiguration.getDouble(text);
   }

   @Override
   public int getInt(String text, int value) {
      return this.fileConfiguration.getInt(text, value);
   }

   @Override
   public long getLong(String text, long value) {
      return this.fileConfiguration.getLong(text, value);
   }

   @Override
   public String getString(String text) {
      return this.fileConfiguration.getString(text);
   }

   @Override
   public String getString(String text, String text2) {
      return this.fileConfiguration.getString(text, text2);
   }

   @Override
   public List getStringList(String text) {
      return this.fileConfiguration.getStringList(text);
   }

   @Override
   public boolean isSet(String text) {
      return this.fileConfiguration.isSet(text);
   }
}
