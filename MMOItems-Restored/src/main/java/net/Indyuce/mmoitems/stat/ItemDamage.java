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
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      if (var1.getMeta() instanceof Damageable) {
         int var3 = (int)var2.getValue();
         if (var3 > 0) {
            ((Damageable)var1.getMeta()).setDamage(var3);
         }
      }
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
      this.whenApplied(var1, var2);
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta() instanceof Damageable) {
         int var2 = ((Damageable)var1.getNBT().getItem().getItemMeta()).getDamage();
         if (var2 > 0) {
            var1.setData(ItemStats.ITEM_DAMAGE, new DoubleData(var2));
         }
      }
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }

   @Override
   public String getLegacyTranslationPath() {
      return "durability";
   }
}
