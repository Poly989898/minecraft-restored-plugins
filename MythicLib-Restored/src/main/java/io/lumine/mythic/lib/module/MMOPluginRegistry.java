package io.lumine.mythic.lib.module;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MMOPluginRegistry {
   private final List<MMOPluginImpl> plugins = new ArrayList<>();
   private final List<Module> sorted = new ArrayList<>();
   private boolean registration = true;
   private static MMOPluginRegistry instance;

   private MMOPluginRegistry() {
   }

   public static MMOPluginRegistry getInstance() {
      if (instance == null) {
         instance = new MMOPluginRegistry();
      }

      return instance;
   }

   public void registerPlugin(@NotNull MMOPluginImpl plugin) {
      this.plugins.add(plugin);
   }

   public boolean isRegistrationAllowed() {
      return this.registration;
   }

   private void sortManagers() {
   }

   @NotNull
   private static String lowerCase(@NotNull String str) {
      return str.toLowerCase().replace(" ", "_").replace("-", "_").replaceAll("[^a-z_]", "");
   }
}
