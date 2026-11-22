package com.Zrips.CMI.events;

import org.bukkit.entity.Player;

public class CMIPlayerChatFilterCapsEvent extends CMIPlayerCancellableEvent {
  private String message;

  public CMIPlayerChatFilterCapsEvent(Player player, String string) {
    super(player);
    this.message = string;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }
}
