package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.pathfinding.AsyncAvoidEntityGoal;
import java.util.List;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

@MythicAIGoal(
   name = "fleeConditional",
   aliases = {"fleeIf"},
   description = "Run away from nearby entities that meets the conditions",
   premium = true
)
public class FleeConditionalGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected float distance;
   protected double speed;
   protected double safeSpeed;
   protected List<SkillCondition> targetConditions = null;
   protected List<SkillCondition> mobConditions = null;

   public FleeConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.distance = mlc.getFloat(new String[]{"distance", "d"}, 4.0F);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
      this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
      String targetConditionString = mlc.getString(new String[]{"targetconditions", "fleeconditions", "conditions", "cond", "c"}, "null");
      if (targetConditionString != null) {
         this.targetConditions = getPlugin().getSkillManager().getConditions(targetConditionString);
      }

      String mobConditionString = mlc.getString(new String[]{"mobconditions", "mconditions", "mcond", "mc"}, "null");
      if (mobConditionString != null) {
         this.mobConditions = getPlugin().getSkillManager().getConditions(mobConditionString);
      }
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new AsyncAvoidEntityGoal(PathfinderHolder.getNMSEntity(this.entity), this.distance, this.safeSpeed, this.speed, () -> {
         if (this.mobConditions != null) {
            for (SkillCondition cond : this.mobConditions) {
               if (!cond.evaluateEntity(this.entity)) {
                  return targetEntity -> false;
               }
            }
         }

         return targetEntity -> {
            if (this.targetConditions == null) {
               return true;
            } else {
               try {
                  AbstractEntity absTarget = BukkitAdapter.adapt(targetEntity.getBukkitEntity());

                  for (SkillCondition condx : this.targetConditions) {
                     if (!condx.evaluateToEntity(this.entity, absTarget)) {
                        return false;
                     }
                  }
               } catch (Error | Exception var5) {
                  var5.printStackTrace();
               }

               return true;
            }
         };
      });
   }
}
