package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.utils.annotations.MythicField;

public class BlockBullet extends ProjectileBullet {
   @MythicField(
      name = "audience",
      description = "The audience of the message"
   )
   protected SkillAudience audience;
   @MythicField(
      name = "material",
      description = "The bullet's block data"
   )
   protected PlaceholderString bulletBlockData;
   @MythicField(
      name = "bulletSpin",
      aliases = {"bspin"},
      description = "Makes the bullet spin",
      defValue = "0"
   )
   protected float bulletSpin;

   public BlockBullet(ProjectileBulletType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.audience = config.getAudience("audience", "world");
      this.bulletBlockData = config.getPlaceholderString(new String[]{"bulletmaterial", "material", "mat"}, "STONE");
      this.bulletSpin = config.getFloat(new String[]{"bulletspin", "bspin"}, 0.0F);
   }

   @Override
   public ProjectileBullet.BulletTracker create(ProjectileBulletableTracker projectile, AbstractEntity target) {
      return new BlockBullet.BlockBulletTracker(projectile, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class BlockBulletTracker extends ProjectileBullet.BulletTracker {
      private PacketFallingBlock virtualBlock;

      public BlockBulletTracker(ProjectileBulletableTracker projectile, AbstractEntity target) {
         super(projectile, target);
      }

      @Override
      public void spawn(AbstractLocation start) {
         String in = BlockBullet.this.bulletBlockData.get(this.getProjectile().getData());
         AbstractBlock blockData = BlockBullet.this.getPlugin().getBootstrap().getBlock(in);
         this.virtualBlock = blockData.createFakeEntity(start);
         this.virtualBlock.getRenderer().spawn(SkillAudience.getEffective(BlockBullet.this.audience, this.getProjectile().getData(), this.getTarget()));
      }

      @Override
      public void tick(AbstractLocation origin) {
         this.virtualBlock.teleport(origin);
      }

      @Override
      public void despawn() {
         this.virtualBlock.destroy();
      }
   }
}
