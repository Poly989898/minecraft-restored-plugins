package io.lumine.mythic.core.utils.physics;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

public class GetBlocks {
   public static Collection<Block> withinAABB(BoundingBox scanBB, World world) {
      List<Block> blocks = Lists.newArrayList();
      int minX = (int)Math.floor(scanBB.getMin().getX());
      int minY = (int)Math.floor(scanBB.getMin().getY());
      int minZ = (int)Math.floor(scanBB.getMin().getZ());
      int maxX = (int)Math.floor(scanBB.getMax().getX());
      int maxY = (int)Math.floor(scanBB.getMax().getY());
      int maxZ = (int)Math.floor(scanBB.getMax().getZ());

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               blocks.add(new Location(world, x, y, z).getBlock());
            }
         }
      }

      return blocks;
   }
}
