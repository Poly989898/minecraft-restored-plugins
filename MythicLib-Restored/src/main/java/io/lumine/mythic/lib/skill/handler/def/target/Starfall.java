package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Starfall extends SkillHandler<TargetSkillResult> {
   public Starfall() {
      this.registerModifiers("damage");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      (new BukkitRunnable() {
         final double ran = Starfall.RANDOM.nextDouble() * Math.PI * 2.0;
         final Location loc = target.getLocation().add(Math.cos(this.ran) * 3.0, 6.0, Math.sin(this.ran) * 3.0);
         final Vector vec = target.getLocation().add(0.0, 0.65, 0.0).toVector().subtract(this.loc.toVector()).multiply(0.05);
         double ti = 0.0;

         public void run() {
            this.loc.getWorld().playSound(this.loc, VSound.BLOCK_NOTE_BLOCK_HAT.get(), 2.0F, 2.0F);

            for (int j = 0; j < 2; j++) {
               this.ti += 0.05;
               this.loc.add(this.vec);
               this.loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), this.loc, 1, 0.04, 0.0, 0.04, 0.0);
               if (this.ti >= 1.0) {
                  this.loc.getWorld().spawnParticle(VParticle.FIREWORK.get(), this.loc, 24, 0.0, 0.0, 0.0, 0.12);
                  this.loc.getWorld().playSound(this.loc, VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 1.0F, 2.0F);
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0F, 2.0F);
      skillMeta.getCaster().attack(target, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC);
   }
}
