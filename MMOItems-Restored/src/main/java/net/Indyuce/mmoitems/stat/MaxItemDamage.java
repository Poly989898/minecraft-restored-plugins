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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      Validate.isTrue(itemStackBuilder.getMeta() instanceof Damageable, "Item is not damageable");
      int count = (int)doubleData.getValue();
      Validate.isTrue(count > 0, "Maximum durability must be positive");
      ((Damageable)itemStackBuilder.getMeta()).setMaxDamage((int)doubleData.getValue());
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
