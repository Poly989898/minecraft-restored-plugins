package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicPostReloadedEvent extends Event {
   private final MythicBukkit inst;
   private static final HandlerList handlers = new HandlerList();

   public MythicPostReloadedEvent(MythicBukkit inst) {
      this.inst = inst;
   }

   public MythicBukkit getInstance() {
      return this.inst;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
