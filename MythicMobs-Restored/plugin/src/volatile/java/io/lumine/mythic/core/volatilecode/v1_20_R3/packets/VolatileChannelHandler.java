package io.lumine.mythic.core.volatilecode.v1_20_R3.packets;

import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicRemoveEntityPacket;
import io.lumine.mythic.bukkit.events.PlayerActionPacketEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.glow.GlowData;
import io.lumine.mythic.core.glow.GlowManager;
import io.lumine.mythic.core.volatilecode.v1_20_R3.VolatilePacketHandlerImpl;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.b;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VolatileChannelHandler extends ChannelDuplexHandler {
   private final VolatilePacketHandlerImpl manager;
   private final Player player;
   private final EntityPlayer serverPlayer;
   private final PacketInterceptor writeInterceptors;
   private final PacketInterceptor readInterceptors;

   public VolatileChannelHandler(VolatilePacketHandlerImpl manager, Player player) {
      this.manager = manager;
      this.player = player;
      this.serverPlayer = ((CraftPlayer)player).getHandle();
      this.writeInterceptors = new PacketInterceptor()
         .register(PacketPlayOutMount.class, this::handlePassengerPacket)
         .register(PacketPlayOutEntityMetadata.class, this::handleEntityDataPacket)
         .registerPost(PacketPlayOutEntityDestroy.class, packet -> {
            if (MythicBukkit.inst().isEnabled()) {
               packet.a().forEach(id -> Events.callSync(new MythicRemoveEntityPacket(id)));
            }
         });
      this.readInterceptors = new PacketInterceptor();
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (msg instanceof Packet<?> packet) {
         try {
            if (packet instanceof ClientboundBundlePacket bundle) {
               ArrayList<Packet<PacketListenerPlayOut>> list = new ArrayList<>();

               for (Packet<PacketListenerPlayOut> subPacket : bundle.a()) {
                  Packet<PacketListenerPlayOut> result = this.writeInterceptors.accept(subPacket);
                  if (result != null) {
                     list.add(result);
                     this.writeInterceptors.acceptPost(packet);
                  }
               }

               msg = new ClientboundBundlePacket(list);
               super.write(ctx, msg, promise);
            } else {
               Object var12 = this.writeInterceptors.accept(packet);
               if (var12 == null) {
                  return;
               }

               super.write(ctx, var12, promise);
               this.writeInterceptors.acceptPost(packet);
            }
         } catch (Throwable var10) {
            var10.printStackTrace();
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

   private PacketPlayOutMount handlePassengerPacket(PacketPlayOutMount packet) {
      int vehicleId = packet.d();
      Collection<Integer> additions = MythicBukkit.inst().getHologramManager().getAttachedHolograms(vehicleId);
      if (additions == null) {
         return packet;
      } else {
         int[] oldPassengers = packet.a();
         List<Integer> passengerList = Arrays.stream(oldPassengers).boxed().collect(Collectors.toList());
         passengerList.addAll(additions);
         PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
         mount.c(vehicleId);
         mount.c(passengerList.size());

         for (Integer i : passengerList) {
            mount.c(i);
         }

         return new PacketPlayOutMount(mount);
      }
   }

   private PacketPlayOutEntityMetadata handleEntityDataPacket(PacketPlayOutEntityMetadata packet) {
      GlowManager glowManager = MythicBukkit.inst().getGlowManager();
      GlowData glowData = glowManager.getGlowData(packet.a());
      if (glowData == null) {
         return packet;
      } else {
         b<?> data = (b<?>)packet.d().getFirst();
         if (data.a() != 0) {
            return packet;
         } else {
            byte dataValue = (Byte)data.c();
            boolean internalGlowValue = (dataValue & 64) == 64;
            boolean currentGlowValue = glowData.data().containsKey(this.player.getUniqueId());
            if (internalGlowValue == currentGlowValue) {
               return packet;
            } else {
               byte newValue = (byte)(currentGlowValue ? dataValue | 64 : dataValue & -65);
               packet.d().set(0, b.a(new DataWatcherObject(0, DataWatcherRegistry.a), newValue));
               return packet;
            }
         }
      }
   }

   public void enablePlayerActionEvent() {
      this.readInterceptors.register(PacketPlayInBlockDig.class, this::handlePlayerAction);
   }

   private PacketPlayInBlockDig handlePlayerAction(PacketPlayInBlockDig packet) {
      PlayerActionPacketEvent.Action action = PlayerActionPacketEvent.Action.valueOf(packet.e().toString());
      AbstractLocation location = new AbstractLocation(
         BukkitAdapter.adapt(this.player.getWorld()), (double)packet.a().u(), (double)packet.a().v(), (double)packet.a().u()
      );
      AbstractDirection direction = AbstractDirection.valueOf(packet.d().toString().toUpperCase());
      PlayerActionPacketEvent event = new PlayerActionPacketEvent(this.player, action, location, direction);
      Events.call(event);
      return event.isCancelled() ? null : packet;
   }
}
