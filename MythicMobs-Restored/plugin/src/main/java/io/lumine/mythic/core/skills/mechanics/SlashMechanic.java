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
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
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
import org.bukkit.util.BoundingBox;

@MythicMechanic(
   author = "Seyarada",
   name = "slash",
   aliases = {},
   description = "Creates a slash"
)
public class SlashMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "points",
      aliases = {"p"},
      description = "Amount of points in the arc",
      defValue = "10"
   )
   final PlaceholderInt points;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "Number of iterations",
      defValue = "0"
   )
   final PlaceholderDuration duration;
   @MythicField(
      name = "width",
      aliases = {"w"},
      description = "The width of the arc",
      defValue = "1"
   )
   final PlaceholderDouble width;
   @MythicField(
      name = "height",
      aliases = {"h"},
      description = "The height of the arc",
      defValue = "1"
   )
   final PlaceholderDouble height;
   @MythicField(
      name = "angle",
      aliases = {"a", "arc"},
      description = "The size of the arc in degrees",
      defValue = "180"
   )
   final PlaceholderDouble angle;
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
      name = "specificStep",
      aliases = {"ss"},
      description = "The specific step index of the slash to use"
   )
   protected PlaceholderInt specificStep;
   @MythicField(
      name = "onPointSkill",
      aliases = {"onpoint", "op"},
      description = "Skill triggered at each point",
      defValue = ""
   )
   final String pointSkillName;
   @MythicField(
      name = "onEndSkill",
      aliases = {"onend", "oe"},
      description = "Skill triggered at the end",
      defValue = ""
   )
   final String endSkillName;
   @MythicField(
      name = "onStartSkill",
      aliases = {"onstart", "os"},
      description = "Skill triggered at the start",
      defValue = ""
   )
   final String startSkillName;
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
      name = "directionTowardsTarget",
      description = "If the yaw/pitch should be calculated to aim to the target",
      defValue = "false"
   )
   protected boolean directionTowardsTarget;
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
   final MythicLineConfig mlc;
   AbstractLocation location;
   AbstractVector rotation;
   String hitConditionString;
   Optional<Skill> onStartSkill;
   Optional<Skill> onPointSkill;
   Optional<Skill> onEndSkill;
   Optional<Skill> onHitEntitySkill;
   double parsedAngle;
   int parsedPoints;
   final HashSet<AbstractEntity> seenEntities = new HashSet<>();

   public SlashMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.mlc = mlc;
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 32);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "0");
      this.width = mlc.getPlaceholderDouble(new String[]{"width", "w"}, 1.0);
      this.height = mlc.getPlaceholderDouble(new String[]{"height", "h"}, 1.0);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "r"}, 1.0);
      this.angle = mlc.getPlaceholderDouble(new String[]{"angle", "a", "arc"}, 180.0);
      this.pitch = mlc.getPlaceholderDouble(new String[]{"pitch"}, 0.0);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw"}, 0.0);
      this.roll = mlc.getPlaceholderDouble(new String[]{"roll"}, 0.0);
      this.rotationString = mlc.getString(new String[]{"rotation", "rot"}, "0,0,0");
      this.matchCasterDirection = mlc.getBoolean(
         new String[]{"matchCasterDirection", "matchPlayerDirection", "matchDirection", "mcd", "mpd", "md", "direction"}, true
      );
      this.fromOrigin = mlc.getBoolean(new String[]{"fromOrigin"}, false);
      this.directionTowardsTarget = mlc.getBoolean(new String[]{"directionTowardsTarget", "dtt"}, false);
      this.specificStep = mlc.getPlaceholderInteger(new String[]{"specificStep", "ss"}, 0);
      this.pointSkillName = mlc.getString(new String[]{"onpointskill", "onpoint", "op"}, "");
      this.endSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"}, "");
      this.startSkillName = mlc.getString(new String[]{"onstartskill", "onstart", "os"}, "");
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

         this.onEndSkill = this.getManager().getSkill(file, this, this.endSkillName);
         if (this.onEndSkill.isEmpty() && !this.endSkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onEndSkill " + this.endSkillName);
         }

         this.onStartSkill = this.getManager().getSkill(file, this, this.startSkillName);
         if (this.onStartSkill.isEmpty() && !this.startSkillName.isEmpty()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find onStartSkill " + this.startSkillName);
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

   public List<AbstractLocation> getLocations(PlaceholderContext context) {
      AbstractLocation pos = this.fromOrigin ? context.skillMetadata().getOrigin() : context.location();
      this.location = pos.clone();
      this.parsedPoints = this.points.get(context);
      this.parsedAngle = this.angle.get(context);
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
      List<AbstractVector> vectors = this.getArcTargets(this.width.get(context), this.height.get(context));
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

   List<AbstractVector> getArcTargets(double width, double height) {
      List<AbstractVector> targets = new ArrayList<>();
      double angleStep = this.parsedAngle / (this.parsedPoints - 1);
      double startingAngle = -this.parsedAngle / 2.0;

      for (int i = 0; i < this.parsedPoints; i++) {
         double currentAngle = Math.toRadians(startingAngle + i * angleStep);
         double x = width * Math.sin(currentAngle);
         double z = height * Math.cos(currentAngle);
         AbstractVector vector = new AbstractVector(x, 0.0, z);
         targets.add(vector);
      }

      return targets;
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
      List<AbstractLocation> locations = this.getLocations(context);
      if (this.specificStep != null) {
         int desiredStep = this.specificStep.get(context);
         if (desiredStep > 0) {
            AbstractLocation wantedLocation = locations.get(desiredStep);
            locations = List.of(wantedLocation);
         }
      }

      this.seenEntities.clear();
      int counter = 0;
      int finalDuration = this.duration.get(context).getTicks();
      if (finalDuration > 0) {
         List<List<AbstractLocation>> partitions = splitList(locations, finalDuration);
         int delay = 0;
         AtomicInteger count = new AtomicInteger();

         for (List<AbstractLocation> partition : partitions) {
            int locationSize = locations.size();
            Schedulers.async().runLater(() -> {
               for (AbstractLocation abstractLocationx : partition) {
                  this.executePoint(abstractLocationx, context, (double)count.get() / (locationSize - 1));
                  count.getAndIncrement();
               }
            }, delay);
            delay++;
         }
      } else {
         for (AbstractLocation abstractLocation : locations) {
            this.executePoint(abstractLocation, context, (double)counter / (locations.size() - 1));
            counter++;
         }
      }

      return SkillResult.SUCCESS;
   }

   void executePoint(AbstractLocation vector, PlaceholderContext context, double stage) {
      if (stage == 0.0 && this.onStartSkill.isPresent()) {
         this.onStartSkill.get().execute(context.skillMetadata().setOrigin(vector).setLocationTarget(vector));
      } else if (stage == 1.0 && this.onEndSkill.isPresent()) {
         this.onEndSkill.get().execute(context.skillMetadata().setOrigin(vector).setLocationTarget(vector));
      } else {
         this.onPointSkill.ifPresent(skill -> skill.execute(context.skillMetadata().setOrigin(vector).setLocationTarget(vector)));
      }

      this.onHitEntitySkill.ifPresent(skill -> {
         if (skill.isUsable(context.skillMetadata())) {
            Collection<AbstractEntity> entities = this.getEntitiesNearPoint(context, this.radius.get(context), vector);
            if (entities.isEmpty()) {
               return;
            }

            SkillMetadata meta = context.skillMetadata().deepCloneNoTargets();
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

   Collection<AbstractEntity> getEntitiesNearPoint(PlaceholderContext context, double radius, AbstractLocation vector) {
      Collection<AbstractEntity> targets = Lists.newArrayList();

      for (AbstractEntity p : this.getPlugin().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(vector, radius)) {
         if (!this.seenEntities.contains(p)
            && !p.getUniqueId().equals(context.meta().getCaster().getEntity().getUniqueId())
            && (!ServerVersion.isFolia() || FoliaHelper.isOwnedByCurrentRegion(p.getBukkitEntity()))
            && this.getBoundingBox(vector, radius).overlaps(p.getBukkitEntity().getBoundingBox())
            && this.entityPassesConditions(context, p)) {
            this.seenEntities.add(p);
            targets.add(p);
         }
      }

      return targets;
   }

   boolean entityPassesConditions(PlaceholderContext context, AbstractEntity entity) {
      if (this.hitConditions != null) {
         for (SkillCondition condition : this.hitConditions) {
            if (!condition.evaluateToEntity(context.skillMetadata(), entity)) {
               return false;
            }
         }
      }

      return true;
   }

   public static <T> List<List<T>> splitList(List<T> list, int n) {
      List<List<T>> parts = new ArrayList<>();
      int size = list.size();
      int partSize = size / n;
      int remainder = size % n;

      for (int i = 0; i < n; i++) {
         int fromIndex = i * partSize + Math.min(i, remainder);
         int toIndex = fromIndex + partSize + (i < remainder ? 1 : 0);
         parts.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
      }

      return parts;
   }

   public BoundingBox getBoundingBox(AbstractLocation location, double radius) {
      return BoundingBox.of(BukkitAdapter.adapt(location), radius, radius, radius);
   }
}
