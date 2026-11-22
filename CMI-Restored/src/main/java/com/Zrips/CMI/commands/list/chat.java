package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFormat.CMIChatRoom;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class chat implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("joined", "{gcp}Joined chat room: {gcs}[chatRoomName]");
    configReader.get("joinedWatcher", "{gcp}Observing chat room: {gcs}[chatRoomName]");
    configReader.get("stopeedWatching", "{gcp}Stopped observing chat room: {gcs}[chatRoomName]");
    configReader.get("watcherSymbol", "◕‿◕");
    configReader.get("watcherSymbolActive", "{gcu}");
    configReader.get("watcherSymbolInactive", "{gcn}");
    configReader.get("joinedInfo", "{gcs}[playerDisplayName] {gcp}joined chat room");
    configReader.get("alreadyIn", "{gcp}You are already in {gcs}[chatRoomName] {gcp}chat room");
    configReader.get("left", "{gcp}Left {gcs}[chatRoomName] {gcp}chat room");
    configReader.get("leftInfo", "{gcs}[playerDisplayName] {gcp}left chat room");
    configReader.get(
        "invite",
        "{gcs}[playerDisplayName] {gcp}invites you to join chat room: {gcs}[chatRoomName]");
    configReader.get(
        "sentInvite",
        "{gcp}Invited {gcs}[playerDisplayName] {gcp}to join chat room: {gcs}[chatRoomName]");
    configReader.get(
        "format", "{gcn}|{gcs}[chatRoomName]{gcn}| {gcs}[playerDisplayName]: &r[message]");
    configReader.get("nobody", "{gcw}Nobody is in chat room");
    configReader.get("notOwner", "{gcw}Only chat owner can do this");
    configReader.get("wrongName", "{gcw}Wrong chat room name");
    configReader.get("takenName", "{gcw}Chat room name is taken, use different one");
    configReader.get("notIn", "{gcw}User is not in chat room");
    configReader.get("cantJoin", "{gcw}Chat room is private and you don't have invitation");
    configReader.get(
        "cantSee", "{gcw}Chat room is private, you don't have permission to see messages in it");
    configReader.get("kicked", "{gcs}[playerDisplayName] {gcp}kicked from chat room");
    configReader.get("cantleave", "{gcp}Can't leave locked room");
    configReader.get("gotKicked", "{gcw}You got kicked from {gcs}[chatRoomName] {gcw}chat room");
    configReader.get("list", "{gcp}[chatRoomName]{gcp}: {gcs}[list]");
    configReader.get(
        "globalList",
        "{gcs}[number]. [joined]{gcp}[chatRoomName][private]{gcp}: {gcs}[count][expires]");
    configReader.get("globalListPrivate", "{gcn}(P)");
    configReader.get("globalListJoined", "{gcu}+");
    configReader.get("globalListHover.Owner", "{gcp}Owner: {gcs}[playerDisplayName]");
    configReader.get("globalListHover.Ends", " {gcp}Expires in: {gcs}[time]");
    configReader.get("globalListHover.Names", "{gcs}[playerDisplayName]");
  }

  @CAnnotation(
      priority = 48,
      info = "Create and join chat rooms",
      args =
          "[create/join/leave/list/invite/kick/listrooms] (chatName/playerName) (-private)"
              + " (-locked) (-persistent)",
      multiTab = {
        "create [playername] -private,-locked,-persistent -private,-locked,-persistent"
            + " -private,-locked,-persistent",
        "join,see,unsee [chatroom]",
        "leave [chatroom]",
        "list",
        "invite [playername]",
        "kick [playername]",
        "listrooms"
      },
      regVar = {1, 2, 3, 4, 5},
      consoleVar = 2,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    chat.Actions actions = null;
    String string = null;
    boolean flag = false;
    boolean silent = false;
    boolean broadcast = false;
    int value = 1;
    Player player = null;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-private")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("-locked")) {
        silent = true;
      } else if (arg.equalsIgnoreCase("-persistent")) {
        broadcast = true;
      } else {
        if (arg.startsWith("-p:")) {
          try {
            value = Integer.parseInt(arg.replace("-p:", ""));
            continue;
          } catch (Exception exception) {
          }
        }

        if (actions == null) {
          actions = chat.Actions.getByName(arg);
          if (actions != null) {
            continue;
          }
        }

        if (player == null) {
          Player targetPlayer = CMIUser.getOnlinePlayer(arg, true);
          if (targetPlayer != null) {
            player = targetPlayer;
            continue;
          }
        }

        string = arg;
      }
    }

    if (actions == null) {
      return false;
    }

    if (player == null && commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    switch (actions) {
      case create:
        if (string == null) {
          return false;
        }

        if (!PermissionsManager.CMIPerm.command_chat_create.hasPermission(commandSender, true)
            || flag
                && !PermissionsManager.CMIPerm.command_chat_create_private.hasPermission(
                    commandSender, true)) {
          return null;
        }

        if (silent
            && !PermissionsManager.CMIPerm.command_chat_create_locked.hasPermission(
                commandSender, true)) {
          return null;
        }

        if (broadcast
            && !PermissionsManager.CMIPerm.command_chat_create_persistent.hasPermission(
                commandSender, true)) {
          return null;
        }

        if (cmi.getChatFormatManager().getChatRoom(string) != null) {
          CMILC.info(this, commandSender, "takenName");
          return null;
        }

        if (cmiUser == null) {
          return true;
        }

        if (cmiUser.getChatRoom() != null) {
          if (cmiUser.getChatRoom().isLocked()
              && !cmiUser.getChatRoom().isOwner(cmiUser.getUniqueId())
              && !PermissionsManager.CMIPerm.command_chat_leave_locked.hasPermission(
                  cmiUser.getPlayer(false))) {
            CMILC.info(this, commandSender, "cantleave");
            return null;
          }

          Snd snd = new Snd();
          snd.setSender(player);
          snd.setTarget(player);
          cmiUser.getChatRoom().informLeave(cmiUser);
          CMILC.info(
              this,
              commandSender,
              "left",
              "[chatRoomName]",
              cmiUser.getChatRoom().getChatName(),
              snd);
        }

        cmiUser.leaveChatRoom();
        cmi.getChatFormatManager().createChatRoom(cmiUser, string, flag, silent, broadcast);
        cmiUser.getChatRoom().informJoin(cmiUser);
        Snd chatSound = new Snd();
        chatSound.setSender(player);
        chatSound.setTarget(player);
        CMILC.info(
            this,
            commandSender,
            "joined",
            "[chatRoomName]",
            cmiUser.getChatRoom().getChatName(),
            chatSound);
        break;
      case join:
        if (string == null) {
          return false;
        }

        CMIChatRoom cmiChatRoom = cmi.getChatFormatManager().getChatRoom(string);
        if (cmiChatRoom == null) {
          CMILC.info(this, commandSender, "wrongName");
          return null;
        }

        if (!PermissionsManager.CMIPerm.command_chat_joinbypass.hasPermission(commandSender)
            && cmiChatRoom.isPrivate()
            && !cmiChatRoom.isOwner(cmiUser.getUniqueId())
            && !cmiChatRoom.isInvited(cmiUser.getUniqueId())) {
          CMILC.info(this, commandSender, "cantJoin");
          return null;
        }

        if (cmiUser.getChatRoom() != null) {
          if (cmiUser.getChatRoom().equals(cmiChatRoom)) {
            CMILC.info(
                this,
                commandSender,
                "alreadyIn",
                "[chatRoomName]",
                cmiUser.getChatRoom().getChatName());
            return null;
          }

          if (cmiUser.getChatRoom().isLocked()
              && !cmiUser.getChatRoom().isOwner(cmiUser.getUniqueId())
              && !PermissionsManager.CMIPerm.command_chat_leave_locked.hasPermission(
                  cmiUser.getPlayer(false))) {
            CMILC.info(this, commandSender, "cantleave");
            return null;
          }

          Snd roomSound = new Snd();
          roomSound.setSender(player);
          roomSound.setTarget(player);
          cmiUser.getChatRoom().informLeave(cmiUser);
          CMILC.info(
              this,
              commandSender,
              "left",
              "[chatRoomName]",
              cmiUser.getChatRoom().getChatName(),
              roomSound);
        }

        cmiUser.leaveChatRoom();
        cmi.getChatFormatManager().joinChatRoom(cmiUser, string);
        cmiUser.getChatRoom().informJoin(cmiUser);
        Snd kickSound = new Snd();
        kickSound.setSender(player);
        kickSound.setTarget(player);
        CMILC.info(
            this,
            commandSender,
            "joined",
            "[chatRoomName]",
            cmiUser.getChatRoom().getChatName(),
            kickSound);
        break;
      case leave:
        Snd leaveSound = new Snd();
        leaveSound.setSender(player);
        leaveSound.setTarget(player);
        if (cmiUser.getChatRoom() != null) {
          if (cmiUser.getChatRoom().isLocked()
              && !cmiUser.getChatRoom().isOwner(cmiUser.getUniqueId())
              && !PermissionsManager.CMIPerm.command_chat_leave_locked.hasPermission(
                  cmiUser.getPlayer(false))) {
            CMILC.info(
                this,
                commandSender,
                "cantleave",
                "[chatRoomName]",
                cmiUser.getChatRoom().getChatName(),
                leaveSound);
            return null;
          }

          cmiUser.getChatRoom().informLeave(cmiUser);
          CMILC.info(
              this,
              commandSender,
              "left",
              "[chatRoomName]",
              cmiUser.getChatRoom().getChatName(),
              leaveSound);
        }

        cmiUser.leaveChatRoom();
        return true;
      case list:
        if (cmiUser.getChatRoom() == null) {
          return false;
        }

        if (!PermissionsManager.CMIPerm.command_chat_list.hasPermission(commandSender, true)) {
          return null;
        }

        String roomList = "";
        cmiUser.getChatRoom().cleanOldUsers();

        for (CMIUser member : cmiUser.getChatRoom().getUsers()) {
          if (!roomList.isEmpty()) {
            roomList = roomList + LC.info_ListSpliter.getLocale(new Object[0]);
          }

          roomList =
              roomList
                  + (member.isOnline() && player != null && player.canSee(member.getPlayer())
                      ? "&7"
                      : "&8")
                  + member.getName();
        }

        CMILC.info(
            this,
            commandSender,
            "list",
            "[list]",
            roomList,
            "[chatRoomName]",
            cmiUser.getChatRoom().getChatName());
        return true;
      case invite:
        if (cmiUser == null) {
          return false;
        }

        if (!PermissionsManager.CMIPerm.command_chat_invite.hasPermission(commandSender, true)) {
          return null;
        }

        if (!cmiUser.isOnline()
                && cmi.getBungeeCordManager().isBungeeCord()
                && !cmi.getBungeeCordManager().isPlayerOnAnotherServer(string)
            || cmiUser.isOnline() && player != null && !player.canSee(cmiUser.getPlayer())) {
          LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
          return null;
        }

        CMIUser roomUser = null;
        if (commandSender instanceof Player) {
          roomUser = cmi.getPlayerManager().getUser((Player) commandSender);
        }

        if (roomUser == null || roomUser.getChatRoom() == null) {
          return false;
        }

        if (cmiUser.isOnline()
            && (cmiUser.getChatRoom() == null
                || !cmiUser
                    .getChatRoom()
                    .getChatName()
                    .equals(roomUser.getChatRoom().getChatName()))) {
          if (roomUser.getChatRoom() != null
              && roomUser.getChatRoom().isOwner(roomUser.getUniqueId())) {
            roomUser.getChatRoom().addInvitation(cmiUser.getUniqueId());
          }

          Snd joinSound = new Snd();
          joinSound.setTarget(commandSender);
          joinSound.setSender(commandSender);
          RawMessage rawMessage = new RawMessage();
          rawMessage
              .addText(
                  CMILC.getIM(
                      this,
                      "invite",
                      "[chatRoomName]",
                      roomUser.getChatRoom().getChatName(),
                      joinSound))
              .addHover(LC.info_Click.getLocale(new Object[0]))
              .addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + chat.class.getSimpleName()
                      + " join "
                      + roomUser.getChatRoom().getChatName());
          rawMessage.show(cmiUser.getPlayer());
          Snd inviteSound = new Snd(cmiUser.getPlayer(), cmiUser.getPlayer());
          CMILC.info(
              this,
              commandSender,
              "sentInvite",
              "[chatRoomName]",
              roomUser.getChatRoom().getChatName(),
              inviteSound);
        }
        break;
      case kick:
        if (cmiUser == null) {
          return false;
        }

        if (cmiUser.getChatRoom() == null) {
          CMILC.info(this, commandSender, "notIn");
          return null;
        }

        if (commandSender instanceof Player
            && !cmiUser.getChatRoom().isOwner(((Player) commandSender).getUniqueId())) {
          CMILC.info(this, commandSender, "notOwner");
          return null;
        }

        if (!PermissionsManager.CMIPerm.command_chat_kick.hasPermission(commandSender, true)) {
          return null;
        }

        CMIChatRoom room = cmiUser.getChatRoom();
        boolean kicked = room.kick(cmiUser);
        Snd muteSound = new Snd();
        muteSound.setSender(commandSender);
        muteSound.setTarget(cmiUser);
        if (kicked) {
          CMILC.info(this, commandSender, "kicked", muteSound);
          CMILC.info(this, cmiUser, "gotKicked", "[chatRoomName]", room.getChatName(), muteSound);
        } else {
          CMILC.info(this, commandSender, "notIn");
        }
        break;
      case listrooms:
        return this.listRooms(commandSender, cmiUser, value);
      case force:
        if (string == null && commandSender instanceof Player) {
          CMIChatRoom targetRoom =
              cmi.getPlayerManager().getUser((Player) commandSender).getChatRoom();
          if (targetRoom != null) {
            string = targetRoom.getChatName();
          }
        }

        if (string == null) {
          return false;
        }

        CMIChatRoom namedRoom = cmi.getChatFormatManager().getChatRoom(string);
        if (namedRoom == null) {
          CMILC.info(this, commandSender, "wrongName");
          return null;
        }

        if (!PermissionsManager.CMIPerm.command_chat_force.hasPermission(commandSender, true)) {
          return null;
        }

        if (cmiUser.getChatRoom() != null) {
          if (cmiUser.getChatRoom().equals(namedRoom)) {
            CMILC.info(
                this,
                commandSender,
                "alreadyIn",
                "[chatRoomName]",
                cmiUser.getChatRoom().getChatName());
            return null;
          }

          Snd unmuteSound = new Snd();
          unmuteSound.setSender(player);
          unmuteSound.setTarget(player);
          cmiUser.getChatRoom().informLeave(cmiUser);
          CMILC.info(
              this,
              player,
              "left",
              "[chatRoomName]",
              cmiUser.getChatRoom().getChatName(),
              unmuteSound);
        }

        cmiUser.leaveChatRoom();
        cmi.getChatFormatManager().joinChatRoom(cmiUser, string);
        cmiUser.getChatRoom().informJoin(cmiUser);
        Snd joinSnd = new Snd();
        joinSnd.setSender(player);
        joinSnd.setTarget(player);
        CMILC.info(
            this, player, "joined", "[chatRoomName]", cmiUser.getChatRoom().getChatName(), joinSnd);
        break;
      case see:
        return this.see(commandSender, cmiUser, string);
      case unsee:
        return this.unsee(commandSender, cmiUser, string);
    }

    return true;
  }

  private Boolean see(CommandSender commandSender, CMIUser cmiUser, String string) {
    if (string == null) {
      return false;
    } else {
      CMIChatRoom cmiChatRoom = CMI.getInstance().getChatFormatManager().getChatRoom(string);
      if (cmiChatRoom == null) {
        CMILC.info(this, commandSender, "wrongName");
        return null;
      } else if (!PermissionsManager.CMIPerm.command_chat_see.hasPermission(commandSender, true)) {
        return null;
      } else if (!PermissionsManager.CMIPerm.command_chat_seebypass.hasPermission(commandSender)
          && cmiChatRoom.isPrivate()
          && !cmiChatRoom.isOwner(cmiUser.getUniqueId())) {
        CMILC.info(this, commandSender, "cantSee");
        return null;
      } else if (cmiUser.getChatRoom() != null && cmiUser.getChatRoom().equals(cmiChatRoom)) {
        CMILC.info(
            this,
            commandSender,
            "alreadyIn",
            "[chatRoomName]",
            cmiUser.getChatRoom().getChatName());
        return null;
      } else {
        cmiChatRoom.addWatcher(cmiUser);
        Snd snd = new Snd();
        snd.setSender(cmiUser);
        snd.setTarget(cmiUser);
        CMILC.info(
            this, commandSender, "joinedWatcher", "[chatRoomName]", cmiChatRoom.getChatName(), snd);
        return true;
      }
    }
  }

  private Boolean unsee(CommandSender commandSender, CMIUser cmiUser, String string) {
    if (string == null) {
      return false;
    }

    CMIChatRoom cmiChatRoom = CMI.getInstance().getChatFormatManager().getChatRoom(string);
    if (cmiChatRoom == null) {
      CMILC.info(this, commandSender, "wrongName");
      return null;
    }

    if (!PermissionsManager.CMIPerm.command_chat_see.hasPermission(commandSender, true)) {
      return null;
    }

    if (cmiUser.getChatRoom() != null && cmiUser.getChatRoom().equals(cmiChatRoom)) {
      return false;
    }

    cmiChatRoom.removeWatcher(cmiUser);
    Snd snd = new Snd();
    snd.setSender(cmiUser);
    snd.setTarget(cmiUser);
    CMILC.info(
        this, commandSender, "stopeedWatching", "[chatRoomName]", cmiChatRoom.getChatName(), snd);
    return true;
  }

  private Boolean listRooms(CommandSender commandSender, final CMIUser cmiUser, final int value) {
    if (!PermissionsManager.CMIPerm.command_chat_listrooms.hasPermission(commandSender, true)) {
      return null;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    PageInfo pageInfo =
        new PageInfo(10, CMI.getInstance().getChatFormatManager().getChatRooms().size(), value);
    CMIChatRoom cmiChatRoom = null;
    if (commandSender instanceof Player) {
      cmiChatRoom =
          CMI.getInstance().getPlayerManager().getUser((Player) commandSender).getChatRoom();
    }

    boolean flag = PermissionsManager.CMIPerm.command_chat_see.hasPermission(commandSender, false);

    for (var entry : CMI.getInstance().getChatFormatManager().getChatRooms().entrySet()) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (!pageInfo.isContinue()) {
        final CMIChatRoom listedRoom = (CMIChatRoom) entry.getValue();
        String string = "";
        listedRoom.cleanOldUsers();
        int usersOnLine = 1;
        int hiddenCount = 0;

        for (CMIUser roomUser : listedRoom.getUsers()) {
          if (!string.isEmpty()) {
            string = string + LC.info_ListSpliter.getLocale(new Object[0]);
          }

          if (!roomUser.isVanished()
              || roomUser.isOnline()
                  && (!(commandSender instanceof Player)
                      || ((Player) commandSender).canSee(roomUser.getPlayer()))) {
            usersOnLine++;
            Snd snd = new Snd();
            snd.setSender(commandSender);
            snd.setTarget(roomUser);
            string = string + CMILC.getIM(this, "globalListHover.Names", snd);
            if (usersOnLine > 5) {
              usersOnLine = 1;
              string = string + " \n";
            }
          } else {
            hiddenCount++;
          }
        }

        String expiresText =
            !listedRoom.isPersistent() && listedRoom.getKeepAliveUntil() > 0L
                ? CMITimeManager.to24hourShort(
                    listedRoom.getKeepAliveUntil() - System.currentTimeMillis())
                : "";
        String privateText = listedRoom.isPrivate() ? CMILC.getIM(this, "globalListPrivate") : "";
        RawMessage rawMessage = new RawMessage();
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(
            CMILC.getIM(
                this,
                "globalListHover.Owner",
                CMI.getInstance().getPlayerManager().getUser(listedRoom.getOwner())));
        if (!string.isEmpty()) {
          arrayList.add(string);
        }

        rawMessage.addText(
            CMILC.getIM(
                this,
                "globalList",
                "[number]",
                pageInfo.getPositionForOutput() < 10
                    ? "0" + pageInfo.getPositionForOutput()
                    : pageInfo.getPositionForOutput(),
                "[chatRoomName]",
                listedRoom.getChatName(),
                "[private]",
                privateText,
                "[joined]",
                cmiChatRoom == entry.getValue() ? CMILC.getIM(this, "globalListJoined") : "",
                "[count]",
                listedRoom.getUsers().isEmpty() ? "-" : listedRoom.getUsers().size() - hiddenCount,
                "[expires]",
                expiresText.isEmpty()
                    ? ""
                    : CMILC.getIM(this, "globalListHover.Ends", "[time]", expiresText)));
        rawMessage.addHover(arrayList);
        rawMessage.addCommand(
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " join "
                + (String) entry.getKey());
        if (cmiChatRoom != entry.getValue() && flag) {
          if (listedRoom.getWatchers().contains(cmiUser)) {
            RawMessageCommand rawMessageCommand =
                new RawMessageCommand() {
                  public void run(CommandSender sender) {
                    chat.this.unsee(sender, cmiUser, listedRoom.getChatName());
                    chat.this.listRooms(sender, cmiUser, value);
                  }
                };
            rawMessage.addText(
                " "
                    + CMILC.getIM(this, "watcherSymbolActive")
                    + CMILC.getIM(this, "watcherSymbol"));
            rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
            rawMessage.addCommand(rawMessageCommand);
          } else {
            RawMessageCommand seeCommand =
                new RawMessageCommand() {
                  public void run(CommandSender sender) {
                    chat.this.see(sender, cmiUser, listedRoom.getChatName());
                    chat.this.listRooms(sender, cmiUser, value);
                  }
                };
            rawMessage.addText(
                " "
                    + CMILC.getIM(this, "watcherSymbolInactive")
                    + CMILC.getIM(this, "watcherSymbol"));
            rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
            rawMessage.addCommand(seeCommand);
          }
        }

        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel() + " " + this.getClass().getSimpleName() + " listrooms",
        "-p:");
    return true;
  }

  private enum Actions {
    create,
    join,
    leave,
    list,
    invite,
    kick,
    listrooms,
    force,
    see,
    unsee;

    public static chat.Actions getByName(String string) {
      chat.Actions[] actionses;
      int value = (actionses = values()).length;

      for (int actionIndex = 0; actionIndex < value; actionIndex++) {
        chat.Actions actions = actionses[actionIndex];
        if (actions.name().equalsIgnoreCase(string)) {
          return actions;
        }
      }

      return null;
    }
  }
}
