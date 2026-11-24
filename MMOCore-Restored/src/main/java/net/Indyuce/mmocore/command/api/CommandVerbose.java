package net.Indyuce.mmocore.command.api;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandVerbose {
   private final Map<CommandVerbose.CommandType, CommandVerbose.VerboseValue> values = new HashMap<>();

   public void reload(ConfigurationSection config) {
      this.values.clear();

      for (CommandVerbose.CommandType type : CommandVerbose.CommandType.values()) {
         try {
            this.values.put(type, CommandVerbose.VerboseValue.valueOf(config.getString(UtilityMethods.ymlName(type.name()), "TRUE").toUpperCase()));
         } catch (IllegalArgumentException exception) {
            this.values.put(type, CommandVerbose.VerboseValue.TRUE);
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load command verbose action for " + type.name());
         }
      }
   }

   public void handle(CommandSender sender, CommandVerbose.CommandType type, String verbose) {
      switch ((CommandVerbose.VerboseValue)this.values.getOrDefault(type, CommandVerbose.VerboseValue.FALSE)) {
         case TRUE:
            sender.sendMessage(verbose);
            break;
         case PLAYER:
            if (sender instanceof Player) {
               sender.sendMessage(verbose);
            }
            break;
         case CONSOLE:
            if (sender instanceof ConsoleCommandSender) {
               sender.sendMessage(verbose);
            }
            break;
         case FALSE:
            return;
      }
   }

   public static void verbose(CommandSender sender, CommandVerbose.CommandType cmd, String verbose) {
      MMOCore.plugin.configManager.commandVerbose.handle(sender, cmd, verbose);
   }

   public enum CommandType {
      ATTRIBUTE,
      SKILL,
      CLASS,
      EXPERIENCE,
      LEVEL,
      NOCD,
      POINTS,
      SKILL_TREE_POINTS,
      RESET,
      RESOURCE,
      WAYPOINT;
   }

   private enum VerboseValue {
      TRUE,
      PLAYER,
      CONSOLE,
      FALSE;
   }
}
