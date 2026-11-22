package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMICustomDialog {
  private String name = null;
  private boolean enabled = true;
  private boolean closeOnExit = true;
  private String label = "";
  private List<CMIDialogVisualField> visualFields = new ArrayList<>();
  private List<CMIDialogInputField> inputFields = new ArrayList<>();
  private int columns = 1;
  private List<CMIDialogButton> buttons = null;
  private CMIDialogButton closeButton = null;
  private HashMap<UUID, CMIDialog> dialog = new HashMap<>();
  protected HashMap<UUID, HashMap<String, Object>> values = new HashMap<>();
  private Map<String, String> variables = new HashMap<>();
  private CMICustomDialogCondition condition = new CMICustomDialogCondition();

  public CMICustomDialog(String string) {
    this.name = string;
  }

  public String getName() {
    return this.name;
  }

  public void clearCache(UUID uuid) {
    CMIDialog cmiDialog = this.dialog.remove(uuid);
    if (cmiDialog != null) {
      this.values.put(uuid, cmiDialog.values);
    }
  }

  public CMIDialog getCMIDialog(Player player) {
    return this.dialog.get(player.getUniqueId());
  }

  public CMICustomDialog show(Player player) {
    if (!this.getCondition().isConditionValid(player, this)) {
      return null;
    }

    this.build(player);
    CMIDialog cmiDialog = this.getCMIDialog(player);
    if (cmiDialog != null) {
      cmiDialog.show(player);
    }

    return this;
  }

  public CMICustomDialog build(Player player) {
    CMIDialog cmiDialog = new CMIDialog(player, this);
    this.dialog.put(player.getUniqueId(), cmiDialog);
    return this;
  }

  public void reset() {
    this.dialog = new HashMap<>();
    this.buttons = null;
    this.enabled = true;
    this.label = "";
    this.visualFields.clear();
    this.inputFields.clear();
    this.columns = 1;
    this.closeButton = null;
  }

  public CMICustomDialog setEnabled(boolean flag) {
    this.enabled = flag;
    return this;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  @Nullable
  public List<CMIDialogButton> getButtons() {
    return this.buttons;
  }

  public CMICustomDialog setButtons(@Nullable List<CMIDialogButton> list) {
    this.buttons = list;
    return this;
  }

  public CMICustomDialog addButton(@Nullable CMIDialogButton cmiDialogButton) {
    if (cmiDialogButton == null) {
      return this;
    }

    if (this.buttons == null) {
      this.buttons = new ArrayList<>();
    }

    this.buttons.add(cmiDialogButton);
    return this;
  }

  @NotNull
  public String getLabel() {
    return this.label;
  }

  public CMICustomDialog setLabel(@Nullable String string) {
    this.label = string == null ? "" : string;
    return this;
  }

  public int getColumns() {
    return this.columns;
  }

  public CMICustomDialog setColumns(int value) {
    this.columns = CMINumber.clamp(value, 1, 999);
    return this;
  }

  public CMIDialogButton getCloseButton() {
    return this.closeButton;
  }

  public CMICustomDialog setCloseButton(@Nullable CMIDialogButton cmiDialogButton) {
    this.closeButton = cmiDialogButton;
    return this;
  }

  public CMICustomDialog setCloseButtonLabel(@Nullable String string) {
    if (this.closeButton == null) {
      this.closeButton = new CMIDialogButton(string);
    } else {
      this.closeButton.setLabel(string);
    }

    return this;
  }

  public boolean isCloseOnExit() {
    return this.closeOnExit;
  }

  public CMICustomDialog setCloseOnExit(boolean flag) {
    this.closeOnExit = flag;
    return this;
  }

  public static CMICustomDialog deserialize(String string, Map<String, Object> map) {
    new CMICustomDialog(string);
    CMICustomDialog cmiCustomDialog = CMI.getInstance().getDialogManager().getDialog(string, true);
    if (cmiCustomDialog == null) {
      cmiCustomDialog = new CMICustomDialog(string);
    } else {
      cmiCustomDialog.reset();
    }

    try {
      if (map.containsKey("Enabled")) {
        cmiCustomDialog.setEnabled((Boolean) map.get("Enabled"));
      }

      if (map.containsKey("Label")) {
        cmiCustomDialog.setLabel((String) map.get("Label"));
      }

      cmiCustomDialog.setCondition(CMICustomDialogCondition.deserialize(map));
      if (map.containsKey("Close")) {
        Map closeMap = (Map) map.get("Close");
        cmiCustomDialog.setCloseButton(CMIDialogButton.deserialize(closeMap));
        cmiCustomDialog.setCloseOnExit(!closeMap.containsKey("OpenDialog"));
      }

      if (map.containsKey("Description")) {
        Map descriptionMap = (Map) map.get("Description");
        cmiCustomDialog.addVisualField(CMIDialogText.deserialize(descriptionMap));
      }

      if (map.containsKey("VisualFields")) {
        for (Map visualMap : (List<Map>) map.get("VisualFields")) {
          if (visualMap.containsKey("Item")) {
            cmiCustomDialog.addVisualField(CMIDialogItem.deserialize(visualMap));
          } else if (visualMap.containsKey("Lines")) {
            cmiCustomDialog.addVisualField(CMIDialogText.deserialize(visualMap));
          }
        }
      }

      if (map.containsKey("Inputs")) {
        for (Map inputMap : (List<Map>) map.get("Inputs")) {
          CMIDialogInputField cmiDialogInputField = CMIDialogInputField.deserialize(inputMap);
          if (cmiDialogInputField.getId() == null) {
            cmiDialogInputField.setId(
                cmiCustomDialog.getName() + "_" + cmiCustomDialog.getInputFields().size());
          }

          cmiCustomDialog.addInputField(cmiDialogInputField);
        }
      }

      if (map.containsKey("Buttons")) {
        Map buttonsMap = (Map) map.get("Buttons");
        if (buttonsMap.containsKey("Columns")) {
          cmiCustomDialog.setColumns((Integer) buttonsMap.get("Columns"));
        }

        for (Map buttonMap : (List<Map>) buttonsMap.get("List")) {
          cmiCustomDialog.addButton(CMIDialogButton.deserialize(buttonMap));
        }
      }

      return cmiCustomDialog;
    } catch (Exception exception) {
      CMIMessages.sendMessage(Bukkit.getConsoleSender(), "Cant load dialog (" + string + ")");
      exception.printStackTrace();
      return null;
    }
  }

  public List<CMIDialogVisualField> getVisualFields() {
    return this.visualFields;
  }

  public CMICustomDialog setVisualFields(List<CMIDialogVisualField> list) {
    this.visualFields = list;
    return this;
  }

  public CMICustomDialog addVisualField(CMIDialogVisualField cmiDialogVisualField) {
    this.visualFields.add(cmiDialogVisualField);
    return this;
  }

  public List<CMIDialogInputField> getInputFields() {
    return this.inputFields;
  }

  public CMICustomDialog setInputFields(List<CMIDialogInputField> list) {
    this.inputFields = list;
    return this;
  }

  public CMICustomDialog addInputField(CMIDialogInputField cmiDialogInputField) {
    this.inputFields.add(cmiDialogInputField);
    return this;
  }

  @NotNull
  public CMICustomDialogCondition getCondition() {
    return this.condition;
  }

  public CMICustomDialog setCondition(@NotNull CMICustomDialogCondition cmiCustomDialogCondition) {
    this.condition = cmiCustomDialogCondition;
    return this;
  }

  public Map<String, String> getVariables() {
    return this.variables;
  }

  public CMICustomDialog setVariables(Map<String, String> map) {
    this.variables = map;
    return this;
  }
}
