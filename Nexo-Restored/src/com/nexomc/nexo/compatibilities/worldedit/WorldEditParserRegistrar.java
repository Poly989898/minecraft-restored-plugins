package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.NexoPlugin;
import com.sk89q.worldedit.WorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/** Holds every direct WorldEdit reference used for registering Nexo's parser and event hooks.
 *  Only loaded when WorldEdit is enabled, so WrappedWorldEdit can be linked on servers without WorldEdit. */
final class WorldEditParserRegistrar {
   private WorldEditParserRegistrar() {
   }

   static void register(WorldEditHandlers handlers) {
      WorldEdit.getInstance().getEventBus().register(handlers);
      WorldEdit.getInstance().getBlockFactory().register(new CustomBlocksFactory());
      Bukkit.getPluginManager().registerEvents(new WorldEditListener(), (Plugin) NexoPlugin.Companion.instance());
   }

   static void unregister(WorldEditHandlers handlers) {
      WorldEdit.getInstance().getEventBus().unregister(handlers);
   }
}