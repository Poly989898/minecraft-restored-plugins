package net.Indyuce.inventory.compat.list;

import net.Indyuce.inventory.compat.ClassModule;
import net.Indyuce.inventory.compat.LevelModule;
import net.Indyuce.inventory.inventory.InventoryHandler;

public class DefaultHook implements LevelModule, ClassModule {
   @Override
   public int getLevel(InventoryHandler player) {
      return player.getPlayer().getLevel();
   }

   @Override
   public String getClass(InventoryHandler player) {
      return "None";
   }
}
