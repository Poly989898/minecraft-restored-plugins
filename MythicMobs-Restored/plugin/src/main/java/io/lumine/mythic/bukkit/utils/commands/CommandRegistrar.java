package io.lumine.mythic.bukkit.utils.commands;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class CommandRegistrar {
   private static CommandMap commandMap = null;
   private static Constructor<?> commandConstructor;
   private static Field owningPluginField;

   @Nonnull
   public static <T extends CommandExecutor> T registerCommand(@Nonnull Plugin plugin, @Nonnull T command, @Nonnull String... aliases) {
      Preconditions.checkArgument(aliases.length != 0, "No aliases");

      for (String alias : aliases) {
         PluginCommand cmd = Bukkit.getServer().getPluginCommand(alias);
         if (cmd == null) {
            try {
               cmd = (PluginCommand)commandConstructor.newInstance(alias, plugin);
            } catch (Exception e) {
               throw new RuntimeException("Could not register command: " + alias);
            }

            if (commandMap == null) {
               try {
                  PluginManager pluginManager = Bukkit.getServer().getPluginManager();
                  Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
                  commandMapField.setAccessible(true);
                  commandMap = (CommandMap)commandMapField.get(pluginManager);
               } catch (Exception ex) {
                  throw new RuntimeException("Could not register command: " + alias);
               }
            }

            commandMap.register(plugin.getDescription().getName(), cmd);
         } else {
            try {
               owningPluginField.set(cmd, plugin);
            } catch (Exception ex2) {
               ex2.printStackTrace();
            }
         }

         cmd.setExecutor(command);
         if (command instanceof TabCompleter) {
            cmd.setTabCompleter((TabCompleter)command);
         } else {
            cmd.setTabCompleter(null);
         }
      }

      return command;
   }

   private CommandRegistrar() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   static {
      try {
         commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
         commandConstructor.setAccessible(true);
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         owningPluginField = PluginCommand.class.getDeclaredField("owningPlugin");
         owningPluginField.setAccessible(true);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
