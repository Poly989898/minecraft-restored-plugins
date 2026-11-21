package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "potion",
   description = "Applies a potion effect to the target entity"
)
public class PotionMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString effect;
   protected PlaceholderDuration duration;
   protected PlaceholderInt lvl;
   protected boolean overwrite;
   protected boolean ambientParticles;
   protected boolean hasParticles;
   protected boolean hasIcon;

   public PotionMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.effect = mlc.getPlaceholderString(new String[]{"type", "effect", "t"}, "SLOW");
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "100");
      this.lvl = mlc.getPlaceholderInteger(new String[]{"level", "lvl", "l"}, 0);
      this.overwrite = mlc.getBoolean(new String[]{"overwrite", "ow", "override", "or", "force"}, false);
      this.ambientParticles = mlc.getBoolean(new String[]{"ambientparticles", "ambient", "a"}, false);
      this.hasParticles = mlc.getBoolean(new String[]{"hasparticles", "particles", "p"}, true);
      this.hasIcon = mlc.getBoolean(new String[]{"hasicon", "icon", "i"}, true);
      if (this.duration.toString().equals("-1")) {
         this.duration = null;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.applyPotionEffect(data, target);
   }

   protected SkillResult applyPotionEffect(SkillMetadata data, AbstractEntity target) {
      String effectName = this.effect.get(data, target);
      int duration = this.duration == null ? -1 : this.duration.get(data, target).getTicks();
      int level = this.lvl.get(data, target);
      AbstractPotionEffect effect = this.getPlugin()
         .getBootstrap()
         .createPotionEffect(effectName, duration, level, this.ambientParticles, this.hasParticles, this.hasIcon);
      if (effect == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "The 'type' attribute must be a valid potion type.");
         return SkillResult.INVALID_CONFIG;
      } else {
         if (this.overwrite) {
            target.removePotionEffectType(effect);
         }

         target.addPotionEffect(effect);
         return SkillResult.SUCCESS;
      }
   }
}
