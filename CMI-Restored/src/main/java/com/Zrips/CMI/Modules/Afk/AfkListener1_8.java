package com.Zrips.CMI.Modules.Afk;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayerCombat.PlayerCombatListener;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.potion.PotionEffect;

public class AfkListener1_8 implements Listener {
  private CMI plugin;

  public AfkListener1_8(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerInteractEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
    if (this.plugin.getAfkManager().isDisableOnInteract()) {
      CMIUser cmiUser =
          this.plugin.getPlayerManager().getUser(playerInteractAtEntityEvent.getPlayer());
      if (cmiUser != null && !AfkListener.isSameDirection(cmiUser)) {
        cmiUser.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        this.plugin.getAfkManager().updateUser(cmiUser);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onSplashPotion(PotionSplashEvent potionSplashEvent) {
    if (!potionSplashEvent.isCancelled() && this.plugin.getAfkManager().isPreventDamage()) {
      boolean flag = false;

      for (PotionEffect potionEffect : potionSplashEvent.getPotion().getEffects()) {
        if (PlayerCombatListener.harmfulList.contains(
            potionEffect.getType().getName().toLowerCase())) {
          flag = true;
          break;
        }
      }

      if (flag) {
        for (LivingEntity livingEntity : potionSplashEvent.getAffectedEntities()) {
          if (livingEntity.getType() == EntityType.PLAYER) {
            CMIUser cmiUser = CMIUser.getUser((Player) livingEntity);
            if (cmiUser == null) {
              return;
            }

            if (cmiUser.isAfk()) {
              potionSplashEvent.setIntensity(livingEntity, 0.0);
            }
          }
        }
      }
    }
  }
}
