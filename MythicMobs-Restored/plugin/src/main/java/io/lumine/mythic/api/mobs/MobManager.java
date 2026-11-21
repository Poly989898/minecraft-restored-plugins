package io.lumine.mythic.api.mobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.EggExecutor;
import io.lumine.mythic.core.mobs.MobStack;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MobManager {
   EggExecutor getEggManager();

   Collection<MythicMob> getMobTypes();

   Collection<String> getMobNames();

   Collection<MythicMob> getVanillaTypes();

   Optional<MythicMob> getVanillaType(MythicEntityType mythicEntityType);

   Collection<MobStack> getMobStacks();

   Collection<ActiveMob> getActiveMobs();

   Collection<ActiveMob> getActiveMobsInCombat();

   Optional<SkillCaster> getSkillCaster(UUID uuid);

   Optional<MythicMob> getMythicMob(String str);

   boolean isMythicMobSpawning();

   boolean isActiveMob(UUID uuid);

   void registerAudienceTrackedEntity(UUID uuid);

   void unregisterAudienceTrackedEntity(UUID uuid);

   Set<UUID> getAudienceTrackedEntities();

   void applyMobAudience(ActiveMob activeMob, Set<UUID> uuids);

   void applyAudienceVisibility(AbstractEntity abstractEntity, Set<UUID> uuids);
}
