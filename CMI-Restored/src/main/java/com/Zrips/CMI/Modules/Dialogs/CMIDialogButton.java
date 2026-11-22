package com.Zrips.CMI.Modules.Dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import org.bukkit.entity.Player;

public class CMIDialogButton {
  private String label = null;
  private int width = 250;
  private CMIDialogAction action = null;
  private String tooltip = null;
  private Consumer<Player> onClick = null;
  private boolean save = false;
  private CMICustomDialogCondition condition = new CMICustomDialogCondition();

  public CMIDialogButton(@Nullable String string) {
    this.label = string;
  }

  @Nullable
  public String getLabel() {
    return this.label;
  }

  public CMIDialogButton setLabel(String string) {
    this.label = string;
    return this;
  }

  @Nullable
  public CMIDialogAction getAction() {
    return this.action;
  }

  public CMIDialogButton setAction(@Nullable CMIDialogAction cmiDialogAction) {
    this.action = cmiDialogAction;
    return this;
  }

  @Nullable
  public String getCustomActionValue() {
    return this.action == null
        ? null
        : (this.action.getActions().isEmpty() ? null : this.action.getActions().get(0));
  }

  @Nonnull
  public CMIDialogButtonType getType() {
    return this.action == null ? CMIDialogButtonType.CUSTOM : this.action.getType();
  }

  public int getWidth() {
    return this.width;
  }

  public CMIDialogButton setWidth(int value) {
    this.width = CMINumber.clamp(value, 1, 9999);
    return this;
  }

  @Nullable
  public String getTooltip() {
    return this.tooltip;
  }

  public CMIDialogButton setTooltip(@Nullable String string) {
    this.tooltip = string;
    return this;
  }

  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getLabel() != null) {
      hashMap.put("Label", this.getLabel());
    }

    if (this.getTooltip() != null) {
      if (this.getTooltip().contains("\n")) {
        hashMap.put("Tooltip", CMIList.stringToList(this.getTooltip(), "\n"));
      } else {
        hashMap.put("Tooltip", this.getTooltip());
      }
    }

    if (this.getWidth() != 250) {
      hashMap.put("Width", this.getWidth());
    }

    switch (this.getType()) {
      case COMMANDS:
        if (this.getAction() != null) {
          hashMap.put("Commands", this.getAction().getActions());
        }
        break;
      case URL:
        hashMap.put("Url", this.getCustomActionValue());
        break;
      case OPENDIALOG:
        hashMap.put("OpenDialog", this.getCustomActionValue());
        break;
      case SUGGESTION:
        hashMap.put("Suggestion", this.getCustomActionValue());
    }

    return hashMap;
  }

  public static CMIDialogButton deserialize(Map<String, Object> map) {
    String string = map.containsKey("Label") ? (String) map.get("Label") : "";
    CMIDialogButton cmiDialogButton = new CMIDialogButton(string);
    if (map.containsKey("Tooltip")) {
      if (map.get("Tooltip") instanceof String) {
        cmiDialogButton.setTooltip((String) map.get("Tooltip"));
      } else if (map.get("Tooltip") instanceof List) {
        cmiDialogButton.setTooltip(CMIList.listToString((List) map.get("Tooltip")));
      }
    }

    if (map.containsKey("Save")) {
      cmiDialogButton.setSave((Boolean) map.get("Save"));
    }

    if (map.containsKey("Width")) {
      cmiDialogButton.setWidth((Integer) map.get("Width"));
    }

    cmiDialogButton.setCondition(CMICustomDialogCondition.deserialize(map));
    CMIDialogButtonType[] cmiDialogButtonTypes;
    int value = (cmiDialogButtonTypes = CMIDialogButtonType.values()).length;

    for (int index = 0; index < value; index++) {
      CMIDialogButtonType cmiDialogButtonType = cmiDialogButtonTypes[index];
      if (cmiDialogButtonType.getConfigEntry() != null
          && map.containsKey(cmiDialogButtonType.getConfigEntry())) {
        cmiDialogButton.setAction(
            new CMIDialogAction(
                map.get(cmiDialogButtonType.getConfigEntry()), cmiDialogButtonType));
      }
    }

    return cmiDialogButton;
  }

  @Nullable
  public Consumer<Player> getOnClick() {
    return this.onClick;
  }

  public CMIDialogButton setOnClick(@Nullable Consumer<Player> consumer) {
    this.onClick = consumer;
    return this;
  }

  public boolean isSave() {
    return this.save;
  }

  public CMIDialogButton setSave(boolean flag) {
    this.save = flag;
    return this;
  }

  @Nonnull
  public CMICustomDialogCondition getCondition() {
    return this.condition;
  }

  public CMIDialogButton setCondition(@Nonnull CMICustomDialogCondition cmiCustomDialogCondition) {
    this.condition = cmiCustomDialogCondition;
    return this;
  }
}
