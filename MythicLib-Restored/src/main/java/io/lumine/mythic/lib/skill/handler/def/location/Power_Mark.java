package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Power_Mark extends SkillHandler<LocationSkillResult> {
   public Power_Mark() {
      this.registerModifiers("duration", "stun", "ratio");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, SkillMetadata skillMeta) {
      new Power_Mark.PowerMark(skillMeta.getCaster(), skillMeta, result.getTarget());
   }

   private Vector format(Vector vec) {
      return vec.length() < 0.01 ? new Vector(0, 0, 0) : vec.normalize();
   }

   public class PowerMark extends BukkitRunnable implements Listener {
      private final PlayerMetadata caster;
      private final Location loc;
      private final double duration;
      private final double ratio;
      private double stun;
      private double accumulate;
      private int j;

      public PowerMark(PlayerMetadata caster, SkillMetadata skillMeta, Location loc) {
         this.caster = caster;
         this.loc = loc;
         loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 2.0F, 1.0F);
         this.duration = skillMeta.getParameter("duration");
         this.ratio = skillMeta.getParameter("ratio") / 100.0;
         this.stun = skillMeta.getParameter("stun");
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      private void unregister() {
         PlayerAttackEvent.getHandlerList().unregister(this);
         this.cancel();
      }

      @EventHandler(priority = EventPriority.HIGHEST)
      public void stackDamage(PlayerAttackEvent event) {
         if (!event.isCancelled()
            && this.j < 20.0 * (this.duration - 2.0)
            && this.radiusCheck(event.getEntity().getLocation())
            && event.getAttacker().getPlayer().equals(this.caster.getPlayer())) {
            this.accumulate = this.accumulate + event.getAttack().getDamage().getDamage() * this.ratio;
            new ParabolicProjectile(
               event.getEntity().getLocation().add(0.0, event.getEntity().getHeight() / 2.0, 0.0),
               this.loc,
               () -> this.loc.getWorld().playSound(this.loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F),
               Color.PURPLE
            );
         }
      }

      private boolean radiusCheck(Location loc) {
         return loc.getWorld().equals(this.loc.getWorld()) && loc.distanceSquared(this.loc) < 16.0;
      }

      public void run() {
         if (this.j++ > this.duration * 20.0) {
            this.unregister();

            for (double a = 0.0; a < Math.PI * 2; a += 0.36959913571644626) {
               new ParabolicProjectile(this.loc, this.loc.clone().add(6.0 * Math.cos(a), 0.0, 6.0 * Math.sin(a)), VParticle.WITCH.get());
            }

            this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.0F);
            this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc.clone().add(0.0, 1.0, 0.0), 16, 2.0, 2.0, 2.0, 0.0);
            this.loc.getWorld().spawnParticle(VParticle.EXPLOSION.get(), this.loc.clone().add(0.0, 1.0, 0.0), 24, 0.0, 0.0, 0.0, 0.3F);
            this.stun = this.stun + Math.log(Math.max(1.0, this.accumulate - 10.0)) / 8.0;

            for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
               if (entity.getLocation().distanceSquared(this.loc) < 25.0 && UtilityMethods.canTarget(this.caster.getPlayer(), entity)) {
                  ((LivingEntity)entity).addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), (int)(this.stun * 20.0), 10, false, false));
                  this.caster.attack((LivingEntity)entity, this.accumulate, DamageType.SKILL, DamageType.MAGIC);
                  entity.setVelocity(Power_Mark.this.format(entity.getLocation().subtract(this.loc).toVector().setY(0)).setY(0.3));
               }
            }
         } else {
            if (this.j % 2 == 0 && this.j > 20.0 * (this.duration - 2.0)) {
               this.loc
                  .getWorld()
                  .playSound(this.loc, VSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0F, (float)(1.0 + (this.j - 20.0 * (this.duration - 2.0)) / 40.0));
            }

            double a = this.j / 16.0;
            double r = Math.sqrt(Math.min(this.duration * 2.0 - this.j / 10.0, 4.0)) * 2.0;

            for (double k = 0.0; k < Math.PI * 2; k += Math.PI * 2.0 / 5.0) {
               this.loc.getWorld().spawnParticle(VParticle.WITCH.get(), this.loc.clone().add(r * Math.cos(k + a), 0.0, r * Math.sin(k + a)), 0);
            }
         }
      }
   }
}
