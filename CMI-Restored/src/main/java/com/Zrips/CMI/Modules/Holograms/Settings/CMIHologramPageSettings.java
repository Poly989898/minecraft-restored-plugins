package com.Zrips.CMI.Modules.Holograms.Settings;

import com.Zrips.CMI.Modules.Holograms.HologramManager;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.Container.CMINumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramPageSettings {
  private int pageChangeIntervalTicks = HologramManager.getDefaultpageChangeInterval();
  private boolean autoPagination = false;

  public boolean isAutoPagination() {
    return this.autoPagination;
  }

  public void setAutoPagination(boolean flag) {
    this.autoPagination = flag;
  }

  public int getPageChangeIntervalTicks() {
    return this.pageChangeIntervalTicks;
  }

  public void setPageChangeIntervalTicks(int value) {
    value = CMINumber.clamp(value, 0, 20000);
    this.pageChangeIntervalTicks = value;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getPageChangeIntervalTicks() != HologramManager.getDefaultpageChangeInterval()) {
      hashMap.put("Change", this.getPageChangeIntervalTicks());
    }

    if (this.isAutoPagination()) {
      hashMap.put("Auto", true);
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramPageSettings deserialize(Map<String, Object> map) {
    CMIHologramPageSettings cmiHologramPageSettings = new CMIHologramPageSettings();
    if (map.get("Change") instanceof Integer) {
      cmiHologramPageSettings.setPageChangeIntervalTicks((Integer) map.get("Change"));
    }

    if (map.get("Auto") instanceof Boolean) {
      cmiHologramPageSettings.setAutoPagination((Boolean) map.get("Auto"));
    }

    return cmiHologramPageSettings;
  }
}
