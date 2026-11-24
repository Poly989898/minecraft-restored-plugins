package me.TechsCode.UltraPermissions.base.command;

import java.util.ArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandEvent implements Listener {
   String perm;
   String command;
   ArrayList<String> alias;

   public CommandEvent(String text, String command, ArrayList<String> texts) {
      this.perm = text;
      this.command = command;
      this.alias = texts;
   }

   @EventHandler
   public void command(PlayerCommandSendEvent event) {
      if (!event.getPlayer().hasPermission(this.perm) && !this.perm.isEmpty()) {
         event.getCommands().remove(this.command);
         this.alias.forEach(text -> event.getCommands().remove(text));
      }
   }
}
