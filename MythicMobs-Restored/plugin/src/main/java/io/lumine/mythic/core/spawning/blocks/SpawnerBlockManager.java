package io.lumine.mythic.core.spawning.blocks;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;

public class SpawnerBlockManager extends ReloadableModule<MythicBukkit> {
   public SpawnerBlockManager(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
   }

   @Override
   public void unload() {
   }
}
