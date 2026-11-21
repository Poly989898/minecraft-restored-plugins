package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.RangedLong;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "worldtime",
   aliases = {},
   description = "Matches a range against the target location's world's time."
)
public class WorldTimeCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "time",
      aliases = {"t"},
      description = "A range of time to check",
      defValue = "0"
   )
   private PlaceholderString time;

   public WorldTimeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.time = mlc.getPlaceholderString(new String[]{"time", "t"}, "0", this.conditionVar);
   }

   @Override
   public boolean check(AbstractLocation l) {
      long time = BukkitAdapter.adapt(l.getWorld()).getTime();
      RangedLong compare = new RangedLong(this.time.get());
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking Worldtime: {0} vs {1}", time, compare.toString());
      return compare.equals(time);
   }
}
