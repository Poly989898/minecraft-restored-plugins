package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;

@Getter
public class BukkitTropicalFish extends BukkitEntityType {
   private Pattern pattern = null;
   private DyeColor bodyColor = null;
   private DyeColor patternColor = null;

   @Override
   public EntityType getBukkitType() {
      return EntityType.TROPICAL_FISH;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      String strPattern = mc.getString("Options.Pattern", null);
      String strBodyColor = mc.getString("Options.BodyColor", null);
      String strPatternColor = mc.getString("Options.PatternColor", null);
      if (strPattern != null) {
         try {
            this.pattern = Pattern.valueOf(strPattern.toUpperCase());
         } catch (Exception e) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Pattern specified");
         }
      }

      if (strBodyColor != null) {
         try {
            this.bodyColor = DyeColor.valueOf(strBodyColor.toUpperCase());
         } catch (Exception ex) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid BodyColor specified");
         }
      }

      if (strPatternColor != null) {
         try {
            this.patternColor = DyeColor.valueOf(strPatternColor.toUpperCase());
         } catch (Exception ex2) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid PatternColor specified");
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      TropicalFish e = (TropicalFish)entity;
      if (this.pattern != null) {
         e.setPattern(this.pattern);
      }

      if (this.bodyColor != null) {
         e.setBodyColor(this.bodyColor);
      }

      if (this.patternColor != null) {
         e.setPatternColor(this.patternColor);
      }

      return e;
   }
}
