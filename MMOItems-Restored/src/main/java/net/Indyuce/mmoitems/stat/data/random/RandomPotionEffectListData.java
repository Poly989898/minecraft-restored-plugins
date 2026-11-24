package net.Indyuce.mmoitems.stat.data.random;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

public class RandomPotionEffectListData implements RandomStatData<PotionEffectListData> {
   private final List<RandomPotionEffectData> effects = new ArrayList<>();

   public RandomPotionEffectListData(ConfigurationSection section) {
      Validate.notNull(section, "Config cannot be null");

      for (String key : section.getKeys(false)) {
         ConfigurationSection effectSection = section.getConfigurationSection(key);
         if (effectSection == null) {
            String text = section.getString(key);
            if (text == null) {
               throw new IllegalArgumentException("Config cannot be null");
            }

            String[] texts = text.split(",");
            if (texts.length < 1) {
               throw new IllegalArgumentException(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), "Incorrect format, expected $e{Effect}: {Duration},{Amplifier}$b instead of $i{0} {1}$b.", new String[]{key, text}
                  )
               );
            }

            Double value = SilentNumbers.DoubleParse(texts[0]);
            Integer amplifier = SilentNumbers.IntegerParse(texts[1]);
            PotionEffectType potionEffectType = PotionEffectType.getByName(key.toUpperCase().replace("-", "_").replace(" ", "_"));
            if (value == null || amplifier == null || potionEffectType == null) {
               throw new IllegalArgumentException(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), "Incorrect format, expected $e{Effect}: {Duration},{Amplifier}$b instead of $i{0} {1}$b.", new String[]{key, text}
                  )
               );
            }

            this.effects.add(new RandomPotionEffectData(potionEffectType, new NumericStatFormula(value), new NumericStatFormula(amplifier)));
         } else {
            this.effects.add(new RandomPotionEffectData(effectSection));
         }
      }
   }

   public RandomPotionEffectListData(RandomPotionEffectData... randomPotionEffectDatas) {
      this.add(randomPotionEffectDatas);
   }

   public void add(RandomPotionEffectData... randomPotionEffectDatas) {
      this.effects.addAll(Arrays.asList(randomPotionEffectDatas));
   }

   public List<RandomPotionEffectData> getEffects() {
      return this.effects;
   }

   public PotionEffectListData randomize(MMOItemBuilder mmoItemBuilder) {
      PotionEffectListData potionEffectListData = new PotionEffectListData();
      this.effects.forEach(randomEffect -> potionEffectListData.add(randomEffect.randomize(mmoItemBuilder)));
      return potionEffectListData;
   }
}
