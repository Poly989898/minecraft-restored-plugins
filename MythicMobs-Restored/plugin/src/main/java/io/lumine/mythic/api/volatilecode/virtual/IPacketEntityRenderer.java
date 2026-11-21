package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public interface IPacketEntityRenderer<T extends PacketEntity> {
   void spawnWithMount(AbstractPlayer abstractPlayer, PacketEntityRenderer packetEntityRenderer);

   void spawn();

   void spawn(Supplier<Collection<AbstractPlayer>> supplier);

   void spawn(AbstractWorld abstractWorld);

   void spawn(Collection<AbstractPlayer> abstractPlayers);

   void spawn(AbstractPlayer abstractPlayer);

   void spawnWithMount(Supplier<Collection<AbstractPlayer>> supplier, PacketEntityRenderer packetEntityRenderer);

   void spawnWithMount(Collection<AbstractPlayer> abstractPlayers, PacketEntityRenderer packetEntityRenderer);

   void spawnWithMount(Supplier<Collection<AbstractPlayer>> supplier, AbstractPlayer abstractPlayer);

   void spawnWithMount(AbstractPlayer abstractPlayer);

   void setCullingDistance(int value);

   void update();

   void destroy(AbstractPlayer abstractPlayer);

   void destroy();

   void mountEntity(AbstractEntity abstractEntity);

   void addPassenger(PacketEntityRenderer packetEntityRenderer);

   void setMount(int value);

   int getEntityId();

   UUID getUniqueId();

   T getWrapper();

   Map<AbstractPlayer, Boolean> getTrackedPlayers();

   default void updateRenderedPlayers() {
      this.updateRenderedPlayers(true);
   }

   void updateRenderedPlayers(boolean flag);
}
