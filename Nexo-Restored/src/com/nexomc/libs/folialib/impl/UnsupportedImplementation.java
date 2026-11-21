package com.nexomc.libs.folialib.impl;

import com.nexomc.libs.folialib.FoliaLib;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class UnsupportedImplementation extends LegacySpigotImplementation {
   public UnsupportedImplementation(FoliaLib foliaLib) {
      super(foliaLib);
      Plugin plugin = foliaLib.getPlugin();
      Logger logger = plugin.getLogger();
      logger.warning(String.format("\n---------------------------------------------------------------------\nFoliaLib does not support this server software! (%s)\nFoliaLib will attempt to use the legacy spigot implementation.\n---------------------------------------------------------------------\n", plugin.getServer().getVersion()));
   }
}
