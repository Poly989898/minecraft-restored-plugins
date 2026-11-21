package io.lumine.mythic.core.worlds;

import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.serialization.WrappedJsonFile;
import io.lumine.mythic.core.SerializingComponent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager extends SerializingComponent {
   protected Map<String, WrappedJsonFile<WorldData>> worldData;

   public WorldManager(MythicBukkit core) {
      super(core);
   }

   public void load(MythicBukkit plugin) {
      this.worldData = new ConcurrentHashMap<>();

      for (File file : this.getModuleFiles("worlds")) {
         if (file.getName().endsWith(".json")) {
            try {
               String worldName = file.getName().split("\\.")[0];
               WrappedJsonFile<WorldData> loaded = this.loadFile(file, WorldData.class);
               if (loaded != null) {
                  this.worldData.put(worldName, loaded);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public void unload() {
      this.worldData.forEach((uuid, file) -> {
         if (file != null) {
            file.get().getVariables().unload();
            file.save();
         }
      });
   }

   public WorldData getWorldData(AbstractWorld world) {
      return this.worldData
         .computeIfAbsent(world.getName(), k -> new WrappedJsonFile<>(this, new File(this.getBasedir(), "worlds/" + k + ".json"), new WorldData(world)))
         .get();
   }

   public void saveAll() {
      this.worldData.forEach((uuid, file) -> {
         if (file != null && file.get() != null) {
            file.get().getVariables().clean();
            file.save();
         }
      });
   }
}
