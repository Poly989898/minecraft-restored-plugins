package com.Zrips.CMI.Modules.Vanish;

public class VanishActionStateBoolean extends VanishActionState {
  boolean state = false;

  public VanishActionStateBoolean() {}

  public VanishActionStateBoolean(boolean flag) {
    this.state = flag;
  }

  @Override
  public String toString() {
    return this.state ? "1" : "0";
  }

  public VanishActionStateBoolean fromString(String string) {
    this.state = string.equals("1");
    return this;
  }

  @Override
  public boolean is() {
    return this.state;
  }
}
