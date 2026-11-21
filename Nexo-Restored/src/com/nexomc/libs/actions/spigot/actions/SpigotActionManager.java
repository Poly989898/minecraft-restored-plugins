package com.nexomc.libs.actions.spigot.actions;

import com.nexomc.libs.actions.actions.ActionManager;
import com.nexomc.libs.actions.spigot.placeholders.PlaceholderAPIProvider;
import com.nexomc.libs.actions.spigot.tasks.SpigotTaskProcessor;
import com.nexomc.libs.actions.tasks.TaskProcessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotActionManager extends ActionManager {
   public SpigotActionManager(@Nullable TaskProcessor taskProcessor, double maxChance) {
      super(taskProcessor, maxChance);
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         this.getPlaceholderManager().register(new PlaceholderAPIProvider());
      }

   }

   public SpigotActionManager(@NotNull JavaPlugin plugin, double maxChance) {
      this((TaskProcessor)(new SpigotTaskProcessor(plugin)), maxChance);
   }

   public SpigotActionManager(@NotNull JavaPlugin plugin) {
      super(new SpigotTaskProcessor(plugin), (double)100.0F);
   }
}
