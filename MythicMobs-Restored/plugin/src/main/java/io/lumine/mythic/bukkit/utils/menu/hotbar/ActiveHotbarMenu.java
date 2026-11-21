package io.lumine.mythic.bukkit.utils.menu.hotbar;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.UUID;

public class ActiveHotbarMenu<T> implements Terminable {
   private final UUID player;
   private final HotbarMenu<T> menu;
   private final T state;
   private long monitorTimestamp;

   public ActiveHotbarMenu(UUID player, HotbarMenu<T> menu, T state) {
      this.player = player;
      this.menu = menu;
      this.state = state;
   }

   @Override
   public void close() throws Exception {
      HotbarMenuHelper.registerClosed(this.player);
   }

   public UUID getPlayer() {
      return this.player;
   }

   public HotbarMenu<T> getMenu() {
      return this.menu;
   }

   public T getState() {
      return this.state;
   }

   public long getMonitorTimestamp() {
      return this.monitorTimestamp;
   }

   public void setMonitorTimestamp(long monitorTimestamp) {
      this.monitorTimestamp = monitorTimestamp;
   }
}
