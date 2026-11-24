package net.Indyuce.mmocore.loot.chest;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.loot.chest.particle.ChestParticleEffect;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class TierEffect {
   private final ChestParticleEffect type;
   private final Particle particle;
   private final int period;

   public TierEffect(ConfigurationSection config) {
      Validate.notNull(config, "Could not load tier config");
      this.type = ChestParticleEffect.valueOf(config.getString("type", "OFFSET").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.particle = Particle.valueOf(config.getString("particle", "FLAME").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.period = Math.max(20, config.getInt("period", 100));
   }

   public void play(Location loc) {
      this.type.play(loc, this.particle);
   }

   public BukkitRunnable startNewRunnable(final Location loc) {
      BukkitRunnable runnable = new BukkitRunnable() {
         public void run() {
            TierEffect.this.type.play(loc, TierEffect.this.particle);
         }
      };
      runnable.runTaskTimer(MMOCore.plugin, 0L, this.period);
      return runnable;
   }
}
