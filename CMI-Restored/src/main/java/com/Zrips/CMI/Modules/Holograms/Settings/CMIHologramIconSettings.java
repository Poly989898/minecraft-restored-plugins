package com.Zrips.CMI.Modules.Holograms.Settings;

import com.Zrips.CMI.Modules.Display.CMIBillboard;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramIconSettings {
  private CMIVector3D scale = new CMIVector3D(1.0, 1.0, 1.0);
  private CMIVector3D direction = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIVector3D offset = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIBillboard billboard = null;

  public CMIBillboard getBillboard() {
    return this.billboard;
  }

  public CMIHologramIconSettings setBillboard(CMIBillboard cmiBillboard) {
    this.billboard = cmiBillboard;
    return this;
  }

  public CMIVector3D getOffset() {
    return this.offset;
  }

  public CMIHologramIconSettings setOffset(CMIVector3D cmiVector3D) {
    this.offset = cmiVector3D;
    return this;
  }

  public CMIVector3D getScale() {
    return this.scale;
  }

  public CMIVector3D getDirection() {
    return this.direction;
  }

  public CMIHologramIconSettings setDirection(CMIVector3D cmiVector3D) {
    this.direction = cmiVector3D;
    return this;
  }

  public CMIHologramIconSettings setScale(double number) {
    this.scale.setX(number);
    this.scale.setY(number);
    this.scale.setZ(number);
    return this;
  }

  public CMIHologramIconSettings setScale(CMIVector3D cmiVector3D) {
    this.scale = cmiVector3D;
    return this;
  }

  public int getPitch() {
    return (int) this.direction.getX();
  }

  public CMIHologramIconSettings setPitch(int value) {
    this.direction.setX(value);
    return this;
  }

  public int getYaw() {
    return (int) this.direction.getY();
  }

  public CMIHologramIconSettings setYaw(int value) {
    this.direction.setY(value);
    return this;
  }

  public int getRoll() {
    return (int) this.direction.getZ();
  }

  public CMIHologramIconSettings setRoll(int value) {
    this.direction.setZ(value);
    return this;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap<String, Object> hashMap = new HashMap<>();
    if (this.getScale().getX() != 1.0
        || this.getScale().getY() != 1.0
        || this.getScale().getZ() != 1.0) {
      hashMap.put("Scale", this.getScale().toString());
    }

    if (this.getDirection().getX() != 0.0
        || this.getDirection().getY() != 0.0
        || this.getDirection().getZ() != 0.0) {
      hashMap.put("Dir", this.getDirection().toString());
    }

    if (this.getOffset().getX() != 0.0
        || this.getOffset().getY() != 0.0
        || this.getOffset().getZ() != 0.0) {
      hashMap.put("Offset", this.getOffset().toString());
    }

    if (this.getBillboard() != null) {
      hashMap.put("Bill", this.getBillboard().toString());
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramIconSettings deserialize(Map<String, Object> map) {
    CMIHologramIconSettings cmiHologramIconSettings = new CMIHologramIconSettings();
    if (map.get("Scale") instanceof String) {
      cmiHologramIconSettings.setScale(CMIVector3D.fromString((String) map.get("Scale")));
    }

    if (map.get("Dir") instanceof String) {
      cmiHologramIconSettings.setDirection(CMIVector3D.fromString((String) map.get("Dir")));
    }

    if (map.get("Offset") instanceof String) {
      cmiHologramIconSettings.setOffset(CMIVector3D.fromString((String) map.get("Offset")));
    }

    if (map.get("Bill") instanceof String) {
      cmiHologramIconSettings.setBillboard(CMIBillboard.getByName((String) map.get("Bill")));
    }

    return cmiHologramIconSettings;
  }
}
