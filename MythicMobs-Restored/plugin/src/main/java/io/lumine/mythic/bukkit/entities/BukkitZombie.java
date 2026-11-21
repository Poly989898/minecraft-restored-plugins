package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class BukkitZombie extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.ZOMBIE_HEAD).build();
   @Getter
   private double reinforcementChance = -1.0;
   @Getter
   private boolean preventConversion = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIE;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.reinforcementChance = mc.getDouble("Options.ReinforcementsChance", -1.0);
      this.preventConversion = mc.getBoolean("Options.PreventConversion", MythicBukkit.inst().getConfiguration().getPreventConversionByDefault());
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Zombie e = (Zombie)entity;
      e.setBaby(false);
      if (this.reinforcementChance >= 0.0) {
         e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      }

      if (this.preventConversion) {
         e.setConversionTime(Integer.MAX_VALUE);
      }

      return e;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
