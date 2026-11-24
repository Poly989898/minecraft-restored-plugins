package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class GalaxyParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float r_speed;
   private final float y_coord;
   private final int amount;
   private double j = 0.0;

   public GalaxyParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed") * 0.2F;
      this.height = (float)particleData.getModifier("height");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.y_coord = (float)particleData.getModifier("y-coord");
      this.amount = (int)particleData.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();

      for (int i = 0; i < this.amount; i++) {
         double value = this.j + (Math.PI * 2) * i / this.amount;
         this.particle.display(location.clone().add(0.0, this.height, 0.0), 0, (float)Math.cos(value), this.y_coord, (float)Math.sin(value), this.speed);
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
