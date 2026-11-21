package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.MetaVariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;

@MythicMechanic(
   author = "Ashijin",
   name = "metavariableskill",
   aliases = {"variableskill", "vskill"},
   description = "Finds and executes a metaskill"
)
public class MetaVariableSkillMechanic extends MetaVariableMechanic implements IMetaSkill {
   @MythicField(
      name = "skill",
      aliases = {"s", "meta", "m", "mechanics", "$", "()"},
      description = "The meta-skill to execute"
   )
   protected PlaceholderString skillName;
   protected boolean branch = false;
   protected boolean executeAfterDeath = false;
   @Getter
   protected Map<String, PlaceholderString> parameters = Maps.newConcurrentMap();

   public MetaVariableSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.target_creative = true;
      this.skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "meta", "m", "mechanics", "$", "()"}, "");
      this.branch = mlc.getBoolean(new String[]{"branch", "b", "fork", "f"}, false);
      this.executeAfterDeath = mlc.getBoolean(new String[]{"executeafterdeath", "continueafterdeath"}, false);
      this.buildParameters(mlc);
   }

   @Override
   public SkillResult cast(SkillMetadata dataIn) {
      if (this.variableName != null) {
         VariableRegistry registry = this.getPlugin().getVariableManager().getRegistry(this.variableScope, dataIn);
         if (registry != null && registry.get(this.variableName.get(dataIn)) instanceof MetaSkillVariable msv) {
            return this.executeSkill(dataIn, msv.get());
         }
      }

      String skillName = this.skillName.get(dataIn);
      Optional<Skill> maybeSkill = this.getManager().getSkill(this.file, this, skillName);
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Looking for MetaVSkill {0}...", this.skillName);
      if (!maybeSkill.isPresent()) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MetaVSkill {0} was not found", this.skillName);
         return SkillResult.INVALID_CONFIG;
      } else {
         Skill ms = maybeSkill.get();
         return this.executeSkill(dataIn, ms);
      }
   }

   public SkillResult executeSkill(SkillMetadata dataIn, Skill ms) {
      if (this.branch) {
         dataIn = dataIn.deeperClone();
         dataIn.setTerminatedReference(new AtomicBoolean());
      } else {
         dataIn = dataIn.deepClone();
      }

      SkillMetadata data;
      if (!this.parameters.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Injecting {0} parameters", this.parameters.size());
         data = this.injectParameters(dataIn);
      } else {
         data = dataIn;
      }

      if (this.executeAfterDeath) {
         data.setExecuteAfterDeath(true);
      }

      if (!ms.isUsable(data)) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MetaSkill {0} is currently unusable", this.skillName);
         return SkillResult.CONDITION_FAILED;
      } else {
         if (this.getPlugin().getConfiguration().getDebugLevel() > 0) {
            if (data.getEntityTargets() != null) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaVSkill (et: {0}) -> {1}", data.getEntityTargets().size(), this.skillName);
            } else if (data.getLocationTargets() != null) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaVSkill (lt: {0}) -> {1}", data.getLocationTargets().size(), this.skillName);
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaVSkill (t: 0) -> {0}", this.skillName);
            }
         }

         if (this.forceSync && data.getIsAsync()) {
            data.setIsAsync(false);
            casterScheduler(data).run(bindVariablesForLaterRun(data, () -> {
               data.setIsAsync(false);
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Running Skill {0} Sync from MetaVSkillMechanic", this.skillName);
               ms.execute(data);
            }));
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Running Skill {0} Async from MetaSkillMechanic", this.skillName);
            ms.execute(data);
         }

         return SkillResult.SUCCESS;
      }
   }

   private void buildParameters(MythicLineConfig mlc) {
      if (MythicBukkit.isVolatile()) {
         for (Entry<String, String> entry : mlc.entrySet()) {
            String toLowerCase = entry.getKey().toLowerCase();
            switch (toLowerCase) {
               case "skill":
               case "s":
               case "meta":
               case "m":
               case "mechanics":
               case "cooldown":
               case "cd":
               case "delay":
               case "repeat":
               case "targetinterval":
               case "targeti":
               case "repeatinterval":
               case "repeati":
               case "power":
               case "powersplitbetweentargets":
               case "powersplit":
               case "splitpower":
               case "forcesync":
               case "sync":
               case "targetisorigin":
               case "sourceisorigin":
               case "castfromorigin":
               case "fromorigin":
               case "fo":
               case "origin":
               case "targetcreative":
                  break;
               default:
                  this.parameters.put(entry.getKey(), PlaceholderString.of(entry.getValue()));
            }
         }
      }
   }

   private SkillMetadata injectParameters(SkillMetadata data) {
      for (Entry<String, PlaceholderString> entry : this.parameters.entrySet()) {
         data.getParameters().put(entry.getKey(), entry.getValue().get(data));
      }

      return data;
   }
}
