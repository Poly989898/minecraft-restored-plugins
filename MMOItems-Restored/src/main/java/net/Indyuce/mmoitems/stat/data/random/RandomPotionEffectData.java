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

   public RandomPotionEffectData(ConfigurationSection var1) {
      Validate.notNull(var1, "Potion effect config cannot be null");
      this.type = PotionEffectType.getByName(var1.getName().toUpperCase().replace("-", "_").replace(" ", "_"));
      Validate.notNull(this.type, "Could not find potion effect with name '" + var1.getName() + "'");
      this.duration = new NumericStatFormula(var1.get("duration"));
      this.amplifier = new NumericStatFormula(var1.get("amplifier"));
   }

   public RandomPotionEffectData(PotionEffectType var1, NumericStatFormula var2) {
      this(var1, new NumericStatFormula(MMOUtils.getEffectDuration(var1) / 20.0, 0.0, 0.0, 0.0), var2);
   }

   public RandomPotionEffectData(PotionEffectType var1, NumericStatFormula var2, NumericStatFormula var3) {
      this.type = var1;
      this.duration = var2;
      this.amplifier = var3;
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

   public PotionEffectData randomize(MMOItemBuilder var1) {
      return new PotionEffectData(this.type, this.duration.calculate(var1.getLevel()), (int)this.amplifier.calculate(var1.getLevel()));
   }
}
