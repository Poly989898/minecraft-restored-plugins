package io.lumine.mythic.lib.skill.handler.def.simple;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Shadow_Veil extends SkillHandler<SimpleSkillResult> {
   public Shadow_Veil() {
      this.registerModifiers("duration", "deception");
   }

   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult();
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      double duration = skillMeta.getParameter("duration");
      Player caster = skillMeta.getCaster().getPlayer();
      caster.getWorld().playSound(caster.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 3.0F, 0.0F);

      for (Player online : Bukkit.getOnlinePlayers()) {
         online.hidePlayer(MythicLib.plugin, caster);
      }

      for (Mob serverEntities : caster.getWorld().getEntitiesByClass(Mob.class)) {
         if (serverEntities.getTarget() != null && serverEntities.getTarget().equals(caster)) {
            serverEntities.setTarget(null);
         }
      }

      Shadow_Veil.ShadowVeilEffect svh = new Shadow_Veil.ShadowVeilEffect(caster, duration);
      svh.setDeceptions(SilentNumbers.floor(skillMeta.getParameter("deception")));
   }

   public class ShadowVeilEffect extends BukkitRunnable implements Listener {
      private final Player player;
      private final double duration;
      private final Location loc;
      int deceptions = 1;
      double ti = 0.0;
      double y = 0.0;

      public void setDeceptions(int dec) {
         this.deceptions = dec;
      }

      public ShadowVeilEffect(Player player, double duration) {
         this.player = player;
         this.duration = duration;
         this.loc = player.getLocation();
         this.runTaskTimer(MythicLib.plugin, 0L, 1L);
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }

      private void close() {
         if (!(this.ti < 0.0)) {
            this.player.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.player.getLocation().add(0.0, 1.0, 0.0), 32, 0.0, 0.0, 0.0, 0.13);
            this.player.getWorld().playSound(this.player.getLocation(), VSound.ENTITY_ENDERMAN_TELEPORT.get(), 3.0F, 0.0F);
            this.ti = -1.0;
            EntityDamageByEntityEvent.getHandlerList().unregister(this);
            EntityTargetEvent.getHandlerList().unregister(this);

            for (Player online : Bukkit.getOnlinePlayers()) {
               online.showPlayer(MythicLib.plugin, this.player);
            }

            this.cancel();
         }
      }

      public void run() {
         if (!(this.ti++ > this.duration * 20.0) && !UtilityMethods.isInvalidated(this.player)) {
            if (this.y < 4.0) {
               for (int j1 = 0; j1 < 5; j1++) {
                  this.y += 0.04;

                  for (int j = 0; j < 4; j++) {
                     double a = this.y * Math.PI * 0.8 + j * Math.PI / 2.0;
                     this.player.getWorld().spawnParticle(VParticle.LARGE_SMOKE.get(), this.loc.clone().add(Math.cos(a) * 2.5, this.y, Math.sin(a) * 2.5), 0);
                  }
               }
            }
         } else {
            this.close();
         }
      }

      @EventHandler
      public void cancelShadowVeil(EntityDamageByEntityEvent event) {
         if (event.getDamager().equals(this.player)) {
            this.deceptions--;
            if (this.deceptions <= 0) {
               this.close();
            }
         }
      }

      @EventHandler
      public void cancelMobTarget(EntityTargetEvent event) {
         if (event.getTarget() != null && event.getTarget().equals(this.player)) {
            event.setCancelled(true);
         }
      }
   }
}
