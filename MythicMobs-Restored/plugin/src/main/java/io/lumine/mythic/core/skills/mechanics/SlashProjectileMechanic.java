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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletType;
import io.lumine.mythic.core.utils.BlockUtil;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.util.BoundingBox;

@MythicMechanic(
   author = "Seyarada",
   name = "slashprojectile",
   aliases = {"slashp", "sp"},
   description = "Creates a slash that travels as a projectile"
)
public class SlashProjectileMechanic extends Projectile implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "points",
      aliases = {"p"},
      description = "Amount of points in the arc",
      defValue = "10"
   )
   final PlaceholderInt points;
   @MythicField(
      name = "slashDuration",
      aliases = {"sd"},
      description = "Number of iterations for the slash arc",
      defValue = "0"
   )
   final PlaceholderDuration slashDuration;
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
      name = "slashRadius",
      aliases = {"sr", "srad"},
      description = "The hit radius for slash hits",
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
   final PlaceholderString pointSkillName;
   @MythicField(
      name = "onHitEntitySkill",
      aliases = {"onhitentity", "ohe"},
      description = "Skill triggered when it hits an entity",
      defValue = ""
   )
   final PlaceholderString hitEntitySkillName;
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
   final MythicLineConfig mlc;
   Optional<Skill> slashOnPointSkill;
   Optional<Skill> slashOnHitEntitySkill;

   public SlashProjectileMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.mlc = mlc;
      this.points = mlc.getPlaceholderInteger(new String[]{"points", "p"}, 32);
      this.width = mlc.getPlaceholderDouble(new String[]{"width", "w"}, 1.0);
      this.height = mlc.getPlaceholderDouble(new String[]{"height", "h"}, 1.0);
      this.radius = mlc.getPlaceholderDouble(new String[]{"radius", "sr", "srad"}, 1.0);
      this.angle = mlc.getPlaceholderDouble(new String[]{"angle", "a", "arc"}, 180.0);
      this.pitch = mlc.getPlaceholderDouble(new String[]{"pitch"}, 0.0);
      this.yaw = mlc.getPlaceholderDouble(new String[]{"yaw"}, 0.0);
      this.roll = mlc.getPlaceholderDouble(new String[]{"roll"}, 0.0);
      this.rotationString = mlc.getString(new String[]{"rotation", "rot"}, "0,0,0");
      this.matchCasterDirection = mlc.getBoolean(
         new String[]{"matchCasterDirection", "matchPlayerDirection", "matchDirection", "mcd", "mpd", "md", "direction"}, true
      );
      this.directionTowardsTarget = mlc.getBoolean(new String[]{"directionTowardsTarget", "dtt"}, false);
      this.specificStep = mlc.getPlaceholderInteger(new String[]{"specificStep", "ss"}, 0);
      this.slashDuration = mlc.getPlaceholderDuration(new String[]{"slashduration", "sd"}, DurationUnit.TICKS, "0");
      this.pointSkillName = mlc.getPlaceholderString(new String[]{"onpointskill", "onpoint", "op"}, "");
      this.hitEntitySkillName = mlc.getPlaceholderString(new String[]{"onHitEntitySkill", "onhitentity", "ohe"}, "");
      this.xOffset = mlc.getPlaceholderDouble(new String[]{"xoffset", "xo", "x"}, 0.0);
      this.yOffset = mlc.getPlaceholderDouble(new String[]{"yoffset", "yo", "y"}, 0.0);
      this.zOffset = mlc.getPlaceholderDouble(new String[]{"zoffset", "zo", "z"}, 0.0);
      this.targetxOffset = mlc.getPlaceholderDouble(new String[]{"targetxoffset", "txo", "tx"}, 0.0);
      this.targetyOffset = mlc.getPlaceholderDouble(new String[]{"targetyoffset", "tyo", "ty"}, 0.0);
      this.targetzOffset = mlc.getPlaceholderDouble(new String[]{"targetzoffset", "tzo", "tz"}, 0.0);
      this.forwardOffset = mlc.getPlaceholderDouble(new String[]{"forwardoffset", "foffset", "fo"}, 0.0);
      this.getManager().queueSecondPass(() -> {
         this.slashOnPointSkill = this.getManager().getSkill(this.pointSkillName.get());
         if (this.slashOnPointSkill.isEmpty() && !this.pointSkillName.get().equals("")) {
            MythicLogger.error("Could not find onPointSkill " + this.pointSkillName.get());
         }

         this.slashOnHitEntitySkill = this.getManager().getSkill(this.hitEntitySkillName.get());
         if (this.slashOnHitEntitySkill.isEmpty() && !this.hitEntitySkillName.get().equals("")) {
            MythicLogger.error("Could not find onHitEntitySkill " + this.hitEntitySkillName.get());
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      try {
         new SlashProjectileMechanic.SlashProjectileTracker(data, target);
         return SkillResult.SUCCESS;
      } catch (Exception e) {
         MythicLogger.error("An error occurred executing a Slash Projectile Mechanic", e);
         return SkillResult.ERROR;
      }
   }

   List<AbstractVector> getArcTargets(double width, double height, double parsedAngle, int parsedPoints) {
      List<AbstractVector> targets = new ArrayList<>();
      if (parsedPoints <= 0) {
         return targets;
      } else if (parsedPoints == 1) {
         targets.add(new AbstractVector(0.0, 0.0, height));
         return targets;
      } else {
         double angleStep = parsedAngle / (parsedPoints - 1);
         double startingAngle = -parsedAngle / 2.0;

         for (int i = 0; i < parsedPoints; i++) {
            double currentAngle = Math.toRadians(startingAngle + i * angleStep);
            double x = width * Math.sin(currentAngle);
            double z = height * Math.cos(currentAngle);
            AbstractVector vector = new AbstractVector(x, 0.0, z);
            targets.add(vector);
         }

         return targets;
      }
   }

   List<AbstractLocation> buildLocations(SkillMetadata data, AbstractLocation base, AbstractLocation target, List<AbstractVector> vectors) {
      AbstractLocation pos = this.fromOrigin ? data.getOrigin() : base;
      AbstractVector rotation = this.parseRotation(this.rotationString, data);
      AbstractVector casterRotation;
      if (this.matchCasterDirection) {
         casterRotation = new AbstractVector(pos.getPitch(), pos.getYaw(), 0.0F);
      } else if (this.directionTowardsTarget) {
         AbstractLocation targetPos = target.add(this.targetxOffset.get(data), this.targetyOffset.get(data), this.targetzOffset.get(data));
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
      return vectors.stream()
         .map(AbstractVector::clone)
         .map(vector -> this.rotateAroundAxisX(vector, rotation.getX()))
         .map(vector -> this.rotateAroundAxisY(vector, rotation.getY()))
         .map(vector -> this.rotateAroundAxisZ(vector, rotation.getZ()))
         .map(vector -> this.rotateAroundAxisX(vector, casterRotation.getX()))
         .map(vector -> this.rotateAroundAxisY(vector, casterRotation.getY()))
         .map(vector -> vector.add(direction.clone().normalize().multiply(this.forwardOffset.get(data))))
         .map(vector -> pos.clone().add(vector))
         .map(location -> location.add(this.xOffset.get(data), this.yOffset.get(data), this.zOffset.get(data)))
         .collect(Collectors.toList());
   }

   void executePoint(AbstractLocation vector, SkillMetadata data, double stage, Set<AbstractEntity> seenEntities) {
      this.slashOnPointSkill.ifPresent(skill -> skill.execute(data.setOrigin(vector).setLocationTarget(vector)));
      this.slashOnHitEntitySkill.ifPresent(skill -> {
         if (skill.isUsable(data)) {
            Collection<AbstractEntity> entities = this.getEntitiesNearPoint(data, this.radius.get(data), vector, seenEntities);
            if (entities.isEmpty()) {
               return;
            }

            SkillMetadata meta = data.deepCloneNoTargets();
            meta.setEntityTargets(entities);
            meta.setOrigin(vector);
            skill.execute(meta);
         }
      });
   }

   AbstractVector parseRotation(String rotationString, SkillMetadata data) {
      String[] split = rotationString.split(",");
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (split.length == 3) {
         try {
            x = PlaceholderDouble.of(split[0]).get(data);
            y = PlaceholderDouble.of(split[1]).get(data);
            z = PlaceholderDouble.of(split[2]).get(data);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
         }
      } else {
         MythicLogger.error("Invalid format for " + rotationString + " in " + this.mlc.getLine());
      }

      double p = this.pitch.get(data);
      if (p != 0.0) {
         x = p;
      }

      double yv = this.yaw.get(data);
      if (yv != 0.0) {
         y = yv;
      }

      double r = this.roll.get(data);
      if (r != 0.0) {
         z = r;
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

   private boolean isZeroOrInvalid(AbstractVector vector) {
      if (vector == null) {
         return true;
      } else {
         double x = vector.getX();
         double y = vector.getY();
         double z = vector.getZ();
         return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ? x == 0.0 && y == 0.0 && z == 0.0 : true;
      }
   }

   Collection<AbstractEntity> getEntitiesNearPoint(SkillMetadata data, double radius, AbstractLocation vector, Set<AbstractEntity> seenEntities) {
      Collection<AbstractEntity> targets = Lists.newArrayList();

      for (AbstractEntity p : this.getPlugin().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(vector, radius)) {
         if (!seenEntities.contains(p)
            && !p.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())
            && this.getBoundingBox(vector, radius).overlaps(p.getBukkitEntity().getBoundingBox())
            && this.entityPassesConditions(data, p)) {
            seenEntities.add(p);
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

   public static List<List<Integer>> splitIndexList(int size, int n) {
      List<List<Integer>> parts = new ArrayList<>();
      if (n > 0 && size > 0) {
         int partSize = size / n;
         int remainder = size % n;
         int index = 0;

         for (int i = 0; i < n; i++) {
            int thisPartSize = partSize + (i < remainder ? 1 : 0);
            List<Integer> part = new ArrayList<>(thisPartSize);

            for (int j = 0; j < thisPartSize; j++) {
               part.add(index++);
            }

            parts.add(part);
         }

         return parts;
      } else {
         return parts;
      }
   }

   public BoundingBox getBoundingBox(AbstractLocation location, double radius) {
      return BoundingBox.of(BukkitAdapter.adapt(location), radius, radius, radius);
   }

   public class SlashProjectileTracker extends Projectile.ProjectileTracker {
      private final AbstractEntity targetEntity;
      private AbstractLocation targetLocation;
      private List<AbstractVector> baseVectors;
      private List<List<Integer>> partitions;
      private int totalPoints;
      private int iterationIndex;
      private boolean slashFinished;
      final HashSet<AbstractEntity> seenEntities = new HashSet<>();

      public SlashProjectileTracker(SkillMetadata data, AbstractLocation target) {
         super(data, target);
         this.targetEntity = data.getEntityTargets() != null && data.getEntityTargets().size() == 1 ? data.getEntityTargets().iterator().next() : null;
         this.targetLocation = target;
         this.iterationIndex = 0;
         this.slashFinished = false;
         this.start();
      }

      @Override
      public void projectileStart() {
         if (SlashProjectileMechanic.this.sourceIsOrigin) {
            this.startLocation = this.data.getOrigin().clone();
         } else {
            this.startLocation = this.data.getCaster().getEntity().getLocation().clone();
         }

         this.velocityMagnitude = SlashProjectileMechanic.this.projectileVelocity.get(this.data) / this.ticksPerSecond;
         if (SlashProjectileMechanic.this.tickInterpolation > 0) {
            this.velocityMagnitude = this.velocityMagnitude / (SlashProjectileMechanic.this.tickInterpolation + 1);
         }

         if (SlashProjectileMechanic.this.powerAffectsVelocity) {
            this.velocityMagnitude = this.velocityMagnitude * this.power;
         }

         double syo = SlashProjectileMechanic.this.startYOffset.get(this.data);
         if (syo != 0.0) {
            this.startLocation.setY(this.startLocation.getY() + syo);
         }

         double sfo = SlashProjectileMechanic.this.startForwardOffset.get(this.data) * -1.0F;
         if (sfo != 0.0) {
            this.startLocation = MythicUtil.move(SlashProjectileMechanic.this.faulty, this.startLocation, sfo, 0.0, 0.0);
         }

         double sso = SlashProjectileMechanic.this.startSideOffset.get(this.data);
         if (sso != 0.0) {
            this.startLocation = MythicUtil.move(SlashProjectileMechanic.this.faulty, this.startLocation, 0.0, 0.0, sso);
         }

         double eso = SlashProjectileMechanic.this.endSideOffset.get(this.data);
         if (eso != 0.0) {
            this.targetLocation.setDirection(this.startLocation.getDirection());
            this.targetLocation = MythicUtil.move(SlashProjectileMechanic.this.faulty, this.targetLocation, 0.0, 0.0, eso);
         }

         this.previousLocation = this.startLocation.clone();
         this.currentLocation = this.startLocation.clone();
         if (SlashProjectileMechanic.this.projectileStartDirection.isPresent()) {
            AbstractLocation startVectorTarget = SlashProjectileMechanic.this.getPlugin()
               .getSkillManager()
               .getLocationTarget(SlashProjectileMechanic.this.projectileStartDirection.get(), this.data);
            this.currentVelocity = startVectorTarget.toVector().rotate(0.001F).subtract(this.currentLocation.toVector()).normalize();
         } else {
            this.currentVelocity = this.targetLocation.toVector().subtract(this.currentLocation.toVector()).normalize();
         }

         if (SlashProjectileMechanic.this.isZeroOrInvalid(this.currentVelocity)) {
            this.currentVelocity = this.startLocation.getDirection().normalize();
         }

         this.currentLocation.setDirection(this.currentVelocity);
         if (SlashProjectileMechanic.this.projectileVelocityHorizOffset != null || SlashProjectileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
            float noise = 0.0F;
            if (SlashProjectileMechanic.this.projectileVelocityHorizNoise > 0.0F) {
               noise = (float)(
                  SlashProjectileMechanic.this.projectileVelocityHorizNoiseBase
                     + Numbers.randomDouble() * SlashProjectileMechanic.this.projectileVelocityHorizNoise
               );
            }

            if (SlashProjectileMechanic.this.projectileVelocityHorizOffset != null) {
               noise = (float)(noise + SlashProjectileMechanic.this.projectileVelocityHorizOffset.get(this.data).getDegrees());
            }

            this.currentVelocity.rotate(noise);
         }

         if (SlashProjectileMechanic.this.projectileVelocityVertOffset != null || SlashProjectileMechanic.this.projectileVelocityVertNoise > 0.0F) {
            float noisex = 0.0F;
            if (SlashProjectileMechanic.this.projectileVelocityVertNoise > 0.0F) {
               noisex = (float)(
                  SlashProjectileMechanic.this.projectileVelocityVertNoiseBase
                     + Numbers.randomDouble() * SlashProjectileMechanic.this.projectileVelocityVertNoise
               );
            }

            if (SlashProjectileMechanic.this.projectileVelocityVertOffset != null) {
               noisex = (float)(noisex + SlashProjectileMechanic.this.projectileVelocityVertOffset.get(this.data).getSlope());
            }

            this.currentVelocity.add(new AbstractVector(0.0F, noisex, 0.0F)).normalize();
         }

         this.currentVelocity.multiply(this.velocityMagnitude);
         if (SlashProjectileMechanic.this.bullet != null) {
            this.bullet = SlashProjectileMechanic.this.bullet.create(this, this.targetEntity);
         }
      }

      @Override
      public void projectileMove() {
         this.previousLocation = this.currentLocation.clone();
         this.currentLocation.add(this.currentVelocity);
         this.clampToMaxRange();
         this.currentLocation.setDirection(this.currentVelocity);
         if (SlashProjectileMechanic.this.stopOnHitGround && !BlockUtil.isPathable(BukkitAdapter.adapt(this.currentLocation).getBlock(), this)) {
            if (!SlashProjectileMechanic.this.canStopAtLocation(this.data, this.currentLocation)) {
               return;
            }

            if (SlashProjectileMechanic.this.onHitBlockSkill.isPresent() && SlashProjectileMechanic.this.onHitBlockSkill.get().isUsable(this.data)) {
               SkillMetadata sData = this.data.deepClone();
               AbstractLocation location;
               if (SlashProjectileMechanic.this.bulletType.isPresent() && SlashProjectileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
                  location = this.previousLocation.clone();
               } else {
                  location = this.currentLocation.clone();
               }

               SlashProjectileMechanic.this.onHitBlockSkill.get().execute(sData.setOrigin(location).setLocationTarget(location));
            }

            this.currentLocation = this.previousLocation;
            this.terminate();
         }
      }

      @Override
      public void projectileTick() {
         if (SlashProjectileMechanic.this.onTickSkill.isPresent() && SlashProjectileMechanic.this.onTickSkill.get().isUsable(this.data)) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            AbstractLocation location;
            if (SlashProjectileMechanic.this.bulletType.isPresent() && SlashProjectileMechanic.this.bulletType.get() == ProjectileBulletType.ARROW) {
               location = this.previousLocation.clone();
            } else {
               location = this.currentLocation.clone();
            }

            List<AbstractLocation> targets = new ArrayList<>();
            targets.add(location);
            sData.setLocationTargets(targets);
            sData.setOrigin(location);
            SlashProjectileMechanic.this.onTickSkill.get().execute(sData);
         }

         this.executeSlashTick();
         this.evaluateTargetsInBB();
         if (!this.targets.isEmpty()) {
            this.doHit(this.targets.clone());
            ObjectIterator objectIterator = this.targets.iterator();

            while (objectIterator.hasNext()) {
               AbstractEntity target = (AbstractEntity)objectIterator.next();
               if (SlashProjectileMechanic.this.canStopAtEntity(this.data, target)) {
                  this.terminate();
                  break;
               }
            }
         }

         this.targets.clear();
      }

      private void executeSlashTick() {
         if (!this.slashFinished) {
            if (this.baseVectors == null) {
               this.initializeSlashVectors();
            }

            int iterations = SlashProjectileMechanic.this.slashDuration.get(this.data).getTicks();
            if (iterations <= 0) {
               if (this.iterationIndex > 0) {
                  this.slashFinished = true;
               } else {
                  this.executeSlashPartition(this.allIndices());
                  this.iterationIndex++;
                  this.slashFinished = true;
               }
            } else if (this.partitions != null && !this.partitions.isEmpty() && this.iterationIndex < this.partitions.size()) {
               this.executeSlashPartition(this.partitions.get(this.iterationIndex));
               this.iterationIndex++;
            } else {
               this.slashFinished = true;
            }
         }
      }

      private void initializeSlashVectors() {
         this.totalPoints = SlashProjectileMechanic.this.points.get(this.data);
         double parsedAngle = SlashProjectileMechanic.this.angle.get(this.data);
         this.baseVectors = SlashProjectileMechanic.this.getArcTargets(
            SlashProjectileMechanic.this.width.get(this.data), SlashProjectileMechanic.this.height.get(this.data), parsedAngle, this.totalPoints
         );
         int desiredStep = SlashProjectileMechanic.this.specificStep.get(this.data);
         if (desiredStep > 0 && desiredStep < this.baseVectors.size()) {
            AbstractVector wantedVector = this.baseVectors.get(desiredStep);
            this.baseVectors = List.of(wantedVector);
            this.totalPoints = 1;
         } else if (desiredStep > 0) {
            MythicLogger.error("specificStep out of range for slashprojectile: " + desiredStep);
         }

         int iterations = SlashProjectileMechanic.this.slashDuration.get(this.data).getTicks();
         if (iterations > 0) {
            this.partitions = SlashProjectileMechanic.splitIndexList(this.totalPoints, iterations);
         }
      }

      private List<Integer> allIndices() {
         List<Integer> indices = new ArrayList<>(this.totalPoints);

         for (int i = 0; i < this.totalPoints; i++) {
            indices.add(i);
         }

         return indices;
      }

      private void executeSlashPartition(List<Integer> indices) {
         if (indices != null && !indices.isEmpty()) {
            AbstractLocation base = this.currentLocation.clone();
            AbstractLocation target = this.resolveTargetLocation();
            List<AbstractLocation> locations = SlashProjectileMechanic.this.buildLocations(this.data, base, target, this.baseVectors);

            for (int index : indices) {
               if (index >= 0 && index < locations.size()) {
                  double stage = this.totalPoints <= 1 ? 0.0 : (double)index / (this.totalPoints - 1);
                  SlashProjectileMechanic.this.executePoint(locations.get(index), this.data, stage, this.seenEntities);
               }
            }
         }
      }

      private AbstractLocation resolveTargetLocation() {
         return this.targetEntity != null ? this.targetEntity.getLocation() : this.targetLocation.clone();
      }

      private void doHit(Collection<AbstractEntity> targets) {
         this.hasHitEntity = true;
         this.applyHitboxVariable(this.data, targets);
         if (SlashProjectileMechanic.this.onHitSkill.isPresent()) {
            SkillMetadata sData = this.data.deepCloneNoTargets();
            sData.setEntityTargets(targets);
            sData.setOrigin(this.currentLocation.clone());
            this.executeOnHit(targets, () -> {
               if (SlashProjectileMechanic.this.onHitSkill.get().isUsable(sData)) {
                  SlashProjectileMechanic.this.onHitSkill.get().execute(sData);
               }
            });
         }
      }
   }
}
