package com.Zrips.CMI.Modules.CmdCost;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMICommandCostManager {
  private HashMap<String, CMICommandCost> costs = new HashMap<>();
  CMI plugin;
  private String fileName = "CommandCost.yml";

  public CMICommandCostManager(CMI cmi) {
    this.plugin = cmi;
  }

  public int getCommandCostCount() {
    return this.costs.size();
  }

  public void addCost(String string, double number) {
    this.costs.put(string.toLowerCase(), new CMICommandCost(string, number));
  }

  public CMICommandCost getCost(String string) {
    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    string = string.toLowerCase();
    string = string.startsWith(":") ? string.substring(1) : string;

    for (var entry : this.costs.entrySet()) {
      if (string.startsWith((String) entry.getKey())) {
        String remainder = string.substring(((String) entry.getKey()).length());
        if (remainder.isEmpty()
            || remainder.startsWith(" ")
            || ((String) entry.getKey()).endsWith(" ")) {
          return (CMICommandCost) entry.getValue();
        }
      }
    }

    return null;
  }

  public CMICommandCostUsage canUseCmd(CommandSender commandSender, String string) {
    if (!(commandSender instanceof Player)) {
      return CMICommandCostUsage.Can;
    }

    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    string =
        string.startsWith(CommandsHandler.getLabel() + " :")
            ? CommandsHandler.getLabel()
                + " "
                + string.substring((CommandsHandler.getLabel() + " :").length())
            : string;
    string =
        string.startsWith(CommandsHandler.getLabel() + ":" + CommandsHandler.getLabel())
            ? CommandsHandler.getLabel()
                + string.substring(CommandsHandler.getLabel().length() * 2 + 1)
            : string;
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
    if (cmiUser == null) {
      return CMICommandCostUsage.Can;
    } else {
      CMICommandCost cmiCommandCost = this.getCost(string);
      if (cmiCommandCost == null) {
        return CMICommandCostUsage.Can;
      } else if (PermissionsManager.CMIPerm.costbypass_$1.hasPermission(
          commandSender, cmiCommandCost.getSubCommand().replace(" ", ""))) {
        return CMICommandCostUsage.CostBypass;
      } else if (cmiCommandCost.isRequiresConfirmation()
          && !cmiCommandCost.isConfirmation(cmiUser.getUniqueId(), string)) {
        cmiCommandCost.addConfirmation(cmiUser.getUniqueId(), string);
        return CMICommandCostUsage.ReqConfirm;
      } else {
        return cmiUser.hasMoney(cmiCommandCost.getCost())
            ? CMICommandCostUsage.Can
            : CMICommandCostUsage.NoMoney;
      }
    }
  }

  public boolean isContinueCommand(CommandSender commandSender, String string) {
    CMICommandCostUsage cmiCommandCostUsage =
        this.plugin.getCommandCostManager().canUseCmd(commandSender, string);
    boolean flag =
        commandSender instanceof Player
            ? cmiCommandCostUsage.equals(CMICommandCostUsage.Can)
                || cmiCommandCostUsage.equals(CMICommandCostUsage.CostBypass)
            : true;
    if (!flag && commandSender instanceof Player) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
      CMICommandCost cmiCommandCost = this.plugin.getCommandCostManager().getCost(string);
      if (!cmiCommandCostUsage.equals(CMICommandCostUsage.NoMoney)
          && !cmiCommandCostUsage.equals(CMICommandCostUsage.Cant)) {
        if (cmiCommandCostUsage.equals(CMICommandCostUsage.ReqConfirm)) {
          RawMessage rawMessage = new RawMessage();
          rawMessage.addText(
              CMILC.econ_commandCost.getLocale(
                  "[cost]", this.plugin.getEconomyManager().format(cmiCommandCost.getCost())));
          rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
          rawMessage.addCommand(string);
          rawMessage.show(commandSender);
          return false;
        }
      } else if (cmiCommandCost != null
          && cmiCommandCost.getCost() > 0.0
          && !cmiUser.hasMoney(cmiCommandCost.getCost())) {
        CMIMessages.sendMessage(
            commandSender,
            LC.econ_notEnoughMoney,
            new Object[] {
              "[amount]", this.plugin.getEconomyManager().format(cmiCommandCost.getCost())
            });
        return false;
      }
    }

    return true;
  }

  public boolean loadConfig(boolean flag) {
    this.plugin.safeRenameFile("commandCost.yml", "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Settings" + File.separator + this.fileName);
    } catch (Exception exception) {
      if (flag) {
        this.plugin.consoleMessage(
            "&cSEVERE CONFIG FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in config file"
                + " reset");
        return false;
      }

      exception.printStackTrace();
    }

    if (configReader == null) {
      return false;
    }

    boolean enabled = configReader.get("Enabled", false);
    configReader.addComment(
        "Commands",
        new String[] {
          "Example: - 'cmi heal:100.5:true'",
          "While defining command avoid using alias and use full command name",
          "Cost can be any positive number",
          "After cost you can use true/false to define if you want to inform player when money is"
              + " charged after using command or not",
          "Second true/false will define if we confirmation from players by typing command again or"
              + " clicking on a message in chat before performing it and charging for it. This will"
              + " include information about command cost. By default players will be asked for"
              + " confirmation",
          "Attention! Don't use alias command name, use full base command, for example /heal is not"
              + " valid, use /cmi heal otherwise you will be risking in allowing players to perform"
              + " base command without a charge",
          "On another hand you can use custom alias commands in here",
          "",
          "Keep in mind: base CMI commands will double check if command was successfully performed"
              + " before charging money, while custom alias will charge money despite commands"
              + " success",
          "So in example of /cmi warp player will not be charged if he enters incorrect warp name,"
              + " while having custom alias will charge money even if command fails to be performed"
        });
    var list =
        Arrays.asList(
            "cmi heal:100.5:true:true",
            "cmi feed:100.5:true:true",
            "cmi home :30:true:true",
            "cmi warp :10:true:true");
    list = configReader.get("Commands", list);
    this.costs.clear();
    if (enabled) {
      for (String string : list) {
        CMICommandCost cmiCommandCost = get(string);
        if (cmiCommandCost != null) {
          this.costs.put(cmiCommandCost.getCommand().toLowerCase(), cmiCommandCost);
        }
      }
    }

    configReader.save();
    return true;
  }

  private static CMICommandCost get(String string) {
    String[] strings = string.split(":");
    CMICommandCost cmiCommandCost = null;

    try {
      cmiCommandCost = new CMICommandCost(strings[0], Double.parseDouble(strings[1]));
    } catch (Exception exception) {
      CMI.getInstance().consoleMessage("&cWrong input for command cost: " + string);
      return null;
    }

    try {
      if (strings.length > 2) {
        cmiCommandCost.setInformOnCharge(Boolean.parseBoolean(strings[2]));
      }
    } catch (Exception exception2) {
      CMI.getInstance().consoleMessage("&cWrong input for command cost: " + string);
    }

    try {
      if (strings.length > 3) {
        cmiCommandCost.setRequiresConfirmation(Boolean.parseBoolean(strings[3]));
      }
    } catch (Exception exception3) {
      CMI.getInstance().consoleMessage("&cWrong input for command confirmation: " + string);
    }

    return cmiCommandCost;
  }
}
