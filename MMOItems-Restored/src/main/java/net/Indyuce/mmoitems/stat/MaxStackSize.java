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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      itemStackBuilder.getMeta().setMaxStackSize((int)Math.max(1.0, Math.min(99.0, doubleData.getValue())));
      itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
   }

   @Override
   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      this.whenApplied(itemStackBuilder, doubleData);
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), (int)doubleData.getValue()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.INTEGER));
      }

      DoubleData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new DoubleData(((Integer)tagAtPath.getValue()).intValue()) : null;
   }
}
