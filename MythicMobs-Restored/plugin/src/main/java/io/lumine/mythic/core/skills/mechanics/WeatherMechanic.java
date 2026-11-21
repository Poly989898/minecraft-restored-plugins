package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "weather",
   description = "Changes the weather"
)
public class WeatherMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "type",
      aliases = {"t"},
      description = "The type of weather to set (sunny, rain, storm)"
   )
   private int type;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "The duration of the weather change in ticks"
   )
   private PlaceholderDuration duration;

   public WeatherMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String t = mlc.getString(new String[]{"type", "t"}, "sunny");
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "500");
      String toLowerCase = t.toLowerCase();
      switch (toLowerCase) {
         case "sunny":
         case "sun":
         case "clear":
            this.type = 0;
            break;
         case "rain":
         case "rainy":
            this.type = 1;
            break;
         case "stormy":
         case "storm":
         case "thunder":
         case "thundering":
            this.type = 2;
            break;
         default:
            this.type = 0;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      switch (this.type) {
         case 0:
            data.getCaster().getEntity().getWorld().setStorm(false);
            data.getCaster().getEntity().getWorld().setThundering(false);
            break;
         case 1:
            data.getCaster().getEntity().getWorld().setStorm(true);
            data.getCaster().getEntity().getWorld().setThundering(false);
            break;
         case 2:
            data.getCaster().getEntity().getWorld().setStorm(true);
            data.getCaster().getEntity().getWorld().setThundering(true);
      }

      data.getCaster().getEntity().getWorld().setWeatherDuration(this.duration.get(data).getTicks());
      return SkillResult.SUCCESS;
   }
}
