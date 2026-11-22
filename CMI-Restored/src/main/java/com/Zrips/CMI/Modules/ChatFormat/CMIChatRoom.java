package com.Zrips.CMI.Modules.ChatFormat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.chat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;

public class CMIChatRoom {
  private Set<CMIUser> users = new HashSet<>();
  private Set<CMIUser> watchers = new HashSet<>();
  private String chatName;
  private UUID owner;
  private boolean priv = false;
  private boolean locked = false;
  private boolean persistent = false;
  private Set<UUID> invitations;
  private long keepAliveUntil = 0L;

  public CMIChatRoom(String string) {
    this.chatName = string;
  }

  public String getChatName() {
    return this.chatName;
  }

  public void setChatName(String string) {
    this.chatName = string;
  }

  public Set<CMIUser> getUsers() {
    return this.users;
  }

  public Set<CMIUser> getWatchers() {
    return this.watchers;
  }

  public Set<UUID> getWatchersUUID() {
    HashSet<UUID> hashSet = new HashSet();

    for (CMIUser cmiUser : this.getWatchers()) {
      hashSet.add(cmiUser.getUniqueId());
    }

    return hashSet;
  }

  public void addUser(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.users.add(cmiUser);
      cmiUser.setChatRoom(this);
      this.keepAliveUntil = 0L;
    }
  }

  public boolean removeUser(CMIUser cmiUser) {
    if (cmiUser == null) {
      return false;
    }

    cmiUser.setChatRoom(null);
    boolean flag = this.users.remove(cmiUser);
    if (this.users.isEmpty() && !this.persistent) {
      this.keepAliveUntil = System.currentTimeMillis() + ChatFormatManager.ChatRoomLife * 1000L;
    } else {
      this.keepAliveUntil = System.currentTimeMillis() + 604800000L;
    }

    return flag;
  }

  public void addWatcher(CMIUser cmiUser) {
    this.watchers.add(cmiUser);
  }

  public boolean removeWatcher(CMIUser cmiUser) {
    return this.watchers.remove(cmiUser);
  }

  public void broadcastMessage(CMIUser cmiUser, String string) {
    this.cleanOldUsers();
    if (cmiUser != null) {
      Snd snd = new Snd();
      snd.setSender(cmiUser);
      snd.setTarget(cmiUser);
      if (!PermissionsManager.CMIPerm.command_socialspy_hide.hasPermission(cmiUser.getPlayer())) {
        CMI.getInstance()
            .getPlayerManager()
            .sendChatRoomMessageToSpies(cmiUser.getPlayer(), this.getChatName(), string);
      }

      if (this.users.size() == 1) {
        CMILC.info(chat.class, cmiUser.getPlayer(), "nobody");
      } else {
        RawMessage rawMessage = new RawMessage();
        rawMessage.setDontBreakLine(true);
        String text = CMILC.Chat_privateHover.getLocale();
        text =
            CMI.getInstance()
                .getPlaceholderAPIManager()
                .updatePlaceHolders(cmiUser.getPlayer(), text);
        if (CMI.getInstance().getChatFormatManager().containsUrl(string)) {
          String textSecond =
              CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(
                      cmiUser.getPlayer(),
                      CMILC.getIM(
                          chat.class,
                          "format",
                          snd,
                          "[chatRoomName]",
                          this.getChatName(),
                          "[message]",
                          ""));
          rawMessage.addText(textSecond).addHover(text);
          ChatFormatManager.chatClickAction.helpop.updateClickAction(rawMessage, cmiUser);
          CMI.getInstance()
              .getChatFormatManager()
              .convertHoverOver(rawMessage, string, cmiUser.getPlayer());
        } else {
          rawMessage
              .addText(
                  CMI.getInstance()
                      .getPlaceholderAPIManager()
                      .updatePlaceHolders(
                          cmiUser.getPlayer(),
                          CMILC.getIM(
                              chat.class,
                              "format",
                              snd,
                              "[chatRoomName]",
                              this.getChatName(),
                              "[message]",
                              string)))
              .addHover(text);
          ChatFormatManager.chatClickAction.helpop.updateClickAction(rawMessage, cmiUser);
        }

        HashSet<org.bukkit.entity.Player> hashSet = new HashSet();

        for (CMIUser cMIUserSecond : new HashSet<>(this.users)) {
          if (cMIUserSecond.isOnline()) {
            hashSet.add(cMIUserSecond.getPlayer());
          }
        }

        HashSet<org.bukkit.entity.Player> set = new HashSet();

        for (CMIUser cMIUserThird : new HashSet<>(this.watchers)) {
          if (cMIUserThird.isOnline()) {
            set.add(cMIUserThird.getPlayer());
          }
        }

        if (!set.isEmpty()) {
          RawMessage rawMessageSecond = new RawMessage();
          rawMessageSecond.setDontBreakLine(true);
          rawMessageSecond.addText(
              CMILC.getIM(chat.class, "watcherSymbolActive")
                  + CMILC.getIM(chat.class, "watcherSymbol"));
          rawMessageSecond.addRM(rawMessage);
          rawMessageSecond.show(set);
        }

        rawMessage.show(hashSet);
        rawMessage.show(Bukkit.getConsoleSender());
      }
    }
  }

  public void cleanOldUsers() {
    for (CMIUser cmiUser : new HashSet<>(this.users)) {
      if (!cmiUser.isOnline() && cmiUser.getLastLogoff() + 60000L <= System.currentTimeMillis()) {
        this.removeUser(cmiUser);
      }
    }
  }

  public void informLeave(CMIUser cmiUser) {
    Snd snd = new Snd();
    snd.setSender(cmiUser);
    snd.setTarget(cmiUser);
    String string = CMILC.getIM(chat.class, "leftInfo", snd);

    for (CMIUser cMIUserSecond : new HashSet<>(this.users)) {
      if (cMIUserSecond.isOnline() && !cMIUserSecond.equals(cmiUser)) {
        cMIUserSecond.sendMessage(string);
      }
    }

    for (CMIUser cMIUserThird : new HashSet<>(this.watchers)) {
      if (cMIUserThird.isOnline() && !cMIUserThird.equals(cmiUser)) {
        cMIUserThird.sendMessage(string);
      }
    }
  }

  public void informJoin(CMIUser cmiUser) {
    Snd snd = new Snd();
    snd.setSender(cmiUser);
    snd.setTarget(cmiUser);
    String string = CMILC.getIM(chat.class, "joinedInfo", snd);

    for (CMIUser cMIUserSecond : new HashSet<>(this.users)) {
      if (cMIUserSecond.isOnline() && !cMIUserSecond.equals(cmiUser)) {
        cMIUserSecond.sendMessage(string);
      }
    }

    for (CMIUser cMIUserThird : new HashSet<>(this.watchers)) {
      if (cMIUserThird.isOnline() && !cMIUserThird.equals(cmiUser)) {
        cMIUserThird.sendMessage(string);
      }
    }
  }

  public UUID getOwner() {
    return this.owner;
  }

  public void setOwner(UUID uuid) {
    this.owner = uuid;
  }

  public boolean isOwner(UUID uuid) {
    return this.owner.equals(uuid);
  }

  public boolean isPrivate() {
    return this.priv;
  }

  public void setPrivate(boolean flag) {
    this.priv = flag;
  }

  public boolean kick(CMIUser cmiUser) {
    boolean flag = this.users.remove(cmiUser);
    if (!flag) {
      return false;
    }

    if (cmiUser.getChatRoom() != null
        && cmiUser.getChatRoom().getChatName().equals(this.getChatName())) {
      cmiUser.setChatRoom(null);
    }

    this.removeInvitation(cmiUser.getUniqueId());
    if (this.users.isEmpty()) {
      this.keepAliveUntil = System.currentTimeMillis() + ChatFormatManager.ChatRoomLife * 1000L;
    } else {
      this.keepAliveUntil = System.currentTimeMillis() + 604800000L;
    }

    return true;
  }

  public Set<UUID> getInvitations() {
    return this.invitations;
  }

  public boolean isInvited(UUID uuid) {
    return this.invitations == null ? false : this.invitations.contains(uuid);
  }

  public void addInvitation(UUID uuid) {
    if (this.invitations == null) {
      this.invitations = new HashSet<>();
    }

    this.invitations.add(uuid);
  }

  public void removeInvitation(UUID uuid) {
    if (this.invitations != null) {
      this.invitations.remove(uuid);
    }
  }

  public long getKeepAliveUntil() {
    return this.keepAliveUntil;
  }

  public void setKeepAliveUntil(long longValue) {
    this.keepAliveUntil = longValue;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public void setLocked(boolean flag) {
    this.locked = flag;
  }

  public boolean isPersistent() {
    return this.persistent;
  }

  public void setPersistent(boolean flag) {
    this.persistent = flag;
  }
}
