package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMICustomLogInOutMessages;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeePlayer;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.regex.Matcher;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Messages.CMIMultiMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CustomMessagesListener implements Listener {
  CMI plugin;

  public CustomMessagesListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onQuit(PlayerQuitEvent playerQuitEvent) {
    if (PermissionsManager.CMIPerm.messages_disablequit.hasSetPermission(
        playerQuitEvent.getPlayer())) {
      playerQuitEvent.setQuitMessage("");
    } else {
      if (CMICustomLogInOutMessages.isLogoutDisabled()) {
        playerQuitEvent.setQuitMessage("");
      }

      if (CMICustomLogInOutMessages.getLogoutAutoHideFrom() > 0
          && CMICustomLogInOutMessages.getLogoutAutoHideFrom()
              < this.plugin.getUtilManager().getOnlinePlayerCount()) {
        playerQuitEvent.setQuitMessage("");
      } else {
        if (CMICustomLogInOutMessages.isLogoutNameFilterUse()) {
          Matcher matcher =
              CMICustomLogInOutMessages.matchLongLogoutNameFilter(
                  playerQuitEvent.getPlayer().getName());
          if (matcher != null && matcher.find()) {
            playerQuitEvent.setQuitMessage("");
            return;
          }
        }

        if (CMICustomLogInOutMessages.isLogoutCustomUse()) {
          Snd snd =
              new Snd()
                  .setSender(playerQuitEvent.getPlayer())
                  .setTarget(playerQuitEvent.getPlayer());
          String string = null;
          if (CMICustomLogInOutMessages.isLogoutServerSwitch()
              && this.plugin.getBungeeCordManager().isBungeeCord()
              && this.plugin.getBungeeCordManager().isCMIBPresent()) {
            BungeePlayer bungeePlayer =
                this.plugin
                    .getBungeeCordManager()
                    .getBungeePlayer(playerQuitEvent.getPlayer().getUniqueId());
            if (bungeePlayer != null
                && bungeePlayer.getPreviousServer() != null
                && bungeePlayer.getServerSwitchTime() + 300L > System.currentTimeMillis()
                && bungeePlayer
                    .getPreviousServer()
                    .equalsIgnoreCase(this.plugin.getBungeeCordManager().getThisServerName())) {
              string =
                  CMILC.info_ServerSwitchOut.getLocale(
                      snd, "[serverName]", bungeePlayer.getBungeeServer().getName());
            }
          }

          if (string == null) {
            string =
                CMILC.info_LogoutCustom.getLocale(
                    snd,
                    "[playerName]",
                    playerQuitEvent.getPlayer().getName(),
                    "[playername]",
                    playerQuitEvent.getPlayer().getName());
          }

          string =
              this.plugin
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(playerQuitEvent.getPlayer(), string);
          if (!string.isEmpty()) {
            playerQuitEvent.setQuitMessage(string);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onJoinMulti(PlayerQuitEvent playerQuitEvent) {
    if (CMICustomLogInOutMessages.isLogoutCustomUse()) {
      String string = playerQuitEvent.getQuitMessage();
      if (string != null && !string.isEmpty()) {
        if (new CMIMultiMessage(string).isMultiType()) {
          playerQuitEvent.setQuitMessage("");
          CMIMessages.broadcastMessage(playerQuitEvent.getPlayer(), string, true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent playerJoinEvent) {
    if (PermissionsManager.CMIPerm.messages_disablelogin.hasSetPermission(
        playerJoinEvent.getPlayer())) {
      playerJoinEvent.setJoinMessage("");
    } else {
      if (CMICustomLogInOutMessages.isLoginDisabled()) {
        playerJoinEvent.setJoinMessage("");
      }

      if (CMICustomLogInOutMessages.getLoginAutoHideFrom() > 0
          && CMICustomLogInOutMessages.getLoginAutoHideFrom()
              < this.plugin.getUtilManager().getOnlinePlayerCount()) {
        playerJoinEvent.setJoinMessage("");
      } else {
        if (CMICustomLogInOutMessages.isLoginNameFilterUse()) {
          Matcher matcher =
              CMICustomLogInOutMessages.matchLongLogoutNameFilter(
                  playerJoinEvent.getPlayer().getName());
          if (matcher != null && matcher.find()) {
            playerJoinEvent.setJoinMessage("");
            return;
          }
        }

        if (CMICustomLogInOutMessages.isLoginCustomUse()) {
          if (!playerJoinEvent.getPlayer().hasPlayedBefore()) {
            playerJoinEvent.setJoinMessage("");
            return;
          }

          Snd snd =
              new Snd()
                  .setSender(playerJoinEvent.getPlayer())
                  .setTarget(playerJoinEvent.getPlayer());
          String string = null;
          if (CMICustomLogInOutMessages.isLoginServerSwitch()
              && this.plugin.getBungeeCordManager().isBungeeCord()
              && this.plugin.getBungeeCordManager().isCMIBPresent()) {
            BungeePlayer bungeePlayer =
                this.plugin
                    .getBungeeCordManager()
                    .getBungeePlayer(playerJoinEvent.getPlayer().getUniqueId());
            if (bungeePlayer != null
                && bungeePlayer.getPreviousServer() != null
                && bungeePlayer.getServerSwitchTime() + 300L > System.currentTimeMillis()
                && bungeePlayer
                    .getBungeeServer()
                    .getName()
                    .equalsIgnoreCase(this.plugin.getBungeeCordManager().getThisServerName())) {
              string =
                  CMILC.info_ServerSwitchIn.getLocale(
                      snd, "[serverName]", bungeePlayer.getPreviousServer());
            }
          }

          if (string == null) {
            string =
                CMILC.info_LoginCustom.getLocale(
                    snd,
                    "[playerName]",
                    playerJoinEvent.getPlayer().getName(),
                    "[playername]",
                    playerJoinEvent.getPlayer().getName());
          }

          string =
              this.plugin
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(playerJoinEvent.getPlayer(), string);
          if (!string.isEmpty()) {
            playerJoinEvent.setJoinMessage(string);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onJoinMulti(PlayerJoinEvent playerJoinEvent) {
    if (CMICustomLogInOutMessages.isLoginCustomUse()) {
      String string = playerJoinEvent.getJoinMessage();
      if (string != null && !string.isEmpty()) {
        if (new CMIMultiMessage(string).isMultiType()) {
          playerJoinEvent.setJoinMessage("");
          CMIMessages.broadcastMessage(playerJoinEvent.getPlayer(), string, true);
        }
      }
    }
  }
}
