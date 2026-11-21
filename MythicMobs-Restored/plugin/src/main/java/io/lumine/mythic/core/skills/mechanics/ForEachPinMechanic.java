package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.variables.IterationVariableRegistry;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "forEachPin",
   version = "5.10",
   description = "Runs a given metaskill for each pin in a multi-pin"
)
public class ForEachPinMechanic extends MetaSkillMechanic implements IMetaSkill, SkillHolder {
   final PlaceholderString key;

   public ForEachPinMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.key = mlc.getPlaceholderString(new String[]{"name", "n", "pin", "p", "key", "k"}, null);
      if (this.key == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "Pin name must be set.");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.metaskill.isEmpty()) {
         return SkillResult.INVALID_CONFIG;
      } else {
         Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(this.key.get(data));
         if (maybePin.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", this.key);
            return SkillResult.INVALID_CONFIG;
         } else {
            Pin pin = maybePin.get();

            for (AbstractLocation pinLocation : pin.getLocations()) {
               SkillMetadata newData = data.deepCloneNoTargets();
               newData.setLocationTarget(pinLocation);
               newData.setVariables(IterationVariableRegistry.of(newData.getVariables(), "pin", new LocationVariable(pinLocation)));
               super.cast(newData);
            }

            return SkillResult.SUCCESS;
         }
      }
   }
}
