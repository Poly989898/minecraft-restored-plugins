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

   public SlotEquippedItem(@NotNull Player var1, int var2, ItemStack var3, EquipmentSlot var4) {
      super(var3, var4);
      this.player = var1;
      this.slotNumber = var2;
   }

   public SlotEquippedItem(@NotNull Player var1, int var2, NBTItem var3, EquipmentSlot var4) {
      super(var3, var4);
      this.player = var1;
      this.slotNumber = var2;
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
   public void setItem(@Nullable ItemStack var1) {
      switch (this.getSlotNumber()) {
         case -106:
            this.getPlayer().getInventory().setItemInOffHand(var1);
            break;
         case -7:
            this.getPlayer().getInventory().setItemInMainHand(var1);
            break;
         case 100:
            this.getPlayer().getInventory().setBoots(var1);
            break;
         case 101:
            this.getPlayer().getInventory().setLeggings(var1);
            break;
         case 102:
            this.getPlayer().getInventory().setChestplate(var1);
            break;
         case 103:
            this.getPlayer().getInventory().setHelmet(var1);
            break;
         default:
            this.getPlayer().getInventory().setItem(this.getSlotNumber(), var1);
      }
   }
}
