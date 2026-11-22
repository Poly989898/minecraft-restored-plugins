package com.Zrips.CMI.Containers;

import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Speed {
  private BossBarInfo bb;
  private Player player;
  private boolean allowBoost = false;
  private boolean allowSuperBoost = false;
  private boolean speedometer = false;
  private List<Long> time = new ArrayList<>();
  private List<Location> loc = new ArrayList<>();
  private Long boost = 0L;
  private Long superBoost = 0L;
  private boolean free = false;
  private boolean dura = false;
  private static int amount = 5;
  private static int timeRange = 500 / amount;

  public Speed(Player elytraPlayer) {
    this.player = elytraPlayer;
    if (PermissionsManager.CMIPerm.elytra_boost.hasPermission(elytraPlayer)) {
      this.allowBoost = true;
    }

    if (PermissionsManager.CMIPerm.elytra_superboost.hasPermission(elytraPlayer)) {
      this.allowSuperBoost = true;
    }

    if (!PermissionsManager.CMIPerm.elytra.hasPermission(elytraPlayer)) {
      ItemStack itemStack = elytraPlayer.getInventory().getChestplate();
      if (itemStack != null && itemStack.getType() == Material.ELYTRA) {
        elytraPlayer.getInventory().setChestplate(null);
        if (elytraPlayer.getInventory().firstEmpty() == -1) {
          CMIUser.getUser(elytraPlayer).dropItemNearPlayer(itemStack);
        } else {
          elytraPlayer.getInventory().addItem(new ItemStack[] {itemStack});
        }

        elytraPlayer.updateInventory();
      }

      elytraPlayer.setGliding(false);
    }

    if (PermissionsManager.CMIPerm.elytra_speedometer.hasPermission(elytraPlayer)) {
      this.speedometer = true;
    }

    if (PermissionsManager.CMIPerm.elytra_freeflight.hasPermission(elytraPlayer)) {
      this.free = true;
    }

    if (PermissionsManager.CMIPerm.informDurability.hasPermission(elytraPlayer)) {
      this.dura = true;
    }

    this.loc.add(elytraPlayer.getLocation());
    this.time.add(System.currentTimeMillis());
  }

  private long getTime() {
    while (this.time.size() > amount) {
      this.time.remove(0);
    }

    return this.time.isEmpty() ? System.currentTimeMillis() : this.time.get(0);
  }

  private Location getLocation() {
    while (this.loc.size() > amount) {
      this.loc.remove(0);
    }

    return this.loc.isEmpty() ? null : this.loc.get(0);
  }

  public double getSpeed() {
    Location location = this.getLocation();
    if (location != null && this.player.getWorld().equals(location.getWorld())) {
      double number = this.player.getLocation().distance(location);
      long longValue = System.currentTimeMillis() - this.getTime();
      return (int) (number / longValue * 100.0 * 6.0 * 6.0 * 100.0) / 100.0;
    } else {
      return 0.0;
    }
  }

  public void setLoc(Location location) {
    this.loc.add(location);

    while (this.loc.size() > amount) {
      this.loc.remove(0);
    }
  }

  public boolean isGoodTime() {
    timeRange = 500 / amount;
    return this.time.isEmpty()
        ? true
        : System.currentTimeMillis() - this.time.get(this.time.size() - 1) > timeRange;
  }

  public void setTime(Long timeValue) {
    this.time.add(timeValue);

    while (this.time.size() > amount) {
      this.time.remove(0);
    }
  }

  public Long getBoost() {
    return this.boost;
  }

  public void setBoost(Long boostValue) {
    this.boost = boostValue;
  }

  public boolean isBoosting() {
    return System.currentTimeMillis() - this.boost < 250L;
  }

  public Long getSuperBoost() {
    return this.superBoost;
  }

  public void setSuperBoost(Long superBoostValue) {
    this.superBoost = superBoostValue;
  }

  public boolean isSuperBoosting() {
    return System.currentTimeMillis() - this.superBoost < 1000L;
  }

  public boolean isAllowBoost() {
    return this.allowBoost;
  }

  public boolean isAllowSuperBoost() {
    return this.allowSuperBoost;
  }

  public boolean isSpeedometer() {
    return this.speedometer;
  }

  public boolean isFree() {
    return this.free;
  }

  public void setFree(boolean flag) {
    this.free = flag;
  }

  public BossBarInfo getBb() {
    return this.bb;
  }

  public void setBb(BossBarInfo bossBarInfo) {
    this.bb = bossBarInfo;
  }

  public boolean isShowDurability() {
    return this.dura;
  }
}
