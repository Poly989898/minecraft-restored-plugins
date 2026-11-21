package io.lumine.mythic.core.volatilecode.v1_21_R6.packets;

import com.mojang.datafixers.util.Pair;
import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPacketItemDataEvent;
import io.lumine.mythic.bukkit.events.MythicRemoveEntityPacket;
import io.lumine.mythic.bukkit.events.PlayerActionPacketEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.glow.GlowData;
import io.lumine.mythic.core.glow.GlowManager;
import io.lumine.mythic.core.volatilecode.v1_21_R6.VolatilePacketHandlerImpl;
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
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
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
               packet.b().forEach(id -> Events.callSync(new MythicRemoveEntityPacket(id)));
            }
         });
      this.readInterceptors = new PacketInterceptor();
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (msg instanceof Packet<?> packet) {
         try {
            if (packet instanceof ClientboundBundlePacket bundle) {
               ArrayList<Packet<? super PacketListenerPlayOut>> list = new ArrayList<>();

               for (Packet<? super PacketListenerPlayOut> subPacket : bundle.b()) {
                  Packet<? super PacketListenerPlayOut> result = this.writeInterceptors.accept(subPacket);
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
      int vehicleId = packet.e();
      Collection<Integer> additions = MythicBukkit.inst().getHologramManager().getAttachedHolograms(vehicleId);
      if (additions == null) {
         return packet;
      } else {
         int[] oldPassengers = packet.b();
         List<Integer> passengerList = Arrays.stream(oldPassengers).boxed().collect(Collectors.toList());
         passengerList.addAll(additions);
         PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
         mount.c(vehicleId);
         mount.c(passengerList.size());

         for (Integer i : passengerList) {
            mount.c(i);
         }

         return (PacketPlayOutMount)PacketPlayOutMount.a.decode(mount);
      }
   }

   private PacketPlayOutEntityMetadata handleEntityDataPacket(PacketPlayOutEntityMetadata packet) {
      GlowManager glowManager = MythicBukkit.inst().getGlowManager();
      GlowData glowData = glowManager.getGlowData(packet.b());
      if (glowData == null) {
         return packet;
      } else {
         c<?> data = (c<?>)packet.e().getFirst();
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
               List<c<?>> packedItems = new ArrayList<>(packet.e());
               packet = new PacketPlayOutEntityMetadata(packet.b(), packedItems);
               packet.e().set(0, c.a(new DataWatcherObject(0, DataWatcherRegistry.a), newValue));
               return packet;
            }
         }
      }
   }

   private void handlePlayerInput(PacketPlayInSteerVehicle packet) {
   }

   public void enablePlayerActionEvent() {
      this.readInterceptors.register(PacketPlayInBlockDig.class, this::handlePlayerAction);
   }

   public void enablePacketItemDataEvent() {
      this.writeInterceptors
         .register(PacketPlayOutSetSlot.class, this::handleContainerSetSlotPacket)
         .register(PacketPlayOutWindowItems.class, this::handleContainerSetContentPacket)
         .register(ClientboundSetCursorItemPacket.class, this::handleSetCursorItemPacket)
         .register(ClientboundSetPlayerInventoryPacket.class, this::handleSetPlayerInventoryPacket)
         .register(PacketPlayOutEntityEquipment.class, this::handleSetEquipmentPacket);
   }

   private PacketPlayOutSetSlot handleContainerSetSlotPacket(PacketPlayOutSetSlot packet) {
      ItemStack original = packet.f();
      ItemStack edited = this.handleItemStack(original);
      return edited == original ? packet : new PacketPlayOutSetSlot(packet.b(), packet.g(), packet.e(), edited);
   }

   private PacketPlayOutWindowItems handleContainerSetContentPacket(PacketPlayOutWindowItems packet) {
      boolean changed = false;
      ArrayList<ItemStack> items = new ArrayList<>(packet.f().size());

      for (ItemStack original : packet.f()) {
         ItemStack edited = this.handleItemStack(original);
         items.add(edited);
         changed |= edited != original;
      }

      ItemStack originalCarriedItem = packet.g();
      ItemStack carriedItem = this.handleItemStack(originalCarriedItem);
      changed |= carriedItem != originalCarriedItem;
      return !changed ? packet : new PacketPlayOutWindowItems(packet.b(), packet.e(), items, carriedItem);
   }

   private ClientboundSetCursorItemPacket handleSetCursorItemPacket(ClientboundSetCursorItemPacket packet) {
      ItemStack original = packet.b();
      ItemStack edited = this.handleItemStack(original);
      return edited == original ? packet : new ClientboundSetCursorItemPacket(edited);
   }

   private ClientboundSetPlayerInventoryPacket handleSetPlayerInventoryPacket(ClientboundSetPlayerInventoryPacket packet) {
      ItemStack original = packet.e();
      ItemStack edited = this.handleItemStack(original);
      return edited == original ? packet : new ClientboundSetPlayerInventoryPacket(packet.b(), edited);
   }

   private PacketPlayOutEntityEquipment handleSetEquipmentPacket(PacketPlayOutEntityEquipment packet) {
      boolean changed = false;
      ArrayList<Pair<EnumItemSlot, ItemStack>> slots = new ArrayList<>(packet.e().size());

      for (Pair<EnumItemSlot, ItemStack> entry : packet.e()) {
         ItemStack original = (ItemStack)entry.getSecond();
         ItemStack edited = this.handleItemStack(original);
         slots.add(Pair.of((EnumItemSlot)entry.getFirst(), edited));
         changed |= edited != original;
      }

      return !changed ? packet : new PacketPlayOutEntityEquipment(packet.b(), slots);
   }

   private ItemStack handleItemStack(ItemStack itemStack) {
      if (itemStack != null && !itemStack.f()) {
         MythicPacketItemDataEvent event = new MythicPacketItemDataEvent(this.player, itemStack.asBukkitCopy());
         Events.call(event);
         return !event.wasEdited() ? itemStack : CraftItemStack.asNMSCopy(event.getItemStack());
      } else {
         return itemStack;
      }
   }

   private PacketPlayInBlockDig handlePlayerAction(PacketPlayInBlockDig packet) {
      PlayerActionPacketEvent.Action action = PlayerActionPacketEvent.Action.valueOf(packet.f().toString());
      AbstractLocation location = new AbstractLocation(
         BukkitAdapter.adapt(this.player.getWorld()), (double)packet.b().u(), (double)packet.b().v(), (double)packet.b().u()
      );
      AbstractDirection direction = AbstractDirection.valueOf(packet.e().toString().toUpperCase());
      PlayerActionPacketEvent event = new PlayerActionPacketEvent(this.player, action, location, direction);
      Events.call(event);
      return event.isCancelled() ? null : packet;
   }
}
