package io.lumine.mythic.core.volatilecode.v26_1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.entity.CraftEntity;

@MythicAIGoal(
   name = "parentHurtBy",
   aliases = {"parentHurtByTarget", "parentDamager", "parentAttacker"},
   description = "Target something that attacks the mob's parent"
)
public class ParentHurtByGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public ParentHurtByGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isMob();
   }

   @Override
   public Goal create() {
      return new ParentHurtByGoal.MythicParentHurtTargetGoal(this.entity, PathfinderHolder.getNMSEntity(this.entity));
   }

   public class MythicParentHurtTargetGoal extends TargetGoal {
      private final AbstractEntity aiEntity;
      private ActiveMob activeMob = null;
      private WeakReference<LivingEntity> controllerEntity = null;
      private LivingEntity controllerLastHurt;
      private int timestamp;

      public MythicParentHurtTargetGoal(AbstractEntity aEntity, Mob mob) {
         super(mob, false);
         this.aiEntity = aEntity;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         if (this.activeMob == null) {
            this.activeMob = ParentHurtByGoal.getPlugin().getMobManager().getMythicMobInstance(this.aiEntity);
            if (this.activeMob == null) {
               return false;
            }
         }

         LivingEntity controller = this.getControllerEntity();
         if (controller == null) {
            return false;
         } else {
            this.controllerLastHurt = controller.getLastHurtByMob();
            int i = controller.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.controllerLastHurt, TargetingConditions.DEFAULT);
         }
      }

      public void start() {
         ((org.bukkit.entity.Mob)this.aiEntity.getBukkitEntity()).setTarget((org.bukkit.entity.LivingEntity)this.controllerLastHurt.getBukkitEntity());
         LivingEntity entityliving = this.getControllerEntity();
         if (entityliving != null) {
            this.timestamp = entityliving.getLastHurtByMobTimestamp();
         }

         super.start();
      }

      private LivingEntity getControllerEntity() {
         if (this.controllerEntity != null && this.controllerEntity.get() != null) {
            return this.controllerEntity.get();
         } else {
            Optl<AbstractEntity> maybeParent = this.activeMob.getParent();
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
