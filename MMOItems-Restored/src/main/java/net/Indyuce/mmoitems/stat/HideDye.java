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
@VersionDependant(version = {1, 16, 3})
public class HideDye extends BooleanStat {
   public HideDye() {
      super(
         "HIDE_DYE",
         Material.CYAN_DYE,
         "Hide Dyed",
         new String[]{"Enable to hide the 'Dyed' tag from the item."},
         new String[0],
         Material.LEATHER_HELMET,
         Material.LEATHER_CHESTPLATE,
         Material.LEATHER_LEGGINGS,
         Material.LEATHER_BOOTS,
         Material.LEATHER_HORSE_ARMOR
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull BooleanData var2) {
      if (var2.isEnabled()) {
         var1.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
         var1.setData(ItemStats.HIDE_DYE, new BooleanData(true));
      }
   }

   @Nullable
   @Override
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }
}
