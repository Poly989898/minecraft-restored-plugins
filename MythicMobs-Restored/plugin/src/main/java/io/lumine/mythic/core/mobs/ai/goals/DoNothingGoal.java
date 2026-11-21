package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import java.util.List;

@MythicAIGoal(
   name = "doNothing",
   aliases = {"nothing"},
   version = "4.9",
   description = "Do nothing if the conditions are met",
   premium = true
)
public class DoNothingGoal extends Pathfinder implements PathfindingGoal {
   protected String conditionString;
   protected List<SkillCondition> conditions = null;

   public DoNothingGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;
      this.conditionString = mlc.getString(new String[]{"fleeconditions", "conditions", "cond", "c"}, "null");
      if (this.conditionString != null) {
         this.conditions = getPlugin().getSkillManager().getConditions(this.conditionString);
      }
   }

   @Override
   public boolean shouldStart() {
      if (this.conditions == null) {
         return true;
      } else {
         try {
            for (SkillCondition cond : this.conditions) {
               if (!cond.evaluateEntity(this.entity)) {
                  return false;
               }
            }
         } catch (Error | Exception e) {
            e.printStackTrace();
         }

         return true;
      }
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
   }

   @Override
   public boolean shouldEnd() {
      if (this.conditions == null) {
         return false;
      } else {
         try {
            for (SkillCondition cond : this.conditions) {
               if (!cond.evaluateEntity(this.entity)) {
                  return true;
               }
            }
         } catch (Error | Exception e) {
            e.printStackTrace();
         }

         return false;
      }
   }

   @Override
   public void end() {
   }
}
