package com.Zrips.CMI.Modules.Totems;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Particl.CMIPEAnimationInterface;
import com.Zrips.CMI.Modules.Particl.CMIVisualEffect;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TotemManager {
  private HashMap<UUID, TotemBossBar> totem = new HashMap<>();
  CMI plugin;
  private boolean TotemRemoveFromInventory = false;
  private boolean TotemRequirePermission = false;
  private boolean TotemCooldownUse = false;
  private boolean TotemWarmupUse = false;
  private boolean TotemProtectFromVoid = false;
  private int TotemCooldownTime = -1;
  private int TotemWarmupTime = -1;
  private int TotemEffectRegeneration = -1;
  private int TotemEffectFireResistance = -1;
  private int TotemEffectAbsorbtion = -1;
  private List<String> blackListedWorlds = new ArrayList<>();
  private List<String> fullDisableInWorlds = new ArrayList<>();

  public TotemManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Totem.RemoveFromInventory",
        new String[] {
          "When this set to true, on players death totem will be used even if he is not holding it"
              + " in hand"
        });
    this.TotemRemoveFromInventory = configReader.get("Totem.RemoveFromInventory", false);
    configReader.addComment(
        "Totem.RequirePermission",
        new String[] {
          "Require permission node for totem to be taken from inventory",
          "RemoveFromInventory should be set to true and if this enabled player will need "
              + PermissionsManager.CMIPerm.totem_frominventory.getPermission()
        });
    this.TotemRequirePermission = configReader.get("Totem.RequirePermission", false);
    configReader.addComment(
        "Totem.Cooldown.Use",
        new String[] {"When this set to true player can use totem only every X second's"});
    this.TotemCooldownUse = configReader.get("Totem.Cooldown.Use", false);
    this.TotemCooldownTime = configReader.get("Totem.Cooldown.Time", 600);
    configReader.addComment(
        "Totem.Warmup.Use",
        new String[] {
          "When this set to true player can use totem to have X amount of second's, during which he"
              + " can die and be resurected",
          "Totem will be consumed durring activation and wont be returned even if resurection is"
              + " not used during warmup time"
        });
    this.TotemWarmupUse = configReader.get("Totem.Warmup.Use", false);
    this.TotemWarmupTime = configReader.get("Totem.Warmup.Time", 10);
    configReader.addComment(
        "Totem.ProtectFromVoid",
        new String[] {
          "If player falls into void while having totem, he will be teleported to respawn location"
              + " and totem gets consumed"
        });
    this.TotemProtectFromVoid = configReader.get("Totem.ProtectFromVoid", true);
    configReader.addComment(
        "Totem.Effects.Regeneration",
        new String[] {
          "Time in seconds effect needs to be applied to the player. Set it to 0 if you want to"
              + " disable it"
        });
    this.TotemEffectRegeneration = configReader.get("Totem.Effects.Regeneration", 45);
    this.TotemEffectFireResistance = configReader.get("Totem.Effects.FireResistance", 40);
    this.TotemEffectAbsorbtion = configReader.get("Totem.Effects.Absorbtion", 5);
    configReader.addComment(
        "Totem.BlackListedWorlds",
        new String[] {"Worlds where totem handling will be left for server default behavior"});
    this.blackListedWorlds =
        configReader.get("Totem.BlackListedWorlds", Arrays.asList("TestWorld", "DeathWorld"));
    CMIList.toLowerCase(this.getBlackListedWorlds());
    configReader.addComment(
        "Totem.FullDisableInWorlds",
        new String[] {"Worlds where totem usage is disabled entirely"});
    this.fullDisableInWorlds =
        configReader.get("Totem.FullDisableInWorlds", Arrays.asList("YouDied"));
    CMIList.toLowerCase(this.fullDisableInWorlds);
  }

  public boolean isTotemRemoveFromInventory() {
    return this.TotemRemoveFromInventory;
  }

  public boolean isTotemRequirePermission() {
    return this.TotemRequirePermission;
  }

  public boolean isTotemCooldownUse() {
    return this.TotemCooldownUse;
  }

  public boolean isTotemWarmupUse() {
    return this.TotemWarmupUse;
  }

  public int getTotemCooldownTime() {
    return this.TotemCooldownTime;
  }

  public int getTotemWarmupTime() {
    return this.TotemWarmupTime;
  }

  public boolean isTotemProtectFromVoid() {
    return this.TotemProtectFromVoid;
  }

  public void hideAllBars() {
    for (var entry : this.totem.entrySet()) {
      Player player = CMIUser.getOnlinePlayer((UUID) entry.getKey());
      TotemBossBar totemBossBar = this.getBossBar(player);
      if (totemBossBar == null) {
        return;
      }

      totemBossBar.getBar().setVisible(false);
    }
  }

  public boolean isOnCd(Player player) {
    TotemBossBar totemBossBar = this.totem.get(player.getUniqueId());
    return totemBossBar == null
        ? false
        : totemBossBar.getType() == TotemManager.BossBarType.Cooldown;
  }

  public boolean isOnWarmup(Player player) {
    TotemBossBar totemBossBar = this.totem.get(player.getUniqueId());
    return totemBossBar == null ? false : totemBossBar.getType() == TotemManager.BossBarType.Warmup;
  }

  public TotemBossBar getBossBar(Player player) {
    TotemBossBar totemBossBar = this.totem.get(player.getUniqueId());
    return totemBossBar != null ? totemBossBar : null;
  }

  @Deprecated
  public TotemBossBar getBossBar(Player player, TotemManager.BossBarType bossBarType) {
    return this.getBossBar(player.getUniqueId(), bossBarType);
  }

  public TotemBossBar getBossBar(UUID uuid, TotemManager.BossBarType bossBarType) {
    TotemBossBar totemBossBar = this.totem.get(uuid);
    return totemBossBar != null && totemBossBar.getType() == bossBarType ? totemBossBar : null;
  }

  public void addCooldownPlayer(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      cmiUser.setTotemCooldown(System.currentTimeMillis());
      if (this.isOnWarmup(player)) {
        this.removePlayer(player, TotemManager.BossBarType.Warmup);
      }

      BossBar bossBar = null;
      if (cmiUser.getOptionState(PlayerOption.totemBossBar)) {
        bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SEGMENTED_20, new BarFlag[0]);
        bossBar.setTitle(
            CMILC.info_totem_cooldown.getLocale(
                "[time]",
                CMITimeManager.to24hourShort(
                    this.getLeftCd(System.currentTimeMillis(), this.getTotemCooldownTime()))));
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        bossBar.setProgress(1.0);
      }

      this.totem.put(
          player.getUniqueId(), new TotemBossBar(bossBar, TotemManager.BossBarType.Cooldown));
      this.ShowTotemCooldown(cmiUser);
    }
  }

  public void addWarmupPlayer(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      cmiUser.setTotemWarmup(System.currentTimeMillis());
      BossBar bossBar =
          Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20, new BarFlag[0]);
      bossBar.setTitle(
          CMILC.info_totem_warmup.getLocale(
              "[time]",
              CMITimeManager.to24hourShort(
                  this.getLeftCd(System.currentTimeMillis(), this.getTotemWarmupTime()))));
      bossBar.setVisible(true);
      bossBar.addPlayer(player);
      bossBar.setProgress(1.0);
      if (this.isOnCd(player)) {
        this.removePlayer(player, TotemManager.BossBarType.Cooldown);
      }

      TotemBossBar totemBossBar = new TotemBossBar(bossBar, TotemManager.BossBarType.Warmup);
      this.totem.put(player.getUniqueId(), totemBossBar);
      this.ShowTotemWarmup(player);
      this.showWarmupEffect(player);
    }
  }

  public void showWarmupEffect(Player player) {
    TotemBossBar totemBossBar = this.totem.get(player.getUniqueId());
    if (totemBossBar == null) {
      BossBar bossBar =
          Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20, new BarFlag[0]);
      totemBossBar = new TotemBossBar(bossBar, TotemManager.BossBarType.Warmup);
      this.totem.put(player.getUniqueId(), totemBossBar);
    }

    CMIPEAnimationInterface cmipeAnimationInterface =
        CMI.getInstance()
            .getParticleManager()
            .getParticleAnimation(ParticleManager.CMIPresetAnimations.TotemHalo.getAnimString());
    if (cmipeAnimationInterface != null) {
      CMIVisualEffect cmiVisualEffect = new CMIVisualEffect(player, cmipeAnimationInterface);
      cmiVisualEffect.setTargetPlayer(player);
      cmiVisualEffect.setUntil(
          System.currentTimeMillis() + this.getTotemWarmupTime() * 1000 - 500L);
      cmiVisualEffect.show();
      totemBossBar.setEffect(cmiVisualEffect);
    }
  }

  public void stopWarmupEffect(Player player) {
    TotemBossBar totemBossBar = this.totem.get(player.getUniqueId());
    if (totemBossBar != null && totemBossBar.getEffect() != null) {
      totemBossBar.getEffect().stop();
    }
  }

  @Deprecated
  public boolean toggleBar(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    return cmiUser == null ? false : this.toggleBar(cmiUser, null);
  }

  public boolean toggleBar(CMIUser cmiUser, Boolean showBar) {
    TotemBossBar totemBossBar =
        this.getBossBar(cmiUser.getUniqueId(), TotemManager.BossBarType.Cooldown);
    if (totemBossBar != null && totemBossBar.getBar() != null) {
      totemBossBar.getBar().setVisible(cmiUser.getOptionState(PlayerOption.totemBossBar));
    }

    if (showBar == null) {
      cmiUser.setOptionState(
          PlayerOption.totemBossBar, !cmiUser.getOptionState(PlayerOption.totemBossBar));
    } else if (!showBar.equals(cmiUser.getOptionState(PlayerOption.totemBossBar))) {
      cmiUser.setOptionState(PlayerOption.totemBossBar, showBar);
    }

    if (cmiUser.getOptionState(PlayerOption.totemBossBar)) {
      this.ShowTotemCooldown(cmiUser);
    } else if (cmiUser.isOnline()) {
      if (totemBossBar != null && totemBossBar.getBar() != null) {
        totemBossBar.getBar().setVisible(false);
      }

      totemBossBar = this.getBossBar(cmiUser.getUniqueId(), TotemManager.BossBarType.Warmup);
      if (totemBossBar != null && totemBossBar.getBar() != null) {
        totemBossBar.getBar().setVisible(false);
      }
    }

    return true;
  }

  public void removePlayer(Player player, TotemManager.BossBarType bossBarType) {
    TotemBossBar totemBossBar = this.getBossBar(player, bossBarType);
    if (totemBossBar != null) {
      if (totemBossBar.getBar() != null) {
        totemBossBar.getBar().setVisible(false);
      }

      this.totem.remove(player.getUniqueId());
    }
  }

  public synchronized void ShowTotemWarmup(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      if (player.isOnline()) {
        if (this.isOnWarmup(player)) {
          Long warmupTime = cmiUser.getTotemWarmup();
          if (warmupTime != 0L) {
            TotemBossBar totemBossBar = this.getBossBar(player, TotemManager.BossBarType.Warmup);
            if (totemBossBar != null) {
              int value = this.getTotemWarmupTime();
              if (this.getLeftCd(warmupTime, value) < 900L) {
                this.removePlayer(player, TotemManager.BossBarType.Warmup);
              } else {
                double number = getLeftCdPercentage(warmupTime, value);
                if (totemBossBar.getBar() != null) {
                  totemBossBar.getBar().setVisible(false);
                  totemBossBar.getBar().setVisible(true);
                  totemBossBar.getBar().addPlayer(player);
                  totemBossBar
                      .getBar()
                      .setTitle(
                          CMILC.info_totem_warmup.getLocale(
                              "[time]",
                              CMITimeManager.to24hourShort(this.getLeftCd(warmupTime, value))));

                  try {
                    totemBossBar.getBar().setProgress(number);
                  } catch (Exception exception) {
                    return;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @Deprecated
  public synchronized void ShowTotemCooldown(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      this.ShowTotemCooldown(cmiUser);
    }
  }

  public synchronized void ShowTotemCooldown(CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      Player player = cmiUser.getPlayer();
      if (this.isOnCd(player)) {
        Long cooldownTime = cmiUser.getTotemCooldown();
        if (cooldownTime != 0L) {
          TotemBossBar totemBossBar =
              this.getBossBar(player.getUniqueId(), TotemManager.BossBarType.Cooldown);
          if (totemBossBar != null) {
            int value = this.getTotemCooldownTime();
            if (this.getLeftCd(cooldownTime, value) < 900L) {
              this.removePlayer(player, TotemManager.BossBarType.Cooldown);
            } else {
              double number = getLeftCdPercentage(cooldownTime, value);
              if (cmiUser.getOptionState(PlayerOption.totemBossBar)
                  && totemBossBar.getBar() == null) {
                totemBossBar.setBar(
                    Bukkit.createBossBar(
                        "", BarColor.GREEN, BarStyle.SEGMENTED_20, new BarFlag[0]));
              }

              if (totemBossBar.getBar() != null
                  && cmiUser.getOptionState(PlayerOption.totemBossBar)) {
                totemBossBar.getBar().setVisible(false);
                totemBossBar.getBar().setVisible(true);
                totemBossBar.getBar().addPlayer(player);
                totemBossBar
                    .getBar()
                    .setTitle(
                        CMILC.info_totem_cooldown.getLocale(
                            "[time]",
                            CMITimeManager.to24hourShort(this.getLeftCd(cooldownTime, value))));

                try {
                  totemBossBar.getBar().setProgress(number);
                } catch (Exception exception) {
                  return;
                }
              }
            }
          }
        }
      }
    }
  }

  public Long getLeftCd(Player player, int value) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      return 0L;
    }

    Long cooldownTime = cmiUser.getTotemCooldown();
    return this.getLeftCd(cooldownTime, value);
  }

  private Long getLeftCd(Long startTime, int value) {
    long longValue = value * 1000 + startTime - System.currentTimeMillis();
    if (longValue < 0L) {
      longValue = 0L;
    }

    return longValue;
  }

  private static double getLeftCdPercentage(Long startTime, int value) {
    double number =
        (value * 1000.0 + startTime.longValue() - System.currentTimeMillis()) / (value * 1000.0);
    if (number < 0.0) {
      number = 0.0;
    }

    if (number > 1.0) {
      number = 1.0;
    }

    return number;
  }

  public void checkAllForTotemCd() {
    for (var entry : new HashMap<>(this.totem).entrySet()) {
      Player player = CMIUser.getOnlinePlayer((UUID) entry.getKey());
      if (player != null && player.isOnline()) {
        if (((TotemBossBar) entry.getValue()).getType() == TotemManager.BossBarType.Cooldown) {
          this.ShowTotemCooldown(player);
        } else {
          this.ShowTotemWarmup(player);
        }
      }
    }
  }

  public int getRegenerationDuration() {
    return this.TotemEffectRegeneration;
  }

  public int getFireResistanceDuration() {
    return this.TotemEffectFireResistance;
  }

  public int getAbsorbtionDuration() {
    return this.TotemEffectAbsorbtion;
  }

  public List<String> getBlackListedWorlds() {
    return this.blackListedWorlds;
  }

  public boolean isBlackListedWorld(World world) {
    return world == null ? false : this.isBlackListedWorld(world.getName());
  }

  public boolean isBlackListedWorld(String string) {
    return string == null
        ? false
        : this.getBlackListedWorlds().contains(string.toLowerCase())
            || this.getFullDisableInWorlds().contains(string.toLowerCase());
  }

  public List<String> getFullDisableInWorlds() {
    return this.fullDisableInWorlds;
  }

  public boolean isFullDisableInWorlds(World world) {
    return world == null ? false : this.isFullDisableInWorlds(world.getName());
  }

  public boolean isFullDisableInWorlds(String string) {
    return string == null ? false : this.getFullDisableInWorlds().contains(string.toLowerCase());
  }

  public enum BossBarType {
    Cooldown,
    Warmup;
  }
}
