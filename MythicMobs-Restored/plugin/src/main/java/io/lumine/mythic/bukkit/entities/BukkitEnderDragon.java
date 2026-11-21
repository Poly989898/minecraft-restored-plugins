package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.inventory.ItemStack;

public class BukkitEnderDragon extends BukkitEntityType {
   @Getter
   private EntityType bukkitType = EntityType.ENDER_DRAGON;
   private static final int HEIGHT = 6;
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.DRAGON_HEAD).build();

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      EnderDragon dragon = (EnderDragon)this.spawnEntity(location, EntityType.ENDER_DRAGON, reason, prespawnFunc);
      dragon.setPhase(Phase.STRAFING);
      return dragon;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      EnderDragon dragon = (EnderDragon)entity;
      return entity;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
