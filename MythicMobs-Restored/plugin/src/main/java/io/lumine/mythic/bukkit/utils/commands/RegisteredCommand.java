package io.lumine.mythic.bukkit.utils.commands;

import io.lumine.mythic.bukkit.utils.command.CommandMapUtil;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public class RegisteredCommand<T extends LuminePlugin> implements Terminable {
   private final T plugin;
   private final Command<T> command;

   public RegisteredCommand(T plugin, Command<T> command) {
      this.plugin = plugin;
      this.command = command;
      this.bindWith(plugin);
   }

   @Override
   public void close() throws Exception {
      CommandMapUtil.unregisterCommand(this.command);
   }
}
