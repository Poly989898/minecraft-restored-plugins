package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.Optional;

@MythicTargeter(
   author = "Ashijin",
   name = "entitiesNearPin",
   aliases = {"NearPin", "ENP"},
   description = "Targets entities near the origin"
)
public class EntitiesNearPinTargeter extends EntitiesNearbyTargeter {
   protected PlaceholderString key;

   public EntitiesNearPinTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.key = mlc.getPlaceholderString(new String[]{"name", "n", "pin", "p", "key", "k"}, null);
      if (this.key == null) {
         MythicLogger.errorTargeterConfig(this, mlc, "Pin name must be set.");
      }
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Optional<Pin> maybePin = this.getPlugin().getPinManager().getPin(this.key.get(data));
      if (maybePin.isEmpty()) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Pin {0} is not defined", this.key);
         return Lists.newArrayList();
      } else {
         Pin pin = maybePin.get();
         Collection<AbstractEntity> targets = Lists.newArrayList();

         for (AbstractLocation pinLocation : pin.getLocations()) {
            targets.addAll(this.getEntitiesNearPoint(data, pinLocation));
         }

         return targets;
      }
   }
}
