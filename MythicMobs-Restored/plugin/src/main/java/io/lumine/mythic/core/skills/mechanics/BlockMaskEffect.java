package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

@MythicMechanic(
   author = "Ashijin",
   name = "blockMask",
   aliases = {"effect:blockMask", "e:blockMask"},
   description = "Temporarily masks a block as a different block"
)
public class BlockMaskEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private static final AtomicLong MASK_ID = new AtomicLong();
   private static final Map<BlockMaskEffect.MaskOwner, Long2LongOpenHashMap> ACTIVE_MASKS = new ConcurrentHashMap<>();
   @MythicField(
      name = "block",
      aliases = {"b", "material", "mat", "m"},
      description = "The block used for the blockmask. Supports blockdata string",
      defValue = "GRAVEL"
   )
   private final PlaceholderString strMaterial;
   private AbstractBlock block;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      description = "The radius of the blockmask effect",
      defValue = "0"
   )
   private final PlaceholderInt radius;
   @MythicField(
      name = "radiusy",
      aliases = {"ry"},
      description = "The radiusy of the blockmask effect",
      defValue = "radius"
   )
   private final PlaceholderInt radiusY;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration of the blockmask effect",
      defValue = "0"
   )
   private final PlaceholderDuration duration;
   @MythicField(
      name = "noise",
      aliases = {"n"},
      description = "The noise/randomness of the blockmask effect",
      defValue = "0"
   )
   private final PlaceholderDouble randomness;
   @MythicField(
      name = "shape",
      aliases = {"s"},
      description = "The shape of the blockmask effect - SPHERE/CUBE.",
      defValue = "SPHERE"
   )
   private final boolean sphere;
   @MythicField(
      name = "occluding",
      aliases = {"o", "oc", "occ"},
      description = "Occlusion filter for onlyair=true: true also masks non-occluding/semi-transparent blocks (glass, stairs, slabs), false masks only air. Has no effect when noair=true (all non-air blocks are masked).",
      defValue = "true"
   )
   private final PlaceholderBoolean occluding;
   private final SkillAudience audience;
   private BlockMaskEffect.MaskType maskType;

   public BlockMaskEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.strMaterial = mlc.getPlaceholderString(new String[]{"material", "mat", "m", "block", "b"}, "GRAVEL");
      if (this.strMaterial.isStatic()) {
         this.getPlugin().getClock().queuePostReload(() -> {
            AbstractBlock block = this.getPlugin().getBootstrap().getBlock(this.strMaterial.get());
            if (block instanceof AbstractBlock.InvalidBlock) {
               MythicLogger.errorMechanicConfig(this, mlc, "'" + this.strMaterial.get() + "' is not a valid block type.");
            } else {
               this.block = block;
            }
         });
      } else {
         this.block = null;
      }

      this.audience = mlc.getAudience(new String[]{"audience"}, "nearby");
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 0);
      this.radiusY = mlc.getPlaceholderInteger(new String[]{"radiusy", "ry"}, this.radius);
      this.randomness = mlc.getPlaceholderDouble(new String[]{"noise", "n"}, 0.0);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
      this.occluding = mlc.getPlaceholderBoolean(new String[]{"o", "oc", "occ"}, true);
      String shape = mlc.getString(new String[]{"shape", "s"}, "SPHERE").toUpperCase();
      this.sphere = shape.equals("SPHERE");
      boolean noAir = mlc.getBoolean(new String[]{"noair", "na"}, true);
      boolean onlyAir = mlc.getBoolean(new String[]{"onlyair", "oa"}, false);
      if (noAir && !onlyAir) {
         this.maskType = BlockMaskEffect.MaskType.IGNORE_AIR;
      } else if (onlyAir) {
         this.maskType = BlockMaskEffect.MaskType.ONLY_AIR;
      } else {
         this.maskType = BlockMaskEffect.MaskType.ALL;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target, target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, target);
      return SkillResult.SUCCESS;
   }

   public void playEffect(SkillMetadata data, AbstractLocation location) {
      this.playEffect(data, null, location);
   }

   public void playEffect(SkillMetadata data, AbstractEntity entity, AbstractLocation location) {
      AbstractBlock blockMask;
      if (this.block == null) {
         String type = entity == null ? this.strMaterial.get(data) : this.strMaterial.get(data, entity);
         AbstractBlock block = this.getPlugin().getBootstrap().getBlock(type);
         if (block instanceof AbstractBlock.InvalidBlock) {
            MythicLogger.errorMechanicConfig(this, this.getConfig(), "'" + type + "' is not a valid block type.");
            return;
         }

         blockMask = block;
      } else {
         if (this.block instanceof AbstractBlock.InvalidBlock) {
            String type = entity == null ? this.strMaterial.get(data) : this.strMaterial.get(data, entity);
            MythicLogger.errorMechanicConfig(this, this.getConfig(), "'" + type + "' is not a valid block type.");
            return;
         }

         blockMask = this.block;
      }

      int duration = entity == null ? this.duration.get(data).getTicks() : this.duration.get(data, entity).getTicks();
      int radius = entity == null ? this.radius.get(data) : this.radius.get(data, entity);
      int radiusY = entity == null ? this.radiusY.get(data) : this.radiusY.get(data, entity);
      Location baseLocation = BukkitAdapter.adapt(location);
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, entity);
      Map<AbstractLocation, AbstractBlock> blocksToMask = Maps.newHashMap();
      if (radius == 0) {
         if (this.shouldMask(baseLocation.getBlock().getType())) {
            blocksToMask.put(location, blockMask);
         }
      } else {
         for (AbstractLocation maskLocation : this.getBlocksInRadius(baseLocation, true, radius, radiusY, this.randomness.get(data))) {
            blocksToMask.put(maskLocation, blockMask);
         }
      }

      if (!blocksToMask.isEmpty()) {
         if (duration > 0) {
            Map<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> maskedLocations = groupByArea(blocksToMask.keySet());
            long maskId = registerActiveMasks(audience, maskedLocations);
            this.getPlugin().getVolatileCodeHandler().getBlockHandler().sendMultiBlockChange(audience, blocksToMask);
            Schedulers.of(data.getCaster().getEntity().getBukkitEntity()).runLater(() -> {
               Map<Map<AbstractLocation, AbstractBlock>, List<AbstractPlayer>> unmaskChanges = collectOwnedUnmaskChanges(audience, maskedLocations, maskId);
               Location lll = BukkitAdapter.adapt(location);
               if (lll.getChunk().isLoaded()) {
                  for (Entry<Map<AbstractLocation, AbstractBlock>, List<AbstractPlayer>> entry : unmaskChanges.entrySet()) {
                     this.getPlugin().getVolatileCodeHandler().getBlockHandler().sendMultiBlockChange(entry.getValue(), entry.getKey());
                  }
               }
            }, duration);
         } else {
            this.getPlugin().getVolatileCodeHandler().getBlockHandler().sendMultiBlockChange(audience, blocksToMask);
         }
      }
   }

   static Map<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> groupByArea(Collection<AbstractLocation> locations) {
      Map<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> groups = new LinkedHashMap<>();
      BlockMaskEffect.MaskArea lastArea = null;
      BlockMaskEffect.MaskGroup lastGroup = null;

      for (AbstractLocation location : locations) {
         AbstractWorld world = location.getWorld();
         UUID worldId = world == null ? null : world.getUniqueId();
         int x = location.getBlockX() + 33554432;
         int y = location.getBlockY() + 2048;
         int z = location.getBlockZ() + 33554432;
         int overflow = x >>> 26 << 26 | z >>> 26 << 20 | y >>> 12;
         if (lastArea == null || lastArea.overflow() != overflow || !Objects.equals(lastArea.worldId(), worldId)) {
            lastArea = new BlockMaskEffect.MaskArea(worldId, overflow);
            lastGroup = groups.computeIfAbsent(lastArea, a -> new BlockMaskEffect.MaskGroup(new LongArrayList(), new ArrayList<>()));
         }

         lastGroup.keys().add((long)(x & 67108863) << 38 | (long)(z & 67108863) << 12 | y & 4095);
         lastGroup.locations().add(location);
      }

      return groups;
   }

   static long registerActiveMasks(Collection<AbstractPlayer> audience, Map<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> groups) {
      long maskId = MASK_ID.incrementAndGet();

      for (AbstractPlayer player : audience) {
         UUID playerId = player.getUniqueId();

         for (Entry<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> entry : groups.entrySet()) {
            LongArrayList keys = entry.getValue().keys();
            ACTIVE_MASKS.compute(new BlockMaskEffect.MaskOwner(playerId, entry.getKey()), (owner, masks) -> {
               if (masks == null) {
                  masks = new Long2LongOpenHashMap(keys.size());
               }

               for (int i = 0; i < keys.size(); i++) {
                  masks.put(keys.getLong(i), maskId);
               }

               return (Long2LongOpenHashMap)masks;
            });
         }
      }

      return maskId;
   }

   static Map<Map<AbstractLocation, AbstractBlock>, List<AbstractPlayer>> collectOwnedUnmaskChanges(
      Collection<AbstractPlayer> audience, Map<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> groups, long maskId
   ) {
      Map<Map<AbstractLocation, AbstractBlock>, List<AbstractPlayer>> unmaskChanges = new IdentityHashMap<>();

      for (Entry<BlockMaskEffect.MaskArea, BlockMaskEffect.MaskGroup> entry : groups.entrySet()) {
         BlockMaskEffect.MaskGroup group = entry.getValue();
         Map<AbstractLocation, AbstractBlock> allChanges = Maps.newHashMap();

         for (AbstractPlayer player : audience) {
            ACTIVE_MASKS.computeIfPresent(new BlockMaskEffect.MaskOwner(player.getUniqueId(), entry.getKey()), (owner, masks) -> {
               Map<AbstractLocation, AbstractBlock> owned = releaseOwned(masks, group, allChanges, maskId);
               if (owned != null) {
                  unmaskChanges.computeIfAbsent(owned, c -> new ArrayList<>()).add(player);
               }

               return (Long2LongOpenHashMap)(masks.isEmpty() ? null : masks);
            });
         }
      }

      return unmaskChanges;
   }

   private static Map<AbstractLocation, AbstractBlock> releaseOwned(
      Long2LongOpenHashMap masks, BlockMaskEffect.MaskGroup group, Map<AbstractLocation, AbstractBlock> allChanges, long maskId
   ) {
      LongArrayList keys = group.keys();
      List<AbstractLocation> locations = group.locations();
      Map<AbstractLocation, AbstractBlock> partial = null;
      int owned = 0;

      for (int i = 0; i < keys.size(); i++) {
         if (masks.remove(keys.getLong(i), maskId)) {
            owned++;
            if (partial != null) {
               partial.put(locations.get(i), null);
            }
         } else if (partial == null) {
            partial = Maps.newHashMap();

            for (int j = 0; j < i; j++) {
               partial.put(locations.get(j), null);
            }
         }
      }

      if (owned == 0) {
         return null;
      } else if (partial != null) {
         return partial;
      } else {
         if (allChanges.isEmpty()) {
            for (AbstractLocation location : locations) {
               allChanges.put(location, null);
            }
         }

         return allChanges;
      }
   }

   private List<AbstractLocation> getBlocksInRadius(Location l, boolean randomize, int radius, int radiusY, double randomness) {
      List<AbstractLocation> blocks = Lists.newArrayList();
      if (!l.getChunk().isLoaded()) {
         return blocks;
      } else {
         World world = l.getWorld();
         AbstractWorld maskWorld = BukkitAdapter.adapt(world);
         double radiusSq = Math.pow(radius, 2.0);

         for (int x = -radius; x <= radius; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
               for (int z = -radius; z <= radius; z++) {
                  double newX = l.getX() + x;
                  double newY = l.getY() + y;
                  double newZ = l.getZ() + z;
                  if (this.sphere) {
                     double dx = l.getX() - newX;
                     double dy = l.getY() - newY;
                     double dz = l.getZ() - newZ;
                     if (dx * dx + dy * dy + dz * dz > radiusSq) {
                        continue;
                     }
                  }

                  if ((!randomize || !(randomness > 0.0) || !(randomness < Numbers.randomDouble()))
                     && this.shouldMask(world.getBlockAt(Location.locToBlock(newX), Location.locToBlock(newY), Location.locToBlock(newZ)).getType())) {
                     blocks.add(new AbstractLocation(maskWorld, newX, newY, newZ));
                  }
               }
            }
         }

         return blocks;
      }
   }

   private boolean shouldMask(Material type) {
      if (this.maskType == BlockMaskEffect.MaskType.ALL) {
         return true;
      } else if (this.maskType == BlockMaskEffect.MaskType.IGNORE_AIR) {
         return !type.isAir();
      } else if (this.maskType == BlockMaskEffect.MaskType.ONLY_AIR) {
         return this.occluding.get() ? !type.isOccluding() : type.isAir();
      } else {
         return false;
      }
   }

   record MaskArea(UUID worldId, int overflow) {
   }

   record MaskGroup(LongArrayList keys, List<AbstractLocation> locations) {
   }

   private record MaskOwner(UUID playerId, BlockMaskEffect.MaskArea area) {
   }

   private static enum MaskType {
      ALL,
      IGNORE_AIR,
      ONLY_AIR;
   }
}
