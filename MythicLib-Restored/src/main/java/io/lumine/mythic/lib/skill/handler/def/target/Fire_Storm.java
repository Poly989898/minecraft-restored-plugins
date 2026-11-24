package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Fire_Storm extends SkillHandler<TargetSkillResult> {
   public Fire_Storm() {
      this.registerModifiers("damage", "ignite");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      final LivingEntity target = result.getTarget();
      final double damage = skillMeta.getParameter("damage");
      final int ignite = (int)(20.0 * skillMeta.getParameter("ignite"));
      caster.getPlayer().getWorld().playSound(caster.getPlayer().getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 1.0F);
      (new BukkitRunnable() {
            int j = 0;

            public void run() {
               if (this.j++ <= 5 && !UtilityMethods.isInvalidated(caster) && !target.isDead() && caster.getPlayer().getWorld().equals(target.getWorld())) {
                  caster.getPlayer().getWorld().playSound(caster.getPlayer().getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0F, 1.0F);
                  new ParabolicProjectile(
                     caster.getPlayer().getLocation().add(0.0, 1.0, 0.0),
                     target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0),
                     Fire_Storm.this.randomVector(caster.getPlayer()),
                     () -> {
                        target.getWorld().playSound(target.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_TWINKLE.get(), 1.0F, 2.0F);
                        target.getWorld()
                           .spawnParticle(VParticle.SMOKE.get(), target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0), 8, 0.0, 0.0, 0.0, 0.15);
                        skillMeta.getCaster().attack(target, damage, DamageType.SKILL, DamageType.MAGIC, DamageType.PROJECTILE);
                        target.setFireTicks(ignite);
                     },
                     2,
                     Particle.FLAME
                  );
               } else {
                  this.cancel();
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 4L);
   }

   private Vector randomVector(Player player) {
      double a = Math.toRadians(player.getEyeLocation().getYaw() + 90.0F);
      a += (RANDOM.nextBoolean() ? 1 : -1) * (RANDOM.nextDouble() * 2.0 + 1.0) * Math.PI / 6.0;
      return new Vector(Math.cos(a), 0.8, Math.sin(a)).normalize().multiply(0.4);
   }
}
