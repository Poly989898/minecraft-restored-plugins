package io.lumine.mythic.bukkit.utils.storage;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;

public abstract class StorageModule<T extends LuminePlugin> extends PluginModule<T> {
   public StorageModule(T plugin) {
      super(plugin, false);
   }
}
