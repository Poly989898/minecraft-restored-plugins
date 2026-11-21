package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;

@MythicCondition(
   author = "Ashijin",
   name = "inPinRegion",
   aliases = {},
   description = "If the location is inside the region created by 2 pins"
)
public class InPinRegionCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "pin1",
      aliases = {"p1"},
      description = ""
   )
   private PlaceholderString pin1;
   @MythicField(
      name = "pin2",
      aliases = {"p2"},
      description = ""
   )
   private PlaceholderString pin2;

   public InPinRegionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pin1 = mlc.getPlaceholderString(new String[]{"pin1", "p1"}, null);
      this.pin2 = mlc.getPlaceholderString(new String[]{"pin2", "p2"}, null);
   }

   @Override
   public boolean check(AbstractLocation target) {
      Optional<Pin> maybePin1 = getPlugin().getPinManager().getPin(this.pin1.get());
      if (maybePin1.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin1 {0} is not defined", this.pin1.get());
         return false;
      } else {
         Optional<Pin> maybePin2 = getPlugin().getPinManager().getPin(this.pin2.get());
         if (maybePin2.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin2 {0} is not defined", this.pin2.get());
            return false;
         } else {
            AbstractLocation pin1 = maybePin1.get().getLocations().get(0);
            AbstractLocation pin2 = maybePin2.get().getLocations().get(0);
            Region region = Region.of(pin1.toPosition(), pin2.toPosition());
            return region.inRegion(target.toPosition());
         }
      }
   }
}
