package io.lumine.mythic.api.skills;

public interface IMetaSkill extends ISkillMechanic {
   SkillResult cast(SkillMetadata skillMetadata);
}
