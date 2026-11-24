package io.lumine.mythic.lib.version;

import java.util.Random;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OreDrops {
   private final Material material;
   private final int min;
   private final int max;
   private static final Random RANDOM = new Random();

   public OreDrops(Material material) {
      this(material, 1, 1);
   }

   public OreDrops(Material material, int min, int max) {
      Validate.notNull(material, "Material cannot be null");
      Validate.isTrue(min > 0, "Min amount must be positive");
      Validate.isTrue(max >= min, "Max amount must be higher than min amount");
      this.material = material;
      this.min = min;
      this.max = max;
   }

   @NotNull
   public ItemStack generate(int fortuneLevel) {
      Validate.isTrue(fortuneLevel >= 0, "Fortune level must be positive");
      int rolled = this.min == this.max ? this.min : RANDOM.nextInt(this.min, this.max + 1);
      return new ItemStack(this.material, rolled * this.rollFortuneCoefficient(fortuneLevel));
   }

   private int rollFortuneCoefficient(int fortuneLevel) {
      if (fortuneLevel == 0) {
         return 1;
      } else {
         return RANDOM.nextDouble() < 2.0 / (2.0 + fortuneLevel) ? 1 : RANDOM.nextInt(2, fortuneLevel + 2);
      }
   }
}
