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

public class RestoreStamina extends DoubleStat implements PlayerConsumable {
   public RestoreStamina() {
      super(
         "RESTORE_STAMINA",
         Material.LIGHT_GRAY_DYE,
         "Restore Stamina",
         new String[]{"The amount of stamina/power", "your consumable restores."},
         new String[]{"consumable"}
      );
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (volatileMMOItem.hasData(ItemStats.RESTORE_STAMINA)) {
         DoubleData data = (DoubleData)volatileMMOItem.getData(ItemStats.RESTORE_STAMINA);
         if (data.getValue() != 0.0) {
            PlayerData.get(player).getRPG().giveStamina(data.getValue());
         }
      }
   }
}
