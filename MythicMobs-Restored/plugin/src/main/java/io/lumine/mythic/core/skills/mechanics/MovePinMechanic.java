package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.pins.SinglePin;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "movePin",
   description = "Moves the given pin to the target location"
)
public class MovePinMechanic extends SkillMechanic implements ITargetedLocationSkill {
   private PlaceholderString pinName;

   public MovePinMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.pinName = mlc.getPlaceholderString(new String[]{"pin", "p"}, null);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      String pinName = this.pinName.get(data);
      Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(pinName);
      if (maybePin.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Pin {0} not found.", pinName);
         return SkillResult.INVALID_CONFIG;
      } else {
         Pin pin = maybePin.get();
         if (pin instanceof SinglePin singlePin) {
            this.getPlugin().getPinManager().movePin(singlePin, target);
            return SkillResult.SUCCESS;
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "MultiPin does not support moving.");
            return SkillResult.INVALID_CONFIG;
         }
      }
   }
}
