package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "sendTitle",
   aliases = {"title"},
   description = "Sends a title message to the target player"
)
public class SendTitleMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderString title;
   protected PlaceholderString subtitle;
   protected PlaceholderDuration timeIn;
   protected PlaceholderDuration timeSt;
   protected PlaceholderDuration timeOt;

   public SendTitleMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;
      this.title = mlc.getPlaceholderString(new String[]{"title", "t"}, "");
      this.subtitle = mlc.getPlaceholderString(new String[]{"subtitle", "st"}, "");
      this.timeIn = mlc.getPlaceholderDuration(new String[]{"fadein", "fi"}, DurationUnit.TICKS, "1");
      this.timeSt = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "1");
      this.timeOt = mlc.getPlaceholderDuration(new String[]{"fadeout", "fo"}, DurationUnit.TICKS, "1");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String t = this.title.get(data, target);
      String st = this.subtitle.get(data, target);
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         target.asPlayer()
            .sendTitle(t, st, this.timeIn.get(data, target).getTicks(), this.timeSt.get(data, target).getTicks(), this.timeOt.get(data, target).getTicks());
         return SkillResult.SUCCESS;
      }
   }
}
