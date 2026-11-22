package com.Zrips.CMI.Modules.Dialogs;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public class CMIDialogItem extends CMIDialogVisualField {
  private String itemString = null;
  private ItemStack itemStack = null;
  private int height = 16;
  private int width = 16;
  private boolean tooltip = true;
  private boolean decorations = true;
  CMIDialogText text = null;

  public CMIDialogItem(ItemStack dialogItemStack) {
    this.itemStack = dialogItemStack;
  }

  public CMIDialogItem(String string) {
    this.itemString = string;
  }

  public String getItemString() {
    return this.itemString;
  }

  public CMIDialogText getText() {
    return this.text;
  }

  public int getHeight() {
    return this.height;
  }

  public int getWidth() {
    return this.width;
  }

  public boolean isTooltip() {
    return this.tooltip;
  }

  public boolean isDecorations() {
    return this.decorations;
  }

  public CMIDialogItem setTooltip(boolean flag) {
    this.tooltip = flag;
    return this;
  }

  public CMIDialogItem setDecorations(boolean flag) {
    this.decorations = flag;
    return this;
  }

  public CMIDialogItem setHeight(int value) {
    this.height = value;
    return this;
  }

  public CMIDialogItem setWidth(int value) {
    this.width = value;
    return this;
  }

  public CMIDialogItem setText(CMIDialogText cmiDialogText) {
    this.text = cmiDialogText;
    return this;
  }

  @Nonnull
  @Override
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getItemString() != null) {
      hashMap.put("Item", this.getItemString());
    }

    if (this.getText() != null) {
      hashMap.put("Text", this.getText());
    }

    if (!this.isTooltip()) {
      hashMap.put("ShowTooltip", this.isTooltip());
    }

    if (!this.isDecorations()) {
      hashMap.put("ShowDecorations", this.isDecorations());
    }

    if (this.getHeight() != 16) {
      hashMap.put("Height", this.getHeight());
    }

    if (this.getWidth() != 16) {
      hashMap.put("Width", this.getWidth());
    }

    return hashMap;
  }

  public static CMIDialogItem deserialize(Map<String, Object> map) {
    if (!map.containsKey("Item")) {
      return null;
    }

    CMIDialogItem cmiDialogItem = new CMIDialogItem((String) map.get("Item"));
    if (map.containsKey("Text")) {
      Map textMap = (Map) map.get("Text");
      cmiDialogItem.setText(CMIDialogText.deserialize(textMap));
    }

    cmiDialogItem.setCondition(CMICustomDialogCondition.deserialize(map));
    if (map.containsKey("ShowTooltip")) {
      cmiDialogItem.setTooltip((Boolean) map.get("ShowTooltip"));
    }

    if (map.containsKey("ShowDecorations")) {
      cmiDialogItem.setDecorations((Boolean) map.get("ShowDecorations"));
    }

    if (map.containsKey("Width")) {
      cmiDialogItem.setWidth((Integer) map.get("Width"));
    }

    if (map.containsKey("Height")) {
      cmiDialogItem.setHeight((Integer) map.get("Height"));
    }

    return cmiDialogItem;
  }

  public ItemStack getItemStack() {
    return this.itemStack;
  }

  public void setItemStack(ItemStack dialogItemStack) {
    this.itemStack = dialogItemStack;
  }
}
