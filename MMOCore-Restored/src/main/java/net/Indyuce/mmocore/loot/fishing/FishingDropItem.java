package net.Indyuce.mmocore.loot.fishing;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.util.math.formula.RandomAmount;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.Weighted;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FishingDropItem implements Weighted {
   private final RandomAmount experience;
   private final RandomAmount tugs;
   private final RandomAmount vanillaExp;
   private final DropItem dropItem;

   public FishingDropItem(MMOLineConfig config) {
      config.validateKeys(new String[]{"tugs"});
      this.tugs = new RandomAmount(config.getString("tugs"));
      this.experience = config.contains("experience") ? new RandomAmount(config.getString("experience")) : new RandomAmount(0.0, 0.0);
      this.vanillaExp = config.contains("vanilla-exp") ? new RandomAmount(config.getString("vanilla-exp")) : new RandomAmount(0.0, 0.0);
      this.dropItem = MMOCore.plugin.loadManager.loadDropItem(config);
   }

   @Override
   public double getWeight() {
      return this.dropItem.getWeight() <= 0.0 ? 1.0 : this.dropItem.getWeight();
   }

   public DropItem getItem() {
      return this.dropItem;
   }

   public RandomAmount getExperience() {
      return this.experience;
   }

   public RandomAmount getVanillaExp() {
      return this.vanillaExp;
   }

   public RandomAmount getTugs() {
      return this.tugs;
   }

   public int rollExperience() {
      return this.experience.calculateInt();
   }

   public int rollVanillaExp() {
      return this.vanillaExp.calculateInt();
   }

   public int rollTugs() {
      return this.tugs.calculateInt();
   }

   public DropItem getDropItem() {
      return this.dropItem;
   }

   @Nullable
   public ItemStack collect(LootBuilder builder) {
      this.dropItem.collect(builder);
      return builder.getLoot().stream().findAny().orElse(null);
   }
}
