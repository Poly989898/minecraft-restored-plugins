package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.IEntityLocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaLocationComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.conditions.ConditionAction;
import io.lumine.mythic.core.skills.conditions.CustomCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderParser;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class SkillCondition implements ISkillCondition {
   @Getter
   protected Optional<SkillHolder> parent = Optional.empty();
   @Getter
   protected String line;
   @Getter @Setter
   protected String conditionVar = "0";
   @Getter
   protected ConditionAction ACTION = ConditionAction.REQUIRED;
   @Getter @Setter
   protected PlaceholderString actionVar = null;
   @Nullable
   private String actionSkillName = null;
   @Nullable
   private volatile Optional<Skill> actionSkill = null;
   @Getter
   protected ThreadSafetyLevel threadSafetyLevel = ThreadSafetyLevel.EITHER;
   @Getter
   protected Optional<Skill> onFailSkill = Optional.empty();
   @Getter
   protected Optional<Skill> onPassSkill = Optional.empty();
   protected volatile ISkillCondition dispatchOverride = null;
   private String typeName = null;

   public static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   public SkillCondition(String line) {
      MythicLineConfigImpl mlc = new MythicLineConfigImpl(line);
      this.line = line;
      String onFailSkillName = mlc.getString(new String[]{"onfailskill", "onfail"});
      getPlugin().getSkillManager().queueSecondPass(() -> {
         if (onFailSkillName != null) {
            this.onFailSkill = getPlugin().getSkillManager().getSkill(null, null, onFailSkillName);
         }
      });
      String onPassSkillName = mlc.getString(new String[]{"onpassskill", "onpass"});
      getPlugin().getSkillManager().queueSecondPass(() -> {
         if (onPassSkillName != null) {
            this.onPassSkill = getPlugin().getSkillManager().getSkill(null, null, onPassSkillName);
         }
      });
      List<String> splitList = MythicUtil.splitIgnoringParentheses(line);
      if (splitList.size() >= 2) {
         String actionValue = null;
         if (ConditionAction.isAction(splitList.get(1))) {
            this.ACTION = ConditionAction.get(splitList.get(1));
            if (splitList.size() > 2) {
               actionValue = parseActionVariable(splitList, 2);
            }
         } else {
            this.conditionVar = splitList.get(1);
            if (splitList.size() > 2 && ConditionAction.isAction(splitList.get(2))) {
               this.ACTION = ConditionAction.get(splitList.get(2));
               if (splitList.size() > 3) {
                  actionValue = parseActionVariable(splitList, 3);
               }
            }
         }

         if (actionValue != null) {
            this.actionVar = PlaceholderString.of(actionValue);
            boolean castAction = this.ACTION == ConditionAction.CAST || this.ACTION == ConditionAction.CASTINSTEAD || this.ACTION == ConditionAction.ORELSECAST;
            boolean inlineBlock = actionValue.startsWith("[");
            if (castAction && (inlineBlock || this.actionVar instanceof PlaceholderParser parser && parser.isStatic())) {
               String skillName = actionValue;
               this.actionSkillName = skillName;
               getPlugin().getSkillManager().queueSecondPass(() -> this.actionSkill = getPlugin().getSkillManager().getSkill(null, null, skillName));
            }
         }
      }
   }

   static String parseActionVariable(List<String> splitList, int index) {
      String value = splitList.get(index);
      return value.startsWith("[") && !value.endsWith("]") && splitList.get(splitList.size() - 1).endsWith("]")
         ? String.join(" ", splitList.subList(index, splitList.size()))
         : value;
   }

   private Optional<Skill> getActionSkill(SkillMetadata meta) {
      Optional<Skill> resolved = this.actionSkill;
      if (resolved != null) {
         return resolved;
      } else {
         return this.actionSkillName != null
            ? MythicBukkit.inst().getSkillManager().getSkill(this.actionSkillName)
            : MythicBukkit.inst().getSkillManager().getSkill(this.actionVar.get(meta));
      }
   }

   public String getTypeName() {
      if (this.typeName == null) {
         this.typeName = this.buildTypeName();
      }

      return this.typeName;
   }

   private String buildTypeName() {
      Class<? extends SkillCondition> clazz = (Class<? extends SkillCondition>)this.getClass();
      return clazz.isAnnotationPresent(MythicCondition.class) ? clazz.getAnnotation(MythicCondition.class).name() : clazz.getSimpleName();
   }

   public void setParent(SkillHolder parent) {
      this.parent = Optional.ofNullable(parent);
   }

   public boolean evaluateEntity(AbstractEntity t) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof IEntityCondition entityCondition) {
            return this.handleOutcome(entityCondition.check(t));
         } else if (condition instanceof ILocationCondition locationCondition) {
            return this.handleOutcome(locationCondition.check(t.getLocation()));
         } else {
            return condition instanceof ICasterCondition casterCondition
               ? this.handleOutcome(casterCondition.check(getPlugin().getSkillManager().getCaster(t)))
               : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   @Deprecated
   public boolean evaluateLocation(AbstractLocation location) {
      return this.evaluateLocation(null, location);
   }

   public boolean evaluateLocation(@Nullable SkillMetadata meta, AbstractLocation location) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (meta != null && condition instanceof ISkillMetaLocationComparisonCondition locationCondition) {
            return this.handleOutcome(locationCondition.check(meta, location));
         } else {
            return condition instanceof ILocationCondition locationCondition ? this.handleOutcome(locationCondition.check(location)) : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateToEntity(SkillMetadata data, AbstractEntity t) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof ISkillMetaComparisonCondition) {
            return this.handleOutcome(((ISkillMetaComparisonCondition)condition).check(data, t));
         } else if (condition instanceof IEntityComparisonCondition) {
            return this.handleOutcome(((IEntityComparisonCondition)condition).check(data.getCaster().getEntity(), t));
         } else if (condition instanceof ILocationComparisonCondition locationComparisonCondition) {
            return this.handleOutcome(locationComparisonCondition.check(data.getCaster().getEntity().getLocation(), t.getLocation()));
         } else if (this instanceof IEntityLocationComparisonCondition entityLocationComparisonCondition) {
            return this.handleOutcome(entityLocationComparisonCondition.check(data.getCaster().getEntity(), t.getLocation()));
         } else if (condition instanceof IEntityCondition entityCondition) {
            return this.handleOutcome(entityCondition.check(t));
         } else if (condition instanceof ILocationCondition locationCondition) {
            return this.handleOutcome(locationCondition.check(t.getLocation()));
         } else {
            return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
               ? this.handleOutcome(skillMetaLocationComparisonCondition.check(data, t.getLocation()))
               : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateToEntity(AbstractEntity base, AbstractEntity t) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof IEntityComparisonCondition) {
            return this.handleOutcome(((IEntityComparisonCondition)condition).check(base, t));
         } else if (condition instanceof ILocationComparisonCondition locationComparisonCondition) {
            return this.handleOutcome(locationComparisonCondition.check(base.getLocation(), t.getLocation()));
         } else if (this instanceof IEntityLocationComparisonCondition entityLocationComparisonCondition) {
            return this.handleOutcome(entityLocationComparisonCondition.check(base, t.getLocation()));
         } else if (condition instanceof IEntityCondition entityCondition) {
            return this.handleOutcome(entityCondition.check(t));
         } else {
            return condition instanceof ILocationCondition locationCondition ? this.handleOutcome(locationCondition.check(t.getLocation())) : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateToLocation(AbstractEntity base, AbstractLocation location) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof ILocationComparisonCondition locationComparisonCondition) {
            return this.handleOutcome(locationComparisonCondition.check(base.getLocation(), location));
         } else if (this instanceof IEntityLocationComparisonCondition entityLocationComparisonCondition) {
            return this.handleOutcome(entityLocationComparisonCondition.check(base, location));
         } else {
            return condition instanceof ILocationCondition locationCondition ? this.handleOutcome(locationCondition.check(location)) : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateCaster(SkillMetadata meta) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "InvalidCondition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof ISkillMetaComparisonCondition skillMetaComparisonCondition) {
            return this.handleOutcome(meta, skillMetaComparisonCondition.check(meta, meta.getCaster().getEntity()));
         } else if (condition instanceof ISkillMetaCondition metaCondition) {
            return this.handleOutcome(meta, metaCondition.check(meta));
         } else if (condition instanceof ICasterCondition casterCondition) {
            return this.handleOutcome(meta, casterCondition.check(meta.getCaster()));
         } else if (condition instanceof IEntityCondition entityCondition) {
            return this.handleOutcome(meta, entityCondition.check(meta.getCaster().getEntity()));
         } else if (condition instanceof ILocationCondition locationCondition) {
            return this.handleOutcome(meta, locationCondition.check(meta.getCaster().getLocation()));
         } else {
            return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
               ? this.handleOutcome(skillMetaLocationComparisonCondition.check(meta, meta.getCaster().getLocation()))
               : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateTrigger(SkillMetadata meta) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning true: invalid condition {0}", ((InvalidCondition)condition).getLine());
            return true;
         } else if (condition instanceof ISkillMetaCondition metaCondition && !(condition instanceof IEntityCondition)) {
            return this.handleOutcome(meta, metaCondition.check(meta));
         } else if (meta.getTrigger() == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Returning false: no trigger found");
            return false;
         } else if (condition instanceof IEntityCondition entityCondition) {
            return this.handleOutcome(meta, entityCondition.check(meta.getTrigger()));
         } else if (condition instanceof IEntityComparisonCondition entityComparisonCondition) {
            return this.handleOutcome(meta, entityComparisonCondition.check(meta.getCaster().getEntity(), meta.getTrigger()));
         } else if (condition instanceof ILocationCondition locationCondition) {
            return this.handleOutcome(meta, locationCondition.check(meta.getTrigger().getLocation()));
         } else if (condition instanceof ILocationComparisonCondition locationComparisonCondition) {
            return this.handleOutcome(meta, locationComparisonCondition.check(meta.getCaster().getLocation(), meta.getTrigger().getLocation()));
         } else if (condition instanceof IEntityLocationComparisonCondition entityLocationComparisonCondition) {
            return this.handleOutcome(meta, entityLocationComparisonCondition.check(meta.getCaster().getEntity(), meta.getTrigger().getLocation()));
         } else {
            return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
               ? this.handleOutcome(skillMetaLocationComparisonCondition.check(meta, meta.getTrigger().getLocation()))
               : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateTargets(SkillMetadata meta) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            return true;
         } else if (condition instanceof ISkillMetaCondition metaCondition
            && !(condition instanceof IEntityCondition)
            && !(condition instanceof ILocationCondition)
            && !(condition instanceof ISkillMetaComparisonCondition)
            && !(condition instanceof ISkillMetaLocationComparisonCondition)) {
            if (!this.handleOutcome(meta, metaCondition.check(meta))) {
               if (meta.getEntityTargets() != null) {
                  meta.setEntityTargets(new ArrayList<>());
               }

               if (meta.getLocationTargets() != null) {
                  meta.setLocationTargets(new ArrayList<>());
               }

               return false;
            } else {
               return meta.getEntityTargets() != null && !meta.getEntityTargets().isEmpty()
                  || meta.getLocationTargets() != null && !meta.getLocationTargets().isEmpty();
            }
         } else {
            if (meta.getEntityTargets() != null && meta.getEntityTargets().size() > 0) {
               ArrayList<AbstractEntity> targets = new ArrayList<>(meta.getEntityTargets());
               AbstractLocation casterLocation = condition instanceof ILocationComparisonCondition ? meta.getCaster().getLocation() : null;
               targets.removeIf(
                  target -> {
                     if (target == null || target.isDead() || !target.isValid()) {
                        return true;
                     } else if (condition instanceof ISkillMetaComparisonCondition metaComparisonCondition) {
                        return !this.handleOutcome(meta, metaComparisonCondition.check(meta, target));
                     } else if (condition instanceof IEntityCondition entityCondition) {
                        return !this.handleOutcome(meta, entityCondition.check(target));
                     } else if (condition instanceof IEntityComparisonCondition) {
                        return !this.handleOutcome(meta, ((IEntityComparisonCondition)condition).check(meta.getCaster().getEntity(), target));
                     } else if (condition instanceof ILocationCondition locationCondition) {
                        return !this.handleOutcome(meta, locationCondition.check(target.getLocation()));
                     } else if (condition instanceof ILocationComparisonCondition) {
                        return !this.handleOutcome(meta, ((ILocationComparisonCondition)condition).check(casterLocation, target.getLocation()));
                     } else if (this instanceof IEntityLocationComparisonCondition) {
                        return !this.handleOutcome(
                           meta, ((IEntityLocationComparisonCondition)condition).check(meta.getCaster().getEntity(), target.getLocation())
                        );
                     } else {
                        return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
                           ? !this.handleOutcome(skillMetaLocationComparisonCondition.check(meta, target.getLocation()))
                           : false;
                     }
                  }
               );
               meta.setEntityTargets(targets);
               if (targets.size() == 0) {
                  return false;
               }
            } else {
               if (meta.getLocationTargets() == null || meta.getLocationTargets().size() <= 0) {
                  return false;
               }

               ArrayList<AbstractLocation> targets = new ArrayList<>(meta.getLocationTargets());
               AbstractLocation casterLocation = condition instanceof ILocationComparisonCondition ? meta.getCaster().getLocation() : null;
               targets.removeIf(
                  target -> {
                     if (condition instanceof ILocationCondition locationCondition) {
                        return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
                           ? !this.handleOutcome(meta, skillMetaLocationComparisonCondition.check(meta, target))
                           : !this.handleOutcome(meta, locationCondition.check(target));
                     } else if (condition instanceof ILocationComparisonCondition) {
                        return !this.handleOutcome(meta, ((ILocationComparisonCondition)condition).check(casterLocation, target));
                     } else if (this instanceof IEntityLocationComparisonCondition) {
                        return !this.handleOutcome(meta, ((IEntityLocationComparisonCondition)condition).check(meta.getCaster().getEntity(), target));
                     } else {
                        return condition instanceof ISkillMetaLocationComparisonCondition skillMetaLocationComparisonCondition
                           ? !this.handleOutcome(skillMetaLocationComparisonCondition.check(meta, target))
                           : false;
                     }
                  }
               );
               meta.setLocationTargets(targets);
               if (targets.size() == 0) {
                  return false;
               }
            }

            return true;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateRandomSpawnPoint(RandomSpawnPoint rsp) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            return true;
         } else if (condition instanceof IEntityCondition entityCondition && rsp.getEntity() != null) {
            return this.handleOutcome(rsp, entityCondition.check(rsp.getEntity()));
         } else {
            return condition instanceof ILocationCondition locationCondition && rsp.getLocation() != null
               ? this.handleOutcome(rsp, locationCondition.check(rsp.getLocation()))
               : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateSpawner(MythicSpawner spawner, AbstractLocation spawnLocation) {
      try {
         ISkillCondition condition = this.getDispatchTarget();
         if (condition instanceof InvalidCondition) {
            return true;
         } else if (condition instanceof ILocationComparisonCondition locationComparisonCondition) {
            return this.handleOutcome(spawner, locationComparisonCondition.check(spawner.getLocation(), spawnLocation));
         } else {
            return condition instanceof ILocationCondition locationCondition ? this.handleOutcome(spawner, locationCondition.check(spawnLocation)) : false;
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateDropper(DropMetadata meta) {
      try {
         if (!meta.getDropper().isPresent()) {
            return false;
         } else {
            SkillCaster dropper = meta.getDropper().get();
            ISkillCondition condition = this.getDispatchTarget();
            if (condition instanceof InvalidCondition) {
               return true;
            } else if (condition instanceof ICasterCondition) {
               return this.handleOutcome(meta, ((ICasterCondition)condition).check(dropper));
            } else if (condition instanceof IEntityCondition entityCondition) {
               return this.handleOutcome(meta, entityCondition.check(dropper.getEntity()));
            } else {
               return condition instanceof ILocationCondition locationCondition
                  ? this.handleOutcome(meta, locationCondition.check(dropper.getLocation()))
                  : false;
            }
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public boolean evaluateDropCause(DropMetadata meta) {
      try {
         if (!meta.getCause().isPresent()) {
            return false;
         } else {
            AbstractEntity cause = meta.getCause().get();
            ISkillCondition condition = this.getDispatchTarget();
            if (condition instanceof InvalidCondition) {
               return true;
            } else if (condition instanceof IEntityCondition entityCondition) {
               return this.handleOutcome(meta, entityCondition.check(cause));
            } else {
               return condition instanceof ILocationCondition locationCondition
                  ? this.handleOutcome(meta, locationCondition.check(cause.getLocation()))
                  : false;
            }
         }
      } catch (Error | Exception e) {
         MythicLogger.errorCondition(this, "An internal error occurred", e);
         return false;
      }
   }

   public ISkillCondition getDispatchTarget() {
      ISkillCondition override = this.dispatchOverride;
      if (override != null) {
         return override;
      } else {
         return (this instanceof CustomCondition customCondition && customCondition.getCondition().isPresent()
            ? customCondition.getCondition().get()
            : this);
      }
   }

   protected boolean handleOutcome(boolean outcome) {
      switch (this.ACTION) {
         case CANCEL:
            return !outcome;
         case REQUIRED:
            return outcome;
         default:
            return true;
      }
   }

   protected boolean handleOutcome(SkillMetadata meta, boolean outcome) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "? Condition {0} outcome {1} action {2}", this.getTypeName(), outcome, this.ACTION);
      switch (this.ACTION) {
         case CANCEL:
            if (outcome && this.actionVar != null && meta.getCaster().getEntity().isPlayer()) {
               meta.getCaster().getEntity().asPlayer().sendMessage(this.actionVar.get(meta));
            }

            return !outcome;
         case REQUIRED:
            if (!outcome && this.actionVar != null && meta.getCaster().getEntity().isPlayer()) {
               meta.getCaster().getEntity().asPlayer().sendMessage(this.actionVar.get(meta));
            }

            return outcome;
         case CAST:
            if (outcome) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "~ Outcome true, executing other cast");
               Optional<Skill> maybeSkill = this.getActionSkill(meta);
               if (maybeSkill.isPresent() && maybeSkill.get().isUsable(meta)) {
                  maybeSkill.get().execute(meta);
               }
            }
            break;
         case CASTINSTEAD:
            if (outcome) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "~ Outcome true, executing castInstead");
               Optional<Skill> maybeSkill = this.getActionSkill(meta);
               if (maybeSkill.isPresent() && maybeSkill.get().isUsable(meta)) {
                  maybeSkill.get().execute(meta);
               }

               return false;
            }
            break;
         case ORELSECAST:
            if (!outcome) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "~ Outcome false, executing orElseCast");
               Optional<Skill> maybeSkill = this.getActionSkill(meta);
               if (maybeSkill.isPresent() && maybeSkill.get().isUsable(meta)) {
                  maybeSkill.get().execute(meta);
               }

               return false;
            }
            break;
         case POWER:
            if (outcome) {
               try {
                  float powerMod = Float.valueOf(this.actionVar.get(meta));
                  meta.setPower(meta.getPower() * powerMod);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
      }

      return true;
   }

   protected boolean handleOutcome(RandomSpawnPoint rsp, boolean outcome) {
      switch (this.ACTION) {
         case CANCEL:
            return !outcome;
         case REQUIRED:
            return outcome;
         case LEVEL:
            try {
               float levelMod = Float.valueOf(this.actionVar.get());
               rsp.setLevelMod(levelMod);
            } catch (Exception e) {
               e.printStackTrace();
            }
         default:
            return true;
      }
   }

   protected boolean handleOutcome(DropMetadata meta, boolean outcome) {
      switch (this.ACTION) {
         case CANCEL:
            return !outcome;
         case REQUIRED:
            return outcome;
         default:
            return true;
      }
   }

   protected boolean handleOutcome(MythicSpawner spawner, boolean outcome) {
      switch (this.ACTION) {
         case CANCEL:
            return !outcome;
         case REQUIRED:
            return outcome;
         default:
            return true;
      }
   }

   public void setAction(ConditionAction action) {
      this.ACTION = action;
   }

   public String getActionVariable() {
      return this.actionVar.get();
   }
}
