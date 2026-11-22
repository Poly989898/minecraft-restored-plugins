package com.Zrips.CMI.Modules.TabList;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import org.bukkit.entity.Player;

public class CMITabSortPlayerData {
  private final Player player;
  private String group = "";
  private String world = "";
  private String name = "";
  private String nickName = "";
  private int weight = 0;
  private double balance = 0.0;
  private int previousOrder = 0;

  public CMITabSortPlayerData(Player newPlayer) {
    this.player = newPlayer;
    this.update();
  }

  public Player getPlayer() {
    return this.player;
  }

  public String getGroup() {
    return this.group;
  }

  public String getWorld() {
    return this.world;
  }

  public int getWeight() {
    return this.weight;
  }

  public String getName() {
    return this.name;
  }

  public String getNickName() {
    return this.nickName;
  }

  public double getBalance() {
    return this.balance;
  }

  public void update() {
    if (this.player != null && this.player.isOnline()) {
      this.name = this.player.getName();
      this.group = CMI.getInstance().getPermissionsManager().getMainGroup(this.player);
      this.world = this.player.getWorld() == null ? "" : this.player.getWorld().getName();
      this.weight =
          CMI.getInstance()
              .getPermissionsManager()
              .getPermissionInfo(this.player, PermissionsManager.CMIPerm.tablistweight_$1, 60000L)
              .getMaxValue(0);
      CMIUser cmiUser = CMIUser.getUser(this.player);
      if (cmiUser != null) {
        this.balance = cmiUser.getBalance();
        this.nickName = cmiUser.getNickName();
      }

      this.group = this.group == null ? "" : this.group;
      this.nickName = this.nickName == null ? this.name : this.nickName;
    }
  }

  public int getPreviousOrder() {
    return this.previousOrder;
  }

  public void setPreviousOrder(int value) {
    this.previousOrder = value;
  }
}
