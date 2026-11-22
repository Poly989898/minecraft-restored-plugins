package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMICustomLogInOutMessages;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinListener implements Listener {
  CMI plugin;

  public FirstJoinListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerFirstLogin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!player.hasPlayedBefore()) {
      this.plugin.getPlayerManager().getUser(player).addForDelayedSave();
      if (CMICustomLogInOutMessages.isFirstJoinMessageUse()) {
        Snd snd =
            new Snd().setSender(playerJoinEvent.getPlayer()).setTarget(playerJoinEvent.getPlayer());
        String string =
            CMILC.info_FirstJoin.getLocale(
                snd,
                "[playername]",
                player.getName(),
                "[playerName]",
                player.getName(),
                "[totalUsers]",
                this.plugin.getPlayerManager().getTotalUserCount(),
                "[onlinePlayers]",
                Bukkit.getOnlinePlayers().size());
        string =
            this.plugin
                .getPlaceholderAPIManager()
                .updatePlaceHolders(playerJoinEvent.getPlayer(), string);
        if (!string.isEmpty()) {
          CMIMessages.broadcastMessage(player, string);
        }
      }
    }
  }
}
