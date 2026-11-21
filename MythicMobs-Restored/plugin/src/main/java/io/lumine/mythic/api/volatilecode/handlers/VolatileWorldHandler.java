package io.lumine.mythic.api.volatilecode.handlers;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.volatilecode.disabled.VolatileBiomeDisabled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public interface VolatileWorldHandler {
   default AbstractBiome getBiome(AbstractLocation target) {
      return new VolatileBiomeDisabled(BukkitAdapter.adapt(target).getBlock().getBiome());
   }

   default Optional<AbstractBiome> getBiome(String key) {
      try {
         Biome biome = Biome.valueOf(key.toUpperCase());
         return Optional.of(new VolatileBiomeDisabled(biome));
      } catch (Error | Exception e) {
         return Optional.empty();
      }
   }

   default Collection<AbstractBiome> getBiomes() {
      Collection<AbstractBiome> biomes = Lists.newArrayList();

      for (Biome biome : Biome.values()) {
         biomes.add(new VolatileBiomeDisabled(biome));
      }

      return biomes;
   }

   default Optional<AbstractStructure> getStructure(String key) {
      return Optional.empty();
   }

   default Collection<AbstractStructure> getStructures() {
      return Lists.newArrayList();
   }

   default Collection<AbstractStructure> getStructures(AbstractLocation target) {
      return Lists.newArrayList();
   }

   void playSoundAtLocation(AbstractLocation abstractLocation, String str, float value, float value2, double value3);

   default boolean isChunkLoaded(AbstractWorld world, int x, int z) {
      return true;
   }

   Collection<AbstractEntity> getEntities(AbstractWorld abstractWorld);

   Collection<AbstractEntity> getLivingEntities(AbstractWorld abstractWorld);

   Collection<AbstractPlayer> getPlayers(AbstractWorld abstractWorld);

   default int getEntitiesInChunk(AbstractWorld world, int x, int z) {
      return 0;
   }

   default Entity[] getLoadedChunkEntities(AbstractWorld world, int x, int z) {
      World bukkitWorld = BukkitAdapter.adapt(world);
      if (!bukkitWorld.isChunkLoaded(x, z)) {
         return new Entity[0];
      } else {
         Chunk chunk = bukkitWorld.getChunkAt(x, z, false);
         return chunk == null ? new Entity[0] : chunk.getEntities();
      }
   }

   float getDifficultyScale(AbstractLocation abstractLocation);

   default Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius) {
      return this.getEntitiesNearLocation(location, radius, null);
   }

   default Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius, Predicate<AbstractEntity> predicate) {
      return new ObjectArrayList();
   }

   default Collection<AbstractEntity> getEntitiesNearLocation(
      AbstractLocation location, double radius, Double yMin, Double yMax, Predicate<AbstractEntity> predicate
   ) {
      return this.getEntitiesNearLocation(location, radius, predicate);
   }

   default RayTraceResult rayTraceBlock(
      AbstractLocation start, AbstractVector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks
   ) {
      if (!(maxDistance < 0.0) && start.getWorld() != null) {
         AbstractVector dir = direction.clone().normalize().multiply(maxDistance);
         AbstractLocation end = start.clone().add(dir);
         return this.rayTraceBlock(start, end, fluidCollisionMode, ignorePassableBlocks);
      } else {
         return null;
      }
   }

   default RayTraceResult rayTraceBlock(AbstractLocation start, AbstractLocation end, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
      return null;
   }

   default RayTraceResult rayTrace(
      AbstractLocation start,
      AbstractVector direction,
      double maxDistance,
      FluidCollisionMode fluidCollisionMode,
      double raySize,
      Predicate<Entity> entityFilter,
      Predicate<Material> blockFilter
   ) {
      return this.rayTrace(BukkitAdapter.adapt(start), BukkitAdapter.adapt(direction), maxDistance, fluidCollisionMode, raySize, entityFilter, blockFilter);
   }

   default RayTraceResult rayTrace(
      Location start,
      Vector direction,
      double maxDistance,
      FluidCollisionMode fluidCollisionMode,
      double raySize,
      Predicate<Entity> entityFilter,
      Predicate<Material> blockFilter
   ) {
      return null;
   }

   default RayTraceResult rayTrace(
      Location start, Vector direction, double maxDistance, double raySize, Predicate<Entity> entityFilter, Predicate<Material> blockFilter
   ) {
      return this.rayTrace(start, direction, maxDistance, FluidCollisionMode.NEVER, raySize, entityFilter, blockFilter);
   }

   default RayTraceResult rayTrace(
      Location start, Location end, FluidCollisionMode fluidCollisionMode, double raySize, Predicate<Entity> entityFilter, Predicate<Material> blockFilter
   ) {
      Vector direction = end.subtract(start).toVector().normalize();
      double maxDistance = start.distance(end);
      return this.rayTrace(start, direction, maxDistance, fluidCollisionMode, raySize, entityFilter, blockFilter);
   }

   Entity spawnInvisibleArmorStand(Location location);

   PacketArmorStand.PacketArmorStandEntityRenderer createVirtualArmorStandRenderer(PacketArmorStand packetArmorStand);

   PacketFallingBlock.PacketFallingBlockEntityRenderer createVirtualBlockRenderer(PacketFallingBlock packetFallingBlock);

   default PacketBlockDisplay.PacketBlockDisplayEntityRenderer createVirtualDisplayBlockEntityRenderer(PacketBlockDisplay wrapper) {
      throw new IllegalStateException("DisplayEntities are only available on 1.19.4+");
   }

   default PacketItemDisplay.PacketItemDisplayEntityRenderer createVirtualDisplayItemEntityRenderer(PacketItemDisplay wrapper) {
      throw new IllegalStateException("DisplayEntities are only available on 1.19.4+");
   }

   default PacketTextDisplay.PacketTextDisplayEntityRenderer createVirtualTextDisplayEntityRenderer(PacketTextDisplay wrapper) {
      throw new IllegalStateException("TextDisplayEntities are only available on 1.19.4+");
   }

   PacketItem.PacketItemEntityRenderer createVirtualItemRenderer(PacketItem packetItem);
}
