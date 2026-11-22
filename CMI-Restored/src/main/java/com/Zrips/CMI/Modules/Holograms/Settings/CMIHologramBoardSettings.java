package com.Zrips.CMI.Modules.Holograms.Settings;

import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramBoardSettings {
  private boolean enabled = false;
  private CMIVector3D extraDimensions = new CMIVector3D(0.0, 0.0, 0.01);
  private CMIVector3D offset = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIVector3D direction = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIMaterial material = CMIMaterial.ACACIA_LOG;

  public double getExtraDepth() {
    return this.extraDimensions.getZ();
  }

  public double getExtraWidth() {
    return this.extraDimensions.getX();
  }

  public double getExtraHeight() {
    return this.extraDimensions.getY();
  }

  public CMIMaterial getMaterial() {
    return this.material;
  }

  public CMIHologramBoardSettings setMaterial(CMIMaterial cmiMaterial) {
    this.material = cmiMaterial;
    return this;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public CMIHologramBoardSettings setEnabled(boolean flag) {
    this.enabled = flag;
    return this;
  }

  public CMIVector3D getExtraDimensions() {
    return this.extraDimensions;
  }

  public CMIHologramBoardSettings setExtraDimensions(CMIVector3D cmiVector3D) {
    this.extraDimensions = cmiVector3D;
    return this;
  }

  public CMIVector3D getOffset() {
    return this.offset;
  }

  public CMIHologramBoardSettings setOffset(CMIVector3D cmiVector3D) {
    this.offset = cmiVector3D;
    return this;
  }

  public CMIVector3D getDirection() {
    return this.direction;
  }

  public void setDirection(CMIVector3D cmiVector3D) {
    this.direction = cmiVector3D;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.isEnabled()) {
      hashMap.put("Enabled", true);
    }

    if (this.getExtraDimensions().getX() != 0.0
        || this.getExtraDimensions().getY() != 0.0
        || this.getExtraDimensions().getZ() != 0.01) {
      hashMap.put("Dimensions", this.getExtraDimensions().toString());
    }

    if (this.getOffset().getX() != 0.0
        || this.getOffset().getY() != 0.0
        || this.getOffset().getZ() != 0.0) {
      hashMap.put("Offset", this.getOffset().toString());
    }

    if (this.getDirection().getX() != 0.0
        || this.getDirection().getY() != 0.0
        || this.getDirection().getZ() != 0.0) {
      hashMap.put("Direction", this.getDirection().toString());
    }

    if (this.getMaterial() != CMIMaterial.ACACIA_LOG && this.getMaterial() != CMIMaterial.AIR) {
      hashMap.put("Material", this.getMaterial().toString());
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramBoardSettings deserialize(Map<String, Object> map) {
    CMIHologramBoardSettings cmiHologramBoardSettings = new CMIHologramBoardSettings();
    if (map.get("Enabled") instanceof Boolean) {
      cmiHologramBoardSettings.setEnabled((Boolean) map.get("Enabled"));
    }

    if (map.get("Dimensions") instanceof String) {
      cmiHologramBoardSettings.setExtraDimensions(
          CMIVector3D.fromString((String) map.get("Dimensions")));
    }

    if (map.get("Offset") instanceof String) {
      cmiHologramBoardSettings.setOffset(CMIVector3D.fromString((String) map.get("Offset")));
    }

    if (map.get("Direction") instanceof String) {
      cmiHologramBoardSettings.setDirection(CMIVector3D.fromString((String) map.get("Direction")));
    }

    if (map.get("Material") instanceof String) {
      cmiHologramBoardSettings.setMaterial(CMIMaterial.get((String) map.get("Material")));
    }

    return cmiHologramBoardSettings;
  }
}
