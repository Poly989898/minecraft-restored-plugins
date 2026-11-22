package com.Zrips.CMI.Modules.NickName;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatListener;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class NickNameListener implements Listener {
  private CMI plugin;

  public NickNameListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerLoginEvent(PlayerLoginEvent playerLoginEvent) {
    Player player = playerLoginEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null && cmiUser.getNickName() != null) {
      String string = this.plugin.getNickNameManager().getNickNameFormat();
      if (string.contains("{prefix}")) {
        string = string.replace("{prefix}", cmiUser.getPrefix());
      }

      if (string.contains("{suffix}")) {
        string = string.replace("{suffix}", cmiUser.getSuffix());
      }

      if (string.contains("{nickName}")) {
        string = string.replace("{nickName}", cmiUser.getNickName());
      }

      string = CMIChatColor.translate(string);
      player.setDisplayName(string);
    }
  }

  @Deprecated
  public static String processGradientChat(Player player, String string) {
    return ChatFormatListener.processGradientChat(player, string);
  }
}
