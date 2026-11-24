package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Fire_Rage extends SkillHandler<SimpleSkillResult> {
   public Fire_Rage() {
      this.registerModifiers("duration", "count", "damage", "ignite");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      new Fire_Rage.RageEffect(skillMeta);
   }

   public class RageEffect extends BukkitRunnable implements Listener {
      private final PlayerMetadata caster;
      private final int count;
      private final int ignite;
      private final double damage;
      private int c;
      private double b;
      private long last = System.currentTimeMillis();
      private static final long MINIMUM_WAIT_TIME = 700L;

      public RageEffect(SkillMetadata skillMeta) {
         this.caster = skillMeta.getCaster();
         this.ignite = (int)(20.0 * skillMeta.getParameter("ignite"));
         this.damage = skillMeta.getParameter("damage");
         this.c = this.count = (int)skillMeta.getParameter("count");
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
         Bukkit.getScheduler().runTaskLater(MythicLib.plugin, this::close, (long)(skillMeta.getParameter("duration") * 20.0));
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
      }

      @EventHandler
      public void a(PlayerInteractEvent event) {
         if (event.getPlayer().equals(this.caster.getPlayer())
            && event.getAction().name().contains("LEFT_CLICK")
            && System.currentTimeMillis() - this.last > 700L) {
            this.last = System.currentTimeMillis();
            this.castEffect();
            this.fireball(this.c < 2);
            if (this.c-- < 2) {
               this.close();
            }
         }
      }

      private void castEffect() {
         for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 13) {
            Vector vec = UtilityMethods.rotate(new Vector(Math.cos(a), Math.sin(a), 0.0), this.caster.getPlayer().getEyeLocation().getDirection())
               .add(this.caster.getPlayer().getEyeLocation().getDirection().multiply(0.5))
               .multiply(0.3);
            this.caster
               .getPlayer()
               .getWorld()
               .spawnParticle(
                  Particle.FLAME,
                  this.caster.getPlayer().getLocation().add(0.0, 1.3, 0.0).add(this.caster.getPlayer().getEyeLocation().getDirection().multiply(0.5)),
                  0,
                  vec.getX(),
                  vec.getY(),
                  vec.getZ(),
                  0.3
               );
         }
      }

      private void close() {
         if (!this.isCancelled()) {
            this.cancel();
            HandlerList.unregisterAll(this);
         }
      }

      private void fireball(boolean last) {
         if (last) {
            this.caster.getPlayer().removePotionEffect(VPotionEffectType.SLOWNESS.get());
            this.caster.getPlayer().removePotionEffect(VPotionEffectType.SLOWNESS.get());
         }

         this.caster
            .getPlayer()
            .getWorld()
            .playSound(this.caster.getPlayer().getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, last ? 0.0F : 1.0F);
         (new BukkitRunnable() {
            int j = 0;
            final Vector vec = RageEffect.this.caster.getPlayer().getEyeLocation().getDirection();
            final Location loc = RageEffect.this.caster.getPlayer().getLocation().add(0.0, 1.3, 0.0);

            public void run() {
               if (this.j++ > 40) {
                  this.cancel();
               }

               this.loc.add(this.vec);
               if (this.j % 2 == 0) {
                  this.loc.getWorld().playSound(this.loc, Sound.BLOCK_FIRE_AMBIENT, 2.0F, 1.0F);
               }

               this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 4, 0.1, 0.1, 0.1, 0.0);
               this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 0);

               for (Entity target : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                  if (UtilityMethods.canTarget(RageEffect.this.caster.getPlayer(), this.loc, target)) {
                     this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 8);
                     this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 32, 0.0, 0.0, 0.0, 0.1);
                     this.loc.getWorld().playSound(this.loc, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.0F);
                     target.setFireTicks(target.getFireTicks() + RageEffect.this.ignite);
                     RageEffect.this.caster.attack((LivingEntity)target, RageEffect.this.damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                     this.cancel();
                  }
               }
            }
         }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      }

      public void run() {
         if (UtilityMethods.isInvalidated(this.caster)) {
            this.close();
         } else {
            this.b += 0.10471975511965977;

            for (int j = 0; j < this.c; j++) {
               double a = (Math.PI * 2) * j / this.count + this.b;
               this.caster
                  .getPlayer()
                  .spawnParticle(
                     Particle.FLAME, this.caster.getPlayer().getLocation().add(Math.cos(a) * 1.5, 1.0 + Math.sin(a * 1.5) * 0.7, Math.sin(a) * 1.5), 0
                  );
            }
         }
      }
   }
}
