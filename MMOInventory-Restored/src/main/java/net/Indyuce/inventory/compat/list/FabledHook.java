package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

public class FabledHook implements LevelModule, ClassModule {
   @Override
   public int getLevel(InventoryHandler player) {
      PlayerData rpgdata = this.getPlayerData(player);
      return rpgdata.hasClass() ? rpgdata.getMainClass().getLevel() : 0;
   }

   @Override
   public String getClass(InventoryHandler player) {
      PlayerData rpgdata = this.getPlayerData(player);
      return rpgdata.hasClass() ? rpgdata.getMainClass().getData().getName() : "";
   }

   private PlayerData getPlayerData(InventoryHandler player) {
      return Fabled.getData(player.getPlayer());
   }
}
