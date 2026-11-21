package io.lumine.mythic.api.holograms;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import java.util.Collection;
import java.util.function.Supplier;

public interface HologramProvider {
   IHologram createHologram(String str, AbstractLocation abstractLocation, String str2);

   default IHologram createHologram(String name, AbstractEntity entity, String text, float offset) {
      return this.createHologram(name, entity.getLocation(), text);
   }

   default IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractEntity entity, String text, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      return null;
   }

   default IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractLocation location, String text, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      return null;
   }

   IHologram createHologram(AbstractEntity abstractEntity, String str, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   default IHologram createHologram(AbstractLocation location, String text, PacketTextDisplay.PacketTextDisplayBuilder options) {
      return null;
   }

   void cleanup();
}
