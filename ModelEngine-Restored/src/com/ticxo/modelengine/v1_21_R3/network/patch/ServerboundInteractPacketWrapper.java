package com.ticxo.modelengine.v1_21_R3.network.patch;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket.Handler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerboundInteractPacketWrapper implements Packet<ServerGamePacketListener> {
   @Getter
   private final int originalId;
   @Getter
   private final int relayedId;
   @Getter
   private final ServerboundInteractPacket original;
   private final boolean isAttack;

   public ServerboundInteractPacketWrapper(int originalId, int relayedId, int action, ServerboundInteractPacket original) {
      this.originalId = originalId;
      this.relayedId = relayedId;
      this.original = original;
      this.isAttack = action == 1;
   }

   @NotNull
   public PacketType<? extends Packet<ServerGamePacketListener>> type() {
      return GamePacketTypes.SERVERBOUND_INTERACT;
   }

   public int getEntityId() {
      return this.originalId;
   }

   public boolean isAttack() {
      return this.isAttack;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      PatchedServerGamePacketListener.handleInteract(this, serverGamePacketListener);
   }

   @Nullable
   public Entity getTarget(ServerLevel serverLevel) {
      return serverLevel.getEntityOrPart(this.relayedId);
   }

   public boolean isFakeInteraction() {
      InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
      return tracker.getModelRelay(this.originalId) != null || tracker.getEntityRelay(this.originalId) != null;
   }

   public boolean isUsingSecondaryAction() {
      return this.original.isUsingSecondaryAction();
   }

   public void dispatch(Handler handler) {
      this.original.dispatch(handler);
   }
}
