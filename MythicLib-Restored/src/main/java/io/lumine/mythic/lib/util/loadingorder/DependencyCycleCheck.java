package io.lumine.mythic.lib.util.loadingorder;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class DependencyCycleCheck {
   public final Map<String, DependencyNode> registry = new HashMap<>();
   public final boolean[] visited;
   public final boolean[] inStack;
   public final Stack<DependencyNode> stack = new Stack<>();

   public DependencyCycleCheck() {
      int counter = 0;

      for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
         DependencyNode loaded = new DependencyNode(counter, plugin);
         this.registry.put(loaded.name, loaded);
         counter++;
      }

      this.registry.values().forEach(pl -> pl.loadAdjacency(this));
      this.visited = new boolean[this.registry.size()];
      this.inStack = new boolean[this.registry.size()];
   }

   @Nullable
   public Stack<DependencyNode> checkCycle() {
      for (DependencyNode root : this.registry.values()) {
         DependencyNode check = this.recursiveCheck(root);
         if (check != null) {
            this.stack.add(check);
            return this.stack;
         }
      }

      return null;
   }

   @Nullable
   private DependencyNode recursiveCheck(DependencyNode plugin) {
      if (this.inStack[plugin.id]) {
         return plugin;
      }

      if (this.visited[plugin.id]) {
         return null;
      }

      this.visited[plugin.id] = true;
      this.inStack[plugin.id] = true;
      this.stack.add(plugin);

      for (DependencyNode dep : plugin.children.values()) {
         DependencyNode check = this.recursiveCheck(dep);
         if (check != null) {
            return check;
         }
      }

      this.inStack[plugin.id] = false;
      this.stack.pop();
      return null;
   }
}
