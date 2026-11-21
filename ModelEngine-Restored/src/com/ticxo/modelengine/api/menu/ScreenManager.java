package com.ticxo.modelengine.api.menu;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

@Getter
public class ScreenManager {
   private final Map<Inventory, AbstractScreen> screens = Maps.newConcurrentMap();

   public void registerScreen(AbstractScreen screen) {
      this.screens.put(screen.inventory, screen);
   }

   public void unregisterScreen(Inventory inventory) {
      this.screens.remove(inventory);
   }

   public void unregisterScreen(AbstractScreen screen) {
      this.screens.remove(screen.inventory);
   }

   public AbstractScreen getScreen(Inventory inventory) {
      return this.screens.get(inventory);
   }

   public boolean isScreen(Inventory inventory) {
      return this.screens.containsKey(inventory);
   }

   public void updateAllScreens() {
      for (AbstractScreen screen : this.screens.values()) {
         screen.onTick();
      }
   }
}
