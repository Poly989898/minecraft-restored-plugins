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

   public RandomRestoreData(ConfigurationSection section) {
      Validate.notNull(section, "Could not load restore config");
      this.health = section.contains("health") ? new NumericStatFormula(section) : NumericStatFormula.ZERO;
      this.food = section.contains("food") ? new NumericStatFormula(section) : NumericStatFormula.ZERO;
      this.saturation = section.contains("saturation") ? new NumericStatFormula(section) : NumericStatFormula.ZERO;
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

   public RestoreData randomize(MMOItemBuilder mmoItemBuilder) {
      return new RestoreData(this.health.calculate(mmoItemBuilder.getLevel()), this.food.calculate(mmoItemBuilder.getLevel()), this.saturation.calculate(mmoItemBuilder.getLevel()));
   }
}
