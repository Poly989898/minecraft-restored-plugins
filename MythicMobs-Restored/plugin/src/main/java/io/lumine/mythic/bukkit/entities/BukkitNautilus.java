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
import org.bukkit.entity.Nautilus;
import org.bukkit.inventory.ArmoredSaddledMountInventory;
import org.bukkit.inventory.ItemStack;

public class BukkitNautilus extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   @Getter
   private TameableProperty tameableProperty;
   private boolean saddled;
   private boolean tamed;

   @Override
   public EntityType getBukkitType() {
      return EntityType.NAUTILUS;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.CACTUS, new Material[0]));
      this.saddled = mc.getBoolean("Options.Saddled", false);
      this.tamed = mc.getBoolean("Options.Tamed", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Nautilus mob = (Nautilus)entity;
      this.ageableProperty.applyProperties(entity);
      ArmoredSaddledMountInventory inventory = mob.getInventory();
      if (this.saddled) {
         inventory.setSaddle(new ItemStack(Material.SADDLE));
      }

      if (this.tamed) {
         mob.setTamed(true);
      }

      return mob;
   }
}
