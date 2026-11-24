package net.Indyuce.mmoitems.comp.enchants;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrazyEnchantsStat extends ItemStat<RandomStatData<CrazyEnchantsData>, CrazyEnchantsData> implements InternalStat {
   public CrazyEnchantsStat() {
      super("CRAZY_ENCHANTS", Material.BOOK, "Advanced Enchants", new String[0], new String[0]);
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull CrazyEnchantsData crazyEnchantsData) {
      Map<CEnchantment, Integer> enchants = crazyEnchantsData.getEnchants();

      for (Entry<CEnchantment, Integer> entry : enchants.entrySet()) {
         CEnchantment key = (CEnchantment)entry.getKey();
         int count = (Integer)entry.getValue();
         itemStackBuilder.getLore().insert(0, Methods.color(key.getColor() + key.getCustomName() + " " + CrazyEnchantments.getInstance().convertLevelString(count)));
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      Map enchantments = CrazyEnchantments.getInstance().getEnchantments(readMMOItem.getNBT().getItem());
      if (enchantments.size() > 0) {
         readMMOItem.setData(this, new CrazyEnchantsData(enchantments));
      }
   }

   @Nullable
   @Override
   public RandomStatData<CrazyEnchantsData> whenInitialized(Object obj) {
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
   public void whenDisplayed(List<String> texts, Optional<RandomStatData<CrazyEnchantsData>> optional) {
      throw new NotImplementedException();
   }

   @Nullable
   public CrazyEnchantsData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull CrazyEnchantsData crazyEnchantsData) {
      return new ArrayList<>();
   }

   public CrazyEnchantsData getClearStatData() {
      return new CrazyEnchantsData(new HashMap<>());
   }
}
