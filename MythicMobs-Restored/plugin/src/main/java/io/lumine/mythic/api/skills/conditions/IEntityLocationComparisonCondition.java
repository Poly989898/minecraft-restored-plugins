package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;

public interface IEntityLocationComparisonCondition extends ISkillCondition {
   boolean check(AbstractEntity abstractEntity, AbstractLocation abstractLocation);
}
