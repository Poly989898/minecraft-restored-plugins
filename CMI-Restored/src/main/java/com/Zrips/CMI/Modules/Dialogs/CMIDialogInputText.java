package com.Zrips.CMI.Modules.Dialogs;

import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public class CMIDialogInputText extends CMIDialogInputField {
  private CMIDialogInputValue initial = new CMIDialogInputValue(null);
  private CMIDialogInputValue maxLength = new CMIDialogInputValue(null);
  private CMIDialogInputValue maxLines = new CMIDialogInputValue(null);
  private CMIDialogInputValue height = new CMIDialogInputValue(null);
  private CMIDialogAction action = null;

  @Override
  public BiConsumer<Player, String> getConsumer() {
    return (BiConsumer<Player, String>) this.consumer;
  }

  @Override
  public Map<String, Object> serialize() {
    return null;
  }

  public static CMIDialogInputText deserialize(Map<String, Object> map) {
    CMIDialogInputText cmiDialogInputText = new CMIDialogInputText();
    if (map.containsKey("InitialText")) {
      cmiDialogInputText.setInitialText(CMIDialogInputValue.getValue(map.get("InitialText")));
    }

    if (map.containsKey("MaxLength")) {
      cmiDialogInputText.setMaxLength(CMIDialogInputValue.getValue(map.get("MaxLength")));
    }

    if (map.containsKey("MaxLines")) {
      cmiDialogInputText.setMaxLines(CMIDialogInputValue.getValue((Integer) map.get("MaxLines")));
    }

    if (map.containsKey("Height")) {
      cmiDialogInputText.setHeight(CMIDialogInputValue.getValue((Integer) map.get("Height")));
    }

    cmiDialogInputText.setAction(CMIDialogAction.deserialize(map));
    return cmiDialogInputText;
  }

  public CMIDialogInputValue getInitialText() {
    return this.initial;
  }

  public CMIDialogInputText setInitialText(CMIDialogInputValue cmiDialogInputValue) {
    this.initial = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getMaxLength() {
    return this.maxLength;
  }

  public CMIDialogInputText setMaxLength(CMIDialogInputValue cmiDialogInputValue) {
    this.maxLength = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getMaxLines() {
    return this.maxLines;
  }

  public CMIDialogInputText setMaxLines(CMIDialogInputValue cmiDialogInputValue) {
    this.maxLines = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getHeight() {
    return this.height;
  }

  public CMIDialogInputText setHeight(CMIDialogInputValue cmiDialogInputValue) {
    this.height = cmiDialogInputValue;
    return this;
  }

  public CMIDialogAction getAction() {
    return this.action;
  }

  public CMIDialogInputText setAction(CMIDialogAction cmiDialogAction) {
    this.action = cmiDialogAction;
    return this;
  }
}
