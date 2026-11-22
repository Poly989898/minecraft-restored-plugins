package com.Zrips.CMI.Modules.Holograms.Settings;

import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramSettings {
  private CMIBillboard billboard = CMIBillboard.FIXED;
  private String group = null;
  private boolean checkLineOfSight = false;
  private boolean requestPermission = false;
  private boolean saveToFile = true;
  private CMIVector2D lightLevels = new CMIVector2D(15.0, 15.0);
  private CMIVector3D direction = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIVector3D centerOffset = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIVector2D scale = new CMIVector2D(1.0, 1.0);
  private int updateRange = HologramManager.getDefaultUpdateRange();
  private int visibilityRange = HologramManager.getDefaultVisibilityRange();
  private int updateIntervalTicks = HologramManager.getDefaultUpdateInterval();

  public CMIBillboard getBillboard() {
    return this.billboard;
  }

  public CMIVector2D getScale() {
    return this.scale;
  }

  public void setScale(CMIVector2D cmiVector2D) {
    this.scale = cmiVector2D;
  }

  public CMIHologramSettings setBillboard(CMIBillboard cmiBillboard) {
    this.billboard = cmiBillboard;
    return this;
  }

  public double getYaw() {
    return this.direction.getY();
  }

  public CMIHologramSettings setYaw(double number) {
    number %= 360.0;
    if (number < 0.0) {
      number += 360.0;
    }

    this.direction.setY(number);
    return this;
  }

  public double getPitch() {
    return this.direction.getX();
  }

  public CMIHologramSettings setPitch(double number) {
    this.direction.setX(CMINumber.clamp(number, -90.0, 90));
    return this;
  }

  public CMIVector3D getDirection() {
    return this.direction;
  }

  public CMIHologramSettings setDirection(CMIVector3D cmiVector3D) {
    this.direction = cmiVector3D;
    return this;
  }

  public CMIVector2D getLightLevel() {
    return this.lightLevels;
  }

  public CMIHologramSettings setLightLevel(CMIVector2D cmiVector2D) {
    this.lightLevels = cmiVector2D;
    return this;
  }

  public int getSkyLevel() {
    return (int) this.lightLevels.getX();
  }

  public CMIHologramSettings setSkyLevel(int value) {
    this.lightLevels.setX(value);
    return this;
  }

  public int getBlockLevel() {
    return (int) this.lightLevels.getY();
  }

  public CMIHologramSettings setBlockLevel(int value) {
    this.lightLevels.setY(value);
    return this;
  }

  public CMIVector3D getOffset() {
    return this.centerOffset;
  }

  public CMIHologramSettings setOffset(CMIVector3D cmiVector3D) {
    this.centerOffset = cmiVector3D;
    return this;
  }

  public String getGroup() {
    return this.group;
  }

  public CMIHologramSettings setGroup(String string) {
    this.group = string;
    return this;
  }

  public boolean isRequiresPermission() {
    return this.requestPermission;
  }

  public CMIHologramSettings setPermissionRequirement(boolean flag) {
    this.requestPermission = flag;
    return this;
  }

  public boolean isSaveToFile() {
    return this.saveToFile;
  }

  public CMIHologramSettings setSaveToFile(boolean flag) {
    this.saveToFile = flag;
    return this;
  }

  public boolean isCheckLineOfSight() {
    return this.checkLineOfSight;
  }

  public CMIHologramSettings setCheckLineOfSight(boolean flag) {
    this.checkLineOfSight = flag;
    return this;
  }

  public CMIHologramSettings setVisibilityRange(int value) {
    value = CMINumber.clamp(value, 1, Integer.MAX_VALUE);
    this.visibilityRange = value < this.getUpdateRange() ? this.getUpdateRange() : value;
    return this;
  }

  public int getVisibilityRange() {
    return this.visibilityRange;
  }

  public int getUpdateRange() {
    return this.updateRange;
  }

  public CMIHologramSettings setUpdateRange(int value) {
    value = CMINumber.clamp(value, 1, Integer.MAX_VALUE);
    this.updateRange = value > this.getVisibilityRange() ? this.getVisibilityRange() : value;
    return this;
  }

  public int getUpdateIntervalTicks() {
    return this.updateIntervalTicks;
  }

  public CMIHologramSettings setUpdateIntervalTicks(int value) {
    this.updateIntervalTicks = CMINumber.clamp(value, 0, Integer.MAX_VALUE);
    return this;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getBillboard() != CMIBillboard.FIXED) {
      hashMap.put("Bill", this.getBillboard().toString());
    }

    if (this.getGroup() != null) {
      hashMap.put("Group", this.getGroup());
    }

    if (this.isCheckLineOfSight()) {
      hashMap.put("LOS", true);
    }

    if (this.isRequiresPermission()) {
      hashMap.put("Perm", true);
    }

    if (this.getLightLevel().getX() != 15.0 || this.getLightLevel().getY() != 15.0) {
      hashMap.put("Light", this.getLightLevel().toString());
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

    if (this.getScale().getX() != 1.0 || this.getScale().getY() != 1.0) {
      hashMap.put("Scale", this.getScale().toString());
    }

    HashMap<String, Integer> rangeMap = new HashMap();
    if (this.getUpdateRange() != HologramManager.getDefaultUpdateRange()) {
      rangeMap.put("Update", this.getUpdateRange());
    }

    if (this.getVisibilityRange() != HologramManager.getDefaultVisibilityRange()) {
      rangeMap.put("Visual", this.getVisibilityRange());
    }

    if (!rangeMap.isEmpty()) {
      hashMap.put("Range", rangeMap);
    }

    if (this.getUpdateIntervalTicks() != HologramManager.getDefaultUpdateInterval()) {
      hashMap.put("Interval", this.getUpdateIntervalTicks());
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramSettings deserialize(Map<String, Object> map) {
    CMIHologramSettings cmiHologramSettings = new CMIHologramSettings();
    if (map.get("Bill") instanceof String) {
      cmiHologramSettings.setBillboard(CMIBillboard.getByName((String) map.get("Bill")));
    }

    if (map.get("Group") instanceof String) {
      cmiHologramSettings.setGroup((String) map.get("Group"));
    }

    if (map.get("LOS") instanceof Boolean) {
      cmiHologramSettings.setCheckLineOfSight((Boolean) map.get("LOS"));
    }

    if (map.get("Perm") instanceof Boolean) {
      cmiHologramSettings.setPermissionRequirement((Boolean) map.get("Perm"));
    }

    if (map.get("Save") instanceof Boolean) {
      cmiHologramSettings.setSaveToFile((Boolean) map.get("Save"));
    } else {
      cmiHologramSettings.setSaveToFile(true);
    }

    if (map.get("Light") instanceof String) {
      cmiHologramSettings.setLightLevel(CMIVector3D.fromString((String) map.get("Light")));
    }

    if (map.get("Dir") instanceof String) {
      cmiHologramSettings.setDirection(CMIVector3D.fromString((String) map.get("Dir")));
    }

    if (map.get("Offset") instanceof String) {
      cmiHologramSettings.setOffset(CMIVector3D.fromString((String) map.get("Offset")));
    }

    if (map.get("Scale") instanceof String) {
      cmiHologramSettings.setScale(CMIVector2D.fromString((String) map.get("Scale")));
    }

    if (map.get("Range") instanceof MemorySection) {
      var rangeSection = ((MemorySection) map.get("Range")).getValues(false);
      if (rangeSection.get("Visual") instanceof Integer) {
        cmiHologramSettings.setVisibilityRange((Integer) rangeSection.get("Visual"));
      }

      if (rangeSection.get("Update") instanceof Integer) {
        int value = (Integer) rangeSection.get("Update");
        if (cmiHologramSettings.getVisibilityRange() < value) {
          cmiHologramSettings.setVisibilityRange(value);
        }

        cmiHologramSettings.setUpdateRange(value);
      }
    }

    if (map.get("Interval") instanceof Integer) {
      cmiHologramSettings.setUpdateIntervalTicks((Integer) map.get("Interval"));
    }

    return cmiHologramSettings;
  }
}
