package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;

public interface ISkillMetaComparisonCondition extends ISkillCondition {
   boolean check(SkillMetadata skillMetadata, AbstractEntity abstractEntity);
}
