package io.lumine.mythic.core.skills.bounds;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.parsers.RandomDouble;
import java.util.List;
import java.util.Optional;

public class BoundsSpec {
   private final BoundsSpec.Shape shape;
   private final BoundsSpec.Corner corner1;
   private final BoundsSpec.Corner corner2;
   private final BoundsSpec.Corner center;
   private final PlaceholderDouble radius;
   private final PlaceholderDouble height;
   private final BoundsSpec.StaticShape staticShape;

   public BoundsSpec(MythicLineConfig mlc) {
      this.shape = BoundsSpec.Shape.parse(mlc.getString(new String[]{"shape"}, "cuboid"));
      if (this.shape == BoundsSpec.Shape.CUBOID) {
         this.corner1 = parseCorner(mlc, new String[]{"corner1", "c1"}, "1");
         this.corner2 = parseCorner(mlc, new String[]{"corner2", "c2"}, "2");
         this.center = null;
         this.radius = null;
         this.height = null;
         this.staticShape = tryPrecomputeCuboid(this.corner1, this.corner2);
      } else {
         String centerValue = mlc.getString(new String[]{"location", "loc", "l", "center", "centre", "origin"}, null);
         if (centerValue == null) {
            throw new IllegalArgumentException(this.shape.configName + " bounds require location/loc (location targeter, pin name, world,x,y,z, or x,y,z)");
         }

         String radiusValue = mlc.getString(new String[]{"radius", "r"}, null);
         if (radiusValue == null) {
            throw new IllegalArgumentException(this.shape.configName + " bounds require radius/r");
         }

         String heightValue = mlc.getString(new String[]{"height", "h"}, null);
         if (this.shape == BoundsSpec.Shape.CYLINDER && heightValue == null) {
            throw new IllegalArgumentException("cylinder bounds require height/h");
         }

         this.corner1 = null;
         this.corner2 = null;
         this.center = BoundsSpec.Corner.parse(centerValue);
         this.radius = PlaceholderDouble.of(radiusValue);
         this.height = heightValue == null ? null : PlaceholderDouble.of(heightValue);
         this.staticShape = tryPrecomputeRadial(this.shape, this.center, this.radius, this.height);
      }
   }

   public boolean isStatic() {
      return this.staticShape != null;
   }

   public boolean contains(AbstractLocation location) {
      if (location == null || location.getWorld() == null) {
         return false;
      } else {
         return this.staticShape != null
            ? this.staticShape.contains(location)
            : this.contains(location, PlaceholderContext.builder().location(location).build());
      }
   }

   public boolean contains(AbstractLocation location, PlaceholderContext context) {
      if (location == null || location.getWorld() == null) {
         return false;
      } else if (this.staticShape != null) {
         return this.staticShape.contains(location);
      } else if (this.shape == BoundsSpec.Shape.CUBOID) {
         AbstractLocation loc1 = this.corner1.resolve(context, location);
         AbstractLocation loc2 = this.corner2.resolve(context, location);
         return loc1 != null && loc2 != null && aabbContains(loc1, loc2, location);
      } else {
         AbstractLocation resolvedCenter = this.center.resolve(context, location);
         if (resolvedCenter == null) {
            return false;
         } else {
            double resolvedRadius = this.radius.get(context);
            if (resolvedRadius < 0.0) {
               return false;
            } else if (this.shape == BoundsSpec.Shape.SPHERE) {
               return sphereContains(resolvedCenter, resolvedRadius, location);
            } else {
               double resolvedHeight = this.height.get(context);
               return resolvedHeight >= 0.0 && cylinderContains(resolvedCenter, resolvedRadius, resolvedHeight, location);
            }
         }
      }
   }

   private static BoundsSpec.Corner parseCorner(MythicLineConfig mlc, String[] names, String suffix) {
      String value = mlc.getString(names, null);
      if (value == null) {
         throw new IllegalArgumentException("Cuboid bounds require " + names[0] + "/" + names[1] + " (pin name, world,x,y,z, or x,y,z)");
      } else {
         return BoundsSpec.Corner.parse(value)
            .withOverrides(optionalDouble(mlc, "x" + suffix), optionalDouble(mlc, "y" + suffix), optionalDouble(mlc, "z" + suffix));
      }
   }

   private static PlaceholderDouble optionalDouble(MythicLineConfig mlc, String key) {
      String value = mlc.getString(new String[]{key}, null);
      return value == null ? null : PlaceholderDouble.of(value);
   }

   private static BoundsSpec.StaticShape tryPrecomputeCuboid(BoundsSpec.Corner a, BoundsSpec.Corner b) {
      if (a.isStatic() && b.isStatic()) {
         String worldA = a.staticWorldName();
         String worldB = b.staticWorldName();
         if (worldA != null && worldB != null && !worldA.equalsIgnoreCase(worldB)) {
            throw new IllegalArgumentException("Bounds corner worlds must match");
         } else {
            double x1 = a.x().get();
            double y1 = a.y().get();
            double z1 = a.z().get();
            double x2 = b.x().get();
            double y2 = b.y().get();
            double z2 = b.z().get();
            return new BoundsSpec.StaticCuboid(
               Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2), Math.min(z1, z2), Math.max(z1, z2), worldA != null ? worldA : worldB
            );
         }
      } else {
         return null;
      }
   }

   private static BoundsSpec.StaticShape tryPrecomputeRadial(
      BoundsSpec.Shape shape, BoundsSpec.Corner center, PlaceholderDouble radius, PlaceholderDouble height
   ) {
      if (center.isStatic() && isStaticValue(radius) && (height == null || isStaticValue(height))) {
         double resolvedRadius = radius.get();
         double resolvedHeight = height == null ? 0.0 : height.get();
         if (!(resolvedRadius < 0.0) && !(resolvedHeight < 0.0)) {
            return (BoundsSpec.StaticShape)(shape == BoundsSpec.Shape.SPHERE
               ? new BoundsSpec.StaticSphere(center.x().get(), center.y().get(), center.z().get(), resolvedRadius, center.staticWorldName())
               : new BoundsSpec.StaticCylinder(center.x().get(), center.y().get(), center.z().get(), resolvedRadius, resolvedHeight, center.staticWorldName()));
         } else {
            throw new IllegalArgumentException(shape.configName + " bounds radius and height must not be negative");
         }
      } else {
         return null;
      }
   }

   private static boolean isStaticValue(PlaceholderDouble value) {
      return value != null && value.isStatic() && !(value instanceof RandomDouble);
   }

   private static boolean sameWorld(AbstractLocation corner, AbstractLocation point) {
      return corner.getWorld() == null || corner.getWorld().getName().equalsIgnoreCase(point.getWorld().getName());
   }

   private static boolean aabbContains(AbstractLocation a, AbstractLocation b, AbstractLocation point) {
      double minX = Math.min(a.getX(), b.getX());
      double maxX = Math.max(a.getX(), b.getX());
      double minY = Math.min(a.getY(), b.getY());
      double maxY = Math.max(a.getY(), b.getY());
      double minZ = Math.min(a.getZ(), b.getZ());
      double maxZ = Math.max(a.getZ(), b.getZ());
      double x = point.getX();
      double y = point.getY();
      double z = point.getZ();
      return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
   }

   private static boolean sphereContains(AbstractLocation center, double radius, AbstractLocation point) {
      double x = point.getX() - center.getX();
      double y = point.getY() - center.getY();
      double z = point.getZ() - center.getZ();
      return x * x + y * y + z * z <= radius * radius;
   }

   private static boolean cylinderContains(AbstractLocation center, double radius, double height, AbstractLocation point) {
      double y = point.getY();
      if (!(y < center.getY()) && !(y > center.getY() + height)) {
         double x = point.getX() - center.getX();
         double z = point.getZ() - center.getZ();
         return x * x + z * z <= radius * radius;
      } else {
         return false;
      }
   }

   private record Corner(
      SkillTargeter targeter, PlaceholderString reference, PlaceholderString world, PlaceholderDouble x, PlaceholderDouble y, PlaceholderDouble z
   ) {
      static BoundsSpec.Corner parse(String value) {
         if (value.startsWith("@")) {
            SkillTargeter targeter = MythicProvider.get().getSkillManager().getTargeter(value);
            return new BoundsSpec.Corner(targeter, null, null, null, null, null);
         } else if (!value.contains(",")) {
            return new BoundsSpec.Corner(null, PlaceholderString.of(value), null, null, null, null);
         } else {
            String[] parts = value.split(",");
            if (parts.length == 3) {
               return new BoundsSpec.Corner(
                  null, null, null, PlaceholderDouble.of(parts[0].trim()), PlaceholderDouble.of(parts[1].trim()), PlaceholderDouble.of(parts[2].trim())
               );
            } else if (parts.length == 4) {
               return new BoundsSpec.Corner(
                  null,
                  null,
                  PlaceholderString.of(parts[0].trim()),
                  PlaceholderDouble.of(parts[1].trim()),
                  PlaceholderDouble.of(parts[2].trim()),
                  PlaceholderDouble.of(parts[3].trim())
               );
            } else {
               throw new IllegalArgumentException("Bounds corner must be a pin name, world,x,y,z, or x,y,z");
            }
         }
      }

      BoundsSpec.Corner withOverrides(PlaceholderDouble xOverride, PlaceholderDouble yOverride, PlaceholderDouble zOverride) {
         return new BoundsSpec.Corner(
            this.targeter,
            this.reference,
            this.world,
            xOverride == null ? this.x : xOverride,
            yOverride == null ? this.y : yOverride,
            zOverride == null ? this.z : zOverride
         );
      }

      boolean isStatic() {
         return this.targeter == null
            && this.reference == null
            && BoundsSpec.isStaticValue(this.x)
            && BoundsSpec.isStaticValue(this.y)
            && BoundsSpec.isStaticValue(this.z)
            && (this.world == null || this.world.isStatic());
      }

      String staticWorldName() {
         if (this.world == null) {
            return null;
         } else {
            String name = this.world.get();
            return name != null && !name.isBlank() ? name : null;
         }
      }

      AbstractLocation resolve(PlaceholderContext context, AbstractLocation point) {
         if (this.targeter != null) {
            AbstractLocation resolved = context.getSkillMetadata()
               .map(meta -> MythicProvider.get().getSkillManager().getLocationTarget(this.targeter, meta))
               .orElse(null);
            return resolved != null && BoundsSpec.sameWorld(resolved, point)
               ? new AbstractLocation(
                  resolved.getWorld(),
                  this.x == null ? resolved.getX() : this.x.get(context),
                  this.y == null ? resolved.getY() : this.y.get(context),
                  this.z == null ? resolved.getZ() : this.z.get(context)
               )
               : null;
         } else if (this.reference != null) {
            AbstractLocation resolved = resolveReference(this.reference.get(context), point);
            return resolved != null && BoundsSpec.sameWorld(resolved, point)
               ? new AbstractLocation(
                  resolved.getWorld(),
                  this.x == null ? resolved.getX() : this.x.get(context),
                  this.y == null ? resolved.getY() : this.y.get(context),
                  this.z == null ? resolved.getZ() : this.z.get(context)
               )
               : null;
         } else {
            if (this.world != null) {
               String name = this.world.get(context);
               if (name == null || name.isBlank() || !point.getWorld().getName().equalsIgnoreCase(name)) {
                  return null;
               }
            }

            return new AbstractLocation(point.getWorld(), this.x.get(context), this.y.get(context), this.z.get(context));
         }
      }

      private static AbstractLocation resolveReference(String value, AbstractLocation point) {
         if (value != null && !value.isEmpty()) {
            Optional<Pin> maybePin = MythicProvider.get().getPinManager().getPin(value);
            if (maybePin.isPresent()) {
               List<AbstractLocation> locs = maybePin.get().getLocations();
               if (!locs.isEmpty()) {
                  return locs.get(0);
               }
            }

            String[] parts = value.split(",");

            try {
               if (parts.length == 4) {
                  AbstractLocation parsed = AbstractLocation.deserializeFromVariableLocation(value);
                  return parsed.getWorld() == null ? null : parsed;
               }

               if (parts.length == 3) {
                  double x = Double.parseDouble(parts[0].trim());
                  double y = Double.parseDouble(parts[1].trim());
                  double z = Double.parseDouble(parts[2].trim());
                  return new AbstractLocation(point.getWorld(), x, y, z);
               }
            } catch (Exception e) {
            }

            return null;
         } else {
            return null;
         }
      }
   }

   private static enum Shape {
      CUBOID("cuboid"),
      SPHERE("sphere"),
      CYLINDER("cylinder");

      private final String configName;

      private Shape(String configName) {
         this.configName = configName;
      }

      static BoundsSpec.Shape parse(String value) {
         for (BoundsSpec.Shape shape : values()) {
            if (shape.configName.equalsIgnoreCase(value)) {
               return shape;
            }
         }

         throw new IllegalArgumentException("Unknown bounds shape '" + value + "'; expected cuboid, sphere, or cylinder");
      }
   }

   private record StaticCuboid(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, String world) implements BoundsSpec.StaticShape {
      @Override
      public boolean contains(AbstractLocation location) {
         if (this.world != null && !location.getWorld().getName().equalsIgnoreCase(this.world)) {
            return false;
         } else {
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
         }
      }
   }

   private record StaticCylinder(double x, double y, double z, double radius, double height, String world) implements BoundsSpec.StaticShape {
      @Override
      public boolean contains(AbstractLocation location) {
         if (this.world != null && !location.getWorld().getName().equalsIgnoreCase(this.world)) {
            return false;
         } else if (!(location.getY() < this.y) && !(location.getY() > this.y + this.height)) {
            double deltaX = location.getX() - this.x;
            double deltaZ = location.getZ() - this.z;
            return deltaX * deltaX + deltaZ * deltaZ <= this.radius * this.radius;
         } else {
            return false;
         }
      }
   }

   private interface StaticShape {
      boolean contains(AbstractLocation abstractLocation);
   }

   private record StaticSphere(double x, double y, double z, double radius, String world) implements BoundsSpec.StaticShape {
      @Override
      public boolean contains(AbstractLocation location) {
         if (this.world != null && !location.getWorld().getName().equalsIgnoreCase(this.world)) {
            return false;
         } else {
            double deltaX = location.getX() - this.x;
            double deltaY = location.getY() - this.y;
            double deltaZ = location.getZ() - this.z;
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= this.radius * this.radius;
         }
      }
   }
}
