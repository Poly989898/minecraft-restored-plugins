package io.lumine.mythic.bukkit.utils.plugin;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableModule;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;

public abstract class PluginModule<T extends LuminePlugin> implements Terminable, TerminableModule, TerminableConsumer {
   protected final T plugin;
   protected CompositeTerminable terminableRegistry = CompositeTerminable.create();
   protected final boolean autoload;

   public PluginModule(T plugin) {
      this(plugin, true);
   }

   public PluginModule(T plugin, boolean autoload) {
      this.plugin = plugin;
      this.autoload = autoload;
      this.doBinding(plugin);
   }

   protected void doBinding(LuminePlugin plugin) {
      this.bindModuleWith(plugin);
   }

   @Override
   public void setup(TerminableConsumer consumer) {
      consumer.bind(this);
      if (this.autoload) {
         this.load(this.plugin);
      }
   }

   public abstract void load(T t);

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

   public T getPlugin() {
      return this.plugin;
   }
}
