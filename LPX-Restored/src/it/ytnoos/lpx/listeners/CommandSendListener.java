package it.ytnoos.lpx.listeners;

import it.ytnoos.lpx.LPXBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandSendListener implements Listener {
   private final LPXBukkit lpxBukkit;

   public CommandSendListener(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @EventHandler
   public void commandSend(PlayerCommandSendEvent playerCommandSendEvent) {
      if (!this.lpxBukkit.getLpxCommand().hasAnyPermission(playerCommandSendEvent.getPlayer())) {
         playerCommandSendEvent.getCommands().remove("lpx");
         playerCommandSendEvent.getCommands().remove("lpx:lpx");
      }
   }

}
