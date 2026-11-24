package net.Indyuce.mmoitems.listener.reforging;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RFGKeepLore implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepLore()) {
         StringListData data = (StringListData)event.getOldMMOItem().getData(ItemStats.LORE);
         if (data != null) {
            ArrayList<String> texts = this.extractLore(data.getList(), event.getOptions().getKeepCase());
            if (texts.size() != 0) {
               StatHistory statHistory = event.getNewMMOItem().computeStatHistory(ItemStats.LORE);
               StringListData stringListData = (StringListData)statHistory.getOriginalData();

               for (String text : texts) {
                  stringListData.getList().add(text);
               }

               statHistory.setOriginalData(stringListData);
            }
         }
      }
   }

   @NotNull
   ArrayList<String> extractLore(@NotNull List<String> texts, @NotNull String text) {
      ArrayList list = new ArrayList();

      for (String text2 : texts) {
         if (text2.startsWith(text)) {
            list.add(text2);
         }
      }

      return list;
   }
}
