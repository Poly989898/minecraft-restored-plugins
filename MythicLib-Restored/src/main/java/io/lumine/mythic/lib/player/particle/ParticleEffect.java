package io.lumine.mythic.lib.player.particle;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.PlayerModifier;
import io.lumine.mythic.lib.player.particle.type.AuraParticleEffect;
import io.lumine.mythic.lib.player.particle.type.DoubleRingsParticleEffect;
import io.lumine.mythic.lib.player.particle.type.FirefliesParticleEffect;
import io.lumine.mythic.lib.player.particle.type.GalaxyParticleEffect;
import io.lumine.mythic.lib.player.particle.type.HelixParticleEffect;
import io.lumine.mythic.lib.player.particle.type.OffsetParticleEffect;
import io.lumine.mythic.lib.player.particle.type.VortexParticleEffect;
import io.lumine.mythic.lib.util.Closeable;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public abstract class ParticleEffect extends PlayerModifier implements Closeable {
   private final ParticleInformation particleUsed;
   private BukkitRunnable particleRunnable;
   private static final Map<String, Function<ConfigObject, ParticleEffect>> BY_NAME = new HashMap<>();

   public ParticleEffect(ConfigObject obj) {
      super(obj.getString("key"), EquipmentSlot.OTHER, ModifierSource.OTHER);
      this.particleUsed = new ParticleInformation(obj.getObject("particle"));
   }

   public int getTick() {
      return 1;
   }

   public ParticleInformation getParticle() {
      return this.particleUsed;
   }

   public abstract void tick(Player player);

   @Override
   public void register(MMOPlayerData playerData) {
      playerData.getParticleEffectMap().addModifier(this);
   }

   @Override
   public void unregister(MMOPlayerData playerData) {
      playerData.getParticleEffectMap().removeModifier(this.getUniqueId());
   }

   @Override
   public void close() {
      this.particleRunnable.cancel();
   }

   public static ParticleEffect fromConfig(ConfigObject obj) {
      Function<ConfigObject, ParticleEffect> configReader = BY_NAME.get(obj.getString("particle-effect"));
      Validate.notNull(configReader, "Could not find particle effect type with ID '" + obj.getString("particle-effect") + "'");
      return configReader.apply(obj);
   }

   public static void registerParticleEffectType(String id, Function<ConfigObject, ParticleEffect> configReader) {
      Validate.notNull(id, "Identifier cannot be null");
      Validate.notNull(configReader, "Function cannot be null");
      BY_NAME.put(id, configReader);
   }

   static {
      registerParticleEffectType("AURA", obj -> new AuraParticleEffect(obj));
      registerParticleEffectType("DOUBLE_RINGS", obj -> new DoubleRingsParticleEffect(obj));
      registerParticleEffectType("FIREFLIES", obj -> new FirefliesParticleEffect(obj));
      registerParticleEffectType("GALAXY", obj -> new GalaxyParticleEffect(obj));
      registerParticleEffectType("HELIX", obj -> new HelixParticleEffect(obj));
      registerParticleEffectType("OFFSET", obj -> new OffsetParticleEffect(obj));
      registerParticleEffectType("VORTEX", obj -> new VortexParticleEffect(obj));
      registerParticleEffectType("AURA", obj -> new AuraParticleEffect(obj));
   }
}
