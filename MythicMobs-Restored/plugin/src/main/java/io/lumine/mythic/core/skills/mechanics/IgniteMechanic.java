package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "ignite",
   description = "Sets the target entity on fire"
)
public class IgniteMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "ticks",
      aliases = {"t", "duration", "d"},
      description = "The ignite duration",
      defValue = "60"
   )
   protected PlaceholderDuration ticks;

   public IgniteMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.ticks = mlc.getPlaceholderDuration(new String[]{"ticks", "t", "duration", "d"}, DurationUnit.TICKS, "60");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      target.setFireTicks(this.ticks.get(data, target).getTicks());
      return SkillResult.SUCCESS;
   }
}
