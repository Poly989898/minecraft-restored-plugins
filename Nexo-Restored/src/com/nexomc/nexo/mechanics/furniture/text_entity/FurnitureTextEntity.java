package com.nexomc.nexo.mechanics.furniture.text_entity;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Collection;
import java.util.UUID;
import kotlin.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u001e\n\u0002\b\t\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fB'\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f¢\u0006\u0004\b\u000b\u0010\u0010J\b\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u0006\u0010\u0002\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/text_entity/FurnitureTextEntity;", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "baseUuid", "Ljava/util/UUID;", "baseId", "", "worldUID", "entityIds", "Lit/unimi/dsi/fastutil/ints/IntArrayList;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Ljava/util/UUID;ILjava/util/UUID;Lit/unimi/dsi/fastutil/ints/IntArrayList;)V", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lorg/bukkit/entity/ItemDisplay;Ljava/util/Collection;)V", "getBaseUuid", "()Ljava/util/UUID;", "getBaseId", "()I", "getWorldUID", "getEntityIds", "()Lit/unimi/dsi/fastutil/ints/IntArrayList;", "com.nexomc:core"}
)
public final class FurnitureTextEntity {
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final UUID baseUuid;
   private final int baseId;
   @NotNull
   private final UUID worldUID;
   @NotNull
   private final IntArrayList entityIds;

   public FurnitureTextEntity(@NotNull FurnitureMechanic mechanic, @NotNull UUID baseUuid, int baseId, @NotNull UUID worldUID, @NotNull IntArrayList entityIds) {
      super();
      this.mechanic = mechanic;
      this.baseUuid = baseUuid;
      this.baseId = baseId;
      this.worldUID = worldUID;
      this.entityIds = entityIds;
   }

   @NotNull
   public final UUID getBaseUuid() {
      return this.baseUuid;
   }

   public final int getBaseId() {
      return this.baseId;
   }

   @NotNull
   public final UUID getWorldUID() {
      return this.worldUID;
   }

   @NotNull
   public final IntArrayList getEntityIds() {
      return this.entityIds;
   }

   public FurnitureTextEntity(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Collection<Integer> entityIds) {
      UUID uniqueId = baseEntity.getUniqueId();
      int entityId = baseEntity.getEntityId();
      UUID uid = baseEntity.getWorld().getUID();
      IntArrayList intArrayList = entityIds instanceof IntArrayList ? (IntArrayList)entityIds : null;
      if ((entityIds instanceof IntArrayList ? (IntArrayList)entityIds : null) == null) {
         intArrayList = new IntArrayList(entityIds);
      }

      this(mechanic, uniqueId, entityId, uid, intArrayList);
   }

   @Nullable
   public final ItemDisplay baseEntity() {
      Entity entity = Bukkit.getEntity(this.baseUuid);
      return entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
   }

   @NotNull
   public final FurnitureMechanic mechanic() {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(this.mechanic.getSection().getName());
      if (furnitureMechanic == null) {
         furnitureMechanic = this.mechanic;
      }

      return furnitureMechanic;
   }
}
