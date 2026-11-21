package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BukkitSkeletonHorse extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private boolean horseSaddled;
   private boolean horseTamed;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SKELETON_HORSE;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.horseSaddled = mc.getBoolean("Options.HorseSaddled", false);
      this.horseSaddled = mc.getBoolean("Options.Saddled", this.horseSaddled);
      this.horseTamed = mc.getBoolean("Options.HorseTamed", false);
      this.horseTamed = mc.getBoolean("Options.Tamed", this.horseTamed);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      SkeletonHorse e = (SkeletonHorse)entity;
      this.ageableProperty.applyProperties(entity);
      AbstractHorseInventory hi = e.getInventory();
      e.setTamed(this.horseTamed);
      if (this.horseSaddled) {
         hi.setSaddle(new ItemStack(Material.SADDLE, 1, (short)0));
      }

      return e;
   }
}
