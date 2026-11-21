package io.lumine.mythic.core.volatilecode.v26_2.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v26_2.ai.pathfinding.AsyncNearestAttackableTargetGoal;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

@MythicAIGoal(
   name = "nearestConditionalTarget",
   aliases = {"nearestConditional", "nearestIf"},
   description = "Target a nearby attacker that meets the conditions",
   premium = true
)
public class NearestConditionalTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected String targetConditionString;
   protected List<SkillCondition> targetConditions = null;
   protected boolean mustReach;
   protected boolean mustSee;
   protected int interval;

   public NearestConditionalTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.targetConditionString = mlc.getString(new String[]{"targetconditions", "conditions", "cond", "c"}, "null");
      if (this.targetConditionString != null) {
         this.targetConditions = getPlugin().getSkillManager().getConditions(this.targetConditionString);
      }

      this.interval = mlc.getInteger(new String[]{"interval", "int", "i"}, 0);
      this.mustSee = mlc.getBoolean(new String[]{"mustsee", "ms"}, true);
      this.mustReach = mlc.getBoolean(new String[]{"mustreach", "mr"}, false);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new AsyncNearestAttackableTargetGoal<LivingEntity>(
         PathfinderHolder.getNMSEntity(this.entity), LivingEntity.class, this.interval, this.mustSee, this.mustReach, () -> (targetEntity, serverLevel) -> {
            if (this.targetConditions == null) {
               return true;
            } else {
               try {
                  AbstractEntity absEntity = BukkitAdapter.adapt(targetEntity.getBukkitEntity());

                  for (SkillCondition cond : this.targetConditions) {
                     if (!cond.evaluateToEntity(this.entity, absEntity)) {
                        return false;
                     }
                  }
               } catch (Error | Exception e) {
                  e.printStackTrace();
               }

               return true;
            }
         }
      );
   }
}
