package com.Zrips.CMI.Modules.Holograms.Animations;

public class CMIHologramValueChanger {
  private double target = 0.0;
  private double current = 0.0;
  private double changeSpeed = 0.0;
  private int direction = 1;

  public CMIHologramValueChanger() {
    this(0.0, 1.0);
  }

  public CMIHologramValueChanger(double number, double targetValue) {
    this.current = number;
    this.target = targetValue;
  }

  public CMIHologramValueChanger(double number, double targetValue, double speedValue) {
    this(number, targetValue);
    this.changeSpeed = speedValue;
    if (this.changeSpeed == 0.0) {
      this.current = this.target;
    }
  }

  public static double increment(double number, double endValue, int value, double stepScale) {
    return (endValue - number) / (value * stepScale);
  }

  public CMIHologramValueChanger increment(double number) {
    return this.increment(number, 20);
  }

  public CMIHologramValueChanger increment(double number, int value) {
    this.changeSpeed = increment(this.current, this.target, value, number);
    if (this.changeSpeed == 0.0) {
      this.current = this.target;
    }

    return this;
  }

  public double getTarget() {
    return this.target;
  }

  public void setTarget(double number) {
    this.target = number;
  }

  public double getCurrent() {
    return this.current;
  }

  public void setCurrent(double number) {
    this.current = number;
  }

  public double getChangeSpeed() {
    return this.changeSpeed;
  }

  public void setChangeSpeed(double number) {
    this.changeSpeed = number;
  }

  public double updateValue() {
    if (this.finished()) {
      return this.current;
    }

    this.current = this.current + this.changeSpeed * this.direction;
    if (this.changeSpeed > 0.0 && this.current > this.target) {
      this.current = this.target;
    }

    if (this.changeSpeed < 0.0 && this.current < this.target) {
      this.current = this.target;
    }

    return this.current;
  }

  public boolean finished() {
    return this.changeSpeed > 0.0 ? this.current >= this.target : this.current <= this.target;
  }
}
