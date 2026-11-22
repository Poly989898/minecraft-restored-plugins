package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class broadcast implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("format", "{gcp}[{gcw}Broadcast{gcp}] {gcu}[message]");
  }

  @CAnnotation(
      priority = 48,
      info = "Sends special message to all players",
      args = "(!) [message] (-w:[worldName,worldName]) (-r:[range]) (-c:[world;x;y;z])",
      explanation = {
        "If message starts with ! then clean message will be shown",
        "Worlds can be defined to broadcast messages only in those worlds"
      },
      regVar = -100,
      consoleVar = -100,
      ignoreHelpPage = true,
      others = false,
      customAlias = "bc")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    ArrayList<World> arrayList = new ArrayList();
    if (commandSender instanceof Player) {
      CMIUser cmiUser = cmi.getPlayerManager().getUser((Player) commandSender);
      if (cmiUser.isMuted()
          && Config.MuteNoPm
          && !PermissionsManager.CMIPerm.command_mute_bypass.hasPermission(commandSender)) {
        CMILC.info(
            "mute",
            commandSender,
            "muted",
            "[time]",
            CMITimeManager.to24hourShort(cmiUser.getMutedUntil() - System.currentTimeMillis())
                + (cmiUser.getMutedReason() == null
                    ? ""
                    : CMILC.getIM("mute", "reason", "[reason]", cmiUser.getMutedReason())));
        return null;
      }
    }

    int value = 0;
    CMILocation cmiLocation = null;
    String[] parts = strings;
    int count = strings.length;

    for (int countSecond = 0; countSecond < count; countSecond++) {
      String text = parts[countSecond];
      if (text.startsWith("-w:")) {
        text = text.substring("-w:".length());
        ArrayList<String> items = new ArrayList();
        if (text.contains(",")) {
          items.addAll(Arrays.asList(text.split(",")));
        } else {
          items.add(text);
        }

        for (String textSecond : items) {
          World world = cmi.getUtilManager().getWorld(textSecond);
          if (world != null) {
            arrayList.add(world);
          }
        }
      } else if (text.startsWith("-r:")) {
        try {
          value = Integer.parseInt(text.substring("-r:".length()));
        } catch (Exception exception) {
        }
      } else if (text.startsWith("-c:")) {
        try {
          cmiLocation = CMILocation.fromString(text.substring("-c:".length()));
        } catch (Exception exception2) {
        }
      } else {
        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + text;
      }
    }

    if (PermissionsManager.CMIPerm.command_broadcast_colors.hasPermission(commandSender)) {
      string = CMIChatColor.translate(string);
    }

    Snd snd = new Snd().setSender(commandSender);
    boolean flag = false;
    if (string.startsWith("!")
        && PermissionsManager.CMIPerm.command_broadcast_clean.hasPermission(commandSender)) {
      string = string.substring(1, string.length());
      flag = true;
    }

    if (cmiLocation == null && commandSender instanceof Player) {
      cmiLocation = new CMILocation(((Player) commandSender).getLocation());
    }

    ArrayList<Player> itemsSecond = new ArrayList<>();
    if (cmiLocation != null && value > 0 && cmiLocation.isValid()) {
      itemsSecond.addAll(cmi.getUtilManager().getPlayersFromRange(cmiLocation, value));
    } else {
      itemsSecond.addAll(Bukkit.getOnlinePlayers());
    }

    boolean flagSecond = flag;
    String textThird = string;

    for (Player player : itemsSecond) {
      if (arrayList.isEmpty() || arrayList.contains(player.getWorld())) {
        snd.setTarget(player);
        if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
          RawMessage rawMessage = new RawMessage();
          if (flagSecond) {
            rawMessage.addText(
                cmi.getLM()
                    .updateSnd(
                        snd, cmi.getPlaceholderAPIManager().updatePlaceHolders(player, textThird)));
          } else {
            rawMessage.addText(
                CMILC.getIM(
                    this,
                    "format",
                    "[message]",
                    cmi.getPlaceholderAPIManager().updatePlaceHolders(player, textThird),
                    snd));
          }

          rawMessage.show(player);
        } else if (flagSecond) {
          player.sendMessage(
              cmi.getLM()
                  .updateSnd(
                      snd, cmi.getPlaceholderAPIManager().updatePlaceHolders(player, textThird)));
        } else {
          player.sendMessage(
              CMILC.getIM(
                  this,
                  "format",
                  "[message]",
                  cmi.getPlaceholderAPIManager().updatePlaceHolders(player, textThird),
                  snd));
        }
      }
    }

    textThird = CTextManager.clean(textThird);
    snd.setTarget(Bukkit.getConsoleSender());
    if (flagSecond) {
      CMIMessages.sendMessage(Bukkit.getConsoleSender(), textThird);
    } else {
      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(), CMILC.getIM(this, "format", "[message]", textThird, snd));
    }

    return true;
  }
}
