package io.lumine.mythic.core.mobs.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ai.Pathfinder;
import io.lumine.mythic.core.mobs.ai.PathfindingGoal;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.GenericPlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@MythicAIGoal(
   name = "goToLocationConditional",
   aliases = {"gotoConditional", "goToLocationCond", "gotoCond"},
   description = "Go to a location while the location and mob match the given conditions"
)
public class GoToLocationConditionalGoal extends Pathfinder implements PathfindingGoal {
   private static final Set<String> REPORTED_LINES = ConcurrentHashMap.newKeySet();
   private final PlaceholderString dynamicLocation;
   private final PlaceholderMeta placeholderMeta;
   private final float speed;
   private final Cooldown cooldown;
   private final double minRangeSquared;
   protected List<SkillCondition> locationConditions = null;
   protected List<SkillCondition> mobConditions = null;
   private AbstractLocation destination;
   private boolean destinationHasWorld;
   private String resolvedLocation;
   private boolean resolvedLocationInitialized;

   public GoToLocationConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.goalType = Pathfinder.GoalType.MOVE_LOOK;
      PlaceholderString configuredLocation = mlc.getPlaceholderString(new String[]{"location", "loc", "l"}, this.dataVar1);
      if (configuredLocation == null) {
         this.dynamicLocation = null;
         this.placeholderMeta = null;
         if (REPORTED_LINES.add(line)) {
            MythicLogger.error("AI goal goToLocationConditional requires a location: {0}", MythicLineConfigImpl.parseBlock(line));
         }
      } else if (!configuredLocation.isStatic() && containsPlaceholder(configuredLocation.serialize())) {
         this.dynamicLocation = configuredLocation;
         this.placeholderMeta = new GenericPlaceholderMeta(
            (SkillCaster)(this.activeMob != null ? this.activeMob : getPlugin().getSkillManager().getCaster(entity)), entity
         );
      } else {
         this.dynamicLocation = null;
         this.placeholderMeta = null;
         this.destination = this.parseLocation(configuredLocation.get());
         if (this.destination == null && REPORTED_LINES.add(line)) {
            MythicLogger.error(
               "AI goal goToLocationConditional could not resolve location {0}: {1}", configuredLocation.get(), MythicLineConfigImpl.parseBlock(line)
            );
         }
      }

      this.speed = mlc.getFloat(new String[]{"speed", "s"}, this.dataVar2 == null ? 1.0F : Float.parseFloat(this.dataVar2));
      this.cooldown = Cooldown.ofTicks(Math.max(0, mlc.getInteger(new String[]{"cooldown", "cd", "interval", "i"}, 5)));
      double minRange = Math.max(0.0, mlc.getDouble(new String[]{"minrange", "minr", "mr"}, 2.0));
      this.minRangeSquared = minRange * minRange;
      String locationConditionString = mlc.getString(new String[]{"locationconditions", "locconditions", "lconditions", "lcond", "lc"}, "null");
      if (locationConditionString != null) {
         this.locationConditions = getPlugin().getSkillManager().getConditions(locationConditionString);
      }

      String mobConditionString = mlc.getString(new String[]{"mobconditions", "mconditions", "mcond", "mc"}, "null");
      if (mobConditionString != null) {
         this.mobConditions = getPlugin().getSkillManager().getConditions(mobConditionString);
      }
   }

   @Override
   public boolean shouldStart() {
      return this.refreshDestination() && this.entity.getLocation().distanceSquared(this.destination) > this.minRangeSquared && this.evaluateConditions();
   }

   @Override
   public void start() {
   }

   @Override
   public void tick() {
      if (this.cooldown.test()) {
         ai().navigateToLocation(this.entity, this.destination, this.speed);
      }
   }

   @Override
   public boolean shouldEnd() {
      return !this.refreshDestination() || this.entity.getLocation().distanceSquared(this.destination) <= this.minRangeSquared || !this.evaluateConditions();
   }

   @Override
   public void end() {
      ai().clearPath(this.entity);
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }

   private boolean refreshDestination() {
      if (this.destination != null && !this.destinationHasWorld && !this.destination.getWorld().getUniqueId().equals(this.entity.getWorld().getUniqueId())) {
         this.destination = new AbstractLocation(this.entity.getWorld(), this.destination.getX(), this.destination.getY(), this.destination.getZ());
      }

      if (this.dynamicLocation != null) {
         String value = this.dynamicLocation.get(PlaceholderContext.builder().meta(this.placeholderMeta).entity(this.entity).build());
         if (!this.resolvedLocationInitialized || !Objects.equals(this.resolvedLocation, value)) {
            this.resolvedLocationInitialized = true;
            this.resolvedLocation = value;
            AbstractLocation resolved = this.parseLocation(value);
            if (!Objects.equals(this.destination, resolved)) {
               this.destination = resolved;
            }
         }
      }

      return this.destination != null && this.destination.getWorld().getUniqueId().equals(this.entity.getWorld().getUniqueId());
   }

   private AbstractLocation parseLocation(String value) {
      AbstractLocation resolved = null;
      this.destinationHasWorld = false;
      if (value != null && !value.isBlank()) {
         String[] parts = value.split(",");

         try {
            if (parts.length == 3) {
               resolved = new AbstractLocation(
                  this.entity.getWorld(), Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()), Double.parseDouble(parts[2].trim())
               );
            } else if (parts.length == 4) {
               resolved = new AbstractLocation(
                  parts[0].trim(), Double.parseDouble(parts[1].trim()), Double.parseDouble(parts[2].trim()), Double.parseDouble(parts[3].trim())
               );
               this.destinationHasWorld = true;
            }
         } catch (NumberFormatException e) {
         }
      }

      if (resolved != null && (resolved.getWorld() == null || !resolved.isFinite())) {
         resolved = null;
      }

      if (resolved == null) {
         MythicLogger.debug(
            MythicLogger.DebugLevel.INFO, "AI goal goToLocationConditional could not resolve location {0}", Objects.requireNonNullElse(value, "null")
         );
      }

      return resolved;
   }

   private static boolean containsPlaceholder(String text) {
      return text.indexOf(37) >= 0 || text.indexOf(60) >= 0 && text.indexOf(62) > text.indexOf(60);
   }

   private boolean evaluateConditions() {
      if (this.locationConditions != null) {
         for (SkillCondition condition : this.locationConditions) {
            if (!condition.evaluateToLocation(this.entity, this.destination)) {
               return false;
            }
         }
      }

      if (this.mobConditions != null) {
         for (SkillCondition conditionx : this.mobConditions) {
            if (!conditionx.evaluateEntity(this.entity)) {
               return false;
            }
         }
      }

      return true;
   }
}
