package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class FirefliesParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float radius;
   private final float r_speed;
   private final int amount;
   private double j = 0.0;

   public FirefliesParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed");
      this.height = (float)particleData.getModifier("height");
      this.radius = (float)particleData.getModifier("radius");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.amount = (int)particleData.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();

      for (int i = 0; i < this.amount; i++) {
         double value = this.j + (Math.PI * 2) * i / this.amount;
         this.particle.display(location.clone().add(Math.cos(value) * this.radius, this.height, Math.sin(value) * this.radius), this.speed);
      }

      this.j = this.j + 0.06544984694978735 * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
