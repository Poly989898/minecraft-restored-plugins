package com.Zrips.CMI.Modules.Jail;

import com.Zrips.CMI.Modules.Portals.CuboidArea;
import java.util.HashMap;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Location;

public class CMIJail {
  private String name;
  private CuboidArea area;
  private CMILocation safeLoc;
  private boolean preventLeave = true;
  private HashMap<Integer, CMIJailCell> cells = new HashMap<>();

  public CMIJail() {
    this.area = new CuboidArea(null);
  }

  public boolean containsLoc(Location location) {
    return this.area.containsLoc(location);
  }

  public boolean containsLoc(Location location, int value) {
    return this.area.containsLoc(location, value);
  }

  public CuboidArea getArea() {
    return this.area;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public boolean checkCollision(CuboidArea cuboidArea) {
    return this.area != null && cuboidArea != null && this.area.checkCollision(cuboidArea);
  }

  public CuboidArea loadBounds(String string) throws Exception {
    if (string != null && string.contains(":")) {
      String[] strings = string.split(":");

      try {
        String worldName = strings[0];
        double number = Double.parseDouble(strings[1]);
        double firstY = Double.parseDouble(strings[2]);
        double firstZ = Double.parseDouble(strings[3]);
        double secondX = Double.parseDouble(strings[4]);
        double secondY = Double.parseDouble(strings[5]);
        double secondZ = Double.parseDouble(strings[6]);
        CuboidArea cuboidArea =
            new CuboidArea(
                new CMILocation(worldName, number, firstY, firstZ),
                new CMILocation(worldName, secondX, secondY, secondZ));
        this.setArea(cuboidArea);
      } catch (Exception exception) {
        exception.printStackTrace();
        throw new Exception("Invalid portal physical location...");
      }

      return this.area;
    } else {
      throw new Exception("Invalid portal physical location...");
    }
  }

  public void setArea(CuboidArea cuboidArea) {
    this.area = cuboidArea;
  }

  public Location getOutsideLocation() {
    return this.safeLoc;
  }

  @Deprecated
  public void setOutsideLocation(Location location) {
    if (location != null) {
      this.safeLoc = new CMILocation(location);
    }
  }

  public void setOutsideLocation(CMILocation cmiLocation) {
    this.safeLoc = cmiLocation;
  }

  public HashMap<Integer, CMIJailCell> getCells() {
    return this.cells;
  }

  public void setCells(HashMap<Integer, CMIJailCell> hashMap) {
    this.cells = hashMap;
  }

  public void addCell(CMIJailCell cmiJailCell) {
    if (cmiJailCell.getId() == 0) {
      cmiJailCell.setId(this.getNextCellId());
    }

    this.cells.put(cmiJailCell.getId(), cmiJailCell);
  }

  public int getCountInCells() {
    if (this.cells.isEmpty()) {
      return 0;
    }

    int value = 0;

    for (var entry : this.cells.entrySet()) {
      value += ((CMIJailCell) entry.getValue()).getJailed().size();
    }

    return value;
  }

  public int getNextCellId() {
    if (this.cells.isEmpty()) {
      return 1;
    }

    int value = 1;

    for (var entry : this.cells.entrySet()) {
      if ((Integer) entry.getKey() >= value) {
        value = (Integer) entry.getKey() + 1;
      }
    }

    return value;
  }

  public boolean isPreventLeave() {
    return this.preventLeave;
  }

  public void setPreventLeave(boolean flag) {
    this.preventLeave = flag;
  }
}
