package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class actionbarmsg implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "[message]");
  }

  @CAnnotation(
      info = "Sends action bar message to player",
      args = "[playerName/all] (-s:[seconds]) [message]",
      tab = "playername",
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      ignoreHelpPage = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Player player = null;
    int value = 0;
    String text = "";
    String[] parts = strings;
    int count = strings.length;

    for (int countSecond = 0; countSecond < count; countSecond++) {
      String textSecond = parts[countSecond];
      if (string == null) {
        string = textSecond;
      } else {
        if (textSecond.startsWith("-s:")) {
          try {
            value = Integer.parseInt(textSecond.substring("-s:".length()));
            continue;
          } catch (Throwable throwable) {
          }
        }

        if (!text.isEmpty()) {
          text = text + " ";
        }

        text = text + textSecond;
      }
    }

    if (string == null) {
      return false;
    }

    if (!string.equalsIgnoreCase("all")) {
      player = CMIUser.getOnlinePlayer(string, true);
      if (player == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }
    }

    if (PermissionsManager.CMIPerm.actionbar_colors.hasPermission(commandSender)) {
      text = CMIChatColor.translate(text);
    }

    text = cmi.getPlaceholderAPIManager().updatePlaceHolders(player, text);
    if (player != null) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      text = cmi.getLM().updateSnd(snd, text);
      CMIActionBar.send(
          player, CMILC.getIM(this, "message", "[messsage]", text, "[message]", text), value);
    } else {
      ArrayList<Player> arrayList = new ArrayList<>(Bukkit.getOnlinePlayers());
      if (arrayList.isEmpty()) {
        return null;
      }

      Snd sndSecond = new Snd().setSender(commandSender).setTarget((Player) arrayList.get(0));
      String textThird = cmi.getLM().updateSnd(sndSecond, text);
      if (textThird.equals(text)) {
        CMIActionBar.send(
            new ArrayList(Bukkit.getOnlinePlayers()),
            CMILC.getIM(this, "message", "[messsage]", text, "[message]", text),
            value);
      } else {
        for (Player playerSecond : arrayList) {
          sndSecond.setTarget(playerSecond);
          String textFourth = cmi.getLM().updateSnd(sndSecond, text);
          CMIActionBar.send(
              playerSecond,
              CMILC.getIM(this, "message", "[messsage]", textFourth, "[message]", textFourth),
              value);
        }
      }
    }

    return true;
  }
}
