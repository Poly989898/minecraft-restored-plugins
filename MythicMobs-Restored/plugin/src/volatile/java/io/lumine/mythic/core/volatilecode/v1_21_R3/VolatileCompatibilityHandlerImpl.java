package io.lumine.mythic.core.volatilecode.v1_21_R3;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import java.util.Set;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Wolf.Variant;

public class VolatileCompatibilityHandlerImpl implements VolatileCompatibilityHandler {
   public VolatileCompatibilityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public Set<String> getWolfVariantValues(Wolf wolf) {
      Variant variant = wolf.getVariant();
      return VolatileCompatibilityHandler.variantValues(variant.toString(), variant.getKey());
   }
}
