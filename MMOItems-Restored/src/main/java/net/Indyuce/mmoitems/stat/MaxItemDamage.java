package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@VersionDependant(version = {1, 20, 5})
public class MaxItemDamage extends DoubleStat implements GemStoneStat {
   public MaxItemDamage() {
      super(
         "MAX_ITEM_DAMAGE",
         Material.DAMAGED_ANVIL,
         "Maximum Vanilla Durability",
         new String[]{
            "Only available in 1.20.5+",
            "Maximum amount of durability on your item.",
            "This works using vanilla durability and is",
            "much more stable than Custom Durability."
         },
         new String[]{"all"}
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      Validate.isTrue(var1.getMeta() instanceof Damageable, "Item is not damageable");
      int var3 = (int)var2.getValue();
      Validate.isTrue(var3 > 0, "Maximum durability must be positive");
      ((Damageable)var1.getMeta()).setMaxDamage((int)var2.getValue());
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
      this.whenApplied(var1, var2);
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), (int)var1.getValue()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.INTEGER));
      }

      DoubleData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new DoubleData(((Integer)var2.getValue()).intValue()) : null;
   }
}
