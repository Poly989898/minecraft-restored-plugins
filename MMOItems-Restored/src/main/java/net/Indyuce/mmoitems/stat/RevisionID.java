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

   public NumericStatFormula whenInitialized(Object obj) {
      if (obj instanceof Integer) {
         return new NumericStatFormula(((Integer)obj).intValue(), 0.0, 0.0, 0.0);
      } else {
         throw new IllegalArgumentException("Must specify a whole number");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), (int)doubleData.getValue()));
      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      new RevisionInventory(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
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
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new DoubleData(((Integer)tagAtPath.getValue()).intValue()) : null;
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<NumericStatFormula> optional) {
      if (optional.isPresent()) {
         NumericStatFormula numericStatFormula = (NumericStatFormula)optional.get();
         texts.add(ChatColor.GRAY + "Current Revision ID: " + ChatColor.GREEN + (int)numericStatFormula.getBase());
      } else {
         texts.add(ChatColor.GRAY + "Current Revision ID: " + ChatColor.GREEN + "1");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to increase this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to decrease this value.");
   }

   @NotNull
   public DoubleData getClearStatData() {
      return new DoubleData(0.0);
   }
}
