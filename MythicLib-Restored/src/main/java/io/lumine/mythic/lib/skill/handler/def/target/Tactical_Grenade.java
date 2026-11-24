package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Tactical_Grenade extends SkillHandler<TargetSkillResult> {
   public Tactical_Grenade() {
      this.registerModifiers("knock-up", "damage", "radius");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
            final Location loc = caster.getLocation().add(0.0, 0.1, 0.0);
            final double radius = skillMeta.getParameter("radius");
            final double knockup = 0.7 * skillMeta.getParameter("knock-up");
            final List<Integer> hit = new ArrayList<>();
            int j = 0;

            public void run() {
               if (!target.isDead() && target.getWorld().equals(this.loc.getWorld()) && this.j++ <= 200) {
                  Vector vec = target.getLocation().add(0.0, 0.1, 0.0).subtract(this.loc).toVector();
                  vec = vec.length() < 3.0 ? vec : vec.normalize().multiply(3);
                  this.loc.add(vec);
                  this.loc.getWorld().spawnParticle(Particle.CLOUD, this.loc, 32, 1.0, 0.0, 1.0, 0.0);
                  this.loc.getWorld().spawnParticle(VParticle.EXPLOSION.get(), this.loc, 16, 1.0, 0.0, 1.0, 0.05);
                  this.loc.getWorld().playSound(this.loc, Sound.BLOCK_ANVIL_LAND, 2.0F, 0.0F);
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);

                  for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                     if (!this.hit.contains(entity.getEntityId())
                        && UtilityMethods.canTarget(caster, entity)
                        && entity.getLocation().distanceSquared(this.loc) < this.radius * this.radius) {
                        this.hit.add(entity.getEntityId());
                        if (entity.equals(target)) {
                           this.cancel();
                        }

                        skillMeta.getCaster().attack((LivingEntity)entity, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC);
                        entity.setVelocity(entity.getVelocity().add(Tactical_Grenade.this.offsetVector(this.knockup)));
                     }
                  }
               } else {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 12L);
   }

   private Vector offsetVector(double y) {
      return new Vector(2.0 * (RANDOM.nextDouble() - 0.5), y, 2.0 * (RANDOM.nextDouble() - 0.5));
   }
}
