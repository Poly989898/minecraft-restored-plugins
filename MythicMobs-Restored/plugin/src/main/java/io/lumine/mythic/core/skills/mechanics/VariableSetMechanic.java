package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableInfoRegistry;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "variableSet",
   aliases = {"setVariable", "setVar"},
   description = "Sets a variable on the target entity"
)
public class VariableSetMechanic extends VariableMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected PolymorphicPlaceholder value;
   protected VariableType type;

   public VariableSetMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.value = mlc.getPolymorphicPlaceholder(new String[]{"value", "val", "v", "amount", "a"}, null);
      String strType = mlc.getString(new String[]{"type", "t"}, VariableType.INTEGER.toString());

      try {
         this.type = VariableType.valueOf(strType.toUpperCase());
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "'" + strType + "' is not a valid variable type.");
      }

      if (this.value == null) {
         VariableInfo<?> variableInfo = VariableInfoRegistry.get(this.type);
         if (variableInfo.defaultValue() != null) {
            this.value = variableInfo.defaultValue();
         } else {
            MythicLogger.errorMechanicConfig(this, mlc, "Variable value must be set.");
         }
      }

      this.value.cache(this.type);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      VariableRegistry registry = this.getVariableManager().getRegistry(this.scope, data, target);
      if (registry == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry");
         return SkillResult.INVALID_CONFIG;
      } else {
         long expireTime = this.duration > 1L ? System.currentTimeMillis() + this.duration : this.duration;
         VariableInfo<?> info = VariableInfoRegistry.get(this.type);
         if (info == null) {
            MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable type information");
            return SkillResult.INVALID_CONFIG;
         } else if (info.polymorphicConstructor() != null) {
            PlaceholderContext context = PlaceholderContext.of(data, target, null, null, null);
            Variable var = info.polymorphicConstructor().create(this.value, context, expireTime);
            registry.put(this.key.get(context), var);
            return SkillResult.SUCCESS;
         } else {
            MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable type constructor");
            return SkillResult.INVALID_CONFIG;
         }
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (this.scope == VariableScope.TARGET) {
         return SkillResult.INVALID_CONFIG;
      } else {
         VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data);
         if (variables == null) {
            MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry");
            return SkillResult.INVALID_CONFIG;
         } else {
            long expireTime = this.duration > 1L ? System.currentTimeMillis() + this.duration : this.duration;
            VariableInfo<?> info = VariableInfoRegistry.get(this.type);
            if (info == null) {
               MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable type information");
               return SkillResult.INVALID_CONFIG;
            } else if (info.polymorphicConstructor() != null) {
               PlaceholderContext context = PlaceholderContext.of(data, null, target, null, null);
               Variable var = info.polymorphicConstructor().create(this.value, context, expireTime);
               variables.put(this.key.get(data, target), var);
               return SkillResult.SUCCESS;
            } else {
               MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable type constructor");
               return SkillResult.INVALID_CONFIG;
            }
         }
      }
   }
}
