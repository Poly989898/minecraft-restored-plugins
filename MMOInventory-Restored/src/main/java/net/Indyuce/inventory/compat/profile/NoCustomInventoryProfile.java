package net.Indyuce.inventory.compat.profile;

import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import net.Indyuce.inventory.listener.option.NoCustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NoCustomInventoryProfile implements Listener {
   @EventHandler
   public void a(ProfileSelectEvent event) {
      NoCustomInventory.putPlaceholderItems(event.getPlayer());
   }
}
