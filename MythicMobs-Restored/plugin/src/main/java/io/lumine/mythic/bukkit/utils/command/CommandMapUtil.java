package io.lumine.mythic.bukkit.utils.command;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public final class CommandMapUtil {
   private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
   private static final Field COMMAND_MAP_FIELD;
   private static final Field KNOWN_COMMANDS_FIELD;

   private static CommandMap getCommandMap() {
      try {
         return (CommandMap)COMMAND_MAP_FIELD.get(Bukkit.getServer().getPluginManager());
      } catch (Exception e) {
         throw new RuntimeException("Could not get CommandMap", e);
      }
   }

   private static Map<String, org.bukkit.command.Command> getKnownCommandMap() {
      try {
         return (Map<String, org.bukkit.command.Command>)KNOWN_COMMANDS_FIELD.get(getCommandMap());
      } catch (Exception e) {
         throw new RuntimeException("Could not get known commands map", e);
      }
   }

   @Nonnull
   public static <T extends CommandExecutor> T registerCommand(@Nonnull Plugin plugin, @Nonnull T command, @Nonnull String... aliases) {
      return registerCommand(plugin, command, null, null, null, aliases);
   }

   @Nonnull
   public static <T extends CommandExecutor> T registerCommand(
      @Nonnull Plugin plugin, @Nonnull T command, String permission, String permissionMessage, String description, @Nonnull String... aliases
   ) {
      Preconditions.checkArgument(aliases.length != 0, "No aliases");

      for (String alias : aliases) {
         try {
            PluginCommand cmd = COMMAND_CONSTRUCTOR.newInstance(alias, plugin);
            getCommandMap().register(plugin.getDescription().getName(), cmd);
            getKnownCommandMap().put(plugin.getDescription().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
            getKnownCommandMap().put(alias.toLowerCase(), cmd);
            cmd.setLabel(alias.toLowerCase());
            if (permission != null) {
               cmd.setPermission(permission);
               if (permissionMessage != null) {
                  cmd.setPermissionMessage(permissionMessage);
               }
            }

            if (description != null) {
               cmd.setDescription(description);
            }

            cmd.setExecutor(command);
            if (command instanceof TabCompleter) {
               cmd.setTabCompleter((TabCompleter)command);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return command;
   }

   @Nonnull
   public static <T extends CommandExecutor> T unregisterCommand(@Nonnull T command) {
      CommandMap map = getCommandMap();

      try {
         Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>)KNOWN_COMMANDS_FIELD.get(map);
         Iterator<org.bukkit.command.Command> iterator = knownCommands.values().iterator();

         while (iterator.hasNext()) {
            org.bukkit.command.Command cmd = iterator.next();
            if (cmd instanceof PluginCommand) {
               CommandExecutor executor = ((PluginCommand)cmd).getExecutor();
               if (command == executor) {
                  cmd.unregister(map);
                  iterator.remove();
               }
            }
         }

         return command;
      } catch (Exception e) {
         throw new RuntimeException("Could not unregister command", e);
      }
   }

   private CommandMapUtil() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   static {
      Constructor<PluginCommand> commandConstructor;
      try {
         commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
         commandConstructor.setAccessible(true);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
      }

      COMMAND_CONSTRUCTOR = commandConstructor;

      Field commandMapField;
      try {
         commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
         commandMapField.setAccessible(true);
      } catch (NoSuchFieldException ex) {
         throw new RuntimeException(ex);
      }

      COMMAND_MAP_FIELD = commandMapField;

      Field knownCommandsField;
      try {
         knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
         knownCommandsField.setAccessible(true);
      } catch (NoSuchFieldException ex2) {
         throw new RuntimeException(ex2);
      }

      KNOWN_COMMANDS_FIELD = knownCommandsField;
   }
}
