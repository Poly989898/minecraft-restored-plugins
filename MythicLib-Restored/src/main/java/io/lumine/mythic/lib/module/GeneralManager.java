package io.lumine.mythic.lib.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class GeneralManager {
   private final List<String> dependencies = new ArrayList<>();

   public void validateDependencies(@NotNull Set<String> activePlugins) {
      Validate.isTrue(MMOPluginRegistry.getInstance().isRegistrationAllowed(), "Dependency validation is not allowed");
      this.dependencies.removeIf(dep -> {
         String pluginId = dep.split("\\:")[0];
         return !activePlugins.contains(pluginId);
      });
   }

   public abstract void prepare();

   public abstract void enable();

   public abstract void clear();

   public void reload() {
      this.clear();
      this.prepare();
      this.enable();
   }
}
