package net.Indyuce.mmoitems.comp.inventory;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultPlayerInventory implements PlayerInventory {
   @NotNull
   @Override
   public List<EquippedItem> getInventory(Player var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getEquipment() == null) {
         return var2;
      }

      var2.add(new SlotEquippedItem(var1, -7, var1.getEquipment().getItemInMainHand(), EquipmentSlot.MAIN_HAND));
      var2.add(new SlotEquippedItem(var1, -106, var1.getEquipment().getItemInOffHand(), EquipmentSlot.OFF_HAND));
      var2.add(new SlotEquippedItem(var1, 103, var1.getEquipment().getHelmet(), EquipmentSlot.ARMOR));
      var2.add(new SlotEquippedItem(var1, 102, var1.getEquipment().getChestplate(), EquipmentSlot.ARMOR));
      var2.add(new SlotEquippedItem(var1, 101, var1.getEquipment().getLeggings(), EquipmentSlot.ARMOR));
      var2.add(new SlotEquippedItem(var1, 100, var1.getEquipment().getBoots(), EquipmentSlot.ARMOR));
      return var2;
   }
}
