package com.Zrips.CMI.Modules.Skin;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SkinListener implements Listener {
  private CMI plugin;

  public SkinListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLogin(PlayerJoinEvent playerJoinEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerJoinEvent.getPlayer());
    if (cmiUser != null && CMIModule.skin.isEnabled()) {
      if (cmiUser.hasSetSkin()) {
        CMIScheduler.runLaterAsync(
            this.plugin,
            () ->
                this.plugin
                    .getSkinManager()
                    .applySkinAsync(
                        CMIUser.getOnlinePlayer(playerJoinEvent.getPlayer().getUniqueId()),
                        cmiUser.getSkin()),
            1L);
      } else if (this.plugin.getSkinManager().isAutoApply()) {
        CMIScheduler.runLaterAsync(
            this.plugin,
            () ->
                this.plugin
                    .getSkinManager()
                    .applySkinAsync(
                        CMIUser.getOnlinePlayer(playerJoinEvent.getPlayer().getUniqueId()),
                        cmiUser.getName()),
            1L);
      }
    }
  }
}
