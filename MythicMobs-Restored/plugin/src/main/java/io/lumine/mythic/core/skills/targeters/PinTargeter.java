package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.pins.MultiPin;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@MythicTargeter(
   author = "Ashijin",
   name = "pin",
   aliases = {},
   description = "Targets the location of a saved pin"
)
public class PinTargeter extends ILocationSelector {
   protected PlaceholderString key;
   protected boolean random;

   public PinTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.key = mlc.getPlaceholderString(new String[]{"name", "n", "pin", "p", "key", "k"}, null);
      this.random = mlc.getBoolean(new String[]{"random", "r"}, false);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = Lists.newArrayList();
      if (this.key == null && data.getVariables().has("pin")) {
         targets.add(data.getVariables().getLocation("pin"));
         return targets;
      } else {
         Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(this.key.get(data));
         if (maybePin.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", this.key);
            return targets;
         } else {
            Pin pin = maybePin.get();
            if (this.random && pin instanceof MultiPin multiPin) {
               List<AbstractLocation> locations = pin.getLocations();
               int size = locations.size();
               if (size == 0) {
                  return targets;
               }

               int idx = size == 1 ? 0 : ThreadLocalRandom.current().nextInt(size);
               targets.add(this.mutate(data, locations.get(idx)));
            } else {
               for (AbstractLocation pinLocation : pin.getLocations()) {
                  targets.add(this.mutate(data, pinLocation));
               }
            }

            return targets;
         }
      }
   }
}
