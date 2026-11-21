package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.MerchantProperty;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;

@Getter
public class BukkitVillager extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private MerchantProperty merchantProperty;
   private Type villagerType;
   private Profession villagerProfession;

   @Override
   public EntityType getBukkitType() {
      return EntityType.VILLAGER;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.merchantProperty = new MerchantProperty(mc);
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
   public Entity applyOptions(Entity entity) {
      Villager e = (Villager)entity;
      if (this.villagerType != null) {
         e.setVillagerType(this.villagerType);
      }

      if (this.villagerProfession != null) {
         e.setProfession(this.villagerProfession);
      } else if (this.merchantProperty.hasTrades()) {
         e.setProfession(Profession.NITWIT);
      }

      this.ageableProperty.applyProperties(entity);
      this.merchantProperty.applyProperties(entity);
      return e;
   }
}
