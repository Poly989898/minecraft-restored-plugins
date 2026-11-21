package io.lumine.mythic.api.spawning;

import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MobSpawnCategory;
import java.util.Collection;
import java.util.Map;

public interface MythicSpawnState {
   AbstractWorld getWorld();

   int getSpawnableChunkCount();

   int getMaxMobCount();

   int getMaxMobsPerPlayer();

   int getCurrentMobCount(MobSpawnCategory mobSpawnCategory);

   int getMonsterCount();

   Collection<MobSpawnCategory> getCategories();

   Map<MobSpawnCategory, Integer> getCurrentMobCounts();
}
