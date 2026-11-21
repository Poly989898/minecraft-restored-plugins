package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.inventory.ItemStack;

public class BukkitBabyZombieVillager extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.ZOMBIE_HEAD).build();
   private double reinforcementChance = -1.0;
   private Type villagerType;
   private Profession villagerProfession;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIE_VILLAGER;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.reinforcementChance = mc.getDouble("Options.ReinforcementsChance", -1.0);
      String prof = mc.getNamespacedString("Options.Profession", "nitwit");
      this.villagerProfession = Registry.VILLAGER_PROFESSION.get(NamespacedKey.fromString(prof));
      if (prof != null && this.villagerProfession == null) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid villager profession configured");
      }

      String type = mc.getNamespacedString("Options.Type", "plains");
      this.villagerType = Registry.VILLAGER_TYPE.get(NamespacedKey.fromString(type));
      if (type != null && this.villagerType == null) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid villager type configured");
      }
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      ZombieVillager e = (ZombieVillager)this.spawnEntity(location, EntityType.ZOMBIE_VILLAGER, reason, prespawnFunc);
      e.setBaby(true);
      e.setVillagerProfession(this.villagerProfession);
      e.setVillagerType(this.villagerType);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      ZombieVillager e = (ZombieVillager)entity;
      e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      return entity;
   }

   @Override
   public boolean compare(Entity e) {
      return isInstanceOf(e);
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof ZombieVillager ? ((ZombieVillager)e).isBaby() : false;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
