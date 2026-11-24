package net.Indyuce.mmocore.api.util.math.formula;

import io.lumine.mythic.lib.MythicLib;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class LinearValue {
   private final double base;
   private final double perLevel;
   private final double min;
   private final double max;
   private final boolean hasMin;
   private final boolean hasMax;
   public static final LinearValue ZERO = new LinearValue(0.0, 0.0, 0.0, 0.0);

   public LinearValue(double base, double perLevel) {
      this.base = base;
      this.perLevel = perLevel;
      this.hasMin = false;
      this.hasMax = false;
      this.min = 0.0;
      this.max = 0.0;
   }

   public LinearValue(double base, double perLevel, double min, double max) {
      this.base = base;
      this.perLevel = perLevel;
      this.hasMin = true;
      this.hasMax = true;
      this.min = min;
      this.max = max;
   }

   public LinearValue(LinearValue value) {
      this.base = value.base;
      this.perLevel = value.perLevel;
      this.hasMin = value.hasMin;
      this.hasMax = value.hasMax;
      this.min = value.min;
      this.max = value.max;
   }

   public LinearValue(ConfigurationSection config) {
      this.base = config.getDouble("base");
      this.perLevel = config.getDouble("per-level");
      this.hasMin = config.contains("min");
      this.hasMax = config.contains("max");
      this.min = this.hasMin ? config.getDouble("min") : 0.0;
      this.max = this.hasMax ? config.getDouble("max") : 0.0;
   }

   public double getBaseValue() {
      return this.base;
   }

   public double getPerLevel() {
      return this.perLevel;
   }

   public double getMax() {
      return this.max;
   }

   public double getMin() {
      return this.min;
   }

   public boolean hasMax() {
      return this.hasMax;
   }

   public boolean hasMin() {
      return this.hasMin;
   }

   @NotNull
   public String getDisplay(int level) {
      return MythicLib.plugin.getMMOConfig().decimals.format(this.calculate(level));
   }

   public double calculate(int level) {
      double value = this.base + this.perLevel * (level - 1);
      if (this.hasMin) {
         value = Math.max(this.min, value);
      }

      if (this.hasMax) {
         value = Math.min(this.max, value);
      }

      return value;
   }

   @Override
   public String toString() {
      return "LinearValue{base="
         + this.base
         + ", perLevel="
         + this.perLevel
         + ", min="
         + this.min
         + ", max="
         + this.max
         + ", hasmin="
         + this.hasMin
         + ", hasmax="
         + this.hasMax
         + "}";
   }
}
