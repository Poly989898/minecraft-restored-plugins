package io.lumine.mythic.core.volatilecode.v26_3.virtual;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityTypes;

public class VirtualTextDisplay extends VirtualEntity implements PacketTextDisplay.PacketTextDisplayEntityRenderer {
   public VirtualTextDisplay(PacketTextDisplay wrapper) {
      super(wrapper);
   }

   public PacketTextDisplay<VirtualTextDisplay> getWrapper() {
      return (PacketTextDisplay<VirtualTextDisplay>)super.getWrapper();
   }

   @Override
   public ClientboundAddEntityPacket createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.TEXT_DISPLAY);
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
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, EntityDataSerializers.INT, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, EntityDataSerializers.BYTE, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, EntityDataSerializers.INT, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(null));
            RegistryOps<JsonElement> ops = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            writeData(buf, 23, EntityDataSerializers.COMPONENT, (Component)ComponentSerialization.CODEC.parse(ops, JsonParser.parseString(text)).getOrThrow());
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> writeData(buf, 24, EntityDataSerializers.INT, value), force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 25, EntityDataSerializers.INT, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 26, EntityDataSerializers.BYTE, value), force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }

   @Override
   public ClientboundSetEntityDataPacket createDataPacket(AbstractPlayer player, boolean force) {
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
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, EntityDataSerializers.INT, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, EntityDataSerializers.VECTOR3, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, EntityDataSerializers.QUATERNION, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, EntityDataSerializers.BYTE, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, EntityDataSerializers.INT, value), force);
         this.getWrapper().getViewRange().ifDirty(value -> writeData(buf, 17, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, EntityDataSerializers.FLOAT, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(player));
            RegistryOps<JsonElement> ops = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            writeData(buf, 23, EntityDataSerializers.COMPONENT, (Component)ComponentSerialization.CODEC.parse(ops, JsonParser.parseString(text)).getOrThrow());
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> {
            if (value != null) {
               writeData(buf, 24, EntityDataSerializers.INT, value);
            }
         }, force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 25, EntityDataSerializers.INT, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 26, EntityDataSerializers.BYTE, value), force);
         return new ClientboundSetEntityDataPacket(this.getEntityId(), buf);
      }
   }
}
