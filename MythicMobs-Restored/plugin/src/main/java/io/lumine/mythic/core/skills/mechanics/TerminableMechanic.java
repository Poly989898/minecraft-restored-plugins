package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@MythicMechanic(
   author = "Ticxo",
   name = "terminable",
   aliases = {"stoppable", "cancelable", "exit", "terminatable"},
   description = "Applies an aura that can be fully canceled from execution if a condition is met."
)
public class TerminableMechanic extends Aura implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected String terminateString;
   protected List<SkillCondition> terminateCondition = null;
   protected String onTerminateSkillName;
   protected Optional<Skill> onTerminateSkill = Optional.empty();
   protected boolean deep;

   public TerminableMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.terminateString = mlc.getString(new String[]{"terminateconditions", "conditions", "cond"}, "null");
      this.deep = mlc.getBoolean(new String[]{"deep"}, false);
      this.onTerminateSkillName = mlc.getString(new String[]{"onterminate", "ox"});
      if (MythicBukkit.isVolatile() && this.terminateString != null) {
         this.terminateCondition = this.getPlugin().getSkillManager().getConditions(this.terminateString);
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onTerminateSkillName != null) {
            this.onTerminateSkill = this.getManager().getSkill(file, this, this.onTerminateSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new TerminableMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new TerminableMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      protected SkillMetadata branch;

      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         if (!TerminableMechanic.this.deep) {
            this.skillMetadata.setTerminatedReference(new AtomicBoolean());
         }

         this.branch = data.deeperClone(false);
         this.branch.setCallingEvent(this);
         this.branch = this.branch.setEntityTarget(entity);
         this.branch = this.branch.setIsAsync(true);
         this.branch.setExecuteAfterDeath(true);
         this.start();
      }

      public Tracker(SkillMetadata data, AbstractLocation location) {
         super(location, data);
         if (!TerminableMechanic.this.deep) {
            this.skillMetadata.setTerminatedReference(new AtomicBoolean());
         }

         this.branch = data.deeperClone();
         this.branch.setCallingEvent(this);
         this.branch = this.branch.setLocationTarget(location);
         this.branch = this.branch.setIsAsync(true);
         this.branch.setExecuteAfterDeath(true);
         this.start();
      }

      @Override
      public void auraTick() {
         SkillMetadataImpl data = this.skillMetadata.deeperClone();
         this.location.ifPresent(data::setLocationTarget);
         this.entity.ifPresent(data::setEntityTarget);
         if (this.canTick(data)) {
            super.auraTick();
         } else {
            this.executeAuraSkill(TerminableMechanic.this.onTerminateSkill, this.branch, false);
            this.skillMetadata.terminate();
            this.terminate();
         }
      }

      private boolean canTick(SkillMetadata data) {
         if (TerminableMechanic.this.terminateCondition != null) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Applying terminate conditions");

            for (SkillCondition mc : TerminableMechanic.this.terminateCondition) {
               if (!mc.evaluateTargets(data)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
