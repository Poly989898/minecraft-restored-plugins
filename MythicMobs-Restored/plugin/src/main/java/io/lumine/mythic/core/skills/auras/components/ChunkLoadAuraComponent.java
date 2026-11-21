package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class ChunkLoadAuraComponent implements AuraComponent {
   private static final ConcurrentMap<ChunkLoadAuraComponent.ChunkKey, Integer> REF_COUNT = new ConcurrentHashMap<>();
   private final Aura.AuraTracker tracker;
   private final int radius;
   private final double lagThreshold;
   private final Set<ChunkLoadAuraComponent.ChunkKey> held = new HashSet<>();
   private int lastChunkX = Integer.MIN_VALUE;
   private int lastChunkZ = Integer.MIN_VALUE;

   public ChunkLoadAuraComponent(Aura.AuraTracker tracker, int radius, double lagThreshold) {
      this.tracker = tracker;
      this.radius = radius;
      this.lagThreshold = lagThreshold;
   }

   @Override
   public void auraStart() {
      this.updateChunks();
   }

   @Override
   public void auraTick() {
      if (this.lagThreshold > 0.0 && Bukkit.getTPS()[0] <= this.lagThreshold) {
         this.tracker.terminate();
      } else {
         Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
         if (!maybeEntity.isEmpty()) {
            Location loc = maybeEntity.get().getBukkitEntity().getLocation();
            int cx = loc.getBlockX() >> 4;
            int cz = loc.getBlockZ() >> 4;
            if (cx != this.lastChunkX || cz != this.lastChunkZ) {
               this.updateChunks();
            }
         }
      }
   }

   @Override
   public void auraStop() {
      this.releaseAll();
   }

   private void updateChunks() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         Entity entity = maybeEntity.get().getBukkitEntity();
         World world = entity.getWorld();
         int cx = entity.getLocation().getBlockX() >> 4;
         int cz = entity.getLocation().getBlockZ() >> 4;
         int effectiveRadius = Math.min(this.radius, Bukkit.getServer().getSimulationDistance());
         Set<ChunkLoadAuraComponent.ChunkKey> next = new HashSet<>();

         for (int dx = -effectiveRadius; dx <= effectiveRadius; dx++) {
            for (int dz = -effectiveRadius; dz <= effectiveRadius; dz++) {
               next.add(new ChunkLoadAuraComponent.ChunkKey(world, cx + dx, cz + dz));
            }
         }

         for (ChunkLoadAuraComponent.ChunkKey key : next) {
            if (!this.held.contains(key)) {
               acquire(key);
            }
         }

         for (ChunkLoadAuraComponent.ChunkKey keyx : new HashSet<>(this.held)) {
            if (!next.contains(keyx)) {
               release(keyx);
            }
         }

         this.held.clear();
         this.held.addAll(next);
         this.lastChunkX = cx;
         this.lastChunkZ = cz;
      }
   }

   private void releaseAll() {
      for (ChunkLoadAuraComponent.ChunkKey key : this.held) {
         release(key);
      }

      this.held.clear();
   }

   private static void acquire(ChunkLoadAuraComponent.ChunkKey key) {
      REF_COUNT.merge(key, 1, Integer::sum);
      key.world().getChunkAt(key.cx(), key.cz()).setForceLoaded(true);
   }

   private static void release(ChunkLoadAuraComponent.ChunkKey key) {
      REF_COUNT.compute(key, (k, count) -> {
         if (count != null && count > 1) {
            return count - 1;
         } else {
            if (k.world().isChunkLoaded(k.cx(), k.cz())) {
               k.world().getChunkAt(k.cx(), k.cz()).setForceLoaded(false);
            }

            return null;
         }
      });
   }

   private record ChunkKey(World world, int cx, int cz) {
      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof ChunkLoadAuraComponent.ChunkKey other)
               ? false
               : this.cx == other.cx && this.cz == other.cz && this.worldUid().equals(other.worldUid());
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.worldUid(), this.cx, this.cz);
      }

      private UUID worldUid() {
         return this.world.getUID();
      }
   }

   @MythicAuraComponent(
      name = "chunkload",
      aliases = {"keeploaded", "loadchunk"},
      description = "Keeps the aura holder's chunk(s) force-loaded for the duration of the aura"
   )
   public static class Factory implements AuraComponentFactory {
      private final int radius;
      private final double lagThreshold;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.radius = mlc.getInteger(new String[]{"radius", "r"}, 0);
         this.lagThreshold = mlc.getDouble(new String[]{"lag", "tps", "tpsthreshold"}, 0.0);
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new ChunkLoadAuraComponent(tracker, this.radius, this.lagThreshold);
      }
   }
}
