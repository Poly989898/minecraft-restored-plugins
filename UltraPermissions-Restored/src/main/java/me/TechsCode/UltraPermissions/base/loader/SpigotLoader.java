package me.TechsCode.UltraPermissions.base.loader;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotLoader extends JavaPlugin {
   private Loader loader;

   public SpigotLoader() {
   }

   public void onEnable() {
      this.loader = new Loader(this, SpigotTechPlugin.class, this.getClassLoader());
   }

   public void onDisable() {
      if (this.loader != null) {
         this.loader.unload();
      }
   }
}
