package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

public class BukkitItem extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private AbstractItemStack item = MythicBukkit.inst().getBootstrap().createItem("stone");
   @Getter
   private int amount = 1;
   @Getter
   private boolean canPickup = true;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ITEM;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      String mat = mc.getString("Options.Item", "STONE");
      MythicBukkit.inst().getClock().queueAfterLoad(() -> {
         try {
            this.item = MythicBukkit.inst().getBootstrap().createItem(mat);
         } catch (Exception e) {
            MythicLogger.errorGenericConfig("Invalid item entity material specified");
            e.printStackTrace();
         }
      });
      this.amount = mc.getInteger("Options.Amount", 1);
      this.canPickup = mc.getBoolean("Options.CanPickup", true);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      return location.getWorld().dropItem(location, BukkitAdapter.adapt(this.item));
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Item item = (Item)entity;
      item.setCanMobPickup(this.canPickup);
      item.setCanPlayerPickup(this.canPickup);
      return item;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
