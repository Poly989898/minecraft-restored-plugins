package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Unbreakable extends BooleanStat {
   public Unbreakable() {
      super("UNBREAKABLE", Material.ANVIL, "Unbreakable", new String[]{"Infinite durability if set to true."}, new String[0]);
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.getMeta().setUnbreakable(true);
         itemStackBuilder.addItemTag(this.getAppliedNBT(booleanData));
         itemStackBuilder.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData booleanData) {
      ArrayList list = new ArrayList();
      if (booleanData.isEnabled()) {
         list.add(new ItemTag(this.getNBTPath(), true));
      }

      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.BOOLEAN));
      }

      BooleanData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   @Override
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new BooleanData((Boolean)tagAtPath.getValue()) : null;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "Unbreakable";
   }
}
