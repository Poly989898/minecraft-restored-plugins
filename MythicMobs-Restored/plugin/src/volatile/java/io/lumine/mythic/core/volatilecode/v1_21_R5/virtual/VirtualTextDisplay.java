package io.lumine.mythic.core.volatilecode.v1_21_R5.virtual;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.List;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.c;
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
   public PacketPlayOutSpawnEntity createSpawnPacket() {
      return this.createSpawnPacket(EntityTypes.bx);
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
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, DataWatcherRegistry.b, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, DataWatcherRegistry.H, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, DataWatcherRegistry.H, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, DataWatcherRegistry.I, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, DataWatcherRegistry.I, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, DataWatcherRegistry.a, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, DataWatcherRegistry.b, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, DataWatcherRegistry.d, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, DataWatcherRegistry.d, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(null));
            RegistryOps<JsonElement> ops = MinecraftServer.getServer().ba().a(JsonOps.INSTANCE);
            writeData(buf, 23, DataWatcherRegistry.f, (IChatBaseComponent)ComponentSerialization.a.parse(ops, JsonParser.parseString(text)).getOrThrow());
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> writeData(buf, 24, DataWatcherRegistry.b, value), force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 25, DataWatcherRegistry.b, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 26, DataWatcherRegistry.a, value), force);
         return new PacketPlayOutEntityMetadata(this.getEntityId(), buf);
      }
   }

   @Override
   public PacketPlayOutEntityMetadata createDataPacket(AbstractPlayer player, boolean force) {
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
         this.getWrapper().getInterpolationDuration().ifDirty(value -> writeData(buf, 9, DataWatcherRegistry.b, value), force);
         this.getWrapper().getTranslation().ifDirty(vector3f -> writeData(buf, 11, DataWatcherRegistry.H, vector3f), force);
         this.getWrapper().getScale().ifDirty(vector3f -> writeData(buf, 12, DataWatcherRegistry.H, vector3f), force);
         this.getWrapper().getRotationLeft().ifDirty(value -> writeData(buf, 13, DataWatcherRegistry.I, value), force);
         this.getWrapper().getRotationRight().ifDirty(value -> writeData(buf, 14, DataWatcherRegistry.I, value), force);
         this.getWrapper().getBillboard().ifDirty(value -> writeData(buf, 15, DataWatcherRegistry.a, (byte)value.ordinal()), force);
         this.getWrapper().getBrightness().ifDirty(value -> writeData(buf, 16, DataWatcherRegistry.b, value), force);
         this.getWrapper().getViewRange().ifDirty(value -> writeData(buf, 17, DataWatcherRegistry.d, value), force);
         this.getWrapper().getWidth().ifDirty(value -> writeData(buf, 20, DataWatcherRegistry.d, value), force);
         this.getWrapper().getHeight().ifDirty(value -> writeData(buf, 21, DataWatcherRegistry.d, value), force);
         this.getWrapper().getText().ifDirty(value -> {
            String text = Text.toJson(value.apply(player));
            RegistryOps<JsonElement> ops = MinecraftServer.getServer().ba().a(JsonOps.INSTANCE);
            writeData(buf, 23, DataWatcherRegistry.f, (IChatBaseComponent)ComponentSerialization.a.parse(ops, JsonParser.parseString(text)).getOrThrow());
         }, force);
         this.getWrapper().getLineWidth().ifDirty(value -> {
            if (value != null) {
               writeData(buf, 24, DataWatcherRegistry.b, value);
            }
         }, force);
         this.getWrapper().getBackgroundColor().ifDirty(value -> writeData(buf, 25, DataWatcherRegistry.b, value.asARGB()), force);
         this.getWrapper().getTextOpacity().ifDirty(value -> writeData(buf, 26, DataWatcherRegistry.a, value), force);
         return new PacketPlayOutEntityMetadata(this.getEntityId(), buf);
      }
   }
}
