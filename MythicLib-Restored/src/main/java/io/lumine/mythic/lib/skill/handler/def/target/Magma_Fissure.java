package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Magma_Fissure extends SkillHandler<TargetSkillResult> {
   public Magma_Fissure() {
      this.registerModifiers("ignite", "damage");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, final SkillMetadata skillMeta) {
      final LivingEntity target = result.getTarget();
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         final Location loc = caster.getLocation().add(0.0, 0.2, 0.0);
         int j = 0;

         public void run() {
            this.j++;
            if (!target.isDead() && target.getWorld().equals(this.loc.getWorld()) && this.j <= 200) {
               Vector vec = target.getLocation().add(0.0, 0.2, 0.0).subtract(this.loc).toVector().normalize().multiply(0.6);
               this.loc.add(vec);
               this.loc.getWorld().spawnParticle(Particle.LAVA, this.loc, 2, 0.2, 0.0, 0.2, 0.0);
               this.loc.getWorld().spawnParticle(Particle.FLAME, this.loc, 2, 0.2, 0.0, 0.2, 0.0);
               this.loc.getWorld().spawnParticle(VParticle.SMOKE.get(), this.loc, 2, 0.2, 0.0, 0.2, 0.0);
               this.loc.getWorld().playSound(this.loc, VSound.BLOCK_NOTE_BLOCK_HAT.get(), 1.0F, 1.0F);
               if (target.getLocation().distanceSquared(this.loc) < 1.0) {
                  this.loc.getWorld().playSound(this.loc, Sound.ENTITY_BLAZE_HURT, 2.0F, 1.0F);
                  target.setFireTicks((int)(target.getFireTicks() + skillMeta.getParameter("ignite") * 20.0));
                  skillMeta.getCaster().attack(target, skillMeta.getParameter("damage"), DamageType.SKILL, DamageType.MAGIC);
                  this.cancel();
               }
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
