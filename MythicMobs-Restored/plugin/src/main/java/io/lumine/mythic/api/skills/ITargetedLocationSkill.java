package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.adapters.AbstractLocation;

public interface ITargetedLocationSkill extends ISkillMechanic {
   SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation);
}
