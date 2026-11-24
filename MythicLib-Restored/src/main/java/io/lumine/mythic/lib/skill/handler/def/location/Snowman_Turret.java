package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Snowman_Turret extends SkillHandler<LocationSkillResult> {
   public Snowman_Turret() {
      this.registerModifiers("duration", "damage", "radius");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, final SkillMetadata skillMeta) {
      Location loc = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = Math.min(skillMeta.getParameter("duration") * 20.0, 300.0);
      final double radiusSquared = Math.pow(skillMeta.getParameter("radius"), 2.0);
      loc.getWorld().playSound(loc, VSound.ENTITY_ENDERMAN_TELEPORT.get(), 2.0F, 1.0F);
      final Snowman snowman = (Snowman)loc.getWorld().spawn(loc.add(0.0, 1.0, 0.0), Snowman.class);
      snowman.setInvulnerable(true);
      snowman.addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), 100000, 254, true));
      (new BukkitRunnable() {
            int ti = 0;
            double j = 0.0;
            final Snowman_Turret.TurretHandler turret = new Snowman_Turret.TurretHandler(skillMeta.getParameter("damage"));

            public void run() {
               if (this.ti++ > duration || UtilityMethods.isInvalidated(caster) || snowman.isDead()) {
                  this.turret.close(60L);
                  snowman.remove();
                  this.cancel();
               }

               this.j += 0.1308996938995747;

               for (double k = 0.0; k < 3.0; k++) {
                  snowman.getWorld()
                     .spawnParticle(
                        VParticle.INSTANT_EFFECT.get(),
                        snowman.getLocation().add(Math.cos(this.j + k / 3.0 * 2.0 * Math.PI) * 1.3, 1.0, Math.sin(this.j + k / 3.0 * 2.0 * Math.PI) * 1.3),
                        0
                     );
               }

               snowman.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), snowman.getLocation().add(0.0, 1.0, 0.0), 1, 0.0, 0.0, 0.0, 0.2);
               if (this.ti % 2 == 0) {
                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(snowman.getLocation())) {
                     if (!entity.equals(snowman)
                        && UtilityMethods.canTarget(caster, entity)
                        && entity.getLocation().distanceSquared(snowman.getLocation()) < radiusSquared) {
                        snowman.getWorld().playSound(snowman.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0F, 1.3F);
                        Snowball snowball = (Snowball)snowman.launchProjectile(Snowball.class);
                        snowball.setVelocity(
                           entity.getLocation()
                              .add(0.0, entity.getHeight() / 2.0, 0.0)
                              .toVector()
                              .subtract(snowman.getLocation().add(0.0, 1.0, 0.0).toVector())
                              .normalize()
                              .multiply(1.3)
                        );
                        this.turret.entities.add(snowball.getUniqueId());
                        break;
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   public static class TurretHandler extends TemporaryListener {
      private final List<UUID> entities = new ArrayList<>();
      private final double damage;

      public TurretHandler(double damage) {
         super();
         this.damage = damage;
      }

      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(EntityDamageByEntityEvent event) {
         if (this.entities.contains(event.getDamager().getUniqueId())) {
            event.setDamage(this.damage);
         }
      }
   }
}
