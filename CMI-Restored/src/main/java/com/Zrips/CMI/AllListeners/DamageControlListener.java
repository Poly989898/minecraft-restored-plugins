package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.DamageControl;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.util;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class DamageControlListener implements Listener {
  private CMI plugin;

  public DamageControlListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void EntityDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.getEntity() instanceof Player) {
      if (!entityDamageEvent.isCancelled()) {
        Player player = (Player) entityDamageEvent.getEntity();
        if (entityDamageEvent.getCause() != null) {
          var arrayList =
              this.plugin.getConfigManager().DamageControlMap.get(entityDamageEvent.getCause());
          if (arrayList != null && !arrayList.isEmpty()) {
            double number = -999.0;

            for (DamageControl damageControl : arrayList) {
              if (PermissionsManager.CMIPerm.damagecontrol_$1.hasPermission(
                  player, damageControl.getNode())) {
                number = damageControl.getMult();
              }
            }

            if (number != -999.0) {
              if (number >= 0.0) {
                double newDamage = entityDamageEvent.getFinalDamage() * number;
                if (util.imortaldebug) {
                  CMIMessages.consoleMessage(
                      "DamageControl EntityDamageEvent->EntityDamageEvent " + newDamage);
                }

                if (Version.isPaperBranch() && Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
                  entityDamageEvent.setDamage(DamageModifier.BASE, newDamage);
                } else {
                  entityDamageEvent.setDamage(newDamage);
                }
              } else {
                EntityRegainHealthEvent entityRegainHealthEvent =
                    new EntityRegainHealthEvent(
                        entityDamageEvent.getEntity(),
                        -(entityDamageEvent.getFinalDamage() * number),
                        RegainReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(entityRegainHealthEvent);
                if (!entityRegainHealthEvent.isCancelled()) {
                  if (!entityDamageEvent.isCancelled()) {
                    double newHealth = player.getHealth() + entityRegainHealthEvent.getAmount();
                    if (newHealth >= player.getMaxHealth()) {
                      player.setHealth(player.getMaxHealth());
                    } else {
                      player.setHealth(newHealth);
                    }

                    if (util.imortaldebug) {
                      CMIMessages.consoleMessage(
                          "DamageControl2 EntityDamageEvent->EntityDamageEvent 0");
                    }

                    entityDamageEvent.setDamage(0.0);
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
