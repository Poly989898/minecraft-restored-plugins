package io.lumine.mythic.core.spawning.raids;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;

public class RaidExecutor extends ReloadableModule<MythicBukkit> {
   public RaidExecutor(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
   }

   @Override
   public void unload() {
   }
}
