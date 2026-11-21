package io.lumine.mythic.bukkit.utils.storage.players.adapters.file;

import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.storage.players.MultiProfile;
import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import io.lumine.mythic.bukkit.utils.storage.players.adapters.FilePlayerStorageAdapter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class JsonPlayerStorageAdapter<T extends LuminePlugin, P extends Profile> extends FilePlayerStorageAdapter<T, P> {
   private Class<P> clazz;

   public JsonPlayerStorageAdapter(T plugin, Class<P> clazz) {
      super(plugin);
      this.clazz = clazz;
   }

   public JsonPlayerStorageAdapter(T plugin, Class<P> clazz, boolean setupDirs) {
      super(plugin, setupDirs);
      this.clazz = clazz;
   }

   @Override
   public void load(T plugin) {
   }

   @Override
   public void unload() {
   }

   @Override
   public Promise<Optional<P>> load(UUID key, int subProfile) {
      return Promise.supplyingAsync(() -> {
         File file = this.getFile(key.toString(), subProfile);
         if (!file.exists()) {
            return Optional.empty();
         } else {
            try {
               Optional optional;
               try (FileReader reader = new FileReader(file)) {
                  optional = Optional.of((Profile)GsonProvider.standard().fromJson(reader, this.clazz));
               }

               return optional;
            } catch (Exception e) {
               Log.severe("Failed to load Json Profile in: " + this.getClass().getName());
               e.printStackTrace();
               return Optional.empty();
            }
         }
      });
   }

   @Override
   public Promise<Boolean> save(UUID key, P object) {
      return Promise.supplyingAsync(() -> this.saveSync(key, object));
   }

   @Override
   public boolean saveSync(UUID key, P object) {
      File file;
      if (object instanceof MultiProfile multiProfile) {
         file = this.getFile(key.toString(), multiProfile.getSubProfileId());
      } else {
         file = this.getFile(key.toString(), 0);
      }

      if (!file.exists()) {
         if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
         }

         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
            return false;
         }
      }

      try {
         boolean flag;
         try (FileWriter writer = new FileWriter(file)) {
            GsonProvider.standard().toJson(object, writer);
            flag = true;
         }

         return flag;
      } catch (IOException ex) {
         Log.severe("Failed to save Json Profile in: " + this.getClass().getName());
         ex.printStackTrace();
         return false;
      }
   }

   @Override
   protected File getFile(String key, int subProfile) {
      return subProfile == 0 ? new File(this.getStorageDirectory(), key + ".json") : new File(this.getStorageDirectory(), key + "-" + subProfile + ".json");
   }
}
