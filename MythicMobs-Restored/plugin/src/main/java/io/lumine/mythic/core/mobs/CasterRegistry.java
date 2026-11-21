package io.lumine.mythic.core.mobs;

import io.lumine.mythic.api.skills.SkillCaster;
import java.util.Optional;
import java.util.UUID;

public interface CasterRegistry {
   Optional<SkillCaster> getSkillCaster(UUID uuid);
}
