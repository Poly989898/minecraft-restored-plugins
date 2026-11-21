package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mule;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BukkitMule extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   private TameableProperty tameableProperty;
   private boolean horseChest;
   private boolean horseSaddled;
   private boolean horseTamed;

   @Override
   public EntityType getBukkitType() {
      return EntityType.MULE;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(
         mc,
         Sets.immutableEnumSet(
            Material.APPLE, new Material[]{Material.GOLDEN_APPLE, Material.SUGAR, Material.WHEAT, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_CARROT}
         )
      );
      this.horseChest = mc.getBoolean("Options.HorseCarryingChest", false);
      this.horseChest = mc.getBoolean("Options.CarryingChest", this.horseChest);
      this.horseSaddled = mc.getBoolean("Options.HorseSaddled", false);
      this.horseSaddled = mc.getBoolean("Options.Saddled", this.horseSaddled);
      this.horseTamed = mc.getBoolean("Options.HorseTamed", false);
      this.horseTamed = mc.getBoolean("Options.Tamed", this.horseTamed);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Mule e = (Mule)entity;
      this.ageableProperty.applyProperties(entity);
      AbstractHorseInventory hi = e.getInventory();
      if (this.horseTamed) {
         e.setTamed(true);
      }

      if (this.horseSaddled) {
         hi.setSaddle(new ItemStack(Material.SADDLE, 1, (short)0));
      }

      if (this.horseChest) {
         e.setCarryingChest(true);
      }

      return e;
   }
}
