package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.RestoreData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class RandomRestoreData implements RandomStatData<RestoreData> {
   private final NumericStatFormula health;
   private final NumericStatFormula food;
   private final NumericStatFormula saturation;

   public RandomRestoreData(ConfigurationSection var1) {
      Validate.notNull(var1, "Could not load restore config");
      this.health = var1.contains("health") ? new NumericStatFormula(var1) : NumericStatFormula.ZERO;
      this.food = var1.contains("food") ? new NumericStatFormula(var1) : NumericStatFormula.ZERO;
      this.saturation = var1.contains("saturation") ? new NumericStatFormula(var1) : NumericStatFormula.ZERO;
   }

   public NumericStatFormula getHealth() {
      return this.health;
   }

   public NumericStatFormula getFood() {
      return this.food;
   }

   public NumericStatFormula getSaturation() {
      return this.saturation;
   }

   public RestoreData randomize(MMOItemBuilder var1) {
      return new RestoreData(this.health.calculate(var1.getLevel()), this.food.calculate(var1.getLevel()), this.saturation.calculate(var1.getLevel()));
   }
}
