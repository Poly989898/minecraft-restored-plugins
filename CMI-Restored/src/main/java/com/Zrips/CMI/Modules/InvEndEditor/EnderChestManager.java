package com.Zrips.CMI.Modules.InvEndEditor;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.ender;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;

public class EnderChestManager {
  private HashMap<UUID, EndWatcher> WatcherList = new HashMap<>();
  private HashMap<UUID, EndWatcher> WatchedList = new HashMap<>();
  private CMI plugin;

  public EnderChestManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void removeWatcher(Player player) {
    EndWatcher endWatcher = this.WatcherList.remove(player.getUniqueId());
    if (endWatcher != null) {
      this.WatchedList.remove(endWatcher.getTarget().getUniqueId());
      if (!endWatcher.getTarget().isOnline() && endWatcher.getSaveTask() != null) {
        endWatcher.getSaveTask().cancel();
        endWatcher.setSaveTask(null);
        this.plugin.save(endWatcher.getTarget());
      }
    }
  }

  public boolean isWatching(Player player) {
    return this.WatcherList.containsKey(player.getUniqueId());
  }

  public boolean isBeingWatched(Player player) {
    return this.WatchedList.containsKey(player.getUniqueId());
  }

  public EndWatcher getWatcher(Player player) {
    return this.WatcherList.get(player.getUniqueId());
  }

  public EndWatcher getSlave(Player player) {
    return this.WatchedList.get(player.getUniqueId());
  }

  public boolean openEnderChest(Player player, Player targetPlayer) {
    if (targetPlayer == null) {
      return false;
    }

    player.openInventory(targetPlayer.getEnderChest());
    Snd snd = new Snd().setSender(player).setTarget(targetPlayer);
    CMILib.getInstance()
        .getReflectionManager()
        .updateInventoryTitle(player, CMILC.getIM(ender.class, "title", snd));
    EndWatcher endWatcher = new EndWatcher(player, targetPlayer);
    this.WatcherList.put(player.getUniqueId(), endWatcher);
    this.WatchedList.put(targetPlayer.getUniqueId(), endWatcher);
    return this.isWatching(player);
  }

  public boolean updateTargetEnderChest(Player player) {
    if (!this.isWatching(player)) {
      return false;
    } else {
      EndWatcher endWatcher = this.getWatcher(player);
      Player targetPlayer = endWatcher.getTarget();
      if (!targetPlayer.isOnline() && endWatcher.getSaveTask() == null) {
        endWatcher.setSaveTask(
            CMIScheduler.runTaskLater(
                this.plugin,
                () -> {
                  this.plugin.save(targetPlayer);
                  endWatcher.setSaveTask(null);
                },
                200L));
        return true;
      } else {
        return true;
      }
    }
  }
}
