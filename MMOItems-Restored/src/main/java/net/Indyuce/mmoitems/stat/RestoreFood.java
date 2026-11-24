package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RestoreFood extends DoubleStat implements PlayerConsumable {
   public RestoreFood() {
      super("RESTORE_FOOD", Material.PORKCHOP, "Food Restoration", new String[]{"Food units given when consumed."}, new String[]{"consumable"});
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (var1.hasData(ItemStats.RESTORE_FOOD)) {
         DoubleData var4 = (DoubleData)var1.getData(ItemStats.RESTORE_FOOD);
         int var5 = SilentNumbers.ceil(var4.getValue() - (var3 ? this.getFoodRestored(var1.getNBT().getItem()) : 0));
         if (var5 != 0) {
            MMOUtils.feed(var2, var5);
         }
      }
   }

   private int getFoodRestored(ItemStack var1) {
      try {
         return MythicLib.plugin.getVersion().getWrapper().getFoodRestored(var1);
      } catch (Exception var3) {
         return 0;
      }
   }
}
