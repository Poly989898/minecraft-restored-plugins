package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;

public interface ITargetedEntitySkill extends ISkillMechanic {
   SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity);
}
