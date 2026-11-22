package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AnimationColoredArmorListener implements Listener {
  private CMI plugin;

  public AnimationColoredArmorListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void leatherArmor(CMIArmorChangeEvent cmiArmorChangeEvent) {
    if (!cmiArmorChangeEvent.isCancelled()) {
      CMIScheduler.runTask(
          this.plugin,
          () -> this.plugin.getAnimationManager().addLeatherArmor(cmiArmorChangeEvent.getPlayer()));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void leatherArmor(PlayerJoinEvent playerJoinEvent) {
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> {
          Player player = playerJoinEvent.getPlayer();
          if (!CMINPCChecker.isNPC(player)) {
            if (player.isOnline()) {
              this.plugin.getAnimationManager().addLeatherArmor(player);
            }
          }
        },
        60L);
  }
}
