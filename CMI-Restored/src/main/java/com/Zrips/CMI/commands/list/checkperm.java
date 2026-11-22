package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.PInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class checkperm implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[permission]");
    configReader.get("hover", "{gcs}[desc]");
    configReader.get(
        "others",
        "{gcw}\n---->When command is used on another player<----\n{gcw}Base command required");
    configReader.get("permissionColor", "{gcp}");
    configReader.get("cmdPermissionColor", "{gcu}");
    configReader.get("variableColor", "{gcn}");
    configReader.get("noPermissionFound", "{gcw}No permissions found by this keyword");
  }

  @CAnnotation(
      priority = 240,
      info = "Check possible permission nodes",
      args = "(key word)",
      multiTab = "[playerName]",
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Integer integer = null;
    String string = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(arg.replace("-p:", ""));
          continue;
        } catch (Exception exception) {
        }
      }

      string = arg.toLowerCase();
    }

    if (integer == null) {
      integer = 1;
    }

    HashMap<String, PInfo> hashMap = new HashMap();
    if (string != null) {
      PermissionsManager.CMIPerm[] cmiPerms;
      int permCount = (cmiPerms = PermissionsManager.CMIPerm.values()).length;

      for (int permIndex = 0; permIndex < permCount; permIndex++) {
        PermissionsManager.CMIPerm cmiPerm = cmiPerms[permIndex];
        if (cmiPerm.getShow()) {
          if (cmiPerm.getPermission().contains(string)) {
            hashMap.put(
                cmiPerm.getPermissionForShow(),
                new PInfo(cmiPerm.getPermissionForShow(), cmiPerm.getDesc()));
          } else if (cmiPerm.getDesc().toLowerCase().contains(string)) {
            hashMap.put(
                cmiPerm.getPermissionForShow(),
                new PInfo(cmiPerm.getPermissionForShow(), cmiPerm.getDesc()));
          } else if (cmiPerm.getWars() != null && cmiPerm.getWars().length > 0) {
            String[] wars;
            int warCount = (wars = cmiPerm.getWars()).length;

            for (int warIndex = 0; warIndex < warCount; warIndex++) {
              String war = wars[warIndex];
              if (war.toLowerCase().contains(string)) {
                hashMap.put(
                    cmiPerm.getPermissionForShow(),
                    new PInfo(cmiPerm.getPermissionForShow(), cmiPerm.getDesc()));
              }
            }
          }
        }
      }

      for (var entry : cmi.getCommandManager().getCommands().entrySet()) {
        if (!((CMICommand) entry.getValue()).getAnottation().hidden()) {
          if (((String) entry.getKey()).toLowerCase().contains(string)) {
            hashMap.put(
                PermissionsManager.CMIPerm.command_$1.getPermission(
                    ((String) entry.getKey()).toLowerCase()),
                new PInfo(
                    PermissionsManager.CMIPerm.command_$1.getPermission(
                        ((String) entry.getKey()).toLowerCase()),
                    ((CMICommand) entry.getValue()).getAnottation().info(),
                    true));
            if (((CMICommand) entry.getValue()).getAnottation().others()) {
              hashMap.put(
                  PermissionsManager.CMIPerm.command_$1_others.getPermission(
                      ((String) entry.getKey()).toLowerCase()),
                  new PInfo(
                      PermissionsManager.CMIPerm.command_$1_others.getPermission(
                          ((String) entry.getKey()).toLowerCase()),
                      ((CMICommand) entry.getValue()).getAnottation().info(),
                      true,
                      true));
            }
          } else if (((CMICommand) entry.getValue())
              .getAnottation()
              .info()
              .toLowerCase()
              .contains(string)) {
            hashMap.put(
                PermissionsManager.CMIPerm.command_$1.getPermission(
                    ((String) entry.getKey()).toLowerCase()),
                new PInfo(
                    PermissionsManager.CMIPerm.command_$1.getPermission(
                        ((String) entry.getKey()).toLowerCase()),
                    ((CMICommand) entry.getValue()).getAnottation().info(),
                    true));
          } else {
            String[] aliases;
            int aliasCount =
                (aliases = ((CMICommand) entry.getValue()).getAnottation().explanation()).length;

            for (int aliasIndex = 0; aliasIndex < aliasCount; aliasIndex++) {
              String alias = aliases[aliasIndex];
              if (alias.toLowerCase().contains(string)) {
                hashMap.put(
                    PermissionsManager.CMIPerm.command_$1.getPermission(
                        ((String) entry.getKey()).toLowerCase()),
                    new PInfo(
                        PermissionsManager.CMIPerm.command_$1.getPermission(
                            ((String) entry.getKey()).toLowerCase()),
                        ((CMICommand) entry.getValue()).getAnottation().info(),
                        true));
              }
            }
          }
        }
      }
    } else {
      PermissionsManager.CMIPerm[] cmiPerms;
      int permCount = (cmiPerms = PermissionsManager.CMIPerm.values()).length;

      for (int permIndex = 0; permIndex < permCount; permIndex++) {
        PermissionsManager.CMIPerm cmiPerm = cmiPerms[permIndex];
        if (cmiPerm.getShow()) {
          hashMap.put(
              cmiPerm.getPermissionForShow(),
              new PInfo(cmiPerm.getPermissionForShow(), cmiPerm.getDesc()));
        }
      }
    }

    return this.show(commandSender, integer, hashMap, string);
  }

  private Boolean show(
      final CommandSender commandSender,
      int value,
      final HashMap<String, PInfo> hashMap,
      final String string) {
    PageInfo pageInfo =
        new PageInfo(10, hashMap.size(), value) {
          public Boolean pageChange(int page) {
            return checkperm.this.show(commandSender, page, hashMap, string);
          }
        };
    if (hashMap.isEmpty()) {
      CMILC.info(this, commandSender, "noPermissionFound");
      return null;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (var entry : hashMap.entrySet()) {
      if (pageInfo.isEntryOk()) {
        if (pageInfo.isBreak()) {
          break;
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[place]",
                    pageInfo.getPositionForOutput(),
                    "[permission]",
                    (!((PInfo) entry.getValue()).isCmd()
                            ? CMILC.getIM("checkperm", "permissionColor")
                            : CMILC.getIM("checkperm", "cmdPermissionColor"))
                        + ((PInfo) entry.getValue()).getPermission()))
            .addHover(
                CMILC.getIM(this, "hover", "[desc]", ((PInfo) entry.getValue()).getDesc())
                    + (((PInfo) entry.getValue()).isOthers() ? CMILC.getIM(this, "others") : ""))
            .addSuggestion(CMIChatColor.stripColor(((PInfo) entry.getValue()).getPermission()));
        if (!(commandSender instanceof Player)) {
          rawMessage.addText(
              " - "
                  + CMILC.getIM(this, "hover", "[desc]", ((PInfo) entry.getValue()).getDesc())
                  + (((PInfo) entry.getValue()).isOthers() ? CMILC.getIM(this, "others") : ""));
        }

        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + this.getClass().getSimpleName()
            + (string == null ? "" : " " + string),
        "-p:");
    return true;
  }
}
