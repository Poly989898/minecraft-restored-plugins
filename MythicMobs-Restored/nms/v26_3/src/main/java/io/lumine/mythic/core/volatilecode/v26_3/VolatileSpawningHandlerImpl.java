package io.lumine.mythic.core.volatilecode.v26_3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MobSpawnCategory;
import io.lumine.mythic.api.spawning.MythicSpawnState;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileSpawningHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftSpawnCategory;
import org.bukkit.entity.SpawnCategory;

public class VolatileSpawningHandlerImpl implements VolatileSpawningHandler {
   static final int MAGIC_NUMBER = (int)Math.pow(17.0, 2.0);
   public Map<String, VolatileSpawningHandlerImpl.WrappedMobCategory> categories = Maps.newConcurrentMap();

   public VolatileSpawningHandlerImpl(VolatileCodeHandler handler) {
      for (MobCategory cat : MobCategory.values()) {
         this.categories.put(cat.name().toUpperCase(), new VolatileSpawningHandlerImpl.WrappedMobCategory(cat));
      }
   }

   @Override
   public MythicSpawnState getSpawnState(AbstractWorld world) {
      World bukkitWorld = BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = ((CraftWorld)bukkitWorld).getHandle();

      int chunkCount;
      try {
         chunkCount = nmsWorld.getChunkSource().chunkMap.getDistanceManager().getNaturalSpawnChunkCount();
      } catch (Throwable e) {
         chunkCount = 0;
      }

      SpawnState spawnState = nmsWorld.getChunkSource().getLastSpawnState();
      int limit = 0;
      MobCategory mobCategory = MobCategory.MONSTER;
      SpawnCategory spawnCategory = CraftSpawnCategory.toBukkit(mobCategory);
      if (CraftSpawnCategory.isValidForLimits(spawnCategory)) {
         limit = nmsWorld.getWorld().getSpawnLimit(spawnCategory);
      }

      int spawnableChunkCount = spawnState == null ? 0 : spawnState.getSpawnableChunkCount();
      Map<MobCategory, Integer> categories = (Map<MobCategory, Integer>)(spawnState == null ? Maps.newConcurrentMap() : spawnState.getMobCategoryCounts());
      int globalMobCap = limit * spawnableChunkCount / MAGIC_NUMBER;
      return new VolatileSpawningHandlerImpl.WrappedSpawnState(world, chunkCount, limit, globalMobCap, categories);
   }

   @Override
   public byte getSpawnRange(AbstractWorld world) {
      World bukkitWorld = BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = ((CraftWorld)bukkitWorld).getHandle();
      return (byte)(nmsWorld.spigotConfig.mobSpawnRange - 1);
   }

   public static class WrappedMobCategory implements MobSpawnCategory {
      private final MobCategory nmsCategory;

      public WrappedMobCategory(MobCategory category) {
         this.nmsCategory = category;
      }

      @Override
      public String getName() {
         return this.nmsCategory.getName();
      }

      public MobCategory toNMS() {
         return this.nmsCategory;
      }
   }

   public static class WrappedSpawnState implements MythicSpawnState {
      private final AbstractWorld world;
      private final int spawnableChunkCount;
      private final int maxMobsPerPlayer;
      private final int maxMobCount;
      private final Map<MobCategory, Integer> mobCount;

      @Override
      public Collection<MobSpawnCategory> getCategories() {
         Collection<MobSpawnCategory> cats = Lists.newArrayList();

         for (MobCategory cat : this.mobCount.keySet()) {
            cats.add(new VolatileSpawningHandlerImpl.WrappedMobCategory(cat));
         }

         return cats;
      }

      @Override
      public Map<MobSpawnCategory, Integer> getCurrentMobCounts() {
         Map<MobSpawnCategory, Integer> map = Maps.newConcurrentMap();

         for (Entry<MobCategory, Integer> entry : this.mobCount.entrySet()) {
            map.put(new VolatileSpawningHandlerImpl.WrappedMobCategory(entry.getKey()), entry.getValue());
         }

         return map;
      }

      @Override
      public int getCurrentMobCount(MobSpawnCategory category) {
         MobCategory nmsCategory = ((VolatileSpawningHandlerImpl.WrappedMobCategory)category).toNMS();
         return this.mobCount.getOrDefault(nmsCategory, 0);
      }

      @Override
      public int getMonsterCount() {
         try {
            return this.mobCount.getOrDefault(MobCategory.MONSTER, 0);
         } catch (NullPointerException e) {
            return 0;
         }
      }

      @Generated
      public WrappedSpawnState(AbstractWorld world, int spawnableChunkCount, int maxMobsPerPlayer, int maxMobCount, Map<MobCategory, Integer> mobCount) {
         this.world = world;
         this.spawnableChunkCount = spawnableChunkCount;
         this.maxMobsPerPlayer = maxMobsPerPlayer;
         this.maxMobCount = maxMobCount;
         this.mobCount = mobCount;
      }

      @Generated
      @Override
      public AbstractWorld getWorld() {
         return this.world;
      }

      @Generated
      @Override
      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      @Generated
      @Override
      public int getMaxMobsPerPlayer() {
         return this.maxMobsPerPlayer;
      }

      @Generated
      @Override
      public int getMaxMobCount() {
         return this.maxMobCount;
      }

      @Generated
      public Map<MobCategory, Integer> getMobCount() {
         return this.mobCount;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof VolatileSpawningHandlerImpl.WrappedSpawnState other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getSpawnableChunkCount() != other.getSpawnableChunkCount()) {
            return false;
         } else if (this.getMaxMobsPerPlayer() != other.getMaxMobsPerPlayer()) {
            return false;
         } else if (this.getMaxMobCount() != other.getMaxMobCount()) {
            return false;
         } else {
            Object this$world = this.getWorld();
            Object other$world = other.getWorld();
            if (this$world == null ? other$world == null : this$world.equals(other$world)) {
               Object this$mobCount = this.getMobCount();
               Object other$mobCount = other.getMobCount();
               return this$mobCount == null ? other$mobCount == null : this$mobCount.equals(other$mobCount);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof VolatileSpawningHandlerImpl.WrappedSpawnState;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getSpawnableChunkCount();
         result = result * 59 + this.getMaxMobsPerPlayer();
         result = result * 59 + this.getMaxMobCount();
         Object $world = this.getWorld();
         result = result * 59 + ($world == null ? 43 : $world.hashCode());
         Object $mobCount = this.getMobCount();
         return result * 59 + ($mobCount == null ? 43 : $mobCount.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "VolatileSpawningHandlerImpl.WrappedSpawnState(world="
            + this.getWorld()
            + ", spawnableChunkCount="
            + this.getSpawnableChunkCount()
            + ", maxMobsPerPlayer="
            + this.getMaxMobsPerPlayer()
            + ", maxMobCount="
            + this.getMaxMobCount()
            + ", mobCount="
            + this.getMobCount()
            + ")";
      }
   }
}
