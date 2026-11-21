package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;

public interface IEntityCondition extends ISkillCondition {
   boolean check(AbstractEntity abstractEntity);
}
