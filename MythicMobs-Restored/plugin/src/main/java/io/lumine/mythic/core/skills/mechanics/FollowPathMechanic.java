package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.serialize.Locus;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MythicMechanic(
   author = "Ashijin",
   name = "followPath",
   aliases = {"path"},
   description = "Follow a path to a location"
)
public class FollowPathMechanic extends Aura implements ITargetedEntitySkill {
   private List<FollowPathMechanic.PathTarget> points = Lists.newArrayList();
   private PlaceholderFloat speed;
   private PlaceholderDouble tolerance;
   private PlaceholderDouble timeoutDistance;
   private PlaceholderDuration timeoutTime;
   @MythicField(
      name = "onGoal",
      aliases = {"og", "then"},
      version = "4.14",
      description = "Skill to execute when the destination is reached"
   )
   protected Optional<Skill> onGoalSkill = Optional.empty();
   protected String onGoalSkillName;

   public FollowPathMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.duration = mlc.getPlaceholderDuration(
         new String[]{"ticks", "t", "duration", "d", "time", "t"}, DurationUnit.TICKS, String.valueOf(Integer.MAX_VALUE)
      );
      this.auraName = Optional.of(PlaceholderString.of("#pathing"));
      this.charges = PlaceholderInt.of("1");
      this.maxStacks = PlaceholderInt.of("1");
      this.mergeAll = true;
      this.timeoutDistance = mlc.getPlaceholderDouble(new String[]{"timeoutdistance", "td", "maxdistance", "md"}, (PlaceholderDouble)null);
      this.timeoutTime = mlc.getPlaceholderDuration(new String[]{"timeouttime", "tt"}, DurationUnit.MILLISECONDS, null);
      String pathString = mlc.getString(new String[]{"path", "p"}, null);
      if (pathString == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Path is required");
      } else {
         this.speed = mlc.getPlaceholderFloat(new String[]{"speed", "s"}, 1.0F);
         this.tolerance = mlc.getPlaceholderDouble(new String[]{"tolerance"}, 1.5);
         this.onGoalSkillName = mlc.getString(new String[]{"ongoalskill", "ongoal", "og", "then"});

         for (String point : this.splitPathString(pathString)) {
            if (point.startsWith("(")) {
               point = point.substring(1, point.length() - 1);
               this.points.add(new FollowPathMechanic.PathLocation(point));
            } else if (point.startsWith("@")) {
               this.points.add(new FollowPathMechanic.PathTargeter(point));
            } else {
               MythicLogger.errorMechanicConfig(this, mlc, "Invalid path point format: " + point);
            }
         }

         this.getManager().queueSecondPass(() -> {
            if (this.onGoalSkillName != null) {
               this.onGoalSkill = this.getManager().getSkill(file, this, this.onGoalSkillName);
            }
         });
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         new FollowPathMechanic.Tracker(data, target);
         return SkillResult.SUCCESS;
      }
   }

   private List<String> splitPathString(String pathString) {
      List<String> result = new ArrayList<>();
      int parenCount = 0;
      int curlyCount = 0;
      int squareCount = 0;
      StringBuilder segment = new StringBuilder();

      for (char ch : pathString.toCharArray()) {
         switch (ch) {
            case '(':
               parenCount++;
               break;
            case ')':
               parenCount--;
               break;
            case ',':
               if (parenCount == 0 && curlyCount == 0 && squareCount == 0) {
                  result.add(segment.toString().trim());
                  segment = new StringBuilder();
                  continue;
               }
               break;
            case '[':
               squareCount++;
               break;
            case ']':
               squareCount--;
               break;
            case '{':
               curlyCount++;
               break;
            case '}':
               curlyCount--;
         }

         segment.append(ch);
      }

      if (segment.length() > 0) {
         result.add(segment.toString().trim());
      }

      return result;
   }

   @Getter
   @ToString
   protected class PathLocation implements FollowPathMechanic.PathTarget {
      private final Locus locus;

      public PathLocation(String location) {
         this.locus = Locus.deserialize(location);
      }

      @Override
      public AbstractLocation getLocation(SkillMetadata metadata, AbstractEntity entity) {
         AbstractWorld world = entity.getLocation().getWorld();
         double x = this.locus.getX();
         double y = this.locus.getY();
         double z = this.locus.getZ();
         return new AbstractLocation(world, x, y, z);
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof FollowPathMechanic.PathLocation other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$locus = this.getLocus();
            Object other$locus = other.getLocus();
            return this$locus == null ? other$locus == null : this$locus.equals(other$locus);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof FollowPathMechanic.PathLocation;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $locus = this.getLocus();
         return result * 59 + ($locus == null ? 43 : $locus.hashCode());
      }
   }

   protected interface PathTarget {
      AbstractLocation getLocation(SkillMetadata skillMetadata, AbstractEntity abstractEntity);
   }

   @Getter
   @Setter
   @ToString
   protected class PathTargeter implements FollowPathMechanic.PathTarget {
      private SkillTargeter targeter;

      public PathTargeter(String targeter) {
         this.targeter = FollowPathMechanic.this.getPlugin().getSkillManager().getTargeter(targeter);
      }

      @Override
      public AbstractLocation getLocation(SkillMetadata metadata, AbstractEntity entity) {
         if (this.targeter == null) {
            MythicLogger.errorMechanicConfig(FollowPathMechanic.this, FollowPathMechanic.this.getConfig(), "Invalid targeter supplied");
            return null;
         } else {
            return FollowPathMechanic.this.getPlugin().getSkillManager().getLocationTarget(this.targeter, metadata);
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof FollowPathMechanic.PathTargeter other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$targeter = this.getTargeter();
            Object other$targeter = other.getTargeter();
            return this$targeter == null ? other$targeter == null : this$targeter.equals(other$targeter);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof FollowPathMechanic.PathTargeter;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $targeter = this.getTargeter();
         return result * 59 + ($targeter == null ? 43 : $targeter.hashCode());
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      private AbstractLocation currentTarget;
      private int currentPointIndex = 0;
      private double timeoutDistance = -1.0;
      private long timeoutTime = -1L;

      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         if (!FollowPathMechanic.this.points.isEmpty()) {
            this.currentTarget = FollowPathMechanic.this.points.get(0).getLocation(data, entity);
         }

         if (FollowPathMechanic.this.timeoutDistance != null) {
            this.timeoutDistance = Math.pow(FollowPathMechanic.this.timeoutDistance.get(data), 2.0);
         }

         if (FollowPathMechanic.this.timeoutTime != null) {
            this.timeoutTime = System.currentTimeMillis() + FollowPathMechanic.this.timeoutTime.get(data).getMilliseconds();
         }

         this.start();
      }

      @Override
      public void auraStart() {
         this.executeAuraSkill(FollowPathMechanic.this.onStartSkill, this.skillMetadata);
      }

      @Override
      public void auraTick() {
         if (this.currentTarget == null) {
            this.terminate();
         } else {
            AbstractLocation currentLocation = this.entity.get().getLocation();
            double distanceSquared = currentLocation.distanceSquared(this.currentTarget);
            double toleranceSquared = Math.pow(FollowPathMechanic.this.tolerance.get(this.skillMetadata), 2.0);
            if (distanceSquared <= toleranceSquared) {
               if (!this.next()) {
                  return;
               }
            } else if (this.timeoutDistance > 0.0 && distanceSquared > this.timeoutDistance) {
               this.entity.get().teleport(this.currentTarget);
               if (!this.next()) {
                  return;
               }
            } else if (this.timeoutTime > 0L && System.currentTimeMillis() > this.timeoutTime) {
               this.entity.get().teleport(this.currentTarget);
               if (!this.next()) {
                  return;
               }
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Pathing to {0}", this.currentTarget.toString());
            FollowPathMechanic.this.getPlugin()
               .getVolatileCodeHandler()
               .getAIHandler()
               .navigateToLocation(this.entity.get(), this.currentTarget, FollowPathMechanic.this.speed.get(this.skillMetadata));
            SkillMetadata meta = this.skillMetadata.deepClone();
            meta.setOrigin(this.entity.get().getLocation());
            this.executeTargetedAuraSkill(FollowPathMechanic.this.onTickSkill, meta, this.currentTarget);
         }
      }

      private boolean next() {
         this.currentPointIndex++;
         if (this.currentPointIndex < FollowPathMechanic.this.points.size()) {
            this.currentTarget = FollowPathMechanic.this.points.get(this.currentPointIndex).getLocation(this.skillMetadata, this.entity.get());
            if (FollowPathMechanic.this.timeoutTime != null) {
               this.timeoutTime = System.currentTimeMillis() + FollowPathMechanic.this.timeoutTime.get(this.getSkillMetadata()).getMilliseconds();
            }

            return true;
         } else {
            if (FollowPathMechanic.this.onGoalSkill.isPresent()) {
               this.executeAuraSkill(FollowPathMechanic.this.onGoalSkill, this.skillMetadata);
            }

            this.terminate();
            return false;
         }
      }
   }
}
