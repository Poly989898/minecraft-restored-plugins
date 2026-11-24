package io.lumine.mythic.lib.api.math;

import java.util.Random;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ScalingFormula {
   private final double base;
   private final double scale;
   private final double spread;
   private final double maxSpread;
   private static final Random random = new Random();
   public static final ScalingFormula ZERO = new ScalingFormula(0.0, 0.0, 0.0, 0.0);

   public ScalingFormula(Object object) {
      Validate.notNull(object, "Config must not be null");
      if (object instanceof Number) {
         this.base = Double.parseDouble(object.toString());
         this.scale = 0.0;
         this.spread = 0.0;
         this.maxSpread = 0.0;
      } else if (object instanceof ConfigurationSection) {
         ConfigurationSection config = (ConfigurationSection)object;
         this.base = config.getDouble("base");
         this.scale = config.getDouble("scale");
         this.spread = config.getDouble("spread");
         this.maxSpread = config.getDouble("max-spread");
         Validate.isTrue(this.spread >= 0.0, "Spread must be positive");
         Validate.isTrue(this.maxSpread >= 0.0, "Max spread must be positive");
      } else {
         throw new IllegalArgumentException("Must specify a config section or a number");
      }
   }

   public ScalingFormula(double base, double scale, double spread, double maxSpread) {
      this.base = base;
      this.scale = scale;
      this.spread = spread;
      this.maxSpread = maxSpread;
   }

   public double getBase() {
      return this.base;
   }

   public double getScale() {
      return this.scale;
   }

   public double calculate(double x) {
      double linear = this.base + this.scale * x;
      return linear * (1.0 + Math.min(Math.max(random.nextGaussian() * this.spread, -this.maxSpread), this.maxSpread));
   }
}
