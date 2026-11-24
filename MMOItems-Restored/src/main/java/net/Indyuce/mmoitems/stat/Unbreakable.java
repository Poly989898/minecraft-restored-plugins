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
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull BooleanData var2) {
      if (var2.isEnabled()) {
         var1.getMeta().setUnbreakable(true);
         var1.addItemTag(this.getAppliedNBT(var2));
         var1.getMeta().addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
      }
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData var1) {
      ArrayList var2 = new ArrayList();
      if (var1.isEnabled()) {
         var2.add(new ItemTag(this.getNBTPath(), true));
      }

      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.BOOLEAN));
      }

      BooleanData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   @Override
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new BooleanData((Boolean)var2.getValue()) : null;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "Unbreakable";
   }
}
