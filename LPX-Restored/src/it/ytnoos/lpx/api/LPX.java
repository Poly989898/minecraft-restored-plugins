package it.ytnoos.lpx.api;

import it.ytnoos.lpx.api.adapter.InventoryViewAdapter;
import it.ytnoos.lpx.api.check.abstraction.CheckManager;
import it.ytnoos.lpx.api.protocol.PlayerRegistry;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public interface LPX {
   String getServer();

   LPXPlugin getPlugin();

   PlayerRegistry getRegistry();

   CheckManager getCheckManager();

   void disable(boolean flag);

   void reloadConfig();

   default Logger getLogger() {
      return this.getPlugin().getLogger();
   }

   InventoryViewAdapter getInventoryViewAdapter(Player player);

   boolean isUpdated();

   void setUpdated(boolean flag);

   long getFirstJoinTime();

   void setFirstJoinTime(long longValue);

   boolean canPacketLogHeavy();
}
