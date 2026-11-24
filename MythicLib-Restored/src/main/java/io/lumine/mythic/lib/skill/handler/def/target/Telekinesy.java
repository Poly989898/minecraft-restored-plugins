package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Telekinesy extends SkillHandler<TargetSkillResult> {
   public Telekinesy() {
      this.registerModifiers("knockback", "duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 1.0F);
      new Telekinesy.TelekinesyRunnable(
         skillMeta.getCaster().getData(), result.getTarget(), skillMeta.getParameter("duration"), skillMeta.getParameter("knockback") / 100.0
      );
   }

   public static class TelekinesyRunnable extends BukkitRunnable implements Listener {
      private final Entity entity;
      private final MMOPlayerData caster;
      private final long duration;
      private final double d;
      private final double f;
      private int j;

      public TelekinesyRunnable(MMOPlayerData caster, Entity entity, double duration, double force) {
         this.entity = entity;
         this.caster = caster;
         this.d = caster.getPlayer().getLocation().distance(entity.getLocation());
         this.f = force;
         this.duration = (long)(20.0 * duration);
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      @EventHandler
      public void a(PlayerInteractEvent event) {
         if (event.getPlayer().equals(this.caster.getPlayer()) && event.getAction().name().contains("LEFT_CLICK")) {
            this.entity.setVelocity(this.caster.getPlayer().getEyeLocation().getDirection().multiply(1.5 * this.f));
            this.entity.getWorld().playSound(this.entity.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 2.0F, 1.0F);
            this.entity.getWorld().spawnParticle(VParticle.WITCH.get(), this.entity.getLocation().add(0.0, this.entity.getHeight() / 2.0, 0.0), 16);
            this.close();
         }
      }

      public void run() {
         if (!UtilityMethods.isInvalidated(this.caster) && !this.entity.isDead() && this.j++ < this.duration) {
            if (this.j % 8 == 0) {
               new ParabolicProjectile(
                  this.caster.getPlayer().getEyeLocation(), this.entity.getLocation().add(0.0, this.entity.getHeight() / 2.0, 0.0), VParticle.WITCH.get()
               );
            }

            Location loc = this.caster.getPlayer().getEyeLocation().add(this.caster.getPlayer().getEyeLocation().getDirection().multiply(this.d));
            this.entity.setVelocity(loc.subtract(this.entity.getLocation().add(0.0, this.entity.getHeight() / 2.0, 0.0)).toVector().multiply(2));
            this.entity.setFallDistance(0.0F);
         } else {
            this.close();
         }
      }

      private void close() {
         this.cancel();
         HandlerList.unregisterAll(this);
      }
   }
}
