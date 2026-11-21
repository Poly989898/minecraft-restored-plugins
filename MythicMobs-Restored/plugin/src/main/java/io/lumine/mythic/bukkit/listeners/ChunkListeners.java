package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ChunkListeners extends ReloadableModule<MythicBukkit> implements Listener {
   public ChunkListeners(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      if (ServerVersion.isAfter(MinecraftVersions.v1_17)) {
         try {
            Events.subscribe(EntitiesLoadEvent.class).handler(event -> this.scanChunk(event.getChunk())).bindWith(this);
         } catch (Error | Exception e) {
         }
      } else {
         Events.subscribe(ChunkLoadEvent.class).handler(event -> this.scanChunk(event.getChunk())).bindWith(this);
      }

      if (ServerVersion.isAfter(MinecraftVersions.v1_17)) {
         try {
            Events.subscribe(EntitiesUnloadEvent.class).handler(this::unloadChunkEntities).bindWith(this);
         } catch (Error | Exception ex) {
         }
      } else {
         Events.subscribe(ChunkUnloadEvent.class).handler(this::unloadChunkEntitiesOld).bindWith(this);
      }

      Events.subscribe(ChunkUnloadEvent.class).handler(this::unloadChunk).bindWith(this);
      Events.subscribe(WorldUnloadEvent.class).handler(this::unloadWorld).bindWith(this);
      Schedulers.sync().runLater(() -> {
         for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
               Schedulers.runOnRegion(new Location(world, chunk.getX() << 4, 0.0, chunk.getZ() << 4), () -> this.scanChunk(chunk));
            }
         }
      }, 1L);
   }

   @Override
   public void unload() {
   }

   private void scanChunk(Chunk chunk) {
      if (chunk.isLoaded()) {
         Entity[] entityList = chunk.getEntities();
         if (entityList.length != 0) {
            for (Entity chunkEntity : entityList) {
               if (!(chunkEntity instanceof Player)) {
                  this.restoreAudienceVisibility(chunkEntity);
                  if (chunkEntity.getPersistentDataContainer().has(MobKeys.TYPE, PersistentDataType.STRING)) {
                     if (chunkEntity.getPersistentDataContainer().has(MobKeys.DESPAWN_MODE, DataType.asEnum(DespawnMode.class))
                        && (chunkEntity.getPersistentDataContainer().get(MobKeys.DESPAWN_MODE, DataType.asEnum(DespawnMode.class)))
                           .getDespawnsOnUnload()) {
                        chunkEntity.remove();
                     } else {
                        ActiveMob registered = this.plugin.getMobManager().getActiveMob(chunkEntity.getUniqueId()).orElse(null);
                        if (registered != null) {
                           AbstractEntity tracked = registered.getEntity();
                           if (tracked == null || tracked.getBukkitEntity() != chunkEntity) {
                              registered.setEntity(BukkitAdapter.adapt(chunkEntity));
                              if (registered.getType() != null) {
                                 registered.getType().applyMobOptions(registered, registered.getLevel());
                                 registered.getType().applyMobVolatileOptions(registered);
                              }
                           }
                        } else {
                           Optional<ActiveMob> am = this.getPlugin().getMobManager().loadMythicMob(chunkEntity);
                           if (am.isEmpty() || am.get().getType() == null || am.get().getDespawnMode().getDespawnsOnUnload()) {
                              chunkEntity.remove();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void restoreAudienceVisibility(Entity chunkEntity) {
      PersistentDataContainer pdc = chunkEntity.getPersistentDataContainer();
      if (pdc.has(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY)) {
         UUID[] arr = pdc.get(MobKeys.EXECUTION_AUDIENCE, DataType.UUID_ARRAY);
         if (arr != null) {
            this.getPlugin().getMobManager().registerAudienceTrackedEntity(chunkEntity.getUniqueId());
            this.getPlugin().getMobManager().applyAudienceVisibility(BukkitAdapter.adapt(chunkEntity), new HashSet<>(Arrays.asList(arr)));
         }
      }
   }

   private void unloadChunkEntities(EntitiesUnloadEvent e) {
      for (Entity chunkEntity : e.getEntities()) {
         if (chunkEntity != null && this.getPlugin().getMobManager().isActiveMob(chunkEntity.getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(chunkEntity);
            if (am != null) {
               if (ServerVersion.isFolia()) {
                  Schedulers.of(chunkEntity).runLater(() -> {
                     if (!am.getEntity().isValid()) {
                        if (am.getType() != null && !am.getDespawnMode().getDespawnsOnUnload()) {
                           am.setUnloaded();
                        } else {
                           am.despawn();
                        }
                     }
                  }, 5L);
               } else {
                  Schedulers.sync().runLater(() -> {
                     if (!am.getEntity().isValid()) {
                        if (am.getType() != null && !am.getDespawnMode().getDespawnsOnUnload()) {
                           am.setUnloaded();
                        } else {
                           am.despawn();
                        }
                     }
                  }, 5L);
               }
            }
         }
      }
   }

   private void unloadChunk(ChunkUnloadEvent e) {
      String cS = e.getChunk().getWorld().getName() + "-" + e.getChunk().getX() + "-" + e.getChunk().getZ();
      Optional<Collection<MythicSpawner>> maybeSpawners = this.getPlugin().getSpawnerManager().getSpawnersByChunk(cS);
      if (maybeSpawners.isPresent()) {
         for (MythicSpawner ms : maybeSpawners.get()) {
            ms.unloadSpawner();
         }
      }
   }

   private void unloadChunkEntitiesOld(ChunkUnloadEvent e) {
      for (Entity ee : e.getChunk().getEntities()) {
         if (ee instanceof LivingEntity && this.getPlugin().getMobManager().isActiveMob(ee.getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance((LivingEntity)ee);
            if (am != null && am.getType() != null && am.getDespawnMode().getDespawnsOnUnload()) {
               if (ServerVersion.isFolia()) {
                  Schedulers.of(ee).run(() -> am.despawn());
               } else {
                  am.despawn();
               }
            }
         }
      }
   }

   public void unloadWorld(WorldUnloadEvent e) {
      for (Entity ee : e.getWorld().getEntities()) {
         if (this.getPlugin().getMobManager().isActiveMob(ee.getUniqueId())) {
            ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(ee);
            if (am != null) {
               if (am.getDespawnMode().getDespawnsOnUnload()) {
                  if (ServerVersion.isFolia()) {
                     Schedulers.of(ee).run(() -> am.despawn());
                  } else {
                     am.despawn();
                  }
               } else {
                  am.setUnloaded();
               }
            }
         }
      }
   }
}
