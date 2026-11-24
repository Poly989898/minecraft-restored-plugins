package me.TechsCode.UltraPermissions.base.legacy.clickGUI;

import me.TechsCode.UltraPermissions.base.item.CustomItem;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public abstract class FloatingItem extends FloatingElement {
   public FloatingItem() {
   }

   public abstract CustomItem getItem();

   @Override
   public void apply(ArmorStand armorStand) {
      CustomItem item = this.getItem();
      this.setShown(item != null);
      if (armorStand != null && item != null) {
         ItemStack stack = item.get();
         if (armorStand.getPassenger() == null || !((Item)armorStand.getPassenger()).getItemStack().isSimilar(stack)) {
            if (armorStand.getPassenger() != null) {
               armorStand.getPassenger().remove();
            }

            Item droppedItem = armorStand.getLocation().getWorld().dropItem(armorStand.getLocation(), stack);
            droppedItem.setGravity(false);
            droppedItem.setPickupDelay(2147483647);
            armorStand.setPassenger(droppedItem);
         }
      }
   }

   @Override
   public double getYOffset() {
      return 2.0;
   }
}
