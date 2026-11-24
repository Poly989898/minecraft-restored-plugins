package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Magical_Path extends SkillHandler<SimpleSkillResult> {
   public Magical_Path() {
      this.registerModifiers("duration");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      new Magical_Path.MagicalPathEffect(skillMeta.getCaster().getPlayer(), skillMeta.getParameter("duration"));
   }

   public class MagicalPathEffect extends BukkitRunnable implements Listener {
      private final Player player;
      private final long duration;
      private boolean safe = true;
      private int j = 0;

      public MagicalPathEffect(Player player, double duration) {
         this.player = player;
         this.duration = (long)(duration * 10.0);
         player.setAllowFlight(true);
         player.setFlying(true);
         player.setVelocity(player.getVelocity().setY(0.5));
         player.getWorld().playSound(player.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 1.0F, 1.0F);
         this.runTaskTimer(MythicLib.plugin, 0L, 2L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      public void close() {
         this.player.setAllowFlight(false);
         HandlerList.unregisterAll(this);
         this.cancel();
      }

      @EventHandler(priority = EventPriority.LOW)
      public void a(EntityDamageEvent event) {
         if (this.safe && event.getEntity().equals(this.player) && event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
            this.safe = false;
            this.player.getWorld().spawnParticle(VParticle.EFFECT.get(), this.player.getLocation(), 8, 0.35, 0.0, 0.35, 0.08);
            this.player.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), this.player.getLocation(), 16, 0.35, 0.0, 0.35, 0.08);
            this.player.getWorld().playSound(this.player.getLocation(), VSound.ENTITY_ENDERMAN_HURT.get(), 1.0F, 2.0F);
         }
      }

      @EventHandler
      public void b(PlayerQuitEvent event) {
         if (event.getPlayer().equals(this.player)) {
            this.close();
         }
      }

      public void run() {
         if (this.j++ > this.duration) {
            this.player.getWorld().playSound(this.player.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 1.0F, 1.0F);
            this.player.setAllowFlight(false);
            this.cancel();
         } else {
            this.player.getWorld().spawnParticle(VParticle.EFFECT.get(), this.player.getLocation(), 8, 0.5, 0.0, 0.5, 0.1);
            this.player.getWorld().spawnParticle(VParticle.INSTANT_EFFECT.get(), this.player.getLocation(), 16, 0.5, 0.0, 0.5, 0.1);
         }
      }
   }
}
