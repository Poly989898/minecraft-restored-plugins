package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitCreeper extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.CREEPER_HEAD).build();
   @Getter
   private boolean powered = false;
   @Getter
   private boolean preventSuicide = false;
   @Getter
   private int explosionFuseTicks = -1;
   @Getter
   private int explosionRadius = -1;

   @Override
   public EntityType getBukkitType() {
      return EntityType.CREEPER;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.powered = mc.getBoolean("Options.SuperCharged", false);
      this.preventSuicide = mc.getBoolean("Options.PreventSuicide", false);
      this.explosionFuseTicks = mc.getInteger("Options.FuseTicks", -1);
      this.explosionRadius = mc.getInteger("Options.ExplosionRadius", -1);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Creeper e = (Creeper)entity;
      if (this.powered) {
         e.setPowered(true);
      }

      if (this.explosionFuseTicks >= 0) {
         e.setMaxFuseTicks(this.explosionFuseTicks);
      }

      if (this.explosionRadius >= 0) {
         e.setExplosionRadius(this.explosionRadius);
      }

      return e;
   }

   public boolean preventSuicide() {
      return this.preventSuicide;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
