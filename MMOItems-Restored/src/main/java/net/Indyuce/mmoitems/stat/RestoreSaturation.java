package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
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

public class RestoreSaturation extends DoubleStat implements PlayerConsumable {
   public RestoreSaturation() {
      super("RESTORE_SATURATION", Material.GOLDEN_CARROT, "Saturation Restoration", new String[]{"Saturation given when consumed."}, new String[]{"consumable"});
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      double saturationRestored = this.getSaturationRestored(volatileMMOItem.getNBT().getItem());
      double value = volatileMMOItem.hasData(ItemStats.RESTORE_SATURATION) ? ((DoubleData)volatileMMOItem.getData(ItemStats.RESTORE_SATURATION)).getValue() : saturationRestored;
      if (flag) {
         value -= saturationRestored;
      }

      if (value != 0.0) {
         MMOUtils.saturate(player, value);
      }
   }

   private float getSaturationRestored(ItemStack item) {
      try {
         return MythicLib.plugin.getVersion().getWrapper().getSaturationRestored(item);
      } catch (Exception exception) {
         return 0.0F;
      }
   }
}
