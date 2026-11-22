package com.Zrips.CMI.Modules.Selection;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.events.CMISelectionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Cuboids.CMIBlockArea;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SelectionManager {
  protected CMI plugin;
  private static HashMap<UUID, CMISelectionVisualizer> playerSelection = new HashMap<>();

  public SelectionManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void hideSelection(Player player) {
    CMISelectionVisualizer cmiSelectionVisualizer = getPlayerSelection(player);
    cmiSelectionVisualizer.cancelAll();
  }

  public void updateLocations(Player player, Location location, Location secondLocation) {
    this.updateLocations(player, location, secondLocation, false);
  }

  public void updateLocations(
      Player player, Location location, Location secondLocation, boolean flag) {
    if (location != null && secondLocation != null) {
      getPlayerSelection(player).setPoint1(location).setPoint2(secondLocation);
      this.afterSelectionUpdate(player, flag);
    }
  }

  public static CMISelectionVisualizer getPlayerSelection(Player player) {
    return getPlayerSelection(player.getUniqueId());
  }

  public static CMISelectionVisualizer getPlayerSelection(UUID uuid) {
    return playerSelection.computeIfAbsent(uuid, playerUuid -> new CMISelectionVisualizer(uuid));
  }

  public void placeLoc1(Player player, Location location) {
    this.placeLoc1(player, location, false);
  }

  public void placeLoc1(Player player, Location location, boolean flag) {
    if (location != null) {
      getPlayerSelection(player).setPoint1(location);
      if (flag) {
        getPlayerSelection(player).showSelectionPoints();
      }
    }
  }

  public void placeLoc2(Player player, Location location) {
    this.placeLoc2(player, location, false);
  }

  public void placeLoc2(Player player, Location location, boolean flag) {
    if (location != null) {
      getPlayerSelection(player).setPoint2(location);
      if (flag) {
        getPlayerSelection(player).showSelectionPoints();
      }
    }
  }

  public void afterSelectionUpdate(Player player) {
    this.afterSelectionUpdate(player, false);
  }

  @Deprecated
  public void afterSelectionUpdate(Player player, boolean flag) {
    getPlayerSelection(player).updateParticles(flag);
  }

  @Deprecated
  public Location getPlayerLoc1(Player player) {
    return this.getPlayerLoc1(player.getUniqueId());
  }

  @Deprecated
  public Location getPlayerLoc1(String string) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
    return cmiUser == null ? null : this.getPlayerLoc1(cmiUser.getUniqueId());
  }

  @Deprecated
  public Location getPlayerLoc1(UUID uuid) {
    CMISelectionPoint cmiSelectionPoint = getPlayerSelection(uuid).getPoint1();
    return cmiSelectionPoint == null ? null : cmiSelectionPoint.toLocation();
  }

  @Deprecated
  public Location getPlayerLoc2(Player player) {
    return this.getPlayerLoc2(player.getUniqueId());
  }

  @Deprecated
  public Location getPlayerLoc2(String string) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
    return cmiUser == null ? null : this.getPlayerLoc2(cmiUser.getUniqueId());
  }

  @Deprecated
  public Location getPlayerLoc2(UUID uuid) {
    CMISelectionPoint cmiSelectionPoint = getPlayerSelection(uuid).getPoint2();
    return cmiSelectionPoint == null ? null : cmiSelectionPoint.toLocation();
  }

  @Deprecated
  public CuboidArea getSelectionCuboid(Player player) {
    return player == null ? null : this.getSelectionCuboid(player.getUniqueId());
  }

  public void setSelectionCuboid(Player player, CMIBlockArea cmiBlockArea) {
    getPlayerSelection(player)
        .setPoint1(player.getWorld(), cmiBlockArea.getLowPoint())
        .setPoint2(player.getWorld(), cmiBlockArea.getHighPoint());
    CMICuboidArea cmiCuboidArea = getPlayerSelection(player).getSelectedCuboid();
    if (cmiCuboidArea != null) {
      CuboidArea cuboidArea =
          new CuboidArea(
              cmiCuboidArea.getWorld(), cmiCuboidArea.getLowPoint(), cmiCuboidArea.getHighPoint());
      this.fireSelectionEvent(player, cuboidArea);
    }
  }

  public void setSelectionCuboid(Player player, CMICuboidArea cmiCuboidArea) {
    getPlayerSelection(player)
        .setPoint1(cmiCuboidArea.getLowLocation())
        .setPoint2(cmiCuboidArea.getHighLocation());
    CMICuboidArea selectedCuboid = getPlayerSelection(player).getSelectedCuboid();
    if (selectedCuboid != null) {
      CuboidArea cuboidArea =
          new CuboidArea(
              selectedCuboid.getWorld(),
              selectedCuboid.getLowPoint(),
              selectedCuboid.getHighPoint());
      this.fireSelectionEvent(player, cuboidArea);
    }
  }

  @Deprecated
  public void setSelectionCuboid(Player player, CuboidArea cuboidArea) {
    getPlayerSelection(player).setPoint1(cuboidArea.getLowLoc()).setPoint2(cuboidArea.getHighLoc());
    CMICuboidArea cmiCuboidArea = getPlayerSelection(player).getSelectedCuboid();
    CuboidArea cuboidSelection =
        new CuboidArea(
            cmiCuboidArea.getWorld(), cmiCuboidArea.getLowPoint(), cmiCuboidArea.getHighPoint());
    this.fireSelectionEvent(player, cuboidSelection);
  }

  private void fireSelectionEvent(Player player, CuboidArea cuboidArea) {
    CMISelectionEvent cmiSelectionEvent = new CMISelectionEvent(player, cuboidArea);
    Bukkit.getPluginManager().callEvent(cmiSelectionEvent);
  }

  @Deprecated
  public CuboidArea getSelectionCuboid(String string) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
    return cmiUser == null ? null : this.getSelectionCuboid(cmiUser.getUniqueId());
  }

  @Deprecated
  public CuboidArea getSelectionCuboid(UUID uuid) {
    if (uuid == null) {
      return null;
    }

    CMICuboidArea cmiCuboidArea = getPlayerSelection(uuid).getSelectedCuboid();
    return new CuboidArea(
        cmiCuboidArea.getWorld(), cmiCuboidArea.getLowPoint(), cmiCuboidArea.getHighPoint());
  }

  public boolean hasPlacedBoth(Player player) {
    return this.hasPlacedBoth(player.getUniqueId());
  }

  @Deprecated
  public boolean hasPlacedBoth(String string) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
    return cmiUser == null ? false : this.hasPlacedBoth(cmiUser.getUniqueId());
  }

  public boolean hasPlacedBoth(UUID uuid) {
    return getPlayerSelection(uuid).placedBothPoints();
  }

  @Deprecated
  public void showSelectionInfo(Player player) {
    this.showSelection(player);
  }

  @Deprecated
  public void showSelection(Player player) {
    getPlayerSelection(player).showFull();
  }

  @Deprecated
  public void showBounds(Player player, @Deprecated Visualizer visualizer) {
    getPlayerSelection(player).showFull();
  }

  @Deprecated
  public List<Location> getLocations(
      Location location, Location secondLocation, Vector vector, boolean flag) {
    var list = CMISelectionVisualizer.getLocations(location, secondLocation, vector, flag);
    ArrayList<Location> arrayList = new ArrayList();

    for (CMIVector3D cmiVector3D : list) {
      arrayList.add(cmiVector3D.toLocation(location.getWorld()));
    }

    return arrayList;
  }

  @Deprecated
  public boolean makeBorders(Player player) {
    return getPlayerSelection(player).makeBorders();
  }

  public void clearSelection(Player player) {
    this.clearSelection(player.getUniqueId());
  }

  public void clearSelection(UUID uuid) {
    getPlayerSelection(uuid).cancelAll();
    playerSelection.remove(uuid);
  }

  public boolean worldEdit(Player player) {
    LC.info_NoInformation.sendMessage(player, new Object[0]);
    return false;
  }

  public boolean worldEditUpdate(Player player) {
    LC.info_NoInformation.sendMessage(player, new Object[0]);
    return false;
  }
}
