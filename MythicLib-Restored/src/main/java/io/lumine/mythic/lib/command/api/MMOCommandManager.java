package io.lumine.mythic.lib.command.api;

import io.lumine.mythic.lib.manager.MMOManager;
import io.lumine.mythic.lib.util.ConfigFile;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MMOCommandManager implements MMOManager {
   public abstract JavaPlugin getPlugin();

   public abstract List<ToggleableCommand> getAll();

   @Override
   public void initialize(boolean clearBefore) {
      Validate.isTrue(!clearBefore, "Reloading this manager requires a reload");
      List<ToggleableCommand> commands = this.getAll();
      if (!new File(this.getPlugin().getDataFolder(), "commands.yml").exists()) {
         ConfigFile config = new ConfigFile(this.getPlugin(), "", "commands");

         for (ToggleableCommand cmd : commands) {
            String path = cmd.getConfigPath();
            config.getConfig().set(path + ".main", cmd.getName());
            config.getConfig().set(path + ".permission", cmd.getPermission());
            config.getConfig().set(path + ".aliases", cmd.getAliases());
            config.getConfig().set(path + ".description", cmd.getDescription());
         }

         config.save();
      }

      try {
         Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         commandMapField.setAccessible(true);
         CommandMap commandMap = (CommandMap)commandMapField.get(Bukkit.getServer());
         FileConfiguration config = new ConfigFile(this.getPlugin(), "", "commands").getConfig();

         for (ToggleableCommand toggleable : commands) {
            if (toggleable.isEnabled() && (toggleable.isForced() || config.contains(toggleable.getConfigPath()))) {
               commandMap.register(this.getPlugin().getName(), toggleable.toBukkit(config.getConfigurationSection(toggleable.getConfigPath())));
            }
         }
      } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
         this.getPlugin().getLogger().log(Level.WARNING, "Unable to register custom commands:");
         exception.printStackTrace();
      }
   }
}
