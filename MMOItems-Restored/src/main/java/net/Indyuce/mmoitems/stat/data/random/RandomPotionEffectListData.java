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

   public RandomPotionEffectListData(ConfigurationSection var1) {
      Validate.notNull(var1, "Config cannot be null");

      for (String var3 : var1.getKeys(false)) {
         ConfigurationSection var4 = var1.getConfigurationSection(var3);
         if (var4 == null) {
            String var5 = var1.getString(var3);
            if (var5 == null) {
               throw new IllegalArgumentException("Config cannot be null");
            }

            String[] var6 = var5.split(",");
            if (var6.length < 1) {
               throw new IllegalArgumentException(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), "Incorrect format, expected $e{Effect}: {Duration},{Amplifier}$b instead of $i{0} {1}$b.", new String[]{var3, var5}
                  )
               );
            }

            Double var7 = SilentNumbers.DoubleParse(var6[0]);
            Integer var8 = SilentNumbers.IntegerParse(var6[1]);
            PotionEffectType var9 = PotionEffectType.getByName(var3.toUpperCase().replace("-", "_").replace(" ", "_"));
            if (var7 == null || var8 == null || var9 == null) {
               throw new IllegalArgumentException(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), "Incorrect format, expected $e{Effect}: {Duration},{Amplifier}$b instead of $i{0} {1}$b.", new String[]{var3, var5}
                  )
               );
            }

            this.effects.add(new RandomPotionEffectData(var9, new NumericStatFormula(var7), new NumericStatFormula(var8)));
         } else {
            this.effects.add(new RandomPotionEffectData(var4));
         }
      }
   }

   public RandomPotionEffectListData(RandomPotionEffectData... var1) {
      this.add(var1);
   }

   public void add(RandomPotionEffectData... var1) {
      this.effects.addAll(Arrays.asList(var1));
   }

   public List<RandomPotionEffectData> getEffects() {
      return this.effects;
   }

   public PotionEffectListData randomize(MMOItemBuilder var1) {
      PotionEffectListData var2 = new PotionEffectListData();
      this.effects.forEach(var2x -> var2.add(var2x.randomize(var1)));
      return var2;
   }
}
