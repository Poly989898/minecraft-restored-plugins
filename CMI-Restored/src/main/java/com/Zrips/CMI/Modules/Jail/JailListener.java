package com.Zrips.CMI.Modules.Jail;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.jail;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class JailListener implements Listener {
  private CMI plugin;
  protected Map<UUID, Long> lastUpdate;

  public JailListener(CMI cmi) {
    this.plugin = cmi;
    this.lastUpdate = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerRespawnEventHunger(PlayerRespawnEvent playerRespawnEvent) {
    Player player = playerRespawnEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.isJailed()) {
        if (cmiUser.getCell().getTeleportInLocation() != null) {
          playerRespawnEvent.setRespawnLocation(cmiUser.getCell().getTeleportInLocation());
        }

        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              CMIJail cmiJail = this.plugin.getJailManager().getByLoc(player.getLocation());
              if (cmiJail == null) {
                if (cmiUser.getCell().getTeleportInLocation() != null) {
                  playerRespawnEvent.setRespawnLocation(cmiUser.getCell().getTeleportInLocation());
                }
              }
            },
            10L);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onFoodChange(FoodLevelChangeEvent foodLevelChangeEvent) {
    if (foodLevelChangeEvent.getEntity() instanceof Player) {
      Player player = (Player) foodLevelChangeEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
          if (this.plugin.getJailManager().isNoHunger()) {
            if (player.getFoodLevel() >= foodLevelChangeEvent.getFoodLevel()) {
              foodLevelChangeEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (player != null) {
      if (this.plugin.getJailManager().anyJailedOnline()) {
        Location location = playerMoveEvent.getFrom();
        Location toLocation = playerMoveEvent.getTo();
        if (playerMoveEvent.getTo() != null) {
          if (location.getBlockX() != toLocation.getBlockX()
              || location.getBlockY() != toLocation.getBlockY()
              || location.getBlockZ() != toLocation.getBlockZ()) {
            if (!CMINPCChecker.isNPC(player)) {
              CMIUser cmiUser = CMIUser.getUser(player);
              if (cmiUser != null) {
                if (cmiUser.isJailed()) {
                  Long lastMoveUpdate = this.lastUpdate.get(player.getUniqueId());
                  if (lastMoveUpdate == null
                      || System.currentTimeMillis() - lastMoveUpdate
                          >= this.plugin.getJailManager().getJailsCheckInterval()) {
                    this.lastUpdate.put(player.getUniqueId(), System.currentTimeMillis());
                    CMIJail cmiJail = this.plugin.getJailManager().getByLoc(toLocation);
                    if (cmiJail == null) {
                      if (cmiUser.getCell().getJail().isPreventLeave()) {
                        cmiUser.getCell().teleportToCell(cmiUser);
                        this.plugin.getJailManager().informAboutLeftTime(player);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    Player player = playerQuitEvent.getPlayer();
    if (player != null) {
      this.lastUpdate.remove(player.getUniqueId());
      this.plugin.getJailManager().removeOnline(this.plugin.getPlayerManager().getUser(player));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player != null) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        cmiUser.updateJailedTimeOnJoin();
        if (cmiUser.isJailed()) {
          this.plugin.getJailManager().placePlayerIntoJail(cmiUser);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(InventoryClickEvent inventoryClickEvent) {
    if (inventoryClickEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
          inventoryClickEvent.setCancelled(true);
          this.plugin.getJailManager().informAboutLeftTime(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(PlayerDropItemEvent playerDropItemEvent) {
    Player player = playerDropItemEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.isJailed()) {
        playerDropItemEvent.setCancelled(true);
        this.plugin.getJailManager().informAboutLeftTime(player);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onHit(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
      Player player = (Player) entityDamageByEntityEvent.getDamager();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed() && this.plugin.getJailManager().isPreventDamage()) {
          entityDamageByEntityEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
          if (DamageCause.STARVATION.equals(entityDamageEvent.getCause())) {
            player.setFoodLevel(20);
            entityDamageEvent.setCancelled(true);
            if (util.imortaldebug) {
              CMIMessages.consoleMessage("Jail onPlayerDamage->EntityDamageEvent");
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerMove(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    Player player = playerCommandPreprocessEvent.getPlayer();
    if (!playerCommandPreprocessEvent.isCancelled()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
          if (!PermissionsManager.CMIPerm.command_jail_bypasscmd.hasPermission(player)) {
            if (!this.plugin
                .getJailManager()
                .canUseCommand(playerCommandPreprocessEvent.getMessage())) {
              this.plugin.getJailManager().informAboutLeftTime(player);
              CMILC.info(jail.class, player, "noCommand");
              playerCommandPreprocessEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerPickupItemEvent(PlayerPickupItemEvent playerPickupItemEvent) {
    if (this.plugin.getJailManager().anyJailedOnline()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerPickupItemEvent.getPlayer());
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
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
  }

  @EventHandler(priority = EventPriority.LOW)
  public void AsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isJailed()) {
          if (this.plugin.getJailManager().getJailChatRange().intValue() != 0.0) {
            if (!PermissionsManager.CMIPerm.chat_rangebypass.hasSetPermission(player)) {
              if (this.plugin.getJailManager().getJailChatRange().intValue() <= -1.0) {
                CMILC.info(jail.class, player, "cantTalk");
                asyncPlayerChatEvent.setCancelled(true);
              } else {
                int value = this.plugin.getJailManager().getJailChatRange();
                HashSet<Player> hashSet = new HashSet<>(asyncPlayerChatEvent.getRecipients());
                if (value > 0) {
                  for (Player otherPlayer : hashSet) {
                    if (!otherPlayer.getName().equals(player.getName())) {
                      if (!otherPlayer.getWorld().equals(player.getWorld())
                          && !PermissionsManager.CMIPerm.chat_rangespy.hasSetPermission(
                              otherPlayer)) {
                        asyncPlayerChatEvent.getRecipients().remove(otherPlayer);
                      } else if (otherPlayer.getWorld().equals(player.getWorld())) {
                        double number = otherPlayer.getLocation().distance(player.getLocation());
                        if (number > value
                            && !PermissionsManager.CMIPerm.chat_rangespy.hasSetPermission(
                                otherPlayer)) {
                          asyncPlayerChatEvent.getRecipients().remove(otherPlayer);
                        }
                      }
                    }
                  }

                  CMILC.info(jail.class, player, "cantTalkRange", "[range]", value);
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIAfkEnterEvent(CMIAfkEnterEvent cmiAfkEnterEvent) {
    if (!cmiAfkEnterEvent.isCancelled()) {
      if (this.plugin.getJailManager().isNoAfk()) {
        this.plugin.getJailManager().hideBossBar(CMIUser.getUser(cmiAfkEnterEvent.getPlayer()));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIAfkEnterEvent(CMIAfkLeaveEvent cmiAfkLeaveEvent) {
    if (this.plugin.getJailManager().isNoAfk()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(cmiAfkLeaveEvent.getPlayer());
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        CMIScheduler.runTask(
            this.plugin, () -> this.plugin.getJailManager().refreshBossBar(cmiUser));
      }
    }
  }
}
