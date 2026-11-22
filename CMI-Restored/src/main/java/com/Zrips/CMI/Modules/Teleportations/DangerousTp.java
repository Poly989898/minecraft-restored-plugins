package com.Zrips.CMI.Modules.Teleportations;

import java.util.UUID;
import org.bukkit.Location;

public class DangerousTp {
  private UUID uuid;
  private Location location;
  private Long time;

  public DangerousTp(UUID playerUuid, Location targetLocation) {
    this.uuid = playerUuid;
    this.location = targetLocation;
    this.time = System.currentTimeMillis();
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(UUID playerUuid) {
    this.uuid = playerUuid;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(Location targetLocation) {
    this.location = targetLocation;
  }

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long timeValue) {
    this.time = timeValue;
  }
}
