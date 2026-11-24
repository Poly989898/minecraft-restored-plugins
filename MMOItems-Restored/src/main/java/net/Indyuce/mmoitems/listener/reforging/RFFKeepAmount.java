package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.api.event.MMOItemReforgeFinishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFFKeepAmount implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeFinishEvent var1) {
      var1.getFinishedItem().setAmount(var1.getReforger().getNBTItem().getItem().getAmount());
   }
}
