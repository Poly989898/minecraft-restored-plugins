package io.lumine.mythic.lib.skill.handler.def.location;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.LocationSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Arcane_Hail extends SkillHandler<LocationSkillResult> {
   public Arcane_Hail() {
      this.registerModifiers("damage", "duration", "radius");
   }

   public LocationSkillResult getResult(SkillMetadata meta) {
      return new LocationSkillResult(meta);
   }

   public void whenCast(LocationSkillResult result, final SkillMetadata skillMeta) {
      final Location loc = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      final double damage = skillMeta.getParameter("damage");
      final double duration = skillMeta.getParameter("duration") * 10.0;
      final double radius = skillMeta.getParameter("radius");
      (new BukkitRunnable() {
         int j = 0;

         public void run() {
            if (this.j++ > duration) {
               this.cancel();
            } else {
               Location loc1 = loc.clone().add(Arcane_Hail.this.randomCoordMultiplier() * radius, 0.0, Arcane_Hail.this.randomCoordMultiplier() * radius);
               loc1.getWorld().playSound(loc1, VSound.ENTITY_ENDERMAN_HURT.get(), 1.0F, 0.0F);

               for (Entity entity : UtilityMethods.getNearbyChunkEntities(loc1)) {
                  if (UtilityMethods.canTarget(caster, entity) && entity.getLocation().distanceSquared(loc1) <= 4.0) {
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC);
                  }
               }

               loc1.getWorld().spawnParticle(VParticle.WITCH.get(), loc1, 12, 0.0, 0.0, 0.0, 0.1);
               loc1.getWorld().spawnParticle(VParticle.SMOKE.get(), loc1, 6, 0.0, 0.0, 0.0, 0.1);
               Vector vector = new Vector(Arcane_Hail.this.randomCoordMultiplier() * 0.03, 0.3, Arcane_Hail.this.randomCoordMultiplier() * 0.03);

               for (double k = 0.0; k < 60.0; k++) {
                  loc1.getWorld().spawnParticle(VParticle.WITCH.get(), loc1.add(vector), 0);
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 2L);
   }

   private double randomCoordMultiplier() {
      return (RANDOM.nextDouble() - 0.5) * 2.0;
   }
}
