package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "doppleganger",
   aliases = {"copyplayer"},
   description = "Disguises the caster as the target entity"
)
public class DopplegangerMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "hasNamePlate",
      aliases = {"nameplate"},
      description = "Sets the disguise name to \"_\"",
      defValue = "true"
   )
   private final boolean hasNameplate;
   @MythicField(
      name = "usePlayerName",
      aliases = {"upn"},
      description = "Whether to use the target's name",
      defValue = "false"
   )
   private final boolean usePlayerName;

   public DopplegangerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.hasNameplate = mlc.getBoolean(new String[]{"hasnameplate", "nameplate"}, true);
      this.usePlayerName = mlc.getBoolean(new String[]{"useplayername", "upn"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (data.getCaster() instanceof ActiveMob am) {
         if (CompatibilityManager.LibsDisguises == null) {
            return SkillResult.MISSING_COMPATIBILITY;
         } else if (!target.isPlayer()) {
            return SkillResult.INVALID_TARGET;
         } else {
            String disguise;
            if (!this.hasNameplate) {
               disguise = "PLAYER " + target.asPlayer().getName();
            } else if (this.usePlayerName) {
               disguise = "PLAYER " + target.asPlayer().getName();
            } else if (am.getType().getDisplayName() == null) {
               disguise = "PLAYER " + target.asPlayer().getName();
            } else {
               disguise = "PLAYER " + target.asPlayer().getName();
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "[+] Running Doppleganger Mechanic with string: " + disguise);
            CompatibilityManager.LibsDisguises.setDisguise(am, disguise);
            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
