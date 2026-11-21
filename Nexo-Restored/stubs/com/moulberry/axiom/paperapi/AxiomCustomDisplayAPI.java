package com.moulberry.axiom.paperapi;

import com.moulberry.axiom.paperapi.display.AxiomCustomDisplayBuilder;

/** Compile-only stub of the Axiom custom display API (not packaged). */
public interface AxiomCustomDisplayAPI {
   static AxiomCustomDisplayAPI getAPI() { return null; }

   void unregisterAll(org.bukkit.plugin.Plugin plugin);

   AxiomCustomDisplayBuilder create(net.kyori.adventure.key.Key key, String searchKey, org.bukkit.inventory.ItemStack itemStack);

   void register(org.bukkit.plugin.Plugin plugin, AxiomCustomDisplayBuilder builder);
}
