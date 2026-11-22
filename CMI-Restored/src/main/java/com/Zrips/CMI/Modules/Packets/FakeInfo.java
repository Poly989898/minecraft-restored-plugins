package com.Zrips.CMI.Modules.Packets;

import org.bukkit.entity.Player;

public class FakeInfo {
  private int id = -1;
  private Player player;
  private Object obj;
  private Object entity;

  public Player getPlayer() {
    return this.player;
  }

  public FakeInfo setPlayer(Player newPlayer) {
    this.player = newPlayer;
    return this;
  }

  public Object getObj() {
    return this.obj;
  }

  public FakeInfo setObj(Object object) {
    this.obj = object;
    return this;
  }

  public int getId() {
    return this.id;
  }

  public FakeInfo setId(int value) {
    this.id = value;
    return this;
  }

  public Object getEntity() {
    return this.entity;
  }

  public void setEntity(Object object) {
    this.entity = object;
  }
}
