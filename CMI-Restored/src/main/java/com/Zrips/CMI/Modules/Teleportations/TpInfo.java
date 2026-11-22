package com.Zrips.CMI.Modules.Teleportations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpInfo {
  private Player whoOffers;
  private Player whoAccepts;
  private TeleportManager.TpAction action;
  private Location loc = null;
  private long when;

  public TpInfo(Player player, Player acceptingPlayer, TeleportManager.TpAction tpAction) {
    this.whoOffers = player;
    this.whoAccepts = acceptingPlayer;
    this.action = tpAction;
    this.when = System.currentTimeMillis();
  }

  public Player getWhoOffers() {
    return this.whoOffers;
  }

  public void setWhoOffers(Player player) {
    this.whoOffers = player;
  }

  public Player getWhoAccepts() {
    return this.whoAccepts;
  }

  public void setWhoAccepts(Player player) {
    this.whoAccepts = player;
  }

  public TeleportManager.TpAction getAction() {
    return this.action;
  }

  public void setAction(TeleportManager.TpAction tpAction) {
    this.action = tpAction;
  }

  public long getWhen() {
    return this.when;
  }

  public void setWhen(long longValue) {
    this.when = longValue;
  }

  public Location getLoc() {
    return this.loc;
  }

  public void setLoc(Location location) {
    this.loc = location;
  }
}
