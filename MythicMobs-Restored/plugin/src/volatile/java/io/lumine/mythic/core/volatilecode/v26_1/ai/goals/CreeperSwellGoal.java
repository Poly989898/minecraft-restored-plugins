package io.lumine.mythic.core.volatilecode.v26_1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
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
   public Goal create() {
      return new CreeperSwellGoal.MythicSwellGoal((Creeper)PathfinderHolder.getNMSEntity(this.entity));
   }

   protected class MythicSwellGoal extends SwellGoal {
      private Creeper creeper;

      public MythicSwellGoal(Creeper entity) {
         super(entity);
         this.creeper = entity;
      }

      public void start() {
         CreeperSwellGoal.getPlugin()
            .getMobManager()
            .getActiveMob(this.creeper.getUUID())
            .ifPresent(am -> new TriggeredSkill(SkillTriggers.PRIME, am, BukkitAdapter.adapt(this.creeper.getTarget().getBukkitEntity()), true));
         super.start();
      }
   }
}
