package io.lumine.mythic.core.mobs.npcs;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.NPCManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Map;

public class NPCExecutor extends ReloadableModule<MythicBukkit> implements NPCManager {
   private final Map<String, ActiveMob> activeNPCs = Maps.newConcurrentMap();

   public NPCExecutor(MythicBukkit plugin, MobManager mobManager) {
      super(plugin);
   }

   public void load(MythicBukkit mythicBukkit) {
   }

   @Override
   public void unload() {
   }
}
