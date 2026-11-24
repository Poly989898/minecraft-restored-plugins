package io.lumine.mythic.lib.api.stat.provider;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class EntityStatProvider implements StatProvider {
   private final Set<NBTItem> equipment = new HashSet<>();
   private final LivingEntity entity;

   public EntityStatProvider(LivingEntity entity) {
      this.entity = entity;
      EntityEquipment equip = entity.getEquipment();
      if (equip != null) {
         for (ItemStack equipped : entity.getEquipment().getArmorContents()) {
            this.registerItem(equipped);
         }

         this.registerItem(entity.getEquipment().getItemInMainHand());
         this.registerItem(entity.getEquipment().getItemInOffHand());
      }
   }

   @Override
   public LivingEntity getEntity() {
      return this.entity;
   }

   private void registerItem(ItemStack item) {
      if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
         this.equipment.add(NBTItem.get(item));
      }
   }

   @Override
   public double getStat(String id) {
      double d = 0.0;

      for (NBTItem nbt : this.equipment) {
         d += nbt.getStat(id);
      }

      return d;
   }
}
