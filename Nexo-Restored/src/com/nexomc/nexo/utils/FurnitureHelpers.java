package com.nexomc.nexo.utils;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.light.LightMechanic;
import com.nexomc.nexo.mechanics.limitedplacing.LimitedPlacing;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import org.bukkit.Color;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005J\u001c\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\u0005H\u0007b\u0002\b\rJ\u0014\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u0007b\u0002\b\rJ2\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u000b\u001a\u00020\fH\u0007b\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018b\u0002\b\rJ8\u0010\u0011\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u0012H\u0007b\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018b\u0002\b\rJ7\u0010\u001a\u001a\t\u0018\u00010\u001b¢\u0006\u0002\b\u001c2\u0006\u0010\u000b\u001a\u00020\fH\u0007b\u0002\b\rb\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018J:\u0010\u001a\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001bH\u0007b\u0002\b\rb\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018J0\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u000b\u001a\u00020\fH\u0007b\u0002\b\rb\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018JQ\u0010 \u001a\u00020\u001f2\u0006\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b#b\u0002\b\rb\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015b\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018¢\u0006\u0002\u0010\"¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/utils/FurnitureHelpers;", "", "<init>", "()V", "correctedYaw", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "yaw", "furnitureYaw", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "Lkotlin/jvm/JvmStatic;", "rotationToYaw", "rotation", "Lorg/bukkit/Rotation;", "furnitureItem", "Lorg/bukkit/inventory/ItemStack;", "Lkotlin/Deprecated;", "message", "This has been moved into NexoFurniture", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.18", "itemStack", "furnitureDye", "Lorg/bukkit/Color;", "Lorg/jetbrains/annotations/NotNull;", "dyeColor", "lightState", "", "toggleLight", "state", "(Lorg/bukkit/entity/ItemDisplay;Ljava/lang/Boolean;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)Z", "Lkotlin/jvm/JvmOverloads;", "com.nexomc:core"}
)
public final class FurnitureHelpers {
   @NotNull
   public static final FurnitureHelpers INSTANCE = new FurnitureHelpers();

   private FurnitureHelpers() {
   }

   public final float correctedYaw(@NotNull FurnitureMechanic mechanic, float yaw) {
      LimitedPlacing limitedPlacing = mechanic.getLimitedPlacing();
      return (limitedPlacing != null ? !limitedPlacing.isRoof() : false) ? yaw : (mechanic.getProperties().isFixedTransform() ? yaw - (float)180 : yaw);
   }

   @JvmStatic
   public static final void furnitureYaw(@NotNull ItemDisplay baseEntity, float yaw) {
      if (NexoFurniture.isFurniture((Entity)baseEntity)) {
         baseEntity.setRotation(yaw, baseEntity.getPitch());
      }
   }

   @JvmStatic
   public static final float rotationToYaw(@NotNull Rotation rotation) {
      return (float)FurnitureHelpers.EntriesMappings.entries$0.indexOf(rotation) * 360.0F / 8.0F;
   }

   /** @deprecated */
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @JvmStatic
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   @Nullable
   public static final ItemStack furnitureItem(@NotNull ItemDisplay baseEntity) {
      FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      return furnitureBaseEntity != null ? furnitureBaseEntity.getItemStack() : null;
   }

   /** @deprecated */
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @JvmStatic
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final void furnitureItem(@NotNull ItemDisplay baseEntity, @NotNull ItemStack itemStack) {
      if (NexoFurniture.furnitureMechanic((Entity)baseEntity) != null) {
         FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
         if (furnitureBaseEntity != null) {
            furnitureBaseEntity.itemStack(itemStack, baseEntity);
         }

      }
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   @Nullable
   public static final Color furnitureDye(@NotNull ItemDisplay baseEntity) {
      Integer number = (Integer)baseEntity.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY(), PersistentDataType.INTEGER);
      Color color;
      if (number != null) {
         int p0 = ((Number)number).intValue();
         int value = 0;
         color = Color.fromRGB(p0);
      } else {
         color = null;
      }

      return color;
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final void furnitureDye(@NotNull ItemDisplay baseEntity, @Nullable Color dyeColor) {
      if (dyeColor == null) {
         baseEntity.getPersistentDataContainer().remove(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY());
      } else {
         baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY(), PersistentDataType.INTEGER, dyeColor.asRGB());
      }

      FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.refreshItem(baseEntity);
      }

   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final boolean lightState(@NotNull ItemDisplay baseEntity) {
      boolean flag;
      block: {
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic != null) {
            LightMechanic light = furnitureMechanic.getLight();
            if (light != null) {
               flag = !light.isEmpty();
               break block;
            }
         }

         flag = false;
      }

      if (!flag) {
         return false;
      } else {
         Object orDefault = baseEntity.getPersistentDataContainer().getOrDefault(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, true);
         return (Boolean)orDefault;
      }
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @JvmOverloads
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity, @Nullable Boolean state, @Nullable FurnitureMechanic mechanic) {
      if (mechanic != null) {
         int value = 0;
         FurnitureMechanic furnitureMechanic = !mechanic.getLight().isEmpty() ? mechanic : null;
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic_1 = furnitureMechanic;
            boolean newState = state != null ? state : !(Boolean)baseEntity.getPersistentDataContainer().getOrDefault(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, true);
            if (!mechanic_1.getLight().getToggleable()) {
               return true;
            }

            baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_LIGHT_KEY(), DataType.BOOLEAN, state != null ? state : newState);
            FurnitureBaseEntity furnitureBaseEntity = (FurnitureBaseEntity)IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
            if (furnitureBaseEntity != null) {
               furnitureBaseEntity.refreshItem(baseEntity);
            }

            mechanic_1.getLight().refreshLights(baseEntity, mechanic_1);
            return newState;
         }
      }

      return false;
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @JvmOverloads
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity, @Nullable Boolean state) {
      return toggleLight(baseEntity, state, (FurnitureMechanic)null);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "This has been moved into NexoFurniture"
   )
   @JvmOverloads
   @ScheduledForRemoval(
      inVersion = "1.18"
   )
   public static final boolean toggleLight(@NotNull ItemDisplay baseEntity) {
      return toggleLight(baseEntity, (Boolean)null, (FurnitureMechanic)null);
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.Rotation.values());
   }
}
