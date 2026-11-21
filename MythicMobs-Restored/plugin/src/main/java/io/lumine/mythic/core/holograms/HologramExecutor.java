package io.lumine.mythic.core.holograms;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.holograms.HologramProvider;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.holograms.types.CastBar;
import io.lumine.mythic.core.holograms.types.HealthBar;
import io.lumine.mythic.core.holograms.types.Nameplate;
import io.lumine.mythic.core.holograms.types.SpeechBubble;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.mechanics.CastMechanic;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Getter;

public class HologramExecutor implements HologramManager {
   @Getter
   private MythicBukkit plugin;
   private HologramProvider provider;
   private Map<Integer, Collection<Integer>> attachedHolograms = Maps.newConcurrentMap();

   public HologramExecutor(MythicBukkit plugin) {
      this.plugin = plugin;
      this.provider = new DisplayHologramProvider(this);
   }

   public boolean isActive() {
      return this.provider != null;
   }

   @Override
   public IHologram createHologram(String name, AbstractLocation location) {
      return this.provider == null ? null : this.provider.createHologram(name, location, " ");
   }

   @Override
   public IHologram createHologram(String name, AbstractLocation location, String text) {
      return this.provider == null ? null : this.provider.createHologram(name, location, text);
   }

   @Override
   public IHologram createHologram(AbstractEntity entity, PacketTextDisplay.PacketTextDisplayBuilder options) {
      return this.provider == null ? null : this.provider.createHologram(entity, " ", options);
   }

   @Override
   public IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractEntity entity, String s, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      return this.provider == null ? null : this.provider.createHologram(audience, entity, " ", options);
   }

   @Override
   public IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractLocation location, String s, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      return this.provider == null ? null : this.provider.createHologram(audience, location, " ", options);
   }

   @Override
   public IHologram createHologram(AbstractEntity entity, String text, PacketTextDisplay.PacketTextDisplayBuilder options) {
      return this.provider == null ? null : this.provider.createHologram(entity, text, options);
   }

   @Override
   public IHologram createHologram(AbstractLocation location, String text, PacketTextDisplay.PacketTextDisplayBuilder options) {
      return this.provider == null ? null : this.provider.createHologram(location, text, options);
   }

   @Override
   public Nameplate createNameplate(ActiveMob activeMob) {
      return this.provider == null ? null : new Nameplate(this, activeMob);
   }

   @Override
   public HealthBar createHealthBar(ActiveMob activeMob) {
      return this.provider == null ? null : new HealthBar(this, activeMob);
   }

   @Override
   public SpeechBubble createSpeechBubble(
      SkillCaster skillCaster, Supplier<Collection<AbstractPlayer>> audience, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      return this.provider == null ? null : new SpeechBubble(this, skillCaster, audience, options);
   }

   @Override
   public CastBar createCastBar(CastMechanic.CastTracker castTracker, String castingText) {
      return this.provider == null ? null : new CastBar(this, castTracker, castingText);
   }

   @Override
   public void registerAttachedHologram(int entity, int hologram) {
      Collection<Integer> collection = this.attachedHolograms.computeIfAbsent(entity, x -> Sets.newConcurrentHashSet());
      collection.add(hologram);
   }

   @Override
   public void unregisterAttachedHologram(int entity, int hologram) {
      Collection<Integer> collection = this.attachedHolograms.get(entity);
      if (collection != null) {
         collection.remove(hologram);
         if (collection.isEmpty()) {
            this.attachedHolograms.remove(entity);
         }
      }
   }

   @Override
   public Collection<Integer> getAttachedHolograms(int vehicleId) {
      return this.attachedHolograms.getOrDefault(vehicleId, null);
   }
}
