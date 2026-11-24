package io.lumine.mythic.lib.skill.handler.def.vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.VectorSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Ice_Crystal extends SkillHandler<VectorSkillResult> {
   public Ice_Crystal() {
      this.registerModifiers("damage", "duration", "amplifier");
   }

   public VectorSkillResult getResult(SkillMetadata meta) {
      return new VectorSkillResult(meta);
   }

   public void whenCast(final VectorSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
      (new BukkitRunnable() {
            final Vector vec = result.getTarget().multiply(0.45);
            final Location loc = caster.getEyeLocation().clone().add(0.0, -0.3, 0.0);
            int ti = 0;

            public void run() {
               if (this.ti++ > 25) {
                  this.cancel();
               }

               this.loc.getWorld().playSound(this.loc, Sound.BLOCK_GLASS_BREAK, 2.0F, 1.0F);
               List<Entity> entities = UtilityMethods.getNearbyChunkEntities(this.loc);

               for (int j = 0; j < 3; j++) {
                  this.loc.add(this.vec);
                  if (this.loc.getBlock().getType().isSolid()) {
                     this.cancel();
                  }

                  for (double r = 0.0; r < 0.4; r += 0.1) {
                     for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 2) {
                        Vector vec = UtilityMethods.rotate(
                           new Vector(r * Math.cos(a + this.ti / 10.0), r * Math.sin(a + this.ti / 10.0), 0.0), this.loc.getDirection()
                        );
                        this.loc.add(vec);
                        this.loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), this.loc, 1, new DustOptions(Color.WHITE, 0.7F));
                        this.loc.add(vec.multiply(-1));
                     }
                  }

                  for (Entity entity : entities) {
                     if (UtilityMethods.canTarget(caster, this.loc, entity)) {
                        this.loc.getWorld().spawnParticle(VParticle.LARGE_EXPLOSION.get(), this.loc, 0);
                        this.loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), this.loc, 48, 0.0, 0.0, 0.0, 0.2);
                        this.loc.getWorld().playSound(this.loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 1.0F);
                        skillMeta.getCaster()
                           .attack((LivingEntity)entity, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                        ((LivingEntity)entity)
                           .addPotionEffect(
                              new PotionEffect(
                                 VPotionEffectType.SLOWNESS.get(), (int)(skillMeta.getParameter("duration") * 20.0), (int)skillMeta.getParameter("amplifier")
                              )
                           );
                        this.cancel();
                        return;
                     }
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
