package io.lumine.mythic.core.volatilecode.v26_3.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import java.util.List;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class VirtualDisplayItem extends VirtualEntity implements PacketItemDisplay.PacketItemDisplayEntityRenderer {
   public VirtualDisplayItem(PacketItemDisplay wrapper) {
      super(wrapper);
   }

   public PacketItemDisplay<VirtualDisplayItem> getWrapper() {
      return (PacketItemDisplay<VirtualDisplayItem>)super.getWrapper();
   }

   @Override
   public ClientboundAddEntityPacket createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.ITEM_DISPLAY);
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
         this.getWrapper().getItem().ifDirty(itemStack -> writeData(buf, 23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack)), force);
         this.getWrapper()
            .getDisplayTransform()
            .ifDirty(display -> writeData(buf, 24, EntityDataSerializers.BYTE, display == null ? 0 : (byte)display.ordinal()), force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }
}
