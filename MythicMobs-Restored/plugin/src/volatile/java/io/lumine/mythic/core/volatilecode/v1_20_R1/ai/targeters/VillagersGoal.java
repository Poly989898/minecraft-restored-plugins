package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.npc.EntityVillager;

@MythicAIGoal(
   name = "villagers",
   aliases = {"villager"},
   description = "Target nearby villagers"
)
public class VillagersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public VillagersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new PathfinderGoalNearestAttackableTarget(PathfinderHolder.getNMSEntity(this.entity), EntityVillager.class, false);
   }
}
