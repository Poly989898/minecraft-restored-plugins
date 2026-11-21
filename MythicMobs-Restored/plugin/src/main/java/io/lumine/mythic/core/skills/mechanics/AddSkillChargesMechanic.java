package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.cooldowns.ChargeRestoreOrder;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "addSkillCharges",
   aliases = {"skillCharges", "addskillcharge"},
   description = "Adds charges to the given skill for the target"
)
public class AddSkillChargesMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private Optional<Skill> skill = Optional.empty();
   private final PlaceholderInt amount;
   private final ChargeRestoreOrder order;
   private final boolean overflow;

   public AddSkillChargesMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      String skillName = mlc.getString(new String[]{"skill", "s"}, null);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.order = mlc.getEnum(new String[]{"order", "o"}, ChargeRestoreOrder.class, ChargeRestoreOrder.LATEST);
      this.overflow = mlc.getBoolean(new String[]{"overflow", "of"}, false);
      this.getManager().queueSecondPass(() -> {
         if (skillName != null) {
            this.skill = this.getManager().getSkill(file, this, skillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!this.skill.isEmpty() && this.skill.get() instanceof AbstractSkill trackedSkill) {
         int grantAmount = this.amount.get(data, target);
         if (grantAmount <= 0) {
            return SkillResult.SUCCESS;
         } else {
            SkillCaster caster = this.getPlugin().getSkillManager().getCaster(target);
            trackedSkill.addCharges(caster, grantAmount, this.order, this.overflow);
            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_CONFIG;
      }
   }
}
