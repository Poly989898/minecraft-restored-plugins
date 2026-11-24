package me.TechsCode.UltraPermissions.base.command;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Requirement {
   boolean isMatching(CommandSender sender, Player player, List<String> texts);

   void onUnmatched(CommandSender sender, Player player);
}
