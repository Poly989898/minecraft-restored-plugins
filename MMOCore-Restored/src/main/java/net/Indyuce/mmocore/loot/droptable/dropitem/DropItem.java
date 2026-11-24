package net.Indyuce.mmocore.loot.droptable.dropitem;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Random;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;
import net.Indyuce.mmocore.loot.LootBuilder;

public abstract class DropItem {
   protected static final Random random = new Random();
   private final double chance;
   private final double weight;
   private final RandomAmount amount;
   @Deprecated
   public static final double CHANCE_FACTOR = 0.07;
   @Deprecated
   public static final double CHANCE_POWER = 0.33333333333;

   public DropItem(MMOLineConfig config) {
      this.chance = config.args().length > 0 ? Double.parseDouble(config.args()[0]) : 1.0;
      this.amount = config.args().length > 1 ? new RandomAmount(config.args()[1]) : new RandomAmount(1.0, 1.0);
      this.weight = config.args().length > 2 ? Double.parseDouble(config.args()[2]) : 0.0;
   }

   public RandomAmount getAmount() {
      return this.amount;
   }

   public double getChance() {
      return this.chance;
   }

   public double getWeight() {
      return this.weight;
   }

   public int rollAmount() {
      return this.amount.calculateInt();
   }

   public boolean rollChance(PlayerData player) {
      double effectiveLuck = 0.07 * MMOCore.plugin.configManager.dropItemsChanceWeight * player.getStats().getStat("CHANCE");
      double randomValue = random.nextDouble();
      return randomValue < Math.pow(this.chance, Math.pow(1.0 + effectiveLuck, 0.33333333333));
   }

   public abstract void collect(LootBuilder lootBuilder);
}
