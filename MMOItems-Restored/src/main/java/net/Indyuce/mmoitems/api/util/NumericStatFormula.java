package net.Indyuce.mmoitems.api.util;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.random.UpdatableRandomStatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class NumericStatFormula implements RandomStatData<DoubleData>, UpdatableRandomStatData<DoubleData> {
   private final double base;
   private final double scale;
   private final double spread;
   private final double maxSpread;
   private final double min;
   private final double max;
   private final boolean uniform;
   private final boolean hasMin;
   private final boolean hasMax;
   private static final Random RANDOM = new Random();
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####");
   public static final NumericStatFormula ZERO = new NumericStatFormula(0.0, 0.0, 0.0, 0.0);
   private static final double DEFAULT_MAX_SPREAD = 0.3;
   public static boolean RELATIVE_SPREAD = false;

   public NumericStatFormula(Object var1) {
      Validate.notNull(var1, "Config must not be null");
      if (var1 instanceof String) {
         if (var1.toString().contains("->")) {
            String[] var2 = var1.toString().replace(" ", "").split(Pattern.quote("->"));
            this.base = 0.0;
            this.scale = 0.0;
            this.spread = 0.0;
            this.maxSpread = 0.0;
            this.uniform = true;
            this.hasMin = true;
            this.hasMax = true;
            this.min = Double.parseDouble(var2[0]);
            this.max = Double.parseDouble(var2[1]);
         } else {
            String[] var3 = var1.toString().split(" ");
            this.base = Double.parseDouble(var3[0]);
            this.scale = var3.length > 1 ? Double.parseDouble(var3[1]) : 0.0;
            this.spread = var3.length > 2 ? Double.parseDouble(var3[2]) : 0.0;
            this.maxSpread = var3.length > 3 ? Double.parseDouble(var3[3]) : 0.0;
            this.hasMin = var3.length > 4;
            this.hasMax = var3.length > 5;
            this.min = this.hasMin ? Double.parseDouble(var3[4]) : 0.0;
            this.max = this.hasMax ? Double.parseDouble(var3[5]) : 0.0;
            this.uniform = false;
         }
      } else if (var1 instanceof Number) {
         this.base = Double.parseDouble(var1.toString());
         this.scale = 0.0;
         this.spread = 0.0;
         this.maxSpread = 0.0;
         this.uniform = false;
         this.hasMin = false;
         this.hasMax = false;
         this.min = 0.0;
         this.max = 0.0;
      } else {
         if (!(var1 instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("Must specify a config section, a string or a number");
         }

         ConfigurationSection var4 = (ConfigurationSection)var1;
         this.base = var4.getDouble("base");
         this.scale = var4.getDouble("scale");
         this.spread = var4.getDouble("spread");
         this.maxSpread = var4.getDouble("max-spread", 0.3);
         this.hasMin = var4.contains("min");
         this.hasMax = var4.contains("max");
         this.uniform = !var4.contains("spread") && !var4.contains("scale") && !var4.contains("base") && this.hasMin && this.hasMax;
         this.min = this.hasMin ? var4.getDouble("min") : 0.0;
         this.max = this.hasMax ? var4.getDouble("max") : 0.0;
      }

      Validate.isTrue(this.spread >= 0.0, "Spread must be positive");
      Validate.isTrue(this.maxSpread >= 0.0, "Max spread must be positive");
   }

   public NumericStatFormula(double var1, double var3, double var5, double var7) {
      this(var1, var3, var5, var7, false, false, 0.0, false, 0.0);
   }

   public NumericStatFormula(double var1, double var3, double var5, double var7, double var9) {
      this(var1, var3, var5, 100.0, false, true, var7, true, var9);
   }

   public NumericStatFormula(double var1, double var3, double var5, double var7, boolean var9, boolean var10, double var11, boolean var13, double var14) {
      this.base = var1;
      this.scale = var3;
      this.spread = var5;
      this.maxSpread = var7;
      this.uniform = var9;
      this.hasMin = var10;
      this.hasMax = var13;
      this.min = var11;
      this.max = var14;
   }

   public double getBase() {
      return this.base;
   }

   public double getScale() {
      return this.scale;
   }

   public double getSpread() {
      return this.spread;
   }

   public double getMaxSpread() {
      return this.maxSpread;
   }

   public boolean isUniform() {
      return this.uniform;
   }

   public boolean hasMin() {
      return this.hasMin;
   }

   public boolean hasMax() {
      return this.hasMax;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double calculate(double var1) {
      return this.calculate(var1, NumericStatFormula.FormulaInputType.RANDOM);
   }

   public double calculate(double var1, @NotNull NumericStatFormula.FormulaInputType var3) {
      return this.calculate(
         var1,
         var3 == NumericStatFormula.FormulaInputType.RANDOM
            ? (this.uniform ? RANDOM.nextDouble() : RANDOM.nextGaussian())
            : (
               var3 == NumericStatFormula.FormulaInputType.UPPER_BOUND
                  ? (this.uniform ? 1.0 : 2.5)
                  : (var3 == NumericStatFormula.FormulaInputType.LOWER_BOUND ? (this.uniform ? 0.0 : -2.5) : Double.NaN)
            )
      );
   }

   public double calculate(double var1, double var3) {
      if (this.uniform) {
         return var1 * this.scale + this.min + (this.max - this.min) * var3;
      }

      double var5 = this.base + this.scale * var1;
      double var7 = Math.min(Math.max(var3 * this.spread, -this.maxSpread), this.maxSpread);
      double var9 = RELATIVE_SPREAD ? var5 * (1.0 + var7) : var5 + var7;
      if (this.hasMin) {
         var9 = Math.max(this.min, var9);
      }

      if (this.hasMax) {
         var9 = Math.min(this.max, var9);
      }

      return var9;
   }

   public DoubleData randomize(MMOItemBuilder var1) {
      return new DoubleData(this.calculate(var1.getLevel()));
   }

   public void fillConfigurationSection(@NotNull ConfigurationSection var1, @NotNull String var2, @NotNull NumericStatFormula.FormulaSaveOption var3) {
      if (var2 == null) {
         throw new NullPointerException("Path is empty");
      }

      if (this.scale == 0.0 && this.spread == 0.0 && this.maxSpread == 0.0 && !this.uniform) {
         var1.set(var2, this.base == 0.0 && var3 == NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO ? null : this.base);
      } else {
         var1.createSection(var2);
         var1 = var1.getConfigurationSection(var2);
         if (!this.uniform) {
            var1.set("base", this.base);
            var1.set("scale", this.scale);
            var1.set("spread", this.spread);
            var1.set("max-spread", this.maxSpread);
         }

         if (this.hasMin) {
            var1.set("min", this.min);
         }

         if (this.hasMax) {
            var1.set("max", this.max);
         }
      }
   }

   public void fillConfigurationSection(ConfigurationSection var1, String var2) {
      this.fillConfigurationSection(var1, var2, NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO);
   }

   @Override
   public String toString() {
      if (this.scale == 0.0 && this.spread == 0.0) {
         return DECIMAL_FORMAT.format(this.base);
      } else {
         return this.scale == 0.0
            ? "["
               + DECIMAL_FORMAT.format(this.base * (1.0 - this.maxSpread))
               + " -> "
               + DECIMAL_FORMAT.format(this.base * (1.0 + this.maxSpread))
               + "] ("
               + DECIMAL_FORMAT.format(this.spread * 100.0)
               + "% Spread) ("
               + DECIMAL_FORMAT.format(this.base)
               + " Avg)"
            : "{Base="
               + DECIMAL_FORMAT.format(this.base)
               + (this.scale != 0.0 ? ",Scale=" + DECIMAL_FORMAT.format(this.scale) : "")
               + (this.spread != 0.0 ? ",Spread=" + this.spread : "")
               + (this.maxSpread != 0.0 ? ",Max=" + this.maxSpread : "")
               + "}";
      }
   }

   @NotNull
   public DoubleData reroll(@NotNull ItemStat var1, @NotNull DoubleData var2, int var3) {
      double var4 = this.getBase() + this.getScale() * var3;
      double var6 = var2.getValue();
      double var8 = var6 - var4;
      double var10 = RELATIVE_SPREAD ? Math.abs(var8 / (this.getSpread() * var4)) : Math.abs(var8 / this.getSpread());
      double var12 = this.getMaxSpread() / this.getSpread();
      return !(var10 > var12) && !(var10 > 3.5) ? var2.clone() : new DoubleData(this.calculate(var3));
   }

   public enum FormulaInputType {
      UPPER_BOUND,
      LOWER_BOUND,
      RANDOM;
   }

   public enum FormulaSaveOption {
      DELETE_IF_ZERO,
      NONE;
   }
}
