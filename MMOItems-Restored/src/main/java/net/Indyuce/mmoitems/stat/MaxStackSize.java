package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@HasCategory(cat = "vanilla_attribute")
@VersionDependant(version = {1, 20, 5})
public class MaxStackSize extends DoubleStat implements GemStoneStat {
   public MaxStackSize() {
      super(
         "MAX_STACK_SIZE",
         Material.CHEST,
         "Max Stack Size",
         new String[]{"Maximum amount of items which", "can be stacked together (1.20.5+).", "Maximum value is 99."},
         new String[]{"all"}
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      var1.getMeta().setMaxStackSize((int)Math.max(1.0, Math.min(99.0, var2.getValue())));
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
