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
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScopedName;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Lxlp",
   name = "variableMove",
   aliases = {"moveVariable", "moveVar", "varMove"},
   description = "Moves an already created variable across names and/or registries "
)
public class VariableMoveMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private final VariableScopedName from;
   private final VariableScopedName to;
   private final boolean createNew;
   private final boolean inheritExpirationTime;
   private final boolean removeOld;

   public VariableMoveMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.from = mlc.getVariableScopedName(new String[]{"from"}, null);
      this.to = mlc.getVariableScopedName(new String[]{"to"}, null);
      this.createNew = mlc.getBoolean(new String[]{"createNew"}, false);
      this.removeOld = mlc.getBoolean(new String[]{"removeOld"}, false);
      this.inheritExpirationTime = mlc.getBoolean(new String[]{"inheritExpirationTime"}, true);
      if (this.from == null || this.to == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "Both \"from\" and \"to\" attributes have to be defined");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
      return this.castLogic(context);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
      return this.castLogic(context);
   }

   public SkillResult castLogic(PlaceholderContext context) {
      if (this.from != null && this.to != null) {
         VariableRegistry fromRegistry = this.getPlugin().getVariableManager().getRegistry(this.from.scope(), context);
         if (fromRegistry == null) {
            return SkillResult.INVALID_TARGET;
         } else {
            VariableRegistry toRegistry = this.getPlugin().getVariableManager().getRegistry(this.to.scope(), context);
            if (toRegistry == null) {
               return SkillResult.INVALID_TARGET;
            } else {
               String fromName = this.from.name().get(context);
               Variable fromVariable = fromRegistry.get(fromName);
               if (fromVariable == null) {
                  return SkillResult.ERROR;
               } else {
                  if (this.removeOld) {
                     fromRegistry.remove(fromName);
                  }

                  if (this.createNew) {
                     Variable newVariable;
                     if (this.inheritExpirationTime) {
                        newVariable = Variable.ofType(fromVariable.getType(), fromVariable.getValueClone(), fromVariable.getExpirationTime());
                     } else {
                        newVariable = Variable.ofType(fromVariable.getType(), fromVariable.getValueClone());
                     }

                     toRegistry.put(this.to.name().get(context), newVariable);
                  } else {
                     toRegistry.put(this.to.name().get(context), fromVariable);
                  }

                  return SkillResult.SUCCESS;
               }
            }
         }
      } else {
         return SkillResult.INVALID_CONFIG;
      }
   }
}
