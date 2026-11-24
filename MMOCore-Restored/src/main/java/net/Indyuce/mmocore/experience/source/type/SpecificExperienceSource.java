package net.Indyuce.mmocore.experience.source.type;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public abstract class SpecificExperienceSource<T> extends ExperienceSource<T> {
   private final RandomAmount amount;

   public SpecificExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser);
      config.validateKeys(new String[]{"amount"});
      this.amount = new RandomAmount(config.getString("amount"));
   }

   public SpecificExperienceSource(ExperienceDispenser dispenser) {
      super(dispenser);
      this.amount = new RandomAmount(0.0, 0.0);
   }

   public RandomAmount getAmount() {
      return this.amount;
   }

   public double rollAmount() {
      return this.amount.calculate();
   }

   public void giveExperience(PlayerData player, double multiplier, @Nullable Location hologramLocation) {
      this.getDispenser().giveExperience(player, this.rollAmount() * multiplier, hologramLocation, EXPSource.SOURCE);
   }
}
