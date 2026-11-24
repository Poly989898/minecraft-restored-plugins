package fr.phoenixdevt.profiles.util;

public class WrappedLocation {
   private final String worldName;
   private final double x;
   private final double y;
   private final double z;
   private final float yaw;
   private final float pitch;

   public WrappedLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
      this.worldName = worldName;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   @Override
   public String toString() {
      return "WrappedLocation{worldName='"
         + this.worldName
         + '\''
         + ", x="
         + this.x
         + ", y="
         + this.y
         + ", z="
         + this.z
         + ", yaw="
         + this.yaw
         + ", pitch="
         + this.pitch
         + '}';
   }
}
