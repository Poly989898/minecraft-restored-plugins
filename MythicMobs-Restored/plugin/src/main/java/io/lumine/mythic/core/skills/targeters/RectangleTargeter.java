package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MythicTargeter(
   author = "Seyarada",
   name = "rectangle",
   aliases = {"cube", "cuboid"},
   description = "Targets points in a rectangle"
)
public class RectangleTargeter extends ILocationSelector {
   @MythicFields({@MythicField(
         name = "x",
         description = "The x length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      )})
   private final PlaceholderDouble x;
   @MythicFields({@MythicField(
         name = "x",
         description = "The x length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      )})
   private final PlaceholderDouble y;
   @MythicFields({@MythicField(
         name = "x",
         description = "The x length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      ), @MythicField(
         name = "y",
         description = "The z length of the cube",
         defValue = "1"
      )})
   private final PlaceholderDouble z;
   @MythicFields({@MythicField(
         name = "xOffset",
         description = "The xOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The yOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The zOffset of the cube",
         defValue = "0"
      )})
   private final PlaceholderDouble xOffset;
   @MythicFields({@MythicField(
         name = "xOffset",
         description = "The xOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The yOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The zOffset of the cube",
         defValue = "0"
      )})
   private final PlaceholderDouble yOffset;
   @MythicFields({@MythicField(
         name = "xOffset",
         description = "The xOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The yOffset of the cube",
         defValue = "0"
      ), @MythicField(
         name = "yOffset",
         description = "The zOffset of the cube",
         defValue = "0"
      )})
   private final PlaceholderDouble zOffset;
   @MythicField(
      name = "points",
      aliases = {"p", "density", "d"},
      description = "Amount of points per cube line",
      defValue = "10"
   )
   private final PlaceholderInt points;
   @MythicField(
      name = "fill",
      aliases = {"filled", "f"},
      description = "If the cube should be filled",
      defValue = "false"
   )
   private final boolean filled;
   @MythicField(
      name = "outline",
      aliases = {"onlyOutline", "o", "edge", "onlyEdge", "e"},
      description = "If only the edge should be drawn",
      defValue = "false"
   )
   private final boolean onlyOutline;
   @MythicField(
      name = "fromOrigin",
      aliases = {"origin"},
      description = "If the location should be the origin",
      defValue = "false"
   )
   private final boolean fromOrigin;
   @MythicField(
      name = "rotation",
      aliases = {"r"},
      description = "The rotation of the cube, in the x,y,z format",
      defValue = "0,0,0"
   )
   private final String rotationString;
   final MythicLineConfig mlc;
   private double parsedX;
   private double parsedY;
   private double parsedZ;
   private int parsedPoints;
   private AbstractVector rotation;
   private AbstractLocation location;

   public RectangleTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.mlc = mlc;
      this.x = mlc.getPlaceholderDouble(new String[]{"x"}, 1.0);
      this.y = mlc.getPlaceholderDouble(new String[]{"y"}, 1.0);
      this.z = mlc.getPlaceholderDouble(new String[]{"z"}, 1.0);
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"xOffset"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yOffset"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"zOffset"}, 0.0);
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p", "density", "d"}, 10);
      this.filled = mlc.getBoolean(new String[]{"filled", "fill", "f"}, false);
      this.onlyOutline = mlc.getBoolean(new String[]{"onlyedge", "onlyoutline", "outline", "edge", "e", "o"}, false);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromOrigin", "origin"}, false);
      this.rotationString = mlc.getString(new String[]{"rotation", "r"}, "0,0,0");
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      AbstractLocation target = data.getCaster().getLocation();
      this.location = this.fromOrigin ? data.getOrigin().clone() : target.clone();
      this.location.add(this.xOffset.get(data), this.yOffset.get(data), this.zOffset.get(data));
      this.rotation = this.parseRotation(this.rotationString, data);
      this.parsedPoints = this.points.get(data);
      this.parsedX = this.x.get(data);
      this.parsedY = this.y.get(data);
      this.parsedZ = this.z.get(data);
      return this.onlyOutline ? this.getOutlineTargets() : this.getCuboidTargets();
   }

   List<AbstractLocation> getOutlineTargets() {
      List<AbstractLocation> targets = new ArrayList<>();
      double xStep = this.parsedX / (this.parsedPoints - 1);
      double yStep = this.parsedY / (this.parsedPoints - 1);
      double zStep = this.parsedZ / (this.parsedPoints - 1);

      for (int i = 0; i < this.parsedPoints; i++) {
         double x = i * xStep;
         double y = i * yStep;
         double z = i * zStep;
         targets.add(this.rotateTargetPoint(new AbstractVector(x, 0.0, 0.0)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX - x, 0.0, this.parsedZ)));
         targets.add(this.rotateTargetPoint(new AbstractVector(0.0, 0.0, z)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX, 0.0, this.parsedZ - z)));
         targets.add(this.rotateTargetPoint(new AbstractVector(x, this.parsedY, 0.0)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX - x, this.parsedY, this.parsedZ)));
         targets.add(this.rotateTargetPoint(new AbstractVector(0.0, this.parsedY, z)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX, this.parsedY, this.parsedZ - z)));
         targets.add(this.rotateTargetPoint(new AbstractVector(0.0, y, 0.0)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX, y, 0.0)));
         targets.add(this.rotateTargetPoint(new AbstractVector(0.0, y, this.parsedZ)));
         targets.add(this.rotateTargetPoint(new AbstractVector(this.parsedX, y, this.parsedZ)));
      }

      return targets;
   }

   List<AbstractLocation> getCuboidTargets() {
      List<AbstractLocation> targets = new ArrayList<>();
      double xStep = this.parsedX / this.parsedPoints;
      double yStep = this.parsedY / this.parsedPoints;
      double zStep = this.parsedZ / this.parsedPoints;

      for (int x = 0; x < this.parsedPoints; x++) {
         for (int y = 0; y < this.parsedPoints; y++) {
            for (int z = 0; z < this.parsedPoints; z++) {
               if (this.filled || this.isBorderPoint(x, y, z, this.parsedPoints)) {
                  AbstractVector vector = new AbstractVector(x * xStep, y * yStep, z * zStep);
                  targets.add(this.rotateTargetPoint(vector));
               }
            }
         }
      }

      return targets;
   }

   AbstractLocation rotateTargetPoint(AbstractVector vector) {
      vector.rotateAroundX(Math.toRadians(this.rotation.getX()));
      vector.rotateAroundY(Math.toRadians(this.rotation.getY()));
      vector.rotateAroundZ(Math.toRadians(this.rotation.getZ()));
      return this.location.clone().add(vector);
   }

   boolean isBorderPoint(int x, int y, int z, int pointDensity) {
      return x == 0 || y == 0 || z == 0 || x == pointDensity - 1 || y == pointDensity - 1 || z == pointDensity - 1;
   }

   AbstractVector parseRotation(String rotationString, SkillMetadata data) {
      String[] split = rotationString.split(",");
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (split.length == 3) {
         try {
            x = PlaceholderInt.of(split[0]).get(data);
            y = PlaceholderInt.of(split[1]).get(data);
            z = PlaceholderInt.of(split[2]).get(data);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
         }
      } else {
         MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
      }

      return new AbstractVector(x, y, z);
   }
}
