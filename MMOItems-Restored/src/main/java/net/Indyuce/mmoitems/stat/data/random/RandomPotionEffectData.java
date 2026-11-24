package net.Indyuce.mmoitems.stat.data.random;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

public class RandomPotionEffectData {
   private final PotionEffectType type;
   private final NumericStatFormula duration;
   private final NumericStatFormula amplifier;

   public RandomPotionEffectData(ConfigurationSection section) {
      Validate.notNull(section, "Potion effect config cannot be null");
      this.type = PotionEffectType.getByName(section.getName().toUpperCase().replace("-", "_").replace(" ", "_"));
      Validate.notNull(this.type, "Could not find potion effect with name '" + section.getName() + "'");
      this.duration = new NumericStatFormula(section.get("duration"));
      this.amplifier = new NumericStatFormula(section.get("amplifier"));
   }

   public RandomPotionEffectData(PotionEffectType potionEffectType, NumericStatFormula numericStatFormula) {
      this(potionEffectType, new NumericStatFormula(MMOUtils.getEffectDuration(potionEffectType) / 20.0, 0.0, 0.0, 0.0), numericStatFormula);
   }

   public RandomPotionEffectData(PotionEffectType potionEffectType, NumericStatFormula numericStatFormula, NumericStatFormula numericStatFormula2) {
      this.type = potionEffectType;
      this.duration = numericStatFormula;
      this.amplifier = numericStatFormula2;
   }

   public PotionEffectType getType() {
      return this.type;
   }

   public NumericStatFormula getDuration() {
      return this.duration;
   }

   public NumericStatFormula getAmplifier() {
      return this.amplifier;
   }

   public PotionEffectData randomize(MMOItemBuilder mmoItemBuilder) {
      return new PotionEffectData(this.type, this.duration.calculate(mmoItemBuilder.getLevel()), (int)this.amplifier.calculate(mmoItemBuilder.getLevel()));
   }
}
