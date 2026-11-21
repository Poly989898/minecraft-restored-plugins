package com.nexomc.libs.gui;

import com.nexomc.libs.gui.guis.BaseGui;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class TriumphGui {
   private static Plugin PLUGIN = null;

   private TriumphGui() {
   }

   public static void init(@NotNull Plugin plugin) {
      PLUGIN = plugin;
   }

   public static @NotNull Plugin getPlugin() {
      if (PLUGIN == null) {
         init(JavaPlugin.getProvidingPlugin(BaseGui.class));
      }

      return PLUGIN;
   }
}
