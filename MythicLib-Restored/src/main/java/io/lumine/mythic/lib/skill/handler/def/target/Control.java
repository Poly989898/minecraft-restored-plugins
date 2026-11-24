package io.lumine.mythic.lib.skill.handler.def.target;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.TargetSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Control extends SkillHandler<TargetSkillResult> {
   public Control() {
      this.registerModifiers("knockback", "duration");
   }

   public TargetSkillResult getResult(SkillMetadata meta) {
      return new TargetSkillResult(meta);
   }

   public void whenCast(TargetSkillResult result, SkillMetadata skillMeta) {
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 1.0F);
      result.getTarget().addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), 40, 0));
      new Control.TelekinesyRunnable(skillMeta.getCaster(), result.getTarget(), skillMeta.getParameter("knockback") / 100.0, skillMeta.getParameter("duration"));
   }

   public static class TelekinesyRunnable extends BukkitRunnable implements Listener {
      private final LivingEntity entity;
      private final PlayerMetadata caster;
      private final double f;
      private final double d;
      private int j;

      public TelekinesyRunnable(PlayerMetadata caster, LivingEntity entity, double force, double duration) {
         this.entity = entity;
         this.caster = caster;
         this.d = duration * 20.0;
         this.f = force;
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      @EventHandler
      public void a(PlayerInteractEvent event) {
         if (event.getPlayer().equals(this.caster.getPlayer()) && event.getAction().name().contains("LEFT_CLICK")) {
            Vector vec = this.caster.getPlayer().getEyeLocation().getDirection().multiply(3.0 * this.f);
            vec.setY(Math.max(0.5, vec.getY() / 2.0));
            this.entity.setVelocity(vec);
            PotionEffect effect = this.entity.getPotionEffect(VPotionEffectType.SLOWNESS.get());
            if (effect.getDuration() < this.d && effect.getAmplifier() == 0) {
               this.entity.removePotionEffect(VPotionEffectType.SLOWNESS.get());
            }

            this.entity.getWorld().spawnParticle(VParticle.WITCH.get(), this.entity.getLocation().add(0.0, this.entity.getHeight() / 2.0, 0.0), 16);
            this.entity.getWorld().playSound(this.entity.getLocation(), VSound.ENTITY_FIREWORK_ROCKET_BLAST.get(), 2.0F, 1.0F);
            this.close();
         }
      }

      public void run() {
         if (!UtilityMethods.isInvalidated(this.caster) && !this.entity.isDead() && !(this.j++ >= this.d)) {
            double a = this.j / 3.0;
            this.entity
               .getWorld()
               .spawnParticle(VParticle.WITCH.get(), this.entity.getLocation().add(Math.cos(a), this.entity.getHeight() / 2.0, Math.sin(a)), 0);
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
