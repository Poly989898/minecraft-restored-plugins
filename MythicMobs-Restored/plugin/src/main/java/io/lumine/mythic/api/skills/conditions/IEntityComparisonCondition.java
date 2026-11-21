package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;

public interface IEntityComparisonCondition extends ISkillCondition {
   boolean check(AbstractEntity abstractEntity, AbstractEntity abstractEntity2);
}
