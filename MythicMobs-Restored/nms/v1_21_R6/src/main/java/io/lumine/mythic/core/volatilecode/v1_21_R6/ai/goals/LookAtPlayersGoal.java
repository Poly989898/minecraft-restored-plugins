package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R6.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.player.EntityHuman;

@MythicAIGoal(
   name = "lookAtPlayer",
   aliases = {"lookAtPlayers"},
   description = "Float on water"
)
public class LookAtPlayersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private final float range;

   public LookAtPlayersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.range = mlc.getFloat(new String[]{"range", "radius", "r"}, 5.0F);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalLookAtPlayer(PathfinderHolder.getNMSEntity(this.entity), EntityHuman.class, this.range, 1.0F);
   }
}
