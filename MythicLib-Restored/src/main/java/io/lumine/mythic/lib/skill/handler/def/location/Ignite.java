package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Ignite extends SkillHandler<LocationSkillResult> {
   public Ignite() {
      this.registerModifiers("duration", "max-ignite", "radius");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, SkillMetadata skillMeta) {
      Location loc = result.getTarget();
      Player caster = skillMeta.getCaster().getPlayer();
      int maxIgnite = (int)(skillMeta.getParameter("max-ignite") * 20.0);
      int ignite = (int)(skillMeta.getParameter("duration") * 20.0);
      double radiusSquared = Math.pow(skillMeta.getParameter("radius"), 2.0);
      loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), loc.add(0.0, 0.1, 0.0), 0);
      loc.getWorld().spawnParticle(Particle.LAVA, loc, 12);
      loc.getWorld().spawnParticle(Particle.FLAME, loc, 48, 0.0, 0.0, 0.13);
      loc.getWorld().playSound(loc, VSound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST.get(), 2.0F, 1.0F);

      for (Entity entity : UtilityMethods.getNearbyChunkEntities(loc)) {
         if (entity.getLocation().distanceSquared(loc) < radiusSquared && UtilityMethods.canTarget(caster, entity)) {
            entity.setFireTicks(Math.min(entity.getFireTicks() + ignite, maxIgnite));
         }
      }
   }
}
