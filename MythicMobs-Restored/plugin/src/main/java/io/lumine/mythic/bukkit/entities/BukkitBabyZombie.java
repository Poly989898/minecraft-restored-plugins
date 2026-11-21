package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class BukkitBabyZombie extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.ZOMBIE_HEAD).build();
   private double reinforcementChance = -1.0;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIE;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.reinforcementChance = mc.getDouble("Options.ReinforcementsChance", -1.0);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Zombie e = (Zombie)this.spawnEntity(location, EntityType.ZOMBIE, reason, prespawnFunc);
      e.setBaby(true);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Zombie e = (Zombie)entity;
      e.setBaby(true);
      if (this.reinforcementChance >= 0.0) {
         e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      }

      return e;
   }

   @Override
   public boolean compare(Entity e) {
      return isInstanceOf(e);
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof Zombie ? ((Zombie)e).isBaby() : false;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
