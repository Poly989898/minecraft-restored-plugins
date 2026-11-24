package net.Indyuce.mmoitems.api.block;

import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.MushroomState;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomBlock {
   private final int id;
   private final MushroomState state;
   private final MMOItem mmoitem;
   @Nullable
   private final WorldGenTemplate template;
   private final int minExp;
   private final int maxExp;
   private final int requiredPower;
   private final boolean requirePowerToBreak;
   private static final Random RANDOM = new Random();

   public CustomBlock(MushroomState var1, MMOItem var2) {
      this.mmoitem = var2;
      this.id = var2.hasData(ItemStats.BLOCK_ID) ? (int)((DoubleData)var2.getData(ItemStats.BLOCK_ID)).getValue() : 0;
      this.state = var1;
      this.minExp = var2.hasData(ItemStats.MIN_XP) ? (int)((DoubleData)var2.getData(ItemStats.MIN_XP)).getValue() : 0;
      this.maxExp = var2.hasData(ItemStats.MAX_XP) ? (int)((DoubleData)var2.getData(ItemStats.MAX_XP)).getValue() : 0;
      this.requiredPower = var2.hasData(ItemStats.REQUIRED_POWER) ? (int)((DoubleData)var2.getData(ItemStats.REQUIRED_POWER)).getValue() : 0;
      this.requirePowerToBreak = var2.hasData(ItemStats.REQUIRE_POWER_TO_BREAK) && ((BooleanData)var2.getData(ItemStats.REQUIRE_POWER_TO_BREAK)).isEnabled();
      this.template = var2.hasData(ItemStats.GEN_TEMPLATE) ? MMOItems.plugin.getWorldGen().getOrThrow(var2.getData(ItemStats.GEN_TEMPLATE).toString()) : null;
   }

   public int getId() {
      return this.id;
   }

   public MushroomState getState() {
      return this.state;
   }

   public boolean hasGenTemplate() {
      return this.template != null;
   }

   public WorldGenTemplate getGenTemplate() {
      return this.template;
   }

   public int getMinExpDrop() {
      return this.minExp;
   }

   public int getMaxExpDrop() {
      return this.maxExp;
   }

   public int rollExperience() {
      return Math.max(0, this.maxExp < this.minExp ? this.minExp : RANDOM.nextInt(this.maxExp - this.minExp + 1) + this.minExp);
   }

   public int getRequiredPower() {
      return this.requiredPower;
   }

   public ItemStack getItem() {
      return this.mmoitem.newBuilder().build();
   }

   public boolean requirePowerToBreak() {
      return this.requirePowerToBreak;
   }
}
