package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.ILocationTargeter;
import io.lumine.mythic.core.skills.SkillExecutor;
import java.util.Collection;

public class WrappedLocationTargeter extends ILocationSelector implements ILocationTargeter {
   private final ILocationTargeter targeter;

   public WrappedLocationTargeter(SkillExecutor manager, MythicLineConfig mlc, ILocationTargeter targeter) {
      super(manager, mlc);
      this.targeter = targeter;
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      Collection<AbstractLocation> targets = this.targeter.getLocations(data);
      if (targets.isEmpty() && this.fallback instanceof ILocationTargeter fallbackLocation) {
         targets = fallbackLocation.getLocations(data);
      }

      if (this.targeter.useLocationMutators()) {
         targets.forEach(target -> this.mutate(data, target));
      }

      return targets;
   }
}
