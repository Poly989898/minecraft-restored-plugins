package io.lumine.mythic.api.skills.conditions;

import io.lumine.mythic.api.skills.SkillCaster;

public interface ICasterCondition extends ISkillCondition {
   boolean check(SkillCaster skillCaster);
}
