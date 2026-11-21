package com.nexomc.nexo.nms;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import kotlin.Metadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u0012\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/nms/EmptyFurniturePacketManager;", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "<init>", "()V", "getEntity", "Lorg/bukkit/entity/Entity;", "entityId", "", "sendHitboxEntityPacket", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "player", "Lorg/bukkit/entity/Player;", "removeHitboxEntityPacket", "sendBarrierHitboxPacket", "removeBarrierHitboxPacket", "com.nexomc:core"}
)
public final class EmptyFurniturePacketManager implements IFurniturePacketManager {
   @Nullable
   public Entity getEntity(int entityId) {
      return null;
   }

   public void sendHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   public void sendHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   public void removeHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   public void removeHitboxEntityPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   public void sendBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   public void sendBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }

   public void removeBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
   }

   public void removeBarrierHitboxPacket(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player) {
   }
}
