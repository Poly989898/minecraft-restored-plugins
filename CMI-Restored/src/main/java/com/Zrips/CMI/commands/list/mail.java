package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIMail;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.PlayerMail;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import java.util.List;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mail implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("sentMail", "{gcp}Mail sent to {gcs}[playerDisplayName]");
    configReader.get("gotMail", "!actionbar!{gcp}You received mail from {gcs}[senderDisplayName]");
    configReader.get("clearMail", "{gcp}All mail has been cleared!");
    configReader.get("removedMail", "{gcp}Removed mail");
    configReader.get("noMailsById", "{gcp}There is no mail by this id for this player");
    configReader.get("noMails", "{gcp}You have no mail");
    configReader.get("mailHover", "{gcp}[senderName] \n{gcp}[time]");
    configReader.get("mailHoverExpires", "\n{gcp}Expires in: {gcs}[time]");
    configReader.get("mailList", " {gcs}[senderDisplayName]{gcp}: [mail] ");
    configReader.get(
        "waitingMail", "{gcp}You have {gcs}[amount] {gcp}mails. Click to check them out.");
  }

  @CAnnotation(
      info = "Send and receive mail",
      args = "[send/clear/read/sendtemp] [playerName] (time) (message)",
      tab = {"send%%clear%%read%%sendtemp", "playername"},
      explanation = {},
      regVar = -666,
      consoleVar = -100,
      others = true,
      subIgnoreHelpPage = true)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    mail.Action action = null;
    String string = null;
    String text = "";
    Long duration = null;
    int value = 1;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      label230:
      {
        String arg = argsCopy[argIndex];
        if (action == null) {
          String target;
          switch ((target = arg.toLowerCase()).hashCode()) {
            case -934610812:
              if (target.equals("remove")) {
                action = mail.Action.Remove;
                continue;
              }
              break;
            case 3322014:
              if (target.equals("list")) {
                break label230;
              }
              break;
            case 3496342:
              if (target.equals("read")) {
                break label230;
              }
              break;
            case 3526536:
              if (target.equals("send")) {
                action = mail.Action.Send;
                continue;
              }
              break;
            case 94746189:
              if (target.equals("clear")) {
                action = mail.Action.Clear;
                continue;
              }
              break;
            case 1248399196:
              if (target.equals("sendtemp")) {
                action = mail.Action.SendTemp;
                continue;
              }
          }
        }

        if (action != null && action.equals(mail.Action.List) && arg.startsWith("-p:")) {
          try {
            value = Integer.parseInt(arg.substring("-p:".length(), arg.length()));
            continue;
          } catch (NumberFormatException numberFormatException) {
          }
        }

        if (duration == null && action != null && action.equals(mail.Action.SendTemp)) {
          try {
            duration = Long.parseLong(arg);
            continue;
          } catch (Exception exception) {
            try {
              duration = timeModifier.getTimeRangeFromString(arg);
              if (duration != null) {
                continue;
              }
            } catch (Exception exception2) {
            }
          }
        }

        if (text.isEmpty() && string == null && action != null) {
          string = arg;
          continue;
        }

        if (!text.isEmpty()) {
          text = text + " ";
        }

        text = text + arg;
        continue;
      }

      action = mail.Action.List;
    }

    if (action == null) {
      action = mail.Action.List;
    }

    final CMIUser cmiUser =
        cmi.getUser(commandSender, string, this, !action.equals(mail.Action.Send));
    if (cmiUser == null) {
      return null;
    }

    switch (action) {
      case List:
        if (!PermissionsManager.CMIPerm.command_mail_read.hasPermission(commandSender, true)) {
          return null;
        }

        var list = cmiUser.getMails();
        if (list.isEmpty()) {
          CMILC.info(this, commandSender, "noMails");
          return true;
        }

        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        new PageInfo(10, list.size(), value);
        PageInfo pageInfo = new PageInfo(10, list.size(), value);
        ChatMessageObjectEdit chatMessageObjectEdit =
            new ChatMessageObjectEdit(commandSender, pageInfo);
        chatMessageObjectEdit.setAddNew(false);

        for (int index = list.size() - 1; index >= 0; index--) {
          if (pageInfo.isEntryOk()) {
            final PlayerMail playerMail = (PlayerMail) list.get(index);
            Snd snd = new Snd();
            if (playerMail.getSender().equalsIgnoreCase("Console")) {
              snd.setSender(Bukkit.getConsoleSender());
            } else {
              snd.setSender(cmi.getPlayerManager().getUser(playerMail.getSender()));
            }

            snd.setTarget(commandSender);
            snd.setSenderName(playerMail.getSender());
            ChatEditorObject chatEditorObject =
                new ChatEditorObject(
                    CMILC.getIM(this, "mailList", "[mail]", playerMail.getMessage(), snd)) {
                  public void onDelete() {
                    cmiUser.getMails().remove(playerMail);
                    cmi.getCommandManager().performCMICommand(commandSender, mail.class, "");
                  }
                };
            chatEditorObject.setSuggestion(
                "/"
                    + CommandsHandler.getCommandPrefix(this.getClass().getSimpleName())
                    + " send "
                    + playerMail.getSender()
                    + " ");
            String line =
                CMILC.getIM(
                    this, "mailHover", snd, "[time]", DateFormat.MiliToDate(playerMail.getTime()));
            if (playerMail.getKeepFor() > 0L) {
              if (playerMail.getKeepFor() * 1000L + playerMail.getTime()
                  <= System.currentTimeMillis()) {
                cmiUser.addForDelayedSave();
                continue;
              }

              long longValue =
                  playerMail.getKeepFor() * 1000L
                      + playerMail.getTime()
                      - System.currentTimeMillis();
              line =
                  line
                      + CMILC.getIM(
                          this,
                          "mailHoverExpires",
                          snd,
                          "[time]",
                          CMITimeManager.to24hourShort(longValue));
            } else {
              if (CMIMail.getMailExpiresInMS() + playerMail.getTime()
                  <= System.currentTimeMillis()) {
                cmiUser.addForDelayedSave();
                continue;
              }

              long elapsed =
                  CMIMail.getMailExpiresInMS() + playerMail.getTime() - System.currentTimeMillis();
              line =
                  line
                      + CMILC.getIM(
                          this,
                          "mailHoverExpires",
                          snd,
                          "[time]",
                          CMITimeManager.to24hourShort(elapsed));
            }

            chatEditorObject.setHover(line);
            chatMessageObjectEdit.addline(chatEditorObject);
          }
        }

        chatMessageObjectEdit.print();
        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " list "
                + cmiUser.getName(),
            "-p:");
        break;
      case Send:
        if (text.isEmpty()) {
          return false;
        }

        if (!PermissionsManager.CMIPerm.command_mail_send.hasPermission(commandSender, true)) {
          return null;
        }

        if (commandSender instanceof Player
            && cmiUser.isIgnoring(((Player) commandSender).getUniqueId())
            && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
          LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[0]);
          return null;
        }

        if (commandSender instanceof Player
            && !PermissionsManager.CMIPerm.command_socialspy_hide.hasPermission(commandSender)) {
          cmi.getPlayerManager()
              .sendChatRoomMessageToSpies(commandSender, "[Mail] " + cmiUser.getName(), text);
        }

        cmiUser.addMail(new PlayerMail(commandSender, text), true);
        Snd mailSound = new Snd().setSender(commandSender).setTarget(cmiUser);
        CMILC.info(this, commandSender, "sentMail", mailSound);
        if (cmiUser.isOnline()) {
          CMISoundPreset.MailNotification.play(cmiUser.getPlayer(false));
          CMILC.info(this, cmiUser, "gotMail", mailSound);
        }

        cmiUser.addForDelayedSave();
        break;
      case SendTemp:
        if (text.isEmpty() || duration == null || duration < 0L) {
          return false;
        }

        if (!PermissionsManager.CMIPerm.command_mail_sendtemp.hasPermission(commandSender, true)) {
          return null;
        }

        if (commandSender instanceof Player
            && cmiUser.isIgnoring(((Player) commandSender).getUniqueId())
            && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
          LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[0]);
          return null;
        }

        if (commandSender instanceof Player
            && !PermissionsManager.CMIPerm.command_socialspy_hide.hasPermission(commandSender)) {
          cmi.getPlayerManager()
              .sendChatRoomMessageToSpies(commandSender, "[Mail] " + cmiUser.getName(), text);
        }

        cmiUser.addMail(new PlayerMail(commandSender, text, duration), true);
        Snd readSound = new Snd().setSender(commandSender).setTarget(cmiUser);
        CMILC.info(this, commandSender, "sentMail", readSound);
        if (cmiUser.isOnline()) {
          CMISoundPreset.MailNotification.play(cmiUser.getPlayer(false));
          CMILC.info(this, cmiUser, "gotMail", readSound);
        }

        cmiUser.addForDelayedSave();
        break;
      case Clear:
        if (!PermissionsManager.CMIPerm.command_mail_clear.hasPermission(commandSender, true)) {
          return null;
        }

        cmiUser.getMails().clear();
        CMILC.info(this, commandSender, "clearMail");
        cmiUser.addForDelayedSave();
    }

    return true;
  }

  private enum Action {
    List,
    Send,
    SendTemp,
    Clear,
    Remove;
  }
}
