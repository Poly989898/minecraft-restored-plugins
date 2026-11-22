package com.Zrips.CMI.Modules.TabList;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

public class TabListCache {
  private List<String> header = Arrays.asList("");
  private List<String> footer = Arrays.asList("");
  private String playerNameFormat = "";
  private Player player;
  private TabList tl;

  public TabListCache(Player newPlayer, TabList tabList) {
    this.player = newPlayer;
    this.tl = tabList;
  }

  public List<String> getHeader() {
    return this.header;
  }

  public void setHeader(List<String> list) {
    this.header = list;
  }

  public String getPlayerNameFormat() {
    return this.playerNameFormat;
  }

  public void setPlayerNameFormat(String string) {
    this.playerNameFormat = string;
  }

  public List<String> getFooter() {
    return this.footer;
  }

  public void setFooter(List<String> list) {
    this.footer = list;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public TabList getTl() {
    return this.tl;
  }

  public void setTl(TabList tabList) {
    this.tl = tabList;
  }
}
