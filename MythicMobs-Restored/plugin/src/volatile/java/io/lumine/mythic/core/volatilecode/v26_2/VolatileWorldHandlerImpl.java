package io.lumine.mythic.core.volatilecode.v26_2;

import ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.api.volatilecode.virtual.PacketItem;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.lib.lang3.Validate;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualArmorStand;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualDisplayBlock;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualDisplayItem;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualFallingBlock;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualItem;
import io.lumine.mythic.core.volatilecode.v26_2.virtual.VirtualTextDisplay;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class VolatileWorldHandlerImpl implements VolatileWorldHandler {
   private static Method entityGetter;
   private static final org.bukkit.entity.Entity[] NO_ENTITIES = new org.bukkit.entity.Entity[0];
   private final Map<Biome, AbstractBiome> biomeMap = Maps.newConcurrentMap();
   private final Map<String, AbstractBiome> biomeLookup = Maps.newConcurrentMap();
   private final Map<Structure, AbstractStructure> structureMap = Maps.newConcurrentMap();
   private final Map<String, AbstractStructure> structureLookup = Maps.newConcurrentMap();

   public VolatileWorldHandlerImpl(VolatileCodeHandler handler) {
      CraftServer craftServer = (CraftServer)Bukkit.getServer();
      Optional<Registry<Biome>> maybeBiomeRegistry = craftServer.getHandle().getServer().registryAccess().lookup(Registries.BIOME);
      if (maybeBiomeRegistry.isEmpty()) {
         MythicLogger.error("BiomeRegistry not found");
      } else {
         Registry<Biome> biomeRegistry = maybeBiomeRegistry.get();
         biomeRegistry.entrySet().forEach(entry -> {
            String key = ((ResourceKey)entry.getKey()).identifier().toString();
            int id = biomeRegistry.getId((Biome)entry.getValue());
            Biome biome = (Biome)entry.getValue();
            VolatileBiomeImpl volatileBiome = new VolatileBiomeImpl(this, id, key, biomeRegistry.getOrThrow((ResourceKey)entry.getKey()));
            this.biomeMap.put(biome, volatileBiome);
            this.biomeLookup.put(key, volatileBiome);
         });
         Optional<Registry<Structure>> maybeStructureRegistry = craftServer.getHandle().getServer().registryAccess().lookup(Registries.STRUCTURE);
         if (maybeStructureRegistry.isEmpty()) {
            MythicLogger.error("StructureRegistry not found");
         } else {
            Registry<Structure> structureRegistry = maybeStructureRegistry.get();
            structureRegistry.entrySet()
               .forEach(
                  entry -> {
                     String key = ((ResourceKey)entry.getKey()).identifier().toString();
                     int id = structureRegistry.getId((Structure)entry.getValue());
                     Structure structure = (Structure)entry.getValue();
                     VolatileStructureImpl volatileStructure = new VolatileStructureImpl(
                        this, id, key, structureRegistry.getOrThrow((ResourceKey)entry.getKey()), structure
                     );
                     this.structureMap.put(structure, volatileStructure);
                     this.structureLookup.put(key, volatileStructure);
                  }
               );
         }
      }
   }

   @Override
   public void playSoundAtLocation(AbstractLocation location, String sound, float volume, float pitch, double radius) {
      Location l = BukkitAdapter.adapt(location);
      l.getWorld().playSound(l, sound, volume, pitch);
   }

   @Override
   public boolean isChunkLoaded(AbstractWorld world, int x, int z) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      return null != nmsWorld.getChunkIfLoaded(x, z);
   }

   public static boolean hasAsyncEntityGetter() {
      return entityGetter != null;
   }

   public static LevelEntityGetter<Entity> getEntityGetter(ServerLevel level) {
      if (entityGetter == null) {
         return level.getEntities();
      } else {
         try {
            return (LevelEntityGetter<Entity>)entityGetter.invoke(level);
         } catch (Throwable var2) {
            return null;
         }
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(AbstractWorld world) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractEntity> ret = Lists.newArrayList();

      for (Entity entity : getEntityGetter(nmsWorld).getAll()) {
         ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
      }

      return ret;
   }

   @Override
   public Collection<AbstractEntity> getLivingEntities(AbstractWorld world) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractEntity> ret = Lists.newArrayList();

      for (Entity entity : getEntityGetter(nmsWorld).getAll()) {
         if (entity instanceof LivingEntity) {
            ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
         }
      }

      return ret;
   }

   @Override
   public Collection<AbstractPlayer> getPlayers(AbstractWorld world) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractPlayer> ret = Lists.newArrayList();

      for (ServerPlayer entity : nmsWorld.players()) {
         ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
      }

      return ret;
   }

   @Override
   public int getEntitiesInChunk(AbstractWorld world, int x, int z) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      LevelChunk chunk = nmsWorld.getChunkIfLoaded(x, z);
      if (chunk == null) {
         return 0;
      } else {
         CraftChunk craftChunk = new CraftChunk(chunk);
         return craftChunk.getEntities().length;
      }
   }

   @Override
   public org.bukkit.entity.Entity[] getLoadedChunkEntities(AbstractWorld world, int x, int z) {
      ServerLevel nmsWorld = ((CraftWorld)BukkitAdapter.adapt(world)).getHandle();
      ChunkEntitySlices slices = nmsWorld.moonrise$getEntityLookup().getChunk(x, z);
      return slices != null && !slices.isEmpty() && nmsWorld.getChunkSource().isChunkLoaded(x, z) ? nmsWorld.getChunkEntities(x, z) : NO_ENTITIES;
   }

   @Override
   public Collection<AbstractBiome> getBiomes() {
      return Collections.unmodifiableCollection(this.biomeMap.values());
   }

   @Override
   public Optional<AbstractBiome> getBiome(String key) {
      if (!key.contains(":")) {
         key = "minecraft:" + key;
      }

      key = key.toLowerCase();
      return Optional.ofNullable(this.biomeLookup.getOrDefault(key, null));
   }

   @Override
   public AbstractBiome getBiome(AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      BlockPos blockPosition = new BlockPos(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      ServerLevel server = bukkitWorld.getHandle();
      Biome biome = (Biome)server.getBiomeManager().getBiome(blockPosition).value();
      return this.biomeMap.getOrDefault(biome, null);
   }

   @Override
   public Collection<AbstractStructure> getStructures() {
      return Collections.unmodifiableCollection(this.structureMap.values());
   }

   @Override
   public Optional<AbstractStructure> getStructure(String key) {
      if (!key.contains(":")) {
         key = "minecraft:" + key;
      }

      key = key.toLowerCase();
      return Optional.ofNullable(this.structureLookup.getOrDefault(key, null));
   }

   @Override
   public Collection<AbstractStructure> getStructures(AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      BlockPos blockPosition = new BlockPos(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      ServerLevel server = bukkitWorld.getHandle();
      Set<Structure> structures = server.structureManager().getAllStructuresAt(blockPosition).keySet();
      List<AbstractStructure> ret = Lists.newArrayList();

      for (Structure struct : structures) {
         StructureStart start = server.structureManager().getStructureAt(blockPosition, struct);

         for (StructurePiece piece : start.getPieces()) {
            BoundingBox bb = piece.getBoundingBox();
            if (bb.isInside(blockPosition)) {
               ret.add(this.structureMap.get(struct));
               break;
            }
         }
      }

      return ret;
   }

   @Override
   public float getDifficultyScale(AbstractLocation location) {
      BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      DifficultyInstance scaler = ((CraftWorld)location.getWorld()).getHandle().getCurrentDifficultyAt(pos);
      return scaler.getEffectiveDifficulty();
   }

   @Override
   public Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius, Predicate<AbstractEntity> predicate) {
      return this.getEntitiesNearLocation(location, radius, null, null, predicate);
   }

   @Override
   public Collection<AbstractEntity> getEntitiesNearLocation(
      AbstractLocation location, double radius, Double yMin, Double yMax, Predicate<AbstractEntity> predicate
   ) {
      List<AbstractEntity> entities = new ObjectArrayList();
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(location.getWorld());
      ServerLevel nmsWorld = bukkitWorld.getHandle();
      double rawX = location.getX();
      double rawZ = location.getZ();
      if (!Double.isInfinite(rawX) && !Double.isInfinite(rawZ) && !Double.isNaN(rawX) && !Double.isNaN(rawZ)) {
         double boundedYMin;
         double boundedYMax;
         if (yMin != null && yMax != null && Double.isFinite(yMin) && Double.isFinite(yMax) && yMin <= yMax) {
            boundedYMin = Math.max((double)bukkitWorld.getMinHeight(), Math.min(yMin, (double)bukkitWorld.getMaxHeight()));
            boundedYMax = Math.max((double)bukkitWorld.getMinHeight(), Math.min(yMax, (double)bukkitWorld.getMaxHeight()));
         } else {
            boundedYMin = bukkitWorld.getMinHeight();
            boundedYMax = bukkitWorld.getMaxHeight();
         }

         AABB aabb = new AABB(rawX - radius, boundedYMin, rawZ - radius, rawX + radius, boundedYMax, rawZ + radius);
         LevelEntityGetter<Entity> entityGetter = getEntityGetter(nmsWorld);
         if (entityGetter == null) {
            return Collections.emptyList();
         } else {
            entityGetter.get(aabb, nmsEntity -> {
               AbstractEntity entity = BukkitAdapter.adapt(nmsEntity.getBukkitEntity());
               if (predicate == null || predicate.test(entity)) {
                  entities.add(entity);
               }
            });
            return entities;
         }
      } else {
         Log.warn("getEntitiesNearLocation: invalid location (\u221e/NaN) \u2192 X={0}, Z={1}", rawX, rawZ);
         return Collections.emptyList();
      }
   }

   @Override
   public RayTraceResult rayTraceBlock(AbstractLocation start, AbstractLocation end, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
      World world = BukkitAdapter.adapt(start).getWorld();
      if (world == null || !start.getWorld().equals(end.getWorld())) {
         return null;
      } else if (Double.isFinite(start.getX())
         && Double.isFinite(start.getY())
         && Double.isFinite(start.getZ())
         && Double.isFinite(end.getX())
         && Double.isFinite(end.getY())
         && Double.isFinite(end.getZ())) {
         double dx = end.getX() - start.getX();
         double dy = end.getY() - start.getY();
         double dz = end.getZ() - start.getZ();
         double dsq = dx * dx + dy * dy + dz * dz;
         if (dsq > 1.0E10) {
            Log.warn(
               "rayTraceBlock: ray too long to trace safely ({0} blocks) \u2192 start=({1},{2},{3}) end=({4},{5},{6})",
               Math.sqrt(dsq),
               start.getX(),
               start.getY(),
               start.getZ(),
               end.getX(),
               end.getY(),
               end.getZ()
            );
            return null;
         } else {
            ServerLevel level = ((CraftWorld)world).getHandle();
            Vec3 startPos = new Vec3(start.getX(), start.getY(), start.getZ());
            Vec3 endPos = new Vec3(end.getX(), end.getY(), end.getZ());
            Fluid var10000;
            if (fluidCollisionMode == null) {
               var10000 = null;
            } else {
               switch (fluidCollisionMode) {
                  case ALWAYS:
                     var10000 = Fluid.ANY;
                     break;
                  case SOURCE_ONLY:
                     var10000 = Fluid.SOURCE_ONLY;
                     break;
                  case NEVER:
                     var10000 = Fluid.NONE;
                     break;
                  default:
                     var10000 = null;
               }
            }

            Fluid nmsFluidCollisionMode = var10000;
            HitResult nmsHitResult = level.clip(
               new ClipContext(startPos, endPos, ignorePassableBlocks ? Block.COLLIDER : Block.OUTLINE, nmsFluidCollisionMode, (Entity)null)
            );
            if (nmsHitResult != null && nmsHitResult.getType() != Type.MISS) {
               Vec3 nmsHitPos = nmsHitResult.getLocation();
               Vector hitPosition = new Vector(nmsHitPos.x, nmsHitPos.y, nmsHitPos.z);
               BlockFace hitBlockFace = null;
               if (nmsHitResult.getType() == Type.ENTITY) {
                  org.bukkit.entity.Entity hitEntity = ((EntityHitResult)nmsHitResult).getEntity().getBukkitEntity();
                  return new RayTraceResult(hitPosition, hitEntity, (BlockFace)null);
               } else {
                  org.bukkit.block.Block hitBlock = null;
                  BlockPos nmsBlockPos = null;
                  if (nmsHitResult.getType() == Type.BLOCK) {
                     BlockHitResult blockHitResult = (BlockHitResult)nmsHitResult;
                     hitBlockFace = CraftBlock.notchToBlockFace(blockHitResult.getDirection());
                     nmsBlockPos = blockHitResult.getBlockPos();
                  }

                  if (nmsBlockPos != null && world != null) {
                     hitBlock = world.getBlockAt(nmsBlockPos.getX(), nmsBlockPos.getY(), nmsBlockPos.getZ());
                  }

                  return new RayTraceResult(hitPosition, hitBlock, hitBlockFace);
               }
            } else {
               return null;
            }
         }
      } else {
         Log.warn(
            "rayTraceBlock: invalid coordinates (\u221e/NaN) \u2192 start=({0},{1},{2}) end=({3},{4},{5})",
            start.getX(),
            start.getY(),
            start.getZ(),
            end.getX(),
            end.getY(),
            end.getZ()
         );
         return null;
      }
   }

   public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<org.bukkit.entity.Entity> filter) {
      Validate.notNull(start, "Start location is null!");
      start.checkFinite();
      Validate.notNull(direction, "Direction is null!");
      direction.checkFinite();
      Validate.isTrue(direction.lengthSquared() > 0.0, "Direction's magnitude is 0!");
      if (maxDistance < 0.0) {
         return null;
      } else {
         Vector startPos = start.toVector();
         Vector dir = direction.clone().normalize().multiply(maxDistance);
         org.bukkit.util.BoundingBox aabb = org.bukkit.util.BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
         Collection<org.bukkit.entity.Entity> entities = start.getWorld().getNearbyEntities(aabb, filter);
         org.bukkit.entity.Entity nearestHitEntity = null;
         RayTraceResult nearestHitResult = null;
         double nearestDistanceSq = Double.MAX_VALUE;

         for (org.bukkit.entity.Entity entity : entities) {
            org.bukkit.util.BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
            RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);
            if (hitResult != null) {
               double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());
               if (distanceSq < nearestDistanceSq) {
                  nearestHitEntity = entity;
                  nearestHitResult = hitResult;
                  nearestDistanceSq = distanceSq;
               }
            }
         }

         return nearestHitEntity == null ? null : new RayTraceResult(nearestHitResult.getHitPosition(), nearestHitEntity, nearestHitResult.getHitBlockFace());
      }
   }

   @Override
   public RayTraceResult rayTrace(
      Location start,
      Vector direction,
      double maxDistance,
      FluidCollisionMode fluidCollisionMode,
      double raySize,
      Predicate<org.bukkit.entity.Entity> entityFilter,
      Predicate<Material> blockFilter
   ) {
      if (!(direction.lengthSquared() < 1.0E-5) && !(maxDistance <= 1.0E-5)) {
         RayTraceResult blockRayTrace = null;
         RayTraceResult entityRayTrace = start.getWorld().rayTraceEntities(start, direction, maxDistance, raySize, entityFilter);
         double distance;
         if (entityRayTrace != null && entityRayTrace.getHitEntity() != null) {
            distance = start.distance(entityRayTrace.getHitEntity().getLocation());
            if (distance == 0.0) {
               distance = maxDistance;
            }
         } else {
            distance = maxDistance;
         }

         BlockIterator bIterator = new BlockIterator(start.getWorld(), start.toVector(), direction, 0.0, (int)Math.ceil(distance));
         org.bukkit.block.Block block = null;

         while (bIterator.hasNext()) {
            block = bIterator.next();
            if (!block.isEmpty() && !blockFilter.test(block.getType())) {
               RayTraceResult res = block.rayTrace(start, direction, distance, fluidCollisionMode);
               if (res != null) {
                  blockRayTrace = res;
                  break;
               }
            }
         }

         if (entityRayTrace != null && entityRayTrace.getHitEntity() != null) {
            return blockRayTrace != null ? blockRayTrace : entityRayTrace;
         } else if (blockRayTrace != null) {
            return blockRayTrace;
         } else {
            return entityRayTrace != null
               ? new RayTraceResult(entityRayTrace.getHitPosition())
               : new RayTraceResult(start.toVector().add(direction.clone().normalize().multiply(maxDistance)));
         }
      } else {
         return null;
      }
   }

   @Override
   public org.bukkit.entity.Entity spawnInvisibleArmorStand(Location location) {
      ServerLevel w = ((CraftWorld)location.getWorld()).getHandle();
      ArmorStand nmsEntity = new ArmorStand(w, location.getX(), location.getY(), location.getZ());
      nmsEntity.absSnapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      nmsEntity.setInvisible(true);
      w.addFreshEntity(nmsEntity);
      return nmsEntity.getBukkitEntity();
   }

   @Override
   public PacketArmorStand.PacketArmorStandEntityRenderer createVirtualArmorStandRenderer(PacketArmorStand wrapper) {
      return new VirtualArmorStand(wrapper);
   }

   @Override
   public PacketFallingBlock.PacketFallingBlockEntityRenderer createVirtualBlockRenderer(PacketFallingBlock wrapper) {
      return new VirtualFallingBlock(wrapper);
   }

   @Override
   public PacketItem.PacketItemEntityRenderer createVirtualItemRenderer(PacketItem wrapper) {
      return new VirtualItem(wrapper);
   }

   @Override
   public PacketBlockDisplay.PacketBlockDisplayEntityRenderer createVirtualDisplayBlockEntityRenderer(PacketBlockDisplay wrapper) {
      return new VirtualDisplayBlock(wrapper);
   }

   @Override
   public PacketItemDisplay.PacketItemDisplayEntityRenderer createVirtualDisplayItemEntityRenderer(PacketItemDisplay wrapper) {
      return new VirtualDisplayItem(wrapper);
   }

   @Override
   public PacketTextDisplay.PacketTextDisplayEntityRenderer createVirtualTextDisplayEntityRenderer(PacketTextDisplay wrapper) {
      return new VirtualTextDisplay(wrapper);
   }

   static {
      for (Method method : ServerLevel.class.getMethods()) {
         if (LevelEntityGetter.class.isAssignableFrom(method.getReturnType()) && method.getReturnType() != LevelEntityGetter.class) {
            entityGetter = method;
            break;
         }
      }
   }
}
