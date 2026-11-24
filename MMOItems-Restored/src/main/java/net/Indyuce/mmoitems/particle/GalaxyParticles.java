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

   public GalaxyParticles(ParticleData var1, PlayerData var2) {
      super(var1, var2);
      this.speed = (float)var1.getModifier("speed") * 0.2F;
      this.height = (float)var1.getModifier("height");
      this.r_speed = (float)var1.getModifier("rotation-speed");
      this.y_coord = (float)var1.getModifier("y-coord");
      this.amount = (int)var1.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location var1 = this.player.getPlayer().getLocation();

      for (int var2 = 0; var2 < this.amount; var2++) {
         double var3 = this.j + (Math.PI * 2) * var2 / this.amount;
         this.particle.display(var1.clone().add(0.0, this.height, 0.0), 0, (float)Math.cos(var3), this.y_coord, (float)Math.sin(var3), this.speed);
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
