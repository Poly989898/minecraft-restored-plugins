package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ShulkerMissile extends SkillHandler<VectorSkillResult> {
   public ShulkerMissile() {
      super("SHULKER_MISSILE");
      this.registerModifiers("damage", "effect-duration", "duration");
   }

   @NotNull
   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
            double n = 0.0;

            public void run() {
               if (this.n++ > 3.0) {
                  this.cancel();
               } else {
                  caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0F, 2.0F);
                  ShulkerBullet shulkerBullet = (ShulkerBullet)caster.getWorld()
                     .spawnEntity(caster.getLocation().add(0.0, 1.0, 0.0), EntityType.SHULKER_BULLET);
                  shulkerBullet.setShooter(caster);
                  new ShulkerMissile.ShulkerMissileHandler(
                     skillMeta.getCaster(),
                     shulkerBullet,
                     result.getTarget(),
                     (long)(skillMeta.getParameter("duration") * 20.0),
                     skillMeta.getParameter("damage"),
                     (int)(20.0 * skillMeta.getParameter("effect-duration"))
                  );
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 3L);
   }

   public static class ShulkerMissileHandler extends TemporaryListener {
      private final PlayerMetadata caster;
      private final ShulkerBullet bullet;
      private final Vector vel;
      private final long duration;
      private final double damage;
      private final int effectDuration;

      public ShulkerMissileHandler(PlayerMetadata caster, final ShulkerBullet bullet, final Vector vel, final long duration, double damage, int effectDuration) {
         super();
         this.caster = caster;
         this.bullet = bullet;
         this.vel = vel;
         this.duration = duration;
         this.damage = damage;
         this.effectDuration = effectDuration;
         this.registerRunnable(new BukkitRunnable() {
            double ti = 0.0;

            public void run() {
               if (!bullet.isDead() && !(this.ti++ >= duration)) {
                  bullet.setVelocity(vel);
               } else {
                  ShulkerMissileHandler.this.close();
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
      public void onHit(EntityDamageByEntityEvent event) {
         if (event.getDamager().equals(this.bullet)) {
            if (!UtilityMethods.canTarget(this.caster.getPlayer(), null, event.getEntity(), InteractionType.OFFENSE_SKILL)) {
               event.setCancelled(true);
            } else {
               final LivingEntity entity = (LivingEntity)event.getEntity();
               event.setDamage(this.damage);
               (new BukkitRunnable() {
                     final Location loc = entity.getLocation();
                     double y = 0.0;

                     public void run() {
                        if (this.y == 0.0) {
                           entity.removePotionEffect(PotionEffectType.LEVITATION);
                           entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, ShulkerMissileHandler.this.effectDuration, 0));
                        }

                        for (int j1 = 0; j1 < 3; j1++) {
                           this.y += 0.04;

                           for (int j = 0; j < 2; j++) {
                              double xz = this.y * Math.PI * 1.3 + j * Math.PI;
                              this.loc
                                 .getWorld()
                                 .spawnParticle(
                                    VParticle.REDSTONE.get(), this.loc.clone().add(Math.cos(xz), this.y, Math.sin(xz)), 1, new DustOptions(Color.MAROON, 1.0F)
                                 );
                           }
                        }

                        if (this.y >= 2.0) {
                           this.cancel();
                        }
                     }
                  })
                  .runTaskTimer(MythicLib.plugin, 0L, 1L);
            }
         }
      }
   }
}
