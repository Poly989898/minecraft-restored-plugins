package io.lumine.mythic.core.utils.physics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class CollisionHelper {
   @NotNull
   public static List<PhysicsCollision> getCollisions(
      @NotNull AbstractVector velocity, @NotNull BoundingBox boundingBox, @NotNull AbstractLocation currentPosition
   ) {
      World world = BukkitAdapter.adapt(currentPosition).getWorld();
      List<PhysicsCollision> physicsCollisions = new ArrayList<>();
      if (velocity.getX() > 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.0, 0.0, 0.0, 0.001, 0.0, 0.0);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(boundingBox.getWidthX(), 0.0, 0.0);
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.WEST));
            }
         }
      } else if (velocity.getX() < 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.001, 0.0, 0.0, 0.0, 0.0, 0.0);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(-boundingBox.getWidthX(), 0.0, 0.0);
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.EAST));
            }
         }
      }

      if (velocity.getZ() > 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.0, 0.0, 0.0, 0.0, 0.0, 0.001);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(0.0, 0.0, boundingBox.getWidthZ());
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.NORTH));
            }
         }
      } else if (velocity.getZ() < 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.0, 0.0, 0.001, 0.0, 0.0, 0.0);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(0.0, 0.0, -boundingBox.getWidthZ());
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.SOUTH));
            }
         }
      }

      if (velocity.getY() > 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.0, 0.0, 0.0, 0.0, 0.001, 0.0);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(0.0, boundingBox.getHeight(), 0.0);
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.DOWN));
            }
         }
      } else if (velocity.getY() < 0.0) {
         BoundingBox expandedBoundingBox = boundingBox.clone().expand(0.0, 0.001, 0.0, 0.0, 0.001, 0.0);
         BoundingBox shiftedBoundingBox = boundingBox.clone().shift(0.0, -boundingBox.getHeight(), 0.0);
         if (shiftedBoundingBox.overlaps(expandedBoundingBox)) {
            BoundingBox intersection = shiftedBoundingBox.intersection(expandedBoundingBox);
            PhysicsBlock blockAtIntersection = new PhysicsBlock(intersection.getCenter().toLocation(world).getBlock());
            if (blockAtIntersection.collidesWith(intersection)) {
               physicsCollisions.add(new PhysicsCollision(blockAtIntersection.getBlock(), BlockFace.UP));
            }
         }
      }

      return physicsCollisions;
   }
}
