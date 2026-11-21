package io.lumine.mythic.core.volatilecode.v1_21_R5.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R5.ai.PathfinderHolder;
import java.lang.ref.WeakReference;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

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
   public PathfinderGoal create() {
      return new ParentHurtByGoal.MythicParentHurtTargetGoal(this.entity, PathfinderHolder.getNMSEntity(this.entity));
   }

   public class MythicParentHurtTargetGoal extends PathfinderGoalTarget {
      private final AbstractEntity aiEntity;
      private ActiveMob activeMob = null;
      private WeakReference<EntityLiving> controllerEntity = null;
      private EntityLiving controllerLastHurt;
      private int timestamp;

      public MythicParentHurtTargetGoal(AbstractEntity aEntity, EntityInsentient mob) {
         super(mob, false);
         this.aiEntity = aEntity;
         this.a(EnumSet.of(Type.d));
      }

      public boolean b() {
         if (this.activeMob == null) {
            this.activeMob = ParentHurtByGoal.getPlugin().getMobManager().getMythicMobInstance(this.aiEntity);
            if (this.activeMob == null) {
               return false;
            }
         }

         EntityLiving controller = this.getControllerEntity();
         if (controller == null) {
            return false;
         } else {
            this.controllerLastHurt = controller.ew();
            int i = controller.ey();
            return i != this.timestamp && this.a(this.controllerLastHurt, PathfinderTargetCondition.a);
         }
      }

      public void d() {
         ((Mob)this.aiEntity.getBukkitEntity()).setTarget((LivingEntity)this.controllerLastHurt.getBukkitEntity());
         EntityLiving entityliving = this.getControllerEntity();
         if (entityliving != null) {
            this.timestamp = entityliving.ey();
         }

         super.d();
      }

      private EntityLiving getControllerEntity() {
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

               if (nmsEntity instanceof EntityLiving livingEntity) {
                  this.controllerEntity = new WeakReference<>(livingEntity);
                  return livingEntity;
               }
            }

            return null;
         }
      }
   }
}
