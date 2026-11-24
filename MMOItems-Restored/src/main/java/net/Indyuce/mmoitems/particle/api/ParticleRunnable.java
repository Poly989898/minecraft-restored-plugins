package net.Indyuce.mmoitems.particle.api;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ParticleRunnable extends BukkitRunnable {
   protected final ParticleData particle;
   protected final PlayerData player;

   public ParticleRunnable(ParticleData var1, PlayerData var2) {
      this.particle = var1;
      this.player = var2;
   }

   public void run() {
      if (this.player.isOnline()) {
         this.createParticles();
      }
   }

   public abstract void createParticles();
}
