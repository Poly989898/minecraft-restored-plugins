package me.TechsCode.UltraPermissions.base.addons;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.addons.cloud.CloudFileType;

public abstract class AddonLoader {
   protected SpigotTechPlugin plugin;
   protected CloudFileType fileType;
   protected File installedAddonsFolder;
   protected final HashMap<File, InstalledAddon> loaded = new HashMap<>();

   public AddonLoader() {
      this.installedAddonsFolder = null;
   }

   public AddonLoader setFileType(CloudFileType fileType) {
      if (this.fileType != null) {
         throw new IllegalStateException("FileType is already set, you cannot set it twice.");
      }

      this.fileType = fileType;
      return this;
   }

   public AddonLoader setPlugin(SpigotTechPlugin plugin) {
      if (this.plugin != null) {
         throw new IllegalStateException("Plugin is already set, you cannot set it twice.");
      }

      this.installedAddonsFolder = new File(plugin.getPluginFolder(), "Addons");
      this.plugin = plugin;
      return this;
   }

   public void onDisable() {
      new ArrayList<>(this.loaded.values()).forEach(this::unload);
   }

   public AddonLoader start() {
      if (!this.installedAddonsFolder.exists()) {
         this.installedAddonsFolder.mkdir();
      }

      this.plugin.getScheduler().runTaskTimer(this::sync, 20L, 20L);
      return this;
   }

   protected void sync() {
      List<File> list = this.getAddonJarsInFolder();
      list.stream()
         .filter(file -> file.getName().endsWith("." + this.fileType.getExtension()))
         .filter(
            file -> this.loaded.values().stream().noneMatch(installedAddon -> installedAddon.getAddonFile().getName().equals(file.getName()))
               || this.loaded
                  .values()
                  .stream()
                  .anyMatch(installedAddon -> installedAddon.getSize() != file.length() && installedAddon.getAddonFile().getName().equals(file.getName()))
         )
         .forEach(this::load);
      new ArrayList<>(this.loaded.values()).stream().filter(file -> !list.contains(file.getAddonFile())).forEach(this::unload);
   }

   protected abstract void load(File file);

   protected abstract void unload(InstalledAddon installedAddon);

   protected List<File> getAddonJarsInFolder() {
      return Arrays.asList(Objects.requireNonNull(this.installedAddonsFolder.listFiles()));
   }

   public File getInstalledAddonsFolder() {
      return this.installedAddonsFolder;
   }

   public CloudFileType getFileType() {
      return this.fileType;
   }

   public List<InstalledAddon> getInstalledAddons() {
      return new ArrayList<>(this.loaded.values());
   }
}
