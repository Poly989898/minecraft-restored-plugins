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

   public NumericStatFormula(Object obj) {
      Validate.notNull(obj, "Config must not be null");
      if (obj instanceof String) {
         if (obj.toString().contains("->")) {
            String[] texts = obj.toString().replace(" ", "").split(Pattern.quote("->"));
            this.base = 0.0;
            this.scale = 0.0;
            this.spread = 0.0;
            this.maxSpread = 0.0;
            this.uniform = true;
            this.hasMin = true;
            this.hasMax = true;
            this.min = Double.parseDouble(texts[0]);
            this.max = Double.parseDouble(texts[1]);
         } else {
            String[] texts = obj.toString().split(" ");
            this.base = Double.parseDouble(texts[0]);
            this.scale = texts.length > 1 ? Double.parseDouble(texts[1]) : 0.0;
            this.spread = texts.length > 2 ? Double.parseDouble(texts[2]) : 0.0;
            this.maxSpread = texts.length > 3 ? Double.parseDouble(texts[3]) : 0.0;
            this.hasMin = texts.length > 4;
            this.hasMax = texts.length > 5;
            this.min = this.hasMin ? Double.parseDouble(texts[4]) : 0.0;
            this.max = this.hasMax ? Double.parseDouble(texts[5]) : 0.0;
            this.uniform = false;
         }
      } else if (obj instanceof Number) {
         this.base = Double.parseDouble(obj.toString());
         this.scale = 0.0;
         this.spread = 0.0;
         this.maxSpread = 0.0;
         this.uniform = false;
         this.hasMin = false;
         this.hasMax = false;
         this.min = 0.0;
         this.max = 0.0;
      } else {
         if (!(obj instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("Must specify a config section, a string or a number");
         }

         ConfigurationSection section = (ConfigurationSection)obj;
         this.base = section.getDouble("base");
         this.scale = section.getDouble("scale");
         this.spread = section.getDouble("spread");
         this.maxSpread = section.getDouble("max-spread", 0.3);
         this.hasMin = section.contains("min");
         this.hasMax = section.contains("max");
         this.uniform = !section.contains("spread") && !section.contains("scale") && !section.contains("base") && this.hasMin && this.hasMax;
         this.min = this.hasMin ? section.getDouble("min") : 0.0;
         this.max = this.hasMax ? section.getDouble("max") : 0.0;
      }

      Validate.isTrue(this.spread >= 0.0, "Spread must be positive");
      Validate.isTrue(this.maxSpread >= 0.0, "Max spread must be positive");
   }

   public NumericStatFormula(double value, double value2, double value3, double value4) {
      this(value, value2, value3, value4, false, false, 0.0, false, 0.0);
   }

   public NumericStatFormula(double value, double value2, double value3, double value4, double value5) {
      this(value, value2, value3, 100.0, false, true, value4, true, value5);
   }

   public NumericStatFormula(double value, double value2, double value3, double value4, boolean flag, boolean flag2, double value5, boolean flag3, double value6) {
      this.base = value;
      this.scale = value2;
      this.spread = value3;
      this.maxSpread = value4;
      this.uniform = flag;
      this.hasMin = flag2;
      this.hasMax = flag3;
      this.min = value5;
      this.max = value6;
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

   public double calculate(double value) {
      return this.calculate(value, NumericStatFormula.FormulaInputType.RANDOM);
   }

   public double calculate(double value, @NotNull NumericStatFormula.FormulaInputType formulaInputType) {
      return this.calculate(
         value,
         formulaInputType == NumericStatFormula.FormulaInputType.RANDOM
            ? (this.uniform ? RANDOM.nextDouble() : RANDOM.nextGaussian())
            : (
               formulaInputType == NumericStatFormula.FormulaInputType.UPPER_BOUND
                  ? (this.uniform ? 1.0 : 2.5)
                  : (formulaInputType == NumericStatFormula.FormulaInputType.LOWER_BOUND ? (this.uniform ? 0.0 : -2.5) : Double.NaN)
            )
      );
   }

   public double calculate(double value, double value2) {
      if (this.uniform) {
         return value * this.scale + this.min + (this.max - this.min) * value2;
      }

      double baseValue = this.base + this.scale * value;
      double spreadValue = Math.min(Math.max(value2 * this.spread, -this.maxSpread), this.maxSpread);
      double resultValue = RELATIVE_SPREAD ? baseValue * (1.0 + spreadValue) : baseValue + spreadValue;
      if (this.hasMin) {
         resultValue = Math.max(this.min, resultValue);
      }

      if (this.hasMax) {
         resultValue = Math.min(this.max, resultValue);
      }

      return resultValue;
   }

   public DoubleData randomize(MMOItemBuilder mmoItemBuilder) {
      return new DoubleData(this.calculate(mmoItemBuilder.getLevel()));
   }

   public void fillConfigurationSection(@NotNull ConfigurationSection section, @NotNull String text, @NotNull NumericStatFormula.FormulaSaveOption formulaSaveOption) {
      if (text == null) {
         throw new NullPointerException("Path is empty");
      }

      if (this.scale == 0.0 && this.spread == 0.0 && this.maxSpread == 0.0 && !this.uniform) {
         section.set(text, this.base == 0.0 && formulaSaveOption == NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO ? null : this.base);
      } else {
         section.createSection(text);
         section = section.getConfigurationSection(text);
         if (!this.uniform) {
            section.set("base", this.base);
            section.set("scale", this.scale);
            section.set("spread", this.spread);
            section.set("max-spread", this.maxSpread);
         }

         if (this.hasMin) {
            section.set("min", this.min);
         }

         if (this.hasMax) {
            section.set("max", this.max);
         }
      }
   }

   public void fillConfigurationSection(ConfigurationSection section, String text) {
      this.fillConfigurationSection(section, text, NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO);
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
   public DoubleData reroll(@NotNull ItemStat itemStat, @NotNull DoubleData doubleData, int count) {
      double value = this.getBase() + this.getScale() * count;
      double dataValue = doubleData.getValue();
      double difference = dataValue - value;
      double deviation = RELATIVE_SPREAD ? Math.abs(difference / (this.getSpread() * value)) : Math.abs(difference / this.getSpread());
      double maxDeviation = this.getMaxSpread() / this.getSpread();
      return !(deviation > maxDeviation) && !(deviation > 3.5) ? doubleData.clone() : new DoubleData(this.calculate(count));
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
