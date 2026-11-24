package io.lumine.mythic.lib.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public abstract class Module {
   protected final MMOPluginImpl plugin;
   protected final NamespacedKey key;
   protected final List<NamespacedKey> dependencies = new ArrayList<>();

   protected Module(@NotNull MMOPluginImpl plugin, @NotNull String key) {
      this.plugin = plugin;
      this.key = new NamespacedKey(plugin, key);
   }

   public abstract void load();

   public abstract void enable();

   public abstract void clear();

   public void addDependencies(NamespacedKey... dependencies) {
      Collections.addAll(this.dependencies, dependencies);
   }

   @NotNull
   public List<NamespacedKey> getDependencies() {
      return this.dependencies;
   }

   @NotNull
   public NamespacedKey getNamespacedKey() {
      return this.key;
   }

   @NotNull
   public MMOPluginImpl getPlugin() {
      return this.plugin;
   }
}
