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
   public List<EquippedItem> getInventory(Player player) {
      ArrayList list = new ArrayList();
      if (player.getEquipment() == null) {
         return list;
      }

      list.add(new SlotEquippedItem(player, -7, player.getEquipment().getItemInMainHand(), EquipmentSlot.MAIN_HAND));
      list.add(new SlotEquippedItem(player, -106, player.getEquipment().getItemInOffHand(), EquipmentSlot.OFF_HAND));
      list.add(new SlotEquippedItem(player, 103, player.getEquipment().getHelmet(), EquipmentSlot.ARMOR));
      list.add(new SlotEquippedItem(player, 102, player.getEquipment().getChestplate(), EquipmentSlot.ARMOR));
      list.add(new SlotEquippedItem(player, 101, player.getEquipment().getLeggings(), EquipmentSlot.ARMOR));
      list.add(new SlotEquippedItem(player, 100, player.getEquipment().getBoots(), EquipmentSlot.ARMOR));
      return list;
   }
}
