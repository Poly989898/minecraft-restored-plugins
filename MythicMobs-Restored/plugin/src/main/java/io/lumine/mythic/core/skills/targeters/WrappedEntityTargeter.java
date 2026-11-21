package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.core.skills.SkillExecutor;
import java.util.Collection;

public class WrappedEntityTargeter extends IEntitySelector implements IEntityTargeter {
   private final IEntityTargeter targeter;

   public WrappedEntityTargeter(SkillExecutor manager, MythicLineConfig mlc, IEntityTargeter targeter) {
      super(manager, mlc, targeter);
      this.targeter = targeter;
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      Collection<AbstractEntity> entities = this.targeter.getEntities(data);
      if (entities.isEmpty() && this.fallback instanceof IEntityTargeter fallbackEntities) {
         entities = fallbackEntities.getEntities(data);
      }

      return entities;
   }

   @Override
   public IEntitySelector.TargetMode getFilter(IEntitySelector.TargetFilter filter) {
      return this.targeter.getFilter(filter);
   }
}
