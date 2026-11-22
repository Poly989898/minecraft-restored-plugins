package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishActionState;
import com.Zrips.CMI.Modules.Vanish.VanishActionStateBoolean;
import com.Zrips.CMI.Modules.Vanish.VanishActionStateInt;
import java.util.HashMap;
import java.util.UUID;

public class CMIVanish {
  private UUID uuid;
  private HashMap<VanishAction, VanishActionState> map = new HashMap<>();
  private boolean wasCollidable = true;
  private long vanishedFrom = 0L;
  private long playtimeAtVanishStart = 0L;

  public void fromString(String string) {
    if (string != null && string.contains(":")) {
      String[] strings = string.split(":");
      int value = 0;
      VanishAction[] vanishActions;
      int actionCount = (vanishActions = VanishAction.values()).length;

      for (int index = 0; index < actionCount; index++) {
        VanishAction vanishAction = vanishActions[index];
        if (strings.length > value) {
          boolean flag = strings[value].equals("1");
          if (vanishAction.getMaxValue() == 0 && vanishAction.isEnabled() == flag
              || vanishAction.getMaxValue() > 0 && strings[value].equals("0")) {
            value++;
            continue;
          }

          this.map.put(vanishAction, vanishAction.valueFomString(strings[value]));
        }

        value++;
      }

      if (this.is(VanishAction.isVanished)) {
        CMI.getInstance().getVanishManager().addPlayer(this.uuid);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    VanishAction[] vanishActions;
    int value = (vanishActions = VanishAction.values()).length;

    for (int index = 0; index < value; index++) {
      VanishAction vanishAction = vanishActions[index];
      VanishActionState vanishActionState = this.map.get(vanishAction);
      if (vanishActionState == null) {
        vanishActionState = new VanishActionStateBoolean(vanishAction.isEnabled());
      }

      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(":");
      }

      stringBuilder.append(vanishActionState.toString());
    }

    return stringBuilder.toString();
  }

  public CMIVanish() {}

  public CMIVanish(CMIUser cmiUser) {
    this.uuid = cmiUser.getUniqueId();
  }

  public CMIUser getUser() {
    return CMIUser.getUser(this.uuid);
  }

  public void setUser(CMIUser cmiUser) {
    this.uuid = cmiUser.getUniqueId();
  }

  @Deprecated
  public boolean is(VanishAction vanishAction) {
    return this.uuid != null
            && this.map.containsKey(vanishAction)
            && this.map.get(vanishAction) != null
        ? this.map.get(vanishAction).is()
        : vanishAction.isEnabled();
  }

  public VanishActionState getState(VanishAction vanishAction) {
    if (this.uuid == null) {
      return new VanishActionState();
    } else if (this.map.containsKey(vanishAction) && this.map.get(vanishAction) != null) {
      return this.map.get(vanishAction);
    } else {
      return vanishAction.getMaxValue() > 0
          ? new VanishActionStateInt()
          : vanishAction.valueFomString(vanishAction.isEnabled() ? "1" : "0");
    }
  }

  public void set(VanishAction vanishAction, boolean flag) {
    if (vanishAction.isEnabled() == flag) {
      this.map.remove(vanishAction);
    } else {
      this.map.put(vanishAction, new VanishActionStateBoolean(flag));
    }
  }

  public void set(VanishAction vanishAction, int value) {
    if (value == 0) {
      this.map.remove(vanishAction);
    } else {
      this.map.put(vanishAction, new VanishActionStateInt(value));
    }
  }

  public Boolean getWasCollidable() {
    return this.wasCollidable;
  }

  public void setWasCollidable(Boolean collidable) {
    this.wasCollidable = collidable == null ? true : collidable;
  }

  public Long getVanishedFrom() {
    return this.vanishedFrom;
  }

  public void setVanishedFrom(Long vanishedFromTime) {
    this.vanishedFrom = vanishedFromTime == null ? 0L : vanishedFromTime;
  }

  public Long getPlaytimeAtVanishStart() {
    return this.playtimeAtVanishStart;
  }

  public void setPlaytimeAtVanishStart(Long playtimeAtStart) {
    this.playtimeAtVanishStart = playtimeAtStart;
  }
}
