package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.Modules.Display.CMIBlockDisplay;
import com.Zrips.CMI.Modules.Display.CMIDisplay;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class CMIHologramBatchDisplay extends CMIHologramBatch {
  private ConcurrentHashMap<Integer, CMIDisplay> displays = new ConcurrentHashMap<>();
  private ConcurrentHashMap<Integer, CMIHologramBatchGroup> icons = new ConcurrentHashMap<>();
  private CMIBlockDisplay board = null;

  public ConcurrentHashMap<Integer, CMIDisplay> getDisplays() {
    return this.displays;
  }

  public CMIDisplay getFrontDisplay() {
    return this.displays.get(1);
  }

  public CMIDisplay getBackDisplay() {
    return this.displays.get(2);
  }

  public CMIDisplay getDisplay(int value) {
    return this.displays.get(value);
  }

  public void addDisplay(int value, CMIDisplay cmiDisplay) {
    this.displays.put(value, cmiDisplay);
  }

  public void addFrontDisplay(CMIDisplay cmiDisplay) {
    this.displays.put(1, cmiDisplay);
  }

  public void addBackDisplay(CMIDisplay cmiDisplay) {
    this.displays.put(2, cmiDisplay);
  }

  @Override
  public void clear() {
    this.displays.clear();
    this.icons.clear();
    this.setBoard(null);
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
        if (cmiHologramBatchGroup instanceof CMIHologramBatchGroupDisplay) {
          CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay =
              (CMIHologramBatchGroupDisplay) cmiHologramBatchGroup;
          if (cmiHologramBatchGroupDisplay.getDisplay() != null) {
            cmiHologramBatchGroupDisplay.getDisplay().destroy(player);
          }
        }
      }
    }

    this.icons.clear();
  }

  public void destroyBoard(Player player) {
    if (player != null && player.isOnline() && this.getBoard() != null) {
      this.getBoard().destroy(player);
    }

    this.board = null;
  }

  @Override
  public void destroy(Player player) {
    try {
      if (player != null && player.isOnline()) {
        for (CMIDisplay cmiDisplay : this.displays.values()) {
          cmiDisplay.destroy(player);
        }
      }

      this.destroyIcons(player);
      this.destroyBoard(player);
      this.clear();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public CMIBlockDisplay getBoard() {
    return this.board;
  }

  public void setBoard(CMIBlockDisplay cmiBlockDisplay) {
    this.board = cmiBlockDisplay;
  }
}
