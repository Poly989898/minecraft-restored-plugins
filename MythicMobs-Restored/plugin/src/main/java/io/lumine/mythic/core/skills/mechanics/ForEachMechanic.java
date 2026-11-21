package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.IterationVariableRegistry;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@MythicMechanic(
   author = "Lxlp",
   name = "forEach",
   version = "5.10",
   description = "Runs a given metaskill for each element of the specified list"
)
public class ForEachMechanic extends MetaSkillMechanic implements IMetaSkill, SkillHolder {
   final PlaceholderString targetType;
   private static final Map<String, ForEachMechanic.TargetType> TARGETTYPE_TYPE_MAP = new HashMap<>();

   public ForEachMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.targetType = mlc.getPlaceholderString(new String[]{"targettype", "targett", "tt"}, "ALL");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.metaskill.isEmpty()) {
         return SkillResult.INVALID_CONFIG;
      } else {
         ForEachMechanic.TargetType targetType = ForEachMechanic.TargetType.getOperator(this.targetType.get(data));
         switch (targetType) {
            case ENTITY:
               this.castEntity(data);
               break;
            case LOCATION:
               this.castLocation(data);
               break;
            case ALL:
               this.castEntity(data);
               this.castLocation(data);
         }

         return SkillResult.SUCCESS;
      }
   }

   private void castEntity(SkillMetadata data) {
      if (data.hasEntityTargets()) {
         int index = 0;

         for (AbstractEntity entity : data.getEntityTargets()) {
            SkillMetadata newData = data.deepCloneNoTargets();
            newData.setVariables(IterationVariableRegistry.of(newData.getVariables(), "index", new IntegerVariable(index)));
            newData.setEntityTarget(entity);
            super.cast(newData);
            index++;
         }
      }
   }

   private void castLocation(SkillMetadata data) {
      if (data.hasLocationTargets()) {
         int index = 0;

         for (AbstractLocation location : data.getLocationTargets()) {
            SkillMetadata newData = data.deepCloneNoTargets();
            newData.setVariables(IterationVariableRegistry.of(newData.getVariables(), "index", new IntegerVariable(index)));
            newData.setLocationTarget(location);
            super.cast(newData);
            index++;
         }
      }
   }

   private static enum TargetType {
      ENTITY("ENTITY"),
      LOCATION("LOCATION"),
      ALL("ALL");

      private TargetType(String... aliases) {
         for (String alias : aliases) {
            ForEachMechanic.TARGETTYPE_TYPE_MAP.put(alias, this);
         }
      }

      public static ForEachMechanic.TargetType getOperator(String op) {
         return ForEachMechanic.TARGETTYPE_TYPE_MAP.getOrDefault(op.toUpperCase(), ALL);
      }
   }
}
