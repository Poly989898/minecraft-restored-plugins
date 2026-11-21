package com.ticxo.modelengine.v1_20_R1.network.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import org.jetbrains.annotations.NotNull;

public class Packets extends ArrayList<Object> {
   public Packets(int initialCapacity, float loadFactor) {
      super(initialCapacity);
   }

   public Packets(int initialCapacity) {
      super(initialCapacity);
   }

   public Packets() {
   }

   public Packets(@NotNull Collection<?> c) {
      super((Collection<? extends Object>)c);
   }

   @Override
   public boolean add(Object obj) {
      return obj == null ? false : super.add(obj);
   }

   public boolean add(Packet<PacketListenerPlayOut> packet) {
      return packet == null ? false : super.add(packet);
   }

   private Collection<Packet<PacketListenerPlayOut>> compile(UUID player) {
      Collection<Packet<PacketListenerPlayOut>> list = new ArrayList<>();

      for (Object obj : this) {
         if (obj instanceof Packet) {
            list.add((Packet<PacketListenerPlayOut>)obj);
         } else if (obj instanceof Packets.PacketSupplier supplier) {
            Packet<PacketListenerPlayOut> packet = supplier.supply(player);
            if (packet != null) {
               list.add(packet);
            }
         }
      }

      return list;
   }

   public void compile(UUID player, Consumer<Collection<Packet<PacketListenerPlayOut>>> ifNotEmpty) {
      Collection<Packet<PacketListenerPlayOut>> set = this.compile(player);
      if (!set.isEmpty()) {
         ifNotEmpty.accept(set);
      }
   }

   @FunctionalInterface
   public interface PacketSupplier {
      Packet<PacketListenerPlayOut> supply(UUID uuid);
   }
}
