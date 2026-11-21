package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.volatilecode.virtual.IPacketEntityRenderer;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.TaskScheduler;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.TextOpacityFade;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Display.Billboard;

public class TextDisplayPacketBullet extends ProjectileBullet {
   @MythicField(
      name = "audience",
      description = "The audience of the message"
   )
   protected SkillAudience audience;
   @MythicField(
      name = "bulletText",
      aliases = {"text"},
      description = "The text of the bullet",
      defValue = "*"
   )
   protected PlaceholderString bulletText = null;
   @MythicField(
      name = "bulletBillboard",
      aliases = {"billboard"},
      description = "The billboard mode of the bullet",
      defValue = "CENTER"
   )
   protected Billboard bulletBillboard = Billboard.CENTER;
   @MythicField(
      name = "bulletBrightness",
      aliases = {"bulletBrightnessBlock"},
      description = "The bullet's brightness"
   )
   protected int bulletBrightnessBlock = -1;
   @MythicField(
      name = "bulletBrightnessSky",
      description = "The bullet's sky light brightness"
   )
   protected int bulletBrightnessSky = -1;
   protected int bulletBrightness = -1;
   @MythicField(
      name = "bulletCullingDistance",
      aliases = {"bulletViewDistance", "bulletViewRange"},
      description = "The bullet's brightness"
   )
   protected int bulletCullingDistance = 50;
   @MythicField(
      name = "bulletCullingHeight",
      aliases = {"cullHeight"},
      description = "The bullet's display height"
   )
   protected float bulletCullingHeight = 0.0F;
   @MythicField(
      name = "bulletCullingWidth",
      aliases = {"cullWidth"},
      description = "The bullet's display width"
   )
   protected float bulletCullingWidth = 0.0F;
   @MythicField(
      name = "bulletScale",
      description = "The bullet's size scale"
   )
   protected PlaceholderString bulletScale;
   @MythicField(
      name = "backgroundColor",
      aliases = {"color"},
      description = "The background color to use",
      defValue = "1073741824"
   )
   private Color backgroundColor;
   @MythicField(
      name = "forcedBulletRotation",
      aliases = {"forcedRotation"},
      description = "The bullet's rotation"
   )
   private final PlaceholderString forcedBulletRotation;
   @MythicField(
      name = "bulletRotatesBasedOnDirection",
      description = "Whether the bullet will automatically rotate based on the direction of movement"
   )
   private final PlaceholderBoolean bulletRotatesBasedOnDirection;
   private PacketTextDisplay.PacketTextDisplayBuilder options;

   public TextDisplayPacketBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      this.bulletText = config.getPlaceholderString(new String[]{"bulletText", "text"}, "*");
      this.bulletCullingDistance = config.getInteger(new String[]{"bulletCullingDistance", "bulletViewDistance", "bulletViewRange"}, 50);
      this.bulletCullingHeight = config.getFloat(new String[]{"bulletCullingHeight", "cullHeight"}, 0.0F);
      this.bulletCullingWidth = config.getFloat(new String[]{"bulletCullingWidth", "cullWidth"}, 0.0F);
      this.bulletScale = config.getPlaceholderString(new String[]{"bulletscale", "scale"}, "0.5,0.5,0.5");
      AbstractVector bulletScaleStatic = null;
      if (this.bulletScale.isStatic()) {
         try {
            bulletScaleStatic = new AbstractVector(this.bulletScale.get());
         } catch (Throwable e) {
            bulletScaleStatic = new AbstractVector(0.5, 0.5, 0.5);
            MythicLogger.errorMechanic(projectile, "Invalid bullet scale set: " + this.bulletScale.get());
         }
      }

      String strBulletBillboard = config.getString(new String[]{"bulletbillboard", "billboard"}, "CENTER");

      try {
         this.bulletBillboard = Billboard.valueOf(strBulletBillboard.toUpperCase());
      } catch (Throwable ex) {
         this.bulletBillboard = Billboard.VERTICAL;
         MythicLogger.errorMechanic(projectile, "Invalid bullet billboard mode: " + strBulletBillboard);
      }

      this.forcedBulletRotation = config.getPlaceholderString(new String[]{"forcedBulletRotation", "forcedRotation"}, null);
      this.bulletRotatesBasedOnDirection = config.getPlaceholderBoolean(new String[]{"bulletRotatesBasedOnDirection"}, false);
      this.bulletBrightnessBlock = config.getInteger(new String[]{"bulletbrightness", "bulletbrightnessblock"}, -1);
      this.bulletBrightnessSky = config.getInteger(new String[]{"bulletbrightnesssky"}, this.bulletBrightnessBlock);
      if (this.bulletBrightnessBlock <= -1 && this.bulletBrightnessSky <= -1) {
         this.bulletBrightness = -1;
      } else {
         this.bulletBrightness = this.bulletBrightnessBlock << 4 | this.bulletBrightnessSky << 20;
      }

      String colorStr = config.getString(new String[]{"backgroundcolor", "color"}, "64,0,0,0");

      try {
         String[] splitStr = colorStr.split(",");
         int a = Integer.parseInt(splitStr[0]);
         int r = Integer.parseInt(splitStr[1]);
         int g = Integer.parseInt(splitStr[2]);
         int b = Integer.parseInt(splitStr[3]);
         this.backgroundColor = Color.fromARGB(a, r, g, b);
      } catch (Throwable ex2) {
         this.backgroundColor = Color.fromARGB(64, 0, 0, 0);
         MythicLogger.errorMechanic(projectile, "Invalid bullet background color set: " + colorStr);
      }

      this.options = PacketTextDisplay.create()
         .textComponent(Text.parse(this.bulletText.get()))
         .height(this.bulletCullingHeight)
         .width(this.bulletCullingWidth)
         .billboard(this.bulletBillboard)
         .brightnessBitshifted(this.bulletBrightness)
         .lineWidth(200)
         .backgroundColor(this.backgroundColor);
      if (bulletScaleStatic != null) {
         this.options.scale(bulletScaleStatic);
      }

      if (this.forcedBulletRotation != null) {
         String bRotation = this.forcedBulletRotation.get();
         if (bRotation != null && !bRotation.isEmpty()) {
            String[] bRotationArray = bRotation.split(",");
            this.options.rotation(Float.parseFloat(bRotationArray[0]), Float.parseFloat(bRotationArray[1]), Float.parseFloat(bRotationArray[2]));
         }
      }
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new TextDisplayPacketBullet.TextBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class TextBulletTracker extends ProjectileBullet.BulletTracker implements ITextBulletTracker {
      protected PacketArmorStand mount;
      protected PacketTextDisplay<?> bullet;
      private volatile boolean despawned;

      public TextBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      private void applyBulletRotation(String bRotation, PacketTextDisplay bullet) {
         try {
            String[] rotVals = bRotation.split(",");
            float yaw = Float.parseFloat(rotVals[0]);
            float pitch = Float.parseFloat(rotVals[1]);
            float roll = Float.parseFloat(rotVals[2]);
            bullet.setRotation(yaw, pitch, roll);
         } catch (Exception e) {
            MythicLogger.errorMechanic(TextDisplayPacketBullet.this.getProjectileMechanic(), "Invalid bulletRotation: " + bRotation);
         }
      }

      @Override
      public void spawn(AbstractLocation start) {
         AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity();
         AbstractVector dir = currentVelocity.clone().normalize();
         AbstractLocation l = start.clone()
            .add(dir.multiply(TextDisplayPacketBullet.this.bulletForwardOffset))
            .add(0.0, TextDisplayPacketBullet.this.bulletYOffset, 0.0);
         this.mount = new PacketArmorStand(l);
         this.bullet = TextDisplayPacketBullet.this.options.build(l);
         AbstractVector delta = dir.clone().multiply(this.getProjectile().getVelocityMagnitude() / 7.6);
         AbstractLocation loc = this.getProjectile().getPreviousLocation().clone().add(delta);
         if (TextDisplayPacketBullet.this.forcedBulletRotation != null) {
            String bRotation = TextDisplayPacketBullet.this.forcedBulletRotation.get(this.getProjectile().getData());
            if (bRotation != null && !bRotation.isEmpty()) {
               this.applyBulletRotation(bRotation, this.bullet);
            }
         } else {
            double yaw = Math.atan2(dir.getX(), dir.getZ());
            double len = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
            float pitch = (float)Math.atan2(dir.getY(), len);
            float newYaw = (float)Math.toDegrees(-yaw) + 180.0F;
            float newPitch = (float)Math.toDegrees(-pitch);
            loc.setYaw(newYaw);
            loc.setPitch(newPitch);
         }

         Collection<AbstractPlayer> seen = SkillAudience.getEffective(TextDisplayPacketBullet.this.audience, this.getProjectile().getData(), this.getTarget());
         this.bullet.setTextComponent(Text.parse(TextDisplayPacketBullet.this.bulletText.get(this.getProjectile().getContext())));
         if (!TextDisplayPacketBullet.this.bulletScale.isStatic()) {
            String parsedBulletScale = TextDisplayPacketBullet.this.bulletScale.get(this.getProjectile().getContext());
            AbstractVector scaleVector = new AbstractVector(parsedBulletScale);
            this.bullet.setScale(scaleVector);
         }

         this.mount.getRenderer().setCullingDistance(TextDisplayPacketBullet.this.bulletCullingDistance);
         this.bullet.getRenderer().setCullingDistance(TextDisplayPacketBullet.this.bulletCullingDistance);
         IPacketEntityRenderer mountRenderer = this.mount.getRenderer();
         PacketEntityRenderer bulletRenderer = this.bullet.getRenderer();
         bulletRenderer.spawnWithMount(seen, (PacketEntityRenderer)mountRenderer);
         mountRenderer.addPassenger(bulletRenderer);
         Schedulers.async().runLater(() -> {
            if (!this.despawned) {
               mountRenderer.addPassenger(bulletRenderer);
            }
         }, 5L);
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (this.getProjectile().getTimesRan() % 5 == 0) {
            this.mount.getRenderer().updateRenderedPlayers();
            this.bullet.getRenderer().updateRenderedPlayers();
         }

         AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity();
         AbstractVector dir = currentVelocity.clone().normalize();
         AbstractLocation loc = origin.clone()
            .add(dir.multiply(TextDisplayPacketBullet.this.bulletForwardOffset))
            .add(0.0, TextDisplayPacketBullet.this.bulletYOffset, 0.0);
         if (TextDisplayPacketBullet.this.forcedBulletRotation != null) {
            String bRotation = TextDisplayPacketBullet.this.forcedBulletRotation.get(this.getProjectile().getData());
            if (bRotation != null && !bRotation.isEmpty()) {
               this.applyBulletRotation(bRotation, this.bullet);
            }
         } else {
            double yaw = Math.atan2(dir.getX(), dir.getZ());
            double len = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
            float pitch = (float)Math.atan2(dir.getY(), len);
            float newYaw = (float)Math.toDegrees(-yaw) + 180.0F;
            float newPitch = (float)Math.toDegrees(-pitch);
            loc.setYaw(newYaw);
            loc.setPitch(newPitch);
            if (TextDisplayPacketBullet.this.bulletRotatesBasedOnDirection != null
               && TextDisplayPacketBullet.this.bulletRotatesBasedOnDirection.get(this.getProjectile().getData())) {
               this.bullet.setRotation(newYaw, newPitch);
            }
         }

         this.bullet.setPosition(loc);
         this.bullet.update();
         this.mount.teleport(loc);
      }

      @Override
      public void despawn() {
         this.despawned = true;
         this.mount.destroy();
         this.bullet.destroy();
      }

      @Override
      public void setBulletText(String text) {
         if (!this.despawned) {
            this.bullet.setTextComponent((Component)(text == null ? Component.empty() : Text.parse(text)));
            this.bullet.update();
         }
      }

      @Override
      public void setTextOpacity(int opacity, int configuredDuration) {
         if (!this.despawned) {
            int duration = configuredDuration >= 0 ? configuredDuration : this.bullet.getInterpolationDuration().peek();

            for (TextOpacityFade.Step step : TextOpacityFade.plan(this.currentOpacity(), opacity, duration)) {
               if (step.delayTicks() == 0) {
                  this.applyStep(step);
               } else {
                  this.casterScheduler().runLater(() -> {
                     if (!this.despawned && this.currentOpacity() == step.expectedOpacity()) {
                        this.applyStep(step);
                     }
                  }, step.delayTicks());
               }
            }
         }
      }

      private int currentOpacity() {
         return this.bullet.getTextOpacity().peek() & 0xFF;
      }

      private void applyStep(TextOpacityFade.Step step) {
         this.bullet.setInterpolationDuration(step.interpolationDuration());
         this.bullet.restartInterpolation();
         this.bullet.setTextOpacity(step.opacity());
         this.bullet.update();
      }

      private TaskScheduler casterScheduler() {
         AbstractEntity caster = this.getProjectile().getData().getCaster().getEntity();
         Entity entity = caster == null ? null : caster.getBukkitEntity();
         return entity == null ? Schedulers.sync() : Schedulers.of(entity);
      }
   }
}
