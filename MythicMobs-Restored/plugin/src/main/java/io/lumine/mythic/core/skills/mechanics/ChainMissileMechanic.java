package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@MythicMechanic(
   author = "Ashijin",
   name = "chainmissile",
   aliases = {"cmi"},
   description = "Shoots a chaining homing missile at the target"
)
public class ChainMissileMechanic extends MissileMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "bounces",
      aliases = {"b"},
      defValue = "2",
      version = "4.12",
      description = "How many times the chain should bounce"
   )
   protected PlaceholderInt bounces;
   @MythicField(
      name = "radius",
      aliases = {"r"},
      defValue = "5",
      version = "4.12",
      description = "How far the skill will bounce to a new target"
   )
   protected PlaceholderDouble bounceRadius;
   @MythicField(
      name = "returnToCaster",
      aliases = {"return", "rtc"},
      defValue = "false",
      version = "4.12",
      description = "If the missile should return to the caster"
   )
   protected boolean returnToCaster;
   protected ChainMechanic.BounceSelector bounceSelector;
   protected String bounceConditionString;
   @MythicField(
      name = "bounceConditions",
      aliases = {"conditions", "cond", "c"},
      defValue = "NONE",
      version = "4.8",
      description = "Conditions applied to the bounce target"
   )
   protected List<SkillCondition> bounceConditions = null;

   public ChainMissileMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "ChainMissile mechanic requires Mythic Premium");
      } else {
         this.bounces = mlc.getPlaceholderInteger(new String[]{"bounces", "b"}, 2);
         this.bounceRadius = mlc.getPlaceholderDouble(new String[]{"bounceradius", "bouncerange", "radius", "range", "r"}, 5.0);
         this.returnToCaster = mlc.getBoolean(new String[]{"returntocaster", "return", "rtc"}, false);
         if (this.returnToCaster) {
            this.hitSelf = true;
         }

         this.bounceConditionString = mlc.getString(new String[]{"bounceconditions", "conditions", "cond", "c"}, null);
         this.getPlugin().getSkillManager().queueSecondPass(() -> {
            if (this.bounceConditionString != null) {
               this.bounceConditions = this.getPlugin().getSkillManager().getConditions(this.bounceConditionString);
            }
         });
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, this.config, "ChainMissile mechanic requires Mythic Premium");
         return SkillResult.REQUIRES_PREMIUM;
      } else {
         try {
            new ChainMissileMechanic.ChainMissileTracker(data, target).start();
            return SkillResult.SUCCESS;
         } catch (Exception e) {
            MythicLogger.error("An error occurred executing a Missile Mechanic", e);
            return SkillResult.ERROR;
         }
      }
   }

   @Override
   protected boolean canStopAtEntity(SkillMetadata data, AbstractEntity target) {
      return false;
   }

   public class ChainMissileTracker extends MissileMechanic.MissileTracker {
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
      protected int bouncesRemaining;
      @Getter
      protected float power;
      protected boolean finalBounce = false;
      protected ObjectOpenHashSet<AbstractEntity> alreadyHit = new ObjectOpenHashSet();
      protected final ObjectOpenHashSet<AbstractEntity> bounceTargets = new ObjectOpenHashSet();

      public ChainMissileTracker(SkillMetadata data, AbstractEntity target) {
         super(data, target);
         this.currentBounce = data.getCaster().getEntity();
         this.currentBounceUUID = this.currentBounce.getUniqueId();
         this.currentOrigin = data.getCaster().getLocation();
         this.bouncesRemaining = ChainMissileMechanic.this.bounces.get(this.context);
         this.bounceRadius = ChainMissileMechanic.this.bounceRadius.get(this.context);
         this.bounceRadiusSq = Math.pow(this.bounceRadius, 2.0);
         this.evaluateBounceTargets();
         MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC,
            "++ ChainMissile fired by Entity {0}: skill = {1}",
            data.getCaster().getEntity().getName(),
            ChainMissileMechanic.this.line
         );
      }

      @Override
      public AbstractLocation getTarget() {
         if (this.target != null) {
            if (this.target.isDead() || !this.target.isValid()) {
               this.doBounce(this.target);
            }

            if (this.target != null) {
               return this.target.getLocation();
            }
         }

         return this.targetLocation.clone();
      }

      @Override
      public void doHit(Collection<AbstractEntity> targets) {
         this.hasHitEntity = true;
         this.applyHitboxVariable(this.data, targets);
         AbstractEntity hit = null;
         Iterator vars = targets.iterator();
         if (vars.hasNext()) {
            AbstractEntity t = (AbstractEntity)vars.next();
            hit = t;
         }

         if (hit != null) {
            this.alreadyHit.add(hit);
            if (ChainMissileMechanic.this.onHitSkill.isPresent()) {
               this.context = this.context.toBuilder().meta(this.context.skillMetadata().deepCloneNoTargets()).build();
               this.data = this.context.skillMetadata();
               this.context.skillMetadata().setEntityTargets(targets);
               this.context.skillMetadata().setOrigin(this.currentLocation.clone());
               if (ChainMissileMechanic.this.onHitSkill.get().isUsable(this.data)) {
                  VariableRegistry varsx = this.data.getVariables();
                  varsx.putInt("chain-bounces", this.bouncesRemaining);
                  ChainMissileMechanic.this.onHitSkill.get().execute(this.data);
               }
            }

            if (hit.equals(this.target)) {
               if (this.finalBounce) {
                  this.terminate();
               } else {
                  this.doBounce(hit);
               }
            }
         }
      }

      private void doBounce(AbstractEntity hit) {
         this.currentBounce = hit;
         this.currentBounceUUID = hit.getUniqueId();
         this.bouncesRemaining--;
         if (this.bouncesRemaining == 0) {
            if (ChainMissileMechanic.this.returnToCaster && !this.target.equals(this.data.getCaster().getEntity())) {
               this.target = this.data.getCaster().getEntity();
               this.finalBounce = true;
            } else {
               this.terminate();
            }
         } else if (this.bouncesRemaining < 0) {
            this.terminate();
         } else {
            this.target = this.getNextTarget();
         }
      }

      private void evaluateBounceTargets() {
         double maxDistance = Math.pow(this.bounceRadius * this.bouncesRemaining * 2.0, 2.0);
         if (ChainMissileMechanic.this.hitSelf || ChainMissileMechanic.this.hitPlayers || ChainMissileMechanic.this.hitNonPlayers) {
            this.bounceTargets.addAll(this.data.getCaster().getLocation().getWorld().getLivingEntities());
            this.bounceTargets
               .removeIf(
                  e -> {
                     if (e == null) {
                        return true;
                     } else if (this.bullet != null && this.bullet.equals(e)) {
                        return true;
                     } else if (this.currentOrigin.distanceSquared(e.getLocation()) > maxDistance
                        && !e.getUniqueId().equals(this.data.getCaster().getEntity().getUniqueId())) {
                        return true;
                     } else if (e.isLiving() && !e.isCitizensNPC()) {
                        if (!ChainMissileMechanic.this.hitSelf && e.getUniqueId().equals(this.data.getCaster().getEntity().getUniqueId())) {
                           return true;
                        } else if (!ChainMissileMechanic.this.hitPlayers && e.isPlayer()) {
                           return true;
                        } else {
                           return !ChainMissileMechanic.this.hitNonPlayers && !e.isPlayer() ? true : this.alreadyHit.contains(e);
                        }
                     } else {
                        return true;
                     }
                  }
               );
            if (ChainMissileMechanic.this.returnToCaster) {
               this.bounceTargets.add(this.data.getCaster().getEntity());
            }
         }
      }

      private AbstractEntity getNextTarget() {
         if (this.currentBounce == null) {
            return null;
         } else {
            AbstractEntity target = null;
            double range = 0.0;
            ObjectIterator objectIterator = this.bounceTargets.iterator();

            while (true) {
               AbstractEntity entity;
               double nextRange;
               while (true) {
                  if (!objectIterator.hasNext()) {
                     if (target == null) {
                        if (!ChainMissileMechanic.this.returnToCaster) {
                           this.terminate();
                           return null;
                        }

                        target = this.data.getCaster().getEntity();
                        this.finalBounce = true;
                     }

                     return target;
                  }

                  entity = (AbstractEntity)objectIterator.next();
                  if (entity != null
                     && !entity.isDead()
                     && entity.isValid()
                     && !this.alreadyHit.contains(entity)
                     && (!ChainMissileMechanic.this.returnToCaster || !entity.equals(this.data.getCaster().getEntity()))) {
                     AbstractLocation origin = this.currentBounce != null ? this.currentBounce.getLocation() : this.currentOrigin;
                     nextRange = origin.distanceSquared(entity.getLocation());
                     if (!(nextRange > this.bounceRadiusSq)) {
                        if (ChainMissileMechanic.this.bounceConditions == null) {
                           break;
                        }

                        boolean ok = true;

                        for (SkillCondition condition : ChainMissileMechanic.this.bounceConditions) {
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
   }
}
