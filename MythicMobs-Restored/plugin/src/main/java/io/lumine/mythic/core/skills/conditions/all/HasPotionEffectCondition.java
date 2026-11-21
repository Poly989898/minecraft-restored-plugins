package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.adapters.BukkitPotionEffect;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Joikd",
   name = "hasPotionEffect",
   aliases = {"hasPotion"},
   description = "Tests if the target entity has a potion effect"
)
public class HasPotionEffectCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "type",
      aliases = {"t"},
      description = "The potion effect type"
   )
   private final String effect;
   @MythicField(
      name = "level",
      aliases = {"lvl", "l"},
      description = "An optional level range to match"
   )
   private RangedDouble level = null;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "An optional duration range to match"
   )
   private RangedDouble duration = null;
   private final boolean checkAll;

   public HasPotionEffectCondition(String line, MythicLineConfig mlc) {
      super(line);
      String effectType = mlc.getString(new String[]{"type", "t"}, this.conditionVar.equals("0") ? "ANY" : this.conditionVar).toUpperCase();
      if (effectType.equals("ANY")) {
         this.checkAll = true;
         this.effect = null;
      } else {
         this.checkAll = false;
         this.effect = BukkitPotionEffect.getEffectName(BukkitPotionEffect.getEffectType(effectType));
      }

      String strLevel = mlc.getString(new String[]{"level", "lvl", "l"}, null);
      if (strLevel != null) {
         this.level = new RangedDouble(strLevel);
      }

      String strDuration = mlc.getString(new String[]{"duration", "d"}, null);
      if (strDuration != null) {
         this.duration = new RangedDouble(strDuration);
      }
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (this.checkAll) {
         return entity.hasPotionEffect();
      } else {
         MythicLogger.debug(
            MythicLogger.DebugLevel.CONDITION,
            "Checking if target has PotionEffect type = " + this.effect + ", level = " + this.level + ", d = " + this.duration
         );
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning " + entity.hasPotionEffect(this.effect, this.level, this.duration));
         return entity.hasPotionEffect(this.effect, this.level, this.duration);
      }
   }
}
