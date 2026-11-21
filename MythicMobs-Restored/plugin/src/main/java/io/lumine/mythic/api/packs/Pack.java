package io.lumine.mythic.api.packs;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.core.constants.ConfigFileType;
import java.io.File;
import java.util.Collection;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public interface Pack extends PropertyHolder {
   File getFolder();

   MythicConfig getConfig();

   String getKey();

   String getName();

   String getAuthor();

   String getUrl();

   String getVersion();

   File getSchematicDirectory();

   Integer getItemCount();

   void addItem();

   default File getPackFolder(String name) {
      return this.getPackFolder(name, false);
   }

   File getPackFolder(String str, boolean flag);

   default Collection<File> getPackFolders(String name) {
      return this.getPackFolders(name, false, false);
   }

   Collection<File> getPackFolders(String str, boolean flag, boolean flag2);

   File getPackFile(String str);

   <T> Icon<T> createMenuIcon(BiConsumer<T, Player> biConsumer);

   Collection<File> getConfigFiles(ConfigFileType configFileType);
}
