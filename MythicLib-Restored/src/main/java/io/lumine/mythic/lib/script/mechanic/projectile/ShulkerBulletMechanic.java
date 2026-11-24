package io.lumine.mythic.lib.script.mechanic.projectile;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShulkerBulletMechanic extends DirectionMechanic {
   private final DoubleFormula lifeSpan;
   private final Script onHitEntity;
   private static final double DEFAULT_LIFE_SPAN = 60.0;

   public ShulkerBulletMechanic(ConfigObject config) {
      super(config);
      this.onHitEntity = config.contains("hit_entity") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("hit_entity")) : null;
      this.lifeSpan = config.getDoubleFormula("life_span", DoubleFormula.constant(60.0));
   }

   @Override
   public void cast(SkillMetadata meta, Location source, Vector dir) {
      long lifespan = (long)this.lifeSpan.evaluate(meta);
      Validate.isTrue(lifespan > 0L, "Life spawn must be strictly positive");
      ShulkerBullet shulkerBullet = (ShulkerBullet)source.getWorld().spawnEntity(source, EntityType.SHULKER_BULLET);
      shulkerBullet.setShooter(meta.getCaster().getPlayer());
      new ShulkerBulletMechanic.ShulkerBulletHandler(shulkerBullet, meta, dir).close(lifespan);
   }

   public class ShulkerBulletHandler extends TemporaryListener {
      private final ShulkerBullet bullet;
      private final SkillMetadata skillMetadata;

      public ShulkerBulletHandler(ShulkerBullet bullet, SkillMetadata skillMetadata, Vector direction) {
         super();
         this.bullet = bullet;
         this.skillMetadata = skillMetadata;
         this.registerRunnable(new BukkitRunnable() {
            public void run() {
               if (bullet.isDead()) {
                  ShulkerBulletHandler.this.close();
               } else {
                  bullet.setVelocity(direction);
               }
            }
         }, runnable -> runnable.runTaskTimer(MythicLib.plugin, 0L, 1L));
      }

      @Override
      public void whenClosed() {
         if (!this.bullet.isDead()) {
            this.bullet.remove();
         }
      }

      @EventHandler
      public void registerHit(EntityDamageByEntityEvent event) {
         if (event.getDamager().equals(this.bullet)) {
            event.setCancelled(true);
            this.close();
            ShulkerBulletMechanic.this.onHitEntity
               .cast(
                  this.skillMetadata
                     .clone(
                        this.skillMetadata.getSourceLocation(),
                        this.skillMetadata.getTargetLocationOrNull(),
                        event.getEntity(),
                        this.skillMetadata.getOrientationOrNull()
                     )
               );
         }
      }
   }
}
