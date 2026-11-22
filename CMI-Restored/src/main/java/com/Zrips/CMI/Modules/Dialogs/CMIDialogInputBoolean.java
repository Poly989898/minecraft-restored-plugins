package com.Zrips.CMI.Modules.Dialogs;

import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public class CMIDialogInputBoolean extends CMIDialogInputField {
  private CMIDialogInputValue initial = new CMIDialogInputValue(false);
  private String valueOnTrue = null;
  private String valueOnFalse = null;
  private CMIDialogAction commandsOnTrue = null;
  private CMIDialogAction commandsOnFalse = null;

  public CMIDialogInputValue getInitialState() {
    return this.initial;
  }

  public void setInitialState(CMIDialogInputValue cmiDialogInputValue) {
    this.initial = cmiDialogInputValue;
  }

  @Override
  public BiConsumer<Player, Boolean> getConsumer() {
    return (BiConsumer<Player, Boolean>) this.consumer;
  }

  @Override
  public Map<String, Object> serialize() {
    return null;
  }

  public static CMIDialogInputBoolean deserialize(Map<String, Object> map) {
    CMIDialogInputBoolean cmiDialogInputBoolean = new CMIDialogInputBoolean();
    if (map.containsKey("Default")) {
      cmiDialogInputBoolean.setInitialState(new CMIDialogInputValue(map.get("Default")));
    }

    if (map.containsKey("OnTrue")) {
      Map onTrueMap = (Map) map.get("OnTrue");
      cmiDialogInputBoolean.setCommandsOnTrue(CMIDialogAction.deserialize(onTrueMap));
      if (onTrueMap.containsKey("Value")) {
        cmiDialogInputBoolean.setValueOnTrue((String) onTrueMap.get("Value"));
      }
    }

    if (map.containsKey("OnFalse")) {
      Map onFalseMap = (Map) map.get("OnFalse");
      cmiDialogInputBoolean.setCommandsOnFalse(CMIDialogAction.deserialize(onFalseMap));
      if (onFalseMap.containsKey("Value")) {
        cmiDialogInputBoolean.setValueOnFalse((String) onFalseMap.get("Value"));
      }
    }

    return cmiDialogInputBoolean;
  }

  public CMIDialogAction getCommandsOnTrue() {
    return this.commandsOnTrue;
  }

  public void setCommandsOnTrue(@Nonnull CMIDialogAction cmiDialogAction) {
    this.commandsOnTrue = cmiDialogAction;
  }

  public CMIDialogAction getCommandsOnFalse() {
    return this.commandsOnFalse;
  }

  public void setCommandsOnFalse(@Nonnull CMIDialogAction cmiDialogAction) {
    this.commandsOnFalse = cmiDialogAction;
  }

  public String getValueOnTrue() {
    return this.valueOnTrue;
  }

  public void setValueOnTrue(String string) {
    this.valueOnTrue = string;
  }

  public String getValueOnFalse() {
    return this.valueOnFalse;
  }

  public void setValueOnFalse(String string) {
    this.valueOnFalse = string;
  }
}
