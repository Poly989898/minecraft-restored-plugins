package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractLocation;

public interface ILocationComparisonCondition extends ISkillCondition {
   boolean check(AbstractLocation abstractLocation, AbstractLocation abstractLocation2);
}
