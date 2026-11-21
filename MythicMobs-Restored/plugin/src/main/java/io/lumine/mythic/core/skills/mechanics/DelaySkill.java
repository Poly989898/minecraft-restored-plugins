package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IDummySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "delay",
   description = "Delays the execution of the next mechanic"
)
public class DelaySkill extends SkillMechanic implements IDummySkill {
   @MythicField(
      name = "ticks",
      defValue = "0",
      description = "The number of ticks to delay the execution."
   )
   protected PlaceholderDuration ticks;

   public DelaySkill(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      PlaceholderDuration ticks = mlc.getPlaceholderDuration(new String[]{"ticks"}, DurationUnit.TICKS, null);
      if (ticks == null) {
         try {
            ticks = PlaceholderDuration.of(line.split(" ")[1], DurationUnit.TICKS);
         } catch (Exception e) {
            ticks = PlaceholderDuration.of("0", DurationUnit.TICKS);
            MythicLogger.errorMechanicConfig(this, mlc, "A delay is incorrectly configured: second argument must be a duration.");
         }
      }

      this.ticks = ticks;
   }

   public int getTicks(SkillMetadata data) {
      return this.ticks.get(data).getTicks();
   }
}
