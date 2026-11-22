package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.ChatFilter.RuleResponse;
import org.bukkit.entity.Player;

public class CMIPlayerChatFilterEvent extends CMIPlayerCancellableEvent {
  private RuleResponse response;

  public CMIPlayerChatFilterEvent(Player player, RuleResponse ruleResponse) {
    super(player);
    this.response = ruleResponse;
  }

  public RuleResponse getResponse() {
    return this.response;
  }
}
