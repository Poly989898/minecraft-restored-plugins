package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;

public class OffsetParticles extends ParticleRunnable {
   private final float speed;
   private final float h_offset;
   private final float v_offset;
   private final float height;
   private final int amount;

   public OffsetParticles(ParticleData var1, PlayerData var2) {
      super(var1, var2);
      this.speed = (float)var1.getModifier("speed");
      this.height = (float)var1.getModifier("height");
      this.h_offset = (float)var1.getModifier("horizontal-offset");
      this.v_offset = (float)var1.getModifier("vertical-offset");
      this.amount = (int)var1.getModifier("amount");
   }

   @Override
   public void createParticles() {
      this.particle
         .display(this.player.getPlayer().getLocation().add(0.0, this.height, 0.0), this.amount, this.h_offset, this.v_offset, this.h_offset, this.speed);
   }
}
