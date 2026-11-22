package net.Zrips.CMILib.BossBar;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BossBarListener implements Listener {
  private CMILib plugin;

  public BossBarListener(CMILib plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoinBossBar(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    CMIScheduler.get()
        .runTaskLater(
            new Runnable() {
              @Override
              public void run() {
                BossBarListener.this.plugin.getBossBarManager().updateGlobalBars(player);
              }
            },
            5L);
  }
}
