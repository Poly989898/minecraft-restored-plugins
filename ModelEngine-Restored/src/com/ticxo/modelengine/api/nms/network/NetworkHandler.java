package com.ticxo.modelengine.api.nms.network;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface NetworkHandler {
   int getProtocolVersion();

   Optional<PipelineWrapper> getPipeline(UUID uuid);

   void removePipeline(UUID uuid);

   void injectChannel(Player player);

   void ejectChannel(Player player);

   void ping(UUID uuid);

   void startBatch();

   boolean isBatching();

   void endBatch();
}
