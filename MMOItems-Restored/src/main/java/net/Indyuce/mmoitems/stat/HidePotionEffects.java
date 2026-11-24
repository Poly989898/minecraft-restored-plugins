package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.Arrays;
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
public class HidePotionEffects extends BooleanStat {
   public static final ItemFlag ITEM_FLAG = (ItemFlag)UtilityMethods.resolveEnumField(
      ItemFlag::valueOf, () -> Arrays.stream(ItemFlag.values()).findAny().get(), new String[]{"HIDE_ADDITIONAL_TOOLTIP", "HIDE_POTION_EFFECTS"}
   );

   public HidePotionEffects() {
      super(
         "HIDE_POTION_EFFECTS",
         Material.POTION,
         "Hide Additional Tooltips",
         new String[]{"Hides potion effects & additional", "vanilla tooltips from the item lore."},
         new String[0],
         Material.POTION,
         Material.SPLASH_POTION,
         Material.LINGERING_POTION,
         Material.TIPPED_ARROW
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.getMeta().addItemFlags(new ItemFlag[]{ITEM_FLAG});
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta().hasItemFlag(ITEM_FLAG)) {
         readMMOItem.setData(ItemStats.HIDE_POTION_EFFECTS, new BooleanData(true));
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
