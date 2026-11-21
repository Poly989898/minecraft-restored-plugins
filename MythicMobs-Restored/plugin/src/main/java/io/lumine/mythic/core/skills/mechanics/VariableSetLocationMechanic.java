package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "variableSetLocation",
   aliases = {"setVariableLocation", "setVarLoc"},
   description = "Sets a variable to the given location"
)
public class VariableSetLocationMechanic extends VariableMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected Optional<SkillTargeter> value;

   public VariableSetLocationMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String strValue = mlc.getString(new String[]{"value", "val", "v", "amount", "a"}, "@self");
      this.value = Optional.ofNullable(this.parseSkillTargeter(strValue));
      if (this.value.isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Invalid targeter");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data, target);
      if (variables == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry");
         return SkillResult.INVALID_CONFIG;
      } else if (this.value.isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Invalid targeter");
         return SkillResult.ERROR;
      } else {
         AbstractLocation location = this.getPlugin().getSkillManager().getLocationTarget(this.value.get(), data);
         Variable var = new LocationVariable(location, this.duration);
         variables.put(this.key.get(data, target), var);
         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      VariableRegistry variables = this.getVariableManager().getRegistry(this.scope, data);
      if (variables == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry");
         return SkillResult.INVALID_CONFIG;
      } else if (this.value.isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Invalid targeter");
         return SkillResult.ERROR;
      } else {
         AbstractLocation location = this.getPlugin().getSkillManager().getLocationTarget(this.value.get(), data);
         Variable var = new LocationVariable(location, this.duration);
         variables.put(this.key.get(data, target), var);
         return SkillResult.SUCCESS;
      }
   }
}
