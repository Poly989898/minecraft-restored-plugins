package com.Zrips.CMI.Modules.EventActions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Worlds.CMIWorld;
import com.Zrips.CMI.events.CMIPlayerBanEvent;
import com.Zrips.CMI.events.CMIPvPEndEventAsync;
import com.Zrips.CMI.events.CMIPvPStartEventAsync;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventActionListener implements Listener {
  private CMI plugin;
  HashMap<UUID, Long> last = new HashMap<>();

  public EventActionListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerPortalEvent(PlayerPortalEvent playerPortalEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvPEndEventAsync(CMIPvPEndEventAsync cmiPvPEndEventAsync) {
    if (EventActionManager.eventAction.pvpend.isEnabled()) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.pvpend, cmiPvPEndEventAsync.getPlayer()));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvPStartEventAsync(CMIPvPStartEventAsync cmiPvPStartEventAsync) {
    if (EventActionManager.eventAction.pvpstart.isEnabled()) {
      CMIScheduler.runTask(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.pvpstart, cmiPvPStartEventAsync.getPlayer()));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerFirstJoinEvent(PlayerJoinEvent playerJoinEvent) {
    if (EventActionManager.eventAction.firstJoinServer.isEnabled()
        && !playerJoinEvent.getPlayer().hasPlayedBefore()) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.firstJoinServer, playerJoinEvent.getPlayer()),
          1L);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    if (EventActionManager.eventAction.joinServer.isEnabled()
        && playerJoinEvent.getPlayer().hasPlayedBefore()) {
      this.plugin
          .getEventActionManager()
          .performCommands(EventActionManager.eventAction.joinServer, playerJoinEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    if (EventActionManager.eventAction.playerDeath.isEnabled()) {
      Player player = playerDeathEvent.getEntity().getKiller();
      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.playerDeath, playerDeathEvent.getEntity(), player);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerKillPlayerEvent(PlayerDeathEvent playerDeathEvent) {
    if (EventActionManager.eventAction.playerKillPlayer.isEnabled()) {
      Player player = playerDeathEvent.getEntity().getKiller();
      if (player != null) {
        this.plugin
            .getEventActionManager()
            .performCommands(
                EventActionManager.eventAction.playerKillPlayer,
                playerDeathEvent.getEntity(),
                player);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerRespawnEvent(PlayerRespawnEvent playerRespawnEvent) {
    if (EventActionManager.eventAction.playerRespawn.isEnabled()
        && (!Version.isCurrentEqualOrHigher(Version.v1_20_R1)
            || playerRespawnEvent.getRespawnReason() == null
            || !playerRespawnEvent.getRespawnReason().equals(RespawnReason.END_PORTAL))) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.playerRespawn, playerRespawnEvent.getPlayer()),
          1L);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    if (EventActionManager.eventAction.quitServer.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(EventActionManager.eventAction.quitServer, playerQuitEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerTeleportEvent(PlayerTeleportEvent playerTeleportEvent) {
    if ((EventActionManager.eventAction.playerPreWorldChange.isEnabled()
            || EventActionManager.eventAction.playerTeleport.isEnabled())
        && playerTeleportEvent.getTo() != null) {
      if (!playerTeleportEvent
          .getFrom()
          .getWorld()
          .equals(playerTeleportEvent.getTo().getWorld())) {
        this.plugin
            .getEventActionManager()
            .performCommands(
                EventActionManager.eventAction.playerPreWorldChange,
                playerTeleportEvent.getPlayer());
      }

      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.playerTeleport, playerTeleportEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerBedEnterEvent(PlayerBedEnterEvent playerBedEnterEvent) {
    if (EventActionManager.eventAction.bedEnter.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.bedEnter, playerBedEnterEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerBedLeaveEvent(PlayerBedLeaveEvent playerBedLeaveEvent) {
    if (EventActionManager.eventAction.bedLeave.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.bedLeave, playerBedLeaveEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerVoidFallEvent(PlayerMoveEvent playerMoveEvent) {
    if (EventActionManager.eventAction.voidFall.isEnabled()
        && playerMoveEvent.getTo() != null
        && !(playerMoveEvent.getTo().getY()
            >= CMIWorld.getMinHeight(playerMoveEvent.getPlayer().getWorld()) - 10)) {
      if (this.last.get(playerMoveEvent.getPlayer().getUniqueId()) == null
          || this.last.get(playerMoveEvent.getPlayer().getUniqueId()) + 1000L
              < System.currentTimeMillis()) {
        this.last.put(playerMoveEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
        this.plugin
            .getEventActionManager()
            .performCommands(EventActionManager.eventAction.voidFall, playerMoveEvent.getPlayer());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerChangedWorldEvent(PlayerChangedWorldEvent playerChangedWorldEvent) {
    if (EventActionManager.eventAction.playerWorldChange.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.playerWorldChange,
              playerChangedWorldEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerGameModeChangeEvent(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
    if (EventActionManager.eventAction.playerGameModeChange.isEnabled()) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.playerGameModeChange,
                      playerGameModeChangeEvent.getPlayer()),
          1L);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerKickEvent(PlayerKickEvent playerKickEvent) {
    if (EventActionManager.eventAction.playerKick.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(EventActionManager.eventAction.playerKick, playerKickEvent.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void CMIPlayerBanEvent(CMIPlayerBanEvent cmiPlayerBanEvent) {
    if (EventActionManager.eventAction.playerBan.isEnabled()) {
      this.plugin
          .getEventActionManager()
          .performCommands(
              EventActionManager.eventAction.playerBan,
              CMIUser.getOnlinePlayer(cmiPlayerBanEvent.getBanned()));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerLevelChangeEvent(PlayerLevelChangeEvent playerLevelChangeEvent) {
    if (EventActionManager.eventAction.playerLevelChange.isEnabled()) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () ->
              this.plugin
                  .getEventActionManager()
                  .performCommands(
                      EventActionManager.eventAction.playerLevelChange,
                      playerLevelChangeEvent.getPlayer()),
          1L);
    }
  }
}
