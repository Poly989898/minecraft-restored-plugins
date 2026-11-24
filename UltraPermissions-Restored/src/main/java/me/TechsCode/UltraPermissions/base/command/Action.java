package me.TechsCode.UltraPermissions.base.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Action<T> {
   void run(CommandSender sender, Player player, Arguments list);
}
