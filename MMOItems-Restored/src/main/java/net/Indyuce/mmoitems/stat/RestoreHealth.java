package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RestoreHealth extends DoubleStat implements PlayerConsumable {
   public RestoreHealth() {
      super("RESTORE_HEALTH", Material.RED_DYE, "Health Restoration", new String[]{"Health given when consumed."}, new String[]{"consumable"});
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (!player.isDead() && !(player.getHealth() <= 0.0)) {
         if (volatileMMOItem.hasData(ItemStats.RESTORE_HEALTH)) {
            DoubleData data = (DoubleData)volatileMMOItem.getData(ItemStats.RESTORE_HEALTH);
            if (data.getValue() != 0.0) {
               MMOUtils.heal(player, data.getValue());
            }
         }
      }
   }
}
