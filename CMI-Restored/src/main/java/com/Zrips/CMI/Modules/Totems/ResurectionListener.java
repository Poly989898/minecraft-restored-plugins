package com.Zrips.CMI.Modules.Totems;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.util;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ResurectionListener implements Listener {
  private CMI plugin;

  public ResurectionListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
    if (this.plugin.getTotemManager().isTotemWarmupUse()
        && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR
            || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      Player player = playerInteractEvent.getPlayer();
      if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
        if (!this.plugin.getTotemManager().isOnWarmup(player)) {
          if (!this.plugin.getTotemManager().isOnCd(player)) {
            if (this.removeTotemMainHand(player)) {
              this.plugin.getTotemManager().addWarmupPlayer(player);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void EntityResurrectEventTotemUse(EntityResurrectEvent entityResurrectEvent) {
    if (!entityResurrectEvent.isCancelled()
        && this.plugin.getTotemManager().isTotemCooldownUse()
        && entityResurrectEvent.getEntity() instanceof Player) {
      Player player = (Player) entityResurrectEvent.getEntity();
      if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
        Long cooldown =
            this.plugin
                .getTotemManager()
                .getLeftCd(player, this.plugin.getTotemManager().getTotemCooldownTime());
        if (cooldown > 0L) {
          entityResurrectEvent.setCancelled(true);
          CMILC.info_totem_cantConsume.sendMessage(player);
          player.setHealth(0.0);
        } else {
          this.plugin.getTotemManager().addCooldownPlayer(player);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityResurrectEventTotemUse(EntityResurrectEvent entityResurrectEvent) {
    if (!entityResurrectEvent.isCancelled() && entityResurrectEvent.getEntity() instanceof Player) {
      Player player = (Player) entityResurrectEvent.getEntity();
      if (this.plugin.getTotemManager().isFullDisableInWorlds(player.getWorld())) {
        entityResurrectEvent.setCancelled(true);
        player.setHealth(0.0);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {}

  @EventHandler(priority = EventPriority.HIGHEST)
  public void EntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (entityDamageByEntityEvent.getEntity() instanceof Player
        && !entityDamageByEntityEvent.isCancelled()
        && this.plugin.getTotemManager().isTotemRemoveFromInventory()
        && !this.plugin.getTotemManager().isTotemWarmupUse()) {
      Player player = (Player) entityDamageByEntityEvent.getEntity();
      if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
        if (!this.plugin.getTotemManager().isTotemRequirePermission()
            || PermissionsManager.CMIPerm.totem_frominventory.hasPermission(player)) {
          double number = entityDamageByEntityEvent.getFinalDamage();
          double health = player.getHealth();
          if (!(health - number > 0.0)) {
            if (haveTotem(player)) {
              EntityResurrectEvent entityResurrectEvent = new EntityResurrectEvent(player);
              Bukkit.getServer().getPluginManager().callEvent(entityResurrectEvent);
              if (!entityResurrectEvent.isCancelled()) {
                ItemStack itemStack = this.removeTotem(player);
                if (itemStack == null) {
                  return;
                }

                entityDamageByEntityEvent.setDamage(0.0);
                if (util.imortaldebug) {
                  CMIMessages.consoleMessage(
                      "Totem EntityDamageByEntityEvent->EntityDamageByEntityEvent");
                }

                this.plugin.getNMS().showResurection(player);
                player.updateInventory();
                this.plugin.getTotemManager().stopWarmupEffect(player);
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void EntityDamageByEntityEventWarmup(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (entityDamageByEntityEvent.getEntity() instanceof Player
        && this.plugin.getTotemManager().isTotemWarmupUse()) {
      Player player = (Player) entityDamageByEntityEvent.getEntity();
      if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
        double number = entityDamageByEntityEvent.getFinalDamage();
        double health = player.getHealth();
        if (!(health - number > 0.0)) {
          if (this.plugin.getTotemManager().isOnWarmup(player)) {
            EntityResurrectEvent entityResurrectEvent = new EntityResurrectEvent(player);
            Bukkit.getServer().getPluginManager().callEvent(entityResurrectEvent);
            if (!entityResurrectEvent.isCancelled()) {
              if (util.imortaldebug) {
                CMIMessages.consoleMessage(
                    "Totem EntityDamageByEntityEventWarmup->EntityDamageByEntityEvent");
              }

              entityDamageByEntityEvent.setCancelled(true);
              this.plugin.getNMS().showResurection(player);
              this.plugin.getTotemManager().stopWarmupEffect(player);
              this.plugin.getTotemManager().removePlayer(player, TotemManager.BossBarType.Warmup);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)
        && entityDamageEvent.getEntity() instanceof Player
        && !DamageCause.SUICIDE.equals(entityDamageEvent.getCause())
        && !entityDamageEvent.isCancelled()) {
      if (this.plugin.getTotemManager().isTotemRemoveFromInventory()) {
        Player player = (Player) entityDamageEvent.getEntity();
        if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
          double number = entityDamageEvent.getFinalDamage();
          if (!(number >= 1000.0)) {
            double health = player.getHealth();
            if (!(health - number > 0.0)) {
              if (haveTotem(player)) {
                EntityResurrectEvent entityResurrectEvent = new EntityResurrectEvent(player);
                Bukkit.getServer().getPluginManager().callEvent(entityResurrectEvent);
                if (!entityResurrectEvent.isCancelled()) {
                  ItemStack itemStack = this.removeTotem(player);
                  if (itemStack != null) {
                    entityDamageEvent.setDamage(0.0);
                    entityDamageEvent.setCancelled(true);
                    if (DamageCause.VOID.equals(entityDamageEvent.getCause())
                        && this.plugin.getTotemManager().isTotemProtectFromVoid()) {
                      Location location = this.plugin.getHomeManager().getReSpawnLocation(player);
                      CMIGod.forceApplyNoDamageTick(player, 20);
                      if (location != null) {
                        this.plugin.getNMS().showResurection(player);
                        this.plugin.getTotemManager().stopWarmupEffect(player);
                        player.setHealth(health + number);
                        this.plugin
                            .getTeleportHandler()
                            .teleportPlayer(player, location, CMITeleportType.Totem)
                            .thenAccept(
                                cmiTeleportFeedback -> {
                                  if (cmiTeleportFeedback.isSuccess()) {
                                    player.updateInventory();
                                  }
                                });
                      }
                    } else {
                      this.plugin.getNMS().showResurection(player);
                      this.plugin.getTotemManager().stopWarmupEffect(player);
                      player.updateInventory();
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

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityDamageEventWarmup(EntityDamageEvent entityDamageEvent) {
    if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)
        && entityDamageEvent.getEntity() instanceof Player
        && !DamageCause.SUICIDE.equals(entityDamageEvent.getCause())
        && this.plugin.getTotemManager().isTotemWarmupUse()) {
      Player player = (Player) entityDamageEvent.getEntity();
      if (!this.plugin.getTotemManager().isBlackListedWorld(player.getWorld())) {
        double number = entityDamageEvent.getFinalDamage();
        double health = player.getHealth();
        if (!(health - number > 0.0)) {
          if (this.plugin.getTotemManager().isOnWarmup(player)) {
            EntityResurrectEvent entityResurrectEvent = new EntityResurrectEvent(player);
            Bukkit.getServer().getPluginManager().callEvent(entityResurrectEvent);
            if (!entityResurrectEvent.isCancelled()) {
              entityDamageEvent.setCancelled(true);
              if (util.imortaldebug) {
                CMIMessages.consoleMessage(
                    "Resurection EntityDamageEventWarmup->EntityDamageEvent");
              }

              if (DamageCause.VOID.equals(entityDamageEvent.getCause())
                  && this.plugin.getTotemManager().isTotemProtectFromVoid()) {
                CMIGod.forceApplyNoDamageTick(player, 20);
                Location location = this.plugin.getHomeManager().getReSpawnLocation(player);
                if (location != null) {
                  this.plugin
                      .getTeleportHandler()
                      .teleportPlayer(player, location, CMITeleportType.Totem)
                      .thenAccept(
                          cmiTeleportFeedback -> {
                            if (cmiTeleportFeedback.isSuccess()) {
                              this.plugin.getNMS().showResurection(player);
                              player.updateInventory();
                              this.plugin.getTotemManager().stopWarmupEffect(player);
                            }
                          });
                }
              } else {
                this.plugin.getNMS().showResurection(player);
                this.plugin.getTotemManager().stopWarmupEffect(player);
              }

              this.plugin.getTotemManager().removePlayer(player, TotemManager.BossBarType.Warmup);
            }
          }
        }
      }
    }
  }

  public static boolean haveTotem(Player player) {
    ItemStack[] itemStacks;
    int value = (itemStacks = player.getInventory().getContents()).length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = itemStacks[index];
      if (itemStack != null && CMIMaterial.TOTEM_OF_UNDYING.equals(itemStack.getType())) {
        return true;
      }
    }

    return false;
  }

  private boolean removeTotemMainHand(Player player) {
    ItemStack itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
    if (itemStack != null && CMIMaterial.TOTEM_OF_UNDYING.equals(itemStack.getType())) {
      if (itemStack.getAmount() == 1) {
        CMILib.getInstance().getReflectionManager().setItemInMainHand(player, null);
      } else {
        ItemStack copyItem = itemStack.clone();
        copyItem.setAmount(itemStack.getAmount() - 1);
        CMILib.getInstance().getReflectionManager().setItemInMainHand(player, copyItem);
      }

      player.updateInventory();
      return true;
    } else {
      return false;
    }
  }

  private ItemStack removeTotem(Player player) {
    int value = -1;
    ItemStack itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
    if (itemStack != null && CMIMaterial.TOTEM_OF_UNDYING.equals(itemStack.getType())) {
      ItemStack resultItem = null;
      if (itemStack.getAmount() == 1) {
        CMILib.getInstance().getReflectionManager().setItemInMainHand(player, null);
        resultItem = itemStack.clone();
        resultItem.setAmount(1);
      } else {
        ItemStack copyItem = itemStack.clone();
        copyItem.setAmount(itemStack.getAmount() - 1);
        CMILib.getInstance().getReflectionManager().setItemInMainHand(player, copyItem);
        resultItem = copyItem.clone();
        resultItem.setAmount(1);
      }

      player.updateInventory();
      return resultItem;
    } else {
      ItemStack[] itemStacks;
      int contentsLength = (itemStacks = player.getInventory().getContents()).length;

      for (int i = 0; i < contentsLength; i++) {
        ItemStack invItem = itemStacks[i];
        value++;
        if (invItem != null && CMIMaterial.TOTEM_OF_UNDYING.equals(invItem.getType())) {
          ItemStack totemToReturn = null;
          if (invItem.getAmount() == 1) {
            player.getInventory().setItem(value, null);
            totemToReturn = invItem.clone();
            totemToReturn.setAmount(1);
          } else {
            ItemStack remainingItem = invItem.clone();
            remainingItem.setAmount(invItem.getAmount() - 1);
            player.getInventory().setItem(value, remainingItem);
            totemToReturn = remainingItem.clone();
            totemToReturn.setAmount(1);
          }

          player.updateInventory();
          return totemToReturn;
        }
      }

      return null;
    }
  }
}
