package net.Zrips.CMILib.Chat;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Locale.Snd;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CTextEditorListener implements Listener {
  private CMILib plugin;

  public CTextEditorListener(CMILib plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
    final Player player = event.getPlayer();
    if (ListEditor.isChatEditing(player)) {
      final String cmd = ListEditor.getChatEditorCmd(player);
      final String message =
          event.getMessage().endsWith("?") ? event.getMessage() + "<>-<>" : event.getMessage();
      if (cmd != null) {
        CMIScheduler.get()
            .runTask(
                new Runnable() {
                  @Override
                  public void run() {
                    player.performCommand(cmd + message);
                  }
                });
      }

      ListEditor.removeChatEditor(player);
      event.setCancelled(true);
      event.getRecipients().clear();
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onShadowCommand(PlayerCommandPreprocessEvent event) {
    if (!event.isCancelled()) {
      Player player = event.getPlayer();
      String message = event.getMessage();
      if (message.startsWith("/" + CommandsHandler.getLabel() + " shadowcmd ")) {
        event.setCancelled(true);
        String id =
            message.substring(
                    ("/" + CommandsHandler.getLabel() + " shadowcmd ").length(), message.length())
                .split(" ")[0];
        ShadowCommandInfo cmd = ListEditor.getShadowCommand(player, id);
        if (cmd != null) {
          String command = cmd.getCmd();
          Snd snd = new Snd(player, player);
          command = this.plugin.getLM().updateSnd(snd, command);
          switch (cmd.getType()) {
            case Console:
              command =
                  this.plugin.getPlaceholderAPIManager().translateOwnPlaceHolder(player, command);
              ServerCommandEvent e =
                  new ServerCommandEvent(this.plugin.getServer().getConsoleSender(), command);
              this.plugin.getServer().getPluginManager().callEvent(e);
              if (e.isCancelled()) {
                return;
              }

              this.plugin
                  .getServer()
                  .dispatchCommand(this.plugin.getServer().getConsoleSender(), e.getCommand());
              break;
            case Player:
              command =
                  this.plugin.getPlaceholderAPIManager().translateOwnPlaceHolder(player, command);
              PlayerCommandPreprocessEvent ev =
                  new PlayerCommandPreprocessEvent(player, "/" + command);
              this.plugin.getServer().getPluginManager().callEvent(ev);
              if (ev.isCancelled()) {
                return;
              }

              player.performCommand(cmd.getCmd());
          }
        }
      }
    }
  }
}
