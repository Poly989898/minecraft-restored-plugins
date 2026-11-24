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

   public WorldGenerationListener(WorldGenManager worldGenManager) {
      this.manager = worldGenManager;
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      World world = event.getWorld();
      if (event.isNewChunk() && !this.populatorMap.containsKey(world.getName())) {
         MMOBlockPopulator mmoBlockPopulator = this.manager.populator(world);
         world.getPopulators().add(mmoBlockPopulator);
         this.populatorMap.put(world.getName(), mmoBlockPopulator);
      }
   }
}
