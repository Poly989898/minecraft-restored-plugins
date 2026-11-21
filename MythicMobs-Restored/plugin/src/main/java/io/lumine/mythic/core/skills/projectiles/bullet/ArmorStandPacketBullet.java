package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandPacketBullet extends ProjectileBullet {
   @MythicField(
      name = "audience",
      description = "The audience of the message"
   )
   protected SkillAudience audience;
   @MythicField(
      name = "bulletMaterial",
      aliases = {"material", "mat"},
      description = "The material to use for TRACKING, BLOCK, ITEM, or MYTHICITEM bullet types",
      defValue = "STONE"
   )
   protected Material bulletMaterial = null;
   @MythicField(
      name = "bulletModel",
      aliases = {"model"},
      description = "The custom model data of the bullet type",
      defValue = "0"
   )
   protected int bulletModelId = 0;
   @MythicField(
      name = "bulletColor",
      description = "The color of the bullet type if applicable i.e. potion color, leather armor."
   )
   protected String bulletColor = null;
   @MythicField(
      name = "bulletSmall",
      description = "Whether the bullet should use the small version or not"
   )
   protected boolean bulletSmall = false;
   @MythicField(
      name = "bulletEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean bulletEnchanted;
   @MythicField(
      name = "pitch",
      description = "The pitch rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationPitch;
   @MythicField(
      name = "yaw",
      description = "The yaw rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationYaw;
   @MythicField(
      name = "roll",
      description = "The roll rotation",
      defValue = "0"
   )
   final PlaceholderDouble rotationRoll;
   @MythicField(
      name = "rotation",
      aliases = {"rot"},
      description = "The rotation of the projectile, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String rotationString;
   @MythicField(
      name = "pitchSpeed",
      description = "The pitch rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble pitchSpeed;
   @MythicField(
      name = "yaw",
      description = "The yaw rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble yawSpeed;
   @MythicField(
      name = "roll",
      description = "The roll rotation speed",
      defValue = "0"
   )
   final PlaceholderDouble rollSpeed;
   @MythicField(
      name = "rotation",
      aliases = {"rot"},
      description = "The rotation speed of the projectile, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String rotationStringSpeed;
   AbstractVector rotationSpeed;
   AbstractVector rotation;
   private final PlaceholderDouble[] rotationComponents;
   private final PlaceholderDouble[] rotationSpeedComponents;
   private PacketArmorStand.PacketArmorStandBuilder armorStandOptions;
   private BukkitItemStack bulletItemStack;

   public ArmorStandPacketBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, 0);
      this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
      this.bulletEnchanted = config.getBoolean(new String[]{"bulletEnchanted", "enchanted"}, false);
      this.rotationPitch = config.getPlaceholderDouble(new String[]{"pitch"}, 0.0);
      this.rotationYaw = config.getPlaceholderDouble(new String[]{"yaw"}, 0.0);
      this.rotationRoll = config.getPlaceholderDouble(new String[]{"roll"}, 0.0);
      this.rotationString = config.getString(new String[]{"rotation", "rot"}, "0,0,0");
      this.pitchSpeed = config.getPlaceholderDouble(new String[]{"pitchSpeed", "ps"}, 0.0);
      this.yawSpeed = config.getPlaceholderDouble(new String[]{"yawSpeed", "ys"}, 0.0);
      this.rollSpeed = config.getPlaceholderDouble(new String[]{"rollSpeed", "rs"}, 0.0);
      this.rotationStringSpeed = config.getString(new String[]{"rotationSpeed", "rotSpeed", "rots"}, "0,0,0");
      this.rotationComponents = this.precompileRotation(this.rotationString);
      this.rotationSpeedComponents = this.precompileRotation(this.rotationStringSpeed);
      this.getPlugin().getSkillManager().queueAfterLoad(() -> {
         try {
            Optional<AbstractModelEngineSupport> support = this.getPlugin().getCompatibility().getModelEngine();
            if (strBulletMaterial.contains(":") && support.isPresent()) {
               support.get().queuePostModelRegistration(() -> {
                  try {
                     String[] mat = strBulletMaterial.split(":");
                     AbstractModelEngineSupport.ModelConfig modelConfig = support.get().getBoneModel(mat[0], mat[1]);
                     int id = modelConfig.getId();
                     if (id == -1) {
                        throw new Exception();
                     }

                     this.bulletModelId = id;
                     this.bulletMaterial = modelConfig.getMaterial();
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               });
            } else {
               this.bulletMaterial = Material.valueOf(strBulletMaterial.toUpperCase());
               this.bulletModelId = config.getInteger(new String[]{"bulletmodel", "model"}, 0);
            }

            this.bulletColor = config.getString(new String[]{"bulletcolor"}, null);
         } catch (Exception ex) {
            ex.printStackTrace();
            MythicLogger.errorMechanicConfig(projectile, config, "Specified bullet material does not exist");
            this.bulletMaterial = Material.STONE;
         }

         this.bulletItemStack = BukkitAdapter.adapt(this.bulletMaterial).modelData(this.bulletModelId).enchantmentGlow(this.bulletEnchanted);
         if (this.bulletColor != null) {
            this.bulletItemStack.color(this.bulletColor);
         }

         this.armorStandOptions = PacketArmorStand.create().headItem(this.bulletItemStack.getItemStack()).small(this.bulletSmall);
      });
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new ArmorStandPacketBullet.ArmorStandBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   private PlaceholderDouble[] precompileRotation(String rotationString) {
      String[] split = rotationString.split(",");
      if (split.length != 3) {
         MythicLogger.error("Invalid format for " + rotationString + " in " + this.getConfig().getLine());
         return null;
      } else {
         return new PlaceholderDouble[]{PlaceholderDouble.of(split[0]), PlaceholderDouble.of(split[1]), PlaceholderDouble.of(split[2])};
      }
   }

   AbstractVector parseRotation(PlaceholderDouble[] components, PlaceholderDouble pPitch, PlaceholderDouble pYaw, PlaceholderDouble pRoll, SkillMetadata data) {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (components != null) {
         try {
            x = components[0].get(data);
            y = components[1].get(data);
            z = components[2].get(data);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid rotation value in " + this.getConfig().getLine());
         }
      }

      double pitch = pPitch.get(data);
      if (pitch != 0.0) {
         x = pitch;
      }

      double yaw = pYaw.get(data);
      if (yaw != 0.0) {
         y = yaw;
      }

      double roll = pRoll.get(data);
      if (roll != 0.0) {
         z = roll;
      }

      return new AbstractVector(x, y, z);
   }

   public class ArmorStandBulletTracker extends ProjectileBullet.BulletTracker implements IItemBulletTracker {
      protected PacketArmorStand bullet;
      protected double lastPitch = 0.0;
      AbstractVector lastRotation = new AbstractVector(0, 0, 0);

      public ArmorStandBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      @Override
      public void spawn(AbstractLocation start) {
         ItemStack i = ArmorStandPacketBullet.this.bulletItemStack.getItemStack();
         AbstractLocation l = start.clone().subtract(0.0, 1.4375, 0.0);
         this.bullet = ArmorStandPacketBullet.this.armorStandOptions.build(l);
         AbstractLocation currentLocation = this.getProjectile().getPreviousLocation();
         AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity();
         Location to = BukkitAdapter.adapt(currentLocation).clone();
         Vector delta = BukkitAdapter.adapt(currentVelocity).normalize().multiply(this.getProjectile().getVelocityMagnitude() / 7.6);
         double yaw = Math.atan2(delta.getX(), delta.getZ());
         double len = delta.getZ() / Math.cos(yaw);
         double pitch = Math.atan2(delta.getY(), len);
         if (Math.abs(pitch - this.lastPitch) > 0.01F) {
            this.bullet.setHeadPose(new EulerAngle(-pitch, 0.0, 0.0));
            this.lastPitch = pitch;
         }

         Location loc = to.clone().add(delta).subtract(0.0, 1.4375, 0.0);
         float newYaw = (float)Math.toDegrees(-yaw);
         loc.setYaw(newYaw);
         this.bullet.getRenderer().spawn(SkillAudience.getEffective(ArmorStandPacketBullet.this.audience, this.getProjectile().getData(), this.getTarget()));
      }

      @Override
      public void tick(AbstractLocation origin) {
         if (this.getProjectile().getTimesRan() % 5 == 0) {
            this.bullet.getRenderer().updateRenderedPlayers();
         }

         double forwardOffset;
         if (ArmorStandPacketBullet.this.getProjectileMechanic() instanceof Projectile projectileMechanic) {
            forwardOffset = projectileMechanic.getTickInterpolation() == 0
               ? ArmorStandPacketBullet.this.bulletForwardOffset
               : ArmorStandPacketBullet.this.bulletForwardOffset * projectileMechanic.getTickInterpolation();
         } else {
            forwardOffset = ArmorStandPacketBullet.this.bulletForwardOffset;
         }

         AbstractLocation currentLocation = this.getProjectile().getCurrentLocation().clone();
         AbstractVector currentVelocity = this.getProjectile().getCurrentVelocity().clone().multiply(forwardOffset);
         Location to = BukkitAdapter.adapt(currentLocation).add(0.0, 0.2, 0.0);
         Vector delta = BukkitAdapter.adapt(currentVelocity);
         to.add(delta);
         double yaw = Math.atan2(delta.getX(), delta.getZ());
         double len = delta.getZ() / Math.cos(yaw);
         double pitch = Math.atan2(delta.getY(), len);
         if (this.getProjectile().getTimesRan() == 1) {
            ArmorStandPacketBullet.this.rotation = ArmorStandPacketBullet.this.parseRotation(
               ArmorStandPacketBullet.this.rotationComponents,
               ArmorStandPacketBullet.this.rotationPitch,
               ArmorStandPacketBullet.this.rotationYaw,
               ArmorStandPacketBullet.this.rotationRoll,
               this.getProjectile().getData()
            );
            this.bullet
               .setHeadPose(
                  new EulerAngle(
                     -pitch + ArmorStandPacketBullet.this.rotation.getX(),
                     ArmorStandPacketBullet.this.rotation.getY(),
                     ArmorStandPacketBullet.this.rotation.getZ()
                  )
               );
         }

         if (Math.abs(pitch - this.lastPitch) > 0.01F) {
            this.bullet.setHeadPose(new EulerAngle(-pitch, 0.0, 0.0));
            this.lastPitch = pitch;
         }

         Location loc = to.clone().add(delta).subtract(0.0, 1.4375, 0.0);
         float newYaw = (float)Math.toDegrees(-yaw);
         loc.setYaw(newYaw);
         ArmorStandPacketBullet.this.rotationSpeed = ArmorStandPacketBullet.this.parseRotation(
            ArmorStandPacketBullet.this.rotationSpeedComponents,
            ArmorStandPacketBullet.this.pitchSpeed,
            ArmorStandPacketBullet.this.yawSpeed,
            ArmorStandPacketBullet.this.rollSpeed,
            this.getProjectile().getData()
         );
         if (ArmorStandPacketBullet.this.rotationSpeed.length() > 0.0) {
            this.lastRotation.add(ArmorStandPacketBullet.this.rotationSpeed);
            EulerAngle euler = new EulerAngle(this.lastRotation.getX(), this.lastRotation.getY(), this.lastRotation.getZ());
            this.bullet.setHeadPose(euler);
         }

         this.bullet.teleport(BukkitAdapter.adapt(loc));
      }

      @Override
      public void despawn() {
         this.bullet.destroy();
      }

      @Override
      public void setBulletItemModel(int model) {
         ItemStack item = ArmorStandPacketBullet.this.bulletItemStack.getItemStack().clone();
         ItemMeta meta = item.getItemMeta();
         meta.setCustomModelData(model);
         item.setItemMeta(meta);
         this.bullet.setItem(item);
      }
   }
}
