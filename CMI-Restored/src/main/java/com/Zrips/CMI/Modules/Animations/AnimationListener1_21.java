package com.Zrips.CMI.Modules.Animations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class AnimationListener1_21 implements Listener {
  private CMI plugin;

  public AnimationListener1_21(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void VehicleExitEvent(EntityDismountEvent entityDismountEvent) {
    process(entityDismountEvent.getEntity(), entityDismountEvent.getDismounted());
  }

  public static void process(Entity entity, Entity fakeSlime) {
    if (!(entity instanceof Player)) {
      if (entity instanceof Slime
          && (entity.hasMetadata("CMIFakeSlime")
              || entity.getCustomName() != null
                  && entity.getCustomName().equalsIgnoreCase("CMIFakeSlime"))) {
        CMI.getInstance().getTeleportHandler().shakePassengers(entity);
        entity.remove();
      }
    } else {
      Player player = (Player) entity;
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null && !cmiUser.isFakeAccount()) {
        if (Version.isCurrentEqualOrHigher(Version.v1_19_R3) && cmiUser.isSitting()) {
          CMI.getInstance().getAnimationManager().removePlayerFromChair(player);
        }

        if (CMI.getInstance().getAnimationManager().isRiding(player.getUniqueId())) {
          if (fakeSlime instanceof Slime
              && (fakeSlime.hasMetadata("CMIFakeSlime")
                  || fakeSlime.getCustomName() != null
                      && fakeSlime.getCustomName().equalsIgnoreCase("CMIFakeSlime"))) {
            fakeSlime.leaveVehicle();
            fakeSlime.remove();
          }

          CMI.getInstance().getAnimationManager().removeRiding(player.getUniqueId());
        }
      }
    }
  }
}
