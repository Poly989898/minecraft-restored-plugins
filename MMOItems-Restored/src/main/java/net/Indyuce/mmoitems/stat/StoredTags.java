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
   public RandomStatData<StoredTagsData> whenInitialized(Object var1) {
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
   public void whenDisplayed(List<String> var1, Optional<RandomStatData<StoredTagsData>> var2) {
      throw new NotImplementedException();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StoredTagsData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StoredTagsData var1) {
      return new ArrayList<>(var1.getTags());
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      var1.setData(ItemStats.STORED_TAGS, new StoredTagsData(var1.getNBT()));
   }

   @Nullable
   public StoredTagsData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return new StoredTagsData(var1);
   }

   @NotNull
   public StoredTagsData getClearStatData() {
      return new StoredTagsData(new ArrayList<>());
   }
}
