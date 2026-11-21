package io.lumine.mythic.api.skills.stats;

import io.lumine.mythic.core.skills.stats.StatType;

public interface StatSnapshot {
   double get(StatType statType);
}
