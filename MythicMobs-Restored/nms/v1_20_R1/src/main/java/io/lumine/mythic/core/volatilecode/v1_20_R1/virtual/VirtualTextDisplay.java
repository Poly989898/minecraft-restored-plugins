package io.lumine.mythic.core.volatilecode.v1_20_R1.virtual;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;

public class VirtualTextDisplay extends VirtualEntity implements PacketTextDisplay.PacketTextDisplayEntityRenderer {
   public VirtualTextDisplay(PacketTextDisplay wrapper) {
      super(wrapper);
   }

   public PacketTextDisplay<VirtualTextDisplay> getWrapper() {
      return (PacketTextDisplay<VirtualTextDisplay>)super.getWrapper();
   }

   @Override
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.aX);
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.d(this.getEntityId());
         if (force) {
            writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
         }

         this.getWrapper().getInterpolationDelay().ifDirty(value -> writeData(buf, 8, DataWatcherRegistry.b, value), force);
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, DataWatcherRegistry.b, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 10, DataWatcherRegistry.A, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 11, DataWatcherRegistry.A, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 12, DataWatcherRegistry.B, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 13, DataWatcherRegistry.B, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 14, DataWatcherRegistry.a, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 15, DataWatcherRegistry.b, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 19, DataWatcherRegistry.d, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, DataWatcherRegistry.d, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(null));
            writeData(buf, 22, DataWatcherRegistry.f, ChatSerializer.a(text));
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> writeData(buf, 23, DataWatcherRegistry.b, value), force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 24, DataWatcherRegistry.b, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 25, DataWatcherRegistry.a, value), force);
         buf.writeByte(255);
         return new PacketPlayOutEntityMetadata(buf);
      }
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(AbstractPlayer player, boolean force) {
      if (!this.getWrapper().isDirty() && !force) {
         return null;
      } else {
         this.getWrapper().clearDirty();
         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.d(this.getEntityId());
         if (force) {
            writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
         }

         this.getWrapper().getInterpolationDelay().ifDirty(value -> writeData(buf, 8, DataWatcherRegistry.b, value), force);
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, DataWatcherRegistry.b, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 10, DataWatcherRegistry.A, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 11, DataWatcherRegistry.A, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 12, DataWatcherRegistry.B, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 13, DataWatcherRegistry.B, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 14, DataWatcherRegistry.a, (byte)value.ordinal()), force);
         this.getWrapper().getViewRange().ifDirty(value -> writeData(buf, 16, DataWatcherRegistry.d, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 19, DataWatcherRegistry.d, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 20, DataWatcherRegistry.d, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(player));
            writeData(buf, 22, DataWatcherRegistry.f, ChatSerializer.a(text));
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> {
            if (value != null) {
               writeData(buf, 23, DataWatcherRegistry.b, value);
            }
         }, force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 24, DataWatcherRegistry.b, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 25, DataWatcherRegistry.a, value), force);
         buf.writeByte(255);
         return new PacketPlayOutEntityMetadata(buf);
      }
   }
}
