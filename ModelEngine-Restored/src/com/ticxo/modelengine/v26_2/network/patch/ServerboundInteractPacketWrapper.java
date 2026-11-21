package com.ticxo.modelengine.v26_2.network.patch;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ServerboundInteractPacketWrapper extends RelayedPacketWrapper<ServerboundInteractPacket> {
   public ServerboundInteractPacketWrapper(int relayedId, ServerboundInteractPacket original) {
      super(relayedId, original);
   }

   @NotNull
   public PacketType<? extends Packet<ServerGamePacketListener>> type() {
      return GamePacketTypes.SERVERBOUND_INTERACT;
   }

   public void handle(ServerGamePacketListener listener) {
      PatchedServerGamePacketListener.handleInteract(this, listener);
   }

   @Override
   public int getOriginalId() {
      return this.getOriginal().entityId();
   }

   public InteractionHand getHand() {
      return this.getOriginal().hand();
   }

   public boolean isUsingSecondaryAction() {
      return this.getOriginal().usingSecondaryAction();
   }

   public Vec3 getLocation() {
      return this.getOriginal().location();
   }

   public boolean isFakeInteraction() {
      InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
      return tracker.getModelRelay(this.getOriginalId()) != null || tracker.getEntityRelay(this.getOriginalId()) != null;
   }
}
