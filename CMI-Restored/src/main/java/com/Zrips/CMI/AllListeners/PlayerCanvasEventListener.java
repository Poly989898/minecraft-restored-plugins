package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import java.lang.reflect.Method;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public class PlayerCanvasEventListener {
  private static Class<? extends Event> EVENT_CLASS;
  private static Method GET_PLAYER;
  private static Method GET_RESPAWN_LOCATION;
  private static Method SET_RESPAWN_LOCATION;
  private static boolean initialized = false;

  private static boolean init() {
    if (initialized) {
      return EVENT_CLASS != null;
    }

    initialized = true;

    try {
      var loadedClass = Class.forName("io.canvasmc.canvas.event.PlayerRespawnAsyncEvent");
      if (!Event.class.isAssignableFrom(loadedClass)) {
        return false;
      }

      Class eventClass = loadedClass;
      EVENT_CLASS = eventClass;
      GET_PLAYER = EVENT_CLASS.getMethod("getPlayer");
      GET_RESPAWN_LOCATION = EVENT_CLASS.getMethod("getRespawnLocation");
      SET_RESPAWN_LOCATION = EVENT_CLASS.getMethod("setRespawnLocation", Location.class);
      GET_PLAYER.setAccessible(true);
      GET_RESPAWN_LOCATION.setAccessible(true);
      SET_RESPAWN_LOCATION.setAccessible(true);
      return true;
    } catch (Throwable throwable) {
      return false;
    }
  }

  public static void register(Plugin plugin) {
    if (init()) {
      EventExecutor eventExecutor =
          (listener, event) -> {
            if (EVENT_CLASS.isInstance(event)) {
              try {
                Player player = (Player) GET_PLAYER.invoke(event);
                Location location = (Location) GET_RESPAWN_LOCATION.invoke(event);
                Location respawnLocation =
                    CMI.getInstance().getHomeManager().getReSpawnLocation(player, location);
                if (respawnLocation == null || respawnLocation.getWorld() == null) {
                  return;
                }

                SET_RESPAWN_LOCATION.invoke(event, respawnLocation);
                if (CMI.getInstance().getHomeManager().getRespawnImmortality() > 0
                    && CMI.getInstance().getHomeManager().getRespawnImmortality() <= 60) {
                  CMIScheduler.runTask(
                      CMI.getInstance(),
                      () -> {
                        if (player.isOnline()) {
                          CMIGod.applyNoDamageTick(
                              player,
                              CMI.getInstance().getHomeManager().getRespawnImmortality() * 20);
                        }
                      });
                }
              } catch (Throwable throwable) {
                throwable.printStackTrace();
              }
            }
          };
      Bukkit.getPluginManager()
          .registerEvent(
              EVENT_CLASS, new Listener() {}, EventPriority.NORMAL, eventExecutor, plugin, true);
    }
  }
}
