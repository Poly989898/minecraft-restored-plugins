package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIStaffMessageEvent;
import java.util.HashSet;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class staffmsg implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "message", "{gcu}[Staff]{gcd}[{gcs}[senderDisplayName]{gcd}]&r{gcw} [message]");
    configReader.get(
        "toggledOn",
        "{gcp}A persistent staff chat has been started. You can now write messages in public"
            + " chat.");
    configReader.get("toggledOff", "{gcp}Toggled off Persistent staff chat");
  }

  @CAnnotation(
      info = "Sends message to staff channel",
      args = "[message/toggle/on/off]",
      tab = "toggle%%on%%off",
      regVar = -100,
      consoleVar = -100,
      ignoreHelpPage = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + text;
    }

    if (string.equalsIgnoreCase("toggle") && commandSender instanceof Player) {
      UUID uuid = ((Player) commandSender).getUniqueId();
      if (!cmi.getChatFormatManager().getStaffChats().contains(uuid)) {
        cmi.getChatFormatManager().getStaffChats().add(uuid);
        CMILC.info(this, commandSender, "toggledOn");
      } else {
        cmi.getChatFormatManager().getStaffChats().remove(uuid);
        CMILC.info(this, commandSender, "toggledOff");
      }

      return true;
    } else {
      if (string.equalsIgnoreCase("on") && commandSender instanceof Player) {
        UUID uUIDSecond = ((Player) commandSender).getUniqueId();
        cmi.getChatFormatManager().getStaffChats().add(uUIDSecond);
        CMILC.info(this, commandSender, "toggledOn");
        return true;
      }

      if (string.equalsIgnoreCase("off") && commandSender instanceof Player) {
        UUID uUIDThird = ((Player) commandSender).getUniqueId();
        cmi.getChatFormatManager().getStaffChats().remove(uUIDThird);
        CMILC.info(this, commandSender, "toggledOff");
        return true;
      }

      sendStaffMsg(commandSender, null, string);
      if (cmi.getChatFormatManager().isBungeeStaffMessages()) {
        cmi.getBungeeCordManager().sendStaffMessage(commandSender.getName(), string);
      }

      return true;
    }
  }

  public static void sendStaffMsg(CommandSender commandSender, String string, String text) {
    Snd snd = new Snd();
    if (commandSender != null) {
      snd.setSender(commandSender);
      snd.setTarget(commandSender);
    }

    if (string != null) {
      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(string);
      if (cmiUser != null) {
        snd.setSender(cmiUser);
        snd.setTarget(cmiUser);
      } else {
        snd.setSenderName(string);
      }
    }

    String textSecond = commandSender != null ? commandSender.getName() : string;
    String textThird =
        CMILC.getIM(
            staffmsg.class.getSimpleName(),
            "message",
            snd,
            "[message]",
            CMIChatColor.stripColor(text));
    textThird = CMIChatColor.translate(textThird);
    HashSet<Player> hashSet = new HashSet();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (PermissionsManager.CMIPerm.command_$1.hasPermission(
              player, staffmsg.class.getSimpleName())
          || player.getName() == textSecond) {
        hashSet.add(player);
      }
    }

    CMIStaffMessageEvent cmiStaffMessageEvent =
        new CMIStaffMessageEvent(commandSender, hashSet, textThird);
    Bukkit.getPluginManager().callEvent(cmiStaffMessageEvent);
    if (!cmiStaffMessageEvent.isCancelled()) {
      textThird = cmiStaffMessageEvent.getMessage();

      for (Player playerSecond : cmiStaffMessageEvent.getReceivers()) {
        RawMessage rawMessage = new RawMessage();
        String textFourth = CMILC.Chat_staffHover.getLocale();
        CMIUser cMIUserSecond =
            CMI.getInstance()
                .getPlayerManager()
                .getUser(commandSender instanceof Player ? (Player) commandSender : null);
        if (cMIUserSecond == null && textSecond != null) {
          cMIUserSecond = CMI.getInstance().getPlayerManager().getUser(textSecond);
        }

        textFourth =
            commandSender instanceof Player
                ? CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders((Player) commandSender, textFourth)
                : (cMIUserSecond != null
                    ? CMI.getInstance()
                        .getPlaceholderAPIManager()
                        .updatePlaceHolders(cMIUserSecond.getUniqueId(), textFourth)
                    : CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(textFourth));
        if (CMI.getInstance().getChatFormatManager().containsUrl(text)) {
          rawMessage
              .addText(CMILC.getIM(staffmsg.class.getSimpleName(), "message", snd, "[message]", ""))
              .addHover(textFourth);
          ChatFormatManager.chatClickAction.staffmsg.updateClickAction(rawMessage, cMIUserSecond);
          CMI.getInstance()
              .getChatFormatManager()
              .convertHoverOver(
                  rawMessage,
                  text,
                  commandSender instanceof Player ? (Player) commandSender : null);
        } else {
          rawMessage
              .addText(
                  CMILC.getIM(staffmsg.class.getSimpleName(), "message", snd, "[message]", text))
              .addHover(textFourth);
          ChatFormatManager.chatClickAction.staffmsg.updateClickAction(rawMessage, cMIUserSecond);
        }

        rawMessage.show(playerSecond);
      }

      CMIMessages.consoleMessage(textThird);
    }
  }
}
