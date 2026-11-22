package com.Zrips.CMI.Modules.Vanish;

public class VanishActionStateInt extends VanishActionState {
  int value = 0;

  public VanishActionStateInt() {}

  public VanishActionStateInt(int newValue) {
    this.value = newValue;
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

  public VanishActionStateInt fromString(String string) {
    try {
      this.value = Integer.parseInt(string);
    } catch (NumberFormatException numberFormatException) {
      numberFormatException.printStackTrace();
      this.value = -1;
    }

    return this;
  }

  @Override
  public boolean is() {
    return this.value > 0;
  }

  public int getValue() {
    return this.value;
  }
}
