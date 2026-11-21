package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IDummySkill;
import io.lumine.mythic.api.skills.IWaitableSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.tasks.TaskScheduler;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@MythicMechanic(
   author = "Seyarada",
   name = "wait",
   description = "Wait execution to finish before executing the next mechanic"
)
public class WaitUntilMechanic extends SkillMechanic implements IWaitableSkill, IDummySkill {
   @MythicField(
      name = "conditions",
      aliases = {"cond", "c", "until"},
      description = "Conditions to check against"
   )
   protected List<SkillCondition> conditions = null;
   protected String conditionsStr;
   @MythicField(
      name = "interval",
      aliases = {"i"},
      defValue = "1",
      description = "How often to check the conditions"
   )
   protected PlaceholderDuration checkInterval;
   @MythicField(
      name = "timeout",
      aliases = {"timeouttime", "tt"},
      defValue = "200",
      description = "Maximum ticks to wait before executing even if conditions aren't met"
   )
   protected PlaceholderDuration timeout;
   @MythicField(
      name = "cancelSkill",
      aliases = {"cancel", "cs"},
      defValue = "false",
      description = "If the metaskill should be cancelled on timeout instead of executing"
   )
   protected boolean shouldCancelMetaskill;
   @MythicField(
      name = "onTimeoutSkill",
      aliases = {"ontimeoutskill", "ontimeout", "ots"},
      description = "The meta-skill to execute when the wait times out"
   )
   protected Optional<Skill> onTimeoutSkill = Optional.empty();
   protected String onTimeoutSkillStr;
   @MythicField(
      name = "cancelConditions",
      aliases = {"cc", "unless"},
      description = "Conditions cancel the wait, if met"
   )
   protected List<SkillCondition> cancelConditions = null;
   protected String cancelConditionsStr;

   public WaitUntilMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.conditionsStr = mlc.getString(new String[]{"conditions", "cond", "c", "until"}, null);
      this.checkInterval = mlc.getPlaceholderDuration(new String[]{"interval", "i"}, DurationUnit.TICKS, "1");
      this.timeout = mlc.getPlaceholderDuration(new String[]{"timeout", "timeouttime", "tt"}, DurationUnit.TICKS, "200");
      this.shouldCancelMetaskill = mlc.getBoolean(new String[]{"cancelSkill", "cancel", "cs"}, false);
      this.onTimeoutSkillStr = mlc.getString(new String[]{"onTimeoutSkill", "ontimeoutskill", "ontimeout", "ots"}, null);
      this.cancelConditionsStr = mlc.getString(new String[]{"cancelConditions", "cc", "unless"}, null);
      this.getManager().queueSecondPass(() -> {
         if (this.conditionsStr != null) {
            this.conditions = this.getManager().getConditions(this.conditionsStr);
         }

         if (this.onTimeoutSkillStr != null) {
            this.onTimeoutSkill = this.getManager().getSkill(file, this, this.onTimeoutSkillStr);
            if (this.onTimeoutSkill.isEmpty()) {
               MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + this.onTimeoutSkillStr);
            }
         }

         if (this.cancelConditionsStr != null) {
            this.cancelConditions = this.getManager().getConditions(this.cancelConditionsStr);
         }
      });
   }

   @Override
   public CompletableFuture<Boolean> completableExecution(final SkillMetadata data) {
      final CompletableFuture<Boolean> future = new CompletableFuture<>();
      final int maxTicks = this.timeout.get(data).getTicks();
      final int interval = Math.max(1, this.checkInterval.get(data).getTicks());
      TaskScheduler scheduler;
      if (data.getCaster() != null && data.getCaster().getEntity() != null && data.getCaster().getEntity().getBukkitEntity() != null) {
         scheduler = Schedulers.of(data.getCaster().getEntity().getBukkitEntity());
      } else if (data.getCaster() != null && data.getCaster().getLocation() != null) {
         scheduler = Schedulers.of(BukkitAdapter.adapt(data.getCaster().getLocation()));
      } else {
         scheduler = Schedulers.sync();
      }

      scheduler.runRepeating(new Consumer<Task>() {
         private int elapsedTicks = 0;

         public void accept(Task task) {
            if (maxTicks > 0 && this.elapsedTicks >= maxTicks) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "WaitUntil timed out after {0} ticks", this.elapsedTicks);
               WaitUntilMechanic.this.onTimeoutSkill.ifPresent(skill -> {
                  SkillMetadata timeoutData = data.deepClone();
                  if (skill.isUsable(timeoutData)) {
                     skill.execute(timeoutData);
                  }
               });
               future.complete(!WaitUntilMechanic.this.shouldCancelMetaskill);
               task.stop();
            } else {
               if (WaitUntilMechanic.this.cancelConditions != null) {
                  for (SkillCondition condition : WaitUntilMechanic.this.cancelConditions) {
                     if (condition.evaluateCaster(data)) {
                        future.complete(false);
                        task.stop();
                        return;
                     }
                  }
               }

               boolean allPassed = true;
               if (WaitUntilMechanic.this.conditions != null) {
                  for (SkillCondition conditionx : WaitUntilMechanic.this.conditions) {
                     if (!conditionx.evaluateCaster(data)) {
                        allPassed = false;
                        break;
                     }
                  }
               }

               if (allPassed) {
                  future.complete(true);
                  task.stop();
               } else {
                  this.elapsedTicks = this.elapsedTicks + interval;
               }
            }
         }
      }, 0L, (long)interval);
      return future;
   }
}
