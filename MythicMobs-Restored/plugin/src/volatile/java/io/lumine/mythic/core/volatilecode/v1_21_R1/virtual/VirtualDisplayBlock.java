package io.lumine.mythic.core.volatilecode.v1_21_R1.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay;
import java.util.List;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.v1_21_R1.block.data.CraftBlockData;

public class VirtualDisplayBlock extends VirtualEntity implements PacketBlockDisplay.PacketBlockDisplayEntityRenderer {
   public VirtualDisplayBlock(PacketBlockDisplay wrapper) {
      super(wrapper);
   }

   public PacketBlockDisplay<VirtualDisplayBlock> getWrapper() {
      return (PacketBlockDisplay<VirtualDisplayBlock>)super.getWrapper();
   }

   @Override
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.j);
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         List<c<?>> buf = Lists.newArrayList();
         if (force) {
            writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
         }

         this.getWrapper().getInterpolationDelay().ifDirty(value -> writeData(buf, 8, DataWatcherRegistry.b, value), force);
         this.getWrapper().getInterpolationDuration().ifDirty(value -> {
            writeData(buf, 9, DataWatcherRegistry.b, value);
            writeData(buf, 10, DataWatcherRegistry.b, value);
         }, force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, DataWatcherRegistry.D, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, DataWatcherRegistry.D, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, DataWatcherRegistry.E, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, DataWatcherRegistry.E, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, DataWatcherRegistry.a, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, DataWatcherRegistry.b, value), force);
         this.getWrapper().getViewRange().ifDirty(value -> writeData(buf, 17, DataWatcherRegistry.d, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, DataWatcherRegistry.d, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, DataWatcherRegistry.d, value), force);
         this.getWrapper().getBlockData().ifDirty(blockState -> writeData(buf, 23, DataWatcherRegistry.i, ((CraftBlockData)blockState).getState()), force);
         return new PacketPlayOutEntityMetadata(this.getEntityId(), buf);
      }
   }
}
