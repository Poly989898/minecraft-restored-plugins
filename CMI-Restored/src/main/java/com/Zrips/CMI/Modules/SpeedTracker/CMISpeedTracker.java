package com.Zrips.CMI.Modules.SpeedTracker;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CMISpeedTracker {
  private static final Set<UUID> tracked = ConcurrentHashMap.newKeySet();
  private static final Map<UUID, CMISpeedTracker.Sample> last = new ConcurrentHashMap<>();
  private static final Map<UUID, ConcurrentLinkedQueue<CMISpeedTracker.SpeedSample>> history =
      new ConcurrentHashMap<>();
  private static final ConcurrentLinkedQueue<CMISpeedTracker.Update> queue =
      new ConcurrentLinkedQueue<>();
  private static CMITask syncTask;
  private static CMITask asyncTask;

  private static synchronized void startIfNeeded() {
    if (syncTask == null && asyncTask == null) {
      syncTask =
          CMIScheduler.scheduleSyncRepeatingTask(CMI.getInstance(), CMISpeedTracker::tick, 1L, 1L);
      asyncTask =
          CMIScheduler.runTimerAsync(CMI.getInstance(), CMISpeedTracker::processQueue, 1L, 1L);
    }
  }

  private static synchronized void stopIfIdle() {
    if (tracked.isEmpty()) {
      if (syncTask != null) {
        syncTask.cancel();
        syncTask = null;
      }

      if (asyncTask != null) {
        asyncTask.cancel();
        asyncTask = null;
      }

      queue.clear();
      last.clear();
      history.clear();
    }
  }

  private static void tick() {
    long longValue = System.nanoTime();

    for (UUID uuid : tracked) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline()) {
        Location location = player.getLocation();
        queue.add(
            new CMISpeedTracker.Update(
                uuid, location.getX(), location.getY(), location.getZ(), longValue));
      }
    }
  }

  private static void processQueue() {
    CMISpeedTracker.Update update;
    while ((update = queue.poll()) != null) {
      CMISpeedTracker.Sample sample = last.get(update.id);
      if (sample != null) {
        double number = update.x - sample.x;
        double deltaY = update.y - sample.y;
        double deltaZ = update.z - sample.z;
        double distance = Math.sqrt(number * number + deltaY * deltaY + deltaZ * deltaZ);
        double deltaSeconds = (update.time - sample.time) / 1.0E9;
        if (deltaSeconds > 0.0 && distance < 20.0) {
          double speed = distance / deltaSeconds;
          var concurrentLinkedQueue =
              history.computeIfAbsent(update.id, uuid -> new ConcurrentLinkedQueue<>());
          concurrentLinkedQueue.add(new CMISpeedTracker.SpeedSample(speed, update.time));
          long longValue = update.time - 100000000L;

          CMISpeedTracker.SpeedSample speedSample;
          while ((speedSample = (CMISpeedTracker.SpeedSample) concurrentLinkedQueue.peek()) != null
              && speedSample.time < longValue) {
            concurrentLinkedQueue.poll();
          }
        }
      }

      CMISpeedTracker.Sample newSample = new CMISpeedTracker.Sample();
      newSample.x = update.x;
      newSample.y = update.y;
      newSample.z = update.z;
      newSample.time = update.time;
      last.put(update.id, newSample);
    }

    for (UUID trackedUuid : tracked) {
      sendSpeedMessage(trackedUuid);
    }
  }

  private static void sendSpeedMessage(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null && player.isOnline()) {
      double number = getSpeed(uuid);
      CMIActionBar.send(
          player, CMILC.Elytra_Speed.getLocale("[speed]", CMINumber.format2(number * 3.6)));
    }
  }

  public static void register(Player player) {
    register(player.getUniqueId());
  }

  public static void register(UUID uuid) {
    tracked.add(uuid);
    startIfNeeded();
  }

  public static void unregister(Player player) {
    unregister(player.getUniqueId());
  }

  public static void unregister(UUID uuid) {
    tracked.remove(uuid);
    last.remove(uuid);
    history.remove(uuid);
    stopIfIdle();
  }

  public static double getSpeed(Player player) {
    return getSpeed(player.getUniqueId());
  }

  public static double getSpeed(UUID uuid) {
    var concurrentLinkedQueue = history.get(uuid);
    if (concurrentLinkedQueue != null && !concurrentLinkedQueue.isEmpty()) {
      double number = 0.0;
      int value = 0;

      for (CMISpeedTracker.SpeedSample speedSample : concurrentLinkedQueue) {
        number += speedSample.speed;
        value++;
      }

      return value == 0 ? 0.0 : number / value;
    } else {
      return 0.0;
    }
  }

  private static class Sample {
    double x;
    double y;
    double z;
    long time;

    private Sample() {}
  }

  private static class SpeedSample {
    double speed;
    long time;

    SpeedSample(double number, long longValue) {
      this.speed = number;
      this.time = longValue;
    }
  }

  private static class Update {
    UUID id;
    double x;
    double y;
    double z;
    long time;

    Update(UUID uuid, double number, double yCoord, double zCoord, long longValue) {
      this.id = uuid;
      this.x = number;
      this.y = yCoord;
      this.z = zCoord;
      this.time = longValue;
    }
  }
}
