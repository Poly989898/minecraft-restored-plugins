package io.lumine.mythic.core.volatilecode.v26_1.packets;

import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicRemoveEntityPacket;
import io.lumine.mythic.bukkit.events.PlayerActionPacketEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.glow.GlowData;
import io.lumine.mythic.core.glow.GlowManager;
import io.lumine.mythic.core.volatilecode.v26_1.VolatilePacketHandlerImpl;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VolatileChannelHandler extends ChannelDuplexHandler {
   private final VolatilePacketHandlerImpl manager;
   private final Player player;
   private final ServerPlayer serverPlayer;
   private final PacketInterceptor writeInterceptors;
   private final PacketInterceptor readInterceptors;

   public VolatileChannelHandler(VolatilePacketHandlerImpl manager, Player player) {
      this.manager = manager;
      this.player = player;
      this.serverPlayer = ((CraftPlayer)player).getHandle();
      this.writeInterceptors = new PacketInterceptor()
         .register(ClientboundSetPassengersPacket.class, this::handlePassengerPacket)
         .register(ClientboundSetEntityDataPacket.class, this::handleEntityDataPacket)
         .registerPost(ClientboundRemoveEntitiesPacket.class, packet -> {
            if (MythicBukkit.inst().isEnabled()) {
               packet.getEntityIds().forEach(id -> Events.callSync(new MythicRemoveEntityPacket(id)));
            }
         });
      this.readInterceptors = new PacketInterceptor();
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (msg instanceof Packet<?> packet) {
         try {
            if (packet instanceof ClientboundBundlePacket bundle) {
               ArrayList<Packet<? super ClientGamePacketListener>> list = new ArrayList<>();

               for (Packet<? super ClientGamePacketListener> subPacket : bundle.subPackets()) {
                  Packet<? super ClientGamePacketListener> result = this.writeInterceptors.accept(subPacket);
                  if (result != null) {
                     list.add(result);
                     this.writeInterceptors.acceptPost(packet);
                  }
               }

               msg = new ClientboundBundlePacket(list);
               super.write(ctx, msg, promise);
            } else {
               Object obj = this.writeInterceptors.accept(packet);
               if (obj == null) {
                  return;
               }

               super.write(ctx, obj, promise);
               this.writeInterceptors.acceptPost(packet);
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }
      } else {
         super.write(ctx, msg, promise);
      }
   }

   public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
      if (msg instanceof Packet<?> packet) {
         msg = this.readInterceptors.accept(packet);
         if (msg != null) {
            super.channelRead(ctx, msg);
            this.readInterceptors.acceptPost(packet);
         }
      } else {
         super.channelRead(ctx, msg);
      }
   }

   private ClientboundSetPassengersPacket handlePassengerPacket(ClientboundSetPassengersPacket packet) {
      int vehicleId = packet.getVehicle();
      Collection<Integer> additions = MythicBukkit.inst().getHologramManager().getAttachedHolograms(vehicleId);
      if (additions == null) {
         return packet;
      } else {
         int[] oldPassengers = packet.getPassengers();
         List<Integer> passengerList = Arrays.stream(oldPassengers).boxed().collect(Collectors.toList());
         passengerList.addAll(additions);
         FriendlyByteBuf mount = new FriendlyByteBuf(Unpooled.buffer());
         mount.writeVarInt(vehicleId);
         mount.writeVarInt(passengerList.size());

         for (Integer i : passengerList) {
            mount.writeVarInt(i);
         }

         return ClientboundSetPassengersPacket.STREAM_CODEC.decode(mount);
      }
   }

   private ClientboundSetEntityDataPacket handleEntityDataPacket(ClientboundSetEntityDataPacket packet) {
      GlowManager glowManager = MythicBukkit.inst().getGlowManager();
      GlowData glowData = glowManager.getGlowData(packet.id());
      if (glowData == null) {
         return packet;
      } else {
         DataValue<?> data = (DataValue<?>)packet.packedItems().getFirst();
         if (data.id() != 0) {
            return packet;
         } else {
            byte dataValue = (Byte)data.value();
            boolean internalGlowValue = (dataValue & 64) == 64;
            boolean currentGlowValue = glowData.data().containsKey(this.player.getUniqueId());
            if (internalGlowValue == currentGlowValue) {
               return packet;
            } else {
               byte newValue = (byte)(currentGlowValue ? dataValue | 64 : dataValue & -65);
               List<DataValue<?>> packedItems = new ArrayList<>(packet.packedItems());
               packet = new ClientboundSetEntityDataPacket(packet.id(), packedItems);
               packet.packedItems().set(0, DataValue.create(new EntityDataAccessor(0, EntityDataSerializers.BYTE), newValue));
               return packet;
            }
         }
      }
   }

   private void handlePlayerInput(ServerboundPlayerInputPacket packet) {
   }

   public void enablePlayerActionEvent() {
      this.readInterceptors.register(ServerboundPlayerActionPacket.class, this::handlePlayerAction);
   }

   private ServerboundPlayerActionPacket handlePlayerAction(ServerboundPlayerActionPacket packet) {
      PlayerActionPacketEvent.Action action = PlayerActionPacketEvent.Action.valueOf(packet.getAction().toString());
      AbstractLocation location = new AbstractLocation(
         BukkitAdapter.adapt(this.player.getWorld()), (double)packet.getPos().getX(), (double)packet.getPos().getY(), (double)packet.getPos().getX()
      );
      AbstractDirection direction = AbstractDirection.valueOf(packet.getDirection().toString().toUpperCase());
      PlayerActionPacketEvent event = new PlayerActionPacketEvent(this.player, action, location, direction);
      Events.call(event);
      return event.isCancelled() ? null : packet;
   }
}
