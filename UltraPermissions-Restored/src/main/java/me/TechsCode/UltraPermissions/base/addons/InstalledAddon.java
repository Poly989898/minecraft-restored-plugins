package me.TechsCode.UltraPermissions.base.addons;

import java.io.File;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudAddon;

public class InstalledAddon {
   private final SpigotTechPlugin plugin;
   private final File addonFile;
   private final String name;
   private final String version;
   private final String author;
   private final String depend;
   private final long size;
   private final boolean loaded;

   public static InstalledAddon newLoadedAddon(SpigotTechPlugin spigotTechPlugin, File file, String text, String version, String author, String depend) {
      return new InstalledAddon(spigotTechPlugin, file, text, version, author, depend, true);
   }

   public static InstalledAddon newNotLoadedAddon(SpigotTechPlugin spigotTechPlugin, File file, String text, String version, String author, String depend) {
      return new InstalledAddon(spigotTechPlugin, file, text, version, author, depend, false);
   }

   private InstalledAddon(SpigotTechPlugin spigotTechPlugin, File file, String text, String version, String author, String depend, boolean flag) {
      this.plugin = spigotTechPlugin;
      this.addonFile = file;
      this.name = text;
      this.version = version;
      this.author = author;
      this.depend = depend;
      this.loaded = flag;
      this.size = file.length();
   }

   public File getAddonFile() {
      return this.addonFile;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public String getAuthor() {
      return this.author;
   }

   public String getDepend() {
      return this.depend;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public long getSize() {
      return this.size;
   }

   public Optional<CloudAddon> getCloudAddon() {
      return this.plugin.getAddonManager().getCloudAddons().stream().filter(cloudAddon -> cloudAddon.getName().equals(this.name)).findFirst();
   }

   public boolean uninstall() {
      return this.addonFile.exists() ? this.addonFile.delete() : false;
   }

   public boolean isOutOfDate() {
      Optional cloudAddon = this.getCloudAddon();
      return cloudAddon.isPresent() && !((CloudAddon)cloudAddon.get()).getVersion().equals(this.version);
   }
}
