package io.lumine.mythic.bukkit.utils.particles;

import java.awt.Color;
import org.bukkit.Particle.DustOptions;

public class DustParticle extends ParticleBuilder {
   protected DustParticle(Particle particle) {
      super(particle);
   }

   public DustParticle withColor(String strColor) {
      return this.withColorAndSize(strColor, 1.0F);
   }

   public DustParticle withColorAndSize(String strColor, float size) {
      Color color = Color.decode(strColor);
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();
      org.bukkit.Color c = org.bukkit.Color.fromRGB(r, g, b);
      this.particleData = new DustOptions(c, size);
      return this;
   }
}
