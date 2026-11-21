package com.nexomc.nexo.mechanics.furniture.compatibility;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.protectionlib.ProtectionLib;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent.Action;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/compatibility/ModelEngineCompatibility;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onMegFurnitureInteract", "", "Lcom/ticxo/modelengine/api/events/BaseEntityInteractEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class ModelEngineCompatibility implements Listener {
   @EventHandler
   public final void onMegFurnitureInteract(@NotNull BaseEntityInteractEvent baseEntityInteractEvent) {
      EquipmentSlot slot = baseEntityInteractEvent.getSlot();
      ItemStack itemStack2 = (slot == null ? -1 : ModelEngineCompatibility.WhenMappings.$EnumSwitchMapping$0[slot.ordinal()]) == 1 ? baseEntityInteractEvent.getPlayer().getInventory().getItemInMainHand() : baseEntityInteractEvent.getPlayer().getInventory().getItemInOffHand();
      ItemStack itemStack = itemStack2;
      Vector clickedPosition = baseEntityInteractEvent.getClickedPosition();
      Location interactionPoint = clickedPosition != null ? clickedPosition.toLocation(baseEntityInteractEvent.getPlayer().getWorld()) : null;
      Object original = baseEntityInteractEvent.getBaseEntity().getOriginal();
      ItemDisplay itemDisplay = original instanceof ItemDisplay ? (ItemDisplay)original : null;
      if ((original instanceof ItemDisplay ? (ItemDisplay)original : null) != null) {
         ItemDisplay baseEntity = itemDisplay;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            if (baseEntityInteractEvent.getAction() == Action.ATTACK && baseEntityInteractEvent.getPlayer().getGameMode() != GameMode.ADVENTURE) {
               Player player = baseEntityInteractEvent.getPlayer();
               Location location = baseEntity.getLocation();
               if (ProtectionLib.canBreak(player, location)) {
                  EventUtils.INSTANCE.call((Event)(new BlockBreakEvent(baseEntity.getLocation().getBlock(), baseEntityInteractEvent.getPlayer())), (a0_ -> ModelEngineCompatibility.onMegFurnitureInteract$lambda$0(mechanic, baseEntity, (BlockBreakEvent)a0_)));
                  return;
               }
            }

            if (baseEntityInteractEvent.getAction() != Action.ATTACK) {
               Player player2 = baseEntityInteractEvent.getPlayer();
               Location location2 = baseEntity.getLocation();
               if (ProtectionLib.canInteract(player2, location2) && baseEntityInteractEvent.getClickedPosition() != null) {
                  EventUtils eventUtils = EventUtils.INSTANCE;
                  Player player3 = baseEntityInteractEvent.getPlayer();
                  EquipmentSlot equipmentSlot = baseEntityInteractEvent.getSlot();
                  eventUtils.call(new NexoFurnitureInteractEvent(mechanic, baseEntity, player3, itemStack, equipmentSlot, interactionPoint, org.bukkit.event.Event.Result.DEFAULT, org.bukkit.event.Event.Result.DEFAULT, null));
               }
            }

         }
      }
   }

   private static final Unit onMegFurnitureInteract$lambda$0$0(ItemDisplay baseEntity, NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      NexoFurniture.remove((Entity)((Entity)baseEntity), nexoFurnitureBreakEvent.getPlayer(), (Drop)null);
      return Unit.INSTANCE;
   }

   private static final Unit onMegFurnitureInteract$lambda$0(FurnitureMechanic mechanic, ItemDisplay baseEntity, BlockBreakEvent blockBreakEvent) {
      EventUtils eventUtils = EventUtils.INSTANCE;
      Player player = blockBreakEvent.getPlayer();
      eventUtils.call(new NexoFurnitureBreakEvent(mechanic, baseEntity, player), (a0_ -> ModelEngineCompatibility.onMegFurnitureInteract$lambda$0$0(baseEntity, (NexoFurnitureBreakEvent)a0_)));
      return Unit.INSTANCE;
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.inventory.EquipmentSlot.values().length];
         try { a_0[org.bukkit.inventory.EquipmentSlot.HAND.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
