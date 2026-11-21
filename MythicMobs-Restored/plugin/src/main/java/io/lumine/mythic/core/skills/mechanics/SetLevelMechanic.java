package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setlevel",
   aliases = {"modifylevel"},
   description = "Modifies the casting mob's level"
)
public class SetLevelMechanic extends SkillMechanic implements INoTargetSkill {
   protected SetLevelMechanic.SetLevelAction action = SetLevelMechanic.SetLevelAction.SET;
   protected PlaceholderDouble level;

   public SetLevelMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strAction = mlc.getString(new String[]{"action", "a"}, "SET");

      try {
         this.action = SetLevelMechanic.SetLevelAction.valueOf(strAction.toUpperCase());
      } catch (Exception e) {
      }

      this.level = PlaceholderDouble.of(mlc.getString(new String[]{"level", "l"}, "1"));
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!(data.getCaster() instanceof ActiveMob)) {
         return SkillResult.INVALID_TARGET;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         double level = this.level.get(data);
         if (this.action == SetLevelMechanic.SetLevelAction.SET) {
            am.setLevel(level);
         } else if (this.action == SetLevelMechanic.SetLevelAction.ADD) {
            am.setLevel(am.getLevel() + level);
         } else if (this.action == SetLevelMechanic.SetLevelAction.SUBTRACT) {
            am.setLevel(am.getLevel() - level);
         } else if (this.action == SetLevelMechanic.SetLevelAction.MULTIPLY) {
            am.setLevel(am.getLevel() * level);
         } else if (this.action == SetLevelMechanic.SetLevelAction.DIVIDE) {
            am.setLevel(am.getLevel() / level);
         }

         return SkillResult.SUCCESS;
      }
   }

   protected static enum SetLevelAction {
      SET,
      ADD,
      SUBTRACT,
      MULTIPLY,
      DIVIDE;
   }
}
