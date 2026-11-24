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
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      double var4 = this.getSaturationRestored(var1.getNBT().getItem());
      double var6 = var1.hasData(ItemStats.RESTORE_SATURATION) ? ((DoubleData)var1.getData(ItemStats.RESTORE_SATURATION)).getValue() : var4;
      if (var3) {
         var6 -= var4;
      }

      if (var6 != 0.0) {
         MMOUtils.saturate(var2, var6);
      }
   }

   private float getSaturationRestored(ItemStack var1) {
      try {
         return MythicLib.plugin.getVersion().getWrapper().getSaturationRestored(var1);
      } catch (Exception var3) {
         return 0.0F;
      }
   }
}
