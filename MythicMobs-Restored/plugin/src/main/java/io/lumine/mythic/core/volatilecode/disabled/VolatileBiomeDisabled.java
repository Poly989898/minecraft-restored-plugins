package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.adapters.AbstractBiome;
import org.bukkit.block.Biome;

public class VolatileBiomeDisabled extends AbstractBiome {
   public VolatileBiomeDisabled(Biome biome) {
      super(biome.toString());
   }

   @Override
   public String getType() {
      return "unknown";
   }
}
