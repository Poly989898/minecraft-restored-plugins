package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "setSkillCooldown",
   aliases = {"skillCooldown", "setskillcd", "skillcd"},
   description = "Sets the cooldown on the given skill for the target"
)
public class SkillCooldownMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private Optional<Skill> skill = Optional.empty();
   private PlaceholderDuration cooldown;

   public SkillCooldownMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      String skillName = mlc.getString(new String[]{"skill", "s"}, null);
      this.cooldown = mlc.getPlaceholderDuration(new String[]{"seconds", "sec", "time", "t"}, DurationUnit.SECONDS, "0");
      this.getManager().queueSecondPass(() -> {
         if (skillName != null) {
            this.skill = this.getManager().getSkill(file, this, skillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!this.skill.isEmpty() && this.skill.get() instanceof AbstractSkill trackedSkill) {
         Duration duration = this.cooldown.get(data, target);
         SkillCaster caster = this.getPlugin().getSkillManager().getCaster(target);
         trackedSkill.setCooldown(caster, duration);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_CONFIG;
      }
   }
}
