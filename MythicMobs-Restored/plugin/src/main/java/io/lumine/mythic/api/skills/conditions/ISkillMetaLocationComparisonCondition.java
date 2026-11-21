package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillMetadata;

public interface ISkillMetaLocationComparisonCondition extends ISkillCondition {
   boolean check(SkillMetadata skillMetadata, AbstractLocation abstractLocation);
}
