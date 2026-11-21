package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import java.util.Collection;

public class VanillaTargeter extends IEntitySelector {
   private String selector;

   public VanillaTargeter(SkillExecutor manager, MythicLineConfig mlc, String selector) {
      super(manager, mlc);
      selector = "@" + selector;
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded VANILLA Targeter: " + selector);
      this.selector = selector;
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      return MythicBukkit.inst().getVolatileCodeHandler().getEntitiesBySelector(am, this.selector);
   }
}
