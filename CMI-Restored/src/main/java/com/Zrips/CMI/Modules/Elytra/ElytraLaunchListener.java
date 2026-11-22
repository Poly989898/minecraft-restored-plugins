package com.Zrips.CMI.Modules.Elytra;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.launch;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraLaunchListener implements Listener {
  private CMI plugin;
  HashMap<UUID, ElytraLaunchListener.CMILaunch> map = new HashMap<>();

  public ElytraLaunchListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void onSneakEnd(PlayerToggleSneakEvent playerToggleSneakEvent) {
    if (!playerToggleSneakEvent.isSneaking()) {
      ElytraLaunchListener.CMILaunch cmiLaunch =
          this.map.remove(playerToggleSneakEvent.getPlayer().getUniqueId());
      if (cmiLaunch != null
          && cmiLaunch.isFullyCharged()
          && System.currentTimeMillis() - cmiLaunch.getLastCharge() < 200L) {
        if (!PermissionsManager.CMIPerm.elytralaunch.hasPermission(
            playerToggleSneakEvent.getPlayer(), true)) {
          return;
        }

        CMI.getInstance()
            .getCommandManager()
            .performCMICommand(
                Bukkit.getConsoleSender(),
                launch.class,
                playerToggleSneakEvent.getPlayer().getName(),
                "a:90",
                "p:1.5");
        Location location = playerToggleSneakEvent.getPlayer().getLocation().clone();
        location.setPitch(90.0F);
        location.add(0.0, 0.2, 0.0);
        ElytraBoostListener.spawnCircle(location);
      }
    }
  }

  @EventHandler
  public void onMoveRemove(PlayerMoveEvent playerMoveEvent) {
    if (!playerMoveEvent.isCancelled() && !this.map.isEmpty() && playerMoveEvent.getTo() != null) {
      if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockX()
          || playerMoveEvent.getFrom().getBlockY() != playerMoveEvent.getTo().getBlockY()
          || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ()) {
        this.map.remove(playerMoveEvent.getPlayer().getUniqueId());
      }
    }
  }

  @EventHandler
  public void onElytraLaunch(PlayerInteractEvent playerInteractEvent) {
    Player player = playerInteractEvent.getPlayer();
    if (player.isSneaking() && player.isOnGround()) {
      if (this.plugin.getElytraManager().getLaunchItemStack() == null
          || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR
          || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack != null) {
          if (this.plugin.getElytraManager().isLaunchItemStack(itemStack)) {
            if (PermissionsManager.CMIPerm.elytralaunch.hasPermission(
                playerInteractEvent.getPlayer())) {
              ElytraLaunchListener.CMILaunch cmiLaunch = this.map.get(player.getUniqueId());
              if (cmiLaunch == null) {
                cmiLaunch = new ElytraLaunchListener.CMILaunch();
              } else if (System.currentTimeMillis() - cmiLaunch.getLastCharge() > 333L) {
                this.map.remove(playerInteractEvent.getPlayer().getUniqueId());
                return;
              }

              CMIUser cmiUser =
                  this.plugin.getPlayerManager().getUser(playerInteractEvent.getPlayer());
              if (cmiUser != null) {
                ItemStack equippedItem =
                    cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate);
                if (equippedItem != null) {
                  try {
                    if (equippedItem.getType() != Material.ELYTRA) {
                      return;
                    }
                  } catch (Exception exception) {
                    return;
                  }

                  cmiLaunch.setLastCharge();
                  this.map.put(player.getUniqueId(), cmiLaunch);
                  if (this.plugin.getElytraManager().getLaunchTime() > 0.0) {
                    BossBarInfo bossBarInfo = cmiUser.getBossBar("ElytraLaunch");
                    if (bossBarInfo == null) {
                      bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "ElytraLaunch");
                    }

                    long longValue = System.currentTimeMillis() - cmiLaunch.getStarted();
                    double number =
                        longValue
                            * 100L
                            / (this.plugin.getElytraManager().getLaunchTime() * 1000.0)
                            / 100.0;
                    bossBarInfo.setPercentage(number);
                    bossBarInfo.setTitleOfBar(
                        CMILC.Elytra_Charging.getLocale(
                            "[percentage]", (int) (bossBarInfo.getPercentage() * 100.0)));
                    bossBarInfo.setKeepForTicks(10);
                    bossBarInfo.setColor(BarColor.RED);
                    if (bossBarInfo.getBar() != null) {
                      if (number > 0.3 && number < 1.0) {
                        bossBarInfo.setColor(BarColor.YELLOW);
                      }

                      if (number >= 1.0) {
                        bossBarInfo.setColor(BarColor.GREEN);
                      }
                    }

                    cmiUser.addBossBar(bossBarInfo);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private class CMILaunch {
    private Long started;
    private Long lastCharge;

    private CMILaunch() {}

    public Long getStarted() {
      return this.started;
    }

    public Long getLastCharge() {
      return this.lastCharge;
    }

    public void setLastCharge() {
      if (this.started == null) {
        this.started = System.currentTimeMillis();
      }

      if (this.lastCharge != null && this.lastCharge + 333L < System.currentTimeMillis()) {
        this.started = System.currentTimeMillis();
      } else {
        this.lastCharge = System.currentTimeMillis();
      }
    }

    public boolean isFullyCharged() {
      return this.started != null && this.lastCharge != null
          ? this.started.longValue()
                  + ElytraLaunchListener.this.plugin.getElytraManager().getLaunchTime() * 1000.0
              < System.currentTimeMillis()
          : false;
    }
  }
}
