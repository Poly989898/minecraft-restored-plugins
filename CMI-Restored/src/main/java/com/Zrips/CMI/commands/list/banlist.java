/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.PageInfo
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.RawMessages.RawMessage
 *  org.bukkit.BanEntry
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import java.util.Iterator;
import java.util.Set;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.BanEntry;
import org.bukkit.command.CommandSender;

public class banlist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @Override
  @CAnnotation(
      info = "Ban list",
      args = "(-s)",
      multiTab = {"-s"},
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 3},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    String arg;
    Integer n = null;
    boolean bl = false;
    int argCount = args.length;
    int argIndex = 0;
    while (argIndex < argCount) {
      arg = args[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (!arg.startsWith("-p:")) break;
            try {
              n = Integer.parseInt(arg.replace("-p:", ""));
              break;
            } catch (Exception exception) {
              // empty catch block
            }
          }
      }
      ++argIndex;
    }
    if (n == null) {
      n = 1;
    }
    var entries = CMIBanRecords.getBanEntries().getBanEntries();
    if (entries.isEmpty()) {
      LC.info_nothingToShow.sendMessage((Object) commandSender, new Object[0]);
      return true;
    }
    PageInfo pageInfo = new PageInfo(10, entries.size(), n.intValue());
    LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
    String string = CMILC.getIM(banlist.class, "banList", new Object[0]);
    Iterator banIterator = entries.iterator();
    while (banIterator.hasNext()) {
      BanEntry iteratedBan = (BanEntry) banIterator.next();
      if (pageInfo.isBreak()) break;
      if (pageInfo.isContinue()) continue;
      BanEntry banEntry = iteratedBan;
      StringBuilder stringBuilder = new StringBuilder();
      int ipUserCount = 0;
      Set<CMIUser> set = cMI.getIpManager().getUsers(banEntry.getTarget());
      if (set != null) {
        for (CMIUser cMIUser : set) {
          if (cMIUser.getName() == null) continue;
          ++ipUserCount;
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
          }
          if (ipUserCount > 5) {
            ipUserCount = 0;
            stringBuilder.append("\n");
          }
          stringBuilder.append(cMIUser.getName());
        }
      }
      RawMessage banRawMessage = new RawMessage();
      banRawMessage
          .addText(LC.modify_deleteSymbol.getLocale(new Object[0]))
          .addHover(
              LC.modify_deleteSymbolHover.getLocale(new Object[] {"[text]", banEntry.getTarget()}))
          .addCommand(
              String.valueOf(CommandsHandler.getLabel())
                  + " "
                  + unban.class.getSimpleName()
                  + " "
                  + banEntry.getTarget()
                  + (bl ? " -s" : ""))
          .build();
      banRawMessage
          .addText(
              " "
                  + CMILC.getIM(
                      "ipbanlist",
                      "banList",
                      "[place]",
                      pageInfo.getPositionForOutput(),
                      "[ip]",
                      banEntry.getTarget(),
                      "[reason]",
                      banEntry.getReason()))
          .addHover(
              String.valueOf(CMILC.getIM("ipbanlist", "bannedBy", "[source]", banEntry.getSource()))
                  + (banEntry.getExpiration() == null
                      ? ""
                      : "\n"
                          + CMILC.getIM(
                              "ipbanlist",
                              "ends",
                              "[time]",
                              DateFormat.MiliToDate(banEntry.getExpiration().getTime())))
                  + (stringBuilder.toString().isEmpty()
                      ? ""
                      : "\n"
                          + CMILC.getIM(
                              "ipbanlist", "related", "[list]", stringBuilder.toString())))
          .addInsertion(banEntry.getTarget())
          .addCommand(
              String.valueOf(CommandsHandler.getLabel())
                  + " "
                  + checkban.class.getSimpleName()
                  + " "
                  + banEntry.getTarget())
          .build();
      banRawMessage.show(commandSender);
    }
    pageInfo.autoPagination(
        commandSender,
        String.valueOf(CommandsHandler.getLabel())
            + " "
            + this.getClass().getSimpleName()
            + (bl ? " -s" : "")
            + " ",
        "-p:");
    return true;
  }
}
