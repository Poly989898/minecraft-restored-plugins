package com.ticxo.modelengine.v26_3.network.patch;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import org.jetbrains.annotations.NotNull;

public class ServerboundAttackPacketWrapper extends RelayedPacketWrapper<ServerboundAttackPacket> {
   public ServerboundAttackPacketWrapper(int relayedId, ServerboundAttackPacket original) {
      super(relayedId, original);
   }

   @NotNull
   public PacketType<? extends Packet<ServerGamePacketListener>> type() {
      return GamePacketTypes.SERVERBOUND_ATTACK;
   }

   public void handle(ServerGamePacketListener listener) {
      PatchedServerGamePacketListener.handleAttack(this, listener);
   }

   @Override
   public int getOriginalId() {
      return this.getOriginal().entityId();
   }
}
