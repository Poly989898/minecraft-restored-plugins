package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Animations.AnimationManager;
import org.bukkit.entity.Player;

public class CMIPlayerSitEvent extends CMIPlayerCancellableEvent {
  private AnimationManager.Chair chair;

  public CMIPlayerSitEvent(Player player, AnimationManager.Chair chair) {
    super(player);
    this.chair = chair;
  }

  public AnimationManager.Chair getChair() {
    return this.chair;
  }
}
