package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitCustom extends BukkitEntityType {
   private EntityType bukkitType = EntityType.ZOMBIE;
   private static final int HEIGHT = 1;
   private final String headString = "";
   private final ItemStack head = ItemFactory.of(Material.BARRIER).build();
   private String strMobType = null;

   @Override
   public void instantiate(MythicConfig mc) {
      this.strMobType = mc.getString("Type");
      this.strMobType = mc.getString("MobType", this.strMobType);
      this.strMobType = mc.getString("Mobtype", this.strMobType);

      try {
         this.bukkitType = EntityType.fromName(this.strMobType);
      } catch (Throwable e) {
         MythicLogger.error("Could not parse MythicMob of type {0}! Unknown Type specified.", this.strMobType);
      }
   }

   @Generated
   @Override
   public EntityType getBukkitType() {
      return this.bukkitType;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }

   @Generated
   @Override
   public ItemStack getHead() {
      return this.head;
   }
}
