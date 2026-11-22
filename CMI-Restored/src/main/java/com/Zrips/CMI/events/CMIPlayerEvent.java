package com.Zrips.CMI.events;

import org.bukkit.entity.Player;

public class CMIPlayerEvent extends CMIEvent {
  private final Player player;

  public CMIPlayerEvent(Player eventPlayer, boolean flag) {
    super(flag);
    this.player = eventPlayer;
  }

  public CMIPlayerEvent(Player eventPlayer) {
    this.player = eventPlayer;
  }

  public Player getPlayer() {
    return this.player;
  }
}
