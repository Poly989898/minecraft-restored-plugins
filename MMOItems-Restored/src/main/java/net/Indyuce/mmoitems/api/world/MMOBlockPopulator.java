package net.Indyuce.mmoitems.api.world;

import java.util.Map;
import java.util.Random;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.block.WorldGenTemplate;
import net.Indyuce.mmoitems.manager.WorldGenManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class MMOBlockPopulator extends BlockPopulator {
   private static final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.DOWN, BlockFace.UP};
   private final WorldGenManager manager;
   private final World world;

   public MMOBlockPopulator(World world, WorldGenManager worldGenManager) {
      this.manager = worldGenManager;
      this.world = world;
   }

   public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int count, int chunkZ, @NotNull LimitedRegion limitedRegion) {
      Map<CustomBlock, WorldGenTemplate> map = this.manager.assigned();
      map.entrySet()
         .stream()
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).canGenerateInWorld(worldInfo.getName()))
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).getMinDepth() >= worldInfo.getMinHeight())
         .filter(var1x -> ((WorldGenTemplate)var1x.getValue()).getMaxDepth() <= worldInfo.getMaxHeight())
         .forEach(var5x -> {
            CustomBlock var6x = (CustomBlock)var5x.getKey();
            WorldGenTemplate worldGenTemplate = (WorldGenTemplate)var5x.getValue();
            if (!(random.nextDouble() > worldGenTemplate.getChunkChance())) {
               for (int i = 0; i < worldGenTemplate.getVeinCount(); i++) {
                  int worldX = count * 16 + random.nextInt(16);
                  int worldY = random.nextInt(worldGenTemplate.getMaxDepth() - worldGenTemplate.getMinDepth() + 1) + worldGenTemplate.getMinDepth();
                  int worldZ = chunkZ * 16 + random.nextInt(16);
                  Location location = new Location(this.world, worldX, worldY, worldZ);
                  if (worldGenTemplate.canGenerate(location) && location.getWorld() != null) {
                     Block blockAt = location.getWorld().getBlockAt(location);

                     for (int j = 0; j < worldGenTemplate.getVeinSize(); j++) {
                        if (limitedRegion.isInRegion(blockAt.getLocation())) {
                           if (worldGenTemplate.canReplace(limitedRegion.getType(blockAt.getLocation()))) {
                              limitedRegion.setType(blockAt.getLocation(), var6x.getState().getType());
                              limitedRegion.setBlockData(blockAt.getLocation(), var6x.getState().getBlockData());
                           }

                           BlockFace blockFace = faces[random.nextInt(faces.length)];
                           blockAt = blockAt.getRelative(blockFace);
                        }
                     }
                  }
               }
            }
         });
   }
}
