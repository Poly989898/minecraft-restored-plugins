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

   public MushroomState(Material var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.material = var1;
      this.up = var2;
      this.down = var3;
      this.west = var4;
      this.east = var5;
      this.south = var6;
      this.north = var7;
   }

   public int getUniqueId() {
      String var1 = this.material == Material.BROWN_MUSHROOM_BLOCK ? "0" : (this.material == Material.RED_MUSHROOM_BLOCK ? "1" : "2");
      return Integer.parseInt(
         var1
            + (this.up ? "1" : "0")
            + (this.down ? "1" : "0")
            + (this.west ? "1" : "0")
            + (this.east ? "1" : "0")
            + (this.south ? "1" : "0")
            + (this.north ? "1" : "0")
      );
   }

   public boolean matches(MushroomState var1) {
      return this.up == var1.up
         && this.down == var1.down
         && this.west == var1.west
         && this.east == var1.east
         && this.south == var1.south
         && this.north == var1.north;
   }

   public Material getType() {
      return this.material;
   }

   public BlockData getBlockData() {
      MultipleFacing var1 = (MultipleFacing)this.material.createBlockData();
      var1.setFace(BlockFace.UP, this.up);
      var1.setFace(BlockFace.DOWN, this.down);
      var1.setFace(BlockFace.NORTH, this.north);
      var1.setFace(BlockFace.SOUTH, this.south);
      var1.setFace(BlockFace.EAST, this.east);
      var1.setFace(BlockFace.WEST, this.west);
      return var1;
   }

   public boolean getSide(String var1) {
      var1 = var1.toLowerCase();
      switch (var1) {
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
