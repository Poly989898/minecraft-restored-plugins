package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "jaylawl",
   name = "pitch",
   aliases = {},
   version = "4.5",
   description = "Checks if the pitch of the target entity is within a range"
)
public class PitchCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "pitch",
      aliases = {"p"},
      description = "The number range to match"
   )
   private RangedDouble pitch;

   public PitchCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pitch = new RangedDouble(mlc.getString(new String[]{"pitch", "p"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity e) {
      return this.pitch.equals(e.getBukkitEntity().getLocation().getPitch());
   }
}
