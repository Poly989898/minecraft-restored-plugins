package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.Bukkit;

public class CustomMechanic extends SkillMechanic {
   protected final String skillName;
   protected final MythicLineConfig config;
   protected ISkillMechanic mechanic = null;
   protected boolean loaded = false;

   public CustomMechanic(SkillExecutor manager, File file, String skill, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.skillName = skill;
      this.config = mlc;
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading CustomMechanic with name " + skill);

      try {
         this.tryLoad(false);
      } catch (Exception e) {
      }

      if (!this.loaded) {
         Schedulers.sync().runLater(() -> this.tryLoad(true), 1L);
      }
   }

   protected void tryLoad(boolean printFail) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Attempting to Register CustomMechanic: " + this.skillName);
      MythicMechanicLoadEvent event = new MythicMechanicLoadEvent(this, this.skillName, this.config);
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (event.getMechanic().isPresent()) {
         this.mechanic = event.getMechanic().get();
         this.threadSafetyLevel = this.mechanic.getThreadSafetyLevel();
         this.target_creative = this.target_creative | this.mechanic.getTargetsCreatives();
         this.loaded = true;
         MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loaded Custom Mechanic {0}", this.skillName);
      } else if (printFail) {
         MythicLogger.errorMechanicConfig(this, this.config, "Failed to load custom mechanic " + this.skillName);
      }
   }

   public Optional<ISkillMechanic> getMechanic() {
      return Optional.ofNullable(this.mechanic);
   }
}
