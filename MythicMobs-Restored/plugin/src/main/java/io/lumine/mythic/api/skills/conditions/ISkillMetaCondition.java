package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.skills.SkillMetadata;

public interface ISkillMetaCondition extends ISkillCondition {
   boolean check(SkillMetadata skillMetadata);
}
