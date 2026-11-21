package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.skills.variables.types.VectorVariable;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "variableLocation",
   aliases = {"varLocation"},
   description = "Targets the location stored in a variable"
)
public class VariableLocationTargeter extends ILocationSelector {
   protected String key;
   protected VariableScope scope = VariableScope.SKILL;

   public VariableLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.key = mlc.getString(new String[]{"name", "n", "variable", "var", "key", "k"}, null);
      if (this.key == null) {
         MythicLogger.errorTargeterConfig(this, mlc, "Variable name must be set.");
      } else {
         String prefixVar = VariableScope.SKILL.toString();
         if (this.key.contains(".")) {
            String[] split = this.key.split("\\.");
            prefixVar = split[0].toUpperCase();
            this.key = split[1];
         }

         String strScope = mlc.getString(new String[]{"scope", "s"}, prefixVar);

         try {
            this.scope = VariableScope.valueOf(strScope.toUpperCase());
         } catch (Exception e) {
            MythicLogger.errorTargeterConfig(this, mlc, "'" + strScope + "' is not a valid variable scope.");
         }
      }
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      Collection<AbstractLocation> targets = Lists.newArrayList();
      VariableRegistry registry = this.getPlugin().getVariableManager().getRegistry(this.scope, data, am.getEntity());
      Variable variable = registry.get(this.key);
      if (variable == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Variable {0} is not set in VariableLocation targeter", this.key);
         return targets;
      } else {
         if (variable instanceof LocationVariable locationVariable) {
            targets.add(this.mutate(data, locationVariable.getValue()));
         } else if (variable instanceof VectorVariable vectorVariable) {
            AbstractLocation location = vectorVariable.getValue().toLocation(am.getEntity().getWorld());
            targets.add(this.mutate(data, location));
         } else {
            MythicLogger.errorTargeterConfig(this, this.config, "VariableLocation var '" + this.key + "' is not a valid variable type.");
         }

         return targets;
      }
   }
}
