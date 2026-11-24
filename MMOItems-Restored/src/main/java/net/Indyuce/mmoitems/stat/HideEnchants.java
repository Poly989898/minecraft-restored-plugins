package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class HideEnchants extends BooleanStat {
   public HideEnchants() {
      super(
         "HIDE_ENCHANTS",
         Material.BOOK,
         "Hide Enchantments",
         new String[]{"Enable to completely hide your item", "enchants. You can still see the glowing effect."},
         new String[0]
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
         readMMOItem.setData(ItemStats.HIDE_ENCHANTS, new BooleanData(true));
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData booleanData) {
      return new ArrayList<>();
   }

   @Nullable
   @Override
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }
}
