package io.lumine.mythic.core.volatilecode.v26_3.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Tameable;

@MythicAIGoal(
   name = "ownerTarget",
   aliases = {"ownerHurt", "ownerAttack"},
   description = "Target the mob's owner's target"
)
public class OwnerHurtGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public OwnerHurtGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isMob();
   }

   @Override
   public Goal create() {
      return (Goal)(this.entity.getBukkitEntity() instanceof Tameable
         ? new OwnerHurtTargetGoal((TamableAnimal)PathfinderHolder.getNMSEntity(this.entity))
         : new OwnerHurtGoal.MythicOwnerHurtTargetGoal(this.entity, PathfinderHolder.getNMSEntity(this.entity)));
   }

   public class MythicOwnerHurtTargetGoal extends TargetGoal {
      private final AbstractEntity aiEntity;
      private ActiveMob activeMob = null;
      private WeakReference<LivingEntity> controllerEntity = null;
      private LivingEntity controllerLastHurt;
      private int timestamp;

      public MythicOwnerHurtTargetGoal(AbstractEntity aEntity, Mob mob) {
         super(mob, false);
         this.aiEntity = aEntity;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         if (this.activeMob == null) {
            this.activeMob = OwnerHurtGoal.getPlugin().getMobManager().getMythicMobInstance(this.aiEntity);
            if (this.activeMob == null) {
               return false;
            }
         }

         LivingEntity controller = this.getControllerEntity();
         if (controller == null) {
            return false;
         } else {
            this.controllerLastHurt = controller.getLastHurtMob();
            int i = controller.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.controllerLastHurt, TargetingConditions.DEFAULT);
         }
      }

      public void start() {
         ((org.bukkit.entity.Mob)this.aiEntity.getBukkitEntity()).setTarget(this.controllerLastHurt.getBukkitEntity());
         LivingEntity entityliving = this.getControllerEntity();
         if (entityliving != null) {
            this.timestamp = entityliving.getLastHurtMobTimestamp();
         }

         super.start();
      }

      private LivingEntity getControllerEntity() {
         if (this.controllerEntity != null && this.controllerEntity.get() != null) {
            return this.controllerEntity.get();
         } else {
            Optl<AbstractEntity> maybeParent = this.activeMob.getOwner();
            if (maybeParent.isPresent()) {
               AbstractEntity aEntity = maybeParent.get();
               Entity nmsEntity = ((CraftEntity)aEntity.getBukkitEntity()).getHandle();
               if (nmsEntity == null) {
                  return null;
               }

               if (nmsEntity instanceof LivingEntity livingEntity) {
                  this.controllerEntity = new WeakReference<>(livingEntity);
                  return livingEntity;
               }
            }

            return null;
         }
      }
   }
}
