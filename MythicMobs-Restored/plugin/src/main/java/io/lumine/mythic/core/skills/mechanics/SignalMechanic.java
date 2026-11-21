package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicPlayerSignalEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "signal",
   aliases = {"sendSignal"},
   description = "Sends a signal to the target mob"
)
public class SignalMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private PlaceholderString signal;

   public SignalMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.signal = mlc.getPlaceholderString(new String[]{"signal", "s"}, "ping");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         PlayerData profile = this.getPlugin().getPlayerManager().getProfile(target.asPlayer());
         Events.call(new MythicPlayerSignalEvent(profile, this.signal.get(data, target)));
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending signal {0} to player {1}", this.signal, target.getName());
         return SkillResult.SUCCESS;
      } else {
         Optional<ActiveMob> maybeMob = this.getPlugin().getMobManager().getActiveMob(target.getUniqueId());
         if (maybeMob.isPresent()) {
            String signal = this.signal.get(data, target);
            ActiveMob at = maybeMob.get();
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Sending signal {0} to mob {1} {2}", signal, at.getType().getInternalName(), at.getUniqueId());
            at.signalMob(data.getCaster().getEntity(), signal);
            return SkillResult.SUCCESS;
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Failed to signal vanilla mob {0}", target.getName());
            return SkillResult.INVALID_TARGET;
         }
      }
   }
}
