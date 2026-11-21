package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.util.BoundingBox;

@MythicMechanic(
   author = "Seyarada",
   name = "polygon",
   aliases = {},
   description = "Creates a polygon"
)
public class PolygonMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "points",
      aliases = {"p"},
      description = "Amount of points in the lines",
      defValue = "10"
   )
   final PlaceholderInt points;
   @MythicField(
      name = "distanceBetween",
      aliases = {"distanceBetween", "distance", "density", "db"},
      description = "Number of iterations",
      defValue = "0.1"
   )
   final PlaceholderDouble distanceBetween;
   @MythicField(
      name = "scale",
      aliases = {"size", "s"},
      description = "Number of iterations",
      defValue = "1"
   )
   final PlaceholderDouble scale;
   @MythicField(
      name = "skip",
      aliases = {"star", "sv"},
      description = "Number of iterations",
      defValue = "2"
   )
   final PlaceholderInt skipVertices;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "Number of iterations",
      defValue = "0"
   )
   final PlaceholderDuration duration;
   @MythicField(
      name = "xOffset",
      aliases = {"xo", "x"},
      description = "X-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble xOffset;
   @MythicField(
      name = "yOffset",
      aliases = {"yo", "y"},
      description = "Y-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble yOffset;
   @MythicField(
      name = "zOffset",
      aliases = {"zo", "z"},
      description = "Z-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble zOffset;
   @MythicField(
      name = "forwardOffset",
      aliases = {"foffset", "fo"},
      description = "Forward offset",
      defValue = "0"
   )
   final PlaceholderDouble forwardOffset;
   @MythicField(
      name = "pitch",
      description = "The pitch rotation",
      defValue = "0"
   )
   final PlaceholderDouble pitch;
   @MythicField(
      name = "yaw",
      description = "The yaw rotation",
      defValue = "0"
   )
   final PlaceholderDouble yaw;
   @MythicField(
      name = "roll",
      description = "The roll rotation",
      defValue = "0"
   )
   final PlaceholderDouble roll;
   @MythicField(
      name = "radius",
      description = "The hit radius",
      defValue = "1"
   )
   final PlaceholderDouble radius;
   @MythicField(
      name = "onPointSkill",
      aliases = {"onpoint", "op"},
      description = "Skill triggered at each point",
      defValue = ""
   )
   final String pointSkillName;
   @MythicField(
      name = "onEdgeSkill",
      aliases = {"onedge", "oe"},
      description = "Skill triggered at the edges",
      defValue = ""
   )
   final String edgeSkillName;
   @MythicField(
      name = "onStarSkill",
      aliases = {"onstar", "os"},
      description = "Skill triggered at the star",
      defValue = ""
   )
   final String starSkillName;
   @MythicField(
      name = "onHitEntitySkill",
      aliases = {"onhitentity", "ohe", "oh"},
      description = "Skill triggered when it hits an entity",
      defValue = ""
   )
   final String hitEntitySkillName;
   @MythicField(
      name = "rotation",
      aliases = {"rot"},
      description = "The rotation of the cube, in the x,y,z format",
      defValue = "0,0,0"
   )
   final String rotationString;
   @MythicField(
      name = "matchCasterDirection",
      aliases = {"matchPlayerDirection", "matchDirection", "mcd", "mpd", "md", "direction"},
      description = "Match the caster's direction",
      defValue = "false"
   )
   final boolean matchCasterDirection;
   @MythicField(
      name = "fromOrigin",
      description = "Calculate the arc from the origin",
      defValue = "false"
   )
   final boolean fromOrigin;
   @MythicField(
      name = "hitConditions",
      aliases = {"conditions", "cond", "c", "oC", "hC"},
      defValue = "NONE",
      description = "Conditions applied to the hit target"
   )
   List<SkillCondition> hitConditions = null;
   @MythicField(
      name = "directionTowardsTarget",
      description = "If the yaw/pitch should be calculated to aim to the target",
      defValue = "false"
   )
   protected boolean directionTowardsTarget;
   @MythicField(
      name = "targetxOffset",
      aliases = {"xo", "x"},
      description = "Target X-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble targetxOffset;
   @MythicField(
      name = "targetyOffset",
      aliases = {"yo", "y"},
      description = "Target Y-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble targetyOffset;
   @MythicField(
      name = "targetzOffset",
      aliases = {"zo", "z"},
      description = "Target Z-axis offset",
      defValue = "0"
   )
   final PlaceholderDouble targetzOffset;
   final MythicLineConfig mlc;
   AbstractLocation location;
   AbstractVector rotation;
   String hitConditionString;
   Optional<Skill> onPointSkill;
   Optional<Skill> onEdgeSkill;
   Optional<Skill> onStarSkill;
   Optional<Skill> onHitEntitySkill;
   int parsedPoints;
   final HashSet<AbstractEntity> seenEntities = new HashSet<>();

   public PolygonMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.mlc = mlc;
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 32);
      this.distanceBetween = mlc.getPlaceholderDouble(new String[]{"distanceBetween", "distanceBetween", "distance", "density", "db", "d"}, 0.1);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
      this.scale = mlc.getPlaceholderDouble(new String[]{"scale", "size", "s"}, 1.0);
      this.skipVertices = mlc.getPlaceholderInteger(new String[]{"skip", "star", "sv"}, 2);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 1.0);
      this.pitch = mlc.getPlaceholderDouble(new String[]{"pitch"}, 0.0);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw"}, 0.0);
      this.roll = mlc.getPlaceholderDouble(new String[]{"roll"}, 0.0);
      this.rotationString = mlc.getString(new String[]{"rotation", "rot"}, "0,0,0");
      this.matchCasterDirection = mlc.getBoolean(
         new String[]{"matchCasterDirection", "matchPlayerDirection", "matchDirection", "mcd", "mpd", "md", "direction"}, true
      );
      this.fromOrigin = mlc.getBoolean(new String[]{"fromOrigin"}, false);
      this.directionTowardsTarget = mlc.getBoolean(new String[]{"directionTowardsTarget", "dtt"}, false);
      this.pointSkillName = mlc.getString(new String[]{"onpointskill", "onpoint", "op"}, "");
      this.edgeSkillName = mlc.getString(new String[]{"onEdgeSkill", "onedge", "oe"}, "");
      this.starSkillName = mlc.getString(new String[]{"onStarSkill", "onstar", "os"}, "");
      this.hitEntitySkillName = mlc.getString(new String[]{"onHitEntitySkill", "onhitentity", "ohe", "oh"}, "");
      this.hitConditionString = mlc.getString(new String[]{"hitconditions", "conditions", "cond", "c", "oC", "hC"}, null);
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"xoffset", "xo", "x"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "yo", "y"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"zoffset", "zo", "z"}, 0.0);
      this.targetxOffset = mlc.getPlaceholderDouble(new String[]{"targetxoffset", "txo", "tx"}, 0.0);
      this.targetyOffset = mlc.getPlaceholderDouble(new String[]{"targetyoffset", "tyo", "ty"}, 0.0);
      this.targetzOffset = mlc.getPlaceholderDouble(new String[]{"targetzoffset", "tzo", "tz"}, 0.0);
      this.forwardOffset = mlc.getPlaceholderDouble(new String[]{"forwardoffset", "foffset", "fo"}, 0.0);
      this.getManager().queueSecondPass(() -> {
         this.onPointSkill = this.getManager().getSkill(file, this, this.pointSkillName);
         if (this.onPointSkill.isEmpty() && !this.pointSkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onPointSkill " + this.pointSkillName);
         }

         this.onEdgeSkill = this.getManager().getSkill(file, this, this.edgeSkillName);
         if (this.onEdgeSkill.isEmpty() && !this.edgeSkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onEdgeSkill " + this.edgeSkillName);
         }

         this.onStarSkill = this.getManager().getSkill(file, this, this.starSkillName);
         if (this.onStarSkill.isEmpty() && !this.starSkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onEdgeSkill " + this.starSkillName);
         }

         this.onHitEntitySkill = this.getManager().getSkill(file, this, this.hitEntitySkillName);
         if (this.onHitEntitySkill.isEmpty() && !this.hitEntitySkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onHitEntitySkill " + this.hitEntitySkillName);
         }

         if (this.hitConditionString != null) {
            this.hitConditions = this.getPlugin().getSkillManager().getConditions(this.hitConditionString);
         }
      });
   }

   public Triple<List<AbstractLocation>, List<AbstractLocation>, List<AbstractLocation>> getLocations(PlaceholderContext context) {
      AbstractLocation pos = this.fromOrigin ? ((SkillMetadata)context.meta()).getOrigin() : context.location();
      this.location = pos.clone();
      this.parsedPoints = this.points.get(context);
      this.rotation = this.parseRotation(this.rotationString, context);
      AbstractVector casterRotation;
      if (this.matchCasterDirection) {
         casterRotation = new AbstractVector(pos.getPitch(), pos.getYaw(), 0.0F);
      } else if (this.directionTowardsTarget) {
         AbstractLocation targetPos = context.location().add(this.targetxOffset.get(context), this.targetyOffset.get(context), this.targetzOffset.get(context));
         AbstractVector directionVector = targetPos.toVector().subtract(pos.toVector());
         double yaw = Math.atan2(directionVector.getZ(), directionVector.getX());
         yaw = Math.toDegrees(yaw) - 90.0;
         yaw = (yaw + 360.0) % 360.0;
         double horizontalDistance = Math.sqrt(directionVector.getX() * directionVector.getX() + directionVector.getZ() * directionVector.getZ());
         double pitch = Math.atan2(-directionVector.getY(), horizontalDistance);
         pitch = Math.toDegrees(pitch);
         casterRotation = new AbstractVector(pitch, yaw, 0.0);
      } else {
         casterRotation = new AbstractVector(0, 0, 0);
      }

      AbstractVector direction = pos.getDirection();
      return new MutableTriple(
         this.transformVectors(context, this.getEdgeTargets(this.points.get(context), this.scale.get(context)), casterRotation, direction),
         this.transformVectors(
            context, this.getLineTargets(this.points.get(context), this.distanceBetween.get(context), this.scale.get(context)), casterRotation, direction
         ),
         this.transformVectors(
            context,
            this.getStarLineTargets(this.points.get(context), this.distanceBetween.get(context), this.scale.get(context), this.skipVertices.get(context)),
            casterRotation,
            direction
         )
      );
   }

   List<AbstractLocation> transformVectors(PlaceholderContext context, List<AbstractVector> vectors, AbstractVector casterRotation, AbstractVector direction) {
      return vectors.stream()
         .map(vector -> this.rotateAroundAxisX(vector, this.rotation.getX()))
         .map(vector -> this.rotateAroundAxisY(vector, this.rotation.getY()))
         .map(vector -> this.rotateAroundAxisZ(vector, this.rotation.getZ()))
         .map(vector -> this.rotateAroundAxisX(vector, casterRotation.getX()))
         .map(vector -> this.rotateAroundAxisY(vector, casterRotation.getY()))
         .map(vector -> vector.add(direction.clone().normalize().multiply(this.forwardOffset.get(context))))
         .map(vector -> this.location.clone().add(vector))
         .map(location1 -> location1.add(this.xOffset.get(context), this.yOffset.get(context), this.zOffset.get(context)))
         .collect(Collectors.toList());
   }

   List<AbstractVector> getEdgeTargets(int points, double scale) {
      List<AbstractVector> targets = new ArrayList<>();

      for (int iteration = 0; iteration < points; iteration++) {
         double angle = 360.0 / points * iteration;
         angle = Math.toRadians(angle);
         double x = Math.cos(angle) * scale;
         double z = Math.sin(angle) * scale;
         AbstractVector vector = new AbstractVector(x, 0.0, z);
         targets.add(vector);
      }

      return targets;
   }

   List<AbstractVector> getLineTargets(int points, double distanceBetween, double scale) {
      List<AbstractVector> targets = new ArrayList<>();

      for (int iteration = 0; iteration < points; iteration++) {
         double angle = 360.0 / points * iteration;
         double nextAngle = 360.0 / points * (iteration + 1);
         angle = Math.toRadians(angle);
         nextAngle = Math.toRadians(nextAngle);
         double x = Math.cos(angle) * scale;
         double z = Math.sin(angle) * scale;
         double x2 = Math.cos(nextAngle) * scale;
         double z2 = Math.sin(nextAngle) * scale;
         double deltaX = x2 - x;
         double deltaZ = z2 - z;
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         int numSegments = (int)Math.floor(distance / distanceBetween);

         for (int i = 0; i < numSegments; i++) {
            double t = (double)i / numSegments;
            AbstractVector vector = new AbstractVector(x + deltaX * t, 0.0, z + deltaZ * t);
            targets.add(vector);
         }
      }

      return targets;
   }

   List<AbstractVector> getStarTargets(int points, double scale, int skipVertices) {
      List<AbstractVector> targets = new ArrayList<>();

      for (int iteration = 0; iteration < points; iteration++) {
         double angle = 360.0 / points * (iteration * (skipVertices + 1));
         angle = Math.toRadians(angle);
         double x = Math.cos(angle) * scale;
         double z = Math.sin(angle) * scale;
         AbstractVector vector = new AbstractVector(x, 0.0, z);
         targets.add(vector);
      }

      return targets;
   }

   List<AbstractVector> getStarLineTargets(int points, double distanceBetween, double scale, int skipVertices) {
      List<AbstractVector> starVertices = this.getStarTargets(points, scale, skipVertices);
      List<AbstractVector> lineTargets = new ArrayList<>();

      for (int i = 0; i < points; i++) {
         AbstractVector currentVertex = starVertices.get(i);
         AbstractVector nextVertex = starVertices.get((i + 1) % points);
         double deltaX = nextVertex.getX() - currentVertex.getX();
         double deltaZ = nextVertex.getZ() - currentVertex.getZ();
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         int numSegments = (int)Math.floor(distance / distanceBetween);

         for (int j = 0; j < numSegments; j++) {
            double t = (double)j / numSegments;
            AbstractVector vector = new AbstractVector(currentVertex.getX() + deltaX * t, 0.0, currentVertex.getZ() + deltaZ * t);
            lineTargets.add(vector);
         }
      }

      return lineTargets;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.cast(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.cast(context);
   }

   public SkillResult cast(PlaceholderContext context) {
      Triple<List<AbstractLocation>, List<AbstractLocation>, List<AbstractLocation>> triple = this.getLocations(context);
      this.seenEntities.clear();
      this.handleLocations(context, (List<AbstractLocation>)triple.getLeft(), 0);
      this.handleLocations(context, (List<AbstractLocation>)triple.getMiddle(), 1);
      this.handleLocations(context, (List<AbstractLocation>)triple.getRight(), 2);
      return SkillResult.SUCCESS;
   }

   void handleLocations(PlaceholderContext context, List<AbstractLocation> locations, int type) {
      int finalDuration = this.duration.get(context).getTicks();
      if (finalDuration > 0) {
         List<List<AbstractLocation>> partitions = splitListEqually(locations, finalDuration);
         int delay = 0;
         AtomicInteger count = new AtomicInteger();

         for (List<AbstractLocation> partition : partitions) {
            Schedulers.async().runLater(() -> {
               for (AbstractLocation abstractLocationx : partition) {
                  this.executePoint(abstractLocationx, context, type);
                  count.getAndIncrement();
               }
            }, delay);
            delay++;
         }
      } else {
         for (AbstractLocation abstractLocation : locations) {
            this.executePoint(abstractLocation, context, type);
         }
      }
   }

   void executePoint(AbstractLocation vector, PlaceholderContext context, int type) {
      SkillMetadata data = (SkillMetadata)context.meta();
      if (type == 0) {
         this.onEdgeSkill.ifPresent(skill -> skill.execute(data.setOrigin(vector).setLocationTarget(vector)));
      } else if (type == 1) {
         this.onPointSkill.ifPresent(skill -> skill.execute(data.setOrigin(vector).setLocationTarget(vector)));
      } else {
         this.onStarSkill.ifPresent(skill -> skill.execute(data.setOrigin(vector).setLocationTarget(vector)));
      }

      this.onHitEntitySkill.ifPresent(skill -> {
         if (skill.isUsable(data)) {
            Collection<AbstractEntity> entities = this.getEntitiesNearPoint(data, this.radius.get(context), vector);
            SkillMetadata meta = data.deepCloneNoTargets();
            meta.setEntityTargets(entities);
            meta.setOrigin(vector);
            skill.execute(meta);
         }
      });
   }

   AbstractVector parseRotation(String rotationString, PlaceholderContext context) {
      String[] split = rotationString.split(",");
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (split.length == 3) {
         try {
            x = PlaceholderDouble.of(split[0]).get(context);
            y = PlaceholderDouble.of(split[1]).get(context);
            z = PlaceholderDouble.of(split[2]).get(context);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
         }
      } else {
         MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
      }

      double pitch = this.pitch.get(context);
      if (pitch != 0.0) {
         x = pitch;
      }

      double yaw = this.yaw.get(context);
      if (yaw != 0.0) {
         y = yaw;
      }

      double roll = this.roll.get(context);
      if (roll != 0.0) {
         z = roll;
      }

      return new AbstractVector(x, y, z);
   }

   AbstractVector rotateAroundAxisX(AbstractVector v, double angle) {
      angle = Math.toRadians(angle);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double y = v.getY() * cos - v.getZ() * sin;
      double z = v.getY() * sin + v.getZ() * cos;
      return v.setY(y).setZ(z);
   }

   AbstractVector rotateAroundAxisY(AbstractVector v, double angle) {
      angle = -angle;
      angle = Math.toRadians(angle);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x = v.getX() * cos + v.getZ() * sin;
      double z = v.getX() * -sin + v.getZ() * cos;
      return v.setX(x).setZ(z);
   }

   AbstractVector rotateAroundAxisZ(AbstractVector v, double angle) {
      angle = Math.toRadians(angle);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double x = v.getX() * cos - v.getY() * sin;
      double y = v.getX() * sin + v.getY() * cos;
      return v.setX(x).setY(y);
   }

   Collection<AbstractEntity> getEntitiesNearPoint(SkillMetadata data, double radius, AbstractLocation vector) {
      Collection<AbstractEntity> targets = Lists.newArrayList();

      for (AbstractEntity p : this.getPlugin().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(vector, radius)) {
         if (!this.seenEntities.contains(p)
            && !p.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())
            && this.getBoundingBox(vector, radius).overlaps(p.getBukkitEntity().getBoundingBox())
            && this.entityPassesConditions(data, p)) {
            this.seenEntities.add(p);
            targets.add(p);
         }
      }

      return targets;
   }

   boolean entityPassesConditions(SkillMetadata data, AbstractEntity entity) {
      if (this.hitConditions != null) {
         for (SkillCondition condition : this.hitConditions) {
            if (!condition.evaluateToEntity(data, entity)) {
               return false;
            }
         }
      }

      return true;
   }

   public static <T> List<List<T>> splitListEqually(List<T> list, int numOfSublists) {
      List<List<T>> sublists = new ArrayList<>(numOfSublists);

      for (int i = 0; i < numOfSublists; i++) {
         sublists.add(new ArrayList<>());
      }

      for (int i = 0; i < list.size(); i++) {
         int sublistIndex = i % numOfSublists;
         sublists.get(sublistIndex).add(list.get(i));
      }

      return sublists;
   }

   public BoundingBox getBoundingBox(AbstractLocation location, double radius) {
      return BoundingBox.of(BukkitAdapter.adapt(location), radius, radius, radius);
   }
}
