package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;

@MythicCondition(
   author = "Ashijin",
   name = "distanceFromPin",
   description = "Tests if the caster is within a certain distance of a pin."
)
public class DistanceFromPinCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "pin",
      aliases = {"p"},
      description = ""
   )
   private PlaceholderString pin;
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The distance to match"
   )
   protected RangedDouble distance;

   public DistanceFromPinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.pin = mlc.getPlaceholderString(new String[]{"pin", "p"}, null);
      String d = mlc.getString(new String[]{"distance", "d"}, this.conditionVar);
      this.distance = new RangedDouble(d, true);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Optional<Pin> maybePin = getPlugin().getPinManager().getPin(this.pin.get());
      if (maybePin.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", this.pin.get());
         return false;
      } else {
         AbstractLocation pin = maybePin.get().getLocations().get(0);
         double diffSq = (float)pin.distanceSquared(target.getLocation());
         return this.distance.equals(diffSq);
      }
   }
}
