package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveCommand extends Command<MythicBukkit> {
   public SaveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      this.plugin.getConfiguration().save();
      sender.sendMessage(ChatColor.GOLD + "MythicMobs" + ChatColor.GREEN + " has been saved!");
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.save";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "save";
   }
}
