package io.lumine.mythic.core.skills.projectiles;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import lombok.Generated;

public abstract class ProjectileBullet {
   private final SkillMechanic projectileMechanic;
   private final MythicLineConfig config;
   @MythicField(
      name = "bulletForwardOffset",
      aliases = {"bfoffset", "bfo"},
      description = "The forward offset for the bullet",
      defValue = "1.35"
   )
   protected double bulletForwardOffset;
   @MythicField(
      name = "bulletYOffset",
      aliases = {"byoffset", "byo"},
      description = "The Y offset for the bullet",
      defValue = "1.35"
   )
   protected double bulletYOffset;

   public ProjectileBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      this.projectileMechanic = projectile;
      this.config = config;
      this.bulletForwardOffset = config.getDouble(new String[]{"bulletforwardoffset", "bulletfo", "bulletoffset", "bfo"}, 1.8);
      this.bulletYOffset = config.getDouble(new String[]{"bulletyoffset", "byoffset", "byo"}, 0.0);
   }

   public MythicBukkit getPlugin() {
      return this.projectileMechanic == null ? MythicBukkit.inst() : this.projectileMechanic.getPlugin();
   }

   public abstract ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectileBulletableTracker, AbstractEntity abstractEntity);

   public abstract boolean isVirtual();

   @Generated
   public SkillMechanic getProjectileMechanic() {
      return this.projectileMechanic;
   }

   @Generated
   public MythicLineConfig getConfig() {
      return this.config;
   }

   @Generated
   public double getBulletForwardOffset() {
      return this.bulletForwardOffset;
   }

   @Generated
   public double getBulletYOffset() {
      return this.bulletYOffset;
   }

   public abstract class BulletTracker {
      private final ProjectileBulletableTracker projectile;
      private final AbstractEntity target;

      public boolean isVirtual() {
         return ProjectileBullet.this.isVirtual();
      }

      public abstract void spawn(AbstractLocation abstractLocation);

      public abstract void tick(AbstractLocation abstractLocation);

      public abstract void despawn();

      @Generated
      public BulletTracker(final ProjectileBulletableTracker projectile, final AbstractEntity target) {
         this.projectile = projectile;
         this.target = target;
      }

      @Generated
      public ProjectileBulletableTracker getProjectile() {
         return this.projectile;
      }

      @Generated
      public AbstractEntity getTarget() {
         return this.target;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof ProjectileBullet.BulletTracker other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$projectile = this.getProjectile();
            Object other$projectile = other.getProjectile();
            if (this$projectile == null ? other$projectile == null : this$projectile.equals(other$projectile)) {
               Object this$target = this.getTarget();
               Object other$target = other.getTarget();
               return this$target == null ? other$target == null : this$target.equals(other$target);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof ProjectileBullet.BulletTracker;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $projectile = this.getProjectile();
         result = result * 59 + ($projectile == null ? 43 : $projectile.hashCode());
         Object $target = this.getTarget();
         return result * 59 + ($target == null ? 43 : $target.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "ProjectileBullet.BulletTracker(projectile=" + this.getProjectile() + ", target=" + this.getTarget() + ")";
      }
   }
}
