package me.TechsCode.UltraPermissions.base.addons;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudAddon;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudDataFetcher;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudFileType;
import org.apache.commons.io.FileUtils;

public class AddonManager {
   private final AddonLoader loader;
   private final CloudDataFetcher cloud;

   public AddonManager(final String text, final CloudFileType cloudFileType, AddonLoader addonLoader, SpigotTechPlugin spigotTechPlugin) {
      this.loader = addonLoader.setPlugin(spigotTechPlugin).setFileType(cloudFileType).start();
      this.cloud = new CloudDataFetcher() {
         @Override
         public String getUrl() {
            return text;
         }

         @Override
         public CloudFileType getFileType() {
            return cloudFileType;
         }
      };
      spigotTechPlugin.addDisableHook(this::onDisable);
   }

   public void onDisable() {
      this.loader.onDisable();
      this.cloud.onDisable();
   }

   public boolean isCloudOnline() {
      return this.cloud.isOnline();
   }

   public List<CloudAddon> getCloudAddons() {
      return this.cloud.getAddons();
   }

   public List<InstalledAddon> getInstalledAddons() {
      return this.loader.getInstalledAddons();
   }

   public AddonLoader getLoader() {
      return this.loader;
   }

   public CloudDataFetcher getCloud() {
      return this.cloud;
   }

   public void download(CloudAddon cloudAddon) {
      try {
         File file = new File(this.loader.getInstalledAddonsFolder().getAbsolutePath() + "/" + cloudAddon.getName() + "." + this.loader.getFileType().getExtension());
         file.delete();
         FileUtils.copyURLToFile(new URL(cloudAddon.getUrl()), file);
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }
}
