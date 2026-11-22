package com.Zrips.CMI.Modules.Worlds;

import com.Zrips.CMI.CMI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class CMIWorldListener implements Listener {
  private CMI plugin;
  static ConcurrentHashMap<String, Set<UpdateOnWorldLoad>> locations = new ConcurrentHashMap<>();

  public CMIWorldListener(CMI cmi) {
    this.plugin = cmi;
  }

  public static void delayRecheck(String string, UpdateOnWorldLoad updateOnWorldLoad) {
    if (string == null) {
      CMIMessages.consoleMessage("Can't determine world name for delayed update.");
    } else {
      var set = locations.get(string.toLowerCase());
      if (set == null) {
        set = new HashSet();
        locations.put(string.toLowerCase(), set);
      }

      set.add(updateOnWorldLoad);
    }
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent worldLoadEvent) {
    var set = locations.get(worldLoadEvent.getWorld().getName().toLowerCase());
    if (set != null) {
      HashSet<UpdateOnWorldLoad> hashSet = new HashSet<>(set);
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            for (UpdateOnWorldLoad updateOnWorldLoad : hashSet) {
              updateOnWorldLoad.update(worldLoadEvent.getWorld());
            }
          });
    }
  }
}
