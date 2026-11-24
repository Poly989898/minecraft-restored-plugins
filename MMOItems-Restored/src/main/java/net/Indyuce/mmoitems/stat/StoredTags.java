package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StoredTagsData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoredTags extends ItemStat<RandomStatData<StoredTagsData>, StoredTagsData> implements InternalStat, GemStoneStat {
   public StoredTags() {
      super(
         "STORED_TAGS",
         Material.OAK_SIGN,
         "Stored Tags",
         new String[]{"You found a secret dev easter egg", "introduced during the 2020 epidemic!"},
         new String[0]
      );
   }

   @Nullable
   @Override
   public RandomStatData<StoredTagsData> whenInitialized(Object obj) {
      throw new NotImplementedException();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      throw new NotImplementedException();
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      throw new NotImplementedException();
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomStatData<StoredTagsData>> optional) {
      throw new NotImplementedException();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StoredTagsData storedTagsData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(storedTagsData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StoredTagsData storedTagsData) {
      return new ArrayList<>(storedTagsData.getTags());
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      readMMOItem.setData(ItemStats.STORED_TAGS, new StoredTagsData(readMMOItem.getNBT()));
   }

   @Nullable
   public StoredTagsData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return new StoredTagsData(itemTags);
   }

   @NotNull
   public StoredTagsData getClearStatData() {
      return new StoredTagsData(new ArrayList<>());
   }
}
