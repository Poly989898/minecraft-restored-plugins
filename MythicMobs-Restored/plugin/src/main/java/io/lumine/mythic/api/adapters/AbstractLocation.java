package io.lumine.mythic.api.adapters;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Direction;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.bukkit.utils.serialize.Point;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import java.util.Optional;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class AbstractLocation implements Cloneable {
   private AbstractWorld world;
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public static AbstractLocation deserialize(String element) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      double x = Double.parseDouble(values[0]);
      double y = Double.parseDouble(values[1]);
      double z = Double.parseDouble(values[2]);
      String world = values[3];
      return new AbstractLocation(world, x, y, z);
   }

   public static AbstractLocation deserializeFromVariableLocation(String element) {
      Preconditions.checkNotNull(element);
      String[] values = element.split(",");
      String world = values[0];
      double x = Double.parseDouble(values[1]);
      double y = Double.parseDouble(values[2]);
      double z = Double.parseDouble(values[3]);
      return new AbstractLocation(world, x, y, z);
   }

   public static AbstractLocation from(Vector3dc position, float yaw, float pitch) {
      return new AbstractLocation(null, position.x(), position.y(), position.z(), yaw, pitch);
   }

   public AbstractLocation() {
   }

   public AbstractLocation(double x, double y, double z) {
      this.world = null;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractLocation(String strWorld, double x, double y, double z) {
      this.world = MythicProvider.get().getBootstrap().getWorld(strWorld);
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractLocation(AbstractWorld world, double x, double y, double z) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public AbstractLocation(AbstractWorld world, double x, double y, double z, float yaw, float pitch) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public AbstractLocation(Position position) {
      this.world = MythicProvider.get().getBootstrap().getWorld(position.getWorld());
      this.x = position.getX();
      this.y = position.getY();
      this.z = position.getZ();
      this.yaw = 0.0F;
      this.pitch = 0.0F;
   }

   public Position toPosition() {
      return Position.of(this.x, this.y, this.z, BukkitAdapter.adapt(this.world));
   }

   public Locus toLocus() {
      return Locus.of(this.x, this.y, this.z);
   }

   public Point toPoint() {
      return Point.of(this.toPosition(), Direction.of(this.yaw, this.pitch));
   }

   public AbstractLocation block() {
      return new AbstractLocation(this.getWorld(), (double)this.getBlockX(), (double)this.getBlockY(), (double)this.getBlockZ());
   }

   public AbstractLocation center() {
      return new AbstractLocation(this.getWorld(), this.getCenterX(), this.getCenterY(), this.getCenterZ());
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getX() {
      return this.x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getY() {
      return this.y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public double getZ() {
      return this.z;
   }

   public int getBlockX() {
      return Numbers.floor(this.x);
   }

   public int getBlockY() {
      return Numbers.floor(this.y);
   }

   public int getBlockZ() {
      return Numbers.floor(this.z);
   }

   public int getChunkX() {
      return this.getBlockX() / 16;
   }

   public int getChunkZ() {
      return this.getBlockZ() / 16;
   }

   public double getCenterX() {
      return Numbers.floor(this.x) + 0.5F;
   }

   public double getCenterY() {
      return Numbers.floor(this.y) + 0.5F;
   }

   public double getCenterZ() {
      return Numbers.floor(this.z) + 0.5F;
   }

   public void setPosition(Vector3dc position) {
      this.x = position.x();
      this.y = position.y();
      this.z = position.z();
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public AbstractWorld getWorld() {
      return this.world;
   }

   public double distance(AbstractLocation t) throws IllegalArgumentException {
      return Math.sqrt(this.distanceSquared(t));
   }

   public double distanceSquared(AbstractLocation t) throws IllegalArgumentException {
      AbstractWorld tWorld = t.getWorld();
      if (this.world == tWorld || this.world != null && this.world.equals(tWorld)) {
         double dx = t.getX() - this.getX();
         double dy = t.getY() - this.getY();
         double dz = t.getZ() - this.getZ();
         return dx * dx + dy * dy + dz * dz;
      } else {
         throw new IllegalArgumentException("Cannot measure distance between two different worlds.");
      }
   }

   public double distanceSquared(AbstractEntity entity) {
      if (!entity.getWorld().equals(this.world)) {
         throw new IllegalArgumentException("Cannot measure distance between two different worlds.");
      } else {
         Optional<AbstractModelEngineSupport> maybeMeg = MythicBukkit.inst().getCompatibility().getModelEngine();
         if (maybeMeg.isPresent()) {
            AbstractModelEngineSupport support = maybeMeg.get();
            if (support.isSubHitbox(entity.getUniqueId())) {
               return support.distanceSquaredToSubHitbox(this, entity);
            }
         }

         BoundingBox box = entity.getBukkitEntity().getBoundingBox();
         double x = Math.max(Math.abs(this.getX() - box.getCenterX()) - box.getWidthX() * 0.5, 0.0);
         double y = Math.max(Math.abs(this.getY() - box.getCenterY()) - box.getHeight() * 0.5, 0.0);
         double z = Math.max(Math.abs(this.getZ() - box.getCenterZ()) - box.getWidthZ() * 0.5, 0.0);
         return x * x + y * y + z * z;
      }
   }

   public AbstractVector closestPoint(AbstractEntity entity) {
      BoundingBox box = entity.getBukkitEntity().getBoundingBox();
      double x = Math.max(box.getMinX(), Math.min(this.getX(), box.getMaxX())) - this.getX();
      double y = Math.max(box.getMinY(), Math.min(this.getY(), box.getMaxY())) - this.getY();
      double z = Math.max(box.getMinZ(), Math.min(this.getZ(), box.getMaxZ())) - this.getZ();
      return new AbstractVector(x, y, z);
   }

   public double distance2D(AbstractLocation t) {
      return Math.sqrt(this.distance2DSquared(t));
   }

   public double distance2DSquared(AbstractLocation t) {
      if (!t.getWorld().equals(this.world)) {
         throw new IllegalArgumentException("Cannot measure distance between two different worlds.");
      } else {
         return Math.pow(t.getX() - this.getX(), 2.0) + Math.pow(t.getZ() - this.getZ(), 2.0);
      }
   }

   public boolean distanceLessThan(AbstractLocation location, double distance) {
      return this.distanceLessThanSquared(location, Math.pow(distance, 2.0));
   }

   public boolean distanceLessThanSquared(AbstractLocation location, double distance) {
      return !location.getWorld().equals(this.world) ? false : this.distanceSquared(location) < distance;
   }

   public boolean distanceLessOrEq(AbstractLocation location, double distance) {
      return this.distanceLessOrEqSquared(location, Math.pow(distance, 2.0));
   }

   public boolean distanceLessOrEqSquared(AbstractLocation location, double distance) {
      return !location.getWorld().equals(this.world) ? false : this.distanceSquared(location) <= distance;
   }

   public boolean distanceGreaterThan(AbstractLocation location, double distance) {
      return this.distanceGreaterThanSquared(location, Math.pow(distance, 2.0));
   }

   public boolean distanceGreaterThanSquared(AbstractLocation location, double distance) {
      return !location.getWorld().equals(this.world) ? false : this.distanceSquared(location) > distance;
   }

   public boolean distanceGreaterOrEq(AbstractLocation location, double distance) {
      return this.distanceGreaterOrEqSquared(location, Math.pow(distance, 2.0));
   }

   public boolean distanceGreaterOrEqSquared(AbstractLocation location, double distance) {
      return !location.getWorld().equals(this.world) ? false : this.distanceSquared(location) >= distance;
   }

   public AbstractLocation add(double xA, double yA, double zA) {
      this.x += xA;
      this.y += yA;
      this.z += zA;
      return this;
   }

   public AbstractVector getDirection() {
      AbstractVector vector = new AbstractVector();
      double rotX = this.getYaw();
      double rotY = this.getPitch();
      vector.setY(-Math.sin(Math.toRadians(rotY)));
      double xz = Math.cos(Math.toRadians(rotY));
      vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
      vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
      return vector;
   }

   public AbstractLocation setDirection(AbstractVector vector) {
      double _2PI = Math.PI * 2;
      double x = vector.getX();
      double z = vector.getZ();
      if (x == 0.0 && z == 0.0) {
         this.pitch = vector.getY() > 0.0 ? -90.0F : 90.0F;
         return this;
      } else {
         double theta = Math.atan2(-x, z);
         this.yaw = (float)Math.toDegrees((theta + (Math.PI * 2)) % (Math.PI * 2));
         double x2 = NumberConversions.square(x);
         double z2 = NumberConversions.square(z);
         double xz = Math.sqrt(x2 + z2);
         this.pitch = (float)Math.toDegrees(Math.atan(-vector.getY() / xz));
         return this;
      }
   }

   public AbstractLocation add(AbstractLocation vec) {
      if (vec != null && vec.getWorld().equals(this.getWorld())) {
         this.x = this.x + vec.x;
         this.y = this.y + vec.y;
         this.z = this.z + vec.z;
         return this;
      } else {
         throw new IllegalArgumentException("Cannot add Locations of differing worlds");
      }
   }

   public AbstractLocation add(AbstractVector vec) {
      this.x = this.x + vec.getX();
      this.y = this.y + vec.getY();
      this.z = this.z + vec.getZ();
      return this;
   }

   public AbstractLocation subtract(double x, double y, double z) {
      this.x -= x;
      this.y -= y;
      this.z -= z;
      return this;
   }

   public AbstractLocation subtract(AbstractLocation vec) {
      if (vec != null && vec.getWorld().equals(this.getWorld())) {
         this.x = this.x - vec.x;
         this.y = this.y - vec.y;
         this.z = this.z - vec.z;
         return this;
      } else {
         throw new IllegalArgumentException("Cannot add Locations of differing worlds");
      }
   }

   public AbstractLocation subtract(AbstractVector vec) {
      this.x = this.x - vec.getX();
      this.y = this.y - vec.getY();
      this.z = this.z - vec.getZ();
      return this;
   }

   public AbstractLocation multiply(double m) {
      this.x *= m;
      this.y *= m;
      this.z *= m;
      return this;
   }

   public AbstractLocation interpolate(AbstractLocation point, int t) {
      return this.clone().multiply(1 - t).add(point.clone().multiply(t));
   }

   public AbstractLocation zero() {
      this.x = 0.0;
      this.y = 0.0;
      this.z = 0.0;
      return this;
   }

   public AbstractVector toVector() {
      return new AbstractVector(this.x, this.y, this.z);
   }

   public Vector3dc toVector3d() {
      return new Vector3d(this.x, this.y, this.z);
   }

   public AbstractLocation getRelative(AbstractDirection face) {
      return this.getRelative(face, 1);
   }

   public AbstractLocation getRelative(AbstractDirection face, int distance) {
      return this.clone().add(face.getStepX() * distance, face.getStepY() * distance, face.getStepZ() * distance);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AbstractLocation other = (AbstractLocation)obj;
         if (this.world == other.world || this.world != null && this.world.equals(other.world)) {
            if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
               return false;
            } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
               return false;
            } else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
               return false;
            } else {
               return Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)
                  ? false
                  : Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
            }
         } else {
            return false;
         }
      }
   }

   public String serializeShortForm() {
      return this.x + "," + this.y + "," + this.z + "," + this.world.getName();
   }

   @Override
   public String toString() {
      return "Location{world="
         + this.world
         + ",x="
         + formatDouble(this.x)
         + ",y="
         + formatDouble(this.y)
         + ",z="
         + formatDouble(this.z)
         + ",pitch="
         + formatFloat(this.pitch)
         + ",yaw="
         + formatFloat(this.yaw)
         + "}";
   }

   private static String formatDouble(double d) {
      long l = (long)d;
      if (l == d) {
         return l == 0L && Double.doubleToRawLongBits(d) == Long.MIN_VALUE ? "-0.0" : l + ".0";
      } else if (d == l + 0.5) {
         return l + ".5";
      } else if (d == l - 0.5) {
         return l == 0L ? "-0.5" : l + ".5";
      } else {
         return String.valueOf(d);
      }
   }

   private static String formatFloat(float f) {
      int i = (int)f;
      if (i == f) {
         return i == 0 && Float.floatToRawIntBits(f) == Integer.MIN_VALUE ? "-0.0" : i + ".0";
      } else if (f == i + 0.5F) {
         return i + ".5";
      } else if (f == i - 0.5F) {
         return i == 0 ? "-0.5" : i + ".5";
      } else {
         return String.valueOf(f);
      }
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 19 * hash + (this.world != null ? this.world.hashCode() : 0);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
      hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
      hash = 19 * hash + Float.floatToIntBits(this.pitch);
      return 19 * hash + Float.floatToIntBits(this.yaw);
   }

   public AbstractLocation clone() {
      try {
         return (AbstractLocation)super.clone();
      } catch (CloneNotSupportedException e) {
         throw new Error(e);
      }
   }

   public boolean isLoaded() {
      return this.getWorld().isLocationLoaded(this);
   }

   public AbstractBiome getBiome() {
      return this.getWorld().getLocationBiome(this);
   }

   public boolean isFinite() {
      return NumberConversions.isFinite(this.x)
         && NumberConversions.isFinite(this.y)
         && NumberConversions.isFinite(this.z)
         && NumberConversions.isFinite(this.yaw)
         && NumberConversions.isFinite(this.pitch);
   }

   public AbstractBlock getBlock() {
      return MythicProvider.get().getBootstrap().getBlock(this);
   }
}
