package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class CMIHologramBatchArmorStand extends CMIHologramBatch {
  private ArrayList<FakeEntityInfo> displays = new ArrayList<>();
  private ConcurrentHashMap<Integer, CMIHologramBatchGroup> icons = new ConcurrentHashMap<>();

  public ArrayList<FakeEntityInfo> getDisplays() {
    return new ArrayList<>(this.displays);
  }

  public void addDisplay(int value, FakeEntityInfo fakeEntityInfo) {
    while (this.displays.size() <= value) {
      this.displays.add(new FakeEntityInfo());
    }

    this.displays.set(value, fakeEntityInfo);
  }

  @Override
  public void clear() {
    this.displays.clear();
    this.icons.clear();
  }

  @Override
  public ConcurrentHashMap<Integer, CMIHologramBatchGroup> getIcons() {
    return this.icons;
  }

  @Override
  public CMIHologramBatchGroup getIcon(int value) {
    return this.icons.get(value);
  }

  @Override
  public void addIcon(int value, CMIHologramBatchGroup cmiHologramBatchGroup) {
    if (cmiHologramBatchGroup == null) {
      this.icons.remove(value);
    } else {
      this.icons.put(value, cmiHologramBatchGroup);
    }
  }

  @Override
  public void destroyIcons(Player player) {
    if (player != null && player.isOnline()) {
      for (CMIHologramBatchGroup cmiHologramBatchGroup : this.icons.values()) {
        if (cmiHologramBatchGroup instanceof CMIHologramBatchGroupArmorStand) {
          FakeEntityInfo fakeEntityInfo =
              ((CMIHologramBatchGroupArmorStand) cmiHologramBatchGroup).getDisplay();
          CMI.getInstance().getNMS().removeHologram(player, fakeEntityInfo.getId());
        }
      }
    }

    this.icons.clear();
  }

  @Override
  public void destroy(Player player) {
    this.destroyIcons(player);
    if (player != null && player.isOnline()) {
      for (FakeEntityInfo fakeEntityInfo : this.displays) {
        CMI.getInstance().getNMS().removeHologram(player, fakeEntityInfo.getId());
      }
    }

    this.clear();
  }
}
