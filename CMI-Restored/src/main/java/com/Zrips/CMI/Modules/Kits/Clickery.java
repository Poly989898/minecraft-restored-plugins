package com.Zrips.CMI.Modules.Kits;

public class Clickery {
  private long time = 0L;
  private int times = 0;
  private int speed = 1;

  public Clickery() {
    this.time = System.currentTimeMillis();
  }

  private void addClick() {
    if (System.currentTimeMillis() - this.time < 1000L) {
      this.times++;
    } else {
      this.times = -3;
    }

    if (this.times > 3) {
      this.times = 0;
      this.speed = (int) (this.speed + (10.0 + this.speed * 0.3));
    }

    if (this.times < -2) {
      this.speed = 1;
    }

    this.time = System.currentTimeMillis();
  }

  public int getSpeed() {
    this.addClick();
    return this.speed;
  }
}
