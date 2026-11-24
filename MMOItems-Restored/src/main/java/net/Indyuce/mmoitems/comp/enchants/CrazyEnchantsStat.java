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

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull CrazyEnchantsData var2) {
      Map<CEnchantment, Integer> var3 = var2.getEnchants();

      for (Entry<CEnchantment, Integer> var5 : var3.entrySet()) {
         CEnchantment var6 = (CEnchantment)var5.getKey();
         int var7 = (Integer)var5.getValue();
         var1.getLore().insert(0, Methods.color(var6.getColor() + var6.getCustomName() + " " + CrazyEnchantments.getInstance().convertLevelString(var7)));
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      Map var2 = CrazyEnchantments.getInstance().getEnchantments(var1.getNBT().getItem());
      if (var2.size() > 0) {
         var1.setData(this, new CrazyEnchantsData(var2));
      }
   }

   @Nullable
   @Override
   public RandomStatData<CrazyEnchantsData> whenInitialized(Object var1) {
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
   public void whenDisplayed(List<String> var1, Optional<RandomStatData<CrazyEnchantsData>> var2) {
      throw new NotImplementedException();
   }

   @Nullable
   public CrazyEnchantsData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull CrazyEnchantsData var1) {
      return new ArrayList<>();
   }

   public CrazyEnchantsData getClearStatData() {
      return new CrazyEnchantsData(new HashMap<>());
   }
}
