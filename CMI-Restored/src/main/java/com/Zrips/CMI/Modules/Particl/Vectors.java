package com.Zrips.CMI.Modules.Particl;

import org.bukkit.util.Vector;

public class Vectors {
  Vector p1 = null;
  Vector p2 = null;

  public void setp1(Vector vector) {
    this.p1 = vector;
  }

  public Vector getp1() {
    return this.p1;
  }

  public void setp2(Vector vector) {
    this.p2 = vector;
  }

  public Vector getp2() {
    return this.p2;
  }
}
