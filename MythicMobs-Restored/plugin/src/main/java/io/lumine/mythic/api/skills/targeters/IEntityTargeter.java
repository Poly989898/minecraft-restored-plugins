package io.lumine.mythic.api.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import java.util.Collection;

public interface IEntityTargeter extends ISkillTargeter {
   Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata);

   default IEntitySelector.TargetMode getFilter(IEntitySelector.TargetFilter filter) {
      return IEntitySelector.TargetMode.INHERIT;
   }
}
