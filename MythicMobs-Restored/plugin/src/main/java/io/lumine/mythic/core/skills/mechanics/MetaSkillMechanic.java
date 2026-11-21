package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
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
   name = "metaskill",
   aliases = {"skill", "meta"},
   description = "Executes a metaskill"
)
public class MetaSkillMechanic extends SkillMechanic implements IMetaSkill, SkillHolder {
   protected String skillName;
   @MythicField(
      name = "skill",
      aliases = {"s", "meta", "m", "mechanics", "$", "()"},
      description = "The meta-skill to execute"
   )
   protected Optional<Skill> metaskill;
   protected boolean branch = false;
   protected boolean executeAfterDeath = false;
   protected boolean snapshotCasterStats = false;
   @Getter
   protected Map<String, PlaceholderString> parameters = Maps.newConcurrentMap();

   public MetaSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.target_creative = true;
      this.skillName = mlc.getString(new String[]{"skill", "s", "spell", "meta", "m", "mechanics", "$", "()"});
      this.branch = mlc.getBoolean(new String[]{"branch", "b", "fork", "f"}, false);
      this.executeAfterDeath = mlc.getBoolean(new String[]{"executeafterdeath", "continueafterdeath"}, false);
      this.snapshotCasterStats = mlc.getBoolean(new String[]{"snapshotcasterstats", "snapshotstats", "scs"}, false);
      this.buildParameters(mlc);
      this.getManager().queueSecondPass(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading MetaSkillMechanic -> {0}", this.skillName);
         this.metaskill = this.getManager().getSkill(file, this, this.skillName);
         if (!this.metaskill.isPresent()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + this.skillName);
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded MetaSkillMechanic successfully");
            this.metaskill.get().addParent(this);
         }
      });
   }

   public MetaSkillMechanic(SkillExecutor manager, File file, String skill, String skillName, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.target_creative = true;
      this.skillName = skillName;
      this.buildParameters(mlc);
      this.getManager().queueSecondPass(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading MetaSkillMechanic -> {0}", this.skillName);
         this.metaskill = this.getManager().getSkill(file, this, this.skillName);
         if (!this.metaskill.isPresent()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + this.skillName);
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded MetaSkillMechanic successfully");
            this.metaskill.get().addParent(this);
         }
      });
   }

   public MetaSkillMechanic(SkillExecutor manager, File file, Skill skill) {
      super(manager, file);
      this.target_creative = true;
      this.skillName = skill.getInternalName();
      this.metaskill = Optional.of(skill);
      skill.addParent(this);
   }

   @Override
   public String getInternalName() {
      return this.parent.isPresent() ? this.parent.get().getInternalName() + "/skillMechanic" : "Unknown/skillMechanic";
   }

   public Skill getSkill() {
      return this.metaskill.get();
   }

   public boolean hasSkill() {
      return this.metaskill.isPresent();
   }

   @Override
   public SkillResult cast(SkillMetadata dataIn) {
      return this.castWithOutput(dataIn).result;
   }

   public MetaSkillMechanic.SkillCastOutput castWithOutput(SkillMetadata dataIn) {
      if (this.metaskill != null && this.metaskill.isPresent()) {
         Skill ms = this.metaskill.get();
         if (this.branch) {
            dataIn = dataIn.deeperClone();
            dataIn.setTerminatedReference(new AtomicBoolean());
         } else {
            dataIn = dataIn.deepClone();
         }

         if (this.snapshotCasterStats) {
            dataIn.snapshotCasterStats();
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
            return MetaSkillMechanic.SkillCastOutput.of(SkillResult.CONDITION_FAILED, dataIn);
         } else {
            if (this.getPlugin().getConfiguration().getDebugLevel() > 0) {
               if (data.getEntityTargets() != null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaSkill (et: {0}) -> {1}", data.getEntityTargets().size(), this.skillName);
               } else if (data.getLocationTargets() != null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaSkill (lt: {0}) -> {1}", data.getLocationTargets().size(), this.skillName);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing MetaSkill (t: 0) -> {0}", this.skillName);
               }
            }

            if (this.forceSync && data.getIsAsync()) {
               data.setIsAsync(false);
               casterScheduler(data).run(bindVariablesForLaterRun(data, () -> {
                  data.setIsAsync(false);
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Running Skill {0} Sync from MetaSkillMechanic", this.skillName);
                  ms.execute(data);
               }));
            } else {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Running Skill {0} Async from MetaSkillMechanic", this.skillName);
               ms.execute(data);
            }

            return MetaSkillMechanic.SkillCastOutput.of(SkillResult.SUCCESS, data);
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MetaSkill {0} was not found on load", this.skillName);
         return MetaSkillMechanic.SkillCastOutput.of(SkillResult.INVALID_CONFIG, dataIn);
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
               case "charges":
               case "cooldownmode":
               case "chargecooldownmode":
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
               case "branch":
               case "fork":
               case "snapshotcasterstats":
               case "snapshotstats":
               case "scs":
               case "snapshottriggerstats":
               case "sts":
               case "targetcreative":
                  break;
               default:
                  this.parameters.put(entry.getKey(), PlaceholderString.of(entry.getValue()));
            }
         }
      }
   }

   private SkillMetadata injectParameters(SkillMetadata data) {
      data = data.deepClone();

      for (Entry<String, PlaceholderString> entry : this.parameters.entrySet()) {
         data.getParameters().put(entry.getKey(), entry.getValue().get(data));
      }

      return data;
   }

   public record SkillCastOutput(SkillResult result, SkillMetadata dataOut) {
      public static MetaSkillMechanic.SkillCastOutput of(SkillResult result, SkillMetadata dataOut) {
         return new MetaSkillMechanic.SkillCastOutput(result, dataOut);
      }

      public static MetaSkillMechanic.SkillCastOutput ofSkillResult(SkillResult result) {
         return new MetaSkillMechanic.SkillCastOutput(result, null);
      }
   }
}
