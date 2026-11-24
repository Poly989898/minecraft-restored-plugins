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
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (!var2.isDead() && !(var2.getHealth() <= 0.0)) {
         if (var1.hasData(ItemStats.RESTORE_HEALTH)) {
            DoubleData var4 = (DoubleData)var1.getData(ItemStats.RESTORE_HEALTH);
            if (var4.getValue() != 0.0) {
               MMOUtils.heal(var2, var4.getValue());
            }
         }
      }
   }
}
