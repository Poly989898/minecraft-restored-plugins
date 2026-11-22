package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import org.bukkit.entity.Player;

public class CMIPlayerTeleportRequestEvent extends CMIPlayerCancellableEvent {
  private Player whoAccepts;
  private TeleportManager.TpAction action;

  public CMIPlayerTeleportRequestEvent(
      Player player, Player acceptingPlayer, TeleportManager.TpAction tpAction) {
    super(player);
    this.whoAccepts = acceptingPlayer;
    this.action = tpAction;
  }

  public Player getWhoOffers() {
    return this.getPlayer();
  }

  public Player getWhoAccepts() {
    return this.whoAccepts;
  }

  public TeleportManager.TpAction getAction() {
    return this.action;
  }
}
