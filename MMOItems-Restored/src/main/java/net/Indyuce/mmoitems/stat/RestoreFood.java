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
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (volatileMMOItem.hasData(ItemStats.RESTORE_FOOD)) {
         DoubleData data = (DoubleData)volatileMMOItem.getData(ItemStats.RESTORE_FOOD);
         int count = SilentNumbers.ceil(data.getValue() - (flag ? this.getFoodRestored(volatileMMOItem.getNBT().getItem()) : 0));
         if (count != 0) {
            MMOUtils.feed(player, count);
         }
      }
   }

   private int getFoodRestored(ItemStack item) {
      try {
         return MythicLib.plugin.getVersion().getWrapper().getFoodRestored(item);
      } catch (Exception exception) {
         return 0;
      }
   }
}
