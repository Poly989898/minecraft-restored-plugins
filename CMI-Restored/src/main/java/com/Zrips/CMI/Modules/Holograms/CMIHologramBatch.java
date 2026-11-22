package com.Zrips.CMI.Modules.Holograms;

import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class CMIHologramBatch {
  public void clear() {}

  public ConcurrentHashMap<Integer, CMIHologramBatchGroup> getIcons() {
    return new ConcurrentHashMap<>();
  }

  public CMIHologramBatchGroup getIcon(int value) {
    return null;
  }

  public void addIcon(int value, CMIHologramBatchGroup cmiHologramBatchGroup) {}

  public void destroyIcons(Player player) {}

  public void destroy(Player player) {}
}
