package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMIMessageReplies;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeePlayer;
import com.Zrips.CMI.Modules.ChatFilter.RuleResponse;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class msg implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "TargetMsg", "{gcn}[{gcs}[senderDisplayName] {gcn}-> &8me{gcn}]&r&f [message]");
    configReader.get(
        "SenderMsg", "{gcn}[{gcs}me {gcn}-> {gcs}[playerDisplayName]{gcn}]&r&f [message]");
    configReader.get("noPM", "{gcd}Player is not accepting private messages now");
    configReader.get("badGroup", "{gcd}Can't send private messages to this group of players");
  }

  @CAnnotation(
      priority = 48,
      info = "Sends message to player",
      args = "[playerName] [message]",
      tab = "playername",
      explanation = {
        "If message starts with ! then clean message without sender name will be shown",
        "Requires cmi.command.msg.clean permission",
        "If message starts with !- then clean message without sender name will be shown and without"
            + " option to reply",
        "Requires cmi.command.msg.noreply permission"
      },
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      ignoreHelpPage = true,
      others = false,
      customAlias = "w")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
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
            CMITimeManager.to24hourShort(
                    (cmiUser.isShadowMuted()
                            ? cmiUser.getShadowMutedUntil()
                            : cmiUser.getMutedUntil())
                        - System.currentTimeMillis())
                + (cmiUser.getMutedReason() == null
                    ? ""
                    : CMILC.getIM("mute", "reason", "[reason]", cmiUser.getMutedReason())));
        return null;
      }
    }

    String string = strings[0];
    var list = Arrays.asList(Arrays.copyOfRange(strings, 1, strings.length));
    StringBuilder stringBuilder = new StringBuilder();

    for (String text : list) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(" ");
      }

      stringBuilder.append(text);
    }

    String textSecond = stringBuilder.toString();
    boolean flag = true;
    RuleResponse ruleResponse = null;
    if (commandSender instanceof Player) {
      ruleResponse =
          cmi.getChatFilterManager().getCorrectMessage((Player) commandSender, textSecond, true);
      if (!ruleResponse.getRules().isEmpty()) {
        ruleResponse.informStaff((Player) commandSender);
        ruleResponse.performCommands((Player) commandSender);
        switch (ruleResponse.getMaxFilterBlockType()) {
          case All:
            return null;
          case Others:
            flag = false;
          case None:
          default:
            textSecond = ruleResponse.getUpdatedMessage();
        }
      }
    }

    textSecond = cmi.getChatFormatManager().updateUrl(CMIChatColor.deColorize(textSecond));
    if (cmi.getChatFormatManager().isBungeeMessages()
        && cmi.getBungeeCordManager().isBungeeCord()
        && cmi.getBungeeCordManager().isPlayerOnAnotherServer(string)) {
      if (cmi.getChatManager().isColorsPrivateMessage()) {
        textSecond =
            CMITextCleaner.cleanFromColorCodes(
                commandSender, textSecond, CMIColorTypes.privatemessage, false);
      }

      BungeePlayer bungeePlayer = cmi.getBungeeCordManager().getBungeePlayer(string);
      if (bungeePlayer != null
          && (!(commandSender instanceof Player)
              || !bungeePlayer.getVanished()
              || PermissionsManager.CMIPerm.command_msg_vanish.hasPermission(commandSender))) {
        CMIUser cMIUserSecond = cmi.getPlayerManager().getUser(bungeePlayer.getUniqueId());
        if (cMIUserSecond != null && commandSender instanceof Player) {
          if (cMIUserSecond.isIgnoring(((Player) commandSender).getUniqueId())
              && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
            LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
            return null;
          }

          if (cMIUserSecond.isSilenceMode()
              && !PermissionsManager.CMIPerm.command_silence_bypass.hasPermission(commandSender)) {
            LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
            return null;
          }
        }

        if (flag) {
          cmi.getBungeeCordManager()
              .sendPrivateMessage(commandSender.getName(), bungeePlayer.getName(), textSecond);
        }

        cmi.getChatFormatManager()
            .sendMessage(commandSender.getName(), bungeePlayer.getName(), textSecond);
        return true;
      } else {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }
    } else {
      CMIUser cMIUserThird = cmi.getPlayerManager().getUserByAproxName(string);
      Player player = cMIUserThird == null ? null : cMIUserThird.getPlayer(false);
      if ((cMIUserThird != null
              || strings[0].equalsIgnoreCase("console")
              || strings[0].equalsIgnoreCase(
                  CMIChatColor.stripColor(LC.info_Console.getLocale(new Object[0]))))
          && (player != null
              || strings[0].equalsIgnoreCase("console")
              || strings[0].equalsIgnoreCase(
                  CMIChatColor.stripColor(LC.info_Console.getLocale(new Object[0]))))
          && (player == null || player.isOnline())) {
        if (player != null
            && commandSender instanceof Player
            && !((Player) commandSender).canSee(player)
            && cMIUserThird != null
            && !cMIUserThird.getVanish().is(VanishAction.PrivateMessages)
            && !PermissionsManager.CMIPerm.command_msg_vanish.hasPermission(commandSender)) {
          String textThird = CMIMessageReplies.getMessageReplyTo(commandSender.getName());
          if (textThird == null || !textThird.equalsIgnoreCase(string)) {
            LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
            return null;
          }
        }

        if (player != null
            && cMIUserThird != null
            && commandSender instanceof Player
            && !cMIUserThird.getOptionState(PlayerOption.acceptingPM)
            && !PermissionsManager.CMIPerm.command_msg_togglebypass.hasPermission(commandSender)) {
          CMILC.info(this, commandSender, "noPM");
          return null;
        }

        if (cmi.getChatManager().isPrivateMessagesGroups()
            && cMIUserThird != null
            && cMIUserThird.isOnline()) {
          String textFourth = cMIUserThird.getGroupName();
          if (!textFourth.isEmpty()
              && !PermissionsManager.CMIPerm.command_msg_$1_send.hasPermission(
                  commandSender, textFourth)) {
            CMILC.info(this, commandSender, "badGroup");
            return null;
          }
        }

        if (cmi.getChatManager().isColorsPrivateMessage() || !(commandSender instanceof Player)) {
          textSecond =
              CMITextCleaner.cleanFromColorCodes(
                  commandSender, textSecond, CMIColorTypes.privatemessage, false);
        }

        cmi.getChatFormatManager().sendMessage(commandSender, player, textSecond, flag);
        return true;
      } else {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }
    }
  }
}
