package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Selection.Visualizer;
import org.bukkit.entity.Player;

public class CMISelectionVisualizationEvent extends CMIPlayerCancellableEvent {
  private Visualizer visualizer;

  @Deprecated
  public CMISelectionVisualizationEvent(Player player) {
    super(player);
  }

  public CMISelectionVisualizationEvent(Player player, Visualizer visualizer) {
    super(player);
    this.visualizer = visualizer;
  }

  public Visualizer getVisualizer() {
    return this.visualizer;
  }
}
