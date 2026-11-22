package com.Zrips.CMI.Modules.BungeeCord;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.Portals.CMIPortal;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.broadcast;
import com.Zrips.CMI.commands.list.portals;
import com.Zrips.CMI.commands.list.staffmsg;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCordListener implements PluginMessageListener, Listener {
  public static final String incomingChannel = "cmib:fromproxy";
  public static final String outgoingChannel = "cmib:fromserver";
  CMI plugin;
  HashMap<UUID, BungeeCordListener.BungeeTeleport> tpMap = new HashMap<>();
  HashMap<String, Long> m = new HashMap<>();
  Long lastMessageTime = 0L;
  Long lastStaffMessageTime = 0L;
  Long lastPrivateMessageTime = 0L;
  String lastMessage = "";
  String lastStaffMessage = "";
  String lastPrivateMessage = "";

  public BungeeCordListener(CMI cmi) {
    this.plugin = cmi;
    cmi.getServer().getMessenger().registerOutgoingPluginChannel(cmi, "cmib:fromserver");
    cmi.getServer().getMessenger().registerIncomingPluginChannel(cmi, "cmib:fromproxy", this);
    cmi.getServer().getMessenger().registerIncomingPluginChannel(cmi, "BungeeCord", this);
    cmi.getServer().getMessenger().registerOutgoingPluginChannel(cmi, "BungeeCord");
    cmi.getBungeeCordManager().updateServers();
    this.plugin.getServer().getPluginManager().registerEvents(this, cmi);
  }

  public void onPluginMessageReceived(String string, Player player, byte[] bs) {
    if (this.plugin.getBungeeCordManager().isEnabledSupport()) {
      if (string.equals("BungeeCord")) {
        this.plugin.getBungeeCordManager().setBungeeCord(true);
        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bs);
        String subChannel = byteArrayDataInput.readUTF();
        String subChannelKey = subChannel;
        switch (subChannel.hashCode()) {
          case -1500810727:
            if (subChannelKey.equals("GetServer")) {
              try {
                String serverName = byteArrayDataInput.readUTF();
                this.plugin.getBungeeCordManager().setThisServerName(serverName);
              } catch (Error | Exception value) {
                value.printStackTrace();
              }

              return;
            }
            break;
          case -205896897:
            if (subChannelKey.equals("PlayerList")) {
              try {
                String listServerName = byteArrayDataInput.readUTF();
                String playerNames = byteArrayDataInput.readUTF();
                if (playerNames.isEmpty()) {
                  return;
                }

                BungeeCordServer bungeeCordServer =
                    this.plugin.getBungeeCordManager().getServer(listServerName);
                if (bungeeCordServer == null) {
                  bungeeCordServer = new BungeeCordServer(listServerName, null, null);
                  this.plugin.getBungeeCordManager().addServer(bungeeCordServer);
                }

                String[] strings;
                int playerCount = (strings = playerNames.split(", ")).length;

                for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
                  String playerName = strings[playerIndex];
                  BungeePlayer bungeePlayer = bungeeCordServer.getPlayer(playerName);
                  if (bungeePlayer == null) {
                    bungeePlayer = new BungeePlayer();
                    bungeePlayer.setBungeeServer(bungeeCordServer);
                    bungeePlayer.setName(playerName);
                    bungeeCordServer.addPlayer(bungeePlayer);
                  }
                }
              } catch (Throwable throwable) {
              }

              return;
            }
            break;
          case 719507834:
            if (subChannelKey.equals("GetServers")) {
              try {
                String[] serverNameParts = byteArrayDataInput.readUTF().split(", ");
                String[] serverNames = serverNameParts;
                int serverCount = serverNameParts.length;

                for (int serverIndex = 0; serverIndex < serverCount; serverIndex++) {
                  String serverItem = serverNames[serverIndex];
                  if (this.plugin.getBungeeCordManager().getServer(serverItem) == null) {
                    this.plugin
                        .getBungeeCordManager()
                        .addServer(new BungeeCordServer(serverItem, null, null));
                  }
                }

                this.plugin.getBungeeCordManager().updateServersIp();
              } catch (Error | Exception value6) {
                value6.printStackTrace();
              }

              return;
            }
            break;
          case 1186775061:
            if (subChannelKey.equals("UUIDOther")) {
              try {
                String targetPlayerName = byteArrayDataInput.readUTF();
                String uuidString = byteArrayDataInput.readUTF();
                BungeePlayer targetBungeePlayer =
                    this.plugin.getBungeeCordManager().getBungeePlayer(targetPlayerName);
                if (targetBungeePlayer != null) {
                  try {
                    UUID uuid =
                        new UUID(
                            new BigInteger(uuidString.substring(0, 16), 16).longValue(),
                            new BigInteger(uuidString.substring(16), 16).longValue());
                    targetBungeePlayer.setUniqueId(uuid);
                  } catch (Throwable throwable2) {
                    UUID fallbackUuid = UUID.fromString(uuidString);
                    targetBungeePlayer.setUniqueId(fallbackUuid);
                  }
                }
              } catch (Throwable throwable3) {
                throwable3.printStackTrace();
              }

              return;
            }
            break;
          case 1443747786:
            if (subChannelKey.equals("ServerIP")) {
              try {
                String ipServerName = byteArrayDataInput.readUTF();
                String serverIp = byteArrayDataInput.readUTF();
                int serverPort = byteArrayDataInput.readUnsignedShort();
                BungeeCordServer ipServer =
                    this.plugin.getBungeeCordManager().getServer(ipServerName);
                if (ipServer != null) {
                  ipServer.setIp(serverIp);
                  ipServer.setPort(serverPort);
                  ipServer.update();
                }
              } catch (Error | Exception value8) {
                value8.printStackTrace();
              }

              return;
            }
        }

        try {
          short shortValue = byteArrayDataInput.readShort();
          byte[] payload = new byte[shortValue];
          byteArrayDataInput.readFully(payload);
          DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(payload));
          String subChannelCopy = subChannel;
          switch (subChannel.hashCode()) {
            case -1868872060:
              if (subChannelCopy.equals("CMIStaffSubChannel")) {
                try {
                  String senderName = dataInputStream.readUTF();
                  String staffMessage = dataInputStream.readUTF();
                  Long sentAt = dataInputStream.readLong();
                  if (sentAt + 1000L < System.currentTimeMillis()) {
                    return;
                  }

                  if (Bukkit.getPlayerExact(senderName) != null) {
                    return;
                  }

                  if (this.lastStaffMessage.equals(staffMessage)
                      && this.lastStaffMessageTime + 100L > System.currentTimeMillis()) {
                    return;
                  }

                  this.lastStaffMessageTime = System.currentTimeMillis();
                  this.lastStaffMessage = staffMessage;
                  staffMessage = CTextManager.clean(staffMessage);
                  staffmsg.sendStaffMsg(null, senderName, staffMessage);
                } catch (Error | Exception value9) {
                  value9.printStackTrace();
                }

                return;
              }
              break;
            case -1143564211:
              if (subChannelCopy.equals("CMIPortalCSetSubChannel")) {
                try {
                  String sourceServer = dataInputStream.readUTF();
                  String skippedField = dataInputStream.readUTF();
                  String portalName = dataInputStream.readUTF();
                  String locationString = dataInputStream.readUTF();
                  String playerName = dataInputStream.readUTF();
                  if (this.lastMessage.equals(portalName + locationString)
                      && this.lastMessageTime + 50L > System.currentTimeMillis()) {
                    return;
                  }

                  this.lastMessageTime = System.currentTimeMillis();
                  this.lastMessage = portalName + locationString;
                  CMILC.info(
                      portals.class.getSimpleName(),
                      this.plugin.getPlayerManager().getUser(playerName),
                      "bungeeSetConfirmed",
                      "[sourceServer]",
                      sourceServer,
                      "[portal]",
                      portalName,
                      "[location]",
                      LC.Location_Full.getLocale(
                          new Object[] {
                            this.plugin.getPlayerManager().convertStringToLocation(locationString)
                          }));
                } catch (IOException ioException) {
                  ioException.printStackTrace();
                }

                return;
              }
              break;
            case 370405029:
              if (subChannelCopy.equals("CMIBroadcastSubChannel")) {
                try {
                  String skippedSender = dataInputStream.readUTF();
                  String broadcastMessage = dataInputStream.readUTF();
                  Long sentAt = dataInputStream.readLong();
                  if (sentAt + 1000L < System.currentTimeMillis()) {
                    return;
                  }

                  broadcastMessage = CTextManager.clean(broadcastMessage);
                  this.plugin
                      .getCommandManager()
                      .performCMICommand(
                          Bukkit.getConsoleSender(), broadcast.class, broadcastMessage);
                } catch (Error | Exception value10) {
                  value10.printStackTrace();
                }

                return;
              }
              break;
            case 535939755:
              if (subChannelCopy.equals("CMIMessageSubChannel")) {
                try {
                  String chatSender = dataInputStream.readUTF();
                  String chatSource = dataInputStream.readUTF();
                  String chatMessage = dataInputStream.readUTF();
                  Long sentAt = dataInputStream.readLong();
                  if (sentAt + 1000L < System.currentTimeMillis()) {
                    return;
                  }

                  if (this.lastPrivateMessage.equals(chatMessage)
                      && this.lastPrivateMessageTime + 100L > System.currentTimeMillis()) {
                    return;
                  }

                  this.lastPrivateMessageTime = System.currentTimeMillis();
                  this.lastPrivateMessage = chatMessage;
                  chatMessage = CTextManager.clean(chatMessage);
                  this.plugin
                      .getChatFormatManager()
                      .sendMessage(chatSender, chatSource, chatMessage);
                } catch (Error | Exception value11) {
                  value11.printStackTrace();
                }

                return;
              }
              break;
            case 1135271706:
              if (subChannelCopy.equals("CMIChatSubChannel")) {
                try {
                  String chatPlayerName = dataInputStream.readUTF();
                  String chatText = dataInputStream.readUTF();
                  Long sentAt = dataInputStream.readLong();
                  if (sentAt + 1000L < System.currentTimeMillis()) {
                    return;
                  }

                  if (Bukkit.getPlayerExact(chatPlayerName) != null) {
                    return;
                  }

                  if (this.lastMessage.equals(chatText)
                      && this.lastMessageTime + 50L > System.currentTimeMillis()) {
                    return;
                  }

                  this.lastMessageTime = System.currentTimeMillis();
                  this.lastMessage = chatText;
                  chatText = CTextManager.clean(chatText);
                  this.plugin
                      .getChatFormatManager()
                      .broadcastBungeeMessage(chatPlayerName, chatText);
                } catch (Error | Exception value12) {
                  value12.printStackTrace();
                }

                return;
              }
              break;
            case 1942448261:
              if (subChannelCopy.equals("CMIPlayerFeedback")) {
                try {
                  String feedbackServerName = dataInputStream.readUTF();
                  String feedbackData = dataInputStream.readUTF();
                  BungeeCordServer feedbackServer =
                      this.plugin.getBungeeCordManager().getServer(feedbackServerName);
                  if (feedbackServer == null) {
                    return;
                  }

                  String[] feedbackParts = feedbackData.split("::");
                  String feedbackPlayerName = feedbackParts[0];
                  String feedbackDisplayName = feedbackParts[1];
                  String feedbackNickName = feedbackParts[2];
                  UUID feedbackUuid = UUID.fromString(feedbackParts[3]);
                  boolean flag = feedbackParts[4].equalsIgnoreCase("1");
                  BungeePlayer feedbackPlayer = feedbackServer.getPlayer(feedbackPlayerName);
                  if (feedbackPlayer == null) {
                    feedbackPlayer = new BungeePlayer();
                    feedbackPlayer.setBungeeServer(feedbackServer);
                    feedbackPlayer.setName(feedbackPlayerName);
                    feedbackPlayer.setUniqueId(feedbackUuid);
                    feedbackServer.addPlayer(feedbackPlayer);
                  }

                  feedbackPlayer.setPlayerDisplayName(feedbackDisplayName);
                  feedbackPlayer.setVanished(flag);
                  feedbackPlayer.setNickName(feedbackNickName);
                } catch (Exception | Error value13) {
                  value13.printStackTrace();
                }

                return;
              }
          }
        } catch (Exception | Error value14) {
        }
      }

      if (string.equalsIgnoreCase("cmib:fromproxy")) {
        this.plugin.getBungeeCordManager().setBungeeCord(true);
        if (!this.plugin.getBungeeCordManager().isCMIBPresent()) {
          this.plugin.getBungeeCordManager().setCMIBPresent(true);
        }

        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bs);
        String subChannel = byteArrayDataInput.readUTF();
        String subChannelKey = subChannel;
        switch (subChannel.hashCode()) {
          case -2043348914:
            if (subChannelKey.equals("CMITalksWith")) {
              try {
                String replySender = byteArrayDataInput.readUTF();
                String replyTarget = byteArrayDataInput.readUTF();
                this.plugin.getUtilManager().addMessageReplyToBySender(replyTarget, replySender);
              } catch (Error | Exception value15) {
                value15.printStackTrace();
              }

              return;
            }
            break;
          case -1789231293:
            if (subChannelKey.equals("CMIPlayerListFeedback")) {
              try {
                String listServerName = byteArrayDataInput.readUTF();
                String playerListData = byteArrayDataInput.readUTF();
                BungeeCordServer listServer =
                    this.plugin.getBungeeCordManager().getServer(listServerName);
                if (listServer == null) {
                  listServer = new BungeeCordServer(listServerName, null, null);
                  this.plugin.getBungeeCordManager().addServer(listServer);
                }

                if (playerListData.isEmpty()) {
                  listServer.clearPlayers();
                } else {
                  String[] playerEntries;
                  int entryCount = (playerEntries = playerListData.split(";;")).length;

                  for (int entryIndex = 0; entryIndex < entryCount; entryIndex++) {
                    String playerEntry = playerEntries[entryIndex];
                    String[] entryParts = playerEntry.split("::");
                    String entryPlayerName = entryParts[0];
                    String entryDisplayName = entryParts[1];
                    UUID entryUuid = UUID.fromString(entryParts[2]);
                    boolean entryVanished = entryParts[3].equalsIgnoreCase("1");
                    BungeePlayer entryPlayer = listServer.getPlayer(entryPlayerName);
                    if (entryPlayer == null) {
                      entryPlayer = new BungeePlayer();
                      entryPlayer.setBungeeServer(listServer);
                      entryPlayer.setName(entryPlayerName);
                      entryPlayer.setUniqueId(entryUuid);
                      listServer.addPlayer(entryPlayer);
                    }

                    entryPlayer.setPlayerDisplayName(entryDisplayName);
                    entryPlayer.setVanished(entryVanished);
                  }
                }
              } catch (Throwable throwable4) {
                throwable4.printStackTrace();
              }

              return;
            }
            break;
          case -598517219:
            if (subChannelKey.equals("PlayerDisconnectEvent")) {
              String disconnectUuidString = byteArrayDataInput.readUTF();
              String disconnectPlayerName = byteArrayDataInput.readUTF();
              String disconnectServerName = byteArrayDataInput.readUTF();
              UUID disconnectUuid = UUID.fromString(disconnectUuidString);
              CMIUser cmiUser = this.plugin.getPlayerManager().getUser(disconnectUuid);
              String disconnectDisplayName =
                  cmiUser == null ? disconnectUuid.toString() : cmiUser.getName();
              if (disconnectServerName.equalsIgnoreCase("unknown")) {
                for (var entry : this.plugin.getBungeeCordManager().getServers().entrySet()) {
                  ((BungeeCordServer) entry.getValue())
                      .removePlayer(disconnectUuid, disconnectDisplayName);
                }
              } else {
                BungeeCordServer disconnectServer =
                    this.plugin.getBungeeCordManager().getServer(disconnectServerName);
                if (disconnectServer != null) {
                  disconnectServer.removePlayer(disconnectUuid, disconnectPlayerName);
                }
              }
            }
            break;
          case -274112870:
            if (subChannelKey.equals("CMIBungeeCommandEvent")) {
              String commandUuidString = byteArrayDataInput.readUTF();
              String ignoredFieldFirst = byteArrayDataInput.readUTF();
              String ignoredFieldSecond = byteArrayDataInput.readUTF();

              UUID commandUuid;
              try {
                commandUuid = UUID.fromString(commandUuidString);
              } catch (Throwable throwable5) {
                throwable5.printStackTrace();
                return;
              }

              CMIUser commandUser = this.plugin.getPlayerManager().getUser(commandUuid);
              if (commandUser == null) {
                return;
              }

              boolean wasAfk = commandUser.isAfk();
              if (this.plugin.getAfkManager().isDisableOnCommand()) {
                CMIScheduler.runTaskLater(
                    this.plugin,
                    () -> {
                      CMIUser afkUser = this.plugin.getPlayerManager().getUser(player);
                      if (afkUser != null) {
                        if (wasAfk && afkUser.isAfk()) {
                          afkUser.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                        }

                        this.plugin.getAfkManager().updateUser(afkUser);
                      }
                    },
                    1L);
              }
            }
            break;
          case -202772719:
            if (subChannelKey.equals("CMIPlayerListRequest")) {
              try {
                String requestId = byteArrayDataInput.readUTF();
                StringBuilder stringBuilder = new StringBuilder();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                  if (!stringBuilder.toString().isEmpty()) {
                    stringBuilder.append(";;");
                  }

                  CMIUser onlineUser = this.plugin.getPlayerManager().getUser(onlinePlayer);
                  stringBuilder.append(
                      onlineUser.getName()
                          + "::"
                          + onlineUser.getDisplayName(false)
                          + "::"
                          + onlineUser.getUniqueId()
                          + "::"
                          + (onlineUser.isVanished() ? 1 : 0));
                }

                ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
                byteArrayDataOutput.writeUTF("CMIPlayerListFeedback");
                byteArrayDataOutput.writeUTF(
                    this.plugin.getBungeeCordManager().getThisServerName());
                byteArrayDataOutput.writeUTF(requestId);
                byteArrayDataOutput.writeUTF(stringBuilder.toString());
                Player firstPlayer = BungeeCordManager.getFirstPlayer();
                if (firstPlayer == null) {
                  return;
                }

                firstPlayer.sendPluginMessage(
                    this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
              } catch (Throwable throwable6) {
                throwable6.printStackTrace();
              }

              return;
            }
            break;
          case -69864099:
            if (subChannelKey.equals("CMINetworkInfo")) {
              String networkName = byteArrayDataInput.readUTF();
              this.plugin
                  .getBungeeCordManager()
                  .setNetworkType(CMIBungeeType.getByName(networkName));
              CMIMessages.consoleMessage(
                  CMI.getInstance().prefix
                      + "&3CMI detected &6"
                      + this.plugin.getBungeeCordManager().getNetworkType()
                      + " &3network");
              this.plugin.getBungeeCordManager().sendServerInformationToBungee();
            }
            break;
          case -29559482:
            if (subChannelKey.equals("ServerListFeedback")) {
              try {
                String thisServerName = byteArrayDataInput.readUTF();
                String serverInfoData = byteArrayDataInput.readUTF();
                String[] serverInfoParts = serverInfoData.split(";:", 4);
                if (serverInfoParts.length < 4) {
                  return;
                }

                String serverInfoName = serverInfoParts[0];
                String serverInfoIp = serverInfoParts[1];
                int serverInfoPort = Integer.parseInt(serverInfoParts[2]);
                String serverInfoMotd = serverInfoParts[3];
                BungeeCordServer infoServer =
                    this.plugin.getBungeeCordManager().getServer(serverInfoName);
                if (infoServer == null) {
                  infoServer = new BungeeCordServer(serverInfoName, serverInfoIp, serverInfoPort);
                } else {
                  infoServer.setIp(serverInfoIp);
                  infoServer.setPort(serverInfoPort);
                }

                infoServer.setMotd(serverInfoMotd);
                this.plugin.getBungeeCordManager().addServer(infoServer);
                this.plugin.getBungeeCordManager().setThisServerName(thisServerName);
                BungeeCordManager.gotServerList = true;
              } catch (Throwable throwable7) {
                throwable7.printStackTrace();
              }
            }
            break;
          case 6731833:
            if (subChannelKey.equals("CMITeleportChannel")) {
              String teleportServerName = byteArrayDataInput.readUTF();
              UUID teleportUuid = UUID.fromString(byteArrayDataInput.readUTF());
              String teleportLocationString = byteArrayDataInput.readUTF();
              String teleportFrom = byteArrayDataInput.readUTF();
              String ignoredTeleportField = byteArrayDataInput.readUTF();
              Long lastTeleportTime =
                  this.m.get(teleportServerName + teleportUuid.toString() + teleportLocationString);
              if (lastTeleportTime != null
                  && lastTeleportTime + 100L > System.currentTimeMillis()) {
                return;
              }

              this.m.put(
                  teleportServerName + teleportUuid.toString() + teleportLocationString,
                  System.currentTimeMillis());
              if (this.plugin.getBungeeCordManager().getThisServerName() == null) {
                return;
              }

              if (!this.plugin
                  .getBungeeCordManager()
                  .getThisServerName()
                  .equalsIgnoreCase(teleportServerName)) {
                return;
              }

              CMILocation cmiLocation = null;
              if (!teleportLocationString.isEmpty()) {
                cmiLocation =
                    this.plugin.getPlayerManager().convertStringToLocation(teleportLocationString);
                if (cmiLocation == null) {
                  return;
                }
              }

              this.tpMap.put(
                  teleportUuid,
                  new BungeeCordListener.BungeeTeleport()
                      .setLoc(cmiLocation)
                      .setFrom(teleportFrom));
              this.checkTeleportRecords(CMIUser.getOnlinePlayer(teleportUuid));
              return;
            }
            break;
          case 601433082:
            if (subChannelKey.equals("CMIPortalSetSubChannel")) {
              String portalServerName = byteArrayDataInput.readUTF();
              String portalThisServerName = byteArrayDataInput.readUTF();
              String portalName = byteArrayDataInput.readUTF();
              String portalLocation = byteArrayDataInput.readUTF();
              String portalPlayerName = byteArrayDataInput.readUTF();
              if (this.plugin.getBungeeCordManager().getThisServerName() != null
                  && this.plugin
                      .getBungeeCordManager()
                      .getThisServerName()
                      .equalsIgnoreCase(portalThisServerName)) {
                CMIPortal cmiPortal = this.plugin.getPortalManager().getByName(portalName);
                if (cmiPortal == null) {
                  return;
                }

                Long lastPortalSetTime =
                    this.m.get(
                        portalServerName + portalThisServerName + portalName + portalLocation);
                if (lastPortalSetTime != null
                    && lastPortalSetTime + 100L > System.currentTimeMillis()) {
                  return;
                }

                this.m.clear();
                this.m.put(
                    portalServerName + portalThisServerName + portalName + portalLocation,
                    System.currentTimeMillis());
                cmiPortal.setBungeeServer(portalServerName);
                cmiPortal.setBungeeLocation(portalLocation);
                this.plugin.getPortalManager().savePortals();
                BungeeCordServer playerServer =
                    this.plugin.getBungeeCordManager().getPlayerServer(portalPlayerName);
                if (playerServer == null) {
                  return;
                }

                String playerServerName = playerServer.getName();
                CMIScheduler.runTaskLater(
                    this.plugin,
                    () ->
                        this.plugin
                            .getBungeeCordManager()
                            .sendPortalSetSuccessResponse(
                                portalPlayerName, playerServerName, portalName, portalLocation),
                    5L);
                return;
              }

              return;
            }
            break;
          case 913885476:
            if (subChannelKey.equals("CMIServerSwitchEvent")) {
              String switchUuidString = byteArrayDataInput.readUTF();
              String switchPlayerName = byteArrayDataInput.readUTF();
              String switchPreviousServer = byteArrayDataInput.readUTF();
              String switchToServer = byteArrayDataInput.readUTF();
              UUID switchUuid = UUID.fromString(switchUuidString);
              CMIUser switchUser = this.plugin.getPlayerManager().getUser(switchUuid);
              if (switchUser == null) {
                switchUuid.toString();
              } else {
                switchUser.getName();
              }

              for (var serverEntry : this.plugin.getBungeeCordManager().getServers().entrySet()) {
                ((BungeeCordServer) serverEntry.getValue())
                    .removePlayer(switchUuid, switchPlayerName);
              }

              BungeeCordServer toServer =
                  this.plugin.getBungeeCordManager().getServer(switchToServer);
              if (toServer == null) {
                toServer = new BungeeCordServer(switchToServer, null, null);
                this.plugin.getBungeeCordManager().addServer(toServer);
              }

              BungeePlayer switchPlayer = toServer.getPlayer(switchUuid);
              if (switchPlayer == null) {
                switchPlayer = new BungeePlayer();
                switchPlayer.setUniqueId(switchUuid);
                switchPlayer.setName(switchPlayerName);
              }

              switchPlayer.setBungeeServer(toServer);
              switchPlayer.setPreviousServer(
                  switchPreviousServer.isEmpty() ? null : switchPreviousServer);
              switchPlayer.setServerSwitchTime(
                  switchPreviousServer.isEmpty() ? 0L : System.currentTimeMillis());
              toServer.addPlayer(switchPlayer);
              if (this.plugin.getBungeeCordManager().isBackToPreviousServer()
                  && switchToServer.equalsIgnoreCase(
                      this.plugin.getBungeeCordManager().getThisServerName())
                  && switchUser != null) {
                CMIScheduler.runTaskLater(
                    this.plugin,
                    () -> switchUser.setBungeeBackLocation(switchPreviousServer + ";Unknown"),
                    20L);
              }
            }
            break;
          case 968195795:
            if (subChannelKey.equals("ServerConnectEvent")) {
              String connectUuidString = byteArrayDataInput.readUTF();
              String connectPlayerName = byteArrayDataInput.readUTF();
              String connectServerName = byteArrayDataInput.readUTF();
              UUID connectUuid = UUID.fromString(connectUuidString);
              CMIUser connectUser = this.plugin.getPlayerManager().getUser(connectUuid);
              if (connectUser != null) {
                connectUser.getName();
              }

              for (var connectServerEntry :
                  this.plugin.getBungeeCordManager().getServers().entrySet()) {
                ((BungeeCordServer) connectServerEntry.getValue())
                    .removePlayer(connectUuid, connectPlayerName);
              }

              BungeeCordServer connectTargetServer =
                  this.plugin.getBungeeCordManager().getServer(connectServerName);
              if (connectTargetServer == null) {
                connectTargetServer = new BungeeCordServer(connectServerName, null, null);
                this.plugin.getBungeeCordManager().addServer(connectTargetServer);
              }

              BungeePlayer connectPlayer = connectTargetServer.getPlayer(connectUuid);
              if (connectPlayer == null) {
                connectPlayer = new BungeePlayer();
                connectPlayer.setUniqueId(connectUuid);
                connectPlayer.setName(connectPlayerName);
              }

              connectPlayer.setBungeeServer(connectTargetServer);
              connectTargetServer.addPlayer(connectPlayer);
            }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (this.plugin.getBungeeCordManager().isEnabledSupport()
        && this.plugin.getBungeeCordManager().isBungeeCord()) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            this.plugin.getBungeeCordManager().sendServerListRequest();
            this.plugin.getBungeeCordManager().sendPlayerListRequest();
          },
          10L);
      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            this.checkTeleportRecords(player);
            this.plugin.getBungeeCordManager().withWhomHeTalks(player.getName());
          },
          5L);
      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            if (player.isOnline()) {
              this.plugin.getBungeeCordManager().sendNewPlayerInfoToNetwork(player);
            }

            this.plugin.getBungeeCordManager().updateServers();
          },
          40L);
    }
  }

  private void checkTeleportRecords(Player player) {
    if (player != null) {
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null) {
        cmiUser.setBungeeBackLocation(null);
      }

      BungeeCordListener.BungeeTeleport bungeeTeleport = this.tpMap.remove(player.getUniqueId());
      if (bungeeTeleport != null
          && player.isOnline()
          && bungeeTeleport.getTime() + 2000L >= System.currentTimeMillis()) {
        if (cmiUser != null) {
          CMIScheduler.runTaskLater(
              this.plugin, () -> cmiUser.setBungeeBackLocation(bungeeTeleport.getFrom()), 30L);
        }

        if (bungeeTeleport.getLoc() != null) {
          this.plugin
              .getTeleportHandler()
              .teleportPlayer(player, bungeeTeleport.getLoc(), CMITeleportType.Bungee);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {}

  class BungeeTeleport {
    private Location loc;
    private String from;
    private long time = 0L;

    public BungeeTeleport() {
      this.time = System.currentTimeMillis();
    }

    public Location getLoc() {
      return this.loc;
    }

    public BungeeCordListener.BungeeTeleport setLoc(Location location) {
      this.loc = location;
      return this;
    }

    public long getTime() {
      return this.time;
    }

    public String getFrom() {
      return this.from;
    }

    public BungeeCordListener.BungeeTeleport setFrom(String string) {
      this.from = string;
      return this;
    }
  }
}
