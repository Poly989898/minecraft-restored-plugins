package litebans;

import java.io.File;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArchiveHelper9 {
   private final Platform platform;
   private final File file;
   private final ConfigModule configModule;
   private Locale locale;
   private ConfigFile configFile;

   public ArchiveHelper9(@NotNull Platform platform2, @NotNull File file2) {
      this.platform = platform2;
      this.file = file2;
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
      this.locale = this.obtainLocale();
   }

   public ArchiveHelper9(Platform platform2, File file2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         File dataFolder = platform2.getDataFolder();
         String text = "messages.yml";
         file2 = new File(dataFolder, text);
      }

      this(platform2, file2);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final File getFile() {
      return this.file;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public final Locale getLocale() {
      return this.locale;
   }

   public final void setLocale(@NotNull Locale locale2) {
      this.locale = locale2;
   }

   public final ConfigFile getConfigFile() {
      return this.configFile;
   }

   public final void setConfigFile(@Nullable ConfigFile configFile2) {
      this.configFile = configFile2;
   }

   public final Locale obtainLocale() {
      return Locale.getDefault(Category.FORMAT);
   }

   public final void handleException(@NotNull Exception exception) {
      Logger logger = this.platform.getLogger();
      String text = "messages";
      this.configModule.logLoadError(text, exception);
      String text2 = "[!!] " + text + ".yml";
      if (this.file.exists()) {
         logger.severe(text2 + " may be invalid. Check with https://yaml-online-parser.appspot.com");
      } else {
         logger.severe(text2 + " could not be created in " + this.platform.getDataFolder().getAbsolutePath());
      }

      if (this.configModule.isDebugAtLeast(1)) {
         this.configModule.handleConfigError(exception);
      }
   }
}
