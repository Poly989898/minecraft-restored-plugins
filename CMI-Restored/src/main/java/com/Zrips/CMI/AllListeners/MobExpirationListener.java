package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobExpirationListener implements Listener {
  private CMI plugin;
  static ConcurrentHashMap<UUID, CMITask> mobTaskers = new ConcurrentHashMap<>();

  public MobExpirationListener(CMI cmi) {
    this.plugin = cmi;
  }

  public static void addMob(Entity entity, double number) {
    mobTaskers.put(
        entity.getUniqueId(),
        CMIScheduler.runAtLocationLater(
            CMI.getInstance(),
            entity.getLocation(),
            () -> {
              mobTaskers.remove(entity.getUniqueId());
              if (!entity.isDead()) {
                entity.remove();
              }
            },
            CMINumber.clamp((int) (number * 20.0), 1, Integer.MAX_VALUE)));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void EntityDeathEvent(EntityDeathEvent entityDeathEvent) {
    if (!mobTaskers.isEmpty() && !(entityDeathEvent.getEntity() instanceof Player)) {
      CMITask cmiTask = mobTaskers.remove(entityDeathEvent.getEntity().getUniqueId());
      if (cmiTask != null) {
        cmiTask.cancel();
      }
    }
  }
}
