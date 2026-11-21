package io.lumine.mythic.core.mobs.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;

public abstract class WrappedPathfindingTargeter extends WrappedPathfinder {
   public WrappedPathfindingTargeter(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }
}
