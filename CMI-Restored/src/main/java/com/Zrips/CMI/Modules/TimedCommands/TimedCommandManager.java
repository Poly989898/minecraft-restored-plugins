package com.Zrips.CMI.Modules.TimedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Player;

public class TimedCommandManager {
  private CMI plugin;
  private CMITask autoTimerBukkitId = null;
  private long nextIn = -1L;
  private boolean delay = false;
  public Set<CMIUser> timed = new HashSet<>();
  private Runnable autoTimer =
      new Runnable() {
        @Override
        public void run() {
          if (!TimedCommandManager.this.timed.isEmpty()) {
            TimedCommandManager.this.checkUsers();
          }
        }
      };

  public TimedCommandManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void addTimed(CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      this.timed.add(cmiUser);
      if (CMIModule.timedCommands.isEnabled()) {
        if (this.autoTimerBukkitId == null || this.delay) {
          this.delay = false;
          if (this.autoTimerBukkitId != null) {
            this.autoTimerBukkitId.cancel();
          }

          this.autoTimerBukkitId =
              CMIScheduler.scheduleSyncRepeatingTask(this.plugin, this.autoTimer, 0L, 20L);
        }
      }
    }
  }

  public void stop() {
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
      this.autoTimerBukkitId = null;
    }
  }

  private void checkUsers() {
    ArrayList<CMIUser> arrayList = new ArrayList();

    for (CMIUser cmiUser : this.timed) {
      if (!cmiUser.isOnline()) {
        arrayList.add(cmiUser);
      } else {
        boolean flag = this.checkTFly(cmiUser, false);
        boolean godActive = this.checkTGod(cmiUser, false);
        if ((godActive || cmiUser.getRawTempGodTime() <= 0L)
            && (flag || cmiUser.getRawTempFlyTime() <= 0L)) {
          arrayList.add(cmiUser);
        }
      }
    }

    this.timed.removeAll(arrayList);
    long longValue = (this.nextIn - System.currentTimeMillis()) / 1000L;
    if (longValue > 11L) {
      if (this.autoTimerBukkitId != null) {
        this.autoTimerBukkitId.cancel();
      }

      this.delay = true;
      this.autoTimerBukkitId =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin, this.autoTimer, (longValue - 10L) * 20L, 20L);
    }

    this.nextIn = -1L;
  }

  private void updateNextIn(Long nextTime) {
    if (this.nextIn == -1L || this.nextIn > nextTime && nextTime != -1L) {
      this.nextIn = nextTime;
    }
  }

  public boolean checkTFly(CMIUser cmiUser, boolean flag) {
    if (cmiUser.getRawTempFlyTime() == 0L) {
      return false;
    }

    this.updateNextIn(cmiUser.getRawTempFlyTime());
    if (cmiUser.getRawTempFlyTime() > 0L
        && cmiUser.getRawTempFlyTime() - 10000L < System.currentTimeMillis()
        && cmiUser.isOnline()) {
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            if (cmiUser.getRawTempFlyTime() != 0L && cmiUser.isOnline()) {
              CMIActionBar.send(
                  cmiUser.getPlayer(),
                  CMILC.getIM(
                      "tfly",
                      "willBeDisabled",
                      "[time]",
                      CMITimeManager.to24hourShort(
                          cmiUser.getRawTempFlyTime() - System.currentTimeMillis() + 1000L)));
            }
          });
    }

    if (cmiUser.getRawTempFlyTime() == -1L && !flag) {
      return true;
    }

    if (cmiUser.getRawTempFlyTime() > System.currentTimeMillis()) {
      return false;
    }

    Player player = cmiUser.getPlayer(false);
    if (player == null) {
      return true;
    }

    if (player.isFlying()) {
      this.plugin
          .getTeleportHandler()
          .teleportPlayer(player, player.getLocation(), true, CMITeleportType.TimedCommand);
    }

    player.setFlying(false);
    player.setAllowFlight(false);
    if (player.isOnline()) {
      player.sendMessage(CMILC.getIM("tfly", "disabled"));
    }

    cmiUser.setTfly(0L, !flag);
    cmiUser.clearCachedFlyModes();
    return true;
  }

  public boolean checkTGod(CMIUser cmiUser, boolean flag) {
    if (cmiUser.getTgod() == 0L) {
      return false;
    }

    this.updateNextIn(cmiUser.getTgod());
    if (cmiUser.getTgod() > 0L
        && cmiUser.getTgod() - 10000L < System.currentTimeMillis()
        && cmiUser.isOnline()) {
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            if (cmiUser.getTgod() != 0L && cmiUser.isOnline()) {
              CMIActionBar.send(
                  cmiUser.getPlayer(),
                  CMILC.getIM(
                      "tgod",
                      "willBeDisabled",
                      "[time]",
                      CMITimeManager.to24hourShort(
                          cmiUser.getTgod() - System.currentTimeMillis() + 1000L)));
            }
          });
    }

    if (cmiUser.getTgod() == -1L && !flag) {
      return true;
    }

    if (cmiUser.getTgod() > System.currentTimeMillis()) {
      return false;
    }

    Player player = cmiUser.getPlayer(false);
    if (player == null) {
      return false;
    }

    CMIGod.forceApplyNoDamageTick(player, 10);
    if (player.isOnline()) {
      player.sendMessage(CMILC.getIM("tgod", "disabled"));
    }

    cmiUser.setTgod(0L, !flag);
    return true;
  }
}
