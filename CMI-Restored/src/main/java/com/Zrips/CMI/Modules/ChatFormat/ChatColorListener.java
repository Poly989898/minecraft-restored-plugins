package com.Zrips.CMI.Modules.ChatFormat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatColorListener implements Listener {
  private CMI plugin;

  public ChatColorListener(CMI cmi) {
    this.plugin = cmi;
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getColorsPublicMessagesPriority().getPriority(),
            (listener, event) -> this.cleanPublicChatFromColors((AsyncPlayerChatEvent) event),
            cmi,
            true);
  }

  private void cleanPublicChatFromColors(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    Player player = asyncPlayerChatEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      cmiUser.updateDisplayName();
    }

    if (this.plugin.getChatManager().isColorsPublicMessages()) {
      asyncPlayerChatEvent.setMessage(
          CMITextCleaner.cleanFromColorCodes(
              player, asyncPlayerChatEvent.getMessage(), CMIColorTypes.publicmessage, false));
    }
  }
}
