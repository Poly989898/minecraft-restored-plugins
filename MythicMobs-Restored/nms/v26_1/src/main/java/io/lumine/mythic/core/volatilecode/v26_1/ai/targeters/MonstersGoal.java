package io.lumine.mythic.core.volatilecode.v26_1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;

@MythicAIGoal(
   name = "monsters",
   aliases = {"monster"},
   description = "Target nearby monsters"
)
public class MonstersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public MonstersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new NearestAttackableTargetGoal(PathfinderHolder.getNMSEntity(this.entity), Monster.class, true);
   }
}
