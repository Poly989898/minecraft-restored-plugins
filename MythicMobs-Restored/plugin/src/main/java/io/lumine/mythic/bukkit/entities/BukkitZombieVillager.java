package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;

public class BukkitZombieVillager extends BukkitEntityType {
   private double reinforcementChance = -1.0;
   private Profession villagerProfession;
   private Type villagerType;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOMBIE_VILLAGER;
   }

   @Override
   public int getHeight() {
      return 2;
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
      this.villagerType = Registry.VILLAGER_TYPE.get(NamespacedKey.fromString(prof));
      if (type != null && this.villagerType == null) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid villager type configured");
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      ZombieVillager e = (ZombieVillager)entity;
      e.setBaby(false);
      if (this.villagerProfession != null) {
         e.setVillagerProfession(this.villagerProfession);
      }

      if (this.villagerType != null) {
         e.setVillagerType(this.villagerType);
      }

      if (this.reinforcementChance >= 0.0) {
         e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      }

      return e;
   }
}
