package com.nexomc.nexo.utils;

import com.nexomc.customblockdata.CustomBlockData;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\bJ.\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0007b\u0002\b\bJ:\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0007b\u0002\b\bJ\u0014\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010H\u0007b\u0002\b\bJ \u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0005H\u0007b\u0002\b\bJ \u0010%\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u00052\n\b\u0002\u0010&\u001a\u0004\u0018\u00010'H\u0007b\u0002\b\bJ(\u0010%\u001a\u00020\u001a2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010'H\u0007b\u0002\b\bJ\u0014\u0010%\u001a\u00020\u001a2\u0006\u0010,\u001a\u00020#H\u0007b\u0002\b\bJ\"\u0010-\u001a\u00020\u001a2\b\u0010.\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0007b\u0002\b\bR2\u0010\t\u001a&\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b \f*\u0012\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b\u0018\u00010\n0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0015\u0010\u001e\u001a\u00020\u001f*\u00020\u00058F¢\u0006\u0006\u001a\u0004\b \u0010!R\u001b\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\n8\u0006X\u0087\u0004\u0092\u0002\u0002\b$¢\u0006\u0002\n\u0000R\u0015\u0010/\u001a\u00020\u001a*\u00020\u00108F¢\u0006\u0006\u001a\u0004\b/\u00100¨\u00061"},
   d2 = {"Lcom/nexomc/nexo/utils/BlockHelpers;", "", "<init>", "()V", "entityStandingOn", "Lorg/bukkit/block/Block;", "entity", "Lorg/bukkit/entity/Entity;", "Lkotlin/jvm/JvmStatic;", "blockFaces", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lorg/bukkit/block/BlockFace;", "kotlin.jvm.PlatformType", "playCustomBlockSound", "", "location", "Lorg/bukkit/Location;", "sound", "", "volume", "", "pitch", "category", "Lorg/bukkit/SoundCategory;", "toCenterBlockLocation", "isStandingInside", "", "player", "Lorg/bukkit/entity/Player;", "block", "persistentDataContainer", "Lorg/bukkit/persistence/PersistentDataContainer;", "getPersistentDataContainer", "(Lorg/bukkit/block/Block;)Lorg/bukkit/persistence/PersistentDataContainer;", "REPLACEABLE_BLOCKS", "Lorg/bukkit/Material;", "Lkotlin/jvm/JvmField;", "isReplaceable", "excludeUUID", "Ljava/util/UUID;", "position", "Lio/papermc/paper/math/Position;", "world", "Lorg/bukkit/World;", "material", "isInteractable", "placedAgainst", "isLoaded", "(Lorg/bukkit/Location;)Z", "com.nexomc:core"}
)
public final class BlockHelpers {
   @NotNull
   public static final BlockHelpers INSTANCE = new BlockHelpers();
   private static final ObjectOpenHashSet<BlockFace> blockFaces;
   @JvmField
   @NotNull
   public static final ObjectOpenHashSet<Material> REPLACEABLE_BLOCKS;

   private BlockHelpers() {
   }

   @JvmStatic
   @Nullable
   public static final Block entityStandingOn(@NotNull Entity entity) {
      Block it = entity.getLocation().getBlock();
      int value = 0;
      Pair pair = kotlin.TuplesKt.to(it, it.getRelative(BlockFace.DOWN));
      Object component1 = pair.component1();
      Block block = (Block)component1;
      component1 = pair.component2();
      it = (Block)component1;
      if (!block.getType().isAir() && block.getType() != Material.LIGHT) {
         return block;
      } else if (!it.getType().isAir()) {
         return it;
      } else {
         BoundingBox boundingBox = entity.getBoundingBox().expand(0.3);
         BoundingBox entityBox = boundingBox;
         ObjectOpenHashSet objectOpenHashSet = blockFaces;
         Iterator iterator = ((Iterable)objectOpenHashSet).iterator();

         Object obj;
         while(true) {
            if (!iterator.hasNext()) {
               obj = null;
               break;
            }

            BlockFace blockFace = (BlockFace)iterator.next();
            int value2 = 0;
            Block it_1 = it.getRelative(blockFace);
            int value3 = 0;
            Block block2 = it_1.getBoundingBox().overlaps(entityBox) && (it_1.getType() != Material.AIR || IFurniturePacketManager.Companion.blockIsHitbox(block, (UUID)null, true)) ? it_1 : null;
            if (block2 != null) {
               obj = block2;
               break;
            }
         }

         return (Block)obj;
      }
   }

   @JvmStatic
   public static final void playCustomBlockSound(@NotNull Location location, @Nullable String sound, float volume, float pitch) {
      playCustomBlockSound(location.toCenterLocation(), sound, SoundCategory.BLOCKS, volume, pitch);
   }

   @JvmStatic
   public static final void playCustomBlockSound(@Nullable Location location, @Nullable String sound, @Nullable SoundCategory category, float volume, float pitch) {
      if (sound != null && category != null && location != null && INSTANCE.isLoaded(location)) {
         location.getWorld().playSound(location, sound, category, volume, pitch);
      }
   }

   @JvmStatic
   @NotNull
   public static final Location toCenterBlockLocation(@NotNull Location location) {
      Location location2 = location.toCenterLocation().subtract((double)0.0F, (double)0.5F, (double)0.0F);
      return location2;
   }

   @JvmStatic
   public static final boolean isStandingInside(@Nullable Player player, @Nullable Block block) {
      if (player != null && block != null) {
         BoundingBox boundingBox = BoundingBox.of(block.getLocation().toCenterLocation(), (double)0.5F, (double)0.5F, (double)0.5F);
         BoundingBox blockBox = boundingBox;
         Collection nearbyEntities = block.getWorld().getNearbyEntities(blockBox);
         Iterable iterable = (Iterable)nearbyEntities;
         boolean flag;
         if (((Collection)iterable).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               Object element = iterator.next();
               Entity it = (Entity)element;
               int value = 0;
               if (it instanceof LivingEntity && (!(it instanceof Player) || ((Player)it).getGameMode() != GameMode.SPECTATOR)) {
                  flag = true;
                  break;
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   @NotNull
   public final PersistentDataContainer getPersistentDataContainer(@NotNull Block block) {
      return new CustomBlockData(block, (Plugin)NexoPlugin.Companion.instance());
   }

   @JvmStatic
   public static final boolean isReplaceable(@NotNull Block block, @Nullable UUID excludeUUID) {
      Material type = block.getType();
      boolean flag;
      if (type == Material.SNOW) {
         BlockData blockData = block.getBlockData();
         flag = ((Snow)blockData).getLayers() == 1;
      } else {
         flag = type == Material.SCULK_VEIN ? true : REPLACEABLE_BLOCKS.contains(type);
      }

      return flag && !IFurniturePacketManager.Companion.blockIsHitbox(block, excludeUUID, false);
   }

   @JvmStatic
   public static final boolean isReplaceable(@NotNull Position position, @NotNull World world, @Nullable UUID excludeUUID) {
      Block block2 = position.toLocation(world).getBlock();
      Block block = block2;
      return isReplaceable(block, excludeUUID);
   }

   @JvmStatic
   public static final boolean isReplaceable(@NotNull Material material) {
      return REPLACEABLE_BLOCKS.contains(material) || material == Material.SCULK_VEIN;
   }

   @JvmStatic
   public static final boolean isInteractable(@Nullable Block placedAgainst, @Nullable Player player) {
      if (placedAgainst == null) {
         return false;
      } else {
         CustomBlockMechanic blockMechanic = NexoBlocks.customBlockMechanic(placedAgainst);
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(placedAgainst);
         Material material = placedAgainst.getType();
         Material type = material;
         boolean flag;
         if (blockMechanic != null) {
            flag = blockMechanic.isInteractable(player, placedAgainst);
         } else if (furnitureMechanic != null) {
            flag = furnitureMechanic.isInteractable(player, placedAgainst);
         } else if (Tag.STAIRS.isTagged(type)) {
            flag = false;
         } else if (Tag.FENCES.isTagged(type)) {
            flag = false;
         } else if (Tag.DOORS.isTagged(type)) {
            flag = false;
         } else if (!type.isInteractable()) {
            flag = false;
         } else {
            switch (BlockHelpers.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
                  flag = false;
                  break;
               default:
                  flag = true;
            }
         }

         return flag;
      }
   }

   public final boolean isLoaded(@NotNull Location location) {
      return location.isWorldLoaded() && location.isChunkLoaded();
   }

   static {
      BlockFace[] blockFaces2 = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
      blockFaces = ObjectOpenHashSet.of(blockFaces2);
      REPLACEABLE_BLOCKS = new ObjectOpenHashSet((Collection)Tag.REPLACEABLE.getValues());
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.Material.values().length];
         try { a_0[org.bukkit.Material.PUMPKIN.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.MOVING_PISTON.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.REDSTONE_ORE.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.REDSTONE_WIRE.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.IRON_TRAPDOOR.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static boolean isReplaceable$default(org.bukkit.block.Block block, java.util.UUID uuid, int mask, Object marker) {
      if ((mask & 2) != 0) uuid = null;
      return isReplaceable(block, uuid);
   }
}
