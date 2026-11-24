package net.Indyuce.mmoitems.listener.reforging;

import java.util.UUID;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepModifications implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepMods()) {
         for (StatHistory var3 : var1.getNewMMOItem().getStatHistories()) {
            var3.clearModifiersBonus();
         }

         for (StatHistory var9 : var1.getOldMMOItem().getStatHistories()) {
            StatHistory var4 = var1.getNewMMOItem().computeStatHistory(var9.getItemStat());

            for (UUID var6 : var9.getAllModifiers()) {
               StatData var7 = var9.getModifiersBonus(var6);
               if (var7 != null) {
                  var4.registerModifierBonus(var6, ((Mergeable)var7).clone());
               }
            }
         }
      }
   }
}
