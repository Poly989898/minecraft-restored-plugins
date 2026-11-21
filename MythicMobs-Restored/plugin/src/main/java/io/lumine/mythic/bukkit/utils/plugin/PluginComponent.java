package io.lumine.mythic.bukkit.utils.plugin;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;

public abstract class PluginComponent<T extends LuminePlugin> implements Terminable, TerminableConsumer {
   protected final T plugin;
   protected CompositeTerminable terminableRegistry = CompositeTerminable.create();

   public PluginComponent(T plugin) {
      this.plugin = plugin;
   }

   @Override
   public <T extends AutoCloseable> T bind(T terminable) {
      this.terminableRegistry.bind(terminable);
      return terminable;
   }

   @Override
   public void close() throws Exception {
      this.terminableRegistry.closeAndReportException();
   }

   public T getPlugin() {
      return this.plugin;
   }
}
