package com.Zrips.CMI.Modules.BungeeCord;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeCordServer {
  private String name;
  private String ip;
  private Integer port;
  private String motd = "";
  private int maxPlayers = 0;
  private int currentPlayers = 0;
  private Long nextCheck = 0L;
  private boolean online = false;
  private ConcurrentHashMap<String, BungeePlayer> playersMapUUID = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, BungeePlayer> playersMapName = new ConcurrentHashMap<>();

  public BungeeCordServer(String string, String ipAddress, Integer integer) {
    this.name = string;
    this.ip = ipAddress;
    this.port = integer;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public String getIp() {
    return this.ip;
  }

  public void setIp(String string) {
    this.ip = string;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int value) {
    this.port = value;
  }

  public String getMotd() {
    return this.getName()
            .equalsIgnoreCase(CMI.getInstance().getBungeeCordManager().getThisServerName())
        ? Bukkit.getServer().getMotd()
        : this.motd;
  }

  public void setMotd(String string) {
    this.motd = string;
  }

  public int getMaxPlayers() {
    return this.maxPlayers;
  }

  public void setMaxPlayers(int value) {
    this.maxPlayers = value;
  }

  public int getCurrentPlayers() {
    int value = 0;
    var iterator = this.playersMapName.entrySet().iterator();

    while (iterator.hasNext()) {
      BungeePlayer bungeePlayer = (BungeePlayer) ((Entry) iterator.next()).getValue();
      if (!bungeePlayer.getVanished()) {
        value++;
      }
    }

    return value;
  }

  public Long getNextCheck() {
    return this.nextCheck;
  }

  public void setNextCheck(Long nextCheckTime) {
    this.nextCheck = nextCheckTime;
  }

  public void update() {
    if (this.nextCheck <= System.currentTimeMillis()) {
      if (this.getName()
          .equalsIgnoreCase(CMI.getInstance().getBungeeCordManager().getThisServerName())) {
        this.maxPlayers = Bukkit.getMaxPlayers();
        var hashMap = new HashMap<>(this.playersMapUUID);
        this.playersMapUUID.clear();
        this.playersMapName.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
          BungeePlayer bungeePlayer = (BungeePlayer) hashMap.get(player.getUniqueId().toString());
          if (bungeePlayer == null) {
            bungeePlayer = new BungeePlayer();
            bungeePlayer.setBungeeServer(this);
            bungeePlayer.setName(player.getName());
            bungeePlayer.setUniqueId(player.getUniqueId());
          }

          CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
          bungeePlayer.setNickName(cmiUser.getNickName());
          bungeePlayer.setPlayerDisplayName(cmiUser.getDisplayName());
          bungeePlayer.setVanished(cmiUser.isVanished());
          this.playersMapUUID.put(player.getUniqueId().toString(), bungeePlayer);
          this.playersMapName.put(player.getName().toLowerCase(), bungeePlayer);
        }

        this.currentPlayers = CMI.getInstance().getUtilManager().getOnlinePlayerCount();
        this.motd = CMIChatColor.deColorize(Bukkit.getMotd());
        this.online = true;
        this.nextCheck = System.currentTimeMillis() + 1000L;
      } else {
        BungeeCordServer bungeeCordServer = this;
        if (this.ip != null && this.port != null && this.nextCheck <= System.currentTimeMillis()) {
          this.nextCheck = System.currentTimeMillis() + 1000L;
          CMI.getInstance()
              .getBungeeCordManager()
              .getBungeeCord()
              .getPlayerList(this.getName())
              .whenComplete(
                  (list, throwable) -> {
                    var playersByNameCopy = new HashMap<>(this.playersMapName);
                    this.playersMapUUID.clear();
                    this.playersMapName.clear();
                    if (list.size() > 1 || !list.get(0).isEmpty()) {
                      this.currentPlayers = list.size();
                    }

                    list.stream()
                        .forEach(
                            string -> {
                              if (!string.isEmpty()) {
                                BungeePlayer namedPlayer =
                                    (BungeePlayer) playersByNameCopy.get(string.toLowerCase());
                                if (namedPlayer == null) {
                                  namedPlayer = new BungeePlayer();
                                }

                                namedPlayer.setBungeeServer(bungeeCordServer);
                                namedPlayer.setName(string);
                                this.playersMapName.put(string.toLowerCase(), namedPlayer);
                                if (namedPlayer.getUniqueId() != null) {
                                  this.playersMapUUID.put(
                                      namedPlayer.getUniqueId().toString(), namedPlayer);
                                } else {
                                  CMI.getInstance()
                                      .getBungeeCordManager()
                                      .getBungeeCord()
                                      .getUUID(string)
                                      .whenComplete(
                                          (uuidText, error) -> {
                                            BungeePlayer uuidPlayer =
                                                this.playersMapName.get(string);
                                            if (uuidPlayer != null) {
                                              uuidPlayer.setUniqueId(UUID.fromString(uuidText));
                                              this.playersMapUUID.put(uuidText, uuidPlayer);
                                            }
                                          });
                                }
                              }
                            });
                  });
          if (CMI.getInstance().getBungeeCordManager().isCMIBPresent()) {
            CMI.getInstance()
                .getBungeeCordManager()
                .getBungeeCord()
                .getServerPlayerCount(this.getName())
                .whenComplete(
                    (entriesText, error) -> {
                      if (!entriesText.isEmpty()
                          && !entriesText.isEmpty()
                          && entriesText.contains(";:")) {
                        String[] strings = entriesText.split(";:");
                        if (strings.length > 3) {
                          this.motd = strings[3];
                        }

                        if (this.currentPlayers != this.getCurrentPlayers()) {
                          CMI.getInstance()
                              .getBungeeCordManager()
                              .sendPlayerInfoRequest(this.getName());
                        }

                        try {
                          this.currentPlayers = Integer.parseInt(strings[1]);
                        } catch (Throwable throwable4) {
                          throwable4.printStackTrace();
                        }

                        try {
                          this.maxPlayers = Integer.parseInt(strings[2]);
                        } catch (Throwable throwable5) {
                          throwable5.printStackTrace();
                        }

                        this.online = strings[0].equals("1");
                      }
                    });
          } else {
            CMIScheduler.runTaskAsynchronously(
                CMI.getInstance(),
                () -> {
                  try {
                    ServerListPing serverListPing = new ServerListPing(this.ip, this.port);
                    ServerListPing.StatusResponse statusResponse = serverListPing.fetchData();
                    this.maxPlayers = statusResponse.getPlayers().getMax();
                    if (this.currentPlayers != this.getCurrentPlayers()) {
                      CMI.getInstance()
                          .getBungeeCordManager()
                          .sendPlayerInfoRequest(this.getName());
                    }

                    this.currentPlayers = statusResponse.getPlayers().getOnline();
                    this.online = true;
                  } catch (Exception exception) {
                    this.online = false;
                  }
                });
          }
        }
      }
    }
  }

  public boolean isOnline() {
    return this.online;
  }

  public void setOnline(boolean flag) {
    this.online = flag;
  }

  public ConcurrentHashMap<String, BungeePlayer> getPlayersMapUUID() {
    return this.playersMapUUID;
  }

  public BungeePlayer getPlayer(UUID uuid) {
    return this.playersMapUUID.get(uuid.toString());
  }

  public BungeePlayer getPlayer(String string) {
    BungeePlayer bungeePlayer = this.playersMapName.get(string.toLowerCase());
    if (bungeePlayer == null) {
      for (var entry : this.playersMapUUID.entrySet()) {
        if (((BungeePlayer) entry.getValue()).getNickName() != null
            && ((BungeePlayer) entry.getValue()).getNickName().equalsIgnoreCase(string)) {
          return (BungeePlayer) entry.getValue();
        }
      }
    }

    return bungeePlayer;
  }

  public void addPlayer(BungeePlayer bungeePlayer) {
    if (bungeePlayer.getUniqueId() != null) {
      this.playersMapUUID.put(bungeePlayer.getUniqueId().toString(), bungeePlayer);
    }

    if (bungeePlayer.getName() != null) {
      this.playersMapName.put(bungeePlayer.getName().toLowerCase(), bungeePlayer);
    }
  }

  public void removePlayer(UUID uuid, String string) {
    this.playersMapUUID.remove(uuid.toString());
    this.playersMapName.remove(string.toLowerCase());
  }

  public void clearPlayers() {
    this.playersMapUUID.clear();
    this.playersMapName.clear();
  }

  public ConcurrentHashMap<String, BungeePlayer> getPlayersMapName() {
    return this.playersMapName;
  }
}
