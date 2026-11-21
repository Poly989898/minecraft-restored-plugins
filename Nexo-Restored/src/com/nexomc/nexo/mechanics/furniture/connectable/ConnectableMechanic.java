package com.nexomc.nexo.mechanics.furniture.connectable;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\b\u0018\u0000 M2\u00020\u0001:\u0002MNBS\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\r\u0010\u000eB\u0011\b\u0016\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\r\u0010\u0011J\u001a\u00100\u001a\u00020\u001c2\b\u00101\u001a\u0004\u0018\u00010\u00072\u0006\u00102\u001a\u00020\u0005H\u0002J\u0018\u00103\u001a\u0002042\u0006\u00105\u001a\u0002062\b\b\u0002\u00107\u001a\u000208J\u000e\u00109\u001a\u0002042\u0006\u00105\u001a\u000206J\u000e\u0010:\u001a\u0002042\u0006\u00105\u001a\u000206J\u0010\u0010;\u001a\u00020<2\u0006\u00105\u001a\u000206H\u0002J\u000e\u0010=\u001a\u00020\u001c2\u0006\u00105\u001a\u000206J\t\u0010>\u001a\u00020\u0003HÆ\u0003J\t\u0010?\u001a\u00020\u0005HÂ\u0003J\u000b\u0010@\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010C\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010D\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u0007HÆ\u0003Je\u0010F\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0007HÆ\u0001J\u0014\u0010G\u001a\u00020H2\b\u0010I\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010J\u001a\u00020KHÖ\u0081\u0004J\n\u0010L\u001a\u00020\u0005HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u001b\u0010\u001b\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010 \u001a\u0004\b\u001d\u0010\u001eR\u001b\u0010!\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b#\u0010 \u001a\u0004\b\"\u0010\u001eR\u001b\u0010$\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b&\u0010 \u001a\u0004\b%\u0010\u001eR\u001b\u0010'\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b)\u0010 \u001a\u0004\b(\u0010\u001eR\u001b\u0010*\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b,\u0010 \u001a\u0004\b+\u0010\u001eR\u001b\u0010-\u001a\u00020\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b/\u0010 \u001a\u0004\b.\u0010\u001e¨\u0006O"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic;", "", "type", "Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;", "defaultId", "", "default", "Lnet/kyori/adventure/key/Key;", "straight", "left", "right", "inner", "outer", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;Ljava/lang/String;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getType", "()Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;", "getDefault", "()Lnet/kyori/adventure/key/Key;", "getStraight", "getLeft", "getRight", "getInner", "getOuter", "defaultItem", "Lcom/nexomc/nexo/items/ItemBuilder;", "getDefaultItem", "()Lcom/nexomc/nexo/items/ItemBuilder;", "defaultItem$delegate", "Lkotlin/Lazy;", "straightItem", "getStraightItem", "straightItem$delegate", "leftEndItem", "getLeftEndItem", "leftEndItem$delegate", "rightEndItem", "getRightEndItem", "rightEndItem$delegate", "innerCornerItem", "getInnerCornerItem", "innerCornerItem$delegate", "outerCornerItem", "getOuterCornerItem", "outerCornerItem$delegate", "buildDisplayItem", "typeKey", "suffix", "scheduleUpdateState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "delay", "", "updateState", "updateSurrounding", "determineConnectableShape", "Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic$ConnectType;", "displayedItem", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "ConnectType", "com.nexomc:core"}
)
public final class ConnectableMechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ConnectableItemType type;
   @NotNull
   private final String defaultId;
   @Nullable
   private final Key default_;
   @Nullable
   private final Key straight;
   @Nullable
   private final Key left;
   @Nullable
   private final Key right;
   @Nullable
   private final Key inner;
   @Nullable
   private final Key outer;
   @NotNull
   private final Lazy defaultItem$delegate;
   @NotNull
   private final Lazy straightItem$delegate;
   @NotNull
   private final Lazy leftEndItem$delegate;
   @NotNull
   private final Lazy rightEndItem$delegate;
   @NotNull
   private final Lazy innerCornerItem$delegate;
   @NotNull
   private final Lazy outerCornerItem$delegate;
   @NotNull
   private static final NamespacedKey CONNECTABLE_KEY;
   @NotNull
   private static final String BLOCKSTATE_KEY = "connectable";

   public ConnectableMechanic(@NotNull ConnectableItemType type, @NotNull String defaultId, @Nullable Key key, @Nullable Key straight, @Nullable Key left, @Nullable Key right, @Nullable Key inner, @Nullable Key outer) {
      super();
      this.type = type;
      this.defaultId = defaultId;
      this.default_ = key;
      this.straight = straight;
      this.left = left;
      this.right = right;
      this.inner = inner;
      this.outer = outer;
      this.defaultItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.defaultItem_delegate$lambda$0(this)));
      this.straightItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.straightItem_delegate$lambda$0(this)));
      this.leftEndItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.leftEndItem_delegate$lambda$0(this)));
      this.rightEndItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.rightEndItem_delegate$lambda$0(this)));
      this.innerCornerItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.innerCornerItem_delegate$lambda$0(this)));
      this.outerCornerItem$delegate = LazyKt.lazy((() -> ConnectableMechanic.outerCornerItem_delegate$lambda$0(this)));
   }

   @NotNull
   public final ConnectableItemType getType() {
      return this.type;
   }

   @Nullable
   public final Key getDefault() {
      return this.default_;
   }

   @Nullable
   public final Key getStraight() {
      return this.straight;
   }

   @Nullable
   public final Key getLeft() {
      return this.left;
   }

   @Nullable
   public final Key getRight() {
      return this.right;
   }

   @Nullable
   public final Key getInner() {
      return this.inner;
   }

   @Nullable
   public final Key getOuter() {
      return this.outer;
   }

   private final ItemBuilder getDefaultItem() {
      Lazy lazy = this.defaultItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder getStraightItem() {
      Lazy lazy = this.straightItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder getLeftEndItem() {
      Lazy lazy = this.leftEndItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder getRightEndItem() {
      Lazy lazy = this.rightEndItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder getInnerCornerItem() {
      Lazy lazy = this.innerCornerItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder getOuterCornerItem() {
      Lazy lazy = this.outerCornerItem$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final ItemBuilder buildDisplayItem(Key typeKey, String suffix) {
      ItemBuilder itemBuilder;
      if (ConnectableMechanic.WhenMappings.$EnumSwitchMapping$0[this.type.ordinal()] == 1) {
         itemBuilder = typeKey != null ? (new ItemBuilder(Material.PAPER)).setItemModel(typeKey) : this.getDefaultItem().clone().setBlockStates(MapsKt.mapOf(TuplesKt.to("connectable", suffix)));
      } else {
         itemBuilder = NexoItems.itemFromId(typeKey != null ? typeKey.value() : null);
         if (itemBuilder == null) {
            itemBuilder = this.getDefaultItem();
         }
      }

      return itemBuilder;
   }

   public ConnectableMechanic(@NotNull ConfigurationSection section) {
      String str = "type";
      ConnectableItemType connectableItemType = EnumUtils.getEnum(ConnectableItemType.class, NexoYamlKt.stringOrNull(section, str));
      if (connectableItemType == null) {
         connectableItemType = ConnectableItemType.Companion.getDefaultType();
      }

      String rootId = NexoYamlKt.getRootId(section);
      Key key = NexoYamlKt.key(section, "default");
      if (key == null) {
         key = NexoYamlKt.key(NexoYamlKt.getRootSection(section), "Components.item_model");
         if (key == null) {
            key = NexoYamlKt.key(NexoYamlKt.getRootSection(section), "Pack.model");
         }
      }

      Key key2 = NexoYamlKt.key(section, "straight");
      if (key2 == null) {
         key2 = NexoYamlKt.key(section, "default");
         key2 = key2 != null ? KeyUtils.INSTANCE.appendSuffix(key2, "_straight") : null;
      }

      Key key3 = NexoYamlKt.key(section, "left");
      if (key3 == null) {
         key3 = NexoYamlKt.key(section, "default");
         key3 = key3 != null ? KeyUtils.INSTANCE.appendSuffix(key3, "_left") : null;
      }

      Key key4 = NexoYamlKt.key(section, "right");
      if (key4 == null) {
         key4 = NexoYamlKt.key(section, "default");
         key4 = key4 != null ? KeyUtils.INSTANCE.appendSuffix(key4, "_right") : null;
      }

      Key key5 = NexoYamlKt.key(section, "inner");
      if (key5 == null) {
         key5 = NexoYamlKt.key(section, "default");
         key5 = key5 != null ? KeyUtils.INSTANCE.appendSuffix(key5, "_inner") : null;
      }

      Key key6 = NexoYamlKt.key(section, "outer");
      if (key6 == null) {
         key6 = NexoYamlKt.key(section, "default");
         key6 = key6 != null ? KeyUtils.INSTANCE.appendSuffix(key6, "_outer") : null;
      }

      this(connectableItemType, rootId, key, key2, key3, key4, key5, key6);
   }

   public final void scheduleUpdateState(@NotNull ItemDisplay baseEntity, long delay) {
      SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, delay, (a0_ -> ConnectableMechanic.scheduleUpdateState$lambda$0(this, baseEntity, a0_)));
   }

   public final void updateState(@NotNull ItemDisplay baseEntity) {
      ConnectType type = this.determineConnectableShape(baseEntity);
      baseEntity.getPersistentDataContainer().set(CONNECTABLE_KEY, ConnectableMechanic.ConnectType.Companion.getDataType(), type);
      FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.refreshItem(baseEntity);
      }

   }

   public final void updateSurrounding(@NotNull ItemDisplay baseEntity) {
      SchedulerUtils.INSTANCE.runTaskLater(2L, (() -> ConnectableMechanic.updateSurrounding$lambda$0(baseEntity, this)));
   }

   private final ConnectType determineConnectableShape(ItemDisplay baseEntity) {
      ConnectType currentType = baseEntity.getPersistentDataContainer().get(CONNECTABLE_KEY, ConnectableMechanic.ConnectType.Companion.getDataType());
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic == null) {
         return ConnectableMechanic.ConnectType.DEFAULT;
      } else {
         FurnitureMechanic mechanic = furnitureMechanic;
         Function1 furnitureCheck = (a0_ -> ConnectableMechanic.determineConnectableShape$lambda$0(mechanic, (Entity)a0_));
         if (currentType != null ? currentType.isCornerRotated() : false) {
            baseEntity.setRotation(baseEntity.getYaw() - 90.0F, baseEntity.getPitch());
         }

         BlockFace facing = baseEntity.getFacing();
         BlockFace aheadFacing = facing;
         facing = aheadFacing.getOppositeFace();
         BlockFace behindFacing = facing;
         switch (ConnectableMechanic.WhenMappings.$EnumSwitchMapping$1[aheadFacing.ordinal()]) {
            case 1 -> facing = BlockFace.WEST;
            case 2 -> facing = BlockFace.NORTH;
            case 3 -> facing = BlockFace.EAST;
            case 4 -> facing = BlockFace.SOUTH;
            default -> facing = BlockFace.WEST;
         }

         facing = facing.getOppositeFace();
         BlockFace leftFacing = facing;
         Location location = baseEntity.getLocation().add(leftFacing.getDirection());
         Location leftBlock = location;
         location = baseEntity.getLocation().add(leftFacing.getOppositeFace().getDirection());
         Location rightBlock = location;
         location = baseEntity.getLocation().add(aheadFacing.getDirection());
         Location aheadBlock = location;
         location = baseEntity.getLocation().add(behindFacing.getDirection());
         Location behindBlock = location;
         ItemDisplay itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(leftBlock);
         if (itemDisplay != null) {
            ItemDisplay itemDisplay2 = itemDisplay;
            itemDisplay = (Boolean)furnitureCheck.invoke(itemDisplay2) ? itemDisplay2 : null;
         } else {
            itemDisplay = null;
         }

         ItemDisplay leftEntity = itemDisplay;
         itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(rightBlock);
         if (itemDisplay != null) {
            ItemDisplay itemDisplay3 = itemDisplay;
            itemDisplay = (Boolean)furnitureCheck.invoke(itemDisplay3) ? itemDisplay3 : null;
         } else {
            itemDisplay = null;
         }

         ItemDisplay rightEntity = itemDisplay;
         itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(aheadBlock);
         if (itemDisplay != null) {
            ItemDisplay itemDisplay4 = itemDisplay;
            itemDisplay = (Boolean)furnitureCheck.invoke(itemDisplay4) ? itemDisplay4 : null;
         } else {
            itemDisplay = null;
         }

         ItemDisplay aheadEntity = itemDisplay;
         itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(behindBlock);
         if (itemDisplay != null) {
            ItemDisplay itemDisplay5 = itemDisplay;
            itemDisplay = (Boolean)furnitureCheck.invoke(itemDisplay5) ? itemDisplay5 : null;
         } else {
            itemDisplay = null;
         }

         ItemDisplay behindEntity = itemDisplay;
         ConnectType connectType;
         if (leftEntity != null) {
            ConnectType.Companion companion = ConnectableMechanic.ConnectType.Companion;
            Entity p0 = (Entity)leftEntity;
            int value = 0;
            connectType = companion.fromEntity(p0);
         } else {
            connectType = null;
         }

         ConnectType leftState = connectType;
         if (rightEntity != null) {
            ConnectType.Companion behindState = ConnectableMechanic.ConnectType.Companion;
            Entity p0 = (Entity)rightEntity;
            int value2 = 0;
            connectType = behindState.fromEntity(p0);
         } else {
            connectType = null;
         }

         ConnectType rightState = connectType;
         if (aheadEntity != null) {
            ConnectType.Companion companion2 = ConnectableMechanic.ConnectType.Companion;
            Entity p0 = (Entity)aheadEntity;
            int value3 = 0;
            connectType = companion2.fromEntity(p0);
         } else {
            connectType = null;
         }

         ConnectType aheadState = connectType;
         if (behindEntity != null) {
            ConnectType.Companion companion3 = ConnectableMechanic.ConnectType.Companion;
            Entity p0 = (Entity)behindEntity;
            int value4 = 0;
            connectType = companion3.fromEntity(p0);
         } else {
            connectType = null;
         }

         ConnectType behindState = connectType;
         if (leftState != null) {
            if (leftEntity.getFacing() == leftEntity.getFacing()) {
               currentType = ConnectableMechanic.ConnectType.RIGHT_END;
            } else {
               this.scheduleUpdateState(baseEntity, 1L);
            }
         }

         if (rightState != null) {
            if (rightEntity.getFacing() == rightEntity.getFacing()) {
               currentType = ConnectableMechanic.ConnectType.LEFT_END;
            } else {
               this.scheduleUpdateState(baseEntity, 1L);
            }
         }

         if ((aheadState != null ? !aheadState.isCorner() : false) && aheadFacing != aheadEntity.getFacing()) {
            if (rightState != null) {
               currentType = ConnectableMechanic.ConnectType.INNER_CORNER;
            }

            if (leftState != null) {
               currentType = ConnectableMechanic.ConnectType.INNER_CORNER_ROTATED;
            }
         } else if ((behindState != null ? !behindState.isCorner() : false) && behindFacing != behindEntity.getFacing()) {
            currentType = rightState != null ? ConnectableMechanic.ConnectType.OUTER_CORNER_ROTATED : (leftState == null && behindEntity.getFacing() == leftFacing ? ConnectableMechanic.ConnectType.OUTER_CORNER_ROTATED : ConnectableMechanic.ConnectType.OUTER_CORNER);
         }

         if (leftState != null && rightState != null && (leftEntity.getFacing() == rightEntity.getFacing() || leftState.isCorner() || rightState.isCorner())) {
            currentType = ConnectableMechanic.ConnectType.STRAIGHT;
         }

         if (leftState == null && rightState == null && (behindState == null || behindFacing == behindEntity.getFacing() || behindFacing == behindEntity.getFacing().getOppositeFace())) {
            currentType = ConnectableMechanic.ConnectType.DEFAULT;
         }

         if (currentType != null ? currentType.isCornerRotated() : false) {
            baseEntity.setRotation(baseEntity.getYaw() + 90.0F, baseEntity.getPitch());
         }

         connectType = currentType;
         if (currentType == null) {
            connectType = ConnectableMechanic.ConnectType.DEFAULT;
         }

         return connectType;
      }
   }

   @NotNull
   public final ItemBuilder displayedItem(@NotNull ItemDisplay baseEntity) {
      ItemBuilder itemBuilder;
      switch (ConnectableMechanic.WhenMappings.$EnumSwitchMapping$2[(baseEntity.getPersistentDataContainer().getOrDefault(CONNECTABLE_KEY, ConnectableMechanic.ConnectType.Companion.getDataType(), ConnectableMechanic.ConnectType.DEFAULT)).ordinal()]) {
         case 1:
            itemBuilder = this.getStraightItem();
            break;
         case 2:
            itemBuilder = this.getLeftEndItem();
            break;
         case 3:
            itemBuilder = this.getRightEndItem();
            break;
         case 4:
         case 5:
            itemBuilder = this.getInnerCornerItem();
            break;
         case 6:
         case 7:
            itemBuilder = this.getOuterCornerItem();
            break;
         default:
            itemBuilder = this.getDefaultItem();
      }

      ItemBuilder item = itemBuilder;
      return item;
   }

   @NotNull
   public final ConnectableItemType component1() {
      return this.type;
   }

   private final String component2() {
      return this.defaultId;
   }

   @Nullable
   public final Key component3() {
      return this.default_;
   }

   @Nullable
   public final Key component4() {
      return this.straight;
   }

   @Nullable
   public final Key component5() {
      return this.left;
   }

   @Nullable
   public final Key component6() {
      return this.right;
   }

   @Nullable
   public final Key component7() {
      return this.inner;
   }

   @Nullable
   public final Key component8() {
      return this.outer;
   }

   @NotNull
   public final ConnectableMechanic copy(@NotNull ConnectableItemType type, @NotNull String defaultId, @Nullable Key key, @Nullable Key straight, @Nullable Key left, @Nullable Key right, @Nullable Key inner, @Nullable Key outer) {
      return new ConnectableMechanic(type, defaultId, key, straight, left, right, inner, outer);
   }

   @NotNull
   public String toString() {
      return "ConnectableMechanic(type=" + this.type + ", defaultId=" + this.defaultId + ", default_=" + this.default_ + ", straight=" + this.straight + ", left=" + this.left + ", right=" + this.right + ", inner=" + this.inner + ", outer=" + this.outer + ")";
   }

   public int hashCode() {
      int result = this.type.hashCode();
      result = result * 31 + this.defaultId.hashCode();
      result = result * 31 + (this.default_ == null ? 0 : this.default_.hashCode());
      result = result * 31 + (this.straight == null ? 0 : this.straight.hashCode());
      result = result * 31 + (this.left == null ? 0 : this.left.hashCode());
      result = result * 31 + (this.right == null ? 0 : this.right.hashCode());
      result = result * 31 + (this.inner == null ? 0 : this.inner.hashCode());
      result = result * 31 + (this.outer == null ? 0 : this.outer.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ConnectableMechanic)) {
         return false;
      } else {
         ConnectableMechanic connectableMechanic = (ConnectableMechanic)other;
         if (this.type != connectableMechanic.type) {
            return false;
         } else if (!Intrinsics.areEqual(this.defaultId, connectableMechanic.defaultId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.default_, connectableMechanic.default_)) {
            return false;
         } else if (!Intrinsics.areEqual(this.straight, connectableMechanic.straight)) {
            return false;
         } else if (!Intrinsics.areEqual(this.left, connectableMechanic.left)) {
            return false;
         } else if (!Intrinsics.areEqual(this.right, connectableMechanic.right)) {
            return false;
         } else if (!Intrinsics.areEqual(this.inner, connectableMechanic.inner)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.outer, connectableMechanic.outer);
         }
      }
   }

   private static final ItemBuilder defaultItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      ItemBuilder itemBuilder = NexoItems.itemFromId(connectableMechanic.defaultId);
      if (itemBuilder == null) {
         ItemBuilder it = new ItemBuilder(Material.PAPER);
         int value = 0;
         if (connectableMechanic.type == ConnectableItemType.ITEM_MODEL) {
            it.setItemModel(connectableMechanic.default_);
         }

         itemBuilder = it;
      }

      return itemBuilder;
   }

   private static final ItemBuilder straightItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      return connectableMechanic.buildDisplayItem(connectableMechanic.straight, "straight");
   }

   private static final ItemBuilder leftEndItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      return connectableMechanic.buildDisplayItem(connectableMechanic.left, "left");
   }

   private static final ItemBuilder rightEndItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      return connectableMechanic.buildDisplayItem(connectableMechanic.right, "right");
   }

   private static final ItemBuilder innerCornerItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      return connectableMechanic.buildDisplayItem(connectableMechanic.inner, "inner");
   }

   private static final ItemBuilder outerCornerItem_delegate$lambda$0(ConnectableMechanic connectableMechanic) {
      return connectableMechanic.buildDisplayItem(connectableMechanic.outer, "outer");
   }

   private static final Unit scheduleUpdateState$lambda$0(ConnectableMechanic connectableMechanic, ItemDisplay baseEntity, Entity it) {
      connectableMechanic.updateState(baseEntity);
      return Unit.INSTANCE;
   }

   private static final Unit updateSurrounding$lambda$0(ItemDisplay baseEntity, ConnectableMechanic connectableMechanic) {
      Pair pair = TuplesKt.to(LocationUtilsKt.minus(LocationUtilsKt.getBlockLocation((Entity)baseEntity), 1, 0, 0), LocationUtilsKt.plus(LocationUtilsKt.getBlockLocation((Entity)baseEntity), 1, 0, 0));
      Location leftLoc = (Location)pair.component1();
      Location rightLoc = (Location)pair.component2();
      Pair pair2 = TuplesKt.to(LocationUtilsKt.minus(LocationUtilsKt.getBlockLocation((Entity)baseEntity), 0, 0, 1), LocationUtilsKt.plus(LocationUtilsKt.getBlockLocation((Entity)baseEntity), 0, 0, 1));
      Location aheadLoc = (Location)pair2.component1();
      Location behindLoc = (Location)pair2.component2();
      ItemDisplay itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(leftLoc);
      if (itemDisplay != null) {
         ItemDisplay it = itemDisplay;
         int value = 0;
         itemDisplay = it.isValid() ? it : null;
         if (itemDisplay != null) {
            ItemDisplay p0 = itemDisplay;
            value = 0;
            connectableMechanic.scheduleUpdateState(p0, 1L);
         }
      }

      itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(rightLoc);
      if (itemDisplay != null) {
         ItemDisplay it = itemDisplay;
         int value2 = 0;
         itemDisplay = it.isValid() ? it : null;
         if (itemDisplay != null) {
            ItemDisplay p0 = itemDisplay;
            value2 = 0;
            connectableMechanic.scheduleUpdateState(p0, 1L);
         }
      }

      itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(aheadLoc);
      if (itemDisplay != null) {
         ItemDisplay it = itemDisplay;
         int value3 = 0;
         itemDisplay = it.isValid() ? it : null;
         if (itemDisplay != null) {
            ItemDisplay p0 = itemDisplay;
            value3 = 0;
            connectableMechanic.scheduleUpdateState(p0, 1L);
         }
      }

      itemDisplay = IFurniturePacketManager.Companion.baseEntityFromHitbox(behindLoc);
      if (itemDisplay != null) {
         ItemDisplay it = itemDisplay;
         int value4 = 0;
         itemDisplay = it.isValid() ? it : null;
         if (itemDisplay != null) {
            ItemDisplay p0 = itemDisplay;
            value4 = 0;
            connectableMechanic.scheduleUpdateState(p0, 1L);
         }
      }

      return Unit.INSTANCE;
   }

   private static final boolean determineConnectableShape$lambda$0(FurnitureMechanic mechanic, Entity entity) {
      return entity.isValid() && Intrinsics.areEqual(NexoFurniture.furnitureMechanic(entity), mechanic);
   }

   static {
      CONNECTABLE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "connectable");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic$Companion;", "", "<init>", "()V", "CONNECTABLE_KEY", "Lorg/bukkit/NamespacedKey;", "getCONNECTABLE_KEY", "()Lorg/bukkit/NamespacedKey;", "BLOCKSTATE_KEY", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getCONNECTABLE_KEY() {
         return ConnectableMechanic.CONNECTABLE_KEY;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u0000 \u00102\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0010B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\f\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\b\f\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u000ej\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic$ConnectType;", "", "<init>", "(Ljava/lang/String;I)V", "DEFAULT", "STRAIGHT", "LEFT_END", "RIGHT_END", "OUTER_CORNER", "OUTER_CORNER_ROTATED", "INNER_CORNER", "INNER_CORNER_ROTATED", "isCorner", "", "()Z", "isCornerRotated", "Companion", "com.nexomc:core"}
   )
   public static enum ConnectType {
      DEFAULT,
      STRAIGHT,
      LEFT_END,
      RIGHT_END,
      OUTER_CORNER,
      OUTER_CORNER_ROTATED,
      INNER_CORNER,
      INNER_CORNER_ROTATED;

      @NotNull
      public static final Companion Companion = new Companion();
      @NotNull
      private static final PersistentDataType<String, ConnectType> dataType;

      public final boolean isCorner() {
         return this == OUTER_CORNER || this == INNER_CORNER || this == OUTER_CORNER_ROTATED || this == INNER_CORNER_ROTATED;
      }

      public final boolean isCornerRotated() {
         return this == OUTER_CORNER_ROTATED || this == INNER_CORNER_ROTATED;
      }

      @NotNull
      public static EnumEntries<ConnectType> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }

      static {
         PersistentDataType persistentDataType = DataType.asEnum(ConnectType.class);
         dataType = persistentDataType;
      }

      @Metadata(
         mv = {2, 4, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\n\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000b\u001a\u00020\fR\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\r"},
         d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic$ConnectType$Companion;", "", "<init>", "()V", "dataType", "Lorg/bukkit/persistence/PersistentDataType;", "", "Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic$ConnectType;", "getDataType", "()Lorg/bukkit/persistence/PersistentDataType;", "fromEntity", "entity", "Lorg/bukkit/entity/Entity;", "com.nexomc:core"}
      )
      public static final class Companion {
         private Companion() {
         }

         @NotNull
         public final PersistentDataType<String, ConnectType> getDataType() {
            return ConnectableMechanic.ConnectType.dataType;
         }

         @Nullable
         public final ConnectType fromEntity(@NotNull Entity entity) {
            return entity.getPersistentDataContainer().get(ConnectableMechanic.Companion.getCONNECTABLE_KEY(), this.getDataType());
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      public static final int[] $EnumSwitchMapping$1;
      public static final int[] $EnumSwitchMapping$2;
      static {
         int[] a_0 = new int[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableItemType.values().length];
         try { a_0[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableItemType.ITEM_MODEL.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
         int[] a_1 = new int[org.bukkit.block.BlockFace.values().length];
         try { a_1[org.bukkit.block.BlockFace.NORTH.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_1[org.bukkit.block.BlockFace.EAST.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_1[org.bukkit.block.BlockFace.SOUTH.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_1[org.bukkit.block.BlockFace.WEST.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$1 = a_1;
         int[] a_2 = new int[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.values().length];
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.STRAIGHT.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.LEFT_END.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.RIGHT_END.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.INNER_CORNER.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.INNER_CORNER_ROTATED.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.OUTER_CORNER.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         try { a_2[com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic.ConnectType.OUTER_CORNER_ROTATED.ordinal()] = 7; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$2 = a_2;
      }
   }
}
