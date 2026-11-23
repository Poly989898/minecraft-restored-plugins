package io.lumine.mythic.core.volatilecode.v1_21_R5;

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
import org.apache.commons.lang3.Validate;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualArmorStand;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualDisplayBlock;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualDisplayItem;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualFallingBlock;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualItem;
import io.lumine.mythic.core.volatilecode.v1_21_R5.virtual.VirtualTextDisplay;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.RayTrace.BlockCollisionOption;
import net.minecraft.world.level.RayTrace.FluidCollisionOption;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_21_R5.CraftChunk;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlock;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class VolatileWorldHandlerImpl implements VolatileWorldHandler {
   private static Method entityGetter;
   private static final org.bukkit.entity.Entity[] NO_ENTITIES = new org.bukkit.entity.Entity[0];
   private final Map<BiomeBase, AbstractBiome> biomeMap = Maps.newConcurrentMap();
   private final Map<String, AbstractBiome> biomeLookup = Maps.newConcurrentMap();
   private final Map<Structure, AbstractStructure> structureMap = Maps.newConcurrentMap();
   private final Map<String, AbstractStructure> structureLookup = Maps.newConcurrentMap();

   public VolatileWorldHandlerImpl(VolatileCodeHandler handler) {
      CraftServer craftServer = (CraftServer)Bukkit.getServer();
      Optional<IRegistry<BiomeBase>> maybeBiomeRegistry = craftServer.getHandle().b().ba().a(Registries.aK);
      if (maybeBiomeRegistry.isEmpty()) {
         MythicLogger.error("BiomeRegistry not found");
      } else {
         IRegistry<BiomeBase> biomeRegistry = maybeBiomeRegistry.get();
         biomeRegistry.k().forEach(entry -> {
            String key = ((ResourceKey)entry.getKey()).a().toString();
            int id = biomeRegistry.a((BiomeBase)entry.getValue());
            BiomeBase biome = (BiomeBase)entry.getValue();
            VolatileBiomeImpl volatileBiome = new VolatileBiomeImpl(this, id, key, biomeRegistry.b((ResourceKey)entry.getKey()));
            this.biomeMap.put(biome, volatileBiome);
            this.biomeLookup.put(key, volatileBiome);
         });
         Optional<IRegistry<Structure>> maybeStructureRegistry = craftServer.getHandle().b().ba().a(Registries.bj);
         if (maybeStructureRegistry.isEmpty()) {
            MythicLogger.error("StructureRegistry not found");
         } else {
            IRegistry<Structure> structureRegistry = maybeStructureRegistry.get();
            structureRegistry.k().forEach(entry -> {
               String key = ((ResourceKey)entry.getKey()).a().toString();
               int id = structureRegistry.a((Structure)entry.getValue());
               Structure structure = (Structure)entry.getValue();
               VolatileStructureImpl volatileStructure = new VolatileStructureImpl(this, id, key, structureRegistry.b((ResourceKey)entry.getKey()), structure);
               this.structureMap.put(structure, volatileStructure);
               this.structureLookup.put(key, volatileStructure);
            });
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
      WorldServer nmsWorld = bukkitWorld.getHandle();
      return null != nmsWorld.getChunkIfLoaded(x, z);
   }

   public static boolean hasAsyncEntityGetter() {
      return entityGetter != null;
   }

   public static LevelEntityGetter<Entity> getEntityGetter(WorldServer level) {
      if (entityGetter == null) {
         return level.I();
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
      WorldServer nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractEntity> ret = Lists.newArrayList();

      for (Entity entity : getEntityGetter(nmsWorld).a()) {
         ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
      }

      return ret;
   }

   @Override
   public Collection<AbstractEntity> getLivingEntities(AbstractWorld world) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      WorldServer nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractEntity> ret = Lists.newArrayList();

      for (Entity entity : getEntityGetter(nmsWorld).a()) {
         if (entity instanceof EntityLiving) {
            ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
         }
      }

      return ret;
   }

   @Override
   public Collection<AbstractPlayer> getPlayers(AbstractWorld world) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      WorldServer nmsWorld = bukkitWorld.getHandle();
      Collection<AbstractPlayer> ret = Lists.newArrayList();

      for (EntityPlayer entity : nmsWorld.A()) {
         ret.add(BukkitAdapter.adapt(entity.getBukkitEntity()));
      }

      return ret;
   }

   @Override
   public int getEntitiesInChunk(AbstractWorld world, int x, int z) {
      CraftWorld bukkitWorld = (CraftWorld)BukkitAdapter.adapt(world);
      WorldServer nmsWorld = bukkitWorld.getHandle();
      Chunk chunk = nmsWorld.getChunkIfLoaded(x, z);
      if (chunk == null) {
         return 0;
      } else {
         CraftChunk craftChunk = new CraftChunk(chunk);
         return craftChunk.getEntities().length;
      }
   }

   @Override
   public org.bukkit.entity.Entity[] getLoadedChunkEntities(AbstractWorld world, int x, int z) {
      WorldServer nmsWorld = ((CraftWorld)BukkitAdapter.adapt(world)).getHandle();
      ChunkEntitySlices slices = nmsWorld.moonrise$getEntityLookup().getChunk(x, z);
      return slices != null && !slices.isEmpty() && nmsWorld.n().isChunkLoaded(x, z) ? nmsWorld.getChunkEntities(x, z) : NO_ENTITIES;
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
      BlockPosition blockPosition = new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      WorldServer server = bukkitWorld.getHandle();
      BiomeBase biome = (BiomeBase)server.I_().a(blockPosition).a();
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
      BlockPosition blockPosition = new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ());
      CraftWorld bukkitWorld = (CraftWorld)location.getWorld();
      WorldServer server = bukkitWorld.getHandle();
      Set<Structure> structures = server.b().b(blockPosition).keySet();
      List<AbstractStructure> ret = Lists.newArrayList();

      for (Structure struct : structures) {
         StructureStart start = server.b().a(blockPosition, struct);

         for (StructurePiece piece : start.i()) {
            StructureBoundingBox bb = piece.f();
            if (bb.b(blockPosition)) {
               ret.add(this.structureMap.get(struct));
               break;
            }
         }
      }

      return ret;
   }

   @Override
   public float getDifficultyScale(AbstractLocation location) {
      BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      DifficultyDamageScaler scaler = ((CraftWorld)location.getWorld()).getHandle().d_(pos);
      return scaler.b();
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
      WorldServer nmsWorld = bukkitWorld.getHandle();
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

         AxisAlignedBB aabb = new AxisAlignedBB(rawX - radius, boundedYMin, rawZ - radius, rawX + radius, boundedYMax, rawZ + radius);
         LevelEntityGetter<Entity> entityGetter = getEntityGetter(nmsWorld);
         if (entityGetter == null) {
            return Collections.emptyList();
         } else {
            entityGetter.a(aabb, nmsEntity -> {
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
            WorldServer level = ((CraftWorld)world).getHandle();
            Vec3D startPos = new Vec3D(start.getX(), start.getY(), start.getZ());
            Vec3D endPos = new Vec3D(end.getX(), end.getY(), end.getZ());
            FluidCollisionOption var10000;
            if (fluidCollisionMode == null) {
               var10000 = null;
            } else {
               switch (fluidCollisionMode) {
                  case ALWAYS:
                     var10000 = FluidCollisionOption.c;
                     break;
                  case SOURCE_ONLY:
                     var10000 = FluidCollisionOption.b;
                     break;
                  case NEVER:
                     var10000 = FluidCollisionOption.a;
                     break;
                  default:
                     var10000 = null;
               }
            }

            FluidCollisionOption nmsFluidCollisionMode = var10000;
            MovingObjectPosition nmsHitResult = level.a(
               new RayTrace(startPos, endPos, ignorePassableBlocks ? BlockCollisionOption.a : BlockCollisionOption.b, nmsFluidCollisionMode, (Entity)null)
            );
            if (nmsHitResult != null && nmsHitResult.d() != EnumMovingObjectType.a) {
               Vec3D nmsHitPos = nmsHitResult.g();
               Vector hitPosition = new Vector(nmsHitPos.d, nmsHitPos.e, nmsHitPos.f);
               BlockFace hitBlockFace = null;
               if (nmsHitResult.d() == EnumMovingObjectType.c) {
                  org.bukkit.entity.Entity hitEntity = ((MovingObjectPositionEntity)nmsHitResult).a().getBukkitEntity();
                  return new RayTraceResult(hitPosition, hitEntity, (BlockFace)null);
               } else {
                  Block hitBlock = null;
                  BlockPosition nmsBlockPos = null;
                  if (nmsHitResult.d() == EnumMovingObjectType.b) {
                     MovingObjectPositionBlock blockHitResult = (MovingObjectPositionBlock)nmsHitResult;
                     hitBlockFace = CraftBlock.notchToBlockFace(blockHitResult.c());
                     nmsBlockPos = blockHitResult.b();
                  }

                  if (nmsBlockPos != null && world != null) {
                     hitBlock = world.getBlockAt(nmsBlockPos.u(), nmsBlockPos.v(), nmsBlockPos.w());
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
         BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
         Collection<org.bukkit.entity.Entity> entities = start.getWorld().getNearbyEntities(aabb, filter);
         org.bukkit.entity.Entity nearestHitEntity = null;
         RayTraceResult nearestHitResult = null;
         double nearestDistanceSq = Double.MAX_VALUE;

         for (org.bukkit.entity.Entity entity : entities) {
            BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
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
         Block block = null;

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
      WorldServer w = ((CraftWorld)location.getWorld()).getHandle();
      EntityArmorStand nmsEntity = new EntityArmorStand(w, location.getX(), location.getY(), location.getZ());
      nmsEntity.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      nmsEntity.l(true);
      w.b(nmsEntity);
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
      for (Method method : WorldServer.class.getMethods()) {
         if (LevelEntityGetter.class.isAssignableFrom(method.getReturnType()) && method.getReturnType() != LevelEntityGetter.class) {
            entityGetter = method;
            break;
         }
      }
   }
}
