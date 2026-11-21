package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command<MythicBukkit> {
   public InfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      sender.sendMessage(ChatColor.GOLD + "MM Types Loaded: " + ChatColor.GRAY + MythicBukkit.inst().getMobManager().getMobTypes().size());
      sender.sendMessage(ChatColor.GOLD + "Spawners Loaded: " + ChatColor.GRAY + MythicBukkit.inst().getSpawnerManager().getSpawners().size());
      sender.sendMessage(ChatColor.GOLD + "Naturals Loaded: " + ChatColor.GRAY + MythicBukkit.inst().getRandomSpawningManager().getNumberOfSpawners());
      sender.sendMessage(ChatColor.GOLD + "----");
      sender.sendMessage(ChatColor.GOLD + "Active Mobs: " + ChatColor.GRAY + MythicBukkit.inst().getMobManager().getActiveMobs().size());
      sender.sendMessage(ChatColor.GOLD + "- in Combat: " + ChatColor.GRAY + MythicBukkit.inst().getMobManager().getMobsInCombat().size());
      sender.sendMessage(ChatColor.GOLD + "----");
      this.getPlugin().getSkillManager().getAuraManager().sendAuraDebugInfo(sender);
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.info";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "info";
   }
}
