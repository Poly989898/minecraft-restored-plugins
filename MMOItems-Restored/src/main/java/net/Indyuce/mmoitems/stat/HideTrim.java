package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
@VersionDependant(version = {1, 20})
public class HideTrim extends BooleanStat {
   public HideTrim() {
      super("HIDE_ARMOR_TRIM", Material.LEATHER_CHESTPLATE, "Hide Armor Trim", new String[]{"Hides armor trim from item lore."}, new String[]{"armor", "skin"});
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
         readMMOItem.setData(ItemStats.HIDE_ARMOR_TRIM, new BooleanData(true));
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
