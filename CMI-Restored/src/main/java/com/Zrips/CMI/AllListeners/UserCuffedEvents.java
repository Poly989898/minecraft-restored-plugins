package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.cuff;
import com.Zrips.CMI.commands.list.util;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class UserCuffedEvents implements Listener {
  private CMI plugin;

  public UserCuffedEvents(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void chat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    Player player = asyncPlayerChatEvent.getPlayer();
    if (this.plugin.getConfigManager().CuffMute) {
      if (this.plugin.getPlayerManager().isCuffed(player)) {
        CMILC.info(cuff.class, player, "noChat");
        asyncPlayerChatEvent.setCancelled(true);
        asyncPlayerChatEvent.getRecipients().clear();
        asyncPlayerChatEvent.setMessage("");
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
    Player player = playerInteractEvent.getPlayer();
    if (this.plugin.getPlayerManager().isCuffed(player)) {
      playerInteractEvent.setCancelled(true);
      CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(InventoryClickEvent inventoryClickEvent) {
    if (inventoryClickEvent.getWhoClicked() instanceof Player) {
      Player player = (Player) inventoryClickEvent.getWhoClicked();
      if (this.plugin.getPlayerManager().isCuffed(player)) {
        inventoryClickEvent.setCancelled(true);
        CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDropItemEvent(PlayerDropItemEvent playerDropItemEvent) {
    Player player = playerDropItemEvent.getPlayer();
    if (this.plugin.getPlayerManager().isCuffed(player)) {
      playerDropItemEvent.setCancelled(true);
      CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onProjectileLaunchEvent(ProjectileLaunchEvent projectileLaunchEvent) {
    if (projectileLaunchEvent.getEntity().getShooter() instanceof Player) {
      Player player = (Player) projectileLaunchEvent.getEntity().getShooter();
      if (this.plugin.getPlayerManager().isCuffed(player)) {
        projectileLaunchEvent.setCancelled(true);
        CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onHit(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
      Player player = (Player) entityDamageByEntityEvent.getDamager();
      if (this.plugin.getPlayerManager().isCuffed(player)) {
        if (util.imortaldebug) {
          CMIMessages.consoleMessage("onHit->EntityDamageByEntityEvent");
        }

        entityDamageByEntityEvent.setCancelled(true);
        CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      if (this.plugin.getPlayerManager().isCuffed(player)) {
        if (DamageCause.STARVATION.equals(entityDamageEvent.getCause())) {
          player.setFoodLevel(20);
        }

        if (util.imortaldebug) {
          CMIMessages.consoleMessage("Cuffed onPlayerDamage->EntityDamageEvent");
        }

        entityDamageEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    if (this.plugin.getPlayerManager().isCuffed(player)) {
      Location location = playerMoveEvent.getFrom();
      Location toLocation = playerMoveEvent.getTo();
      if (playerMoveEvent.getTo() != null) {
        if (location.getWorld() != toLocation.getWorld()
            || location.getBlockX() != toLocation.getBlockX()
            || location.getBlockZ() != toLocation.getBlockZ()
            || location.getBlockY() < toLocation.getBlockY()) {
          CMITitleMessage.send(player, CMILC.getIM(cuff.class, "cuffedInfo"), null);
          playerMoveEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerMove(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    Player player = playerCommandPreprocessEvent.getPlayer();
    if (this.plugin.getPlayerManager().isCuffed(player)) {
      if (!PermissionsManager.CMIPerm.command_cuff_bypass.hasPermission(player)) {
        String string = playerCommandPreprocessEvent.getMessage();
        String commandText = string.startsWith("/") ? string.substring(1) : string;
        String lowerCommand = commandText.toLowerCase();
        boolean flag = false;

        for (String allowedCommand : this.plugin.getConfigManager().CuffAllowed) {
          if (lowerCommand.equalsIgnoreCase(allowedCommand.toLowerCase())
              || lowerCommand.startsWith(allowedCommand.toLowerCase() + " ")) {
            flag = true;
          }
        }

        if (!flag) {
          playerCommandPreprocessEvent.setCancelled(true);
          playerCommandPreprocessEvent.setMessage("/");
          player.sendMessage(CMILC.getIM(cuff.class, "noCommand"));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLogin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (cmiUser.isCuffed()) {
        this.plugin.getPlayerManager().addCuffed(player.getUniqueId());
      }
    }
  }
}
