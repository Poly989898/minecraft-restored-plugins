package com.ticxo.modelengine.api.utils.math;

import lombok.ToString;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@ToString
public class OrientedBoundingBox {
   private static final Vector3f GLOBAL_RIGHT = new Vector3f(1.0F, 0.0F, 0.0F);
   private static final Vector3f GLOBAL_UP = new Vector3f(0.0F, 1.0F, 0.0F);
   private static final Vector3f GLOBAL_FORWARD = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Vector3f origin;
   private final Quaternionf rotation;
   private final Vector3f right;
   private final Vector3f up;
   private final Vector3f forward;
   private final float halfX;
   private final float halfY;
   private final float halfZ;

   public OrientedBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      this.origin = new Vector3f((maxX + minX) * 0.5F, (maxY + minY) * 0.5F, (maxZ + minZ) * 0.5F);
      this.rotation = new Quaternionf();
      this.right = new Vector3f(GLOBAL_RIGHT);
      this.up = new Vector3f(GLOBAL_UP);
      this.forward = new Vector3f(GLOBAL_FORWARD);
      this.halfX = (maxX - minX) * 0.5F;
      this.halfY = (maxY - minY) * 0.5F;
      this.halfZ = (maxZ - minZ) * 0.5F;
   }

   public OrientedBoundingBox(Vector3f origin, Vector3f dimension, Vector3f rotation, float yaw) {
      this(origin, dimension, new Quaternionf().rotationXYZ(rotation.x, rotation.y, rotation.z), yaw);
   }

   public OrientedBoundingBox(Vector3f origin, Vector3f dimension, Quaternionf rotation, float yaw) {
      this.origin = new Vector3f(origin);
      this.rotation = rotation.rotateLocalY(yaw * (float) (Math.PI / 180.0), new Quaternionf());
      this.right = GLOBAL_RIGHT.rotate(this.rotation, new Vector3f());
      this.up = GLOBAL_UP.rotate(this.rotation, new Vector3f());
      this.forward = GLOBAL_FORWARD.rotate(this.rotation, new Vector3f());
      this.halfX = dimension.x * 0.5F;
      this.halfY = dimension.y * 0.5F;
      this.halfZ = dimension.z * 0.5F;
   }

   public boolean intersects(BoundingBox aabb) {
      return this.intersects(
         new OrientedBoundingBox(
            (float)aabb.getMinX(), (float)aabb.getMinY(), (float)aabb.getMinZ(), (float)aabb.getMaxX(), (float)aabb.getMaxY(), (float)aabb.getMaxZ()
         )
      );
   }

   public boolean intersects(OrientedBoundingBox obb) {
      Vector3f traversed = new Vector3f(obb.origin.x - this.origin.x, obb.origin.y - this.origin.y, obb.origin.z - this.origin.z);
      Vector3f scaledRight = new Vector3f(this.right).mul(this.halfX);
      Vector3f scaledUp = new Vector3f(this.up).mul(this.halfY);
      Vector3f scaledForward = new Vector3f(this.forward).mul(this.halfZ);
      Vector3f otherScaledRight = new Vector3f(obb.right).mul(obb.halfX);
      Vector3f otherScaledUp = new Vector3f(obb.up).mul(obb.halfY);
      Vector3f otherScaledForward = new Vector3f(obb.forward).mul(obb.halfZ);

      for (int i = 0; i < 15; i++) {
         Vector3f check = this.getL(i, obb);
         double t = this.projectionOnAxis(traversed, check);
         double proj = this.projectionOnAxis(scaledRight, check)
            + this.projectionOnAxis(scaledUp, check)
            + this.projectionOnAxis(scaledForward, check)
            + this.projectionOnAxis(otherScaledRight, check)
            + this.projectionOnAxis(otherScaledUp, check)
            + this.projectionOnAxis(otherScaledForward, check);
         if (t > proj) {
            return false;
         }
      }

      return true;
   }

   public RayTraceResult rayTrace(@NotNull Vector3f start, @NotNull Vector3f direction, double maxDistance, Consumer<BoundingBox> consumer) {
      if (!this.origin.isFinite()) {
         return new RayTraceResult(new Vector(start.x + direction.x * maxDistance, start.y + direction.y * maxDistance, start.z + direction.z * maxDistance));
      }

      Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
      Vector3f relativeStart = start.sub(this.origin, new Vector3f()).rotate(inverse).add(this.origin);
      Vector3f relativeDirection = new Vector3f(direction).rotate(inverse);
      BoundingBox bb = BoundingBox.of(new Vector(this.origin.x, this.origin.y, this.origin.z), this.halfX, this.halfY, this.halfZ);
      if (consumer != null) {
         consumer.accept(bb);
      }

      RayTraceResult result = bb.rayTrace(
         new Vector(relativeStart.x, relativeStart.y, relativeStart.z), new Vector(relativeDirection.x, relativeDirection.y, relativeDirection.z), maxDistance
      );
      if (result == null) {
         return null;
      }

      Vector3f hitPosition = result.getHitPosition().toVector3f().sub(this.origin).rotate(this.rotation).add(this.origin);
      return new RayTraceResult(new Vector(hitPosition.x, hitPosition.y, hitPosition.z), result.getHitBlockFace());
   }

   public boolean isWithinRange(@NotNull Vector3f point, double range) {
      if (this.origin.isFinite() && Float.isFinite(this.halfX) && Float.isFinite(this.halfY) && Float.isFinite(this.halfZ)) {
         double dx = this.origin.x - point.x;
         double dy = this.origin.y - point.y;
         double dz = this.origin.z - point.z;
         float scale = Math.max(Math.max(Math.abs(this.origin.x), Math.abs(this.origin.y)), Math.abs(this.origin.z));
         double base = range + Math.sqrt((double)this.halfX * this.halfX + (double)this.halfY * this.halfY + (double)this.halfZ * this.halfZ);
         double margin = 4.0 * Math.max(Math.ulp(scale + 1.0F), Math.ulp((float)base + 1.0F));
         double reach = base + margin;
         return dx * dx + dy * dy + dz * dz <= reach * reach;
      } else {
         return true;
      }
   }

   public double distanceSquared(@NotNull Vector3f vector) {
      if (!this.origin.isFinite()) {
         return Double.NaN;
      }

      Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
      Vector3f relativeVector = vector.sub(this.origin, new Vector3f()).rotate(inverse).add(this.origin);
      BoundingBox bb = BoundingBox.of(new Vector(this.origin.x, this.origin.y, this.origin.z), this.halfX, this.halfY, this.halfZ);
      return TMath.distanceSquaredToBoundingBox(Vector.fromJOML(relativeVector), bb);
   }

   public boolean contains(@NotNull Vector3f vector) {
      Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
      Vector3f relativeVector = vector.sub(this.origin, new Vector3f()).rotate(inverse).add(this.origin);
      BoundingBox bb = BoundingBox.of(new Vector(this.origin.x, this.origin.y, this.origin.z), this.halfX, this.halfY, this.halfZ);
      return bb.contains(Vector.fromJOML(relativeVector));
   }

   private double projectionOnAxis(Vector3f vector, Vector3f axis) {
      return Math.abs(vector.dot(axis));
   }

   private Vector3f getL(int i, OrientedBoundingBox other) {
      return switch (i) {
         case 0 -> this.right;
         case 1 -> this.up;
         case 2 -> this.forward;
         case 3 -> other.right;
         case 4 -> other.up;
         case 5 -> other.forward;
         case 6 -> this.right.cross(other.right, new Vector3f());
         case 7 -> this.right.cross(other.up, new Vector3f());
         case 8 -> this.right.cross(other.forward, new Vector3f());
         case 9 -> this.up.cross(other.right, new Vector3f());
         case 10 -> this.up.cross(other.up, new Vector3f());
         case 11 -> this.up.cross(other.forward, new Vector3f());
         case 12 -> this.forward.cross(other.right, new Vector3f());
         case 13 -> this.forward.cross(other.up, new Vector3f());
         case 14 -> this.forward.cross(other.forward, new Vector3f());
         default -> throw new IllegalStateException("Unexpected value: " + i);
      };
   }

   public ItemDisplay visualize(World world, ItemStack stack) {
      Location location = new Location(world, this.origin.x, this.origin.y, this.origin.z);
      return world.spawn(
         location,
         ItemDisplay.class,
         itemDisplay -> {
            itemDisplay.setItemStack(stack);
            itemDisplay.setTransformation(
               new Transformation(new Vector3f(), this.rotation, new Vector3f(2.0F * this.halfX, 2.0F * this.halfY, 2.0F * this.halfZ), new Quaternionf())
            );
         }
      );
   }

   public void visualize(World world) {
      Vector3f offsetX = new Vector3f(this.right).mul(this.halfX);
      Vector3f offsetY = new Vector3f(this.up).mul(this.halfY);
      Vector3f offsetZ = new Vector3f(this.forward).mul(this.halfZ);
      Vector3f offsetXN = new Vector3f(offsetX).mul(-1.0F);
      Vector3f offsetYN = new Vector3f(offsetY).mul(-1.0F);
      Vector3f offsetZN = new Vector3f(offsetZ).mul(-1.0F);
      this.drawLine(offsetX, offsetY, offsetZ, world, Color.ORANGE, this.halfZ * 2.0F);
      this.drawLine(offsetX, offsetYN, offsetZ, world, Color.ORANGE, this.halfZ * 2.0F);
      this.drawLine(offsetXN, offsetY, offsetZ, world, Color.ORANGE, this.halfZ * 2.0F);
      this.drawLine(offsetXN, offsetYN, offsetZ, world, Color.ORANGE, this.halfZ * 2.0F);
      this.drawLine(offsetY, offsetZ, offsetX, world, Color.GREEN, this.halfX * 2.0F);
      this.drawLine(offsetY, offsetZN, offsetX, world, Color.GREEN, this.halfX * 2.0F);
      this.drawLine(offsetYN, offsetZ, offsetX, world, Color.GREEN, this.halfX * 2.0F);
      this.drawLine(offsetYN, offsetZN, offsetX, world, Color.GREEN, this.halfX * 2.0F);
      this.drawLine(offsetZ, offsetX, offsetY, world, Color.AQUA, this.halfY * 2.0F);
      this.drawLine(offsetZ, offsetXN, offsetY, world, Color.AQUA, this.halfY * 2.0F);
      this.drawLine(offsetZN, offsetX, offsetY, world, Color.AQUA, this.halfY * 2.0F);
      this.drawLine(offsetZN, offsetXN, offsetY, world, Color.AQUA, this.halfY * 2.0F);
   }

   private void drawLine(Vector3f offset1, Vector3f offset2, Vector3f line, World world, Color color, double dist) {
      Vector3f offset = new Vector3f(offset1).add(offset2);
      Vector3f pointA = new Vector3f(offset).add(line);
      Vector3f pointB = new Vector3f(offset).sub(line);
      double ratio = 1.0 / dist;

      for (double i = 0.0; i < dist; i += 0.1) {
         Vector3f point = TMath.lerp(pointA, pointB, i * ratio);
         world.spawnParticle(Particle.DUST, this.origin.x + point.x, this.origin.y + point.y, this.origin.z + point.z, 1, new DustOptions(color, 0.2F));
      }
   }
}
