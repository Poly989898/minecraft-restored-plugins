package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Contamination extends SkillHandler<LocationSkillResult> {
   public Contamination() {
      this.registerModifiers("damage", "duration");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, final SkillMetadata skillMeta) {
      final Location loc = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = Math.min(30.0, skillMeta.getParameter("duration")) * 20.0;
      loc.add(0.0, 0.1, 0.0);
      (new BukkitRunnable() {
            final double dps = skillMeta.getParameter("damage") / 2.0;
            double ti = 0.0;
            int j = 0;

            public void run() {
               if (this.j++ >= duration) {
                  this.cancel();
               }

               loc.getWorld()
                  .spawnParticle(
                     VParticle.REDSTONE.get(),
                     loc.clone().add(Math.cos(this.ti / 3.0) * 5.0, 0.0, Math.sin(this.ti / 3.0) * 5.0),
                     1,
                     new DustOptions(Color.PURPLE, 1.0F)
                  );

               for (int j = 0; j < 3; j++) {
                  this.ti += 0.09817477042468103;
                  double r = Math.sin(this.ti / 2.0) * 4.0;

                  for (double k = 0.0; k < Math.PI * 2; k += Math.PI * 2.0 / 3.0) {
                     loc.getWorld()
                        .spawnParticle(VParticle.WITCH.get(), loc.clone().add(r * Math.cos(k + this.ti / 4.0), 0.0, r * Math.sin(k + this.ti / 4.0)), 0);
                  }
               }

               if (this.j % 10 == 0) {
                  loc.getWorld().playSound(loc, VSound.ENTITY_ENDERMAN_HURT.get(), 2.0F, 1.0F);

                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(loc)) {
                     if (UtilityMethods.canTarget(caster, entity) && entity.getLocation().distanceSquared(loc) <= 25.0) {
                        skillMeta.getCaster().attack((LivingEntity)entity, this.dps, false, DamageType.SKILL, DamageType.MAGIC);
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
