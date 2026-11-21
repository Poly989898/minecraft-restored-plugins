package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import java.util.Optional;

@FunctionalInterface
public interface SkillCasterProvider {
   Optional<SkillCaster> getCaster(AbstractEntity abstractEntity);
}
