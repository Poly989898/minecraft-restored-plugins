package com.nexomc.nexo.utils;

import kotlin.Metadata;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000:\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a(\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u001a(\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00062\b\b\u0002\u0010\u0004\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u001a(\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00072\b\b\u0002\u0010\u0004\u001a\u00020\u00072\b\b\u0002\u0010\u0005\u001a\u00020\u0007\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\b\u001a\u00020\t\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\b\u001a\u00020\n\u001a(\u0010\u000b\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u001a(\u0010\u000b\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00062\b\b\u0002\u0010\u0004\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u001a\u0012\u0010\u000b\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\b\u001a\u00020\t\u001a\u0012\u0010\f\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\r\u001a\u00020\n\u001a\"\u0010\f\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003\u001a\r\u0010\u000e\u001a\u00020\u0006*\u00020\u0001H\u0086\u0002\u001a\r\u0010\u000f\u001a\u00020\u0006*\u00020\u0001H\u0086\u0002\u001a\r\u0010\u0010\u001a\u00020\u0006*\u00020\u0001H\u0086\u0002\u001a\r\u0010\u0011\u001a\u00020\u0007*\u00020\u0001H\u0086\u0002\u001a\r\u0010\u0012\u001a\u00020\u0007*\u00020\u0001H\u0086\u0002\"\u001a\u0010\u0013\u001a\u00070\u0001¢\u0006\u0002\b\u0014*\u00020\u00158F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017\"\u0015\u0010\u0018\u001a\u00020\u0001*\u00020\u00198F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001b¨\u0006\u001c"},
   d2 = {"plus", "Lorg/bukkit/Location;", "x", "", "y", "z", "", "", "vector", "Lorg/bukkit/util/Vector;", "Lorg/joml/Vector3f;", "minus", "add", "vector3f", "component1", "component2", "component3", "component4", "component5", "blockLocation", "Lorg/jetbrains/annotations/NotNull;", "Lorg/bukkit/entity/Entity;", "getBlockLocation", "(Lorg/bukkit/entity/Entity;)Lorg/bukkit/Location;", "translatedLocation", "Lorg/bukkit/entity/ItemDisplay;", "getTranslatedLocation", "(Lorg/bukkit/entity/ItemDisplay;)Lorg/bukkit/Location;", "com.nexomc:core"}
)
public final class LocationUtilsKt {
   @NotNull
   public static final Location plus(@NotNull Location location, int x, int y, int z) {
      return new Location(location.getWorld(), location.getX() + (double)x, location.getY() + (double)y, location.getZ() + (double)z);
   }

   @NotNull
   public static final Location plus(@NotNull Location location, double x, double y, double z) {
      return new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z);
   }

   @NotNull
   public static final Location plus(@NotNull Location location, float x, float y, float z) {
      return new Location(location.getWorld(), location.getX() + (double)x, location.getY() + (double)y, location.getZ() + (double)z);
   }

   @NotNull
   public static final Location plus(@NotNull Location location, @NotNull Vector vector) {
      return new Location(location.getWorld(), location.getX() + vector.getX(), location.getY() + vector.getY(), location.getZ() + vector.getZ());
   }

   @NotNull
   public static final Location plus(@NotNull Location location, @NotNull Vector3f vector) {
      return new Location(location.getWorld(), location.getX() + (double)vector.x, location.getY() + (double)vector.y, location.getZ() + (double)vector.z);
   }

   @NotNull
   public static final Location minus(@NotNull Location location, int x, int y, int z) {
      return new Location(location.getWorld(), location.getX() - (double)x, location.getY() - (double)y, location.getZ() - (double)z);
   }

   @NotNull
   public static final Location minus(@NotNull Location location, double x, double y, double z) {
      return new Location(location.getWorld(), location.getX() - x, location.getY() - y, location.getZ() - z);
   }

   @NotNull
   public static final Location minus(@NotNull Location location, @NotNull Vector vector) {
      return new Location(location.getWorld(), location.getX() - vector.getX(), location.getY() - vector.getY(), location.getZ() - vector.getZ());
   }

   @NotNull
   public static final Location add(@NotNull Location location2, @NotNull Vector3f vector3f) {
      Location location = location2.add((double)vector3f.x, (double)vector3f.y, (double)vector3f.z);
      return location;
   }

   @NotNull
   public static final Location add(@NotNull Location location2, int x, int y, int z) {
      Location location = location2.add((double)x, (double)y, (double)z);
      return location;
   }

   public static final double component1(@NotNull Location location) {
      return location.getX();
   }

   public static final double component2(@NotNull Location location) {
      return location.getY();
   }

   public static final double component3(@NotNull Location location) {
      return location.getZ();
   }

   public static final float component4(@NotNull Location location) {
      return location.getYaw();
   }

   public static final float component5(@NotNull Location location) {
      return location.getPitch();
   }

   @NotNull
   public static final Location getBlockLocation(@NotNull Entity entity) {
      Location toBlockLocation = entity.getLocation().toBlockLocation();
      return toBlockLocation;
   }

   @NotNull
   public static final Location getTranslatedLocation(@NotNull ItemDisplay itemDisplay) {
      Location location = itemDisplay.getLocation();
      Vector3f translation = itemDisplay.getTransformation().getTranslation();
      return add(location, translation);
   }
}
