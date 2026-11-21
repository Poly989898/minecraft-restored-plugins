package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "barSet",
   version = "4.8",
   description = "Sets the display values on a custom bossbar"
)
public class BarSetMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "name",
      aliases = {"n"},
      defValue = "infobar",
      description = "The name of the bar to modify/set"
   )
   protected String barName;
   @MythicField(
      name = "display",
      aliases = {"d"},
      description = "The text displayed on the bar",
      defValue = "<caster.name>"
   )
   protected PlaceholderString barDisplay;
   @MythicField(
      name = "value",
      aliases = {"v"},
      description = "How filled the bossbar is. Must be between 0.0 and 1.0",
      defValue = "1.0"
   )
   protected PlaceholderDouble value;
   @MythicField(
      name = "color",
      aliases = {"c"},
      description = "The color of the bossbar",
      defValue = "RED"
   )
   protected String barTimerColor;
   @MythicField(
      name = "style",
      aliases = {"s"},
      description = "The style of the bossbar",
      defValue = "SOLID"
   )
   protected String barTimerStyle;

   public BarSetMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.barName = mlc.getString(new String[]{"name", "n"}, "infobar");
      this.barDisplay = mlc.getPlaceholderString(new String[]{"display", "d", "bartimerdisplay", "bartimertext"}, "<caster.name>");
      this.value = mlc.getPlaceholderDouble(new String[]{"value", "v"}, 1.0);
      this.barTimerColor = mlc.getString(new String[]{"color", "c", "bartimercolor"}, "RED");
      this.barTimerStyle = mlc.getString(new String[]{"style", "s", "bartimerstyle"}, "SOLID");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster() instanceof ActiveMob am) {
         AbstractBossBar bar = am.getBar(this.barName);
         if (bar != null) {
            bar.setTitle(this.barDisplay.get(data));
            bar.setProgress(this.value.get(data));
            bar.setColor(this.barTimerColor);
            bar.setStyle(this.barTimerStyle);
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
