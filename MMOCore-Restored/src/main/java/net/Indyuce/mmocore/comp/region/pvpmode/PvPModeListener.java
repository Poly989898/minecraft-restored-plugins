package net.Indyuce.mmocore.comp.region.pvpmode;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.handler.Handler.Factory;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPModeListener implements Listener {
   public PvPModeListener() {
      Validate.isTrue(this.registerHandler(PvPModeHandler.FACTORY), "Could not register WG handler for PvP mode");
      if (MMOCore.plugin.getConfig().getBoolean("pvp_mode.invulnerability.apply_to_pvp_flag")) {
         Validate.isTrue(this.registerHandler(PvPFlagHandler.FACTORY), "Could not register WG handler for PvP");
      }
   }

   private boolean registerHandler(Factory<?> factory) {
      return WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(factory, null);
   }

   @EventHandler(ignoreCancelled = true)
   public void a(EntityDamageByEntityEvent event) {
      if (UtilityMethods.isRealPlayer(event.getEntity())) {
         Player source = UtilityMethods.getPlayerDamager(event);
         if (source != null) {
            Player target = (Player)event.getEntity();
            PlayerData targetData = PlayerData.get(target);
            PlayerData sourceData = PlayerData.get(source);
            int minLevel = MMOCore.plugin.configManager.minCombatLevel;
            if (minLevel > 0) {
               if (targetData.getLevel() < minLevel) {
                  event.setCancelled(true);
                  if (event.getDamage() > 0.0) {
                     ConfigMessage.fromKey("pvp-mode.cannot-hit.low-level-target").send(source);
                  }

                  return;
               }

               if (sourceData.getLevel() < minLevel) {
                  event.setCancelled(true);
                  if (event.getDamage() > 0.0) {
                     ConfigMessage.fromKey("pvp-mode.cannot-hit.low-level-self").send(source);
                  }

                  return;
               }

               int maxLevelDiff = MMOCore.plugin.configManager.maxCombatLevelDifference;
               if (maxLevelDiff > 0 && Math.abs(targetData.getLevel() - sourceData.getLevel()) > maxLevelDiff) {
                  event.setCancelled(true);
                  if (event.getDamage() > 0.0) {
                     ConfigMessage.fromKey("pvp-mode.cannot-hit.high-level-difference").send(source);
                  }

                  return;
               }
            }

            if (targetData.getCombat().isInvulnerable()) {
               if (event.getDamage() > 0.0) {
                  long left = targetData.getCombat().getInvulnerableTill() - System.currentTimeMillis();
                  ConfigMessage.fromKey("pvp-mode.cannot-hit.invulnerable-target", "left", MythicLib.plugin.getMMOConfig().decimal.format(left / 1000.0))
                     .send(source);
               }

               event.setCancelled(true);
            } else if (!MMOCore.plugin.configManager.pvpModeInvulnerabilityCanDamage && sourceData.getCombat().isInvulnerable()) {
               if (event.getDamage() > 0.0) {
                  long left = sourceData.getCombat().getInvulnerableTill() - System.currentTimeMillis();
                  ConfigMessage.fromKey("pvp-mode.cannot-hit.invulnerable-self", "left", MythicLib.plugin.getMMOConfig().decimal.format(left / 1000.0))
                     .send(source);
               }

               event.setCancelled(true);
            } else if (MythicLib.plugin.getFlags().isFlagAllowed(target.getLocation(), CustomFlag.PVP_MODE)) {
               if (!targetData.getCombat().isInPvpMode()) {
                  event.setCancelled(true);
                  if (event.getDamage() > 0.0) {
                     ConfigMessage.fromKey("pvp-mode.cannot-hit.pvp-mode-disabled-target").send(source);
                  }
               } else if (!sourceData.getCombat().isInPvpMode()) {
                  event.setCancelled(true);
                  if (event.getDamage() > 0.0) {
                     ConfigMessage.fromKey("pvp-mode.cannot-hit.pvp-mode-disabled-self").send(source);
                  }
               }
            }
         }
      }
   }
}
