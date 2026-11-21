package com.ticxo.modelengine.core.listener;

import com.google.gson.JsonSyntaxException;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.data.DataUpdater;
import java.util.ArrayList;
import java.util.List;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WorldListener implements Listener {
   private static final int PHANTOM_UNLOAD_RECHECK_TICKS = 5;
   private final ModelGenerator generator;
   private boolean onWorldLoad = false;
   private boolean onWorldUnload = false;

   public WorldListener() {
      this.generator = ModelEngineAPI.getAPI().getModelGenerator();
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
         this.onWorldLoad = ConfigProperty.LOAD_ON_WORLD_LOAD.getBoolean();
         this.onWorldUnload = ConfigProperty.UNLOAD_ON_WORLD_UNLOAD.getBoolean();
      });
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onWorldLoad(WorldLoadEvent event) {
      if (this.onWorldLoad) {
         if (this.generator.isInitialized()) {
            this.loadEntities(event.getWorld().getEntities());
         } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.loadEntities(event.getWorld().getEntities())));
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onWorldUnload(WorldUnloadEvent event) {
      if (this.onWorldUnload) {
         if (this.generator.isInitialized()) {
            this.unloadEntities(event.getWorld().getEntities());
         } else {
            this.generator
               .queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.unloadEntities(event.getWorld().getEntities())));
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onEntityLoad(EntitiesLoadEvent event) {
      if (!this.onWorldLoad) {
         if (this.generator.isInitialized()) {
            this.loadEntities(event.getEntities());
         } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.loadEntities(event.getEntities())));
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onEntityUnload(EntitiesUnloadEvent event) {
      if (!this.onWorldUnload) {
         if (this.generator.isInitialized()) {
            this.unloadEntities(event.getEntities());
         } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.unloadEntities(event.getEntities())));
         }
      }
   }

   private void loadEntities(List<Entity> entities) {
      for (Entity entity : entities) {
         if (!(entity instanceof Player) && !ServerInfo.scheduleOnRegion(entity, () -> this.restoreSavedModel(entity))) {
            this.restoreSavedModel(entity);
         }
      }
   }

   private void restoreSavedModel(Entity entity) {
      if (ModelEngineAPI.getModeledEntity(entity.getUniqueId()) == null) {
         String jsonData = entity.getPersistentDataContainer().get(SavedData.DATA_KEY, PersistentDataType.STRING);
         if (jsonData != null) {
            try {
               SavedData data = DataUpdater.convertToSavedData(entity.getLocation(), jsonData);
               if (!DataUpdater.tryUpdate(data)) {
                  return;
               }

               ModeledEntity model = ModelEngineAPI.createModeledEntity(entity);
               model.load(data);
            } catch (JsonSyntaxException e) {
               e.printStackTrace();
            }
         }
      }
   }

   private void unloadEntities(List<Entity> entities) {
      List<Entity> unloaded = null;

      for (Entity entity : entities) {
         if (!(entity instanceof Player) && (!ServerInfo.HAS_CITIZENS || !CitizensAPI.getNPCRegistry().isNPC(entity))) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            ModeledEntity model = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
            if (model == null) {
               pdc.remove(SavedData.DATA_KEY);
            } else {
               if (model.shouldBeSaved()) {
                  try {
                     String cachedJson = model.getCachedSaveJson();
                     if (cachedJson != null) {
                        pdc.set(SavedData.DATA_KEY, PersistentDataType.STRING, cachedJson);
                     } else {
                        model.save()
                           .ifPresentOrElse(
                              data -> pdc.set(SavedData.DATA_KEY, PersistentDataType.STRING, data.toString()), () -> pdc.remove(SavedData.DATA_KEY)
                           );
                     }
                  } catch (Throwable ex) {
                     throw new IllegalStateException(
                        "Failed to save data for entity "
                           + entity.getType()
                           + " "
                           + entity.getUniqueId()
                           + " at "
                           + entity.getLocation().getX()
                           + ","
                           + entity.getLocation().getY()
                           + ","
                           + entity.getLocation().getZ(),
                        ex
                     );
                  }
               } else {
                  entity.getPersistentDataContainer().remove(SavedData.DATA_KEY);
               }

               ModelEngineAPI.getEntityHandler().setForcedInvisible(entity, false);
               ModelEngineAPI.getAPI().getModelUpdaters().forceRemoveModeledEntity(model);
               if (model.shouldBeSaved()) {
                  if (unloaded == null) {
                     unloaded = new ArrayList<>();
                  }

                  unloaded.add(entity);
               }
            }
         }
      }

      if (unloaded != null) {
         List<Entity> recheck = unloaded;
         if (ServerInfo.IS_FOLIA) {
            for (Entity entity : recheck) {
               ServerInfo.scheduleOnRegionDelayed(entity, () -> {
                  if (entity.isValid()) {
                     this.restoreSavedModel(entity);
                  }
               }, 5L);
            }
         } else {
            DualTicker.queueDelayedSyncTask(() -> {
               for (Entity entity2 : recheck) {
                  if (entity2.isValid()) {
                     this.restoreSavedModel(entity2);
                  }
               }
            }, 5);
         }
      }
   }
}
