package io.lumine.mythic.core.volatilecode.v26_1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

@MythicAIGoal(
   name = "players",
   aliases = {"player"},
   description = "Target nearby players"
)
public class PlayersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public PlayersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new NearestAttackableTargetGoal(PathfinderHolder.getNMSEntity(this.entity), Player.class, true);
   }
}
