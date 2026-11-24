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
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepMods()) {
         for (StatHistory statHistory : event.getNewMMOItem().getStatHistories()) {
            statHistory.clearModifiersBonus();
         }

         for (StatHistory statHistory : event.getOldMMOItem().getStatHistories()) {
            StatHistory statHistory2 = event.getNewMMOItem().computeStatHistory(statHistory.getItemStat());

            for (UUID allModifier : statHistory.getAllModifiers()) {
               StatData modifiersBonus = statHistory.getModifiersBonus(allModifier);
               if (modifiersBonus != null) {
                  statHistory2.registerModifierBonus(allModifier, ((Mergeable)modifiersBonus).clone());
               }
            }
         }
      }
   }
}
