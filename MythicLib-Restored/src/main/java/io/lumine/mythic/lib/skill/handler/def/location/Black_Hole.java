package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Black_Hole extends SkillHandler<LocationSkillResult> {
   public Black_Hole() {
      this.registerModifiers("radius", "duration");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, SkillMetadata skillMeta) {
      final Location loc = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double duration = skillMeta.getParameter("duration") * 20.0;
      final double radius = skillMeta.getParameter("radius");
      loc.getWorld().playSound(loc, VSound.ENTITY_ENDERMAN_TELEPORT.get(), 3.0F, 1.0F);
      (new BukkitRunnable() {
         int ti = 0;
         final double r = 4.0;

         public void run() {
            if (this.ti++ > Math.min(300.0, duration)) {
               this.cancel();
            }

            loc.getWorld().playSound(loc, VSound.BLOCK_NOTE_BLOCK_HAT.get(), 2.0F, 2.0F);
            loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), loc, 0);

            for (int j = 0; j < 3; j++) {
               double ran = Black_Hole.RANDOM.nextDouble() * Math.PI * 2.0;
               double ran_y = Black_Hole.RANDOM.nextDouble() * 2.0 - 1.0;
               double x = Math.cos(ran) * Math.sin(ran_y * Math.PI * 2.0);
               double z = Math.sin(ran) * Math.sin(ran_y * Math.PI * 2.0);
               Location loc1 = loc.clone().add(x * 4.0, ran_y * 4.0, z * 4.0);
               Vector v = loc.toVector().subtract(loc1.toVector());
               loc1.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), loc1, 0, v.getX(), v.getY(), v.getZ(), 0.1);
            }

            for (Entity entity : UtilityMethods.getNearbyChunkEntities(loc)) {
               if (entity.getLocation().distanceSquared(loc) < Math.pow(radius, 2.0) && UtilityMethods.canTarget(caster, entity)) {
                  entity.setVelocity(UtilityMethods.safeNormalize(loc.clone().subtract(entity.getLocation()).toVector()).multiply(0.5));
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
