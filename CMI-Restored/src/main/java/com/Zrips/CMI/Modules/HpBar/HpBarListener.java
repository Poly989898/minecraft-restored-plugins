package com.Zrips.CMI.Modules.HpBar;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.utils.Util;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HpBarListener implements Listener {
  private CMI plugin;

  public HpBarListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void EntityDamageEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
    if (!entityDamageByEntityEvent.isCancelled()) {
      if (this.plugin.getConfigManager().isBossBarHpBarEnabled()) {
        Entity entity = entityDamageByEntityEvent.getEntity();
        if (!this.plugin.getConfigManager().getHpBarBlackList().contains(entity.getType())) {
          if (entity instanceof Damageable) {
            Player player = Util.getAttacker(entityDamageByEntityEvent);
            double number = ((Damageable) entity).getHealth();
            double amount =
                Version.isCurrentEqualOrHigher(Version.v1_13_0)
                    ? ((Damageable) entity).getAbsorptionAmount()
                    : 0.0;
            CMIScheduler.runAtEntity(
                this.plugin,
                player,
                () ->
                    this.show(
                        player,
                        entity,
                        entityDamageByEntityEvent.getDamage(),
                        entityDamageByEntityEvent.getFinalDamage(),
                        number,
                        amount));
          }
        }
      }
    }
  }

  private void show(
      Player player,
      Entity entity,
      double number,
      double amountThird,
      double amountFourth,
      double amountFifth) {
    if (player != null) {
      if (!player.getUniqueId().equals(entity.getUniqueId())) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          if (!entity.getType().name().equals("ARMOR_STAND")) {
            if (PermissionsManager.CMIPerm.bossbar_hpbar.hasPermission(player, false, 60000L)) {
              Damageable damageable = (Damageable) entity;
              double amountSixth = CMIEntity.getMaxHealth(damageable);
              double amountSeventh = damageable.getHealth();
              double amountEighth =
                  Version.isCurrentEqualOrHigher(Version.v1_13_0)
                      ? damageable.getAbsorptionAmount()
                      : 0.0;
              double amountNinth = amountSeventh + amountEighth;
              double amountTenth = Math.max(0.0, amountFourth + amountFifth - amountNinth);
              double amount = amountSixth + amountFifth;
              CMIEntity cmiEntity = new CMIEntity(entity);
              String string = cmiEntity.getCustomName();
              CMIScheduler.runTaskAsynchronously(
                  this.plugin,
                  () -> {
                    BossBarInfo bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIHpBar");
                    bossBarInfo.setPercentage(amount, amountNinth);
                    bossBarInfo.setStyle(BarStyle.SEGMENTED_10);
                    double amountSecond = amountNinth * 100.0 / amount;
                    if (amountSecond > 50.0) {
                      bossBarInfo.setColor(BarColor.GREEN);
                    } else if (amountSecond > 30.0) {
                      bossBarInfo.setColor(BarColor.YELLOW);
                    } else {
                      bossBarInfo.setColor(BarColor.RED);
                    }

                    bossBarInfo.setKeepForTicks(50);
                    String text;
                    if (entity instanceof Player) {
                      String textSecond =
                          amountFifth > 0.0
                              ? CMILC.BossBar_absorption.getLocale("[value]", amountFifth)
                              : "";
                      String textThird =
                          amountEighth > 0.0
                              ? CMILC.BossBar_absorption.getLocale("[value]", amountEighth)
                              : "";
                      text =
                          CMILC.BossBar_playerHpBar.getLocale(
                              "[victim]",
                              string,
                              "[max]",
                              CMINumber.format2Decimals(amount),
                              "[current]",
                              CMINumber.format2Decimals(amountNinth),
                              "[damage]",
                              CMINumber.format2Decimals(amountTenth),
                              "[maxAbsorption]",
                              textSecond,
                              "[currentAbsorption]",
                              textThird);
                      text =
                          this.plugin
                              .getPlaceholderAPIManager()
                              .updatePlaceHolders((Player) entity, text);
                    } else {
                      text =
                          CMILC.BossBar_hpBar.getLocale(
                              "[victim]",
                              string,
                              "[max]",
                              CMINumber.format2Decimals(amount),
                              "[current]",
                              CMINumber.format2Decimals(amountNinth),
                              "[damage]",
                              CMINumber.format2Decimals(amountTenth));
                    }

                    bossBarInfo.setTitleOfBar(text);
                    CMIScheduler.runTask(this.plugin, () -> cmiUser.addBossBar(bossBarInfo));
                  });
            }
          }
        }
      }
    }
  }
}
