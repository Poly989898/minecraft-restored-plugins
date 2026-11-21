package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
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
   name = "gcd",
   aliases = {"globalcooldown", "setgcd", "setglobalcooldown"},
   description = "Triggers the global cooldown for the caster"
)
public class GCDMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "ticks",
      aliases = {"t"},
      description = "How many ticks to set the global cooldown",
      defValue = "20"
   )
   protected PlaceholderDuration ticks;

   public GCDMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.ticks = mlc.getPlaceholderDuration(new String[]{"ticks", "t"}, DurationUnit.TICKS, "20");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      data.getCaster().setGlobalCooldown(this.ticks.get(data).getTicks());
      return SkillResult.SUCCESS;
   }
}
