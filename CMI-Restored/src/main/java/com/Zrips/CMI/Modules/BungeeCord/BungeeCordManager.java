package com.Zrips.CMI.Modules.BungeeCord;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BungeeCordManager {
  public static final String ServerIP = "ServerIP";
  public static final String GetServer = "GetServer";
  public static final String GetServers = "GetServers";
  public static final String MessageChannel = "CMIMessageSubChannel";
  public static final String ServersPlayerCountsChannel = "CMIServersPlayerCounts";
  public static final String ServerPlayerCountsChannel = "CMIServerPlayerCounts";
  public static final String BroadcastChannel = "CMIBroadcastSubChannel";
  public static final String PublicChatChannel = "CMIChatSubChannel";
  public static final String StaffChatChannel = "CMIStaffSubChannel";
  public static final String PortalSetChannel = "CMIPortalSetSubChannel";
  public static final String PortalConfirmedSetChannel = "CMIPortalCSetSubChannel";
  public static final String TeleportChannel = "CMITeleportChannel";
  public static final String TalksWithChannel = "CMITalksWith";
  public static final String TalksWithRecordChannel = "CMITalksWithRecord";
  public static final String SendToServer = "CMIChangeServer";
  public static final String PlayerListRequest = "CMIPlayerListRequest";
  public static final String PlayerListFeedback = "CMIPlayerListFeedback";
  public static final String NetworkInformation = "CMINetworkInfo";
  public static final String ServerInformation = "CMIServerInfo";
  public static final String PlayerFeedback = "CMIPlayerFeedback";
  public static final String ServerListRequest = "CMIServerListRequest";
  public static UUID localRangedMessage;
  public static long localRangedMessageTime;
  ConcurrentHashMap<String, BungeeCordServer> servers = new ConcurrentHashMap<>();
  private CMI plugin;
  private boolean isBungee = false;
  private boolean CMIBPresent = false;
  public static boolean gotServerList = false;
  private BungeeCordServer thisServer;
  private String thisServerName;
  private boolean enabledSupport = true;
  private boolean backToPreviousServer = true;
  private boolean namesInTabComplete = true;
  private List<String> tabNamesServers = new ArrayList<>();
  private CMIBungeeCord bcord = null;
  private CMIBungeeType type = CMIBungeeType.Unknown;
  Long time = 0L;
  private static final String spacer = "_20_";

  public BungeeCordManager(CMI cmi) {
    this.plugin = cmi;
    this.bcord = CMIBungeeCord.of(cmi);
  }

  public void loadConfig() {
    try {
      ConfigReader configReader = this.plugin.getConfigManager().getConfig();
      configReader.addComment(
          "BungeeCord.Enabled",
          new String[] {
            "You can disable bungeecord support entirely if you are exrperiencing issues with it",
            "When setting this to false some features like public messages over bungee cord,"
                + " private messages over bungeecord, portals over bungecoord and other features"
                + " will stop working",
            "Keep in mind that regular behavior of those features will remain intacted"
          });
      this.enabledSupport = configReader.get("BungeeCord.Enabled", false);
      configReader.addComment(
          "BungeeCord.TabComplete.PlayerNames",
          new String[] {
            "When set to true player names from entire bungee network will be included into tab"
                + " complete"
          });
      this.namesInTabComplete =
          configReader.get(
              "BungeeCord.TabComplete.PlayerNames",
              configReader.getC().getBoolean("BungeeCord.NamesInTabComplete", false));
      configReader.addComment(
          "BungeeCord.TabComplete.Servers",
          new String[] {
            "List of servers we want to include into tab complete",
            "If list is empty then all servers will be used"
          });
      this.tabNamesServers = configReader.get("BungeeCord.TabComplete.Servers", new ArrayList());
      CMIList.toLowerCase(this.tabNamesServers);
      configReader.addComment(
          "BungeeCord.BackToPreviousServer",
          new String[] {
            "When enabled player can return to previous server when using /back comand instead of"
                + " being teleported to previous location on that server",
            "This only applies if player came to this server without teleporting around"
          });
      this.backToPreviousServer = configReader.get("BungeeCord.BackToPreviousServer", false);
    } catch (Exception exception) {
      this.plugin.consoleMessage("&cFailed to load BungeeCord Configuration!");
    }
  }

  public void initialize() {
    try {
      if (this.plugin.getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
        this.plugin.consoleMessage("Enabling BungeeCord Support");
        this.isBungee = true;
      }
    } catch (Exception | Error value) {
    }

    if (this.enabledSupport) {
      new BungeeCordListener(this.plugin);
      CMI.getInstance().consoleMessage("Initializing BungeeCord");
    }
  }

  public boolean isCMIBPresent() {
    return this.CMIBPresent;
  }

  public void setCMIBPresent(boolean flag) {
    if (!this.CMIBPresent && flag) {
      this.CMIBPresent = flag;
      this.plugin.consoleMessage("CMI proxy plugin detected");
      this.sendNetworkInfoUpdate();
      this.setBungeeCord(true);
    } else {
      this.CMIBPresent = flag;
    }
  }

  @Deprecated
  public boolean isBungee() {
    return this.isBungeeCord();
  }

  public void setBungeeCord(boolean flag) {
    if (flag && !this.isBungee) {
      this.isBungee = flag;
      this.sendServerListRequest();
      this.sendPlayerListRequest();
    } else {
      this.isBungee = flag;
    }
  }

  public boolean isBungeeCord() {
    return this.isBungee && this.enabledSupport;
  }

  public void addServer(BungeeCordServer bungeeCordServer) {
    this.servers.put(bungeeCordServer.getName().toLowerCase(), bungeeCordServer);
    bungeeCordServer.update();
  }

  public BungeeCordServer getServer(String string) {
    if (System.currentTimeMillis() > this.time + 600L) {
      this.updateServers();
    }

    return this.servers.get(string.toLowerCase());
  }

  public Map<String, BungeeCordServer> getServers() {
    if (System.currentTimeMillis() > this.time + 600L) {
      this.time = System.currentTimeMillis();
      this.sendNetworkInfoUpdate();
      this.updateServers();
    }

    return this.servers;
  }

  public void sendNetworkInfoUpdate() {
    if (this.type.equals(CMIBungeeType.Unknown)
        && this.isCMIBPresent()
        && !Bukkit.getOnlinePlayers().isEmpty()) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMINetworkInfo");
      this.plugin
          .getServer()
          .sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendServerInformationToBungee() {
    if (!this.type.equals(CMIBungeeType.Unknown)
        && this.isCMIBPresent()
        && !Bukkit.getOnlinePlayers().isEmpty()) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMIServerInfo");
      byteArrayDataOutput.writeInt(CMI.getInstance().getUtilManager().getOnlinePlayerCount());
      byteArrayDataOutput.writeInt(Bukkit.getMaxPlayers());
      byteArrayDataOutput.writeUTF(Bukkit.getMotd());
      this.plugin
          .getServer()
          .sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendServerListRequest() {
    if (!gotServerList && !Bukkit.getOnlinePlayers().isEmpty()) {
      Player player = getFirstPlayer();
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMIServerListRequest");
      player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendPlayerListRequest() {
    if (this.isCMIBPresent() && !Bukkit.getOnlinePlayers().isEmpty()) {
      Player player = getFirstPlayer();
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMIPlayerListRequest");
      player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendPlayerInfoRequest(String string) {
    if (!Bukkit.getOnlinePlayers().isEmpty()) {
      Player player = getFirstPlayer();
      if (player != null) {
        BungeeCordServer bungeeCordServer = this.getServer(string);
        if (bungeeCordServer != null && bungeeCordServer.getCurrentPlayers() > 0) {
          ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

          try {
            dataOutputStream.writeUTF(this.getThisServerName());
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }

          byteArrayDataOutput.writeShort(byteArrayOutputStream.toByteArray().length);
          byteArrayDataOutput.write(byteArrayOutputStream.toByteArray());
          this.forward(
              bungeeCordServer.getName(),
              "CMIPlayerListRequest",
              byteArrayDataOutput.toByteArray());
        }
      }
    }
  }

  static Player getFirstPlayer() {
    return (Player) Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
  }

  public BungeeCordServer getThisServer() {
    if (this.thisServer == null && this.getThisServerName() != null) {
      this.thisServer = this.getServer(this.getThisServerName());
    }

    return this.thisServer;
  }

  public void setThisServer(BungeeCordServer bungeeCordServer) {
    this.thisServer = bungeeCordServer;
  }

  public String getThisServerName() {
    if (this.thisServerName == null && this.thisServer != null) {
      this.thisServerName = this.thisServer.getName();
    }

    if (this.thisServerName == null) {
      this.updateServersInfo();
    }

    return this.thisServerName == null
        ? CMILib.getInstance().getReflectionManager().getServerName()
        : this.thisServerName;
  }

  public String getThisServerNameOneWord() {
    if (this.thisServerName == null && this.thisServer != null) {
      this.thisServerName = this.thisServer.getName();
    }

    return this.thisServerName == null
        ? CMILib.getInstance().getReflectionManager().getServerName().replace(" ", "_20_")
        : this.thisServerName.replace(" ", "_20_");
  }

  public void setThisServerName(String string) {
    this.thisServerName = string;
    if (this.getThisServerName() != null) {
      this.thisServer = this.getServer(this.getThisServerName());
    }
  }

  public BungeePlayer getBungeePlayer(String string) {
    if (!this.enabledSupport) {
      return null;
    }

    for (var entry : this.servers.entrySet()) {
      BungeePlayer bungeePlayer = ((BungeeCordServer) entry.getValue()).getPlayer(string);
      if (bungeePlayer != null) {
        return bungeePlayer;
      }
    }

    return null;
  }

  public BungeePlayer getBungeePlayer(UUID uuid) {
    if (!this.enabledSupport) {
      return null;
    }

    for (var entry : this.servers.entrySet()) {
      BungeePlayer bungeePlayer = ((BungeeCordServer) entry.getValue()).getPlayer(uuid);
      if (bungeePlayer != null) {
        return bungeePlayer;
      }
    }

    return null;
  }

  public BungeeCordServer getPlayerServer(UUID uuid) {
    if (!this.enabledSupport) {
      return null;
    }

    for (var entry : this.servers.entrySet()) {
      BungeePlayer bungeePlayer = ((BungeeCordServer) entry.getValue()).getPlayer(uuid);
      if (bungeePlayer != null) {
        return (BungeeCordServer) entry.getValue();
      }
    }

    return CMIUser.getOnlinePlayer(uuid) != null ? this.thisServer : null;
  }

  public BungeeCordServer getPlayerServer(String string) {
    if (!this.enabledSupport) {
      return null;
    }

    for (var entry : this.servers.entrySet()) {
      BungeePlayer bungeePlayer = ((BungeeCordServer) entry.getValue()).getPlayer(string);
      if (bungeePlayer != null) {
        return (BungeeCordServer) entry.getValue();
      }
    }

    return Bukkit.getPlayerExact(string) != null ? this.thisServer : null;
  }

  public boolean anyPlayersOnServer(String string) {
    if (!this.enabledSupport) {
      return false;
    }

    BungeeCordServer bungeeCordServer = this.getServer(string);
    return bungeeCordServer == null ? false : bungeeCordServer.getCurrentPlayers() > 0;
  }

  public void updateServersInfo() {
    Player player = getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("GetServer");
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public void updateServersIp() {
    Player player = getFirstPlayer();
    if (player != null) {
      for (var entry : this.getServers().entrySet()) {
        if (((BungeeCordServer) entry.getValue()).getIp() == null) {
          ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
          byteArrayDataOutput.writeUTF("ServerIP");
          byteArrayDataOutput.writeUTF((String) entry.getKey());
          player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
        }
      }
    }
  }

  public void updateServers() {
    if (this.isBungee) {
      Player player = getFirstPlayer();
      if (player != null) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("GetServers");

        try {
          player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
        } catch (Exception | Error value) {
        }
      }
    }
  }

  public void connectToServer(Player player, String string) {
    if (player != null && player.isOnline()) {
      if (CMIBungeeType.Velocity.equals(this.type)) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("CMIChangeServer");
        byteArrayDataOutput.writeUTF(string);
        byteArrayDataOutput.writeUTF(player.getUniqueId().toString());
        player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
      } else {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(string);
        player.sendPluginMessage(this.plugin, "BungeeCord", output.toByteArray());
      }
    }
  }

  public void connectOther(String string, String server) {
    Player player = getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("ConnectOther");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(server);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public void connectToServer(String string, String server) {
    this.connectToServer(CMIUser.getOnlinePlayer(string), server);
  }

  public int getPlayersInServer(String string) {
    if (!this.enabledSupport) {
      return 0;
    }

    BungeeCordServer bungeeCordServer = this.getServer(string);
    if (bungeeCordServer == null) {
      return 0;
    }

    bungeeCordServer.update();
    return bungeeCordServer.getCurrentPlayers();
  }

  public void sendPublicMessage(String string, String server, String message) {
    if (this.enabledSupport && this.getPlayersInServer(string) >= 1) {
      if (message.length() > 32766) {
        message = CMIChatColor.stripColor(message);
      }

      if (message.length() > 32766) {
        this.plugin.consoleMessage(
            "&cFailed to send public bungee message (" + message.length() + ")");
      } else {
        try {
          ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
          byteArrayDataOutput.writeUTF(server);
          byteArrayDataOutput.writeUTF(message);
          byteArrayDataOutput.writeLong(System.currentTimeMillis());
          this.forward(string, "CMIChatSubChannel", byteArrayDataOutput.toByteArray());
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
  }

  public void sendStaffMessage(String string, String message) {
    for (var entry : this.getServers().entrySet()) {
      if (((BungeeCordServer) entry.getValue()).getCurrentPlayers() > 0) {
        this.sendStaffMessage((String) entry.getKey(), string, message);
      }
    }
  }

  public void sendStaffMessage(String string, String server, String message) {
    if (this.enabledSupport
        && this.getPlayersInServer(string) >= 1
        && !this.getThisServerName().equalsIgnoreCase(string)) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF(server);
      byteArrayDataOutput.writeUTF(message);
      byteArrayDataOutput.writeLong(System.currentTimeMillis());
      this.forward(string, "CMIStaffSubChannel", byteArrayDataOutput.toByteArray());
    }
  }

  @Deprecated
  public void sendMessage(String string, String target, String message) {
    this.sendPrivateMessage(string, target, message);
  }

  public void sendPrivateMessage(String string, String target, String message) {
    if (this.enabledSupport) {
      BungeeCordServer bungeeCordServer = this.getPlayerServer(target);
      if (bungeeCordServer != null && bungeeCordServer.getCurrentPlayers() > 0) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(string);
        byteArrayDataOutput.writeUTF(target);
        byteArrayDataOutput.writeUTF(message);
        byteArrayDataOutput.writeLong(System.currentTimeMillis());
        this.forward(
            bungeeCordServer.getName(), "CMIMessageSubChannel", byteArrayDataOutput.toByteArray());
      }
    }
  }

  public void sendBroadcastMessage(
      String string, BungeeCordServer bungeeCordServer, String message) {
    if (this.enabledSupport
        && bungeeCordServer != null
        && bungeeCordServer.getCurrentPlayers() > 0) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(message);
      byteArrayDataOutput.writeLong(System.currentTimeMillis());
      this.forward(
          bungeeCordServer.getName(), "CMIBroadcastSubChannel", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendPortalSetLocationToBungee(
      String string, String targetServer, String portalName, Location location) {
    if (this.enabledSupport) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMIPortalSetSubChannel");
      byteArrayDataOutput.writeUTF(targetServer);
      byteArrayDataOutput.writeUTF(this.getThisServerName());
      byteArrayDataOutput.writeUTF(portalName);
      byteArrayDataOutput.writeUTF(this.plugin.getPlayerManager().convertLocToString(location));
      byteArrayDataOutput.writeUTF(string);
      this.plugin
          .getServer()
          .sendPluginMessage(
              CMI.getInstance(), "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void sendPortalSetSuccessResponse(
      String string, String targetServer, String portalName, String locationString) {
    if (this.enabledSupport) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("Forward");
      byteArrayDataOutput.writeUTF(targetServer);
      byteArrayDataOutput.writeUTF("CMIPortalCSetSubChannel");
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

      try {
        dataOutputStream.writeUTF(this.getThisServerName());
        dataOutputStream.writeUTF(targetServer);
        dataOutputStream.writeUTF(portalName);
        dataOutputStream.writeUTF(locationString);
        dataOutputStream.writeUTF(string);
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

      byteArrayDataOutput.writeShort(byteArrayOutputStream.toByteArray().length);
      byteArrayDataOutput.write(byteArrayOutputStream.toByteArray());
      this.plugin
          .getServer()
          .sendPluginMessage(CMI.getInstance(), "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public void recordBackLocation(String string, UUID uuid, Location location) {
    this.teleporToLocation(string, uuid, "", location, "");
  }

  public void teleporToLocation(String string, UUID uuid, String targetServer, Location location) {
    this.teleporToLocation(string, uuid, targetServer, location, "");
  }

  public void teleporToLocation(
      String string, UUID uuid, String targetServer, Location location, String extraData) {
    if (this.enabledSupport) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("CMITeleportChannel");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(uuid.toString());
      byteArrayDataOutput.writeUTF(targetServer);
      byteArrayDataOutput.writeUTF(CMILocation.toString(location, false));
      byteArrayDataOutput.writeUTF(extraData);
      this.plugin
          .getServer()
          .sendPluginMessage(
              CMI.getInstance(), "cmib:fromserver", byteArrayDataOutput.toByteArray());
    }
  }

  public void withWhomHeTalks(String string) {
    if (this.isCMIBPresent() && !Bukkit.getOnlinePlayers().isEmpty()) {
      Player player = getFirstPlayer();
      if (player != null) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("CMITalksWith");
        byteArrayDataOutput.writeUTF(string);
        player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
      }
    }
  }

  public void talksWith(String string, String talkTarget) {
    if (this.isCMIBPresent() && !Bukkit.getOnlinePlayers().isEmpty()) {
      Player player = getFirstPlayer();
      if (player != null) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("CMITalksWithRecord");
        byteArrayDataOutput.writeUTF(string);
        byteArrayDataOutput.writeUTF(talkTarget);
        player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
      }
    }
  }

  public void forward(String string, String subChannel, byte[] bs) {
    Player player = getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("Forward");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(subChannel);
      byteArrayDataOutput.writeShort(bs.length);
      byteArrayDataOutput.write(bs);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public boolean isPlayerOnAnotherServer(String string) {
    if (this.enabledSupport
        && this.getThisServerName() != null
        && CMIUser.getOnlinePlayer(string) == null) {
      BungeePlayer bungeePlayer = this.getBungeePlayer(string);
      return bungeePlayer != null
          && !bungeePlayer.getServerName().equalsIgnoreCase(this.getThisServerName());
    } else {
      return false;
    }
  }

  public boolean isAnyoneElseOnlineOnNetwork(String string) {
    if (this.enabledSupport && this.getThisServerName() != null) {
      for (BungeeCordServer bungeeCordServer : this.getServers().values()) {
        if (!bungeeCordServer.getPlayersMapName().isEmpty()
            && !bungeeCordServer.getPlayersMapName().containsKey(string.toLowerCase())) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public void sendNewPlayerInfoToNetwork(Player player) {
    if (this.enabledSupport && this.isBungeeCord()) {
      for (var entry : this.getServers().entrySet()) {
        if (((BungeeCordServer) entry.getValue()).getCurrentPlayers() > 0) {
          this.sendNewPlayerInfoToNetwork(player, (String) entry.getKey());
        }
      }
    }
  }

  public void sendNewPlayerInfoToNetwork(Player player, String string) {
    if (this.enabledSupport && this.isBungeeCord() && this.getThisServerName() != null) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (this.getPlayersInServer(string) >= 1) {
          if (!string.equalsIgnoreCase(this.getThisServerName())) {
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            String playerInfo =
                cmiUser.getName()
                    + "::"
                    + cmiUser.getDisplayName(false)
                    + "::"
                    + (cmiUser.getNickName() == null ? "" : cmiUser.getNickName())
                    + "::"
                    + cmiUser.getUniqueId()
                    + "::"
                    + (cmiUser.isVanished() ? 1 : 0);
            byteArrayDataOutput.writeUTF(this.getThisServerName());
            byteArrayDataOutput.writeUTF(playerInfo);
            this.forward(string, "CMIPlayerFeedback", byteArrayDataOutput.toByteArray());
          }
        }
      }
    }
  }

  public boolean isEnabledSupport() {
    return this.enabledSupport;
  }

  public CMIBungeeCord getBungeeCord() {
    return this.bcord;
  }

  public CMIBungeeType getNetworkType() {
    return this.type;
  }

  public void setNetworkType(CMIBungeeType cmiBungeeType) {
    cmiBungeeType = cmiBungeeType == null ? CMIBungeeType.Unknown : cmiBungeeType;
    if (!cmiBungeeType.equals(CMIBungeeType.Unknown)) {
      this.setBungeeCord(true);
    }

    this.type = cmiBungeeType;
  }

  public boolean isNamesInTabComplete() {
    return this.namesInTabComplete;
  }

  public boolean isBackToPreviousServer() {
    return this.backToPreviousServer;
  }

  public Set<String> getPlayerNamesFromServers() {
    return this.getPlayerNamesFromServers(true, true);
  }

  public Set<String> getPlayerNamesFromServers(boolean flag, boolean useTabNamesFilter) {
    HashSet<String> hashSet = new HashSet();

    for (var entry : this.plugin.getBungeeCordManager().getServers().entrySet()) {
      if (!((BungeeCordServer) entry.getValue())
              .getName()
              .equalsIgnoreCase(this.plugin.getBungeeCordManager().getThisServerName())
          && (!useTabNamesFilter
              || this.tabNamesServers.isEmpty()
              || this.tabNamesServers.contains(
                  ((BungeeCordServer) entry.getValue()).getName().toLowerCase()))) {
        for (var playerEntry :
            ((BungeeCordServer) entry.getValue()).getPlayersMapName().entrySet()) {
          BungeePlayer bungeePlayer = (BungeePlayer) playerEntry.getValue();
          if (!bungeePlayer.getVanished() || flag) {
            if (this.plugin.getNickNameManager().isIncludeNickNameInTabComplete()
                && bungeePlayer.getNickName() != null) {
              hashSet.add(CMIChatColor.stripColor(bungeePlayer.getNickName()));
            }

            hashSet.add(CMIChatColor.stripColor(bungeePlayer.getName()));
          }
        }
      }
    }

    return hashSet;
  }
}
