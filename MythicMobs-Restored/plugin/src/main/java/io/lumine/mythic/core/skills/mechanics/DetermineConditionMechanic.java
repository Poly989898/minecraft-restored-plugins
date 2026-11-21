package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Lxlp",
   name = "determineCondition",
   aliases = {"detCond"},
   description = "Determines the outcome of a Metaskill that is used as a Condition via the `MetaskillCondition` condition"
)
public class DetermineConditionMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill, ITargetedLocationSkill {
   private final PlaceholderBoolean determination;
   private final DetermineConditionMechanic.DeterminationMode mode;

   public DetermineConditionMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.determination = mlc.getPlaceholderBoolean(new String[]{"determination", "det"}, true);
      this.mode = mlc.getEnum(new String[]{"mode"}, DetermineConditionMechanic.DeterminationMode.class, DetermineConditionMechanic.DeterminationMode.SET);
   }

   public Boolean getOldValue(SkillMetadata data) {
      Optional<Object> oldValue = data.getMetadata("condition-outcome");
      return oldValue.isPresent() ? oldValue.get() instanceof Boolean : null;
   }

   public SkillResult applyLogic(SkillMetadata data, boolean value) {
      Boolean oldValue = this.getOldValue(data);
      switch (this.mode) {
         case SET:
            data.setMetadata("condition-outcome", value);
            return SkillResult.SUCCESS;
         case AND:
            if (oldValue == null) {
               data.setMetadata("condition-outcome", value);
               return SkillResult.SUCCESS;
            }

            data.setMetadata("condition-outcome", value && oldValue);
            return SkillResult.SUCCESS;
         case OR:
            if (oldValue == null) {
               data.setMetadata("condition-outcome", value);
               return SkillResult.SUCCESS;
            }

            data.setMetadata("condition-outcome", value || oldValue);
            return SkillResult.SUCCESS;
         case NOT:
            data.setMetadata("condition-outcome", !value);
            return SkillResult.SUCCESS;
         default:
            data.setMetadata("condition-outcome", value);
            return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      boolean value = this.determination.get(data);
      return this.applyLogic(data, value);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      boolean value = this.determination.get(data, target);
      return this.applyLogic(data, value);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      boolean value = this.determination.get(data);
      return this.applyLogic(data, value);
   }

   static enum DeterminationMode {
      SET,
      AND,
      OR,
      NOT;
   }
}
