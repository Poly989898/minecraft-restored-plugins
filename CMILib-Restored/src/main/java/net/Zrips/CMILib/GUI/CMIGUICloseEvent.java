package net.Zrips.CMILib.GUI;

import net.Zrips.CMILib.events.EventAnnotation;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class CMIGUICloseEvent extends PlayerEvent {
  private static final HandlerList handlers = new HandlerList();
  private final CMIGui gui;

  public CMIGUICloseEvent(Player player, CMIGui gui) {
    super(player);
    this.gui = gui;
  }

  public static final HandlerList getHandlerList() {
    return handlers;
  }

  @EventAnnotation(info = "Fired when player closed CMI gui")
  public final HandlerList getHandlers() {
    return handlers;
  }

  public CMIGui getGui() {
    return this.gui;
  }
}
