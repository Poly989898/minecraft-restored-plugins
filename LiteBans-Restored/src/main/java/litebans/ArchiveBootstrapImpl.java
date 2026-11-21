package litebans;

import java.io.File;

public final class ArchiveBootstrapImpl implements Bootstrap {
   final ConfigModule configModule;
   final Class class3;

   ArchiveBootstrapImpl(ConfigModule configModule, Class class2) {
      this.configModule = configModule;
      this.class3 = class2;
   }

   @Override
   public ConfigFile loadConfigFile(Platform platform, File file) {
      ArchiveHelper archiveHelper = new ArchiveHelper(file, this.class3);
      return new YamlConfigFile(this.configModule.platform, archiveHelper).load(file);
   }
}
