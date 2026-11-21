package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "lastSignal",
   aliases = {},
   description = "Matches the last signal received by the target mob"
)
public class LastSignalCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "signal",
      aliases = {"s"},
      description = "The signal to match"
   )
   private final String signal;

   public LastSignalCondition(String line, MythicLineConfig mlc) {
      super(line);
      String signal = mlc.getString(new String[]{"signal", "s"}, "null", this.conditionVar);
      if (signal.equalsIgnoreCase("null")) {
         this.signal = null;
      } else {
         this.signal = signal;
      }
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (getPlugin().getMobManager().isActiveMob(entity)) {
         ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(entity);
         String lastSignal = am.getLastSignal();
         if (lastSignal == null) {
            return this.signal == null;
         } else {
            return this.signal == null ? false : lastSignal.matches(this.signal);
         }
      } else {
         return false;
      }
   }
}
