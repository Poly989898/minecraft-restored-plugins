package com.Zrips.CMI.CombiListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import java.lang.reflect.Method;
import java.util.UUID;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

public class CMIPlayerServerFullCheckEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final String playerName;
  private final UUID uuid;
  private boolean allowed = true;
  private String kickMessage = "";
  private static Class<?> fullCheckClass;
  private static Method getProfileMethod;
  private static Method getProfileUUIDMethod;
  private static Method getProfileNameMethod;
  private static Method denyMethod;

  public CMIPlayerServerFullCheckEvent(UUID playerUuid, String string) {
    this.playerName = string;
    this.uuid = playerUuid;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public boolean isAllowed() {
    return this.allowed;
  }

  public void disallow(String string) {
    this.allowed = false;
    this.kickMessage = string;
  }

  public String getKickMessage() {
    return this.kickMessage;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public static void register() {
    PluginManager pluginManager = Bukkit.getPluginManager();
    if (Version.isPaperBranch() && Version.isCurrentEqualOrHigher(Version.v1_21_R5)) {
      try {
        fullCheckClass = Class.forName("io.papermc.paper.event.player.PlayerServerFullCheckEvent");
        getProfileMethod = fullCheckClass.getMethod("getPlayerProfile");
        var profileClass = getProfileMethod.getReturnType();
        getProfileUUIDMethod = profileClass.getMethod("getUniqueId");
        getProfileNameMethod = profileClass.getMethod("getName");
        denyMethod =
            fullCheckClass.getMethod("deny", Class.forName("net.kyori.adventure.text.Component"));
        EventExecutor eventExecutor =
            (listener, event) -> {
              try {
                Object object = getProfileMethod.invoke(event);
                UUID profileUuid = (UUID) getProfileUUIDMethod.invoke(object);
                String string = (String) getProfileNameMethod.invoke(object);
                if (profileUuid == null) {
                  return;
                }

                CMIPlayerServerFullCheckEvent cmiPlayerServerFullCheckEvent =
                    new CMIPlayerServerFullCheckEvent(profileUuid, string);
                Bukkit.getPluginManager().callEvent(cmiPlayerServerFullCheckEvent);
                if (!cmiPlayerServerFullCheckEvent.isAllowed()) {
                  denyMethod.invoke(
                      event,
                      CMIDialog.getComponent(cmiPlayerServerFullCheckEvent.getKickMessage()));
                }
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            };
        Listener eventListener = new Listener() {};
        var eventClass = fullCheckClass.asSubclass(Event.class);
        pluginManager.registerEvent(
            eventClass, eventListener, EventPriority.LOW, eventExecutor, CMI.getInstance());
      } catch (ClassNotFoundException classNotFoundException) {
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }
    } else {
      pluginManager.registerEvents(
          new Listener() {
            @EventHandler(priority = EventPriority.LOW)
            public void onLogin(PlayerLoginEvent playerLoginEvent) {
              CMIPlayerServerFullCheckEvent fullCheckEvent =
                  new CMIPlayerServerFullCheckEvent(
                      playerLoginEvent.getPlayer().getUniqueId(),
                      playerLoginEvent.getPlayer().getName());
              Bukkit.getPluginManager().callEvent(fullCheckEvent);
              if (!fullCheckEvent.isAllowed()) {
                playerLoginEvent.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER, fullCheckEvent.getKickMessage());
              }
            }
          },
          CMI.getInstance());
    }
  }
}
