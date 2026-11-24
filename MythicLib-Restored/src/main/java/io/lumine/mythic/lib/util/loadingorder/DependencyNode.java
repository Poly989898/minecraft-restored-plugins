package io.lumine.mythic.lib.util.loadingorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

public class DependencyNode {
   public final String name;
   public final int id;
   public final Map<Integer, DependencyNode> children = new HashMap<>();
   public Plugin plugin;

   public DependencyNode(int id, Plugin plugin) {
      this.id = id;
      this.name = plugin.getName();
      this.plugin = plugin;
   }

   public void loadAdjacency(DependencyCycleCheck checker) {
      Validate.notNull(this.plugin, "Already loaded");
      List<String> deps = new ArrayList<>(this.plugin.getDescription().getDepend());
      deps.addAll(this.plugin.getDescription().getSoftDepend());

      for (String name : deps) {
         DependencyNode found = checker.registry.get(name);
         if (found != null) {
            this.children.put(found.id, found);
         }
      }

      for (String name : this.plugin.getDescription().getLoadBefore()) {
         DependencyNode found = checker.registry.get(name);
         if (found != null && !found.children.containsKey(this.id)) {
            found.children.put(this.id, this);
         }
      }

      this.plugin = null;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
