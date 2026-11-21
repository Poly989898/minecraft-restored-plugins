package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

public class RemoveConditionCommand extends Command<MythicBukkit> {
   public RemoveConditionCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 2) {
         CommandHelper.sendError(sender, "Invalid Syntax: <aqua>/mm spawners removecondition [spawner] [condition]");
         return true;
      } else {
         String name = args[0];
         String condition = args[1];
         if (name.startsWith("g:")) {
            String group = name.substring(2);

            for (MythicSpawner ms : MythicBukkit.inst().getSpawnerManager().getSpawnersByGroup(group)) {
               if (!this.getPlugin().getSpawnerManager().removeSpawnerCondition(ms, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(sender, "Removed activation condition <aqua>" + condition + "<green> on group <yellow>" + group);
         } else if (name.equals("*")) {
            for (MythicSpawner msx : this.getPlugin().getSpawnerManager().getSpawners()) {
               if (!this.getPlugin().getSpawnerManager().removeSpawnerCondition(msx, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(sender, "Activation condition <aqua>" + condition + "<green> removed from all spawners!");
         } else if (!name.contains("*") && !name.contains("?")) {
            MythicSpawner msxx = this.getPlugin().getSpawnerManager().getSpawnerByName(name);
            if (msxx == null) {
               CommandHelper.sendError(sender, "You must enter a valid Mythic Spawner ID. That one was not found!");
               return true;
            }

            if (!this.getPlugin().getSpawnerManager().removeSpawnerCondition(msxx, condition)) {
               CommandHelper.sendError(sender, "The condition you entered was invalid!");
            } else {
               CommandHelper.sendSuccess(sender, "Removed activation condition <aqua>" + condition);
            }
         } else {
            for (MythicSpawner msxxx : this.getPlugin().getSpawnerManager().getSpawners()) {
               if (msxxx.getName().matches(name.replace("?", ".?").replace("*", ".*?"))
                  && !this.getPlugin().getSpawnerManager().removeSpawnerCondition(msxxx, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(
               sender, "Activation condition <aqua>" + condition + "<green> removed from spawners matching pattern <yellow>" + name + "!"
            );
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.removecondition";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "removecondition";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"rc"};
   }
}
