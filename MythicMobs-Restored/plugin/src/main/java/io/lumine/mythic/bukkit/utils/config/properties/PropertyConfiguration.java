package io.lumine.mythic.bukkit.utils.config.properties;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;

@Deprecated
public abstract class PropertyConfiguration<T extends LuminePlugin> implements Terminable, TerminableConsumer {
   private final LuminePlugin plugin;
   private CompositeTerminable terminableRegistry = CompositeTerminable.create();

   public PropertyConfiguration(LuminePlugin plugin) {
      this.plugin = plugin;
   }

   public abstract void load();

   @Deprecated
   public void reload() {
      this.plugin.reloadConfiguration();
   }

   public abstract void unload();

   @Override
   public <T extends AutoCloseable> T bind(T terminable) {
      this.terminableRegistry.bind(terminable);
      return terminable;
   }

   @Override
   public void close() throws Exception {
      this.unload();
      this.terminableRegistry.closeAndReportException();
   }
}
