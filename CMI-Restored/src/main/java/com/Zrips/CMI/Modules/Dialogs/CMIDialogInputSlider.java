package com.Zrips.CMI.Modules.Dialogs;

import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public class CMIDialogInputSlider extends CMIDialogInputField {
  private CMIDialogAction action = null;
  private CMIDialogInputValue initialValue = new CMIDialogInputValue(null);
  private CMIDialogInputValue start = new CMIDialogInputValue(0.0F);
  private CMIDialogInputValue end = new CMIDialogInputValue(1.0F);
  private CMIDialogInputValue step = new CMIDialogInputValue(1.0F);

  @Override
  public BiConsumer<Player, Float> getConsumer() {
    return (BiConsumer<Player, Float>) this.consumer;
  }

  @Override
  public Map<String, Object> serialize() {
    return null;
  }

  public static CMIDialogInputSlider deserialize(Map<String, Object> map) {
    CMIDialogInputSlider cmiDialogInputSlider = new CMIDialogInputSlider();
    if (map.containsKey("InitialValue")) {
      cmiDialogInputSlider.setInitialValue(CMIDialogInputValue.getValue(map.get("InitialValue")));
    }

    if (map.containsKey("Start")) {
      cmiDialogInputSlider.setStart(CMIDialogInputValue.getValue(map.get("Start")));
    }

    if (map.containsKey("End")) {
      cmiDialogInputSlider.setEnd(CMIDialogInputValue.getValue(map.get("End")));
    }

    if (map.containsKey("Step")) {
      cmiDialogInputSlider.setStep(CMIDialogInputValue.getValue(map.get("Step")));
    }

    cmiDialogInputSlider.setAction(CMIDialogAction.deserialize(map));
    return cmiDialogInputSlider;
  }

  public CMIDialogAction getAction() {
    return this.action;
  }

  public CMIDialogInputSlider setAction(CMIDialogAction cmiDialogAction) {
    this.action = cmiDialogAction;
    return this;
  }

  public CMIDialogInputValue getInitialValue() {
    return this.initialValue;
  }

  public CMIDialogInputSlider setInitialValue(CMIDialogInputValue cmiDialogInputValue) {
    this.initialValue = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getStart() {
    return this.start;
  }

  public CMIDialogInputSlider setStart(CMIDialogInputValue cmiDialogInputValue) {
    this.start = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getEnd() {
    return this.end;
  }

  public CMIDialogInputSlider setEnd(CMIDialogInputValue cmiDialogInputValue) {
    this.end = cmiDialogInputValue;
    return this;
  }

  public CMIDialogInputValue getStep() {
    return this.step;
  }

  public CMIDialogInputSlider setStep(CMIDialogInputValue cmiDialogInputValue) {
    this.step = cmiDialogInputValue;
    return this;
  }
}
