package it.ytnoos.lpx.api;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public interface LPXPlugin extends Plugin {
   Logger getLogger();

   File getDataFolder();

   File getConfigFile();

   InputStream getResource(String string);

   PluginDescriptionFile getDescription();

   Server getServer();

   FileConfiguration getConfig();

   void reload();

   void saveDefaultConfig();

   void saveConfig();
}
