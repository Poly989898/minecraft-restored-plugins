package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import lombok.Getter;
import org.bukkit.block.data.BlockData;

@Getter
public class PacketFallingBlock<T extends PacketEntityRenderer & PacketFallingBlock.PacketFallingBlockEntityRenderer> extends PacketEntity {
   private DataTracker<BlockData> blockData = DataTracker.of(null);

   public PacketFallingBlock(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketFallingBlock(AbstractLocation location, BlockData block) {
      this(MythicProvider.get(), location);
      this.blockData.set(block);
   }

   public PacketFallingBlock(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualBlockRenderer(this));
   }

   public interface PacketFallingBlockEntityRenderer extends IPacketEntityRenderer {
   }
}
