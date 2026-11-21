package io.lumine.mythic.api.skills.auras;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import java.util.UUID;

public interface AuraManager {
   AuraRegistry getAuraRegistry(AbstractEntity abstractEntity);

   AuraRegistry getAuraRegistry(UUID uuid);

   boolean getHasAura(AbstractEntity abstractEntity, String str);

   int getAuraStacks(AbstractEntity abstractEntity, String str);

   void removeAuraStacks(AbstractEntity abstractEntity, String str, int value);
}
