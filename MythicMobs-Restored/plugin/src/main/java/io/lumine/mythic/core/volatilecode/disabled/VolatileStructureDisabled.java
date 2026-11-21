package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.adapters.AbstractBiome;
import org.bukkit.structure.Structure;

public class VolatileStructureDisabled extends AbstractBiome {
   public VolatileStructureDisabled(Structure structure) {
      super(structure.toString());
   }

   @Override
   public String getType() {
      return "unknown";
   }
}
