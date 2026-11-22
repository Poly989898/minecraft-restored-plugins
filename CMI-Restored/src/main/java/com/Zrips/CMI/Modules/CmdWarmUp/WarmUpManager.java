package com.Zrips.CMI.Modules.CmdWarmUp;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Particl.CMIPEAnimationInterface;
import com.Zrips.CMI.Modules.Particl.CMIVisualEffect;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarmUpManager {
  public HashMap<String, CmdWarmUp> warmups = new HashMap<>();
  private HashMap<UUID, WarmUpInfo> counter = new HashMap<>();
  CMI plugin;
  private static final String bossBarName = "CMICmdWarmupBar";
  boolean InformOnNoMove;
  boolean counterBarInfo;
  boolean showBossBarInfo = false;
  BossBarInfo bossBarInfo = null;

  public WarmUpManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean isOnWarmUp(Player player) {
    return this.isOnWarmUp(player.getUniqueId());
  }

  public boolean isOnWarmUp(UUID uuid) {
    return this.counter.containsKey(uuid);
  }

  public void addWU(String string, CmdWarmUp cmdWarmUp) {
    this.warmups.put(string.toLowerCase(), cmdWarmUp);
  }

  private CmdWarmUp getTime(String string) {
    string = string.toLowerCase();
    CommandAlias commandAlias = this.plugin.getAliasManager().getAliasForCommand(string);
    if (commandAlias != null && commandAlias.getType() != CommandAliasType.custom) {
      return null;
    }

    string = string.endsWith(" ") ? string.substring(0, string.length() - 1) : string;

    for (var entry : this.warmups.entrySet()) {
      if (string.equals(entry.getKey())) {
        return (CmdWarmUp) entry.getValue();
      }
    }

    for (var warmupEntry : this.warmups.entrySet()) {
      if (string.startsWith((String) warmupEntry.getKey() + " ")) {
        return (CmdWarmUp) warmupEntry.getValue();
      }
    }

    for (var warmupEntry : this.warmups.entrySet()) {
      if (string.startsWith((String) warmupEntry.getKey())) {
        String suffix = string.substring(((String) warmupEntry.getKey()).length());
        if (suffix.isEmpty()
            || suffix.startsWith(" ")
            || ((String) warmupEntry.getKey()).endsWith(" ")) {
          return (CmdWarmUp) warmupEntry.getValue();
        }
      }
    }

    return null;
  }

  public boolean canMove(Player player) {
    return this.canMove(player.getUniqueId(), player.getLocation());
  }

  public boolean canMove(UUID uuid) {
    return this.canMove(uuid, null);
  }

  public boolean canMove(UUID uuid, Location location) {
    WarmUpInfo warmUpInfo = this.counter.get(uuid);
    if (warmUpInfo == null) {
      return true;
    }

    String string = warmUpInfo.getCmd();
    if (string != null) {
      string = string.toLowerCase();

      for (var entry : this.warmups.entrySet()) {
        if (string.startsWith((String) entry.getKey())) {
          if (warmUpInfo.getLoc() != null
              && warmUpInfo.getWarmup().getMaxMoveDistance() > 0.0
              && location != null) {
            double number = CMILocation.getDistance(location, warmUpInfo.getLoc());
            if (number <= warmUpInfo.getWarmup().getMaxMoveDistance()) {
              return true;
            }
          }

          return ((CmdWarmUp) entry.getValue()).isMove();
        }
      }
    }

    return warmUpInfo.getLoc() != null ? warmUpInfo.isMove() : true;
  }

  public boolean canMoveByCmd(String string) {
    string = string.toLowerCase();

    for (var entry : this.warmups.entrySet()) {
      if (string.startsWith((String) entry.getKey())) {
        return ((CmdWarmUp) entry.getValue()).isMove();
      }
    }

    return true;
  }

  @Deprecated
  public boolean startTeleportUsage(Player player, Location location) {
    return this.startTeleportUsage(TeleportManager.TpAction.tp, player, location);
  }

  public boolean startTeleportUsage(
      TeleportManager.TpAction tpAction, Player player, Location location) {
    if (CMIModule.warmup.isEnabled()
        && this.plugin.getTeleportManager().getTpaWarmupTime() != 0
        && (tpAction != TeleportManager.TpAction.tpa
            || !PermissionsManager.CMIPerm.command_tpa_warmupbypass.hasSetPermission(player))
        && (tpAction != TeleportManager.TpAction.tpahere
            || !PermissionsManager.CMIPerm.command_tpahere_warmupbypass.hasSetPermission(player))) {
      boolean flag = this.plugin.getTeleportManager().isTpaMove();
      int value = this.plugin.getTeleportManager().getTpaWarmupTime();
      CmdWarmUp cmdWarmUp = new CmdWarmUp("DUMMY", (long) value, flag);
      WarmUpInfo warmUpInfo = new WarmUpInfo(location, cmdWarmUp);
      this.counter.put(player.getUniqueId(), warmUpInfo);
      warmUpInfo.setLeftCount(value);
      this.count(player.getUniqueId());
      CMISoundPreset.CommandWarmup.play(player);
      if (this.showBossBarInfo) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        BossBarInfo barInfo = cmiUser.getBossBar("CMICmdWarmupBar");
        if (barInfo == null) {
          barInfo = this.bossBarInfo.clone(cmiUser.getPlayer());
        }

        warmUpInfo.setBar(barInfo);
        barInfo.setSeconds(-value);
        if (warmUpInfo.isMove()) {
          barInfo.setTitleOfBar(CMILC.info_WarmUp_Boss_WaitFor.getLocale());
        } else {
          barInfo.setTitleOfBar(CMILC.info_WarmUp_Boss_DontMove.getLocale());
        }

        cmiUser.addBossBar(barInfo);
      }

      if (!ParticleManager.CMIPresetAnimations.tpaWarmup.getAnimString().isEmpty()) {
        CMIVisualEffect cmiVisualEffect =
            new CMIVisualEffect(player, ParticleManager.CMIPresetAnimations.tpaWarmup);
        cmiVisualEffect.setTargetPlayer(player);
        cmiVisualEffect.setUntil(System.currentTimeMillis() + value * 1000 - 500L);
        warmUpInfo.setEffectAnimation(cmiVisualEffect);
        cmiVisualEffect.show();
      }

      return true;
    } else {
      this.plugin
          .getTeleportHandler()
          .teleportPlayer(player, location, CMITeleportType.WarmUp)
          .thenAccept(
              cmiTeleportFeedback -> {
                if (!cmiTeleportFeedback.isSuccess()) {
                  cmiTeleportFeedback.showFeedbackMessage(player, player);
                }
              });
      return true;
    }
  }

  public boolean isWarmupCommand(CommandSender commandSender, String string) {
    if (!CMIModule.warmup.isEnabled()) {
      return false;
    }

    if (string.startsWith("/ ")) {
      string = "/" + string.substring(2);
    }

    if (string.startsWith("/:")) {
      string = string.substring(2);
    }

    if (string.startsWith("/" + CommandsHandler.getLabel() + ":")) {
      string =
          CommandsHandler.getLabel()
              + " "
              + string.substring(("/" + CommandsHandler.getLabel() + ":").length());
    }

    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    string = string.toLowerCase();
    if (!(commandSender instanceof Player)) {
      return false;
    }

    CmdWarmUp cmdWarmUp = this.getTime(string);
    return cmdWarmUp != null
        && !PermissionsManager.CMIPerm.warmupbypass_$1.hasPermission(
            commandSender, cmdWarmUp.getSubCommand().replace(" ", ""));
  }

  public boolean startCmdUsage(CommandSender commandSender, String string) {
    return this.startCmdUsage(commandSender, string, true);
  }

  public boolean startCmdUsage(CommandSender commandSender, String string, boolean flag) {
    if (!CMIModule.warmup.isEnabled()) {
      return false;
    }

    if (string.startsWith("/ ")) {
      string = "/" + string.substring(2);
    }

    if (string.startsWith("/:")) {
      string = string.substring(2);
    }

    if (string.startsWith("/" + CommandsHandler.getLabel() + ":")) {
      string =
          CommandsHandler.getLabel()
              + " "
              + string.substring(("/" + CommandsHandler.getLabel() + ":").length());
    }

    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    string = string.toLowerCase();
    if (!(commandSender instanceof Player)) {
      return false;
    }

    Player player = (Player) commandSender;
    if (string.split(" ", 2)[0].contains(":")) {
      string = string.split(":", 2)[1];
    }

    if (!this.plugin.getCooldownManager().canUseCmd(commandSender, string, true, false)) {
      return true;
    }

    CmdWarmUp cmdWarmUp = this.getTime(string);
    if (cmdWarmUp == null
        || PermissionsManager.CMIPerm.warmupbypass_$1.hasPermission(
            commandSender, cmdWarmUp.getSubCommand().replace(" ", ""))) {
      return false;
    }

    if (!this.plugin.getCommandCostManager().isContinueCommand(commandSender, string)) {
      return true;
    }

    boolean canMove = this.canMoveByCmd(string);
    this.cancel(player.getUniqueId());
    WarmUpInfo warmUpInfo = new WarmUpInfo(string, canMove);
    if (flag && string.startsWith(CommandsHandler.getLabel() + " ")) {
      try {
        String warmupKey =
            string.substring((CommandsHandler.getLabel() + " ").length()).split(" ")[0];
        if (!CommandsHandler.hasCommandPermission(commandSender, warmupKey, 1000L)) {
          return false;
        }
      } catch (Exception | Error value) {
      }
    }

    int remaining = cmdWarmUp.getTime().intValue();
    PermissionInfo permissionInfo =
        PermissionsManager.CMIPerm.warmuptime_$1_$2.getPermissionInfo(
            player, cmdWarmUp.getSubCommand().replace(" ", ""), "");
    remaining = permissionInfo.getMinValue(remaining);
    warmUpInfo.setLeftCount(remaining);
    warmUpInfo.setLoc(player.getLocation());
    warmUpInfo.setWarmup(cmdWarmUp);
    this.counter.put(player.getUniqueId(), warmUpInfo);
    if (CMILib.getInstance().getGUIManager().isOpenedGui(player)) {
      CMIUser.closeInventory(player);
    }

    if (cmdWarmUp.getAnimation() != null) {
      CMIPEAnimationInterface cmipeAnimationInterface = cmdWarmUp.getAnimation().getInter();
      CMIVisualEffect cmiVisualEffect = new CMIVisualEffect(player, cmipeAnimationInterface);
      cmiVisualEffect.setTargetPlayer(player);
      cmiVisualEffect.setUntil(System.currentTimeMillis() + remaining * 1000 - 500L);
      warmUpInfo.setEffectAnimation(cmiVisualEffect);
      cmiVisualEffect.show();
    }

    if (this.showBossBarInfo) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      BossBarInfo barInfo = cmiUser.getBossBar("CMICmdWarmupBar");
      if (barInfo == null) {
        barInfo = this.bossBarInfo.clone(cmiUser.getPlayer());
      }

      warmUpInfo.setBar(barInfo);
      barInfo.setSeconds(-remaining);
      if (warmUpInfo.isMove()) {
        barInfo.setTitleOfBar(CMILC.info_WarmUp_Boss_WaitFor.getLocale());
      } else {
        barInfo.setTitleOfBar(CMILC.info_WarmUp_Boss_DontMove.getLocale());
      }

      cmiUser.addBossBar(barInfo);
    }

    CMISoundPreset.CommandWarmup.play(player);
    this.count(player.getUniqueId());
    return true;
  }

  private void performCmd(Player player, WarmUpInfo warmUpInfo) {
    String string = warmUpInfo.getCmd();
    if (player != null) {
      if (string != null) {
        CMIScheduler.runAtLocation(
            this.plugin, player.getLocation(), () -> player.performCommand(string));
        this.plugin.getCooldownManager().recordCommandUsage(player, string);
      } else if (warmUpInfo.getLoc() != null) {
        this.plugin
            .getTeleportHandler()
            .teleportPlayer(player, warmUpInfo.getLoc(), CMITeleportType.WarmUp);
      }

      if (warmUpInfo.getEffectAnimation() != null) {
        warmUpInfo.getEffectAnimation().stop();
      }
    }
  }

  private void count(UUID uuid) {
    WarmUpInfo warmUpInfo = this.counter.get(uuid);
    if (warmUpInfo != null) {
      warmUpInfo.setTask(
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (CMIUser.getOnlinePlayer(uuid) == null) {
                  this.cancel(uuid);
                } else {
                  if (this.InformOnNoMove && warmUpInfo.getLeftCount() > 0) {
                    CMIScheduler.runTaskAsynchronously(
                        this.plugin,
                        () ->
                            CMILC.info_WarmUp_DontMove.sendMessage(
                                CMIUser.getOnlinePlayer(uuid),
                                "[time]",
                                warmUpInfo.getLeftCount() + 1));
                  }

                  if (this.counterBarInfo) {
                    CMIScheduler.runTaskAsynchronously(
                        this.plugin,
                        () ->
                            CMILC.info_WarmUp_counter.sendMessage(
                                CMIUser.getOnlinePlayer(uuid),
                                "[time]",
                                warmUpInfo.getLeftCount() + 1));
                  }

                  if (warmUpInfo.getLeftCount() <= 0) {
                    warmUpInfo.getTask().cancel();
                    CMIActionBar.send(CMIUser.getOnlinePlayer(uuid), "");
                    CMITitleMessage.send(CMIUser.getOnlinePlayer(uuid), "", "");
                    this.performCmd(CMIUser.getOnlinePlayer(uuid), warmUpInfo);
                    this.cancel(uuid, true);
                  } else {
                    warmUpInfo.setLeftCount(warmUpInfo.getLeftCount() - 1);
                  }
                }
              },
              0L,
              20L));
    }
  }

  public boolean cancel(UUID uuid) {
    return this.cancel(uuid, false);
  }

  public boolean cancel(UUID uuid, boolean flag) {
    WarmUpInfo warmUpInfo = this.counter.remove(uuid);
    if (warmUpInfo != null && warmUpInfo.getTask() != null) {
      warmUpInfo.getTask().cancel();
      if (!flag) {
        CMIActionBar.send(CMIUser.getOnlinePlayer(uuid), "");
        CMITitleMessage.send(CMIUser.getOnlinePlayer(uuid), "", "");
      }

      if (warmUpInfo.getBar() != null) {
        warmUpInfo.getBar().remove();
      }

      if (warmUpInfo.getEffectAnimation() != null) {
        warmUpInfo.getEffectAnimation().stop();
      }

      return true;
    } else {
      return false;
    }
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "WarmUps.Enabled",
        new String[] {
          "You can enable any command warmup to prevent instant command usage",
          "tp:5:false means that when player performs /tp command he will need to wait 5 sec and"
              + " will not be able to move outside current block",
          "tp:5:false-3 means that when player performs /tp command he will need to wait 5 sec and"
              + " will be limited to movement of no more than 3 blocks from original position",
          "false variable is optional and when its set to false player cant move while warmup is"
              + " counting",
          "If you don't want to deny empty warp command but want to deny any extra variable after"
              + " that, then just add space, in example 'warp :5:false'",
          "When setting warmups for CMI commands, use full command name and not allias, in example"
              + " 'cmi warp:5:false'",
          "Administration can bypass limitations with cmi.warmupbypass.[commandname] permission"
              + " node",
          "ATTENTION! cmi home command is being handled in special way and to prevent double"
              + " warmup, add space, example: - cmi home :5:false",
          "Experimental: add GlyphHead to the warmup to show particle effect while command is on"
              + " warmup period. Like",
          "- cmi warp :3:false:GlyphHead"
        });
    boolean flag = configReader.get("WarmUps.Enabled", false);
    this.InformOnNoMove = configReader.get("WarmUps.InformOnNoMove", true);
    this.counterBarInfo =
        configReader.get(
            "WarmUps.showCounterBarInfo", !Version.isCurrentEqualOrHigher(Version.v1_9_R1));
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      this.showBossBarInfo =
          configReader.get(
              "WarmUps.BossBarInfo.Enabled",
              configReader.getC().getBoolean("WarmUps.showBossBarInfo", false));
      String string = configReader.get("WarmUps.BossBarInfo.Color", "blue");
      configReader.addComment(
          "WarmUps.BossBarInfo.Segments", new String[] {"Options: 1, 6, 10, 12, 20"});
      String segments = configReader.get("WarmUps.BossBarInfo.Segments", "1");

      try {
        this.bossBarInfo = new BossBarInfo("CMICmdWarmupBar");
        this.bossBarInfo.setKeepForTicks(0);
        BarColor[] barColors;
        int value = (barColors = BarColor.values()).length;

        for (int segmentIndex = 0; segmentIndex < value; segmentIndex++) {
          BarColor barColor = barColors[segmentIndex];
          if (barColor.name().equalsIgnoreCase(string)) {
            this.bossBarInfo.setColor(barColor);
            break;
          }
        }

        String segmentText = segments;
        switch (segments.hashCode()) {
          case 49:
            if (segmentText.equals("1")) {
              this.bossBarInfo.setStyle(BarStyle.SOLID);
            }
            break;
          case 54:
            if (segmentText.equals("6")) {
              this.bossBarInfo.setStyle(BarStyle.SEGMENTED_6);
            }
            break;
          case 1567:
            if (segmentText.equals("10")) {
              this.bossBarInfo.setStyle(BarStyle.SEGMENTED_10);
            }
            break;
          case 1569:
            if (segmentText.equals("12")) {
              this.bossBarInfo.setStyle(BarStyle.SEGMENTED_12);
            }
            break;
          case 1598:
            if (segmentText.equals("20")) {
              this.bossBarInfo.setStyle(BarStyle.SEGMENTED_20);
            }
        }
      } catch (Exception exception) {
      }
    }

    var list =
        configReader.get(
            "WarmUps.List",
            Arrays.asList(
                "cmi tp :5:false",
                "cmi back:3:true",
                "cmi warp :3:false",
                "cmi home :3:false",
                "cmi home :3:false-2"));
    this.warmups.clear();
    if (flag) {
      Long now = System.currentTimeMillis();
      Iterator iterator = list.iterator();

      while (true) {
        String warmUpSpec;
        CmdWarmUp cmdWarmUp;
        while (true) {
          boolean flagValue;
          double number;
          long longValue;
          while (true) {
            while (true) {
              if (!iterator.hasNext()) {
                this.plugin.loadMessage(
                    this.warmups.size(), "warmups", System.currentTimeMillis() - now);
                return;
              }

              warmUpSpec = (String) iterator.next();
              if (warmUpSpec.contains(":")) {
                longValue = -1L;

                try {
                  longValue = Long.parseLong(warmUpSpec.split(":")[1]);
                  break;
                } catch (NumberFormatException numberFormatException) {
                }
              }
            }

            if (longValue >= 1L) {
              flagValue = false;
              number = 0.0;
              if (warmUpSpec.split(":").length < 3) {
                break;
              }

              try {
                String flagPart = warmUpSpec.split(":")[2];
                if (flagPart.contains("-")) {
                  String[] strings = flagPart.split("-", 2);
                  flagValue = Boolean.parseBoolean(strings[0]);
                  number = Double.parseDouble(strings[1]);
                } else {
                  flagValue = Boolean.parseBoolean(flagPart);
                }
                break;
              } catch (NumberFormatException numberFormatException2) {
              }
            }
          }

          cmdWarmUp = new CmdWarmUp(warmUpSpec.split(":")[0].toLowerCase(), longValue, flagValue);
          cmdWarmUp.setMaxMoveDistance(number);
          if (warmUpSpec.split(":").length < 4) {
            break;
          }

          try {
            ParticleManager.CMIPresetAnimations cmiPresetAnimations =
                ParticleManager.CMIPresetAnimations.getByName(warmUpSpec.split(":")[3]);
            if (cmiPresetAnimations != null && cmiPresetAnimations.isEnabled()) {
              cmdWarmUp.setAnimation(cmiPresetAnimations);
            }
            break;
          } catch (NumberFormatException numberFormatException3) {
          }
        }

        this.warmups.put(warmUpSpec.split(":")[0].toLowerCase(), cmdWarmUp);
      }
    }
  }
}
