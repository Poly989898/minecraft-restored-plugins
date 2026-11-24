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
   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      this.whenApplied(itemStackBuilder, doubleData);
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection section, @NotNull Object obj) {
      this.generalStatFormat = section.getString("item-damage");
      if (this.generalStatFormat == null) {
         this.generalStatFormat = section.getString("durability");
      }
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      int count = (int)doubleData.getValue();
      int count2 = itemStackBuilder.getMMOItem().hasData(ItemStats.CUSTOM_DURABILITY)
         ? (int)((DoubleData)itemStackBuilder.getMMOItem().getData(ItemStats.CUSTOM_DURABILITY)).getValue()
         : count;
      itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), count));
      String text = this.getGeneralStatFormat().replace("{max}", String.valueOf(count)).replace("{current}", String.valueOf(count2));
      itemStackBuilder.getLore().insert("durability", text);
   }

   @Override
   public void preprocess(@NotNull MMOItem item) {
      if (!item.hasData(ItemStats.MAX_DURABILITY)) {
         short value = 400;
         if (item.hasData(ItemStats.MATERIAL)) {
            MaterialData data = (MaterialData)item.getData(ItemStats.MATERIAL);
            Material material = data.getMaterial();
            value = material.getMaxDurability();
         }

         if (value < 8) {
            value = 400;
         }

         item.setData(ItemStats.MAX_DURABILITY, new DoubleData(value));
      }
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      if (!nbtItem.hasTag("MMOITEMS_DURABILITY")) {
         return true;
      }

      if (nbtItem.getDouble(ItemStats.CUSTOM_DURABILITY.getNBTPath()) <= 0.0) {
         if (flag) {
            Message.ZERO_DURABILITY.format(ChatColor.RED).send(rpgPlayer.getPlayer());
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         return true;
      }
   }
}
