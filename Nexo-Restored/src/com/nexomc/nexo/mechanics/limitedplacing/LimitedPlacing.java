package com.nexomc.nexo.mechanics.limitedplacing;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u000212B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J!\u0010#\u001a\r\u0012\t\u0012\u00070\f¢\u0006\u0002\b\r0\u000b2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bH\u0002J\u001c\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00130\u000b2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bH\u0002J/\u0010&\u001a\u001b\u0012\u0017\u0012\u0015\u0012\f\u0012\n \u0011*\u0004\u0018\u00010\f0\f0\u0010¢\u0006\u0002\b\r0\u000f2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bH\u0002J\u0016\u0010'\u001a\u00020\u00192\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+J\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bJ\u0012\u0010.\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00100\u000fJ\u000e\u0010/\u001a\u00020\u00192\u0006\u0010(\u001a\u00020)J\u0012\u00100\u001a\u0004\u0018\u00010\u00132\u0006\u0010(\u001a\u00020)H\u0002R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0019\u0010\n\u001a\r\u0012\t\u0012\u00070\f¢\u0006\u0002\b\r0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R'\u0010\u000e\u001a\u001b\u0012\u0017\u0012\u0015\u0012\f\u0012\n \u0011*\u0004\u0018\u00010\f0\f0\u0010¢\u0006\u0002\b\r0\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u001a\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\u001d\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u001c\u0010\u001e\u001a\r\u0012\t\u0012\u00070\u001f¢\u0006\u0002\b\r0\u000b¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\"\u001a\u00020\u00198F¢\u0006\u0006\u001a\u0004\b\"\u0010\u001b¨\u00063"},
   d2 = {"Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "type", "Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$LimitedPlacingType;", "getType", "()Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$LimitedPlacingType;", "blockTypes", "", "Lorg/bukkit/Material;", "Lorg/jetbrains/annotations/Nullable;", "blockTags", "", "Lorg/bukkit/Tag;", "kotlin.jvm.PlatformType", "nexoBlocks", "", "radiusLimitation", "Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$RadiusLimitation;", "getRadiusLimitation", "()Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$RadiusLimitation;", "anyRestrictions", "", "isFloor", "()Z", "isRoof", "isWall", "worlds", "Lorg/bukkit/World;", "getWorlds", "()Ljava/util/List;", "isRadiusLimited", "limitedBlockMaterials", "list", "limitedNexoBlocks", "limitedBlockTags", "isNotPlacableOn", "block", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "limitedBlocks", "limitedNexoBlockIds", "limitedTags", "checkLimited", "checkIfNexoItem", "RadiusLimitation", "LimitedPlacingType", "com.nexomc:core"}
)
public final class LimitedPlacing {
   @NotNull
   private final LimitedPlacingType type;
   @NotNull
   private final List<Material> blockTypes;
   @NotNull
   private final Set<Tag<Material>> blockTags;
   @NotNull
   private final List<String> nexoBlocks;
   @Nullable
   private final RadiusLimitation radiusLimitation;
   private final boolean anyRestrictions;
   private final boolean isFloor;
   private final boolean isRoof;
   private final boolean isWall;
   @NotNull
   private final List<World> worlds;

   public LimitedPlacing(@NotNull ConfigurationSection section) {
      super();
      String key = "type";
      LimitedPlacingType limitedPlacingType = EnumUtils.getEnum(LimitedPlacingType.class, NexoYamlKt.stringOrNull(section, key));
      if (limitedPlacingType == null) {
         limitedPlacingType = LimitedPlacing.LimitedPlacingType.DENY;
      }

      this.type = limitedPlacingType;
      List stringList = section.getStringList("block_types");
      this.blockTypes = this.limitedBlockMaterials(stringList);
      stringList = section.getStringList("block_tags");
      this.blockTags = this.limitedBlockTags(stringList);
      stringList = section.getStringList("nexo_blocks");
      this.nexoBlocks = this.limitedNexoBlocks(stringList);
      LimitedPlacing limitedPlacing = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "radius_limitation");
      RadiusLimitation radiusLimitation2;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         radiusLimitation2 = new RadiusLimitation(p0);
         limitedPlacing = this;
      } else {
         radiusLimitation2 = null;
      }

      this.radiusLimitation = radiusLimitation2;
      this.anyRestrictions = section.getBoolean("floor", section.getBoolean("roof", section.getBoolean("wall")));
      this.isFloor = section.getBoolean("floor", !this.anyRestrictions);
      this.isRoof = section.getBoolean("roof", !this.anyRestrictions);
      this.isWall = section.getBoolean("wall", !this.anyRestrictions);
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "world", (String)null);
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String p0 = (String)element;
         int value2 = 0;
         World world = Bukkit.getWorld(p0);
         if (world != null) {
            Object it = world;
            int value3 = 0;
            destination.add(it);
         }
      }

      this.worlds = (List)destination;
   }

   @NotNull
   public final LimitedPlacingType getType() {
      return this.type;
   }

   @Nullable
   public final RadiusLimitation getRadiusLimitation() {
      return this.radiusLimitation;
   }

   public final boolean isFloor() {
      return this.isFloor;
   }

   public final boolean isRoof() {
      return this.isRoof;
   }

   public final boolean isWall() {
      return this.isWall;
   }

   @NotNull
   public final List<World> getWorlds() {
      return this.worlds;
   }

   public final boolean isRadiusLimited() {
      return this.radiusLimitation != null && this.radiusLimitation.getRadius() != -1 && this.radiusLimitation.getAmount() != -1;
   }

   private final List<Material> limitedBlockMaterials(List<String> list) {
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String p0 = (String)element;
         int value2 = 0;
         Material material = Material.getMaterial(p0);
         if (material != null) {
            Object it = material;
            int value3 = 0;
            destination.add(it);
         }
      }

      return (List)destination;
   }

   private final List<String> limitedNexoBlocks(List<String> list) {
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         String e = (String)element;
         int value = 0;
         if (NexoBlocks.isCustomBlock(e) || NexoFurniture.isFurniture(e)) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   private final Set<Tag<Material>> limitedBlockTags(List<String> list) {
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String it = (String)element;
         int value2 = 0;
         Tag tag = Bukkit.getTag("blocks", NamespacedKey.minecraft(it), Material.class);
         if (tag != null) {
            Object obj = tag;
            int value3 = 0;
            destination.add(obj);
         }
      }

      return CollectionsKt.toSet((Iterable)((List)destination));
   }

   public final boolean isNotPlacableOn(@NotNull Block block, @NotNull BlockFace blockFace) {
      Block block2;
      if (BlockHelpers.isReplaceable(block, (UUID)null)) {
         block2 = block;
      } else {
         block2 = block.getRelative(blockFace);
      }

      Block placedBlock = block2;
      block2 = placedBlock.getRelative(BlockFace.DOWN);
      Block blockBelow = block2;
      block2 = placedBlock.getRelative(BlockFace.UP);
      Block blockAbove = block2;
      if (this.isWall && block.getType().isSolid() && blockFace.getModY() == 0) {
         return false;
      } else if (!this.isFloor || blockFace != BlockFace.UP && !blockBelow.getType().isSolid()) {
         if (this.isRoof && blockFace == BlockFace.DOWN) {
            return false;
         } else {
            return !this.isRoof || !blockAbove.getType().isSolid();
         }
      } else {
         return false;
      }
   }

   @NotNull
   public final List<Material> limitedBlocks() {
      return this.blockTypes;
   }

   @NotNull
   public final List<String> limitedNexoBlockIds() {
      return this.nexoBlocks;
   }

   @NotNull
   public final Set<Tag<Material>> limitedTags() {
      return this.blockTags;
   }

   public final boolean checkLimited(@NotNull Block block) {
      if (this.blockTypes.isEmpty() && this.blockTags.isEmpty() && this.nexoBlocks.isEmpty()) {
         return this.type == LimitedPlacing.LimitedPlacingType.ALLOW;
      } else {
         String str = this.checkIfNexoItem(block);
         if (str == null) {
            boolean flag;
            if (!this.blockTypes.contains(block.getType())) {
               Iterable iterable = (Iterable)this.blockTags;
               if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                  flag = false;
               } else {
                  Iterator iterator = iterable.iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        flag = false;
                        break;
                     }

                     Object element = iterator.next();
                     Tag it = (Tag)element;
                     int value = 0;
                     if (it.isTagged(block.getType())) {
                        flag = true;
                        break;
                     }
                  }
               }

               if (!flag) {
                  flag = false;
                  return flag;
               }
            }

            flag = true;
            return flag;
         } else {
            String nexoId = str;
            return !((Collection)this.nexoBlocks).isEmpty() && this.nexoBlocks.contains(nexoId);
         }
      }
   }

   private final String checkIfNexoItem(Block block) {
      CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(block);
      Mechanic mechanic = customBlockMechanic != null ? (Mechanic)customBlockMechanic : (Mechanic)NexoFurniture.furnitureMechanic(block.getLocation());
      return mechanic != null ? mechanic.getItemID() : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$RadiusLimitation;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "radius", "", "getRadius", "()I", "amount", "getAmount", "com.nexomc:core"}
   )
   public static final class RadiusLimitation {
      private final int radius;
      private final int amount;

      public RadiusLimitation(@NotNull ConfigurationSection section) {
         super();
         this.radius = section.getInt("radius", -1);
         this.amount = section.getInt("amount", -1);
      }

      public final int getRadius() {
         return this.radius;
      }

      public final int getAmount() {
         return this.amount;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing$LimitedPlacingType;", "", "<init>", "(Ljava/lang/String;I)V", "ALLOW", "DENY", "com.nexomc:core"}
   )
   public static enum LimitedPlacingType {
      ALLOW,
      DENY;

      @NotNull
      public static EnumEntries<LimitedPlacingType> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }
}
