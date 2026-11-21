package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.volatilecode.virtual.PacketArmorStand;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class SmallBlockBullet extends ProjectileBullet {
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
   protected BlockData bulletBlockData;
   private PacketArmorStand.PacketArmorStandBuilder armorStandOptions;

   public SmallBlockBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      String strBulletMaterial = config.getString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletBlockData = new BukkitBlock(strBulletMaterial).getBlockData();
      this.armorStandOptions = PacketArmorStand.create().headItem(new ItemStack(this.bulletBlockData.getMaterial())).small(true).upsideDown(true);
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new SmallBlockBullet.ArmorStandBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class ArmorStandBulletTracker extends ProjectileBullet.BulletTracker {
      protected PacketArmorStand bullet;

      public ArmorStandBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      @Override
      public void spawn(AbstractLocation start) {
         AbstractLocation l = start.clone().subtract(0.0, 1.4375, 0.0);
         this.bullet = SmallBlockBullet.this.armorStandOptions.build(l);
         this.bullet.getRenderer().spawn(SkillAudience.getEffective(SmallBlockBullet.this.audience, this.getProjectile().getData(), this.getTarget()));
      }

      @Override
      public void tick(AbstractLocation origin) {
         AbstractLocation to = this.getProjectile().getCurrentLocation().clone();
         AbstractVector delta = this.getProjectile()
            .getCurrentVelocity()
            .clone()
            .normalize()
            .multiply(this.getProjectile().getCurrentVelocity().length() / 7.6);
         double yaw = Math.atan2(delta.getX(), delta.getZ());
         double len = delta.getZ() / Math.cos(yaw);
         double pitch = Math.atan2(delta.getY(), len);
         this.bullet.setHeadPose(new EulerAngle(-pitch, 0.0, 0.0));
         AbstractLocation loc = to.clone().add(delta).subtract(0.0, 1.4375, 0.0);
         loc.setYaw((float)Math.toDegrees(-yaw));
         this.bullet.teleport(loc);
      }

      @Override
      public void despawn() {
         this.bullet.destroy();
      }
   }
}
