package com.Zrips.CMI.Modules.FlightCharge;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.SpawnerCharge.PlayerCharge;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.flightcharge;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

public class FlightChargeManager {
  CMI plugin;
  private boolean FlightChargeEnabledByDefault = true;
  private boolean FlightChargeAutoSwitch = true;
  private boolean bossbar = true;
  private double ExpRechargeCost = 0.0;
  private double MoneyRechargeCost = 0.0;
  private int MaxChargeLevel = 0;
  private int DeductOnFallMulti = 0;
  private double DeductOnIdling = 0.0;
  private double DeductOnlyForTime = 0.0;
  private boolean DamageOnFall = true;
  private boolean DamageOnToggle = true;
  private boolean KillOnFall = true;
  private CMIChatColor GlowColor = null;
  private double autoRechargeFrom = 0.0;
  private double autoRechargeAmount = 0.0;
  private ConcurrentHashMap<UUID, FlightCharge> flightCharges = new ConcurrentHashMap<>();
  public static final String flightChargeBossBar = "CMIFlightChargeBossBar";

  public FlightChargeManager(CMI cmi) {
    this.plugin = cmi;
  }

  public FlightCharge getFlightCharge(UUID uuid) {
    return this.flightCharges.computeIfAbsent(uuid, playerUuid -> new FlightCharge());
  }

  @Deprecated
  public PlayerCharge getPCharge(UUID uuid) {
    return this.getPCharge(uuid, true);
  }

  @Deprecated
  public PlayerCharge getPCharge(UUID uuid, boolean flag) {
    return this.plugin.getSpawnerChargesManager().getPCharge(CMIUser.getUser(uuid), flag);
  }

  public void load() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "FlightCharge.ShowBossBar",
        new String[] {
          "By default boss bar message will be shown with remaining charges. If disabled then"
              + " bossbar will only appear when flight charge was modified but not when its"
              + " depleating while flying"
        });
    this.bossbar = configReader.get("FlightCharge.ShowBossBar", true);
    configReader.addComment(
        "FlightCharge.EnabledByDefault",
        new String[] {
          "When set to true, each time player gets flight charge or relogs, his fly mode will be"
              + " toggled on",
          "If set to false, then players will have to manually turn on flight with /cmi flyc"
        });
    this.FlightChargeEnabledByDefault = configReader.get("FlightCharge.EnabledByDefault", true);
    configReader.addComment(
        "FlightCharge.AutoSwitch",
        new String[] {
          "When set to true, in event of player changing his game mode from survival/adventure to"
              + " creative/spectator his flight charge  mode will get disabled",
          "Same applies when changing game mode from creative/spectator to survival/adventure"
        });
    this.FlightChargeAutoSwitch = configReader.get("FlightCharge.AutoSwitch", false);
    configReader.addComment(
        "FlightCharge.ExpRechargeCost",
        new String[] {
          "How much it costs for one recharge point in exp points. Value can be in decimals, like"
              + " 0.2 but it cant be equal or lower than 0",
          "Set to 0 to disable this type of recharge"
        });
    this.ExpRechargeCost = configReader.get("FlightCharge.ExpRechargeCost", 1.0);
    configReader.addComment(
        "FlightCharge.MoneyRechargeCost",
        new String[] {
          "How much it costs for one recharge point. Value can be in decimals, like 0.2 but it cant"
              + " be equal or lower than 0",
          "Set to 0 to disable this type of recharge"
        });
    this.MoneyRechargeCost = configReader.get("FlightCharge.MoneyRechargeCost", 1.0);
    configReader.addComment(
        "FlightCharge.MaxChargeLevel",
        new String[] {
          "Defines maximum amount of charge player can have",
          "One charge is one traveled block while flying",
          "if player doesn't move, then one charge for each second while hovering"
        });
    this.MaxChargeLevel = configReader.get("FlightCharge.MaxChargeLevel", 1000);
    configReader.addComment(
        "FlightCharge.DeductOnIdling",
        new String[] {
          "Defines multiplier when player doesn't move but is hovering. For each second player"
              + " hovers.",
          "Set to 0 to disable"
        });
    this.DeductOnIdling = configReader.get("FlightCharge.DeductOnIdling", 1.0);
    configReader.addComment(
        "FlightCharge.DeductOnlyForTime",
        new String[] {
          "If this is set above 0, then player will loose defined amount of charges each second"
              + " they are flying instead of traveled blocks",
          "Set to 0 to disable, which will deduct charges for traveled blocks"
        });
    this.DeductOnlyForTime = configReader.get("FlightCharge.DeductOnlyForTime", 0.0);
    configReader.addComment(
        "FlightCharge.AutoRecharge.From",
        new String[] {
          "Value in percentage when we should automatically recharge players flight charges if"
              + " player enabled autorecharge with /autorecharge command"
        });
    this.autoRechargeFrom = configReader.get("FlightCharge.AutoRecharge.From", 10.0);
    this.autoRechargeFrom =
        this.autoRechargeFrom < 0.0
            ? 0.0
            : (this.autoRechargeFrom > 100.0 ? 100.0 : this.autoRechargeFrom);
    configReader.addComment(
        "FlightCharge.AutoRecharge.Amount",
        new String[] {
          "Value in percentage of charges we need to recharge",
          "25 will result into 25% of max allowed charges being recharged each time auto recharge"
              + " is triggered"
        });
    this.autoRechargeAmount = configReader.get("FlightCharge.AutoRecharge.Amount", 25.0);
    this.autoRechargeAmount =
        this.autoRechargeAmount < 0.0
            ? 0.0
            : (this.autoRechargeAmount > 100.0 ? 100.0 : this.autoRechargeAmount);
    configReader.addComment(
        "FlightCharge.DeductOnFallMulti",
        new String[] {
          "Defines multiplier when player falls down of charge to be taken",
          "This only effects when player falls from above 3 blocks of hight",
          "In example if player falls from 10 blocks height, then 7 * 2 = 14 charges will be taken",
          "This is to prevent avoiding no penalty from jumping from cliffs",
          "Set to 0 if you want to disable it"
        });
    this.DeductOnFallMulti = configReader.get("FlightCharge.DeductOnFallMulti", 2);
    configReader.addComment(
        "FlightCharge.DamageOnFall",
        new String[] {
          "Defines if you want to damage player when he falls down from higher than 3 blocks"
              + " height",
          "This will not kill player even if he would drop from 200 block height, but will leave"
              + " him with 1 hp",
          "This will only effect players who jumped down and not those who disabled fly mode in mid"
              + " air"
        });
    this.DamageOnFall = configReader.get("FlightCharge.DamageOnFall", true);
    configReader.addComment(
        "FlightCharge.DamageOnToggle",
        new String[] {
          "DamageOnFall should be enabled for this to work",
          "This will define if you want to damage player when he deactivates fly mode in mid air"
        });
    this.DamageOnToggle = configReader.get("FlightCharge.DamageOnToggle", false);
    this.DamageOnToggle = !this.DamageOnFall ? false : this.DamageOnToggle;
    configReader.addComment(
        "FlightCharge.KillOnFall",
        new String[] {
          "DamageOnFall should be enabled for this to work",
          "This will define if you want to kill player if fall damage if higher than his health"
              + " amount"
        });
    this.KillOnFall = configReader.get("FlightCharge.KillOnFall", false);
    this.KillOnFall = !this.DamageOnFall ? false : this.KillOnFall;
    configReader.addComment(
        "FlightCharge.GlowColor",
        new String[] {
          "When color name is defined then at moment player starts flying with flight charges, he"
              + " will start glowing",
          "Set this to 'none' if you want to disable it"
        });
    this.GlowColor = CMIChatColor.getColor(configReader.get("FlightCharge.GlowColor", "none"));
  }

  public void process(Player player, Integer integer) {
    traveledDistance traveledDistance = FlightListener.distanceMap.get(player.getUniqueId());
    if (traveledDistance == null) {
      traveledDistance = new traveledDistance(this.plugin.getPlayerManager().getUser(player));
      FlightListener.distanceMap.put(player.getUniqueId(), traveledDistance);
    }

    this.process(traveledDistance, integer);
  }

  public void process(traveledDistance traveledDistance, Integer integer) {
    CMIUser cmiUser = traveledDistance.getUser();
    if (cmiUser.getFlightCharge().getCharge() <= 0.0) {
      traveledDistance.updateLocation();
    } else if (cmiUser.getTfly() <= 0L && cmiUser.getTfly() != -1L) {
      if (cmiUser.isOnline() && cmiUser.getPlayer().getVehicle() != null) {
        traveledDistance.updateLocation();
      } else if (!cmiUser.getPlayer().getGameMode().equals(GameMode.SURVIVAL)
          && !cmiUser.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
        cmiUser.getFlightCharge().setEnabled(false);
        this.updateBossBar(traveledDistance);
      } else {
        boolean flag = cmiUser.isOnline() && cmiUser.getPlayer().isFlying();
        double number = traveledDistance.getDistanceTraveled();
        if (integer != null) {
          number = integer.doubleValue();
        }

        if (flag) {
          if (number == 0.0) {
            number = traveledDistance.sinceLastUpdate() / 1000L * this.DeductOnIdling;
          } else if (this.DeductOnlyForTime > 0.0) {
            number = traveledDistance.sinceLastUpdate() / 1000L * this.DeductOnlyForTime;
          }

          if (number == 0.0 && this.DeductOnIdling > 0.0
              || number == 0.0 && this.DeductOnlyForTime > 0.0) {
            number = 1.0;
          }
        }

        if (number > 0.0) {
          cmiUser.getFlightCharge().takeCharge(number);
          this.autorecharge(cmiUser);
          if (cmiUser.isOnline()) {
            Player player = cmiUser.getPlayer();
            if (cmiUser.getFlightCharge().getCharge() <= 0.0) {
              player.setFlying(false);
              player.setAllowFlight(false);
              cmiUser.getFlightCharge().setEnabled(false);
              var completableFuture =
                  this.plugin.getTeleportHandler().getDistanceToDrop(player.getLocation());
              completableFuture.thenAccept(
                  object -> {
                    if (!(object <= 3.0)) {
                      this.plugin
                          .getTeleportHandler()
                          .teleportPlayer(
                              player, player.getLocation(), CMITeleportType.FlightCharge);
                    }
                  });
              cmiUser.getFlightCharge().setCharge(0.0);
            }
          }

          if (this.bossbar) {
            this.updateBossBar(traveledDistance);
          }
        }

        traveledDistance.updateLocation();
      }
    }
  }

  public void autorecharge(CMIUser cmiUser) {
    if (cmiUser.getFlightCharge().isAutoRecharge()
        && cmiUser.getFlightCharge().getCharge() * 100.0 / cmiUser.getFlightCharge().getMax()
            <= this.plugin.getFlightChargeManager().getAutoRechargeFrom()) {
      int value =
          (int)
              (cmiUser.getFlightCharge().getMax()
                  * (this.plugin.getFlightChargeManager().getAutoRechargeAmount() / 100.0));
      if (!cmiUser.getFlightCharge().isMoneyAutoRecharge()
          || !(this.plugin.getFlightChargeManager().getMoneyRechargeCost() > 0.0)
          || !flightcharge.moneyRecharge(null, cmiUser, value, false)) {
        if (!cmiUser.getFlightCharge().isExpAutoRecharge()
            || !(this.plugin.getFlightChargeManager().getExpRechargeCost() > 0.0)
            || !flightcharge.expRecharge(null, cmiUser, value, false)) {
          ;
        }
      }
    }
  }

  public void updateBossBar(CMIUser cmiUser) {
    traveledDistance traveledDistance = FlightListener.distanceMap.get(cmiUser.getUniqueId());
    if (traveledDistance == null) {
      traveledDistance = new traveledDistance(cmiUser);
      FlightListener.distanceMap.put(cmiUser.getUniqueId(), traveledDistance);
    }

    this.updateBossBar(traveledDistance);
  }

  private void updateBossBar(traveledDistance traveledDistance) {
    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      CMIUser cmiUser = traveledDistance.getUser();
      Double chargeValue = cmiUser.getFlightCharge().getCharge();
      if (chargeValue != null && chargeValue > 0.0 && cmiUser.getFlightCharge().isEnabled()) {
        BossBarInfo bossBarInfo = cmiUser.getBossBar("CMIFlightChargeBossBar");
        if (bossBarInfo == null) {
          bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIFlightChargeBossBar");
          bossBarInfo.setPercentage(cmiUser.getFlightCharge().getMax(), chargeValue);
          bossBarInfo.setColor(
              bossBarInfo.getPercentage() > 0.5
                  ? BarColor.GREEN
                  : (bossBarInfo.getPercentage() > 0.2 ? BarColor.YELLOW : BarColor.RED));
        }

        bossBarInfo.setPercentage(cmiUser.getFlightCharge().getMax(), chargeValue);
        if (bossBarInfo.getBar() != null) {
          bossBarInfo
              .getBar()
              .setColor(
                  bossBarInfo.getPercentage() > 0.5
                      ? BarColor.GREEN
                      : (bossBarInfo.getPercentage() > 0.2 ? BarColor.YELLOW : BarColor.RED));
        }

        String string = "";
        if (cmiUser.getFlightCharge().getLastChange() != 0.0) {
          string =
              CMILC.getIM(
                  flightcharge.class,
                  "changeAmount",
                  "[amount]",
                  cmiUser.getFlightCharge().getLastChange() > 0.0
                      ? "&2+" + (int) cmiUser.getFlightCharge().getLastChange()
                      : "&c" + (int) cmiUser.getFlightCharge().getLastChange());
        }

        bossBarInfo.setTitleOfBar(
            CMILC.getIM(
                flightcharge.class,
                "bossbar",
                "[charge]",
                chargeValue.intValue(),
                "[max]",
                cmiUser.getFlightCharge().getMax(),
                "[changeAmount]",
                string));
        bossBarInfo.setKeepForTicks(60);
        cmiUser.addBossBar(bossBarInfo);
      } else {
        BossBarInfo existingBossBar = cmiUser.getBossBar("CMIFlightChargeBossBar");
        if (existingBossBar != null) {
          existingBossBar.remove();
        }
      }
    }
  }

  public double getExpRechargeCost() {
    return this.ExpRechargeCost;
  }

  public void setExpRechargeCost(double number) {
    this.ExpRechargeCost = number;
  }

  public double getMoneyRechargeCost() {
    return this.MoneyRechargeCost;
  }

  public void setMoneyRechargeCost(double number) {
    this.MoneyRechargeCost = number;
  }

  public int getMaxChargeLevel() {
    return this.MaxChargeLevel;
  }

  public void setMaxChargeLevel(int value) {
    this.MaxChargeLevel = value;
  }

  public int getDeductOnFallMulti() {
    return this.DeductOnFallMulti;
  }

  public void setDeductOnFallMulti(int value) {
    this.DeductOnFallMulti = value;
  }

  public boolean isDamageOnFall() {
    return this.DamageOnFall;
  }

  public void setDamageOnFall(boolean flag) {
    this.DamageOnFall = flag;
  }

  public boolean isDamageOnToggle() {
    return this.DamageOnToggle;
  }

  public void setDamageOnToggle(boolean flag) {
    this.DamageOnToggle = flag;
  }

  public boolean isKillOnFall() {
    return this.KillOnFall;
  }

  public void setKillOnFall(boolean flag) {
    this.KillOnFall = flag;
  }

  public double getDeductOnIdling() {
    return this.DeductOnIdling;
  }

  public void setDeductOnIdling(int value) {
    this.DeductOnIdling = value;
  }

  public boolean isFlightChargeEnabledByDefault() {
    return this.FlightChargeEnabledByDefault;
  }

  public void setFlightChargeEnabledByDefault(boolean flag) {
    this.FlightChargeEnabledByDefault = flag;
  }

  public boolean isFlightChargeAutoSwitch() {
    return this.FlightChargeAutoSwitch;
  }

  public CMIChatColor getGlowColor() {
    return this.GlowColor;
  }

  public void setGlowColor(CMIChatColor cmiChatColor) {
    this.GlowColor = cmiChatColor;
  }

  public double getAutoRechargeFrom() {
    return this.autoRechargeFrom;
  }

  public double getAutoRechargeAmount() {
    return this.autoRechargeAmount;
  }
}
