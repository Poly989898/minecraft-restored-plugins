package com.Zrips.CMI.Modules.Dialogs;

import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.entity.Player;

public class CMIDialogInputSingleton {
  private CMIDialogInputValue isDefault = new CMIDialogInputValue(false);
  private CMIDialogAction action = null;
  private String label = null;
  private String id = null;
  private String value = null;

  public CMIDialogInputSingleton() {}

  public CMIDialogInputSingleton(String string, @Nullable String labelText, boolean flag) {
    this.id = string;
    this.label = labelText;
    this.isDefault = new CMIDialogInputValue(flag);
  }

  public CMIDialogInputSingleton setDefault(boolean flag) {
    this.isDefault = new CMIDialogInputValue(flag);
    return this;
  }

  public boolean isDefault() {
    return this.isDefault.getBoolValue(null);
  }

  public boolean isDefault(Player player) {
    return this.isDefault.getBoolValue(player);
  }

  public OptionEntry getOptionEntry(Player player, boolean flag) {
    return this.getOptionEntry(player, flag, new HashMap<>());
  }

  public OptionEntry getOptionEntry(Player player, boolean flag, Map<String, String> map) {
    String string = CMIDialog.updateCommandVariables(this.getLabel(), map);
    return OptionEntry.create(
        this.getId() == null ? this.getLabel() : this.getId(),
        CMIDialog.getComponent(player, string),
        this.isDefault.getBoolValue(player, flag));
  }

  public OptionEntry getOptionEntry(Player player) {
    return this.getOptionEntry(player, new HashMap<>());
  }

  public OptionEntry getOptionEntry(Player player, Map<String, String> map) {
    String string = CMIDialog.updateCommandVariables(this.getLabel(), map);
    return OptionEntry.create(
        this.getId() == null ? this.getLabel() : this.getId(),
        CMIDialog.getComponent(player, string),
        this.isDefault(player));
  }

  public OptionEntry getOptionEntryLimited(Player player, boolean flag) {
    return this.getOptionEntryLimited(player, flag, new HashMap<>());
  }

  public OptionEntry getOptionEntryLimited(Player player, boolean flag, Map<String, String> map) {
    String string = CMIDialog.updateCommandVariables(this.getLabel(), map);
    return OptionEntry.create(
        this.getId() == null ? this.getLabel() : this.getId(),
        CMIDialog.getComponent(player, string),
        flag && this.isDefault(player));
  }

  public static CMIDialogInputSingleton deserialize(Map<?, ?> map) {
    CMIDialogInputSingleton cmiDialogInputSingleton = new CMIDialogInputSingleton();
    if (map.containsKey("Default")) {
      cmiDialogInputSingleton.isDefault = new CMIDialogInputValue(map.get("Default"));
    }

    cmiDialogInputSingleton.setLabel(map.containsKey("Label") ? (String) map.get("Label") : "");
    cmiDialogInputSingleton.setId(
        map.containsKey("ID")
            ? (String) map.get("ID")
            : (cmiDialogInputSingleton.getLabel() != null
                ? CMIChatColor.stripColor(cmiDialogInputSingleton.getLabel())
                : String.valueOf(new Random().nextInt())));
    cmiDialogInputSingleton.setValue(map.containsKey("Value") ? (String) map.get("Value") : null);
    CMIDialogButtonType[] cmiDialogButtonTypes;
    int buttonTypeCount = (cmiDialogButtonTypes = CMIDialogButtonType.values()).length;

    for (int buttonTypeIndex = 0; buttonTypeIndex < buttonTypeCount; buttonTypeIndex++) {
      CMIDialogButtonType cmiDialogButtonType = cmiDialogButtonTypes[buttonTypeIndex];
      if (cmiDialogButtonType.getConfigEntry() != null
          && map.containsKey(cmiDialogButtonType.getConfigEntry())) {
        cmiDialogInputSingleton.action =
            new CMIDialogAction(map.get(cmiDialogButtonType.getConfigEntry()), cmiDialogButtonType);
      }
    }

    return cmiDialogInputSingleton;
  }

  public CMIDialogAction getAction() {
    return this.action;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String string) {
    this.label = string;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String string) {
    this.id = string;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String string) {
    this.value = string;
  }
}
