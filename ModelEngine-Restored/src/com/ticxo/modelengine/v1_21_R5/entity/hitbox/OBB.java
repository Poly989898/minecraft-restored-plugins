package com.ticxo.modelengine.v1_21_R5.entity.hitbox;

import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class OBB extends AABB {
   private final Quaternionf rotation;
   private final float yaw;
   private final OrientedBoundingBox bukkitOBB;

   public OBB(Vec3 cornerA, Vec3 cornerB, Quaternionf rotation, float yaw) {
      this(cornerA.x, cornerA.y, cornerA.z, cornerB.x, cornerB.y, cornerB.z, rotation, yaw);
   }

   public OBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Quaternionf rotation, float yaw) {
      super(minX, minY, minZ, maxX, maxY, maxZ);
      this.rotation = rotation;
      this.yaw = yaw;
      this.bukkitOBB = new OrientedBoundingBox(
         this.getCenter().toVector3f(), new Vector3f((float)this.getXsize(), (float)this.getYsize(), (float)this.getZsize()), rotation, yaw
      );
   }

   public OBB makeOBBInstance(Vec3 position, Quaternionf rotation, float yaw) {
      return new OBB(position.add(this.minX, this.minY, this.minZ), position.add(this.maxX, this.maxY, this.maxZ), rotation, yaw);
   }

   @NotNull
   public AABB inflate(double xInflate, double yInflate, double zInflate) {
      double minX = this.minX - xInflate;
      double minY = this.minY - yInflate;
      double minZ = this.minZ - zInflate;
      double maxX = this.maxX + xInflate;
      double maxY = this.maxY + yInflate;
      double maxZ = this.maxZ + zInflate;
      return new OBB(minX, minY, minZ, maxX, maxY, maxZ, this.rotation, this.yaw);
   }

   public boolean intersects(@NotNull AABB aabb) {
      return aabb instanceof OBB obb ? this.intersects(obb) : super.intersects(aabb);
   }

   public boolean intersects(OBB obb) {
      return this.bukkitOBB.intersects(obb.bukkitOBB);
   }

   public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      OrientedBoundingBox obbB = new OrientedBoundingBox((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
      return this.bukkitOBB.intersects(obbB);
   }

   public Optional<Vec3> clip(Vec3 from, Vec3 to) {
      Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
      Vec3 center = this.getCenter();
      float yaw = this.yaw * (float) (Math.PI / 180.0);
      Vec3 rFrom = new Vec3(from.subtract(center).toVector3f().rotateY(-yaw).rotate(inverse)).add(center);
      Vec3 rTo = new Vec3(to.subtract(center).toVector3f().rotateY(-yaw).rotate(inverse)).add(center);
      Optional<Vec3> result = super.clip(rFrom, rTo);
      if (result.isEmpty()) {
         return result;
      }

      Vec3 clip = new Vec3(result.get().subtract(center).toVector3f().rotate(this.rotation).rotateY(yaw)).add(center);
      return Optional.of(clip);
   }

   @Generated
   public String toString() {
      return "OBB(rotation=" + this.getRotation() + ", yaw=" + this.getYaw() + ", bukkitOBB=" + this.getBukkitOBB() + ")";
   }
}
