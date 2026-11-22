package com.Zrips.CMI.Modules.Votifier;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class VotifierListener implements Listener {
  private CMI plugin;

  public VotifierListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void VotifierEvent(VotifierEvent value) {
    String string = value.getVote().getUsername();
    CMIUser cmiUser = CMIUser.getUser(string);
    if (cmiUser != null) {
      VotifierManager votifierManager = this.plugin.getVotifierManager();
      votifierManager.processVote(
          cmiUser, value.getVote().getServiceName(), value.getVote().getAddress());
      votifierManager.updateVoteCountList(cmiUser);
      votifierManager.updateTopList();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    this.plugin.getVotifierManager().removeVoteReminder(playerQuitEvent.getPlayer().getUniqueId());
  }
}
