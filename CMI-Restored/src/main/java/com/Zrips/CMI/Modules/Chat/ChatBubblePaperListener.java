package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentPaper;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashSet;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ChatBubblePaperListener implements Listener {
  private static CMI plugin;

  public ChatBubblePaperListener(CMI cmi) {
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncChatEvent.class,
            this,
            cmi.getChatBubbleManager().getPriority().getPriority(),
            (listener, event) -> {
              AsyncChatEvent asyncChatEvent = (AsyncChatEvent) event;
              var set = asyncChatEvent.viewers();
              HashSet<Player> hashSet = new HashSet();

              for (Audience audience : set) {
                if (audience instanceof Player) {
                  hashSet.add((Player) audience);
                }
              }

              CMIComponentPaper cmiComponentPaper = new CMIComponentPaper(asyncChatEvent.message());
              cmi.getChatBubbleManager()
                  .showBubble(asyncChatEvent.getPlayer(), cmiComponentPaper, hashSet);
            },
            cmi,
            true);
  }
}
