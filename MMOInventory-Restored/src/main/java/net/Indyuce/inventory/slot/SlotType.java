package net.Indyuce.inventory.slot;

import io.lumine.mythic.lib.MythicLib;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum SlotType {
   HELMET(new VanillaSlotHandler() {
      @Override
      public void equip(Player player, ItemStack item) {
         player.getInventory().setHelmet(item);
      }

      @Override
      public boolean canEquip(ItemStack item) {
         return MythicLib.plugin.getVersion().getWrapper().isHelmet(item.getType());
      }

      @Override
      public ItemStack retrieveItem(Player player) {
         return player.getInventory().getHelmet();
      }

      @Override
      public boolean supportsShiftClick() {
         return true;
      }
   }),
   CHESTPLATE(new VanillaSlotHandler() {
      @Override
      public void equip(Player player, ItemStack item) {
         player.getInventory().setChestplate(item);
      }

      @Override
      public boolean canEquip(ItemStack item) {
         return item.getType().name().endsWith("_CHESTPLATE") || item.getType() == Material.ELYTRA;
      }

      @Override
      public ItemStack retrieveItem(Player player) {
         return player.getInventory().getChestplate();
      }

      @Override
      public boolean supportsShiftClick() {
         return true;
      }
   }),
   LEGGINGS(new VanillaSlotHandler() {
      @Override
      public void equip(Player player, ItemStack item) {
         player.getInventory().setLeggings(item);
      }

      @Override
      public boolean canEquip(ItemStack item) {
         return item != null && item.getType().name().endsWith("_LEGGINGS");
      }

      @Override
      public ItemStack retrieveItem(Player player) {
         return player.getInventory().getLeggings();
      }

      @Override
      public boolean supportsShiftClick() {
         return true;
      }
   }),
   BOOTS(new VanillaSlotHandler() {
      @Override
      public void equip(Player player, ItemStack item) {
         player.getInventory().setBoots(item);
      }

      @Override
      public boolean canEquip(ItemStack item) {
         return item != null && item.getType().name().endsWith("BOOTS");
      }

      @Override
      public ItemStack retrieveItem(Player player) {
         return player.getInventory().getBoots();
      }

      @Override
      public boolean supportsShiftClick() {
         return true;
      }
   }),
   OFF_HAND(new VanillaSlotHandler() {
      @Override
      public void equip(Player player, ItemStack item) {
         player.getInventory().setItemInOffHand(item);
      }

      @Override
      public boolean canEquip(ItemStack item) {
         return true;
      }

      @Override
      public ItemStack retrieveItem(Player player) {
         return player.getInventory().getItemInOffHand();
      }

      @Override
      public boolean supportsShiftClick() {
         return false;
      }
   }),
   ACCESSORY(null),
   FILL(null);

   private final VanillaSlotHandler vanilla;

   SlotType(VanillaSlotHandler vanilla) {
      this.vanilla = vanilla;
   }

   public boolean isCustom() {
      Validate.isTrue(this != FILL, "FILL is not an item slot");
      return this == ACCESSORY;
   }

   public VanillaSlotHandler getVanillaSlotHandler() {
      return this.vanilla;
   }
}
