package it.ytnoos.lpx.tasks;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.api.protocol.CheckContainer;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearTask extends BukkitRunnable {
   private final LPXBukkit lpxBukkit;

   public ClearTask(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   public void run() {
      for (ProtocolPlayer protocolPlayer : this.lpxBukkit.getRegistry().getPlayers().values()) {
         for (CheckContainer checkContainer : protocolPlayer.getChecks().values()) {
            for (Object var9_o : checkContainer) { Check check = (Check) var9_o;
               check.getOptions().setVl(0);
               check.getOptions().setBuffer(0.0);
            }
         }
      }
   }

}
