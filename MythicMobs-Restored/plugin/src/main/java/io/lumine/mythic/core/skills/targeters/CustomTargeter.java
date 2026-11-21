package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import java.util.Optional;
import org.bukkit.Bukkit;

public class CustomTargeter extends SkillTargeter {
   protected final String targeterName;
   protected final MythicLineConfig config;
   protected SkillTargeter targeter = null;
   protected boolean loaded = false;

   public CustomTargeter(SkillExecutor manager, String targeter, MythicLineConfig mlc) {
      super(manager, mlc);
      this.targeterName = targeter;
      this.config = mlc;
      this.targeter = new TriggerTargeter(manager, mlc);
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Loading CustomTargeter with name " + targeter);
      Schedulers.sync().runLater(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Attempting to Register CustomTargeter: " + targeter);
         MythicTargeterLoadEvent event = new MythicTargeterLoadEvent(this, targeter, this.config);
         Bukkit.getServer().getPluginManager().callEvent(event);
         if (event.getTargeter().isPresent()) {
            ISkillTargeter customTargeter = event.getTargeter().get();
            if (customTargeter instanceof IEntityTargeter) {
               this.targeter = new WrappedEntityTargeter(manager, mlc, (IEntityTargeter)customTargeter);
            } else if (customTargeter instanceof ILocationTargeter) {
               this.targeter = new WrappedLocationTargeter(manager, mlc, (ILocationTargeter)customTargeter);
            }

            this.loaded = true;
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loaded Custom Targeter {0}", this.targeterName);
         } else {
            MythicLogger.errorTargeterConfig(this, mlc, "Failed to load custom targeter " + this.targeterName);
         }
      }, 1L);
   }

   public Optional<SkillTargeter> getTargeter() {
      return Optional.ofNullable(this.targeter);
   }
}
