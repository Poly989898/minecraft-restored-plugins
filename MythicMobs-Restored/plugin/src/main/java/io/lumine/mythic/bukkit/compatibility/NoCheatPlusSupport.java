package io.lumine.mythic.bukkit.compatibility;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import io.lumine.mythic.bukkit.utils.Events;
import java.util.UUID;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NoCheatPlusSupport {
   public NoCheatPlusSupport() {
      Events.subscribe(PlayerChangedWorldEvent.class).handler(event -> NCPExemptionManager.unexempt(event.getPlayer()));
      Events.subscribe(PlayerQuitEvent.class).handler(event -> NCPExemptionManager.unexempt(event.getPlayer()));
   }

   public void exemptPlayer(UUID uuid) {
      NCPExemptionManager.exemptPermanently(uuid);
   }

   public void unexemptPlayer(UUID uuid) {
      NCPExemptionManager.unexempt(uuid);
   }
}
