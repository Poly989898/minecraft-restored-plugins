package io.lumine.mythic.core.utils.physics;

import lombok.Generated;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PhysicsCollision {
   private final Block block;
   private final BlockFace blockFace;

   @Generated
   public PhysicsCollision(Block block, BlockFace blockFace) {
      this.block = block;
      this.blockFace = blockFace;
   }

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public BlockFace getBlockFace() {
      return this.blockFace;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PhysicsCollision other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$block = this.getBlock();
         Object other$block = other.getBlock();
         if (this$block == null ? other$block == null : this$block.equals(other$block)) {
            Object this$blockFace = this.getBlockFace();
            Object other$blockFace = other.getBlockFace();
            return this$blockFace == null ? other$blockFace == null : this$blockFace.equals(other$blockFace);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PhysicsCollision;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $block = this.getBlock();
      result = result * 59 + ($block == null ? 43 : $block.hashCode());
      Object $blockFace = this.getBlockFace();
      return result * 59 + ($blockFace == null ? 43 : $blockFace.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "PhysicsCollision(block=" + this.getBlock() + ", blockFace=" + this.getBlockFace() + ")";
   }
}
