package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.Iterator;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u0002\u001a\u00020\u0003J\u0016\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0002\u001a\u00020\u0003J\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u0014\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004J\n\u0010#\u001a\u00020\u0019H\u0096\u0080\u0004R\u001a\u0010\u0004\u001a\u00020\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\u000e\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u001d¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001f¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureBaseEntity;", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "getMechanic$com_nexomc_core", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "setMechanic$com_nexomc_core", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;)V", "refreshItem", "", "itemStack", "item", "Lorg/bukkit/inventory/ItemStack;", "value", "getItemStack", "()Lorg/bukkit/inventory/ItemStack;", "baseUuid", "Ljava/util/UUID;", "getBaseUuid", "()Ljava/util/UUID;", "baseId", "", "getBaseId", "()I", "furnitureId", "", "getFurnitureId", "()Ljava/lang/String;", "equals", "", "other", "hashCode", "com.nexomc:core"}
)
public final class FurnitureBaseEntity {
   @NotNull
   private FurnitureMechanic mechanic;
   @NotNull
   private ItemStack itemStack;
   @NotNull
   private final UUID baseUuid;
   private final int baseId;
   @NotNull
   private final String furnitureId;

   public FurnitureBaseEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      super();
      this.mechanic = mechanic;
      ItemBuilder itemBuilder = this.mechanic.placedItem(baseEntity);
      int value = 0;
      NamespacedKey iteM_ID = NexoItems.INSTANCE.getITEM_ID();
      PersistentDataType persistentDataType = PersistentDataType.STRING;
      itemBuilder.customTag(iteM_ID, persistentDataType, this.mechanic.getItemID());
      this.itemStack = itemBuilder.build();
      Colorable colorable = ColorableKt.asColorable(this.itemStack);
      if (colorable != null) {
         colorable.setColor(NexoFurniture.furnitureDye(baseEntity));
      }

      this.itemStack.unsetData((DataComponentType)DataComponentTypes.CUSTOM_NAME);
      UUID uniqueId = baseEntity.getUniqueId();
      this.baseUuid = uniqueId;
      this.baseId = baseEntity.getEntityId();
      this.furnitureId = this.mechanic.getItemID();
   }

   @NotNull
   public final FurnitureMechanic getMechanic$com_nexomc_core() {
      return this.mechanic;
   }

   public final void setMechanic$com_nexomc_core(@NotNull FurnitureMechanic furnitureMechanic) {
      this.mechanic = furnitureMechanic;
   }

   public final void refreshItem(@NotNull ItemDisplay baseEntity) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      ItemBuilder itemBuilder = this.mechanic.placedItem(baseEntity);
      int value = 0;
      NamespacedKey iteM_ID = NexoItems.INSTANCE.getITEM_ID();
      PersistentDataType persistentDataType = PersistentDataType.STRING;
      itemBuilder.customTag(iteM_ID, persistentDataType, this.mechanic.getItemID());
      this.itemStack = itemBuilder.regenerateItem().build();
      Colorable colorable = ColorableKt.asColorable(this.itemStack);
      if (colorable != null) {
         colorable.setColor(NexoFurniture.furnitureDye(baseEntity));
      }

      this.itemStack.unsetData((DataComponentType)DataComponentTypes.CUSTOM_NAME);
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      value = 0;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value2 = 0;
            MechanicFactory mechanicFactory2 = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory2 = null;
            }

            it = (FurnitureFactory)mechanicFactory2;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
      if (furnitureFactory != null) {
         IFurniturePacketManager packetManager = furnitureFactory.packetManager();
         if (packetManager != null) {
            packetManager.sendFurnitureMetadataPacket(baseEntity, this.mechanic);
         }
      }

   }

   public final void itemStack(@NotNull ItemStack item, @NotNull ItemDisplay baseEntity) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Colorable colorable = ColorableKt.asColorable(item);
      if (colorable != null) {
         colorable.setColor(NexoFurniture.furnitureDye(baseEntity));
      }

      item.unsetData((DataComponentType)DataComponentTypes.CUSTOM_NAME);
      item.editPersistentDataContainer((a0_ -> FurnitureBaseEntity.itemStack$lambda$0(this, a0_)));
      this.itemStack = item;
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            MechanicFactory mechanicFactory2 = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory2 = null;
            }

            it = (FurnitureFactory)mechanicFactory2;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
      if (furnitureFactory != null) {
         IFurniturePacketManager packetManager = furnitureFactory.packetManager();
         if (packetManager != null) {
            packetManager.sendFurnitureMetadataPacket(baseEntity, this.mechanic);
         }
      }

   }

   @NotNull
   public final ItemStack getItemStack() {
      return this.itemStack;
   }

   @NotNull
   public final UUID getBaseUuid() {
      return this.baseUuid;
   }

   public final int getBaseId() {
      return this.baseId;
   }

   @NotNull
   public final String getFurnitureId() {
      return this.furnitureId;
   }

   @Nullable
   public final ItemDisplay baseEntity() {
      return (ItemDisplay)Bukkit.getEntity(this.baseUuid);
   }

   @NotNull
   public final FurnitureMechanic mechanic() {
      return this.mechanic;
   }

   public boolean equals(@Nullable Object other) {
      return other instanceof FurnitureBaseEntity && Intrinsics.areEqual(this.baseUuid, ((FurnitureBaseEntity)other).baseUuid) && this.baseId == ((FurnitureBaseEntity)other).baseId && Intrinsics.areEqual(this.mechanic.getItemID(), ((FurnitureBaseEntity)other).mechanic().getItemID());
   }

   public int hashCode() {
      int result = this.baseId;
      result = 31 * result + this.mechanic.hashCode();
      result = 31 * result + this.itemStack.hashCode();
      result = 31 * result + this.baseUuid.hashCode();
      result = 31 * result + this.furnitureId.hashCode();
      return result;
   }

   private static final void itemStack$lambda$0(FurnitureBaseEntity furnitureBaseEntity, PersistentDataContainer pdc) {
      pdc.set(NexoItems.INSTANCE.getITEM_ID(), PersistentDataType.STRING, furnitureBaseEntity.mechanic.getItemID());
   }
}
