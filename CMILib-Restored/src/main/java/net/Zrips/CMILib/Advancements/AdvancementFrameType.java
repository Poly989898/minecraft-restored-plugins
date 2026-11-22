package net.Zrips.CMILib.Advancements;

public enum AdvancementFrameType {
  TASK("task"),
  GOAL("goal"),
  CHALLENGE("challenge");

  private String name;

  AdvancementFrameType(String name) {
    this.name = name;
  }

  public static AdvancementFrameType getFromString(String frameType) {
    try {
      for (AdvancementFrameType one : values()) {
        if (one.name.equalsIgnoreCase(frameType)) {
          return one;
        }
      }
    } catch (EnumConstantNotPresentException var5) {
    }

    return TASK;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
