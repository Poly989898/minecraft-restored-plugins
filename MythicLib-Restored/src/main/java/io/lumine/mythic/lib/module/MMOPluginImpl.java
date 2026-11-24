package io.lumine.mythic.lib.module;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class MMOPluginImpl extends JavaPlugin {
   private final List<Module> modules = new ArrayList<>();

   public MMOPluginImpl() {
      MMOPluginRegistry.getInstance().registerPlugin(this);
   }

   public boolean hasData() {
      return true;
   }

   public boolean hasProfiles() {
      return false;
   }
}
