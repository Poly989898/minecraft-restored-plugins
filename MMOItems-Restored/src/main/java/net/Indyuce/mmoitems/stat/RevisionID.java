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
import net.Indyuce.mmoitems.gui.edition.RevisionInventory;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RevisionID extends ItemStat<NumericStatFormula, DoubleData> implements GemStoneStat {
   public RevisionID() {
      super(
         "REVISION_ID",
         Material.ITEM_FRAME,
         "Revision ID",
         new String[]{
            "The Revision ID is used to determine",
            "if an item is outdated or not. You",
            "should increase this whenever",
            "you make changes to your item!",
            "",
            "§6The updater is smart and will apply",
            "§6changes to the base stats of the item,",
            "§6keeping gemstones intact (for example)."
         },
         new String[0]
      );
   }

   public NumericStatFormula whenInitialized(Object var1) {
      if (var1 instanceof Integer) {
         return new NumericStatFormula(((Integer)var1).intValue(), 0.0, 0.0, 0.0);
      } else {
         throw new IllegalArgumentException("Must specify a whole number");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), (int)var1.getValue()));
      return var2;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      new RevisionInventory(var1.getPlayer(), var1.getEdited()).open(var1);
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
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
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new DoubleData(((Integer)var2.getValue()).intValue()) : null;
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<NumericStatFormula> var2) {
      if (var2.isPresent()) {
         NumericStatFormula var3 = (NumericStatFormula)var2.get();
         var1.add(ChatColor.GRAY + "Current Revision ID: " + ChatColor.GREEN + (int)var3.getBase());
      } else {
         var1.add(ChatColor.GRAY + "Current Revision ID: " + ChatColor.GREEN + "1");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to increase this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to decrease this value.");
   }

   @NotNull
   public DoubleData getClearStatData() {
      return new DoubleData(0.0);
   }
}
