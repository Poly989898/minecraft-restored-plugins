package com.Zrips.CMI.Modules.Portals;

import org.bukkit.util.Vector;

public class CMIPlane {
  private Vector center;
  private CMIVector2D sizeMin;
  private CMIVector2D sizeMax;

  public CMIPlane(Vector vector, CMIVector2D cmiVector2D, CMIVector2D cmiVector2D2) {
    this.center = vector;
    this.sizeMin = cmiVector2D;
    this.sizeMax = cmiVector2D2;
  }

  public Vector getCenter() {
    return this.center;
  }

  public void setCenter(Vector vector) {
    this.center = vector;
  }

  public CMIVector2D getSizeMin() {
    return this.sizeMin;
  }

  public void setSizeMin(CMIVector2D cmiVector2D) {
    this.sizeMin = cmiVector2D;
  }

  public CMIVector2D getSizeMax() {
    return this.sizeMax;
  }

  public void setSizeMax(CMIVector2D cmiVector2D) {
    this.sizeMax = cmiVector2D;
  }
}
