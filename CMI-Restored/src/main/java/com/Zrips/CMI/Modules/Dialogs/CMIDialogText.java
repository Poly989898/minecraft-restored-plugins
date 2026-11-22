package com.Zrips.CMI.Modules.Dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Container.CMIList;

public class CMIDialogText extends CMIDialogVisualField {
  private List<String> lines = new ArrayList<>();
  private int width = 250;
  private boolean randomize = false;

  public CMIDialogText() {}

  public CMIDialogText(@Nonnull List<String> list) {
    this.lines = list;
  }

  public CMIDialogText(@Nonnull String string) {
    this.lines.add(string);
  }

  @Nonnull
  public List<String> getLines() {
    return this.randomize
        ? Arrays.asList(this.lines.get(new Random().nextInt(this.lines.size())))
        : this.lines;
  }

  @Nonnull
  public List<String> getAllLines() {
    return this.lines;
  }

  public CMIDialogText setLines(@Nonnull List<String> list) {
    this.lines = list;
    return this;
  }

  public int getWidth() {
    return this.width;
  }

  public CMIDialogText setWidth(int value) {
    this.width = value;
    return this;
  }

  public boolean isRandomize() {
    return this.randomize;
  }

  public CMIDialogText setRandomize(boolean flag) {
    this.randomize = flag;
    return this;
  }

  public CMIDialogText setLines(@Nullable String string) {
    this.lines.clear();
    if (string != null) {
      this.lines.addAll(CMIList.stringToList(string, "\n"));
    }

    return this;
  }

  @Override
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getWidth() != 250) {
      hashMap.put("Width", this.getWidth());
    }

    if (this.isRandomize()) {
      hashMap.put("Randomize", this.isRandomize());
    }

    if (!this.getAllLines().isEmpty()) {
      hashMap.put("Lines", this.getAllLines());
    }

    return hashMap;
  }

  public static CMIDialogText deserialize(Map<String, Object> map) {
    CMIDialogText cmiDialogText = new CMIDialogText();
    if (map.containsKey("Lines")) {
      Object object = map.get("Lines");
      if (object instanceof List) {
        cmiDialogText.setLines((List<String>) object);
      } else if (object instanceof String) {
        cmiDialogText.setLines(new ArrayList<>(Arrays.asList((String) object)));
      }
    }

    cmiDialogText.setCondition(CMICustomDialogCondition.deserialize(map));
    if (map.containsKey("Width")) {
      cmiDialogText.setWidth((Integer) map.get("Width"));
    }

    if (map.containsKey("Randomize")) {
      cmiDialogText.setRandomize((Boolean) map.get("Randomize"));
    }

    return cmiDialogText;
  }
}
