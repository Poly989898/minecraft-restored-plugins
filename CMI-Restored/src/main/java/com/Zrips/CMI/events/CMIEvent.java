package com.Zrips.CMI.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CMIEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  public CMIEvent() {}

  public CMIEvent(boolean flag) {
    super(flag);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
