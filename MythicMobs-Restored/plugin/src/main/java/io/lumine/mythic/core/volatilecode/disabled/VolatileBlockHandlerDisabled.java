package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler;
import java.util.Collection;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class VolatileBlockHandlerDisabled implements VolatileBlockHandler {
   @Override
   public void sendBlockChange(Collection<AbstractPlayer> audience, AbstractLocation location, AbstractBlock block) {
   }

   @Override
   public void sendMultiBlockChange(Collection<AbstractPlayer> audience, Map<AbstractLocation, AbstractBlock> changeMap) {
   }

   @Override
   public void applyPhysics(Block target) {
   }

   @Override
   public void togglePowerable(AbstractLocation location) {
   }

   @Override
   public void togglePowerable(AbstractLocation location, long duration) {
   }

   @Override
   public void throwBlock(AbstractLocation target, Material material, AbstractVector velocity, int duration, boolean hideSourceBlock) {
   }
}
