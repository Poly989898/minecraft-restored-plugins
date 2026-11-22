package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;

public class emojipicker implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("hovers", Arrays.asList("{gcs}Shift+Click"));
  }

  @CAnnotation(
      info = "Pick emoji",
      args = "",
      tab = {},
      explanation = {},
      consoleVar = 666,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_16_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmi.getChatFilterManager().getEmojis().isEmpty()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
    }

    int value = 0;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String string = args[argIndex];
      if (value == 0) {
        try {
          value = Integer.parseInt(string);
        } catch (Throwable throwable) {
        }
      }
    }

    if (value <= 0) {
      value = 1;
    }

    showPage(commandSender, value);
    return true;
  }

  private static void showPage(final CommandSender commandSender, int value) {
    PageInfo pageInfo =
        new PageInfo(100, CMI.getInstance().getChatFilterManager().getEmojis().size(), value) {
          public Boolean pageChange(int page) {
            emojipicker.showPage(commandSender, page);
            return true;
          }
        };
    var iterator = CMI.getInstance().getChatFilterManager().getEmojis().iterator();
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    RawMessage rawMessage = new RawMessage();

    while (iterator.hasNext()) {
      String string = (String) iterator.next();
      if (pageInfo.isBreak()) {
        break;
      }

      if (!pageInfo.isContinue()) {
        rawMessage.addText(
            (pageInfo.getPositionForOutput() % 2 == 0 ? "&7" : "&f") + " " + string + " ");
        rawMessage.addHover(Arrays.asList(string, CMILC.getIM(emojipicker.class, "hovers")));
        rawMessage.addInsertion(string);
        rawMessage.addSuggestion(string);
        if (pageInfo.isBreak()) {
          break;
        }
      }
    }

    rawMessage.show(commandSender);
    pageInfo.autoPagination(
        commandSender, CommandsHandler.getLabel() + " " + emojipicker.class.getSimpleName());
  }
}
