package net.Indyuce.mmoitems.listener;

import java.util.HashMap;
import java.util.Map;
import net.Indyuce.mmoitems.api.world.MMOBlockPopulator;
import net.Indyuce.mmoitems.manager.WorldGenManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldGenerationListener implements Listener {
   private final WorldGenManager manager;
   private final Map<String, MMOBlockPopulator> populatorMap = new HashMap<>();

   public WorldGenerationListener(WorldGenManager var1) {
      this.manager = var1;
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent var1) {
      World var2 = var1.getWorld();
      if (var1.isNewChunk() && !this.populatorMap.containsKey(var2.getName())) {
         MMOBlockPopulator var3 = this.manager.populator(var2);
         var2.getPopulators().add(var3);
         this.populatorMap.put(var2.getName(), var3);
      }
   }
}
