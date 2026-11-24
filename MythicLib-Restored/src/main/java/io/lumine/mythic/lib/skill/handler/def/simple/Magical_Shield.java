package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class Magical_Shield extends SkillHandler<SimpleSkillResult> {
   public Magical_Shield() {
      this.registerModifiers("power", "radius", "duration");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult(meta.getCaster().getPlayer().isOnGround());
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      double duration = skillMeta.getParameter("duration");
      double radiusSquared = Math.pow(skillMeta.getParameter("radius"), 2.0);
      double power = skillMeta.getParameter("power") / 100.0;
      new Magical_Shield.MagicalShieldEffect(skillMeta.getCaster().getPlayer(), duration, radiusSquared, power);
   }

   public static class MagicalShieldEffect extends TemporaryListener {
      private final Location loc;
      private final double radiusSquared;
      private final double power;

      public MagicalShieldEffect(Player caster, double duration, double radius, double power) {
         super();
         this.loc = caster.getLocation().clone();
         this.radiusSquared = radius * radius;
         this.power = power;
         caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 3.0F, 0.0F);
         this.registerRunnable(
            new BukkitRunnable() {
               public void run() {
                  for (double j = 0.0; j < Math.PI / 2; j += Math.PI / (28 + Magical_Shield.RANDOM.nextInt(5))) {
                     for (double i = 0.0; i < Math.PI * 2; i += Math.PI / (14 + Magical_Shield.RANDOM.nextInt(5))) {
                        MagicalShieldEffect.this.loc
                           .getWorld()
                           .spawnParticle(
                              VParticle.REDSTONE.get(),
                              MagicalShieldEffect.this.loc
                                 .clone()
                                 .add(2.5 * Math.cos(i + j) * Math.sin(j), 2.5 * Math.cos(j), 2.5 * Math.sin(i + j) * Math.sin(j)),
                              1,
                              new DustOptions(Color.FUCHSIA, 1.0F)
                           );
                     }
                  }
               }
            },
            runnable -> runnable.runTaskTimer(MythicLib.plugin, 0L, 3L)
         );
         this.close((long)(duration * 20.0));
      }

      @EventHandler
      public void a(AttackEvent event) {
         if (event.getEntity() instanceof Player
            && event.getEntity().getWorld().equals(this.loc.getWorld())
            && event.getEntity().getLocation().distanceSquared(this.loc) < this.radiusSquared) {
            event.getDamage().multiplicativeModifier(1.0 - this.power);
         }
      }
   }
}
