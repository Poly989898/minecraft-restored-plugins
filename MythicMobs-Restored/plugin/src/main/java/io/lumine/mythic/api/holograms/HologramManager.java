package io.lumine.mythic.api.holograms;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.core.holograms.types.CastBar;
import io.lumine.mythic.core.holograms.types.HealthBar;
import io.lumine.mythic.core.holograms.types.Nameplate;
import io.lumine.mythic.core.holograms.types.SpeechBubble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.mechanics.CastMechanic;
import java.util.Collection;
import java.util.function.Supplier;

public interface HologramManager {
   IHologram createHologram(String str, AbstractLocation abstractLocation);

   IHologram createHologram(String str, AbstractLocation abstractLocation, String str2);

   IHologram createHologram(AbstractEntity abstractEntity, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   IHologram createHologram(Supplier<Collection<AbstractPlayer>> supplier, AbstractEntity abstractEntity, String str, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   IHologram createHologram(Supplier<Collection<AbstractPlayer>> supplier, AbstractLocation abstractLocation, String str, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   IHologram createHologram(AbstractEntity abstractEntity, String str, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   IHologram createHologram(AbstractLocation abstractLocation, String str, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   Nameplate createNameplate(ActiveMob activeMob);

   HealthBar createHealthBar(ActiveMob activeMob);

   SpeechBubble createSpeechBubble(SkillCaster skillCaster, Supplier<Collection<AbstractPlayer>> supplier, PacketTextDisplay.PacketTextDisplayBuilder packetTextDisplayBuilder);

   CastBar createCastBar(CastMechanic.CastTracker castTracker, String str);

   void registerAttachedHologram(int value, int value2);

   void unregisterAttachedHologram(int value, int value2);

   Collection<Integer> getAttachedHolograms(int value);
}
