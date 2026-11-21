package io.lumine.mythic.api.skills;

public interface INoTargetSkill extends ISkillMechanic {
   SkillResult cast(SkillMetadata skillMetadata);
}
