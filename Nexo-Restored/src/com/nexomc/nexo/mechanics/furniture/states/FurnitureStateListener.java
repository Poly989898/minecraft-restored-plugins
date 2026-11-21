package com.nexomc.nexo.mechanics.furniture.states;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.SchedulerUtils;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStateListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onInteract", "", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "Lorg/bukkit/event/EventHandler;", "onAdd", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "com.nexomc:core"}
)
public final class FurnitureStateListener implements Listener {
   @EventHandler
   public final void onInteract(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      if (nexoFurnitureInteractEvent.getHand() == EquipmentSlot.HAND) {
         FurnitureMechanic furnitureMechanic;
         if (nexoFurnitureInteractEvent.getMechanic() instanceof MechanicState.FurnitureStateMechanic) {
            String mechanicId = (String)nexoFurnitureInteractEvent.getBaseEntity().getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING);
            furnitureMechanic = NexoFurniture.furnitureMechanic(mechanicId);
            if (furnitureMechanic == null) {
               return;
            }
         } else {
            furnitureMechanic = nexoFurnitureInteractEvent.getMechanic();
         }

         FurnitureMechanic mechanic = furnitureMechanic;
         FurnitureStates furnitureStates = mechanic.getStates();
         if (furnitureStates != null) {
            FurnitureStates states = furnitureStates;
            if (!states.canCycle(nexoFurnitureInteractEvent.getPlayer())) {
               nexoFurnitureInteractEvent.setCanCycleState(Result.DENY);
            }

         }
      }
   }

   @EventHandler
   public final void onAdd(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      Entity entity = entityAddToWorldEvent.getEntity();
      ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         ItemDisplay baseEntity = itemDisplay;
         FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            FurnitureStates states = mechanic.getStates();
            if (states != null) {
               FurnitureState furnitureState = states.currentState(baseEntity);
               if (furnitureState != null) {
                  FurnitureState it = furnitureState;
                  int value = 0;
                  furnitureState = !(it instanceof EmptyState) ? it : null;
                  if ((!(it instanceof EmptyState) ? it : null) != null) {
                     FurnitureState state = furnitureState;
                     SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, 4L, (a0_ -> FurnitureStateListener.onAdd$lambda$1(state, baseEntity, (Entity)a0_)));
                     return;
                  }
               }
            }

         }
      }
   }

   private static final Unit onAdd$lambda$1(FurnitureState state, ItemDisplay baseEntity, Entity it) {
      state.setState(baseEntity);
      return Unit.INSTANCE;
   }
}
