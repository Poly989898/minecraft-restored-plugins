package io.lumine.mythic.lib.api.stat.handler;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.stat.StatInstance;
import java.text.DecimalFormat;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class StatHandler {
   protected final boolean hasMinValue;
   protected final boolean hasMaxValue;
   protected final double baseValue;
   protected final double minValue;
   protected final double maxValue;
   protected final DecimalFormat decimalFormat;
   protected final String stat;

   public StatHandler(@NotNull ConfigurationSection config, @NotNull String stat) {
      this.stat = stat;
      String[] splitBounds = (" " + config.getString("min-max-values." + this.stat, "=") + " ").split("=");
      Validate.isTrue(splitBounds.length == 2, "Could not find unique = separator symbol");
      String cleanMin = splitBounds[0].replace(" ", "");
      String cleanMax = splitBounds[1].replace(" ", "");
      this.hasMinValue = !cleanMin.isEmpty();
      this.hasMaxValue = !cleanMax.isEmpty();
      this.minValue = this.hasMinValue ? Double.parseDouble(cleanMin) : 0.0;
      this.maxValue = this.hasMaxValue ? Double.parseDouble(cleanMax) : 0.0;
      this.baseValue = config.getDouble("base-stat-value." + this.stat);
      this.decimalFormat = config.contains("decimal-format." + this.stat)
         ? MythicLib.plugin.getMMOConfig().newDecimalFormat(config.getString("decimal-format." + this.stat))
         : MythicLib.plugin.getMMOConfig().decimal;
   }

   @NotNull
   public String getStat() {
      return this.stat;
   }

   @NotNull
   public DecimalFormat getDecimalFormat() {
      return this.decimalFormat;
   }

   public void runUpdate(@NotNull StatInstance instance) {
   }

   public double getBaseValue(@NotNull StatInstance instance) {
      return this.baseValue;
   }

   public double getFinalValue(@NotNull StatInstance instance) {
      return instance.getTotal();
   }

   public double clampValue(double clamped) {
      if (this.hasMaxValue && clamped > this.maxValue) {
         clamped = this.maxValue;
      }

      if (this.hasMinValue && clamped < this.minValue) {
         clamped = this.minValue;
      }

      return clamped;
   }
}
