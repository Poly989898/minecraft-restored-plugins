package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicStatsRegistrationEvent extends Event {
   private final StatExecutor manager;
   private static final HandlerList handlers = new HandlerList();

   public MythicStatsRegistrationEvent(StatExecutor manager) {
      this.manager = manager;
   }

   public StatExecutor getManager() {
      return this.manager;
   }

   public void register(StatType stat) {
      this.manager.registerPrimaryStat(stat);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
