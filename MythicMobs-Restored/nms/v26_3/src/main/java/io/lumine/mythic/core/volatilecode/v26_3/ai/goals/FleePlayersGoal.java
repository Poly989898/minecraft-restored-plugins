package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

@MythicAIGoal(
   name = "fleePlayers",
   aliases = {"runfromPlayers"},
   description = "Run away from nearby players"
)
public class FleePlayersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected float distance;
   protected double speed;
   protected double safeSpeed;

   public FleePlayersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.distance = mlc.getFloat(new String[]{"distance", "d"}, 16.0F);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
      this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new AvoidEntityGoal(PathfinderHolder.getNMSEntity(this.entity), Player.class, this.distance, this.safeSpeed, this.speed);
   }
}
