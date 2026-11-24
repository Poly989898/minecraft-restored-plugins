package net.Indyuce.mmoitems.stat.data.random;

import java.util.Random;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.BooleanData;

public class RandomBooleanData implements RandomStatData<BooleanData> {
   private final double chance;
   private static final Random random = new Random();

   public RandomBooleanData(boolean flag) {
      this.chance = flag ? 1.0 : 0.0;
   }

   public RandomBooleanData(double value) {
      this.chance = value;
   }

   public double getChance() {
      return this.chance;
   }

   public BooleanData randomize(MMOItemBuilder mmoItemBuilder) {
      return new BooleanData(random.nextDouble() < this.chance);
   }
}
