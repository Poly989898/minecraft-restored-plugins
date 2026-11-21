package io.lumine.mythic.core.volatilecode.v26_3.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.volatilecode.virtual.PacketBlockDisplay;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public class VirtualDisplayBlock extends VirtualEntity implements PacketBlockDisplay.PacketBlockDisplayEntityRenderer {
   public VirtualDisplayBlock(PacketBlockDisplay wrapper) {
      super(wrapper);
   }

   public PacketBlockDisplay<VirtualDisplayBlock> getWrapper() {
      return (PacketBlockDisplay<VirtualDisplayBlock>)super.getWrapper();
   }

   @Override
   public ClientboundAddEntityPacket createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.BLOCK_DISPLAY);
   }

   @Override
   public ClientboundSetEntityDataPacket createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         List<DataValue<?>> buf = Lists.newArrayList();
         if (force) {
            writeData(buf, 0, EntityDataSerializers.BYTE, (byte)32);
            writeData(buf, 1, EntityDataSerializers.INT, Integer.MAX_VALUE);
         }

         this.getWrapper().getInterpolationDelay().ifDirty(value -> writeData(buf, 8, EntityDataSerializers.INT, value), force);
         this.getWrapper().getInterpolationDuration().ifDirty(value -> {
            writeData(buf, 9, EntityDataSerializers.INT, value);
            writeData(buf, 10, EntityDataSerializers.INT, value);
         }, force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, EntityDataSerializers.BYTE, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, EntityDataSerializers.INT, value), force);
         this.getWrapper().getViewRange().ifDirty(value -> writeData(buf, 17, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper()
            .getBlockData()
            .ifDirty(blockState -> writeData(buf, 23, EntityDataSerializers.BLOCK_STATE, ((CraftBlockData)blockState).getState()), force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }
}
