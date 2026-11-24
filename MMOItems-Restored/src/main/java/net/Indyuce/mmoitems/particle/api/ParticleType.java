package net.Indyuce.mmoitems.particle.api;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.StringValue;
import net.Indyuce.mmoitems.particle.AuraParticles;
import net.Indyuce.mmoitems.particle.DoubleRingsParticles;
import net.Indyuce.mmoitems.particle.FirefliesParticles;
import net.Indyuce.mmoitems.particle.GalaxyParticles;
import net.Indyuce.mmoitems.particle.HelixParticles;
import net.Indyuce.mmoitems.particle.OffsetParticles;
import net.Indyuce.mmoitems.particle.VortexParticles;
import net.Indyuce.mmoitems.stat.data.ParticleData;

public enum ParticleType {
   OFFSET(
      OffsetParticles::new,
      false,
      5L,
      "Some particles randomly spawning around your body.",
      new StringValue("amount", 5.0),
      new StringValue("vertical-offset", 0.5),
      new StringValue("horizontal-offset", 0.3),
      new StringValue("speed", 0.0),
      new StringValue("height", 1.0)
   ),
   FIREFLIES(
      FirefliesParticles::new,
      true,
      1L,
      "Particles dashing around you at the same height.",
      new StringValue("amount", 3.0),
      new StringValue("speed", 0.0),
      new StringValue("rotation-speed", 1.0),
      new StringValue("radius", 1.3),
      new StringValue("height", 1.0)
   ),
   VORTEX(
      VortexParticles::new,
      true,
      1L,
      "Particles flying around you in a cone shape.",
      new StringValue("radius", 1.5),
      new StringValue("height", 2.4),
      new StringValue("speed", 0.0),
      new StringValue("y-speed", 1.0),
      new StringValue("rotation-speed", 1.0),
      new StringValue("amount", 3.0)
   ),
   GALAXY(
      GalaxyParticles::new,
      true,
      1L,
      "Particles flying around you in spiral arms.",
      new StringValue("height", 1.0),
      new StringValue("speed", 1.0),
      new StringValue("y-coord", 0.0),
      new StringValue("rotation-speed", 1.0),
      new StringValue("amount", 6.0)
   ),
   DOUBLE_RINGS(
      DoubleRingsParticles::new,
      true,
      1L,
      "Particles drawing two rings around you.",
      new StringValue("radius", 0.8),
      new StringValue("y-offset", 0.4),
      new StringValue("height", 1.0),
      new StringValue("speed", 0.0),
      new StringValue("rotation-speed", 1.0)
   ),
   HELIX(
      HelixParticles::new,
      true,
      1L,
      "Particles drawing a sphere around you.",
      new StringValue("radius", 0.8),
      new StringValue("height", 0.6),
      new StringValue("rotation-speed", 1.0),
      new StringValue("y-speed", 1.0),
      new StringValue("amount", 4.0),
      new StringValue("speed", 0.0)
   ),
   AURA(
      AuraParticles::new,
      true,
      1L,
      "Particles dashing around you (height can differ).",
      new StringValue("amount", 3.0),
      new StringValue("speed", 0.0),
      new StringValue("rotation-speed", 1.0),
      new StringValue("y-speed", 1.0),
      new StringValue("y-offset", 0.7),
      new StringValue("radius", 1.3),
      new StringValue("height", 1.0)
   );

   private final BiFunction<ParticleData, PlayerData, ParticleRunnable> func;
   private final boolean override;
   private final long period;
   private final String lore;
   private final Map<String, Double> modifiers = new HashMap<>();

   ParticleType(BiFunction<ParticleData, PlayerData, ParticleRunnable> nullxx, boolean nullxxx, long nullxxxx, String nullxxxxx, StringValue... nullxxxxxx) {
      this.func = nullxx;
      this.override = nullxxx;
      this.period = nullxxxx;
      this.lore = nullxxxxx;

      for (StringValue var12 : nullxxxxxx) {
         this.modifiers.put(var12.getName(), var12.getValue());
      }
   }

   public String getDefaultName() {
      return UtilityMethods.caseOnWords(this.name().toLowerCase().replace("_", " "));
   }

   public double getModifier(String var1) {
      return this.modifiers.get(var1);
   }

   public Set<String> getModifiers() {
      return this.modifiers.keySet();
   }

   public String getDescription() {
      return this.lore;
   }

   public boolean hasPriority() {
      return this.override;
   }

   public long getTime() {
      return this.period;
   }

   public ParticleRunnable newRunnable(ParticleData var1, PlayerData var2) {
      return this.func.apply(var1, var2);
   }
}
