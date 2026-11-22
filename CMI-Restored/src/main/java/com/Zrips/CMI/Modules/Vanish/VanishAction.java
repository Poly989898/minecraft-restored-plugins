package com.Zrips.CMI.Modules.Vanish;

public enum VanishAction {
  isVanished(4, false, "&eVanished"),
  damageToEntity(10, false, "&eCan deal damage to others"),
  playerDamage(11, false, "&eCan take damage"),
  itemPickup(12, false, "&eCan pickup items"),
  mobAggro(13, false, "&eMobs can target"),
  interaction(14, false, "&eCan physically interact"),
  noisyChest(15, false, "&eNoisy chest's"),
  informOnLeave(19, false, "&eShows leave message"),
  informOnJoin(20, false, "&eShows join message"),
  nightVision(16, true, "&eApply night vision"),
  bossbar(24, true, "&eShow bossBar when vanished"),
  afkcommands(25, false, "&eTrigger afk commands when vanished"),
  PrivateMessages(22, false, "&eAccept private messages"),
  relogDisable(23, false, "&eDisable vanish on relog"),
  noMessages(21, false, "&eNo public messages"),
  fakeJoinLeave(29, false, "&eShow fake join/leave messages on vanish state change"),
  mobSpawning(30, false, "&eMobs will spawn nearby"),
  stopPlaytime(32, true, "&ePlaytime will stop increasing"),
  sleepIgnore(33, true, "&eIgnore for sleeping count"),
  joinVanished(8, false, "&eAlways join in vanish mode"),
  deathMessages(31, false, "&eShow death message"),
  hookPlayers(34, false, "&eHooking entities"),
  level(3, 10, "&eVanish level") {
    public VanishActionStateInt valueFomString(String string) {
      return new VanishActionStateInt().fromString(string);
    }
  };

  private int slot;
  private boolean defaults = true;
  private String desc;
  private int maxValue = 0;
  private static String defaultString = null;

  VanishAction(int value, boolean flag, String string) {
    this.slot = value;
    this.defaults = flag;
    this.desc = string;
  }

  VanishAction(int value, int maximum, String string) {
    this.slot = value;
    this.defaults = true;
    this.desc = string;
    this.maxValue = maximum;
  }

  public VanishActionState valueFomString(String string) {
    return new VanishActionStateBoolean().fromString(string);
  }

  public int getSlot() {
    return this.slot;
  }

  public void setSlot(int value) {
    this.slot = value;
  }

  public boolean isEnabled() {
    return this.defaults;
  }

  public void setEnabled(boolean flag) {
    this.defaults = flag;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String string) {
    this.desc = string;
  }

  public static VanishAction get(String string) {
    VanishAction[] vanishActions;
    int value = (vanishActions = values()).length;

    for (int index = 0; index < value; index++) {
      VanishAction vanishAction = vanishActions[index];
      if (vanishAction.name().equalsIgnoreCase(string)) {
        return vanishAction;
      }
    }

    return null;
  }

  public int getMaxValue() {
    return this.maxValue;
  }

  public void setMaxValue(int value) {
    this.maxValue = value;
  }

  public static String getDefaultConfigString() {
    return VanishManager.defaultString == null
        ? getDefaultInternalString()
        : VanishManager.defaultString;
  }

  public static String getDefaultInternalString() {
    if (defaultString != null) {
      return defaultString;
    }

    StringBuilder stringBuilder = new StringBuilder();
    VanishAction[] vanishActions;
    int value = (vanishActions = values()).length;

    for (int index = 0; index < value; index++) {
      VanishAction vanishAction = vanishActions[index];
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(":");
      }

      if (vanishAction.getMaxValue() > 0) {
        stringBuilder.append(vanishAction.getMaxValue());
      } else {
        stringBuilder.append(vanishAction.isEnabled() ? "1" : "0");
      }
    }

    defaultString = stringBuilder.toString();
    return defaultString;
  }
}
