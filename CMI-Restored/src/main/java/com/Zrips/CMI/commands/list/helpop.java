package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class helpop implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "{gcd}[Help]{gcw}[{gcn}[senderDisplayName]{gcw}]&r{gcd} [message]");
    configReader.get("feedback", "{gcd}[Help]{gcn}Your question was submitted to online staff");
    configReader.get(
        "offline", "{gcd}[Help]{gcn}There are no staff members currently online, try later");
  }

  @CAnnotation(
      info = "Sends message for help to staff members",
      args = "[message]",
      explanation = "Requires cmi.command.helpop.inform too see messages",
      regVar = -100,
      consoleVar = -100,
      ignoreHelpPage = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + arg;
    }

    Snd snd = new Snd();
    snd.setSender(commandSender);
    snd.setTarget(commandSender);
    String helpopMessage =
        CMILC.getIM(this, "message", snd, "[message]", CMIChatColor.stripColor(string));
    helpopMessage = CMIChatColor.translate(helpopMessage);
    value = 0;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (PermissionsManager.CMIPerm.command_helpop_inform.hasPermission(player)
          && (!player.getName().equals(commandSender.getName()) || !Config.helpopfeedbackMessage)) {
        RawMessage rawMessage = new RawMessage();
        String hoverText = CMILC.Chat_helpopHover.getLocale();
        hoverText =
            cmi.getPlaceholderAPIManager()
                .updatePlaceHolders(
                    commandSender instanceof Player ? (Player) commandSender : null, hoverText);
        CMIUser cmiUser =
            cmi.getPlayerManager()
                .getUser(commandSender instanceof Player ? (Player) commandSender : null);
        if (cmi.getChatFormatManager().containsUrl(string)) {
          rawMessage
              .addText(CMILC.getIM(this, "message", snd, "[message]", ""))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.helpop.updateClickAction(rawMessage, cmiUser);
          cmi.getChatFormatManager()
              .convertHoverOver(
                  rawMessage,
                  string,
                  commandSender instanceof Player ? (Player) commandSender : null);
        } else {
          rawMessage
              .addText(CMILC.getIM(this, "message", snd, "[message]", string))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.helpop.updateClickAction(rawMessage, cmiUser);
        }

        rawMessage.show(player);
        value++;
        if (CMIUser.getUser(player).isVanished()
            && commandSender instanceof Player
            && !((Player) commandSender).canSee(player)) {
          value--;
        }
      }
    }

    if (value == 0) {
      CMILC.info(this, commandSender, "offline");
      return null;
    }

    if (Config.helpopfeedbackMessage
        && !PermissionsManager.CMIPerm.command_helpop_inform.hasPermission(commandSender)) {
      CMILC.info(this, commandSender, "feedback");
    } else {
      RawMessage staffMessage = new RawMessage();
      String staffHoverText = CMILC.Chat_helpopHover.getLocale();
      staffHoverText =
          cmi.getPlaceholderAPIManager()
              .updatePlaceHolders(
                  commandSender instanceof Player ? (Player) commandSender : null, staffHoverText);
      CMIUser targetUser =
          cmi.getPlayerManager()
              .getUser(commandSender instanceof Player ? (Player) commandSender : null);
      if (cmi.getChatFormatManager().containsUrl(string)) {
        staffMessage
            .addText(CMILC.getIM(this, "message", snd, "[message]", ""))
            .addHover(staffHoverText);
        ChatFormatManager.chatClickAction.helpop.updateClickAction(staffMessage, targetUser);
        cmi.getChatFormatManager()
            .convertHoverOver(
                staffMessage,
                string,
                commandSender instanceof Player ? (Player) commandSender : null);
      } else {
        staffMessage
            .addText(CMILC.getIM(this, "message", snd, "[message]", string))
            .addHover(staffHoverText);
        ChatFormatManager.chatClickAction.helpop.updateClickAction(staffMessage, targetUser);
      }

      staffMessage.show(commandSender);
    }

    CMIMessages.consoleMessage(helpopMessage);
    return true;
  }
}
