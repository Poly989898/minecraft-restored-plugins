package net.Indyuce.mmoitems.api.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

public class MushroomState {
   private final Material material;
   private final boolean up;
   private final boolean down;
   private final boolean west;
   private final boolean east;
   private final boolean south;
   private final boolean north;

   public MushroomState(Material material, boolean flag, boolean down, boolean west, boolean east, boolean south, boolean north) {
      this.material = material;
      this.up = flag;
      this.down = down;
      this.west = west;
      this.east = east;
      this.south = south;
      this.north = north;
   }

   public int getUniqueId() {
      String text = this.material == Material.BROWN_MUSHROOM_BLOCK ? "0" : (this.material == Material.RED_MUSHROOM_BLOCK ? "1" : "2");
      return Integer.parseInt(
         text
            + (this.up ? "1" : "0")
            + (this.down ? "1" : "0")
            + (this.west ? "1" : "0")
            + (this.east ? "1" : "0")
            + (this.south ? "1" : "0")
            + (this.north ? "1" : "0")
      );
   }

   public boolean matches(MushroomState mushroomState) {
      return this.up == mushroomState.up
         && this.down == mushroomState.down
         && this.west == mushroomState.west
         && this.east == mushroomState.east
         && this.south == mushroomState.south
         && this.north == mushroomState.north;
   }

   public Material getType() {
      return this.material;
   }

   public BlockData getBlockData() {
      MultipleFacing blockData = (MultipleFacing)this.material.createBlockData();
      blockData.setFace(BlockFace.UP, this.up);
      blockData.setFace(BlockFace.DOWN, this.down);
      blockData.setFace(BlockFace.NORTH, this.north);
      blockData.setFace(BlockFace.SOUTH, this.south);
      blockData.setFace(BlockFace.EAST, this.east);
      blockData.setFace(BlockFace.WEST, this.west);
      return blockData;
   }

   public boolean getSide(String text) {
      text = text.toLowerCase();
      switch (text) {
         case "up":
            return this.up;
         case "down":
            return this.down;
         case "north":
            return this.north;
         case "south":
            return this.south;
         case "east":
            return this.east;
         case "west":
            return this.west;
         default:
            return false;
      }
   }
}
