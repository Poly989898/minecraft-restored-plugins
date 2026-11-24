package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public class TimeCondition extends Condition {
   private final TimeCondition.TimePeriod period;

   public TimeCondition(ConfigObject config) {
      super(config);
      config.validateKeys("period");
      this.period = TimeCondition.TimePeriod.valueOf(config.getString("period").toUpperCase());
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return this.period.matches(meta.getSourceLocation().getWorld().getTime());
   }

   public enum TimePeriod {
      DAY(2000L, 10000L),
      DUSK(14000L, 18000L),
      NIGHT(14000L, 22000L),
      DAWN(22000L, 2000L);

      private final long t1;
      private final long t2;

      TimePeriod(long t1, long t2) {
         this.t1 = t1;
         this.t2 = t2;
      }

      public boolean matches(long time) {
         return this.t1 < this.t2 ? this.t1 <= time && time <= this.t2 : this.t1 <= time || time <= this.t2;
      }
   }
}
