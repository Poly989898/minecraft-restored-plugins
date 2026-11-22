/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.Modules.Dialogs;

import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public class CMIDialogInputField {
  private String label = null;
  private String id = null;
  private int width = 200;
  private CMICustomDialogCondition condition = new CMICustomDialogCondition();
  protected BiConsumer<Player, ?> consumer;

  public Map<String, Object> serialize() {
    return null;
  }

  public String getId() {
    return this.id;
  }

  public CMIDialogInputField setId(String string) {
    this.id = string;
    return this;
  }

  public BiConsumer<Player, ?> getConsumer() {
    return this.consumer;
  }

  public CMIDialogInputField setConsumer(BiConsumer<Player, ?> biConsumer) {
    this.consumer = biConsumer;
    return this;
  }

  public String getLabel(Player player) {
    return CMIDialog.updateVariable(player, this.label);
  }

  public CMIDialogInputField setLabel(String string) {
    this.label = string;
    return this;
  }

  public static CMIDialogInputField deserialize(Map<String, Object> map) {
    String string;
    String typeValue = string = map.containsKey("Type") ? (String) map.get("Type") : "";
    if (string.isEmpty()) {
      string =
          map.containsKey("Options")
              ? "singleton"
              : (map.containsKey("OnTrue") || map.containsKey("OnFalse")
                  ? "boolean"
                  : (map.containsKey("Start")
                          || map.containsKey("End")
                          || map.containsKey("Step")
                          || map.containsKey("InitialValue")
                      ? "slider"
                      : (map.containsKey("InitialText")
                              || map.containsKey("MaxLines")
                              || map.containsKey("Height")
                              || map.containsKey("MaxLength")
                          ? "text"
                          : "text")));
    }
    CMIDialogInputField cMIDialogInputField = null;
    switch (string.toLowerCase()) {
      case "boolean":
        {
          cMIDialogInputField = CMIDialogInputBoolean.deserialize(map);
          break;
        }
      case "singleton":
        {
          cMIDialogInputField = CMIDialogInputSingletons.deserialize(map);
          break;
        }
      case "text":
        {
          cMIDialogInputField = CMIDialogInputText.deserialize(map);
          break;
        }
      case "slider":
        {
          cMIDialogInputField = CMIDialogInputSlider.deserialize(map);
        }
    }
    if (cMIDialogInputField == null) {
      return cMIDialogInputField;
    }
    cMIDialogInputField.setLabel(map.containsKey("Label") ? (String) map.get("Label") : "");
    cMIDialogInputField.setWidth(map.containsKey("Width") ? (Integer) map.get("Width") : 200);
    cMIDialogInputField.setId(map.containsKey("ID") ? (String) map.get("ID") : null);
    cMIDialogInputField.setCondition(CMICustomDialogCondition.deserialize(map));
    return cMIDialogInputField;
  }

  public int getWidth() {
    return this.width;
  }

  public CMIDialogInputField setWidth(int n) {
    this.width = n;
    return this;
  }

  @Nonnull
  public CMICustomDialogCondition getCondition() {
    return this.condition;
  }

  public CMIDialogInputField setCondition(
      @Nonnull CMICustomDialogCondition cMICustomDialogCondition) {
    this.condition = cMICustomDialogCondition;
    return this;
  }
}
