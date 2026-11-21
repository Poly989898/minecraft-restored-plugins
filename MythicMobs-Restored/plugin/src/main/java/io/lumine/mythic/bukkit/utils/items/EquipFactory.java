package io.lumine.mythic.bukkit.utils.items;

import org.bukkit.entity.Player;

public class EquipFactory {
   private ItemFactory helmet;
   private ItemFactory chestplate;
   private ItemFactory leggings;
   private ItemFactory boots;
   private ItemFactory weapon;
   private ItemFactory offhand;

   public static EquipFactory create() {
      return new EquipFactory();
   }

   public EquipFactory helmet(ItemFactory factory) {
      this.helmet = factory;
      return this;
   }

   public EquipFactory chest(ItemFactory factory) {
      this.chestplate = factory;
      return this;
   }

   public EquipFactory leggings(ItemFactory factory) {
      this.leggings = factory;
      return this;
   }

   public EquipFactory boots(ItemFactory factory) {
      this.boots = factory;
      return this;
   }

   public EquipFactory weapon(ItemFactory factory) {
      this.weapon = factory;
      return this;
   }

   public EquipFactory offhand(ItemFactory factory) {
      this.offhand = factory;
      return this;
   }

   public void equip(Player player) {
      if (this.helmet != null) {
         player.getInventory().setHelmet(this.helmet.build());
      }

      if (this.chestplate != null) {
         player.getInventory().setChestplate(this.chestplate.build());
      }

      if (this.leggings != null) {
         player.getInventory().setLeggings(this.leggings.build());
      }

      if (this.boots != null) {
         player.getInventory().setBoots(this.boots.build());
      }

      if (this.weapon != null) {
         player.getInventory().setItemInMainHand(this.weapon.build());
      }

      if (this.offhand != null) {
         player.getInventory().setItemInOffHand(this.offhand.build());
      }
   }
}
