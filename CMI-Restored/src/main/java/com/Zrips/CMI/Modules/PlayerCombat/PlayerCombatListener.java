package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.events.CMIPvEEndEventAsync;
import com.Zrips.CMI.events.CMIPvEStartEventAsync;
import com.Zrips.CMI.events.CMIPvPEndEventAsync;
import com.Zrips.CMI.events.CMIPvPStartEventAsync;
import com.Zrips.CMI.utils.Util;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerCombatListener implements Listener {
  private CMI plugin;
  Set<UUID> ignoreFall = new HashSet<>();
  public static List<String> harmfulList =
      Arrays.asList(
          "blindness",
          "confusion",
          "harm",
          "hunger",
          "poison",
          "slow",
          "slow_digging",
          "weakness",
          "wither");
  Random rand = new Random();

  public PlayerCombatListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerQuitEvent.getPlayer());
    if (cmiUser.isInCombatWithPlayer()) {
      this.plugin.getPlayerCombatManager().removePlayerFromCombat(cmiUser);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerJoinEvent.getPlayer());
    if (cmiUser.isInCombatWithPlayer()) {
      this.plugin.getPlayerCombatManager().addPlayerIntoCombat(cmiUser);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerMove(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    Player player = playerCommandPreprocessEvent.getPlayer();
    if (this.plugin.getPlayerCombatManager().isPlayerBlockCommands()
        || this.plugin.getPlayerCombatManager().isMobBlockCommands()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser.isInCombatWithPlayer() || cmiUser.isInCombatWithMob()) {
        String string = playerCommandPreprocessEvent.getMessage();
        String text = string.startsWith("/") ? string.substring(1) : string;
        String textSecond = text.toLowerCase();
        if ((!this.plugin.getPlayerCombatManager().isAllowedPlayerCommand(textSecond)
                    && cmiUser.isInCombatWithPlayer()
                    && this.plugin.getPlayerCombatManager().isPlayerBlockCommands()
                || !this.plugin.getPlayerCombatManager().isAllowedMobCommand(textSecond)
                    && cmiUser.isInCombatWithMob()
                    && this.plugin.getPlayerCombatManager().isMobBlockCommands())
            && !PermissionsManager.CMIPerm.pvp_cmdBypass.hasPermission(player)) {
          playerCommandPreprocessEvent.setCancelled(true);
          playerCommandPreprocessEvent.setMessage("/");
          CMILC.info_combat_CantUseCommand.sendMessage(
              player, "[time]", CMITimeManager.to24hourShort(cmiUser.getLeftCombatTime() + 1000L));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvPEndEventAsync(CMIPvPEndEventAsync cmiPvPEndEventAsync) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvPStartEventAsync(CMIPvPStartEventAsync cmiPvPStartEventAsync) {
    if (this.plugin.getPlayerCombatManager().isPDisableFlight()
        && cmiPvPStartEventAsync.getPlayer().getAllowFlight()
        && !PermissionsManager.CMIPerm.pvp_PFlyBypass.hasPermission(
            cmiPvPStartEventAsync.getPlayer())) {
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            cmiPvPStartEventAsync.getPlayer().setFlying(false);
            cmiPvPStartEventAsync.getPlayer().setAllowFlight(false);
            CMIUser cmiUser =
                this.plugin.getPlayerManager().getUser(cmiPvPStartEventAsync.getPlayer());
            if (cmiUser != null) {
              cmiUser.getFlightCharge().setEnabled(false);
            }
          });
      if (this.plugin.getPlayerCombatManager().isPDisableFallDamage()
          && !cmiPvPStartEventAsync.getPlayer().isOnGround()) {
        this.ignoreFall.add(cmiPvPStartEventAsync.getPlayer().getUniqueId());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvPStartEventAsync(CMIPvEStartEventAsync cmiPvEStartEventAsync) {
    if (this.plugin.getPlayerCombatManager().isMDisableFlight()
        && cmiPvEStartEventAsync.getPlayer().getAllowFlight()
        && !PermissionsManager.CMIPerm.pvp_MFlyBypass.hasPermission(
            cmiPvEStartEventAsync.getPlayer())) {
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            cmiPvEStartEventAsync.getPlayer().setFlying(false);
            cmiPvEStartEventAsync.getPlayer().setAllowFlight(false);
            CMIUser cmiUser =
                this.plugin.getPlayerManager().getUser(cmiPvEStartEventAsync.getPlayer());
            if (cmiUser != null) {
              cmiUser.getFlightCharge().setEnabled(false);
            }
          });
      if (this.plugin.getPlayerCombatManager().isMDisableFallDamage()
          && !cmiPvEStartEventAsync.getPlayer().isOnGround()) {
        this.ignoreFall.add(cmiPvEStartEventAsync.getPlayer().getUniqueId());
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void CMIPvEEndEventAsync(CMIPvEEndEventAsync cmiPvEEndEventAsync) {}

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerDamageEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && entityDamageEvent.getCause() == DamageCause.FALL
        && entityDamageEvent.getEntity() instanceof Player) {
      if (this.ignoreFall.remove(entityDamageEvent.getEntity().getUniqueId())) {
        if (util.imortaldebug) {
          CMIMessages.consoleMessage("PlayerCombat playerDamageEvent->EntityDamageEvent");
        }

        entityDamageEvent.setDamage(0.0);
        entityDamageEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onSplashPotion(PotionSplashEvent potionSplashEvent) {
    if (!potionSplashEvent.isCancelled()) {
      boolean flag = false;

      for (PotionEffect potionEffect : potionSplashEvent.getPotion().getEffects()) {
        if (harmfulList.contains(potionEffect.getType().getName().toLowerCase())) {
          flag = true;
          break;
        }
      }

      if (flag) {
        for (LivingEntity livingEntity : potionSplashEvent.getAffectedEntities()) {
          if (livingEntity.getType() == EntityType.PLAYER) {
            CMIUser cmiUser = CMIUser.getUser((Player) livingEntity);
            if (cmiUser != null
                && cmiUser.getTeleportInvulnerability() > System.currentTimeMillis()) {
              if (util.imortaldebug) {
                CMIMessages.consoleMessage("PlayerCombat entityCombatEvent->PotionSplashEvent");
              }

              potionSplashEvent.setIntensity(livingEntity, 0.0);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void entityPreCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && !(entityDamageEvent.getEntity() instanceof Player)
        && !(entityDamageEvent.getFinalDamage() < 0.0)
        && entityDamageEvent instanceof EntityDamageByEntityEvent) {
      Entity entity = entityDamageEvent.getEntity();
      if (entity instanceof Monster
          || entity instanceof Animals
          || entity instanceof Ambient
          || entity instanceof Creature
          || entity instanceof Flying) {
        Player player = Util.getAttacker(entityDamageEvent);
        if (player != null) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser != null) {
            if (this.plugin.getPlayerCombatManager().isPVENoGodDamage()
                && (cmiUser.isGod() || cmiUser.isTgod())
                && !cmiUser.hasPermission(PermissionsManager.CMIPerm.pve_godBypass)) {
              if (this.plugin.getPlayerCombatManager().isPVENoGodDamageInform()) {
                CMILC.info_pve_noGodDamage.sendMessage(cmiUser);
              }

              entityDamageEvent.setCancelled(true);
            } else if (cmiUser.getTeleportInvulnerability() > System.currentTimeMillis()) {
              if (util.imortaldebug) {
                CMIMessages.consoleMessage("PlayerCombat entityCombatEvent->EntityDamageEvent");
              }

              entityDamageEvent.setCancelled(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void playerDamageByEntityCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && this.plugin.getPlayerCombatManager().isPvEIncludeVictim()
        && entityDamageEvent.getEntity() instanceof Player
        && !(entityDamageEvent.getFinalDamage() < 0.0)) {
      if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
        Entity entity = Util.getDamager(entityDamageEvent);
        if (entity instanceof Monster
            || entity instanceof Animals
            || entity instanceof Ambient
            || entity instanceof Creature
            || entity instanceof Flying) {
          Player player = (Player) entityDamageEvent.getEntity();
          CMIUser cmiUser = CMIUser.getUser(player);
          if (cmiUser != null) {
            cmiUser.setGotLastDamageFromPlayer(System.currentTimeMillis());
            if (this.plugin.getPlayerCombatManager().isPlayerShowBossBar()) {
              showBossBar(cmiUser, CMILC.info_combat_bossBarPvp.getLocale());
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void entityCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && !(entityDamageEvent.getEntity() instanceof Player)
        && !(entityDamageEvent.getFinalDamage() < 0.0)
        && entityDamageEvent instanceof EntityDamageByEntityEvent) {
      Entity entity = entityDamageEvent.getEntity();
      if (entity instanceof Monster
          || entity instanceof Animals
          || entity instanceof Ambient
          || entity instanceof Creature
          || entity instanceof Flying) {
        Player player = Util.getAttacker(entityDamageEvent);
        if (player != null) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser != null && cmiUser.isOnline()) {
            cmiUser.setDidLastDamageToEntity(System.currentTimeMillis());
            if (this.plugin.getPlayerCombatManager().isMobShowBossBar()) {
              showBossBar(cmiUser, CMILC.info_combat_bossBarPve.getLocale());
            }

            if (this.plugin.getPlayerCombatManager().isMobShowDamageNumbers()
                && cmiUser.getOptionState(PlayerOption.pveDamageNumbers)) {
              this.plugin
                  .getPlayerCombatManager()
                  .showDamageNumber(
                      player,
                      entityDamageEvent.getFinalDamage(),
                      entityDamageEvent
                          .getEntity()
                          .getLocation()
                          .clone()
                          .add(
                              this.rand.nextDouble() - 0.5,
                              1.5 + this.rand.nextDouble(),
                              this.rand.nextDouble() - 0.5),
                      false);
            }
          }
        }
      }
    }
  }

  private static void showBossBar(CMIUser cmiUser, String string) {
    if (!Version.isCurrentEqualOrLower(Version.v1_9_R1)
        || CMI.getInstance().getPlayerCombatManager().getCombatTimer() <= 0) {
      BossBarInfo bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "pvptimer");
      Double amount = (double) CMI.getInstance().getPlayerCombatManager().getCombatTimer();
      bossBarInfo.setKeepForTicks(3);

      try {
        bossBarInfo.setColor(BarColor.RED);
        bossBarInfo.setStyle(BarStyle.SOLID);
      } catch (Throwable throwable) {
      }

      bossBarInfo.setTitleOfBar(string);
      bossBarInfo.setSeconds(-amount.intValue());
      cmiUser.addBossBar(bossBarInfo);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerForceCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (entityDamageEvent.isCancelled()
        && this.plugin.getPlayerCombatManager().isSafeZoneDamage()
        && entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isInCombatWithPlayer()) {
          if (util.imortaldebug) {
            CMIMessages.consoleMessage("PlayerCombat PlayerForceCombatEvent->EntityDamageEvent");
          }

          entityDamageEvent.setCancelled(false);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void PlayerPreCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && entityDamageEvent.getEntity() instanceof Player
        && !(entityDamageEvent.getFinalDamage() < 0.0)) {
      Player player = (Player) entityDamageEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)) {
          if (cmiUser.getTeleportInvulnerability() > System.currentTimeMillis()) {
            entityDamageEvent.setCancelled(true);
            entityDamageEvent.setDamage(0.0);
          } else {
            cmiUser.setGotLastDamageAt(System.currentTimeMillis());
          }
        } else {
          Player playerSecond = Util.getAttacker(entityDamageEvent);
          if (playerSecond != null && !player.equals(playerSecond)) {
            CMIUser cMIUserSecond = this.plugin.getPlayerManager().getUser(playerSecond);
            if (cMIUserSecond != null) {
              if (this.plugin.getPlayerCombatManager().isPVPNoGodDamage()
                  && (cMIUserSecond.isGod() || cMIUserSecond.isTgod())
                  && !cMIUserSecond.hasPermission(PermissionsManager.CMIPerm.pvp_godBypass)) {
                if (this.plugin.getPlayerCombatManager().isPVPNoGodDamageInform()) {
                  CMILC.info_pvp_noGodDamage.sendMessage(cMIUserSecond);
                }

                entityDamageEvent.setCancelled(true);
                return;
              }

              if (cMIUserSecond.getTeleportInvulnerability() > System.currentTimeMillis()) {
                entityDamageEvent.setCancelled(true);
                if (util.imortaldebug) {
                  CMIMessages.consoleMessage("PlayerCombat PlayerCombatEvent->EntityDamageEvent");
                }

                return;
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled()
        && entityDamageEvent.getEntity() instanceof Player
        && !(entityDamageEvent.getFinalDamage() < 0.0)) {
      Player player = (Player) entityDamageEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        Entity entity = Util.getDamager(entityDamageEvent);
        if (!(entity instanceof Player)) {
          if (!(entity instanceof LivingEntity)
              && this.plugin.getPlayerCombatManager().isMobIncludeEnvironment()) {
            cmiUser.setGotLastDamageFromMob(System.currentTimeMillis());
            if (this.plugin.getPlayerCombatManager().isMobShowBossBar()) {
              showBossBar(cmiUser, CMILC.info_combat_bossBarPvp.getLocale());
            }
          }
        } else if (!(entity instanceof Player)) {
          cmiUser.setGotLastDamageFromMob(System.currentTimeMillis());
          if (this.plugin.getPlayerCombatManager().isMobShowBossBar()) {
            showBossBar(cmiUser, CMILC.info_combat_bossBarPvp.getLocale());
          }
        } else {
          Player playerSecond = (Player) entity;
          if (!player.equals(playerSecond)) {
            CMIUser cMIUserSecond = this.plugin.getPlayerManager().getUser(playerSecond);
            if (cMIUserSecond != null) {
              cMIUserSecond.setDidLastDamageToPlayer(System.currentTimeMillis());
              if (this.plugin.getPlayerCombatManager().isPlayerShowBossBar()) {
                showBossBar(cMIUserSecond, CMILC.info_combat_bossBarPvp.getLocale());
              }

              if (this.plugin.getPlayerCombatManager().isMobShowDamageNumbers()
                  && cMIUserSecond.getOptionState(PlayerOption.pvpDamageNumbers)) {
                this.plugin
                    .getPlayerCombatManager()
                    .showDamageNumber(
                        playerSecond,
                        entityDamageEvent.getFinalDamage(),
                        entityDamageEvent
                            .getEntity()
                            .getLocation()
                            .clone()
                            .add(
                                this.rand.nextDouble() - 0.5,
                                2.0 + this.rand.nextDouble(),
                                this.rand.nextDouble() - 0.5),
                        true);
              }
            }

            if (this.plugin.getPlayerCombatManager().isPvPIncludeVictim()) {
              cmiUser.setGotLastDamageFromPlayer(System.currentTimeMillis());
              if (this.plugin.getPlayerCombatManager().isPlayerShowBossBar()) {
                showBossBar(cmiUser, CMILC.info_combat_bossBarPvp.getLocale());
              }
            }

            if (!(player.getHealth() > entityDamageEvent.getFinalDamage())) {
              CMIUser cMIUserThird = CMIUser.getUser(playerSecond);
              if (cMIUserThird != null) {
                cMIUserThird.addKill(cmiUser.getUniqueId());
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(EntityDeathEvent entityDeathEvent) {
    if (entityDeathEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDeathEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        BossBarInfo bossBarInfo = cmiUser.getBossBar("pvptimer");
        if (bossBarInfo != null) {
          bossBarInfo.remove();
        }

        CMI.getInstance().getPlayerCombatManager().removePlayerFromCombat(cmiUser);
      }
    }
  }
}
