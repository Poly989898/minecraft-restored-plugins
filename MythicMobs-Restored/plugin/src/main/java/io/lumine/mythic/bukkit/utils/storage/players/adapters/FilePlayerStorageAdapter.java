package io.lumine.mythic.bukkit.utils.storage.players.adapters;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.profiles.MojangApi;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.storage.players.PlayerStorageAdapter;
import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import java.io.File;
import java.util.Optional;

public abstract class FilePlayerStorageAdapter<T extends LuminePlugin, P extends Profile> extends PluginModule<T> implements PlayerStorageAdapter<P> {
   private String strDataDirectory = "data";
   private String strStorageDirectory = "players";
   private File dataDirectory;
   private File storageDirectory;

   public FilePlayerStorageAdapter(T plugin) {
      this(plugin, true);
   }

   public FilePlayerStorageAdapter(T plugin, boolean setupDirs) {
      super(plugin, false);
      if (setupDirs) {
         this.setupDirectories();
      }
   }

   public void setDataDirectory(String file) {
      this.strDataDirectory = file;
      this.setupDirectories();
   }

   public void setStorageDirectory(String file) {
      this.strStorageDirectory = file;
      this.setupDirectories();
   }

   public void setDirectories(String data, String storage) {
      this.strDataDirectory = data;
      this.strStorageDirectory = storage;
      this.setupDirectories();
   }

   private void setupDirectories() {
      this.dataDirectory = new File(this.plugin.getDataFolder(), this.strDataDirectory);
      this.storageDirectory = new File(this.dataDirectory, this.strStorageDirectory);
      if (!this.dataDirectory.exists()) {
         this.dataDirectory.mkdir();
      }

      if (!this.storageDirectory.exists()) {
         this.storageDirectory.mkdir();
      }
   }

   @Override
   public Promise<Optional<P>> loadByName(String key, int subProfile) {
      return MojangApi.usernameToUuid(key).thenComposeAsync(uuid -> this.load(uuid, subProfile));
   }

   protected abstract File getFile(String str, int value);

   public File getDataDirectory() {
      return this.dataDirectory;
   }

   public File getStorageDirectory() {
      return this.storageDirectory;
   }
}
