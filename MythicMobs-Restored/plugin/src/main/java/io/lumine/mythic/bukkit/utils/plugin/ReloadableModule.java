package io.lumine.mythic.bukkit.utils.plugin;

import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;

public abstract class ReloadableModule<T extends LuminePlugin> extends PluginModule<T> {
   public ReloadableModule(T plugin) {
      super(plugin, true);
   }

   public ReloadableModule(T plugin, boolean autoload) {
      super(plugin, autoload);
   }

   public void reload() {
      this.closeAndReportException();
      this.terminableRegistry = CompositeTerminable.create();
      this.load(this.plugin);
   }
}
