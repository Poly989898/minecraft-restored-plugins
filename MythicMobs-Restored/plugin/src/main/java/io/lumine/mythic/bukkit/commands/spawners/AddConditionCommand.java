package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

public class AddConditionCommand extends Command<MythicBukkit> {
   public AddConditionCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 2) {
         CommandHelper.sendError(sender, "Invalid Syntax: <aqua>/mm spawners addcondition [spawner] [condition]");
         return true;
      } else {
         String name = args[0];
         String condition = "";

         for (int i = 1; i < args.length; i++) {
            condition = condition.concat(args[i]) + " ";
         }

         if (name.startsWith("g:")) {
            String group = name.substring(2);

            for (MythicSpawner ms : this.getPlugin().getSpawnerManager().getSpawnersByGroup(group)) {
               if (!this.getPlugin().getSpawnerManager().addSpawnerCondition(ms, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(sender, "Added activation condition <aqua>" + condition + "<green> on group <yellow>" + group);
         } else if (name.equals("*")) {
            for (MythicSpawner msx : this.getPlugin().getSpawnerManager().getSpawners()) {
               if (!this.getPlugin().getSpawnerManager().addSpawnerCondition(msx, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(sender, "Activation condition <aqua>" + condition + "<green> added to all spawners!");
         } else if (!name.contains("*") && !name.contains("?")) {
            MythicSpawner msxx = this.getPlugin().getSpawnerManager().getSpawnerByName(name);
            if (msxx == null) {
               CommandHelper.sendError(sender, "You must enter a valid Mythic Spawner ID. That one was not found!");
               return true;
            }

            if (!this.getPlugin().getSpawnerManager().addSpawnerCondition(msxx, condition)) {
               CommandHelper.sendError(sender, "The condition you entered was invalid!");
            } else {
               CommandHelper.sendSuccess(sender, "Added activation condition <aqua>" + condition);
            }
         } else {
            for (MythicSpawner msxxx : this.getPlugin().getSpawnerManager().getSpawners()) {
               if (msxxx.getName().matches(name.replace("?", ".?").replace("*", ".*?"))
                  && !this.getPlugin().getSpawnerManager().addSpawnerCondition(msxxx, condition)) {
                  CommandHelper.sendError(sender, "The condition you entered was invalid!");
                  return true;
               }
            }

            CommandHelper.sendSuccess(sender, "Activation condition <aqua>" + condition + "<green> added to spawners matching pattern <yellow>" + name + "!");
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args.length == 1) {
         return CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2);
      } else {
         return args.length == 2
            ? CollectionUtils.filterMatches(args[0], this.getPlugin().getSkillManager().getConditions().keySet(), 2)
            : Collections.emptyList();
      }
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.addcondition";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "addcondition";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"ac"};
   }
}
