package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.BanEntry;
import org.bukkit.command.CommandSender;

public class ipbanlist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("banList", "{gcs}[ip] {gcp}- {gcs}[reason]  ");
    configReader.get("bannedBy", "{gcp}Banned by: {gcs}[source]");
    configReader.get("ends", "{gcp}Ends: {gcs}[time]");
    configReader.get("related", "{gcp}Related accounts:\n{gcs}[list]");
  }

  @CAnnotation(
      info = "Ban ip list",
      regVar = {0, 1},
      consoleVar = {0, 1})
  @Override
  public Boolean perform(CMI cmi, final CommandSender commandSender, String[] strings) {
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (string.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(string.replace("-p:", ""));
        } catch (Exception exception) {
        }
      }
    }

    if (integer == null) {
      integer = 1;
    }

    var set = CMIBanRecords.getBanEntriesByIP().getBanEntries();
    if (set.isEmpty()) {
      LC.info_nothingToShow.sendMessage(commandSender, new Object[0]);
      return true;
    }

    PageInfo pageInfo = new PageInfo(10, set.size(), integer);
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    ChatMessageObjectEdit chatMessageObjectEdit =
        new ChatMessageObjectEdit(commandSender, pageInfo);
    chatMessageObjectEdit.setAddNew(false);

    for (BanEntry banEntry : set) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (!pageInfo.isContinue()) {
        final BanEntry currentBanEntry = banEntry;
        StringBuilder stringBuilder = new StringBuilder();
        int userCount = 0;
        var users = cmi.getIpManager().getUsers(currentBanEntry.getTarget());
        if (users != null) {
          for (CMIUser cmiUser : users) {
            if (cmiUser.getName() != null) {
              userCount++;
              if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.append(LC.info_ListSpliter.get(new Object[0]));
              }

              if (userCount > 5) {
                userCount = 0;
                stringBuilder.append("\n");
              }

              stringBuilder.append(cmiUser.getName());
            }
          }
        }

        String targetName = currentBanEntry.getTarget();
        ChatEditorObject chatEditorObject =
            new ChatEditorObject(
                CMILC.getIM(
                    this,
                    "banList",
                    "[place]",
                    pageInfo.getPositionForOutput(),
                    "[ip]",
                    targetName,
                    "[reason]",
                    currentBanEntry.getReason())) {
              public void onDelete() {
                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " "
                            + unban.class.getSimpleName()
                            + " "
                            + currentBanEntry.getTarget());
              }
            };
        chatEditorObject.setHover(
            CMILC.getIM(this, "bannedBy", "[source]", currentBanEntry.getSource())
                + (currentBanEntry.getExpiration() == null
                    ? ""
                    : "\n"
                        + CMILC.getIM(
                            this,
                            "ends",
                            "[time]",
                            DateFormat.MiliToDate(currentBanEntry.getExpiration().getTime())))
                + (stringBuilder.toString().isEmpty()
                    ? ""
                    : "\n" + CMILC.getIM(this, "related", "[list]", stringBuilder.toString())));
        chatMessageObjectEdit.addline(chatEditorObject);
      }
    }

    chatMessageObjectEdit.print();
    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
    return true;
  }
}
