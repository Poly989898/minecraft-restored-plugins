package io.lumine.mythic.api.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.Collection;

public interface ILocationTargeter extends ISkillTargeter {
   Collection<AbstractLocation> getLocations(SkillMetadata skillMetadata);

   default boolean useLocationMutators() {
      return true;
   }
}
