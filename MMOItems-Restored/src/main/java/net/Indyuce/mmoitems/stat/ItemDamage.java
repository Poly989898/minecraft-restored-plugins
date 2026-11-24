package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDamage extends DoubleStat implements GemStoneStat {
   public ItemDamage() {
      super(
         "ITEM_DAMAGE",
         Material.FISHING_ROD,
         "Base Item Damage",
         new String[]{"Default item damage. This does &cNOT", "impact the item's max durability."},
         new String[]{"!block", "all"}
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      if (itemStackBuilder.getMeta() instanceof Damageable) {
         int count = (int)doubleData.getValue();
         if (count > 0) {
            ((Damageable)itemStackBuilder.getMeta()).setDamage(count);
         }
      }
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      this.whenApplied(itemStackBuilder, doubleData);
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      return new ArrayList<>();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta() instanceof Damageable) {
         int damage = ((Damageable)readMMOItem.getNBT().getItem().getItemMeta()).getDamage();
         if (damage > 0) {
            readMMOItem.setData(ItemStats.ITEM_DAMAGE, new DoubleData(damage));
         }
      }
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }

   @Override
   public String getLegacyTranslationPath() {
      return "durability";
   }
}
