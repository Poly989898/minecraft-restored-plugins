package io.lumine.mythic.core.skills.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import java.util.Optional;
import org.bukkit.Bukkit;

public class CustomCondition extends SkillCondition {
   protected final String conditionName;
   protected final MythicLineConfig config;
   protected ISkillCondition condition = null;
   protected boolean loaded = false;

   public CustomCondition(String condition, String line, MythicLineConfig mlc) {
      super(line);
      this.conditionName = condition;
      this.config = mlc;
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Loading CustomCondition with name " + condition);
      Schedulers.sync().runLater(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Attempting to Register CustomCondition: " + condition);
         MythicConditionLoadEvent event = new MythicConditionLoadEvent(this, condition, this.conditionVar, this.config);
         Bukkit.getServer().getPluginManager().callEvent(event);
         if (event.getCondition().isPresent()) {
            this.condition = event.getCondition().get();
            this.loaded = true;
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loaded Custom Condition {0}", this.conditionName);
         } else {
            MythicLogger.errorConditionConfig(this, mlc, "Failed to load custom condition " + this.conditionName);
         }
      }, 1L);
   }

   public Optional<ISkillCondition> getCondition() {
      return Optional.ofNullable(this.condition);
   }

   public String getConditionArgument() {
      return this.conditionVar;
   }
}
