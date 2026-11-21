package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSwell;
import net.minecraft.world.entity.monster.EntityCreeper;
import org.bukkit.entity.EntityType;

@MythicAIGoal(
   name = "creeperswell",
   aliases = {"creeperexplode"},
   description = "Cause a creeper to want to blow up."
)
public class CreeperSwellGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public CreeperSwellGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.getBukkitEntity().getType() == EntityType.CREEPER;
   }

   @Override
   public PathfinderGoal create() {
      return new CreeperSwellGoal.MythicSwellGoal((EntityCreeper)PathfinderHolder.getNMSEntity(this.entity));
   }

   protected class MythicSwellGoal extends PathfinderGoalSwell {
      private EntityCreeper creeper;

      public MythicSwellGoal(EntityCreeper entity) {
         super(entity);
         this.creeper = entity;
      }

      public void d() {
         CreeperSwellGoal.getPlugin()
            .getMobManager()
            .getActiveMob(this.creeper.cG())
            .ifPresent(am -> new TriggeredSkill(SkillTriggers.PRIME, am, BukkitAdapter.adapt(this.creeper.O_().getBukkitEntity()), true));
         super.d();
      }
   }
}
