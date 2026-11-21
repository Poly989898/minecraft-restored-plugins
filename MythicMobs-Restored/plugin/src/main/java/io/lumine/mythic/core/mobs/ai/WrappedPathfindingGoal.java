package io.lumine.mythic.core.mobs.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;

public abstract class WrappedPathfindingGoal extends WrappedPathfinder {
   public WrappedPathfindingGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }
}
