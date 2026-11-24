package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemLevel extends ItemStat<NumericStatFormula, DoubleData> implements InternalStat {
   public ItemLevel() {
      super("ITEM_LEVEL", Material.EXPERIENCE_BOTTLE, "Item Level", new String[]{"The item level"}, new String[0]);
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Nullable
   public NumericStatFormula whenInitialized(Object var1) {
      throw new NotImplementedException();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      throw new NotImplementedException();
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      throw new NotImplementedException();
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<NumericStatFormula> var2) {
      throw new NotImplementedException();
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.getValue()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.DOUBLE));
      }

      DoubleData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new DoubleData((Double)var2.getValue()) : null;
   }

   @NotNull
   public DoubleData getClearStatData() {
      return new DoubleData(0.0);
   }
}
