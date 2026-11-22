package com.Zrips.CMI.Modules.Afk;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerCombat.PlayerCombatListener;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AfkListener1_9 implements Listener {
  private CMI plugin;

  public AfkListener1_9(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onLingeringSplashPotion(LingeringPotionSplashEvent lingeringPotionSplashEvent) {}

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onLingeringEffectApply(AreaEffectCloudApplyEvent areaEffectCloudApplyEvent) {
    if (this.plugin.getAfkManager().isPreventDamage()
        && !this.plugin.getAfkManager().getAfkPlayers().isEmpty()
        && !Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
      boolean flag = false;

      try {
        if (PlayerCombatListener.harmfulList.contains(
            areaEffectCloudApplyEvent.getEntity().getBasePotionType().toString().toLowerCase())) {
          flag = true;
        }
      } catch (Exception exception) {
        return;
      }

      if (flag) {
        for (LivingEntity livingEntity : areaEffectCloudApplyEvent.getAffectedEntities()) {
          if (livingEntity instanceof Player && CMIUser.getUser((Player) livingEntity).isAfk()) {
            areaEffectCloudApplyEvent.getAffectedEntities().remove(livingEntity);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onProjectileLaunchEvent(ProjectileLaunchEvent projectileLaunchEvent) {
    if (!projectileLaunchEvent.isCancelled()) {
      if (this.plugin.getAfkManager().isDisableShulkerTargeting()) {
        if (!this.plugin.getAfkManager().getAfkPlayers().isEmpty()) {
          ProjectileSource projectileSource = projectileLaunchEvent.getEntity().getShooter();
          if (projectileSource instanceof Shulker) {
            Shulker shulker = (Shulker) projectileSource;
            LivingEntity livingEntity = shulker.getTarget();
            if (livingEntity instanceof Player) {
              CMIUser cmiUser = CMIUser.getUser((Player) livingEntity);
              if (cmiUser != null && cmiUser.isAfk()) {
                if (projectileLaunchEvent.getEntity() instanceof ShulkerBullet) {
                  ShulkerBullet shulkerBullet = (ShulkerBullet) projectileLaunchEvent.getEntity();
                  shulkerBullet.setTarget(null);
                }

                shulker.setTarget(null);
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onEntityTargetLivingEntityEvent(
      EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
    if (!entityTargetLivingEntityEvent.isCancelled()) {
      if (this.plugin.getAfkManager().isDisableShulkerTargeting()) {
        if (!this.plugin.getAfkManager().getAfkPlayers().isEmpty()) {
          if (entityTargetLivingEntityEvent.getEntity().getType() == EntityType.SHULKER) {
            if (entityTargetLivingEntityEvent.getTarget() != null) {
              if (entityTargetLivingEntityEvent.getTarget().getType() == EntityType.PLAYER) {
                CMIUser cmiUser =
                    CMIUser.getUser((Player) entityTargetLivingEntityEvent.getTarget());
                if (cmiUser != null && cmiUser.isAfk()) {
                  entityTargetLivingEntityEvent.setCancelled(true);
                  entityTargetLivingEntityEvent.setTarget(null);
                }
              }
            }
          }
        }
      }
    }
  }
}
