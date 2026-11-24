package me.TechsCode.UltraPermissions.base.legacy.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtils {
   public BlockUtils() {
   }

   public static Block[] getBlocksInRadius(Location location, int radius) {
      ArrayList<Block> blocks = new ArrayList<>();

      for (double x = location.getX() - radius; x <= location.getX() + radius; x++) {
         for (double y = location.getY() - radius; y <= location.getY() + radius; y++) {
            for (double z = location.getZ() - radius; z <= location.getZ() + radius; z++) {
               Location blockLocation = new Location(location.getWorld(), x, y, z);
               blocks.add(blockLocation.getBlock());
            }
         }
      }

      return blocks.toArray(new Block[0]);
   }

   public static BlockFace[] getBlockSides() {
      return new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
   }

   public static Block[] getAttachedBlocks(Block block) {
      return Arrays.stream(getBlockSides()).map(block::getRelative).toArray(Block[]::new);
   }

   public static Set<Block> getBlocksInSphere(Location location, int radius, boolean hollow) {
      HashSet blocks = new HashSet();
      int blockX = location.getBlockX();
      int blockY = location.getBlockY();
      int blockZ = location.getBlockZ();

      for (int i = blockX - radius; i <= blockX + radius; i++) {
         for (int j = blockY - radius; j <= blockY + radius; j++) {
            for (int k = blockZ - radius; k <= blockZ + radius; k++) {
               double distanceSquared = (blockX - i) * (blockX - i) + (blockZ - k) * (blockZ - k) + (blockY - j) * (blockY - j);
               if (distanceSquared < radius * radius && (!hollow || !(distanceSquared < (radius - 1) * (radius - 1)))) {
                  Location blockLocation = new Location(location.getWorld(), i, j, k);
                  blocks.add(blockLocation.getBlock());
               }
            }
         }
      }

      return blocks;
   }
}
