package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;

@MythicAIGoal(
   name = "ironGolems",
   aliases = {"irongolem", "iron_golems", "iron_golem"},
   description = "Target nearby iron golems"
)
public class IronGolemGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public IronGolemGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalNearestAttackableTarget(PathfinderHolder.getNMSEntity(this.entity), EntityIronGolem.class, false);
   }
}
