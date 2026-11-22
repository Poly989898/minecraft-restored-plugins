package com.Zrips.CMI.Modules.ChatTag;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class TagListener implements Listener {
  private CMI plugin;

  public TagListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent playerChatTabCompleteEvent) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      String string = playerChatTabCompleteEvent.getLastToken();
      if (string.startsWith("@")) {
        string = string.replace("@", "").toLowerCase();

        for (Player player : Bukkit.getOnlinePlayers()) {
          if (player.getName().toLowerCase().startsWith(string)
              && playerChatTabCompleteEvent.getPlayer().canSee(player)) {
            playerChatTabCompleteEvent.getTabCompletions().add("@" + player.getName());
          }
        }

        for (String tabCompletion :
            this.plugin
                .getNickNameManager()
                .getNickNamesStartingWith(playerChatTabCompleteEvent.getPlayer(), string)) {
          playerChatTabCompleteEvent.getTabCompletions().add("@" + tabCompletion);
        }
      }
    }
  }
}
