package com.nexomc.nexo.mechanics.furniture.evolution;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.drops.Drop;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.random.Random;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0002\b\u0007¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolutionListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onBoneMeal", "", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onFarmland", "Lorg/bukkit/event/entity/EntityChangeBlockEvent;", "com.nexomc:core"}
)
public final class EvolutionListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onBoneMeal(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (nexoFurnitureInteractEvent.getHand() == EquipmentSlot.HAND && nexoFurnitureInteractEvent.getMechanic().getHasEvolution()) {
         if (nexoFurnitureInteractEvent.getBaseEntity().getPersistentDataContainer().has(FurnitureMechanic.Companion.getEVOLUTION_KEY(), DataType.INTEGER)) {
            if (nexoFurnitureInteractEvent.getItemInHand().getType() == Material.BONE_MEAL) {
               nexoFurnitureInteractEvent.setCancelled(true);
               EvolvingFurniture evolvingFurniture = nexoFurnitureInteractEvent.getMechanic().getEvolution();
               if (evolvingFurniture != null) {
                  EvolvingFurniture it = evolvingFurniture;
                  int value = 0;
                  evolvingFurniture = it.getNextStage() != null && it.getBoneMealChance() > 0 ? it : null;
                  if (evolvingFurniture != null) {
                     EvolvingFurniture evolution = evolvingFurniture;
                     MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
                     Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

                     while(true) {
                        if (iterator.hasNext()) {
                           MechanicFactory mechanicFactory3 = (MechanicFactory)iterator.next();
                           int value2 = 0;
                           MechanicFactory mechanicFactory2 = mechanicFactory3;
                           if (!(mechanicFactory3 instanceof FurnitureFactory)) {
                              mechanicFactory2 = null;
                           }

                           mechanicFactory3 = (FurnitureFactory)mechanicFactory2;
                           if (mechanicFactory3 == null) {
                              continue;
                           }

                           mechanicFactory = mechanicFactory3;
                           break;
                        }

                        mechanicFactory = null;
                        break;
                     }

                     FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
                     if (furnitureFactory != null) {
                        FurnitureMechanic mechanic = furnitureFactory.getMechanic(evolution.getNextStage());
                        if (mechanic != null) {
                           FurnitureMechanic nextMechanic = mechanic;
                           nexoFurnitureInteractEvent.getItemInHand().subtract();
                           nexoFurnitureInteractEvent.getBaseEntity().getWorld().playEffect(nexoFurnitureInteractEvent.getBaseEntity().getLocation(), Effect.BONE_MEAL_USE, 3);
                           if (Random.Default.nextDouble() > (double)evolution.getBoneMealChance()) {
                              return;
                           }

                           NexoFurniture.furnitureItem(nexoFurnitureInteractEvent.getBaseEntity(), nextMechanic.placedItem(nexoFurnitureInteractEvent.getBaseEntity()).build());
                           nexoFurnitureInteractEvent.getBaseEntity().getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_KEY(), DataType.STRING, "nexo:" + nextMechanic.getItemID());
                           if (nextMechanic.getHasEvolution()) {
                              nexoFurnitureInteractEvent.getBaseEntity().getPersistentDataContainer().set(FurnitureMechanic.Companion.getEVOLUTION_KEY(), DataType.INTEGER, 0);
                           }

                           return;
                        }
                     }

                     return;
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public final void onFarmland(@NotNull EntityChangeBlockEvent entityChangeBlockEvent) {
      if (entityChangeBlockEvent.getBlock().getType() == Material.FARMLAND && entityChangeBlockEvent.getTo() != Material.FARMLAND) {
         Block relative = entityChangeBlockEvent.getBlock().getRelative(BlockFace.UP);
         Block furnitureBlock = relative;
         ItemDisplay itemDisplay = NexoFurniture.baseEntity(furnitureBlock);
         if (itemDisplay != null) {
            ItemDisplay baseEntity = itemDisplay;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
            if (furnitureMechanic != null ? furnitureMechanic.getFarmlandRequired() : false) {
               Entity entity = (Entity)baseEntity;
               Entity entity2 = entityChangeBlockEvent.getEntity();
               NexoFurniture.remove((Entity)entity, entity2 instanceof Player ? (Player)entity2 : null, (Drop)null);
            }
         }
      }
   }
}
