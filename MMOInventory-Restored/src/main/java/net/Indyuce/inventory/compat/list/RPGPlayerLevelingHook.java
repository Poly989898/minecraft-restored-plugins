package net.Indyuce.inventory.compat.list;

import me.baks.rpl.PlayerList;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class RPGPlayerLevelingHook implements LevelModule {
   @Override
   public int getLevel(InventoryHandler player) {
      PlayerList playerList = PlayerList.getByName(player.getPlayer().getName());
      return playerList == null ? 0 : playerList.getPlayerLevel();
   }
}
