package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.mechanics.TotemMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;
import java.util.UUID;

public class MEGBullet extends ProjectileBullet {
   @MythicField(
      name = "bulletModel",
      description = "The MEG model to use for the bullet."
   )
   protected PlaceholderString bulletModel = null;
   @MythicField(
      name = "bulletState",
      description = "The MEG model's state to use for the bullet."
   )
   protected PlaceholderString bulletState = null;
   @MythicField(
      name = "bulletColor",
      description = "The color of the bullet type"
   )
   protected PlaceholderString bulletColor = null;
   protected PlaceholderDouble bulletScale;
   @MythicField(
      name = "bulletEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean bulletEnchanted;
   @MythicField(
      name = "bulletGlowing",
      aliases = {"glowing"},
      description = "Whether the item should be glowing or not",
      defValue = "false"
   )
   protected boolean bulletGlowing;
   @MythicField(
      name = "bulletCulling",
      aliases = {"culling"},
      description = "",
      defValue = "true"
   )
   protected boolean bulletCulling;
   @MythicField(
      name = "bulletViewRadius",
      aliases = {"viewradius"},
      description = "",
      defValue = ""
   )
   protected int bulletViewRadius;
   @MythicField(
      name = "bulletGlowColor",
      description = "The color of the bullet glow"
   )
   protected PlaceholderString bulletGlowColor = null;
   @MythicField(
      name = "bulletBrightness",
      description = "The brightness of the bullet"
   )
   protected PlaceholderInt bulletBrightness = null;
   @MythicField(
      name = "bulletALOD",
      description = "Whether to use LOD culling on the bullet"
   )
   protected boolean bulletALOD = true;

   public MEGBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletModel = config.getPlaceholderString(new String[]{"bulletmodel", "model"}, null);
      this.bulletState = config.getPlaceholderString(new String[]{"bulletstate", "state"}, null);
      this.bulletColor = config.getPlaceholderString(new String[]{"bulletcolor"}, null);
      this.bulletScale = config.getPlaceholderDouble(new String[]{"bulletscale"}, 1.0);
      this.bulletViewRadius = config.getInteger(new String[]{"bulletViewRadius"}, -1);
      this.bulletEnchanted = config.getBoolean(new String[]{"bulletEnchanted", "enchanted"}, false);
      this.bulletGlowing = config.getBoolean(new String[]{"bulletGlowing", "glowing"}, false);
      this.bulletCulling = config.getBoolean(new String[]{"bulletCulling", "culling"}, true);
      this.bulletGlowColor = config.getPlaceholderString(new String[]{"bulletglowcolor"}, null);
      this.bulletBrightness = config.getPlaceholderInteger(new String[]{"bulletbrightness"}, (String)null);
      this.bulletALOD = config.getBoolean(new String[]{"bulletalod", "alod"}, true);
      this.getPlugin().getSkillManager().queueAfterLoad(() -> {
         Optional<AbstractModelEngineSupport> support = this.getPlugin().getCompatibility().getModelEngine();
         if (!support.isPresent()) {
            MythicLogger.errorMechanicConfig(projectile, config, "MEG Bullets are not usable without ModelEngine");
         }
      });
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new MEGBullet.MEGBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class MEGBulletTracker extends ProjectileBullet.BulletTracker {
      protected AbstractModelEngineSupport.MEGProjectile bullet;

      public MEGBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      public UUID getEntityId() {
         return this.bullet.getEntityId();
      }

      @Override
      public void spawn(AbstractLocation start) {
         Optional<AbstractModelEngineSupport> support = MEGBullet.this.getPlugin().getCompatibility().getModelEngine();
         if (!support.isPresent()) {
            MythicLogger.errorMechanicConfig(
               MEGBullet.this.getProjectileMechanic(), MEGBullet.this.getConfig(), "MEG Bullets are not usable without ModelEngine"
            );
         } else {
            String model = MEGBullet.this.bulletModel.get(this.getProjectile().getData());
            String state = MEGBullet.this.bulletState == null ? null : MEGBullet.this.bulletState.get(this.getProjectile().getData());
            double scale = MEGBullet.this.bulletScale.get(this.getProjectile().getData());
            String color = MEGBullet.this.bulletColor == null ? null : MEGBullet.this.bulletColor.get(this.getProjectile().getData());
            String glowColor = MEGBullet.this.bulletGlowColor == null ? null : MEGBullet.this.bulletGlowColor.get(this.getProjectile().getData());
            Integer brightness = MEGBullet.this.bulletBrightness == null ? null : MEGBullet.this.bulletBrightness.get(this.getProjectile().getData());
            AbstractModelEngineSupport.MEGProjectileData data = new AbstractModelEngineSupport.MEGProjectileData(
               model, scale, color, MEGBullet.this.bulletEnchanted, MEGBullet.this.bulletGlowing, glowColor, state, brightness, MEGBullet.this.bulletALOD
            );
            AbstractLocation location = this.getProjectile().getCurrentLocation().clone();
            this.bullet = support.get().createMEGBullet(data, location);
            if (this.bullet == null) {
               MythicLogger.errorMechanicConfig(
                  MEGBullet.this.getProjectileMechanic(), MEGBullet.this.getConfig(), "MEG Bullet is an invalid model, or MEG is out of date"
               );
            } else {
               if (!MEGBullet.this.bulletCulling) {
                  this.bullet.disableCulling();
               }

               if (MEGBullet.this.bulletViewRadius > 0) {
                  this.bullet.setRenderRadius(MEGBullet.this.bulletViewRadius);
               }

               if (this.getProjectile().hasTerminated()) {
                  this.bullet.terminate();
               }
            }
         }
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (this.bullet != null) {
            double forwardOffset;
            if (MEGBullet.this.getProjectileMechanic() instanceof Projectile projectileMechanic) {
               forwardOffset = projectileMechanic.getTickInterpolation() == 0
                  ? MEGBullet.this.bulletForwardOffset
                  : MEGBullet.this.bulletForwardOffset * projectileMechanic.getTickInterpolation();
            } else {
               forwardOffset = MEGBullet.this.bulletForwardOffset;
            }

            if (this.getProjectile() instanceof TotemMechanic.TotemTracker totemTracker) {
               AbstractLocation currentLocation = this.getProjectile().getCurrentLocation().clone();
               this.bullet.setLocation(currentLocation);
               this.bullet.setDirection(currentLocation.getDirection());
            } else {
               AbstractLocation currentLocation = this.getProjectile().getCurrentLocation().clone();
               AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity().clone().multiply(forwardOffset);
               this.bullet.setLocation(currentLocation);
               this.bullet.setDirection(currentVelocity);
            }
         }
      }

      @Override
      public void despawn() {
         this.bullet.terminate();
      }
   }
}
