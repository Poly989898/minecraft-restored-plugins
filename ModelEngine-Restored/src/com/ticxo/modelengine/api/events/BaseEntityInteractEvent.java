package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
public class BaseEntityInteractEvent extends AbstractEvent {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final BaseEntity<?> baseEntity;
   private final ActiveModel model;
   private final BaseEntityInteractEvent.Action action;
   private final EquipmentSlot slot;
   private final boolean isSecondary;
   private final ItemStack item;
   @Nullable
   private final Vector clickedPosition;
   @Nullable
   private final ModelBone hitBone;

   public BaseEntityInteractEvent(
      Player player,
      BaseEntity<?> baseEntity,
      ActiveModel model,
      BaseEntityInteractEvent.Action action,
      EquipmentSlot slot,
      boolean isSecondary,
      ItemStack item,
      @Nullable Vector clickedPosition
   ) {
      this(player, baseEntity, model, action, slot, isSecondary, item, clickedPosition, null);
   }

   public BaseEntityInteractEvent(
      Player player,
      BaseEntity<?> baseEntity,
      ActiveModel model,
      BaseEntityInteractEvent.Action action,
      EquipmentSlot slot,
      boolean isSecondary,
      ItemStack item,
      @Nullable Vector clickedPosition,
      @Nullable ModelBone hitBone
   ) {
      this.player = player;
      this.baseEntity = baseEntity;
      this.model = model;
      this.action = action;
      this.slot = slot;
      this.isSecondary = isSecondary;
      this.item = item;
      this.clickedPosition = clickedPosition;
      this.hitBone = hitBone;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public enum Action {
      ATTACK,
      INTERACT,
      INTERACT_ON;
   }
}
