package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;

public class BukkitExperienceOrb extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.EXPERIENCE_BOTTLE).build();
   @Getter
   private PlaceholderInt amount;

   @Override
   public EntityType getBukkitType() {
      return EntityType.EXPERIENCE_ORB;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      String exp = mc.getString("Options.Experience", "1");

      try {
         this.amount = PlaceholderInt.of(exp);
      } catch (Exception e) {
         MythicLogger.errorGenericConfig("Invalid experience amount specified");
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      ExperienceOrb xp = (ExperienceOrb)entity;
      xp.setExperience(this.amount.get());
      return xp;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
