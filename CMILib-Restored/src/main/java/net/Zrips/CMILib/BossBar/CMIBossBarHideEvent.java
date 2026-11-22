package net.Zrips.CMILib.BossBar;

import net.Zrips.CMILib.events.EventAnnotation;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CMIBossBarHideEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  private boolean cancel;
  private BossBarInfo bossBar = null;

  public CMIBossBarHideEvent(BossBarInfo bossBar) {
    this.bossBar = bossBar;
  }

  @EventAnnotation(info = "Fired when CMI boss bar should be hidden. Can be canceled.")
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public final void setCancelled(boolean cancel) {
    this.cancel = cancel;
  }

  public final boolean isCancelled() {
    return this.cancel;
  }

  public BossBarInfo getBossBar() {
    return this.bossBar;
  }

  public void setBossBar(BossBarInfo bossBar) {
    this.bossBar = bossBar;
  }
}
