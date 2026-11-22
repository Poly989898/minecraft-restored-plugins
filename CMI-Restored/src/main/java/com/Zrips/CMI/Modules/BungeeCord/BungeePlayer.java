package com.Zrips.CMI.Modules.BungeeCord;

import java.util.UUID;

public class BungeePlayer {
  private String playerName;
  private String playerDisplayName;
  private String nickName;
  private BungeeCordServer bungeeServer;
  private UUID uuid;
  private boolean vanished = false;
  private String previousServer = null;
  private Long serverSwitchTime = 0L;

  public boolean getVanished() {
    return this.vanished;
  }

  public BungeePlayer setVanished(boolean flag) {
    this.vanished = flag;
    return this;
  }

  public String getName() {
    return this.playerName;
  }

  public BungeePlayer setName(String string) {
    this.playerName = string;
    return this;
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public BungeePlayer setUniqueId(UUID newUuid) {
    this.uuid = newUuid;
    return this;
  }

  public String getPlayerDisplayName() {
    return this.playerDisplayName == null ? this.getName() : this.playerDisplayName;
  }

  public BungeePlayer setPlayerDisplayName(String string) {
    this.playerDisplayName = string;
    return this;
  }

  public BungeeCordServer getBungeeServer() {
    return this.bungeeServer;
  }

  public void setBungeeServer(BungeeCordServer bungeeCordServer) {
    this.bungeeServer = bungeeCordServer;
  }

  public String getServerName() {
    return this.getBungeeServer() == null ? "" : this.getBungeeServer().getName();
  }

  public String getNickName() {
    return this.nickName;
  }

  public void setNickName(String string) {
    this.nickName = string;
  }

  public Long getServerSwitchTime() {
    return this.serverSwitchTime;
  }

  public void setServerSwitchTime(Long serverSwitchTimeValue) {
    this.serverSwitchTime = serverSwitchTimeValue;
  }

  public String getPreviousServer() {
    return this.previousServer;
  }

  public void setPreviousServer(String string) {
    this.previousServer = string;
  }
}
