package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;

@MythicCondition(
   author = "Ashijin",
   name = "skillOnCooldown",
   aliases = {},
   description = "Whether the caster has the specified skill on cooldown."
)
public class SkillOnCooldownCondition extends SkillCondition implements ICasterCondition, IEntityCondition {
   @MythicField(
      name = "skill",
      aliases = {"s"},
      description = "The skill name to test"
   )
   private String skillName;
   private AbstractSkill skill;

   public SkillOnCooldownCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.skillName = mlc.getString(new String[]{"skill", "s"}, "", this.conditionVar);
      getPlugin().getClock().queueSecondPass(() -> {
         Optional<Skill> maybeSkill = getPlugin().getSkillManager().getSkill(this.skillName);
         if (maybeSkill.isPresent() && maybeSkill.get() instanceof AbstractSkill trackedSkill) {
            this.skill = trackedSkill;
         }
      });
   }

   @Override
   public boolean check(SkillCaster caster) {
      return this.skill != null && this.skill.onCooldown(caster);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return this.skill != null && target != null ? this.skill.onCooldown(getPlugin().getSkillManager().getCaster(target)) : false;
   }
}
