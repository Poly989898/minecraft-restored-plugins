package io.lumine.mythic.api.volatilecode.handlers;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import java.util.Collection;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public interface VolatileBlockHandler {
   void sendBlockChange(Collection<AbstractPlayer> abstractPlayers, AbstractLocation abstractLocation, AbstractBlock abstractBlock);

   void sendMultiBlockChange(Collection<AbstractPlayer> abstractPlayers, Map<AbstractLocation, AbstractBlock> map);

   void applyPhysics(Block block);

   void togglePowerable(AbstractLocation abstractLocation);

   void togglePowerable(AbstractLocation abstractLocation, long value);

   default void togglePiston(AbstractLocation target) {
   }

   void throwBlock(AbstractLocation abstractLocation, Material material, AbstractVector abstractVector, int value, boolean flag);

   default void throwBlock(AbstractLocation target, BlockState blockState, AbstractVector velocity, int duration, boolean hideSourceBlock) {
      this.throwBlock(target, blockState.getType(), velocity, duration, hideSourceBlock);
   }

   default boolean pushBlock(AbstractLocation location, AbstractDirection direction) {
      return false;
   }

   default void sendBlockDamaged(Collection<AbstractPlayer> audience, AbstractLocation location, int damage) {
   }
}
