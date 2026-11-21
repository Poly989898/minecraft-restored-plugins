package io.lumine.mythic.core.skills.auras;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.auras.AuraManager;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.events.MythicPlayerLoadedEvent;
import io.lumine.mythic.bukkit.events.MythicPlayerQuitEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;

public class AuraExecutor implements AuraManager {
   private final MythicPlugin core;
   private final SkillExecutor skillManager;
   private ConcurrentHashMap<UUID, AuraRegistry> auraRegistries = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, SkillMechanic> persistentMechanics = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<UUID, Map<String, AuraPersistenceData>> unloadedAuras = new ConcurrentHashMap<>();
   private Terminable garbageCollector;
   private final CompositeTerminable registry = CompositeTerminable.create();

   public AuraExecutor(MythicPlugin core, SkillExecutor skillManager) {
      this.core = core;
      this.skillManager = skillManager;
      this.garbageCollector = Schedulers.async().runRepeating(() -> this.auraRegistries.values().forEach(registry -> {}), 20L, 100L);
      Events.subscribe(MythicPlayerLoadedEvent.class, EventPriority.MONITOR)
         .handler(event -> this.restorePersistentAuras(event.getPlayerData()))
         .bindWith(this.registry);
      Events.subscribe(MythicPlayerQuitEvent.class, EventPriority.MONITOR)
         .handler(event -> this.savePersistentAuras(event.getPlayerData()))
         .bindWith(this.registry);
   }

   public void shutdown() {
      this.garbageCollector.terminate();
      this.registry.terminate();
   }

   @Override
   public AuraRegistry getAuraRegistry(AbstractEntity entity) {
      return this.getAuraRegistry(entity.getUniqueId());
   }

   @Override
   public AuraRegistry getAuraRegistry(UUID uuid) {
      return this.auraRegistries.computeIfAbsent(uuid, AuraRegistry::new);
   }

   public void registerPersistentAura(String persistId, SkillMechanic mechanic) {
      if (persistId != null && !persistId.isEmpty() && mechanic != null) {
         this.persistentMechanics.putIfAbsent(persistId, mechanic);
      }
   }

   @Override
   public boolean getHasAura(AbstractEntity entity, String name) {
      AuraRegistry registry = this.auraRegistries.get(entity.getUniqueId());
      return registry != null && registry.hasAura(name);
   }

   @Override
   public int getAuraStacks(AbstractEntity entity, String name) {
      AuraRegistry registry = this.auraRegistries.get(entity.getUniqueId());
      return registry != null ? registry.getStacks(name) : 0;
   }

   @Override
   public void removeAuraStacks(AbstractEntity entity, String buffName, int a) {
      AuraRegistry registry = this.auraRegistries.get(entity.getUniqueId());
      if (registry != null) {
         registry.removeStack(buffName, a);
      }
   }

   public void sendAuraDebugInfo(CommandSender sender) {
      int registries = 0;
      int auras = 0;
      int auraTrackers = 0;
      int activeHolders = 0;
      int inactiveHolders = 0;
      Map<String, Integer> auraTypeCounts = Maps.newHashMap();

      for (AuraRegistry registry : this.auraRegistries.values()) {
         registries++;
         UUID holderUUID = registry.getHolder();
         Entity holderEntity = Bukkit.getEntity(holderUUID);
         if (holderEntity == null) {
            inactiveHolders++;
         } else {
            activeHolders++;
         }

         for (Entry<String, Queue<Aura.AuraTracker>> entry : registry.getAuras().entrySet()) {
            String auraType = entry.getKey();
            auras++;

            for (Aura.AuraTracker auraTracker : entry.getValue()) {
               if (auraType == null || auraType.isEmpty()) {
                  auraType = entry.getValue().getClass().getSimpleName();
               }

               auraTrackers++;
            }

            auraTypeCounts.put(auraType, auraTypeCounts.getOrDefault(auraType, 0) + 1);
         }
      }

      List<Entry<String, Integer>> topAuras = auraTypeCounts.entrySet()
         .stream()
         .sorted(Entry.<String, Integer>comparingByValue().reversed())
         .limit(5L)
         .collect(Collectors.toList());
      CommandHelper.send(sender, "<gold>Aura Registries<gray>: <white>" + registries);
      CommandHelper.send(sender, "<gold>Aura Types<gray>: <white>" + auras);
      CommandHelper.send(sender, "<gold>Aura Trackers<gray>: <white>" + auraTrackers);
      CommandHelper.send(sender, "<gold>Active Holders<gray>: <white>" + activeHolders);
      CommandHelper.send(sender, "<gold>Inactive Holders<gray>: <white>" + inactiveHolders);

      for (Entry<String, Integer> aura : topAuras) {
         CommandHelper.send(sender, "<gold>Top Aura: <gray>" + aura.getKey() + "<white>, Count: " + aura.getValue());
      }
   }

   public void persistAurasThroughUnload(AbstractEntity holder) {
      if (holder != null) {
         AuraRegistry registry = this.auraRegistries.get(holder.getUniqueId());
         if (registry != null) {
            Map<String, AuraPersistenceData> snapshots = new ConcurrentHashMap<>();
            ArrayList<Aura.AuraTracker> trackers = new ArrayList<>();

            for (Queue<Aura.AuraTracker> auraQueue : registry.getAuras().values()) {
               trackers.addAll(auraQueue);
            }

            for (Aura.AuraTracker tracker : trackers) {
               AuraPersistenceData data = tracker.snapshotForUnload();
               if (data != null) {
                  snapshots.put(data.id, data);
               }

               tracker.terminate();
            }

            if (!snapshots.isEmpty()) {
               this.unloadedAuras.put(holder.getUniqueId(), snapshots);
            }
         }
      }
   }

   public void restoreUnloadedAuras(AbstractEntity entity) {
      if (entity != null) {
         Map<String, AuraPersistenceData> stored = this.unloadedAuras.remove(entity.getUniqueId());
         if (stored != null) {
            for (Entry<String, AuraPersistenceData> entry : stored.entrySet()) {
               this.restoreAura(entity, entry.getKey(), entry.getValue());
            }
         }
      }
   }

   private boolean restoreAura(AbstractEntity target, String id, AuraPersistenceData data) {
      if (data.ticksRemaining <= 0 && data.startDuration > 0) {
         return false;
      } else if (data.casterUuid != null && !data.casterUuid.isEmpty()) {
         AbstractEntity casterEntity = MythicProvider.get().getBootstrap().getEntity(UUID.fromString(data.casterUuid));
         if (casterEntity != null && casterEntity.isValid()) {
            SkillMechanic mechanic = this.persistentMechanics.get(id);
            if (!(mechanic instanceof ITargetedEntitySkill targeted)) {
               return false;
            } else {
               SkillCaster caster = this.skillManager.getCaster(casterEntity);
               SkillMetadataImpl meta = new SkillMetadataImpl(SkillTriggers.API, caster, target);
               meta.setEntityTarget(target);
               meta.setMetadata("aura-persist", data);
               SkillResult result = targeted.castAtEntity(meta, target);
               return result != SkillResult.INVALID_TARGET && result != SkillResult.ERROR;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void restorePersistentAuras(PlayerData playerData) {
      if (playerData != null && playerData.getEntity() != null) {
         Map<String, AuraPersistenceData> persisted = playerData.getPersistentAuras();
         if (!persisted.isEmpty()) {
            AbstractEntity playerEntity = playerData.getEntity();

            for (Entry<String, AuraPersistenceData> entry : new ArrayList<>(persisted.entrySet())) {
               AuraPersistenceData data = entry.getValue();
               String id = data != null && data.id != null && !data.id.isEmpty() ? data.id : entry.getKey();
               if (data == null || id == null || id.isEmpty()) {
                  playerData.removePersistentAura(entry.getKey());
               } else if (!this.restoreAura(playerEntity, id, data)) {
                  playerData.removePersistentAura(id);
               }
            }
         }
      }
   }

   private void savePersistentAuras(PlayerData playerData) {
      if (playerData != null) {
         Map<String, AuraPersistenceData> stored = playerData.getPersistentAuras();
         AuraRegistry registry = this.auraRegistries.get(playerData.getUniqueId());
         if (registry != null) {
            stored.clear();

            for (Queue<Aura.AuraTracker> auraQueue : registry.getAuras().values()) {
               for (Aura.AuraTracker tracker : auraQueue) {
                  if (tracker.shouldPersistOnQuit()) {
                     tracker.suppressEndSkillOnQuit();
                     AuraPersistenceData data = tracker.createPersistenceSnapshot();
                     if (data != null && data.id != null && !data.id.isEmpty()) {
                        stored.put(data.id, data);
                     }
                  }
               }
            }
         }
      }
   }
}
