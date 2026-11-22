package net.Zrips.CMILib.Chat;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatEditorListener implements Listener {
  private CMILib plugin;

  public ChatEditorListener(CMILib plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
    if (!event.isCancelled()) {
      if (!ChatEditorManager.map.isEmpty()) {
        Player player = event.getPlayer();
        if (ChatEditorManager.map.containsKey(player.getUniqueId())) {
          CMIScheduler.runTask(
              this.plugin, () -> ChatEditorManager.perform(player, event.getMessage()));
          event.setCancelled(true);
          event.getRecipients().clear();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!ChatEditorManager.map.isEmpty()) {
      Player player = event.getPlayer();
      if (ChatEditorManager.map.containsKey(player.getUniqueId())) {
        ChatEditorManager.remove(player.getUniqueId());
      }
    }
  }
}
