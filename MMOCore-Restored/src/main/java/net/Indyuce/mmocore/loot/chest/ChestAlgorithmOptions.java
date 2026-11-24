package net.Indyuce.mmocore.loot.chest;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ChestAlgorithmOptions {
   public final double minRange;
   public final double maxRange;
   public final double height;
   public final int iterations;
   public static final ChestAlgorithmOptions DEFAULT = new ChestAlgorithmOptions(10.0, 30.0, 8.0, 15);

   public ChestAlgorithmOptions(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      this.minRange = config.getDouble("min-range", DEFAULT.minRange);
      this.maxRange = config.getDouble("max-range", DEFAULT.maxRange);
      this.height = config.getDouble("height", DEFAULT.height);
      this.iterations = config.getInt("iterations", DEFAULT.iterations);
      Validate.isTrue(this.minRange < this.maxRange, "Max range must be greater than min range");
      Validate.isTrue(this.height > 0.0, "Height must be strictly positive");
      Validate.isTrue(this.iterations > 0, "Iterations must be strictly positive");
   }

   public ChestAlgorithmOptions(double minRange, double maxRange, double height, int iterations) {
      this.minRange = minRange;
      this.maxRange = maxRange;
      this.height = height;
      this.iterations = iterations;
   }
}
