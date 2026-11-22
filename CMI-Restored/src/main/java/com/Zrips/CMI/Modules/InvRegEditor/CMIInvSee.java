package com.Zrips.CMI.Modules.InvRegEditor;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIInvSee {
  Player watcher = null;
  Player target = null;
  private ItemStack[] contents;
  private ItemStack[] armor;
  private ItemStack offHand;
  private ItemStack cursor;
  private CMITask task;
  private CMITask lockTask;
  private boolean tempLocked = false;

  public CMIInvSee(Player player, Player targetPlayer) {
    this.watcher = player;
    this.target = targetPlayer;
  }

  public Player getWatcher() {
    return this.watcher;
  }

  public CMIInvSee setWatcher(Player player) {
    this.watcher = player;
    return this;
  }

  public Player getTarget() {
    return CMIUser.getOnlinePlayer(this.target.getUniqueId()) != null
        ? CMIUser.getOnlinePlayer(this.target.getUniqueId())
        : this.target;
  }

  public CMIInvSee setTarget(Player player) {
    this.target = player;
    return this;
  }

  public void setTargetContents(ItemStack[] itemStacks) {
    this.contents = itemStacks;
  }

  public void setTargetArmorContents(ItemStack[] itemStacks) {
    this.armor = itemStacks;
  }

  public ItemStack[] getContents() {
    return this.contents;
  }

  public ItemStack[] getArmorContents() {
    return this.armor;
  }

  public void setOffHand(ItemStack itemStack) {
    this.offHand = itemStack;
  }

  public ItemStack getOffHand() {
    return this.offHand;
  }

  public ItemStack getCursor() {
    return this.cursor;
  }

  public void setCursor(ItemStack itemStack) {
    this.cursor = itemStack;
  }

  public void setTask(CMITask cmiTask) {
    this.task = cmiTask;
  }

  public CMITask getTask() {
    return this.task;
  }

  public boolean isTempLocked() {
    return this.tempLocked;
  }

  public void setTempLocked(boolean flag) {
    this.tempLocked = flag;
    if (flag) {
      if (this.lockTask != null) {
        this.lockTask.cancel();
      }

      this.lockTask =
          CMIScheduler.runTaskLater(
              CMI.getInstance(),
              () -> {
                this.setTempLocked(false);
                this.lockTask = null;
              },
              2L);
    }
  }
}
