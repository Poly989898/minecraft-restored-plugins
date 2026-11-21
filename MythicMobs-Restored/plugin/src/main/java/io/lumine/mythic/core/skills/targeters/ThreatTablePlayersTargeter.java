package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "ThreatTablePlayers",
   aliases = {"PlayersInThreatTable", "TTP"},
   description = "Targets every player on the casting mob's Threat Table"
)
public class ThreatTablePlayersTargeter extends IEntitySelector {
   public ThreatTablePlayersTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      if (!(data.getCaster() instanceof ActiveMob)) {
         return targets;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         if (am.hasThreatTable()) {
            for (AbstractEntity le : am.getThreatTable().getAllThreatTargets()) {
               if (le != null && le.isPlayer()) {
                  targets.add(le);
               }
            }
         } else {
            MythicLogger.errorTargeterConfig(this, this.config, "ThreatTable targeters are only available on mobs with Options.UseThreatTable set to True!.");
         }

         return targets;
      }
   }
}
