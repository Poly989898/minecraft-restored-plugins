package io.lumine.mythic.core.utils.physics;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PhysicsBlock {
   private final Block block;

   public boolean uncertainCollidesWith(@NotNull BoundingBox boundingBox) {
      return !this.block.isPassable() && this.voxelIntersectsWith(boundingBox);
   }

   public boolean voxelIntersectsWith(@NotNull BoundingBox boundingBox) {
      return this.block.getBoundingBox().overlaps(boundingBox);
   }

   public boolean collidesWith(@NotNull BoundingBox boundingBox) {
      return !this.block.isPassable() && ComplexAABB.anyOverlap(boundingBox, ComplexAABB.get(this.block));
   }

   @NotNull
   public List<BlockFace> getCollidedFaces(@NotNull BoundingBox boundingBox) {
      long t = System.nanoTime();
      List<BlockFace> hitFaces = new ArrayList<>();
      World world = this.block.getWorld();
      Location rayTraceOrigin = boundingBox.getCenter().toLocation(world);

      for (BoundingBox overlappedBoundingBox : ComplexAABB.getOverlaps(boundingBox, ComplexAABB.get(this.block))) {
         Vector rayTraceAngle = VectorMath.angleBetween(rayTraceOrigin.toVector(), overlappedBoundingBox.getCenter());
         RayTraceResult rayTraceResult = world.rayTraceBlocks(rayTraceOrigin, rayTraceAngle, 0.25, FluidCollisionMode.NEVER, true);
         if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlockFace() != null) {
            hitFaces.add(rayTraceResult.getHitBlockFace());
         }
      }

      t = System.nanoTime() - t;
      return hitFaces;
   }

   @NotNull
   public List<BlockFace> _getCollidedFaces(@NotNull BoundingBox boundingBox) {
      long t = System.nanoTime();
      List<BlockFace> hitFaces = new ArrayList<>();

      for (BoundingBox overlappedBoundingBox : ComplexAABB.getOverlaps(boundingBox, ComplexAABB.get(this.block))) {
         BoundingBox intersection = overlappedBoundingBox.intersection(boundingBox);
         Vector intersectionCenter = intersection.getCenter();
         Vector boundingBoxCenter = boundingBox.getCenter();
         if (intersectionCenter.getX() > boundingBoxCenter.getX()) {
            hitFaces.add(BlockFace.WEST);
         } else if (intersectionCenter.getX() < boundingBoxCenter.getX()) {
            hitFaces.add(BlockFace.EAST);
         }

         if (intersectionCenter.getZ() > boundingBoxCenter.getZ()) {
            hitFaces.add(BlockFace.NORTH);
         } else if (intersectionCenter.getZ() < boundingBoxCenter.getZ()) {
            hitFaces.add(BlockFace.SOUTH);
         }

         if (intersectionCenter.getY() > boundingBoxCenter.getY()) {
            hitFaces.add(BlockFace.DOWN);
         } else if (intersectionCenter.getY() < boundingBoxCenter.getY()) {
            hitFaces.add(BlockFace.UP);
         }
      }

      t = System.nanoTime() - t;
      return hitFaces;
   }

   @Generated
   public PhysicsBlock(Block block) {
      this.block = block;
   }

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PhysicsBlock other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$block = this.getBlock();
         Object other$block = other.getBlock();
         return this$block == null ? other$block == null : this$block.equals(other$block);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PhysicsBlock;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $block = this.getBlock();
      return result * 59 + ($block == null ? 43 : $block.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "PhysicsBlock(block=" + this.getBlock() + ")";
   }
}
