package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.Containers.CMIPlayerInventory;
import java.util.HashMap;

public class LeatherAnimation {
  private HashMap<Integer, AnimationManager.LeatherAnimationType> map = new HashMap<>();
  private Float colorValue = null;
  private Float saturationValue = null;
  private boolean ascending = true;
  private boolean Sascending = true;

  public HashMap<Integer, AnimationManager.LeatherAnimationType> getMap() {
    return this.map;
  }

  public void setMap(HashMap<Integer, AnimationManager.LeatherAnimationType> hashMap) {
    this.map = hashMap;
  }

  public void setAnimation(
      CMIPlayerInventory.CMIInventorySlot cmiInventorySlot,
      AnimationManager.LeatherAnimationType leatherAnimationType) {
    this.map.put(cmiInventorySlot.getSlot(), leatherAnimationType);
  }

  public AnimationManager.LeatherAnimationType getAnimation(
      CMIPlayerInventory.CMIInventorySlot cmiInventorySlot) {
    return this.map.get(cmiInventorySlot.getSlot());
  }

  public Float getColorValue() {
    return this.colorValue;
  }

  public void setColorValue(Float colorValue) {
    this.colorValue = colorValue;
  }

  public boolean isAscending() {
    return this.ascending;
  }

  public void setAscending(boolean flag) {
    this.ascending = flag;
  }

  public Float getSaturationValue() {
    return this.saturationValue;
  }

  public void setSaturationValue(Float saturationValue) {
    this.saturationValue = saturationValue;
  }

  public boolean isSascending() {
    return this.Sascending;
  }

  public void setSascending(boolean flag) {
    this.Sascending = flag;
  }
}
