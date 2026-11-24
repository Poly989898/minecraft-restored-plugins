package io.lumine.mythic.lib.skill;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.skill.result.SkillResult;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CastingDelayHandler extends TemporaryListener {
   private final SkillMetadata metadata;
   private final double slowness;
   private final int delayTicks;
   @Nullable
   private final BossBar bossbar;
   @Nullable
   private final NamespacedKey bossbarKey;
   private static final String MOVEMENT_SPEED_MODIFIER_KEY = "mythiclibSkillCasting";

   public CastingDelayHandler(final SkillMetadata metadata, final SkillResult result) {
      super();
      this.metadata = metadata;
      this.delayTicks = (int)(metadata.getParameter("delay") * 20.0);
      this.registerRunnable(
         new BukkitRunnable() {
            private int counter = CastingDelayHandler.this.delayTicks;

            public void run() {
               if (UtilityMethods.isInvalidated(CastingDelayHandler.this.getCaster())) {
                  CastingDelayHandler.this.close();
               } else {
                  if (CastingDelayHandler.this.bossbar != null) {
                     CastingDelayHandler.this.bossbar
                        .setProgress((double)(CastingDelayHandler.this.delayTicks - this.counter) / CastingDelayHandler.this.delayTicks);
                  }

                  this.counter--;
                  if (this.counter <= 0) {
                     CastingDelayHandler.this.close();
                     if (CastingDelayHandler.this.bossbarKey != null) {
                        Bukkit.getServer().removeBossBar(CastingDelayHandler.this.bossbarKey);
                     }

                     metadata.getCast().castInstantly(metadata, result);
                  }
               }
            }
         },
         runnable -> runnable.runTaskTimer(MythicLib.plugin, 0L, 1L)
      );
      this.castIfNotNull(MythicLib.plugin.getMMOConfig().skillCastScript);
      this.slowness = MythicLib.plugin.getMMOConfig().castingDelaySlowness;
      if (this.slowness > 0.0) {
         new StatModifier("mythiclibSkillCasting", "MOVEMENT_SPEED", -this.slowness, ModifierType.RELATIVE).register(this.getCaster());
      }

      if (MythicLib.plugin.getMMOConfig().enableCastingDelayBossbar) {
         this.bossbarKey = new NamespacedKey(MythicLib.plugin, "skill_casting_" + this.getCaster().getUniqueId());
         this.bossbar = Bukkit.createBossBar(
            this.bossbarKey,
            MythicLib.plugin.getMMOConfig().castingDelayBossbarFormat,
            MythicLib.plugin.getMMOConfig().castingDelayBarColor,
            MythicLib.plugin.getMMOConfig().castingDelayBarStyle,
            new BarFlag[0]
         );
         this.bossbar.addPlayer(this.getCaster().getPlayer());
      } else {
         this.bossbarKey = null;
         this.bossbar = null;
      }
   }

   @NotNull
   public MMOPlayerData getCaster() {
      return this.metadata.getCaster().getData();
   }

   public boolean hasBossbar() {
      return this.bossbar != null;
   }

   private void castIfNotNull(@Nullable Skill skill) {
      if (skill != null) {
         skill.cast(new SkillMetadata(skill, this.getCaster()));
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onMove(PlayerMoveEvent event) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         if (event.getPlayer().equals(this.getCaster().getPlayer())) {
            if (MythicLib.plugin.getMMOConfig().castingDelayCancelOnMove) {
               event.setCancelled(true);
               this.castIfNotNull(MythicLib.plugin.getMMOConfig().skillCancelScript);
               this.close();
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCast(PlayerCastSkillEvent event) {
      if (event.getPlayer().equals(this.getCaster().getPlayer())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onLogout(PlayerQuitEvent event) {
      if (event.getPlayer().equals(this.getCaster().getPlayer())) {
         this.close();
      }
   }

   @Override
   public void whenClosed() {
      this.bossbar.removeAll();
      if (this.slowness > 0.0) {
         this.getCaster().getStatMap().getInstance("MOVEMENT_SPEED").removeIf("mythiclibSkillCasting"::equals);
      }
   }
}
