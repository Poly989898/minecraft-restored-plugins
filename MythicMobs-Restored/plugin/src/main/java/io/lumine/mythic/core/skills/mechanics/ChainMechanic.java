package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.event.entity.EntityDeathEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "chain",
   version = "4.8",
   description = "Casts a metaskill that bounces between targets"
)
public class ChainMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected Skill onBounceSkill = null;
   @MythicField(
      name = "onBounce",
      aliases = {"ob"},
      version = "4.8",
      description = "The skill that bounces between targets"
   )
   protected String onBounceSkillName;
   @MythicField(
      name = "bounces",
      aliases = {"b"},
      defValue = "2",
      version = "4.8",
      description = "How many times the chain should bounce"
   )
   protected PlaceholderInt bounces;
   @MythicField(
      name = "delay",
      aliases = {"d"},
      defValue = "1",
      version = "4.8",
      description = "The delay between bounces"
   )
   protected PlaceholderDuration bounceDelay;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      defValue = "5",
      version = "4.8",
      description = "How far the skill will bounce to a new target"
   )
   protected PlaceholderDouble bounceRadius;
   protected ChainMechanic.BounceSelector bounceSelector;
   @MythicField(
      name = "hitSelf",
      aliases = {"hs"},
      defValue = "false",
      version = "4.8",
      description = "Whether the chain should affect the caster"
   )
   protected boolean hitSelf = false;
   @MythicField(
      name = "hitTarget",
      aliases = {"ht"},
      defValue = "true",
      version = "4.8",
      description = "Whether the chain should do the initial from the caster to the first target"
   )
   protected boolean hitTarget = true;
   @MythicField(
      name = "hitPlayers",
      aliases = {"hp"},
      defValue = "true",
      version = "4.8",
      description = "Whether the chain should bounce to players"
   )
   protected boolean hitPlayers = true;
   @MythicField(
      name = "hitNonPlayers",
      aliases = {"hnp"},
      defValue = "false",
      version = "4.8",
      description = "Whether the chain should bounce to non-players"
   )
   protected boolean hitNonPlayers = false;
   protected String bounceConditionString;
   @MythicField(
      name = "bounceConditions",
      aliases = {"conditions", "cond", "c"},
      defValue = "NONE",
      version = "4.8",
      description = "Conditions applied to the bounce target"
   )
   protected List<SkillCondition> bounceConditions = null;

   public ChainMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onBounceSkillName = mlc.getString(new String[]{"onbounceskill", "onbounce", "ob", "ontickskill", "ontick", "ot", "skill", "s", "meta", "m"});
      this.bounces = mlc.getPlaceholderInteger(new String[]{"bounces", "b"}, 2);
      this.bounceDelay = mlc.getPlaceholderDuration(new String[]{"bouncedelay", "bd", "delay", "interval", "i", "d"}, DurationUnit.TICKS, "1");
      this.bounceRadius = mlc.getPlaceholderDouble(new String[]{"bounceradius", "bouncerange", "radius", "range", "r"}, 5.0);
      this.hitSelf = mlc.getBoolean(new String[]{"hitself", "hs"}, false);
      this.hitPlayers = mlc.getBoolean(new String[]{"hitplayers", "hp"}, true);
      this.hitNonPlayers = mlc.getBoolean(new String[]{"hitnonplayers", "hnp"}, false);
      this.hitTarget = mlc.getBoolean(new String[]{"hittarget", "ht"}, true);
      this.bounceConditionString = mlc.getString(new String[]{"bounceconditions", "conditions", "cond", "c"}, "null");
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (this.onBounceSkillName != null) {
            Optional<Skill> maybeSkill = this.getPlugin().getSkillManager().getSkill(file, this, this.onBounceSkillName);
            if (!maybeSkill.isPresent()) {
               MythicLogger.errorMechanicConfig(this, mlc, "onBounce skill is required and was not found");
               return;
            }

            this.onBounceSkill = maybeSkill.get();
            if (this.bounceConditionString != null) {
               this.bounceConditions = this.getPlugin().getSkillManager().getConditions(this.bounceConditionString);
            }
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new ChainMechanic.ChainTracker(data, target);
      return SkillResult.SUCCESS;
   }

   public static enum BounceSelector {
      RANDOM,
      CLOSEST,
      FURTHEST;
   }

   public class ChainTracker implements IParentSkill, Runnable, Terminable {
      protected final TerminableRegistry components = TerminableRegistry.create();
      protected SkillMetadata data;
      @Getter
      AbstractEntity currentBounce;
      @Getter
      UUID currentBounceUUID;
      @Getter
      AbstractLocation currentOrigin;
      @Getter
      protected final double bounceRadius;
      @Getter
      protected final double bounceRadiusSq;
      @Getter
      protected final int bounceDelay;
      @Getter
      protected int bouncesRemaining;
      @Getter
      protected float power;
      protected ObjectOpenHashSet<AbstractEntity> potentialTargets = new ObjectOpenHashSet();

      public ChainTracker(SkillMetadata data, AbstractEntity target) {
         this.data = data;
         this.data.setCallingEvent(this);
         this.power = data.getPower();
         this.currentBounce = data.getCaster().getEntity();
         this.currentBounceUUID = this.currentBounce.getUniqueId();
         this.currentOrigin = data.getCaster().getLocation();
         this.bouncesRemaining = ChainMechanic.this.bounces.get(data);
         this.bounceRadius = ChainMechanic.this.bounceRadius.get(data);
         this.bounceRadiusSq = Math.pow(this.bounceRadius, 2.0);
         this.bounceDelay = ChainMechanic.this.bounceDelay.get(data).getTicks();
         MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC, "++ Chain fired by Entity {0}: skill = {1}", data.getCaster().getEntity().getName(), ChainMechanic.this.line
         );
         this.start(target);
      }

      public void start(AbstractEntity target) {
         this.evaluatePotentialTargets();
         this.components
            .accept(Events.subscribe(EntityDeathEvent.class).filter(event -> event.getEntity().getUniqueId().equals(this.currentBounceUUID)).handler(event -> {
               this.currentOrigin = this.currentBounce.getLocation();
               this.currentBounce = null;
            }));
         if (ChainMechanic.this.hitTarget) {
            this.executeChainSkill(target);
         } else {
            this.potentialTargets.remove(target);
            this.currentBounce = target;
            this.currentBounceUUID = target.getUniqueId();
            this.currentOrigin = target.getLocation();
         }

         if (this.potentialTargets.size() > 0) {
            this.components.accept(Schedulers.sync().runRepeating(this, (long)this.bounceDelay, (long)this.bounceDelay));
         } else {
            this.terminate();
         }
      }

      public boolean executeChainSkill(AbstractEntity target) {
         if (ChainMechanic.this.onBounceSkill != null && target != null) {
            this.data = this.data.deepCloneNoTargets();
            this.data.setEntityTarget(target);
            this.data.setOrigin(this.currentOrigin);
            this.potentialTargets.remove(target);
            if (ChainMechanic.this.onBounceSkill.isUsable(this.data)) {
               VariableRegistry vars = this.data.getVariables();
               vars.putInt("chain-bounces", this.bouncesRemaining);
               ChainMechanic.this.onBounceSkill.execute(this.data);
               this.currentBounce = target;
               this.currentBounceUUID = target.getUniqueId();
               this.currentOrigin = target.getLocation();
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }

      @Override
      public void close() {
         if (!this.components.hasTerminated()) {
            this.potentialTargets.clear();
         }

         this.components.terminate();
      }

      @Override
      public void run() {
         AbstractEntity nextTarget = this.getNextTarget();
         if (nextTarget != null && this.executeChainSkill(nextTarget)) {
            if (--this.bouncesRemaining <= 0) {
               this.terminate();
            }
         } else {
            this.terminate();
         }
      }

      @Override
      public void setCancelled() {
         this.terminate();
      }

      @Override
      public boolean getCancelled() {
         return this.components.hasTerminated();
      }

      private void evaluatePotentialTargets() {
         double maxDistance = Math.pow(this.bounceRadius * this.bouncesRemaining * 2.0, 2.0);
         if (ChainMechanic.this.hitSelf || ChainMechanic.this.hitPlayers || ChainMechanic.this.hitNonPlayers) {
            this.potentialTargets.addAll(this.data.getCaster().getLocation().getWorld().getLivingEntities());
            this.potentialTargets.removeIf(e -> {
               if (e == null) {
                  return true;
               } else if (this.currentOrigin.distanceSquared(e.getLocation()) > maxDistance) {
                  return true;
               } else if (e.isLiving() && !e.isCitizensNPC()) {
                  if (!ChainMechanic.this.hitSelf && e.getUniqueId().equals(this.data.getCaster().getEntity().getUniqueId())) {
                     return true;
                  } else {
                     return !ChainMechanic.this.hitPlayers && e.isPlayer() ? true : !ChainMechanic.this.hitNonPlayers && !e.isPlayer();
                  }
               } else {
                  return true;
               }
            });
         }
      }

      private AbstractEntity getNextTarget() {
         if (this.currentBounce == null) {
            return null;
         } else {
            AbstractEntity target = null;
            double range = 0.0;
            ObjectIterator objectIterator = this.potentialTargets.iterator();

            while (true) {
               AbstractEntity entity;
               double nextRange;
               while (true) {
                  if (!objectIterator.hasNext()) {
                     return target;
                  }

                  entity = (AbstractEntity)objectIterator.next();
                  if (entity != null) {
                     AbstractLocation origin = this.currentBounce != null ? this.currentBounce.getLocation() : this.currentOrigin;
                     if (origin.getWorld().equals(entity.getLocation().getWorld())) {
                        nextRange = origin.distanceSquared(entity.getLocation());
                        if (!(nextRange > this.bounceRadiusSq)) {
                           if (ChainMechanic.this.bounceConditions == null) {
                              break;
                           }

                           boolean ok = true;

                           for (SkillCondition condition : ChainMechanic.this.bounceConditions) {
                              if (!condition.evaluateToEntity(this.currentBounce, entity)) {
                                 ok = false;
                                 break;
                              }
                           }

                           if (ok) {
                              break;
                           }
                        }
                     }
                  }
               }

               if (target == null) {
                  target = entity;
                  range = this.currentBounce.getLocation().distanceSquared(entity.getLocation());
               } else if (nextRange < range) {
                  target = entity;
                  range = nextRange;
               }
            }
         }
      }

      public void modifyPower(float p) {
         this.power *= p;
      }
   }
}
