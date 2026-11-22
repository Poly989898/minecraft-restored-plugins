package com.Zrips.CMI.Modules.Jail;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CMIJailCell {
  private int id = 0;
  private CMILocation teleportInLocation;
  private World world;
  private String worldName = null;
  private Set<UUID> jailedOnes = new LinkedHashSet<>();
  private CMIJail jail;

  public CMIJailCell(CMIJail cmiJail) {
    this.jail = cmiJail;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(int value) {
    this.id = value;
  }

  public void loadJailed(String string) throws Exception {
    if (string != null && string.contains(":")) {
      String[] strings = string.split(":");

      try {
        String[] args = strings;
        int value = strings.length;

        for (int argIndex = 0; argIndex < value; argIndex++) {
          String arg = args[argIndex];
          UUID uuid = UUID.fromString(arg);
          if (uuid != null) {
            this.jailedOnes.add(uuid);
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
        throw new Exception("Invalid jail cell jailed information...");
      }
    } else {
      throw new Exception("Invalid jail cell jailed information...");
    }
  }

  public void teleportToCell(CMIUser cmiUser) {
    if (cmiUser.getPlayer().getVehicle() != null) {
      CMI.getInstance().getAnimationManager().removePlayerFromChair(cmiUser.getPlayer());
    }

    CMIScheduler.runTask(
        CMI.getInstance(),
        () -> {
          if (cmiUser.isOnline()) {
            CMI.getInstance()
                .getTeleportHandler()
                .teleportPlayer(
                    cmiUser.getPlayer(), this.getTeleportInLocation(), TeleportCause.PLUGIN);
          }
        });
  }

  public CMILocation getTeleportInLocation() {
    return this.teleportInLocation;
  }

  @Deprecated
  public void setTeleportInLocation(Location location) {
    if (location != null) {
      this.teleportInLocation = new CMILocation(location);
    }
  }

  public void setTeleportInLocation(CMILocation cmiLocation) {
    this.teleportInLocation = cmiLocation;
  }

  public World getWorld() {
    return this.world;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
    this.worldName = newWorld.getName();
  }

  public void setWorld(String string) {
    this.worldName = string;
  }

  public String getWorldName() {
    return this.worldName == null
        ? (this.world == null ? null : this.world.getName())
        : this.worldName;
  }

  public Set<UUID> getJailed() {
    return this.jailedOnes;
  }

  public void setJailed(HashSet<UUID> hashSet) {
    this.jailedOnes = hashSet;
  }

  public void addJailed(UUID uuid) {
    this.jailedOnes.add(uuid);
  }

  public void removeJailed(UUID uuid) {
    this.jailedOnes.remove(uuid);
  }

  public CMIJail getJail() {
    return this.jail;
  }
}
