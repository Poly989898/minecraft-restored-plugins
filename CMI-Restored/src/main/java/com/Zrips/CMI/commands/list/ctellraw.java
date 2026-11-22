package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ctellraw implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 48,
      info = "Send tellraw type message",
      args = "[playerName/all] [formattedMessage]",
      explanation =
          "Usage is same as shown in https://www.zrips.net/cmi/custom-text/ aka customtext command",
      multiTab = "[playername],all",
      regVar = -100,
      consoleVar = -100,
      ignoreHelpPage = true,
      others = false,
      asyncSupported = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    StringBuilder stringBuilder = new StringBuilder();
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(" ");
        }

        stringBuilder.append(arg);
      }
    }

    HashSet<Player> hashSet = new HashSet();
    if (string == null) {
      return null;
    }

    if (!string.equalsIgnoreCase("all")) {
      CMIUser cmiUser = cmi.getUser(commandSender, string, this);
      if (cmiUser == null) {
        return null;
      }

      if (!cmiUser.isOnline()) {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }

      hashSet.add(cmiUser.getPlayer(false));
    } else {
      hashSet.addAll(Bukkit.getOnlinePlayers());
    }

    String joinedText = stringBuilder.toString();
    Snd snd = new Snd();
    snd.setSender(commandSender);
    if (!joinedText.contains("%")
        && !joinedText.contains("<CC>")
        && !joinedText.contains("<CCI>")
        && !joinedText.contains("[")
        && !joinedText.contains("]")) {
      RawMessage rawMessage =
          RawMessage.translateRawMessage(null, CMIChatColor.deColorize(joinedText));
      rawMessage.show(hashSet);
      return true;
    }

    for (Player player : hashSet) {
      snd.setTarget(player);
      String translatedText = cmi.getLM().updateSnd(snd, joinedText);
      translatedText = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, translatedText);
      RawMessage.translateRawMessage(player, CMIChatColor.deColorize(translatedText)).show(player);
    }

    return true;
  }
}
