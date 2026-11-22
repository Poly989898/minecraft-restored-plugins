package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.CMILib;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CMIPlayerConnection implements Listener {
  private static ConcurrentHashMap<UUID, Object> playersConnections = new ConcurrentHashMap<>();

  public static void addConnection(Player player) {
    if (player != null) {
      UUID uuid = player.getUniqueId();
      if (!player.isOnline()) {
        playersConnections.remove(uuid);
      } else {
        Object object = CMILib.getInstance().getReflectionManager().getPlayerConnection(player);
        playersConnections.put(uuid, object);
      }
    }
  }

  @Nullable
  public static Object getConnection(Player player) {
    if (player == null) {
      return null;
    } else {
      UUID uuid = player.getUniqueId();
      if (!player.isOnline()) {
        playersConnections.remove(uuid);
        return null;
      } else {
        Object object = getConnection(uuid);
        if (object == null) {
          addConnection(player);
          return getConnection(uuid);
        } else {
          return object;
        }
      }
    }
  }

  @Nullable
  public static Object getConnection(@Nonnull UUID uuid) {
    return uuid == null ? null : playersConnections.get(uuid);
  }

  public static void removeConnection(Player player) {
    if (player != null) {
      removeConnection(player.getUniqueId());
    }
  }

  public static void removeConnection(UUID uuid) {
    playersConnections.remove(uuid);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent playerJoinEvent) {
    addConnection(playerJoinEvent.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onQuit(PlayerQuitEvent playerQuitEvent) {
    removeConnection(playerQuitEvent.getPlayer());
  }

  public static void sendPacket(Player player, Object object) {
    sendPacket(player.getUniqueId(), object);
  }

  public static void sendPacket(UUID uuid, Object object) {
    Object playerConnection = getConnection(uuid);
    if (playerConnection != null) {
      sendPacket(playerConnection, object);
    }
  }

  public static void sendPacket(Object object, Object packet) {
    try {
      CMI.getInstance().getNMS().sendPacketToConnection(object, packet);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
