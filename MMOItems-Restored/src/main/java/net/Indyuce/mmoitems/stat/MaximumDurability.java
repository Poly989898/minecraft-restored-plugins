package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.MaterialData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.Upgradable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class MaximumDurability extends DoubleStat implements ItemRestriction, GemStoneStat, Upgradable {
   public MaximumDurability() {
      super(
         "MAX_DURABILITY",
         Material.SHEARS,
         "Maximum Custom Durability",
         new String[]{"The amount of uses before your", "item becomes unusable/breaks."},
         new String[]{"!block", "all"}
      );
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
      this.whenApplied(var1, var2);
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection var1, @NotNull Object var2) {
      this.generalStatFormat = var1.getString("item-damage");
      if (this.generalStatFormat == null) {
         this.generalStatFormat = var1.getString("durability");
      }
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      int var3 = (int)var2.getValue();
      int var4 = var1.getMMOItem().hasData(ItemStats.CUSTOM_DURABILITY)
         ? (int)((DoubleData)var1.getMMOItem().getData(ItemStats.CUSTOM_DURABILITY)).getValue()
         : var3;
      var1.addItemTag(new ItemTag(this.getNBTPath(), var3));
      String var5 = this.getGeneralStatFormat().replace("{max}", String.valueOf(var3)).replace("{current}", String.valueOf(var4));
      var1.getLore().insert("durability", var5);
   }

   @Override
   public void preprocess(@NotNull MMOItem var1) {
      if (!var1.hasData(ItemStats.MAX_DURABILITY)) {
         short var2 = 400;
         if (var1.hasData(ItemStats.MATERIAL)) {
            MaterialData var3 = (MaterialData)var1.getData(ItemStats.MATERIAL);
            Material var4 = var3.getMaterial();
            var2 = var4.getMaxDurability();
         }

         if (var2 < 8) {
            var2 = 400;
         }

         var1.setData(ItemStats.MAX_DURABILITY, new DoubleData(var2));
      }
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      if (!var2.hasTag("MMOITEMS_DURABILITY")) {
         return true;
      }

      if (var2.getDouble(ItemStats.CUSTOM_DURABILITY.getNBTPath()) <= 0.0) {
         if (var3) {
            Message.ZERO_DURABILITY.format(ChatColor.RED).send(var1.getPlayer());
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         return true;
      }
   }
}
