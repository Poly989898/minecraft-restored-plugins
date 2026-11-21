package io.lumine.mythic.core.skills.stats;

import io.lumine.mythic.api.adapters.AbstractEntity;

public interface AttributeMappedStat {
   void applyAttribute(AbstractEntity abstractEntity, double value);
}
