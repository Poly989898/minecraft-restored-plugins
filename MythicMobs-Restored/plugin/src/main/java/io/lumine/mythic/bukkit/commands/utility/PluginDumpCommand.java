package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginDumpCommand extends Command<MythicBukkit> {
   public PluginDumpCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      AbstractLocation location = BukkitAdapter.adapt(player.getLocation());
      CommandHelper.sendSuccess(sender, "<yellow>Placeholder Parsers Registered<gray>: <white>" + this.getPlugin().getPlaceholderManager().getParsers().size());
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.plugindump";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "plugindump";
   }
}
