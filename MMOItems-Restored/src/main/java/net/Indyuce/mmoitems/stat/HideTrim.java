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
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull BooleanData var2) {
      if (var2.isEnabled()) {
         var1.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
         var1.setData(ItemStats.HIDE_ARMOR_TRIM, new BooleanData(true));
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData var1) {
      return new ArrayList<>();
   }

   @Nullable
   @Override
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }
}
