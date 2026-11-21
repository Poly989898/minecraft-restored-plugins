package io.lumine.mythic.bukkit.commands.test;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import java.util.List;
import org.bukkit.command.CommandSender;

public class SpawnersCommand extends Command<MythicBukkit> {
   public SpawnersCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args[0].equals("1")) {
      }

      if (args[0].equals("2")) {
         this.plugin.getSpawnerManager().loadSpawners();
      }

      if (args[0].equals("3")) {
         this.plugin.getSpawnerManager().loadSpawners();
      }

      if (args[0].equals("4")) {
         this.getPlugin().getSpawnerManager().saveSpawners();
         this.plugin.getSpawnerManager().loadSpawners();
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.test.taunt";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "spawners";
   }
}
