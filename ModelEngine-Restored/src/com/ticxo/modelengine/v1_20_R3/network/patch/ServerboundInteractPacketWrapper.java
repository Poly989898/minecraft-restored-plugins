package com.ticxo.modelengine.v1_20_R3.network.patch;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import lombok.Getter;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketListenerPlayIn;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity.c;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Getter
public class ServerboundInteractPacketWrapper extends PacketPlayInUseEntity {
   private final int originalId;
   private final int relayedId;
   private final PacketPlayInUseEntity original;

   public ServerboundInteractPacketWrapper(int originalId, int relayedId, PacketPlayInUseEntity original) {
      super(NetworkUtils.readData(original));
      this.originalId = originalId;
      this.relayedId = relayedId;
      this.original = original;
   }

   public void a(PacketDataSerializer packetDataSerializer) {
      this.original.a(packetDataSerializer);
   }

   public void a(PacketListenerPlayIn packetListenerPlayIn) {
      PatchedServerGamePacketListener.handleInteract(this, packetListenerPlayIn);
   }

   @Nullable
   public Entity a(WorldServer worldServer) {
      return worldServer.b(this.relayedId);
   }

   public boolean isFakeInteraction() {
      InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
      return tracker.getModelRelay(this.originalId) != null || tracker.getEntityRelay(this.originalId) != null;
   }

   public boolean a() {
      return this.original.a();
   }

   public void a(c c2) {
      this.original.a(c2);
   }
}
