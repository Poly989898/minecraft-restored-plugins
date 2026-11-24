package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.api.event.MMOItemReforgeFinishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFFKeepAmount implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeFinishEvent event) {
      event.getFinishedItem().setAmount(event.getReforger().getNBTItem().getItem().getAmount());
   }
}
