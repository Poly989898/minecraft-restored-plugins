package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Frog_Mode extends SkillHandler<SimpleSkillResult> {
   public Frog_Mode() {
      super("FROG_MODE");
      this.registerModifiers("duration", "jump-force", "speed");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      final double duration = skillMeta.getParameter("duration") * 20.0;
      final double y = skillMeta.getParameter("jump-force");
      final double xz = skillMeta.getParameter("speed");
      final Player caster = skillMeta.getCaster().getPlayer();
      (new BukkitRunnable() {
         int j = 0;

         public void run() {
            if (this.j++ > duration) {
               this.cancel();
            }

            if (caster.getLocation().getBlock().getType() == Material.WATER) {
               caster.setVelocity(caster.getEyeLocation().getDirection().setY(0).normalize().multiply(0.8 * xz).setY(0.5 / xz * y));
               caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 2.0F, 1.0F);

               for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 12) {
                  caster.getWorld().spawnParticle(Particle.CLOUD, caster.getLocation(), 0, Math.cos(a), 0.0, Math.sin(a), 0.2);
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }
}
