package com.Zrips.CMI.Modules.FlightCharge;

import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class traveledDistance {
  private CMIUser user;
  private Location loc;
  private Long time = 0L;

  traveledDistance(CMIUser cmiUser) {
    this.user = cmiUser;
    if (cmiUser.isOnline()) {
      this.updateLocation();
    }
  }

  public boolean toSoonToCheck() {
    return System.currentTimeMillis() - this.time < 1000L;
  }

  public void updateLocation() {
    if (this.user.isOnline()) {
      Player player = this.user.getPlayer();
      if (player != null) {
        this.loc = player.getLocation();
        this.time = System.currentTimeMillis();
      }
    }
  }

  public Double getDistanceTraveled() {
    if (!this.user.isOnline()) {
      return 0.0;
    }

    Player player = this.user.getPlayer();
    return player != null && this.loc != null && player.getWorld().equals(this.loc.getWorld())
        ? player.getLocation().distance(this.loc)
        : 0.0;
  }

  public Long sinceLastUpdate() {
    return System.currentTimeMillis() - this.time;
  }

  public CMIUser getUser() {
    return this.user;
  }
}
