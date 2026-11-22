package com.Zrips.CMI.Modules.CmdCooldown;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CooldownListener implements Listener {
  private CMI plugin;

  public CooldownListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      Player player = playerCommandPreprocessEvent.getPlayer();
      String string = playerCommandPreprocessEvent.getMessage();
      string = string.endsWith(" ") ? string.substring(0, string.length() - 1) : string;
      string = string.startsWith("/:") ? "/" + string.substring(2) : string;
      boolean flag = this.plugin.getCooldownManager().canUseCmd(player, string, false, false);
      if (!flag) {
        playerCommandPreprocessEvent.setCancelled(true);
      }
    }
  }
}
