package com.Zrips.CMI.Modules.Vanish;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIMessageReplies;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.commands.list.vanish;
import com.Zrips.CMI.events.CMIPlayerVanishEvent;
import java.util.Iterator;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class VanishListener implements Listener {
  private CMI plugin;

  public VanishListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onFishingRodUseOnPlayer(PlayerFishEvent playerFishEvent) {
    if (playerFishEvent.getState() == State.CAUGHT_ENTITY
        || playerFishEvent.getState() == State.FISHING) {
      CMIUser cmiUser = CMIUser.getUser(playerFishEvent.getPlayer());
      if (cmiUser != null) {
        if (cmiUser.isCMIVanished()
            && VanishManager.is(cmiUser.getUniqueId(), VanishAction.hookPlayers)) {
          playerFishEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerPickupItemEvent(PlayerPickupItemEvent playerPickupItemEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerPickupItemEvent.getPlayer());
    if (cmiUser != null) {
      if (cmiUser.isCMIVanished()
          && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.itemPickup)) {
        playerPickupItemEvent.setCancelled(true);
        playerPickupItemEvent
            .getItem()
            .setPickupDelay(
                playerPickupItemEvent.getItem().getPickupDelay() < 20
                    ? 20
                    : playerPickupItemEvent.getItem().getPickupDelay());
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void CreatureSpawnEvent(CreatureSpawnEvent creatureSpawnEvent) {
    if (!creatureSpawnEvent.isCancelled()
        && !this.plugin.getVanishManager().getAllVanished().isEmpty()) {
      if ((creatureSpawnEvent.getSpawnReason().equals(SpawnReason.NATURAL)
              || creatureSpawnEvent.getSpawnReason().equals(SpawnReason.SPAWNER))
          && !this.plugin.getVanishManager().nearActivePlayer(creatureSpawnEvent.getLocation())) {
        creatureSpawnEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void SpawnerSpawnEvent(SpawnerSpawnEvent spawnerSpawnEvent) {
    if (!spawnerSpawnEvent.isCancelled()
        && !this.plugin.getVanishManager().getAllVanished().isEmpty()) {
      if (!this.plugin.getVanishManager().nearActivePlayer(spawnerSpawnEvent.getLocation())) {
        spawnerSpawnEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void EntityDamageByEntityEvent(ProjectileHitEvent projectileHitEvent) {
    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
      Entity entity = projectileHitEvent.getHitEntity();
      if (entity instanceof Player) {
        Player player = (Player) entity;
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          if (cmiUser.isCMIVanished()) {
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
              Projectile projectile = projectileHitEvent.getEntity();
              if (projectile instanceof Arrow) {
                Arrow arrow = (Arrow) projectile;
                if (arrow.getPierceLevel() > 0) {
                  arrow.remove();
                }
              }
            }

            projectileHitEvent.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLeave(PlayerQuitEvent playerQuitEvent) {
    if (!this.plugin.getBungeeCordManager().isBungeeCord()) {
      this.plugin.getUtilManager().removeMessageReplyTo(playerQuitEvent.getPlayer().getName());
    }

    this.plugin.getVanishManager().removePlayer(playerQuitEvent.getPlayer().getUniqueId(), false);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerJoinHideOthers(PlayerJoinEvent playerJoinEvent) {
    this.plugin.getVanishManager().hideVanishedPlayersFor(playerJoinEvent.getPlayer());
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerJoinEvent.getPlayer());
    if (cmiUser != null
        && (!cmiUser.isCMIVanished()
            || !VanishManager.is(cmiUser.getUniqueId(), VanishAction.relogDisable))) {
      this.plugin.getVanishManager().applyVanish(cmiUser, false, false, true);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onVanish(CMIPlayerVanishEvent cmiPlayerVanishEvent) {
    if (!cmiPlayerVanishEvent.isCancelled()
        && !VanishManager.is(
            cmiPlayerVanishEvent.getPlayer().getUniqueId(), VanishAction.PrivateMessages)) {
      CMIMessageReplies.removeMessageReplyTo(cmiPlayerVanishEvent.getPlayer().getName());
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoinHideYou(PlayerJoinEvent playerJoinEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerJoinEvent.getPlayer());
    if (cmiUser != null) {
      if (!cmiUser.isCMIVanished()
          && VanishManager.is(cmiUser.getUniqueId(), VanishAction.joinVanished)
          && PermissionsManager.CMIPerm.command_$1.hasPermission(
              playerJoinEvent.getPlayer(), vanish.class.getSimpleName())) {
        cmiUser.setVanished(true, false);
        this.plugin.getVanishManager().hidePlayerForEveryone(playerJoinEvent.getPlayer());
      } else if (cmiUser.isCMIVanished()
          && VanishManager.is(cmiUser.getUniqueId(), VanishAction.relogDisable)) {
        cmiUser.setVanished(false, false);
        if (!VanishManager.is(cmiUser.getUniqueId(), VanishAction.informOnJoin)) {
          playerJoinEvent.setJoinMessage("");
        }
      } else {
        if (cmiUser.isCMIVanished()
            && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.informOnJoin)) {
          playerJoinEvent.setJoinMessage("");
        }

        if (cmiUser.isCMIVanished()) {
          this.plugin.getVanishManager().hidePlayerForEveryone(playerJoinEvent.getPlayer());
        }
      }
    }
  }

  @EventHandler
  public void onEntityTarget(EntityTargetEvent entityTargetEvent) {
    if (entityTargetEvent.getTarget() instanceof Player) {
      CMIUser cmiUser = CMIUser.getUser((Player) entityTargetEvent.getTarget());
      if (cmiUser != null) {
        if (entityTargetEvent.getEntity() instanceof ExperienceOrb
            && cmiUser.isCMIVanished()
            && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.itemPickup)) {
          Player player = (Player) entityTargetEvent.getTarget();
          if (player != null) {
            entityTargetEvent.setCancelled(true);
            entityTargetEvent.setTarget(null);
          }
        } else {
          if (cmiUser.isCMIVanished()
              && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.mobAggro)) {
            entityTargetEvent.setCancelled(true);
            entityTargetEvent.setTarget(null);
          }
        }
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
    Entity entity = entityDamageEvent.getEntity();
    if (entity instanceof Player) {
      Player player = (Player) entity;
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser == null) {
        return;
      }

      if (cmiUser.isCMIVanished()
          && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.playerDamage)) {
        if (util.imortaldebug) {
          CMIMessages.consoleMessage("Vanish onEntityDamage->EntityDamageEvent");
        }

        entityDamageEvent.setCancelled(true);
      }
    }

    if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent entityDamageByEntityEvent =
          (EntityDamageByEntityEvent) entityDamageEvent;
      Entity damager = entityDamageByEntityEvent.getDamager();
      Player damagerPlayer = null;
      if (damager instanceof Player) {
        damagerPlayer = (Player) damager;
      } else if (damager instanceof Projectile) {
        Projectile projectile = (Projectile) damager;
        if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
          damagerPlayer = (Player) projectile.getShooter();
        }
      }

      CMIUser damagerUser = CMIUser.getUser(damagerPlayer);
      if (damagerUser == null) {
        return;
      }

      if (damagerUser.isCMIVanished()
          && !VanishManager.is(damagerUser.getUniqueId(), VanishAction.damageToEntity)) {
        if (util.imortaldebug) {
          CMIMessages.consoleMessage("Vanish2 onEntityDamage->EntityDamageEvent");
        }

        entityDamageEvent.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction().equals(Action.PHYSICAL)) {
      Player player = playerInteractEvent.getPlayer();
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isCMIVanished()
            && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.interaction)) {
          playerInteractEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void ping(ServerListPingEvent serverListPingEvent) {
    Iterator iterator;
    try {
      iterator = serverListPingEvent.iterator();
    } catch (UnsupportedOperationException unsupportedOperationException) {
      return;
    }

    while (iterator.hasNext()) {
      Player player = (Player) iterator.next();
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null && cmiUser.isVanished()) {
        try {
          iterator.remove();
        } catch (Throwable throwable) {
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onQuit(PlayerQuitEvent playerQuitEvent) {
    CMIUser cmiUser = CMIUser.getUser(playerQuitEvent.getPlayer());
    if (cmiUser != null) {
      if (cmiUser.isCMIVanished()
          && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.informOnLeave)) {
        playerQuitEvent.setQuitMessage("");
      }

      cmiUser.hideBossBars();
      cmiUser.clearBossMaps();
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser != null) {
      if (cmiUser.isCMIVanished()
          && !VanishManager.is(cmiUser.getUniqueId(), VanishAction.deathMessages)) {
        playerDeathEvent.setDeathMessage("");
      }
    }
  }
}
