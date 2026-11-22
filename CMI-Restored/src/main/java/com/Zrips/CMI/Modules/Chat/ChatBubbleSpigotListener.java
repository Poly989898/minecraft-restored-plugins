package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatBubbleSpigotListener implements Listener {
  private static CMI plugin;

  public ChatBubbleSpigotListener(CMI cmi) {
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatBubbleManager().getPriority().getPriority(),
            (listener, event) -> {
              AsyncPlayerChatEvent asyncPlayerChatEvent = (AsyncPlayerChatEvent) event;
              cmi.getChatBubbleManager()
                  .showBubble(
                      asyncPlayerChatEvent.getPlayer(),
                      CMIChatColor.translate(
                              CMIChatColor.getLastColors(asyncPlayerChatEvent.getFormat()))
                          + asyncPlayerChatEvent.getMessage(),
                      asyncPlayerChatEvent.getRecipients());
            },
            cmi,
            true);
  }
}
