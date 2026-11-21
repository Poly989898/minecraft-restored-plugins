package com.nexomc.nexo.mechanics.repair;

import io.papermc.paper.datacomponent.DataComponentTypes;
import kotlin.Metadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t*\u00020\nH\u0007b\u0002\b\u000bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/repair/RepairMechanicListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/repair/RepairMechanicFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/repair/RepairMechanicFactory;)V", "getFactory", "()Lcom/nexomc/nexo/mechanics/repair/RepairMechanicFactory;", "onRepairItem", "", "Lorg/bukkit/event/inventory/InventoryClickEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class RepairMechanicListener implements Listener {
   @NotNull
   private final RepairMechanicFactory factory;

   public RepairMechanicListener(@NotNull RepairMechanicFactory factory) {
      super();
      this.factory = factory;
   }

   @NotNull
   public final RepairMechanicFactory getFactory() {
      return this.factory;
   }

   @EventHandler
   public final void onRepairItem(@NotNull InventoryClickEvent inventoryClickEvent) {
      boolean flag;
      RepairMechanicFactory it = this.factory;
      int value = 0;
      RepairMechanicFactory repairMechanicFactory = !it.isNexoDurabilityOnly() ? it : null;
      if (repairMechanicFactory != null) {
         RepairMechanic mechanic = repairMechanicFactory.getMechanic(inventoryClickEvent.getCursor());
         if (mechanic != null) {
            RepairMechanic repairMechanic = mechanic;
            ItemStack currentItem = inventoryClickEvent.getCurrentItem();
            if (currentItem == null) {
               return;
            }

            ItemStack toRepair;
            Integer number;
            block: {
               toRepair = currentItem;
               number = toRepair.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
               Integer it_3 = (Integer)number;
               int value2 = 0;
               if (it_3 != null) {
                  if (it_3 == 0) {
                     flag = true;
                     break block;
                  }
               }

               flag = false;
            }

            Integer number2 = (Integer)(!flag ? number : null);
            if (number2 != null) {
               int it_2 = number2;
               toRepair.setData(DataComponentTypes.DAMAGE, repairMechanic.finalDamage(toRepair.getType().getMaxDurability(), it_2));
               inventoryClickEvent.setCancelled(true);
               ItemStack it_1 = inventoryClickEvent.getCursor();
               it_1.setAmount(it_1.getAmount() - 1);
               return;
            }

            return;
         }
      }

   }
}
