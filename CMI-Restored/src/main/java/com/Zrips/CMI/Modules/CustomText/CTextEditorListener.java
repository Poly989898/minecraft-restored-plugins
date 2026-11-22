package com.Zrips.CMI.Modules.CustomText;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import net.Zrips.CMILib.Shadow.ShadowCommand;
import net.Zrips.CMILib.Shadow.ShadowCommandInfo;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CTextEditorListener implements Listener {
  private CMI plugin;

  public CTextEditorListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void AsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    Player player = asyncPlayerChatEvent.getPlayer();
    CTextManager cTextManager = this.plugin.getCTextManager();
    if (cTextManager.isChatEditing(player)) {
      String string = cTextManager.getChatEditorCmd(player);
      String commandText =
          asyncPlayerChatEvent.getMessage().endsWith("?")
              ? asyncPlayerChatEvent.getMessage() + "<>-<>"
              : asyncPlayerChatEvent.getMessage();
      if (string != null) {
        CMIScheduler.runTask(this.plugin, () -> player.performCommand(string + commandText));
      }

      cTextManager.removeChatEditor(player);
      asyncPlayerChatEvent.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onShadowCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      Player player = playerCommandPreprocessEvent.getPlayer();
      String string = playerCommandPreprocessEvent.getMessage();
      if (string.startsWith("/cmi shadowcmd ")) {
        playerCommandPreprocessEvent.setCancelled(true);
        String shadowCommandName =
            string.substring("/cmi shadowcmd ".length(), string.length()).split(" ")[0];
        ShadowCommandInfo shadowCommandInfo =
            ShadowCommand.getShadowCommand(player, shadowCommandName);
        if (shadowCommandInfo != null) {
          String commandLine = shadowCommandInfo.getCmd();
          Snd snd = new Snd(player, player);
          commandLine = this.plugin.getLM().updateSnd(snd, commandLine);
          switch (shadowCommandInfo.getType()) {
            case Console:
              commandLine =
                  this.plugin
                      .getPlaceholderAPIManager()
                      .translateOwnPlaceHolder(player, commandLine);
              ServerCommandEvent serverCommandEvent =
                  new ServerCommandEvent(this.plugin.getServer().getConsoleSender(), commandLine);
              this.plugin.getServer().getPluginManager().callEvent(serverCommandEvent);
              if (serverCommandEvent.isCancelled()) {
                return;
              }

              this.plugin
                  .getServer()
                  .dispatchCommand(
                      this.plugin.getServer().getConsoleSender(), serverCommandEvent.getCommand());
              break;
            case Player:
              commandLine =
                  this.plugin
                      .getPlaceholderAPIManager()
                      .translateOwnPlaceHolder(player, commandLine);
              PlayerCommandPreprocessEvent preprocessEvent =
                  new PlayerCommandPreprocessEvent(player, "/" + commandLine);
              this.plugin.getServer().getPluginManager().callEvent(preprocessEvent);
              if (preprocessEvent.isCancelled()) {
                return;
              }

              player.performCommand(shadowCommandInfo.getCmd());
          }
        }
      }
    }
  }
}
