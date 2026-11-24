package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Leap extends SkillHandler<SimpleSkillResult> {
   public Leap() {
      this.registerModifiers("force");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult(meta.getCaster().getPlayer().isOnGround());
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      final Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 1.0F, 0.0F);
      caster.getWorld().spawnParticle(VParticle.EXPLOSION.get(), caster.getLocation(), 16, 0.0, 0.0, 0.1);
      Vector vec = caster.getEyeLocation().getDirection().multiply(2.0 * skillMeta.getParameter("force"));
      vec.setY(vec.getY() / 2.0);
      caster.setVelocity(vec);
      (new BukkitRunnable() {
         double ti = 0.0;

         public void run() {
            this.ti++;
            if (this.ti > 20.0) {
               this.cancel();
            }

            caster.getWorld().spawnParticle(Particle.CLOUD, caster.getLocation().add(0.0, 1.0, 0.0), 0);
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
