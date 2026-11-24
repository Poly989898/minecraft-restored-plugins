package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VPotionEffectType;
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

public class Earthquake extends SkillHandler<VectorSkillResult> {
   public Earthquake() {
      this.registerModifiers("damage", "duration", "amplifier");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return meta.getCaster().getPlayer().isOnGround() ? new VectorSkillResult(meta) : new VectorSkillResult((Vector)null);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final double damage = skillMeta.getParameter("damage");
      final double slowDuration = skillMeta.getParameter("duration");
      final double slowAmplifier = skillMeta.getParameter("amplifier");
      (new BukkitRunnable() {
            final Vector vec = result.getTarget().setY(0);
            final Location loc = caster.getLocation();
            final List<Integer> hit = new ArrayList<>();
            int ti = 0;

            public void run() {
               this.ti++;
               if (this.ti > 20) {
                  this.cancel();
               }

               this.loc.add(this.vec);
               this.loc.getWorld().spawnParticle(Particle.CLOUD, this.loc, 5, 0.5, 0.0, 0.5, 0.0);
               this.loc.getWorld().playSound(this.loc, Sound.BLOCK_GRAVEL_BREAK, 2.0F, 1.0F);

               for (Entity entity : UtilityMethods.getNearbyChunkEntities(this.loc)) {
                  if (UtilityMethods.canTarget(caster, entity)
                     && this.loc.distanceSquared(entity.getLocation()) < 2.0
                     && !this.hit.contains(entity.getEntityId())) {
                     this.hit.add(entity.getEntityId());
                     skillMeta.getCaster().attack((LivingEntity)entity, damage, DamageType.SKILL, DamageType.MAGIC);
                     UtilityMethods.forcePotionEffect((LivingEntity)entity, VPotionEffectType.SLOWNESS.get(), slowDuration, (int)slowAmplifier);
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
