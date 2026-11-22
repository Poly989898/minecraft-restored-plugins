package com.Zrips.CMI.Modules.ChatHandling;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentPaper;
import com.Zrips.CMI.Components.CMIComponentSpigot;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.Version.Version;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CMIAsyncChatListener implements Listener {
  public void init() {
    if (Version.isPaperBranch()) {
      CMI.getInstance()
          .getServer()
          .getPluginManager()
          .registerEvent(
              AsyncChatEvent.class,
              this,
              EventPriority.LOWEST,
              (listener, event) -> {
                AsyncChatEvent asyncChatEvent = (AsyncChatEvent) event;
                var set = asyncChatEvent.viewers();
                HashSet<Player> hashSet = new HashSet();

                for (Audience audience : set) {
                  if (audience instanceof Player) {
                    hashSet.add((Player) audience);
                  }
                }

                CMIComponentPaper cmiComponentPaper =
                    new CMIComponentPaper(asyncChatEvent.message());
                if (this.process(asyncChatEvent.getPlayer(), cmiComponentPaper, hashSet)) {
                  asyncChatEvent.setCancelled(true);
                }
              },
              CMI.getInstance(),
              true);
    } else {
      CMI.getInstance()
          .getServer()
          .getPluginManager()
          .registerEvent(
              AsyncPlayerChatEvent.class,
              this,
              EventPriority.LOWEST,
              (listenerArg, rawEvent) -> {
                AsyncPlayerChatEvent asyncPlayerChatEvent = (AsyncPlayerChatEvent) rawEvent;
                HashSet recipients = new HashSet();
                recipients.addAll(asyncPlayerChatEvent.getRecipients());
                CMIComponentSpigot cmiComponentSpigot = new CMIComponentSpigot(false);
                cmiComponentSpigot.add(
                    CMIComponentBase.quickCreate(asyncPlayerChatEvent.getFormat()));
                cmiComponentSpigot.add(
                    CMIComponentBase.quickCreate(asyncPlayerChatEvent.getMessage()));
                if (this.process(
                    asyncPlayerChatEvent.getPlayer(), cmiComponentSpigot, recipients)) {
                  asyncPlayerChatEvent.setCancelled(true);
                }
              },
              CMI.getInstance(),
              true);
    }
  }

  private boolean process(Player player, CMIComponentBase cmiComponentBase, Set<Player> set) {
    CMIAsyncChatEvent cmiAsyncChatEvent = new CMIAsyncChatEvent(player, cmiComponentBase, set);
    Bukkit.getPluginManager().callEvent(cmiAsyncChatEvent);
    return cmiAsyncChatEvent.isCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void CMIAsyncChatEvent(CMIAsyncChatEvent cmiAsyncChatEvent) {}
}
