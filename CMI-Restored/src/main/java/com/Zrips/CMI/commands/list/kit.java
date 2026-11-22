package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIFieldType;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class kit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "noMoney", "{gcp}Not enough money. Cost: {gcs}[cost] {gcp}you have {gcs}[balance]");
    configReader.get(
        "noExp", "{gcp}Not enough exp. Cost: {gcs}[cost] {gcp}you have {gcs}[balance]");
    configReader.get(
        "noCondition",
        "{gcp}You don't have special condition permission to use this kit: {gcs}[condition]");
    configReader.get("oneTime", "{gcd}This is a one time use kit and you have already used it!");
    configReader.get("cantUseNow", "{gcd}You can't use this kit for the next {gcs}[time]");
    configReader.get("limitedTimeUseError", "{gcp}Can't use more than {gcs}[times] {gcp}times");
    configReader.get("limitedTimeUse", "{gcp}Can use for {gcs}[times] {gcp}times");
    configReader.get("limitedTimeAllUsed", "{gcd}You can't get another one");
    configReader.get("canUseIn", "{gcp}Can use again in: \n[time]");
    configReader.get("cooldown", "{gcp}Cooldown: \n[time]");
    configReader.get("noKit", "{gcp}Can't find kit by this name");
    configReader.get("kitDoesntExist", "{gcp}Can't find kit ([kitName])");
    configReader.get("noKitAccess", "{gcw}You don't have access to this kit");
    configReader.get("noFoundKit", "{gcp}Can't find any kits");
    configReader.get("givenKit", "{gcp}You have given {gcs}[kit] {gcp}to {gcs}[playerDisplayName]");
    configReader.get("title", "{gcp}Kits");
    configReader.addComment(
        "kitGuiTitle", new String[] {"[kitName] or [kitDisplayName] can be used"});
    configReader.get("kitGuiTitle", "{gcs}[kitDisplayName]");
    configReader.addComment(
        "titlesByPage",
        new String[] {
          "List of custom kit GUI titles based by page",
          "If title isint defined we will use default one"
        });
    configReader.get("titlesByPage", Arrays.asList(configReader.get("title", "{gcp}Kits")));
    configReader.get("nameCanUse", "{gcs}[kitName]");
    configReader.get("nameCantUse", "{gcn}[kitName]");
    configReader.get("group", "{gcp}Group: {gcs}[group]");
    configReader.get("left", "{gcs}Left {gcp}click to claim");
    configReader.get("right", "{gcs}Right {gcp}click to preview");
    configReader.get("take", "{gcs}Take this kit!");
    configReader.get("open", "{gcp}Items from {gcs}[kitName] {gcp}kit!");
    configReader.get("eye", " {gcp}☬");
    configReader.get("use", "{gcu}");
    configReader.get("cantuse", "{gcn}");
    configReader.get("list", "{gcs}[number]{gcp}. {gcs}[kitName] {gcp}- {gcs}[description]");
  }

  @CAnnotation(
      priority = 170,
      info = "Gives predefined kit.",
      args = "[kitName] (playerName) (-s) (-open) (-preview) (-c) (-skipusage)",
      tab = {"kitp", "playername", "-s%%"},
      explanation = {
        "Permissions:",
        "cmi.kit.[kitName] - allows to use particular kit",
        "cmi.kit.bypass.money - bypass money requirement",
        "cmi.kit.bypass.exp - bypass exp requirement",
        "cmi.kit.bypass.onetimeuse - bypass one time use",
        "/cmi kit [kitName] [playerName] - will give kit to another player"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {0, 2, 3},
      paccess = true,
      others = true,
      modules = "kits",
      customAlias = {"kits", "!+-preview:kitpreview", "!+-open:kitopen"})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String kitName = null;
    int value = 1;
    boolean flag = false;
    boolean silent = false;
    boolean forceGive = false;
    boolean previewOnly = false;
    boolean ignoreCooldown = false;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          ignoreCooldown = true;
        }
      } else if (arg.equalsIgnoreCase("-preview")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("-c")) {
        forceGive = true;
      } else if (arg.equalsIgnoreCase("-skipusage")) {
        previewOnly = PermissionsManager.CMIPerm.kit_bypass_usetime.hasPermission(commandSender);
      } else if (arg.equalsIgnoreCase("-open")) {
        silent = true;
      } else if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length()));
        } catch (Throwable throwable) {
        }
      } else if (kitName == null) {
        kitName = arg;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    boolean notSelf = !player.getName().equals(commandSender.getName());
    if (kitName == null) {
      if (cmi.getKitsManager().isKitsGUI()) {
        cmi.getKitsManager().listPlayersKitsInGUI(player, value);
      } else {
        cmi.getKitsManager().listPlayersKits(commandSender, player, value);
      }

      return true;
    } else {
      Kit kit = cmi.getKitsManager().getKit(player, kitName, notSelf, true);
      if (kit != null
          && (kit.isEnabled()
              || !(commandSender instanceof Player)
              || PermissionsManager.CMIPerm.kit_$1.hasPermission(commandSender, "*"))) {
        if (!flag
            && (PermissionsManager.CMIPerm.kit_$1.hasPermission(commandSender, kit.getConfigName())
                || PermissionsManager.CMIPerm.kit_$1.hasPermission(
                    commandSender, kit.getCommandName()))) {
          CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
          Long kitTime = cmiUser.getKitTime(kit);
          if (!kit.isDropItems() && !kit.enoughFreeSpace(player)) {
            LC.info_noSpace.sendMessage(player, new Object[0]);
            return null;
          }

          if (commandSender instanceof Player && player.getName().equals(commandSender.getName())
              || forceGive && player.isOnline()) {
            if (cmiUser.getLeftKitUseTimes(kit) == 0
                && !PermissionsManager.CMIPerm.kit_bypass_onetimeuse.hasPermission(commandSender)) {
              CMILC.info(this, commandSender, "limitedTimeUseError", "[times]", kit.getMaxUsages());
              return null;
            }

            if (kit.getCost() > 0.0
                && !PermissionsManager.CMIPerm.kit_bypass_money.hasPermission(player)
                && !cmiUser.hasMoney(kit.getCost())) {
              CMILC.info(
                  this,
                  commandSender,
                  "noMoney",
                  "[cost]",
                  kit.getCost(),
                  "[balance]",
                  cmiUser.getFormatedBalance());
              return null;
            }

            if (kit.getExpCost() > 0.0
                && !PermissionsManager.CMIPerm.kit_bypass_exp.hasPermission(player)
                && cmiUser.getTotalExp() < kit.getExpCost()) {
              CMILC.info(
                  this,
                  commandSender,
                  "noExp",
                  "[cost]",
                  kit.getExpCost(),
                  "[balance]",
                  cmiUser.getTotalExp());
              return null;
            }

            for (String condition : kit.getConditions()) {
              if (!player.hasPermission(condition)) {
                CMILC.info(this, commandSender, "noCondition", "[condition]", condition);
                return null;
              }
            }

            if (kitTime != null) {
              long longValue = kitTime + kit.getDelay() * 1000L;
              if (longValue > System.currentTimeMillis()
                  && !PermissionsManager.CMIPerm.kit_bypass_time.hasPermission(player)) {
                String remainingTime =
                    CMITimeManager.to24hourShort(longValue - System.currentTimeMillis());
                CMILC.info(this, commandSender, "cantUseNow", "[time]", remainingTime);
                return null;
              }
            }

            if (kit.getCost() > 0.0
                && !PermissionsManager.CMIPerm.kit_bypass_money.hasPermission(player)) {
              cmiUser.withdraw(kit.getCost());
            }

            if (kit.getExpCost() > 0.0
                && !PermissionsManager.CMIPerm.kit_bypass_exp.hasPermission(player)) {
              cmiUser.takeExp(kit.getExpCost());
            }
          } else if (forceGive) {
            if (cmiUser.getLeftKitUseTimes(kit) == 0) {
              CMILC.info(this, commandSender, "limitedTimeUseError", "[times]", kit.getMaxUsages());
              return null;
            }

            if (kit.getCost() > 0.0 && !cmiUser.hasMoney(kit.getCost())) {
              CMILC.info(
                  this,
                  commandSender,
                  "noMoney",
                  "[cost]",
                  kit.getCost(),
                  "[balance]",
                  cmiUser.getFormatedBalance());
              return null;
            }

            if (kit.getExpCost() > 0.0 && cmiUser.getTotalExp() < kit.getExpCost()) {
              CMILC.info(
                  this,
                  commandSender,
                  "noExp",
                  "[cost]",
                  kit.getExpCost(),
                  "[balance]",
                  cmiUser.getTotalExp());
              return null;
            }

            if (kitTime != null) {
              long kitAvailableTime = kitTime + kit.getDelay() * 1000L;
              if (kitAvailableTime > System.currentTimeMillis()) {
                String remainingTime =
                    CMITimeManager.to24hourShort(kitAvailableTime - System.currentTimeMillis());
                CMILC.info(this, commandSender, "cantUseNow", "[time]", remainingTime);
                return null;
              }
            }

            if (kit.getCost() > 0.0) {
              cmiUser.withdraw(kit.getCost());
            }

            if (kit.getExpCost() > 0.0) {
              cmiUser.takeExp(kit.getExpCost());
            }
          }

          kitTime = System.currentTimeMillis();
          if (kit.getDelay() < 0L) {
            kitTime = -1L;
          }

          if (!previewOnly) {
            cmiUser.addKit(kit, kitTime, false);
          }

          cmiUser.addForDelayedSave();
          if (silent) {
            this.openKit(player, kit);
          }

          cmi.getKitsManager().giveKit(player, kit, !silent);
          Snd snd = new Snd().setSender(commandSender).setTarget(player);
          if (!commandSender.getName().equals(player.getName()) && !ignoreCooldown) {
            CMILC.info(this, commandSender, "givenKit", snd, "[kit]", kit.getDisplayName());
          }

          return true;
        } else {
          if (player.isOnline()) {
            cmi.getKitsManager().openGuiEditorMain(kit, player, false);
          }

          return true;
        }
      } else {
        if (cmi.getKitsManager().getKit(kitName, false) != null) {
          CMILC.info(this, commandSender, "noKitAccess", "[kitName]", kitName);
        } else {
          CMILC.info(this, commandSender, "kitDoesntExist", "[kitName]", kitName);
        }

        return null;
      }
    }
  }

  private void openKit(Player player, Kit kit) {
    CMIGui cmiGui = new CMIGui(player);
    cmiGui.setTitle(CMILC.getIM(kit.class, "open", "[kitName]", kit.getDisplayName()));

    for (ItemStack itemStack : kit.getItems(player)) {
      if (itemStack != null) {
        CMIGuiButton cmiGuiButton = new CMIGuiButton(itemStack);
        cmiGuiButton.setFieldType(GUIFieldType.noPlacing);
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.setNoItemPlacement(InvType.Gui);
    cmiGui.setAllowShift(true);
    cmiGui.open();
  }
}
