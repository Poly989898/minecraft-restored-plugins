package com.ticxo.modelengine.v26_3.network.patch;

import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

@Getter
public abstract class RelayedPacketWrapper<T extends Packet<ServerGamePacketListener>> implements Packet<ServerGamePacketListener> {
   private final int relayedId;
   private final T original;

   protected RelayedPacketWrapper(int relayedId, T original) {
      this.relayedId = relayedId;
      this.original = original;
   }

   abstract int getOriginalId();
}
