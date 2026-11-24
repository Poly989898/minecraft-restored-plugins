package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RestoreMana extends DoubleStat implements PlayerConsumable {
   public RestoreMana() {
      super("RESTORE_MANA", Material.LAPIS_LAZULI, "Restore Mana", new String[]{"The amount of mana", "your consumable restores."}, new String[]{"consumable"});
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (var1.hasData(ItemStats.RESTORE_MANA)) {
         DoubleData var4 = (DoubleData)var1.getData(ItemStats.RESTORE_MANA);
         if (var4.getValue() != 0.0) {
            PlayerData.get(var2).getRPG().giveMana(var4.getValue());
         }
      }
   }
}
