package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEventRegistry;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class NameChangeListener implements Listener {
  private CMI plugin;
  ConcurrentHashMap<UUID, String> temp = new ConcurrentHashMap<>();

  public NameChangeListener(CMI cmi) {
    this.plugin = cmi;
    CMIEventRegistry.registerEvent(
        EventPriority.LOWEST,
        true,
        PlayerLoginEvent.class,
        (listener, playerLoginEvent) -> {
          if (playerLoginEvent.getResult().equals(Result.ALLOWED)
              && cmi.getConfigManager().isPerformCommandsOnNewName()) {
            Player player = playerLoginEvent.getPlayer();
            if (!CMINPCChecker.isNPC(player)) {
              this.onPreJoin(player.getUniqueId());
            }
          }
        });
  }

  private void onPreJoin(UUID uuid) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
    if (cmiUser != null) {
      this.temp.put(uuid, cmiUser.getName(false));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!CMINPCChecker.isNPC(player)) {
      CMIUser cmiUser = CMIUser.getUser(player.getUniqueId());
      if (cmiUser != null) {
        String string = this.temp.remove(player.getUniqueId());
        this.plugin.getPlayerManager().updateUserName(cmiUser, string, player.getName());
        if (string != null
            && !string.equals(player.getName())
            && this.plugin.getConfigManager().isPerformCommandsOnNewName()) {
          var arrayList =
              new ArrayList<>(this.plugin.getConfigManager().getOptimizationsNameChangeCommands());

          for (int value = 0; value < arrayList.size(); value++) {
            arrayList.set(
                value,
                ((String) arrayList.get(value))
                    .replace("[oldname]", string)
                    .replace("[newname]", player.getName()));
          }

          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                if (player.isOnline()) {
                  Snd snd = new Snd();
                  snd.setSender(cmiUser).setTarget(cmiUser);
                  this.plugin.getLM().updateSnd(snd, arrayList);
                  this.plugin.getSpecializedCommandManager().processCmds(arrayList, player);
                }
              },
              10L);
        }
      }
    }
  }
}
