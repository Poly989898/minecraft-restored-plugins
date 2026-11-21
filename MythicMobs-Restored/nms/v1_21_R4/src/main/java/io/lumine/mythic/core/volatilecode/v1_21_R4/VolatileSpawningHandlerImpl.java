package io.lumine.mythic.core.volatilecode.v1_21_R4;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
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
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.SpawnerCreature.d;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R4.util.CraftSpawnCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;

public class VolatileSpawningHandlerImpl implements VolatileSpawningHandler {
   static final int MAGIC_NUMBER = (int)Math.pow(17.0, 2.0);
   public Map<String, VolatileSpawningHandlerImpl.WrappedMobCategory> categories = Maps.newConcurrentMap();

   public VolatileSpawningHandlerImpl(VolatileCodeHandler handler) {
      for (EnumCreatureType cat : EnumCreatureType.values()) {
         this.categories.put(cat.name().toUpperCase(), new VolatileSpawningHandlerImpl.WrappedMobCategory(cat));
      }
   }

   @Override
   public MythicSpawnState getSpawnState(AbstractWorld world) {
      World bukkitWorld = BukkitAdapter.adapt(world);
      WorldServer nmsWorld = ((CraftWorld)bukkitWorld).getHandle();

      int chunkCount;
      try {
         chunkCount = nmsWorld.m().a.j().a();
      } catch (Throwable var12) {
         chunkCount = 0;
      }

      d spawnState = nmsWorld.m().o();
      int limit = 0;
      EnumCreatureType mobCategory = EnumCreatureType.a;
      SpawnCategory spawnCategory = CraftSpawnCategory.toBukkit(mobCategory);
      if (CraftSpawnCategory.isValidForLimits(spawnCategory)) {
         limit = nmsWorld.getWorld().getSpawnLimit(spawnCategory);
      }

      int spawnableChunkCount = spawnState == null ? 0 : spawnState.a();
      Map<EnumCreatureType, Integer> categories = (Map<EnumCreatureType, Integer>)(spawnState == null ? Maps.newConcurrentMap() : spawnState.b());
      int globalMobCap = limit * spawnableChunkCount / MAGIC_NUMBER;
      return new VolatileSpawningHandlerImpl.WrappedSpawnState(world, chunkCount, limit, globalMobCap, categories);
   }

   @Override
   public byte getSpawnRange(AbstractWorld world) {
      World bukkitWorld = BukkitAdapter.adapt(world);
      WorldServer nmsWorld = ((CraftWorld)bukkitWorld).getHandle();
      return (byte)(nmsWorld.spigotConfig.mobSpawnRange - 1);
   }

   @Override
   public int getMobCountNear(AbstractEntity entity) {
      if (entity == null) {
         return 0;
      } else {
         Entity bukkit = BukkitAdapter.adapt(entity);
         if (bukkit == null) {
            return 0;
         } else {
            net.minecraft.world.entity.Entity nms = ((CraftEntity)bukkit).getHandle();
            if (nms.dV() instanceof WorldServer level) {
               int var14 = nms.an().f().ordinal();
               int max = 0;

               for (EntityPlayer player : level.z()) {
                  double dx = player.dA() - nms.dA();
                  double dz = player.dG() - nms.dG();
                  if (dx * dx + dz * dz <= 16384.0 && var14 < player.mobCounts.length && var14 < player.mobBackoffCounts.length) {
                     int c = player.mobCounts[var14] + player.mobBackoffCounts[var14];
                     if (c > max) {
                        max = c;
                     }
                  }
               }

               return max;
            } else {
               return 0;
            }
         }
      }
   }

   @Override
   public void trackForSpawnCap(AbstractEntity entity) {
      if (entity != null) {
         Entity bukkit = BukkitAdapter.adapt(entity);
         if (bukkit != null) {
            net.minecraft.world.entity.Entity nms = ((CraftEntity)bukkit).getHandle();
            if (nms.dV() instanceof WorldServer level) {
               PlayerChunkMap chunkMap = level.m().a;
               chunkMap.updatePlayerMobTypeMap(nms);
               ChunkCoordIntPair chunkPos = nms.dx();
               chunkMap.updateFailurePlayerMobTypeMap(chunkPos.h, chunkPos.i, nms.an().f());
            }
         }
      }
   }

   public static class WrappedMobCategory implements MobSpawnCategory {
      private final EnumCreatureType nmsCategory;

      public WrappedMobCategory(EnumCreatureType category) {
         this.nmsCategory = category;
      }

      @Override
      public String getName() {
         return this.nmsCategory.a();
      }

      public EnumCreatureType toNMS() {
         return this.nmsCategory;
      }
   }

   public static class WrappedSpawnState implements MythicSpawnState {
      private final AbstractWorld world;
      private final int spawnableChunkCount;
      private final int maxMobsPerPlayer;
      private final int maxMobCount;
      private final Map<EnumCreatureType, Integer> mobCount;

      @Override
      public Collection<MobSpawnCategory> getCategories() {
         Collection<MobSpawnCategory> cats = Lists.newArrayList();

         for (EnumCreatureType cat : this.mobCount.keySet()) {
            cats.add(new VolatileSpawningHandlerImpl.WrappedMobCategory(cat));
         }

         return cats;
      }

      @Override
      public Map<MobSpawnCategory, Integer> getCurrentMobCounts() {
         Map<MobSpawnCategory, Integer> map = Maps.newConcurrentMap();

         for (Entry<EnumCreatureType, Integer> entry : this.mobCount.entrySet()) {
            map.put(new VolatileSpawningHandlerImpl.WrappedMobCategory(entry.getKey()), entry.getValue());
         }

         return map;
      }

      @Override
      public int getCurrentMobCount(MobSpawnCategory category) {
         EnumCreatureType nmsCategory = ((VolatileSpawningHandlerImpl.WrappedMobCategory)category).toNMS();
         return this.mobCount.getOrDefault(nmsCategory, 0);
      }

      @Override
      public int getMonsterCount() {
         try {
            return this.mobCount.getOrDefault(EnumCreatureType.a, 0);
         } catch (NullPointerException var2) {
            return 0;
         }
      }

      @Generated
      public WrappedSpawnState(AbstractWorld world, int spawnableChunkCount, int maxMobsPerPlayer, int maxMobCount, Map<EnumCreatureType, Integer> mobCount) {
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
      public Map<EnumCreatureType, Integer> getMobCount() {
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
