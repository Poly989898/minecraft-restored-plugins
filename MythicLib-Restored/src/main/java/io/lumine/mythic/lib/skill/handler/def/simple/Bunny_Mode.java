package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Bunny_Mode extends SkillHandler<SimpleSkillResult> {
   private static final long JUMP_COOLDOWN = 300L;

   public Bunny_Mode() {
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
         final Bunny_Mode.BunnyModeEffect handler = new Bunny_Mode.BunnyModeEffect(caster);
         int j = 0;
         long lastJump = 0L;

         public void run() {
            if (this.j++ > duration) {
               this.handler.close(60L);
               this.cancel();
            } else {
               if (caster.getLocation().add(0.0, -0.3, 0.0).getBlock().getType().isSolid() && System.currentTimeMillis() - this.lastJump > 300L) {
                  this.lastJump = System.currentTimeMillis();
                  Vector dir = UtilityMethods.safeNormalize(caster.getEyeLocation().getDirection().setY(0), new Vector(0, 1, 0));
                  caster.setVelocity(dir.multiply(0.8 * xz).setY(0.5 * y));
                  caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 2.0F, 1.0F);

                  for (double a = 0.0; a < Math.PI * 2; a += Math.PI / 12) {
                     caster.getWorld().spawnParticle(Particle.CLOUD, caster.getLocation(), 0, Math.cos(a), 0.0, Math.sin(a), 0.2);
                  }
               }
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   public static class BunnyModeEffect extends TemporaryListener {
      private final Player player;

      public BunnyModeEffect(Player player) {
         super();
         this.player = player;
      }

      @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
      public void a(EntityDamageEvent event) {
         if (event.getEntity().equals(this.player) && event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
         }
      }
   }
}
