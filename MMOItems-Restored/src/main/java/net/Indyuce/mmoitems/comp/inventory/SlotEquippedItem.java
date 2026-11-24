package net.Indyuce.mmoitems.comp.inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotEquippedItem extends EquippedItem {
   private final Player player;
   private final int slotNumber;

   public SlotEquippedItem(@NotNull Player player, int count, ItemStack item, EquipmentSlot equipmentSlot) {
      super(item, equipmentSlot);
      this.player = player;
      this.slotNumber = count;
   }

   public SlotEquippedItem(@NotNull Player player, int count, NBTItem nbtItem, EquipmentSlot equipmentSlot) {
      super(nbtItem, equipmentSlot);
      this.player = player;
      this.slotNumber = count;
   }

   @NotNull
   public Player getPlayer() {
      return this.player;
   }

   @NotNull
   public int getSlotNumber() {
      return this.slotNumber;
   }

   @Override
   public void setItem(@Nullable ItemStack item) {
      switch (this.getSlotNumber()) {
         case -106:
            this.getPlayer().getInventory().setItemInOffHand(item);
            break;
         case -7:
            this.getPlayer().getInventory().setItemInMainHand(item);
            break;
         case 100:
            this.getPlayer().getInventory().setBoots(item);
            break;
         case 101:
            this.getPlayer().getInventory().setLeggings(item);
            break;
         case 102:
            this.getPlayer().getInventory().setChestplate(item);
            break;
         case 103:
            this.getPlayer().getInventory().setHelmet(item);
            break;
         default:
            this.getPlayer().getInventory().setItem(this.getSlotNumber(), item);
      }
   }
}
