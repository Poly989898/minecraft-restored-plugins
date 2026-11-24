package me.TechsCode.UltraPermissions.base.loader.reloader;

import me.TechsCode.UltraPermissions.base.TechPlugin;

public abstract class PluginReloader<PLUGIN extends TechPlugin<?>> {
   protected PLUGIN plugin;

   public PluginReloader(PLUGIN value) {
      this.plugin = (PLUGIN)value;
   }

   public abstract void unload();

   public abstract void load();
}
