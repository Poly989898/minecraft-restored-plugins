package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Afk.AfkInfo;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTListener;
import com.Zrips.CMI.Modules.Chat.CMIPlayerMessageColor;
import com.Zrips.CMI.Modules.ChatFormat.CMIChatRoom;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatListener;
import com.Zrips.CMI.Modules.CmdCooldown.CmdCooldown;
import com.Zrips.CMI.Modules.DeathMessages.damageInformation;
import com.Zrips.CMI.Modules.Economy.CMIEconomyAcount;
import com.Zrips.CMI.Modules.FlightCharge.FlightCharge;
import com.Zrips.CMI.Modules.GeoIP.IPLocation;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.Modules.Kits.KitsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayTime;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTROneTime;
import com.Zrips.CMI.Modules.PlayerCombat.EntityKillCount;
import com.Zrips.CMI.Modules.PlayerCombat.PlayerKillCount;
import com.Zrips.CMI.Modules.PlayerMeta.PlayerMeta;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.Modules.SavedInv.SavedInventories;
import com.Zrips.CMI.Modules.SilentChest.silentChestListener;
import com.Zrips.CMI.Modules.SpawnerCharge.PlayerCharge;
import com.Zrips.CMI.Modules.SpawnerCharge.SpawnerChargeListener;
import com.Zrips.CMI.Modules.Statistics.CMIStats;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportFeedback;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Totems.CMIPlayerTotemData;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;
import com.Zrips.CMI.Modules.Warnings.CMIWarningCategory;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import com.Zrips.CMI.events.CMIUserHomeCreateEvent;
import com.Zrips.CMI.events.CMIUserHomeRemoveEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Enchants.CMIEnchantEnum;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIUser {
  private UUID uuid;
  private Player player;
  private boolean collision = true;
  private String name;
  private String nickName;
  private String displayName;
  private CMIEconomyAcount economy;
  private int id = 0;
  private int invId = 0;
  private int playTimeId = 0;
  private int playTimerewardId = 0;
  private LinkedHashMap<String, Integer> ips;
  private CMILocation logOutLocation;
  private List<String> lockedIps;
  private long lastLogin = 0L;
  private long lastLogoff = 0L;
  private long totalPlayTime = 0L;
  private CMILocation DeathLoc;
  private Location TpLoc;
  private long pTime = -1L;
  private long tfly = 0L;
  private long tgod = 0L;
  private long teleportInvulnerability = 0L;
  private boolean isFakeAccount = false;
  private List<PlayerNote> notes;
  private List<PlayerMail> mail;
  private LinkedHashMap<String, CmiHome> homes;
  private Set<UUID> ignores;
  private boolean silenceMode = false;
  private boolean hadAllowFlight = false;
  private boolean AllowFlight = false;
  private boolean Flying = false;
  private int votifierVotes = 0;
  private List<Long> votifierVotesByTime;
  private String skin;
  private HashMap<String, Long> repeatableRewards;
  private Set<String> oneTimeRewards;
  HashMap<String, worldFlyState> tempFlyModes = new HashMap<>();
  HashMap<PlayerOption, Boolean> options = null;
  private CMITask schedId;
  Boolean fakeUser = null;
  Long time = null;
  private static Statistic statCheck = null;
  private boolean cachedVanished = false;
  private long lastCheck = 0L;

  public CMIUser(int value) {
    this.id = value;
    CMI.getInstance().getPlayerManager().addUser(this, value);
  }

  public void cacheClear() {
    CMIPlayerTotemData.setTotemWarmup(this.getUniqueId(), 0L);
    CMIPermissionPlayerData.clearCache(this.getUniqueId());
    damageInformation.cacheClear(this.getUniqueId());
    if (this.isOnline()) {
      this.hideBossBars();
    }

    this.clearBossMaps();
    CMI cmi = CMI.getInstance();
    cmi.getTeleportHandler().removeBadTeleport(this.getUniqueId());
    cmi.getAnimationManager().clearCache(this.getUniqueId());
    cmi.getChatFilterManager().clearCache(this.getUniqueId());
    cmi.getChatFormatManager().clearCache(this.getUniqueId());
    cmi.getChunkPreviewManager().clearCache(this.getUniqueId());
    cmi.getWarmUpManager().cancel(this.getUniqueId());
    CustomNBTListener.clearCache(this.getUniqueId());
    cmi.getHologramManager().clearCache(this.getUniqueId());
    cmi.getKitsManager().clearCache(this.getUniqueId());
    cmi.getSpecializedCommandManager().clearCache(this.getUniqueId());
    cmi.getPlayTimeRewardManager().clearCache(this.getUniqueId());
    cmi.getMirrorManager().removeMirroring(this.getUniqueId());
    cmi.getAfkManager().removeAfkInfo(this.getUniqueId());
    cmi.getPlayerCombatManager().clearCache(this.getUniqueId());
    cmi.getSelectionManager().clearSelection(this.player);
    PacketHandler.clearCache(this.getUniqueId());
    this.scheduleDataClear();
    this.clearCachedFlyModes();
    if (!cmi.getDbManager().isInventorySavePending(this)) {
      cmi.getSavedInventoryManager().removeCacheInventory(this.getUniqueId());
    }

    SpawnerChargeListener.clearCache(this.getUniqueId());
    ChatFormatListener.shouts.remove(this.getUniqueId());
    silentChestListener.clearCache(this.getUniqueId());
  }

  private void scheduleDataClear() {
    if (this.schedId != null) {
      this.schedId.cancel();
    }

    if (CMI.getInstance().isEnabled()) {
      this.schedId =
          CMIScheduler.runTaskLater(
              CMI.getInstance(),
              () -> {
                this.unloadData();
                this.schedId = null;
              },
              1200L);
    }
  }

  public CMIUser(UUID playerUuid) {
    this.uuid = playerUuid;
    this.player = getOnlinePlayer(this.uuid);
    if (this.player != null) {
      this.name = this.player.getName();
    }
  }

  @Deprecated
  public CMIUser(UUID playerUuid, boolean flag) {
    this.uuid = playerUuid;
    this.player = getOnlinePlayer(this.uuid);
    if (this.player != null) {
      this.name = this.player.getName();
    }

    if (flag) {
      try {
        this.updatePermissions();
      } catch (Exception | Error value) {
        value.printStackTrace();
      }
    }
  }

  public CMIUser(OfflinePlayer offlinePlayer) {
    this.name = offlinePlayer.getName();
    this.uuid = offlinePlayer.getUniqueId();
    this.player = getOnlinePlayer(this.uuid);
  }

  public boolean isOnline() {
    if (!CMI.getInstance().isFullyLoaded()) {
      return false;
    }

    if (this.player != null) {
      return this.player.isOnline();
    }

    this.player = getOnlinePlayer(this.uuid);
    return this.player != null;
  }

  public Location getLogOutLocation() {
    if (this.logOutLocation == null && this.isOnline()) {
      this.setLogOutLocation(this.getPlayer(false).getLocation());
    }

    return this.logOutLocation;
  }

  public CMILocation getCMILogOutLocation() {
    if (this.logOutLocation == null && this.isOnline()) {
      this.setLogOutLocation(this.getPlayer(false).getLocation());
    }

    return this.logOutLocation;
  }

  public Location getLocation() {
    return !this.isOnline() ? this.getLogOutLocation() : this.getPlayer(true).getLocation();
  }

  public void setLogOutLocation(Location location) {
    if (location == null) {
      this.logOutLocation = null;
    } else {
      this.logOutLocation = new CMILocation(location);
    }
  }

  public void setLogOutLocation(CMILocation cmiLocation) {
    this.logOutLocation = cmiLocation;
  }

  public void saveIfOffline() {
    if (!this.isOnline()) {
      this.addForDelayedSave();
    }
  }

  public void addForDelayedSave() {
    if (this.fakeUser == null) {
      this.fakeUser =
          this.getName() != null
              && this.getName()
                  .equalsIgnoreCase(CMI.getInstance().getPlayerManager().getFakeUserName());
    }

    if (!this.fakeUser) {
      CMI.getInstance().getDbManager().addForSave(this);
    }
  }

  public void addForPlayTimeRewardSave() {
    CMI.getInstance().getDbManager().addForPlayTimeRewardSave(this);
  }

  public void setIps(LinkedHashMap<String, Integer> linkedHashMap) {
    this.ips = linkedHashMap;
  }

  public HashMap<String, Integer> getIps() {
    if (this.ips == null) {
      this.ips = new LinkedHashMap<>();
    }

    return this.ips;
  }

  public boolean addIps(String string) {
    if (string != null && !string.equalsIgnoreCase("127.0.0.1")) {
      CMI.getInstance().getIpManager().addIp(string, this);
      if (!this.getIps().containsKey(string)) {
        this.getIps().put(string, 1);
        return true;
      }

      Integer integer = this.getIps().remove(string);
      if (integer == null) {
        integer = 0;
      }

      this.getIps().put(string, integer + 1);
      this.addForDelayedSave();
      return true;
    } else {
      return false;
    }
  }

  public String getLastIp() {
    if (this.isOnline()) {
      return this.getPlayer(false).getAddress().getAddress().getHostAddress();
    }

    String string = null;

    for (var entry : this.getIps().entrySet()) {
      string = (String) entry.getKey();
    }

    return string;
  }

  public String getCountry() {
    IPLocation ipLocation = this.lookupIpLocation();
    return ipLocation == null ? "N/A" : ipLocation.getCountryName();
  }

  private IPLocation lookupIpLocation() {
    try {
      return CMI.getInstance().getLookupService().getLocation(this.getLastIp());
    } catch (IOException ioException) {
      ioException.printStackTrace();
      return null;
    }
  }

  public String getCountryCode() {
    IPLocation ipLocation = this.lookupIpLocation();
    return ipLocation == null ? "N/A" : ipLocation.getCountryCode();
  }

  public String getCity() {
    IPLocation ipLocation = this.lookupIpLocation();
    return ipLocation == null ? "N/A" : ipLocation.getCity();
  }

  public boolean addAllIps(Map<String, Integer> map) {
    boolean flag = false;

    for (var entry : map.entrySet()) {
      String string = (String) entry.getKey();
      if (!this.getIps().containsKey(string)) {
        this.getIps().put(string, (Integer) entry.getValue());
        flag = true;
      } else {
        Integer integer = this.getIps().remove(string);
        this.getIps().put(string, integer + 1);
        flag = true;
      }
    }

    return flag;
  }

  public Player getPlayer() {
    return this.getPlayer(!this.isFakeAccount());
  }

  public Player getPlayer(boolean flag) {
    Player onlinePlayer = getOnlinePlayer(this.uuid);
    if (onlinePlayer == null) {
      if (flag) {
        if (this.player == null && CMI.getInstance().isServerLoaded()) {
          this.player = CMI.getInstance().getNMS().getPlayer(this);
        }

        this.scheduleDataClear();
      }
    } else {
      this.player = onlinePlayer;
      if (this.schedId != null) {
        this.schedId.cancel();
        this.schedId = null;
      }
    }

    try {
      if (this.player != null && CMINPCChecker.isNPC(this.player)) {
        return null;
      }
    } catch (Exception exception) {
      return null;
    }

    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public String getName() {
    return this.getName(false);
  }

  public OfflinePlayer getOfflinePlayer() {
    if (this.isOnline()) {
      return this.getPlayer(false);
    } else {
      return this.getUniqueId() == null ? null : Bukkit.getOfflinePlayer(this.getUniqueId());
    }
  }

  public String getName(boolean flag) {
    if (this.isOnline()) {
      this.name = this.getPlayer().getName();
      return this.name;
    }

    if (flag && this.name == null) {
      Player onlinePlayer = getOnlinePlayer(this.uuid);
      this.player = onlinePlayer != null ? onlinePlayer : this.player;
      if (this.player != null && this.player.isOnline()) {
        this.name = this.player.getName();
        this.addForDelayedSave();
      } else {
        this.name = this.getOfflinePlayer().getName();
      }
    }

    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public long getLastLogin() {
    return this.getLastLogin(false);
  }

  public long getLastLogin(boolean flag) {
    if (flag && this.lastLogin == 0L && this.getOfflinePlayer() != null) {
      this.lastLogin = this.getOfflinePlayer().getLastPlayed();
    }

    return this.lastLogin;
  }

  public void setLastLogin(long longValue) {
    this.lastLogin = longValue;
  }

  public long getLastLogoff() {
    return this.getLastLogoff(false);
  }

  public long getLastLogoff(boolean flag) {
    if (flag && this.lastLogoff == 0L && this.getOfflinePlayer() != null) {
      try {
        this.lastLogoff = this.getOfflinePlayer().getLastPlayed();
      } catch (Exception exception) {
        try {
          this.lastLogoff = this.getOfflinePlayer().getLastLogin();
        } catch (Exception | Error value) {
        }
      }

      this.addForDelayedSave();
    }

    return this.lastLogoff;
  }

  public long getLastLogoffClean() {
    return this.lastLogoff;
  }

  public void setLastLogoff(long longValue) {
    this.lastLogoff = longValue;
  }

  public Location getDeathLoc() {
    if (CMI.getInstance().getTeleportManager().getDeathBackExpiration() > 0
        && CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getDeathBackExpiration()
            < System.currentTimeMillis()) {
      this.DeathLoc = null;
    }

    return this.DeathLoc;
  }

  public void setDeathLoc(@Nullable CMILocation cmiLocation) {
    if (cmiLocation != null
        && CMI.getInstance()
            .getTeleportManager()
            .getDBackBlackList()
            .contains(cmiLocation.getWorldName().toLowerCase())) {
      this.DeathLoc = null;
    } else {
      this.DeathLoc = cmiLocation;
      if (CMI.getInstance().getTeleportManager().getDeathBackExpiration() > 0) {
        CMIPlayerTempData.computeIfAbsent(this.getUniqueId())
            .setDeathBackExpiration(
                CMI.getInstance().getTeleportManager().getDeathBackExpiration() * 1000L
                    + System.currentTimeMillis());
      }
    }
  }

  @Deprecated
  public Location getTpLoc() {
    return this.getLastTeleportLocation();
  }

  public Location getLastTeleportLocation() {
    if (CMI.getInstance().getTeleportManager().getBackExpiration() > 0
        && CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getBackExpiration()
            < System.currentTimeMillis()) {
      this.TpLoc = null;
    }

    return this.TpLoc;
  }

  @Deprecated
  public void setTpLoc(Location location) {
    this.setLastTeleportLocation(location, true);
  }

  @Deprecated
  public void setTpLoc(Location location, boolean flag) {
    this.setLastTeleportLocation(location, flag);
  }

  public void setLastTeleportLocation(Location location) {
    this.setLastTeleportLocation(location, true);
  }

  public void setLastTeleportLocation(@Nullable Location location, boolean flag) {
    if (location == null
        || location.getWorld() == null
        || location.getWorld().getName() == null
        || !CMI.getInstance()
            .getTeleportManager()
            .getBackBlackList()
            .contains(location.getWorld().getName().toLowerCase())) {
      this.TpLoc = location != null ? location.clone() : null;
      if (CMI.getInstance().getTeleportManager().getBackExpiration() > 0) {
        CMIPlayerTempData.computeIfAbsent(this.getUniqueId())
            .setBackExpiration(
                CMI.getInstance().getTeleportManager().getBackExpiration() * 1000L
                    + System.currentTimeMillis());
      }

      if (flag) {
        this.addForDelayedSave();
      }
    }
  }

  @Deprecated
  public UUID getUuid() {
    return this.getUniqueId();
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public void setUuid(UUID newUuid) {
    this.uuid = newUuid;
    this.player = getOnlinePlayer(this.uuid);
    if (this.player != null) {
      this.name = this.player.getName();
    }

    this.updatePermissions();
  }

  public void setUuidNoPermUpdate(UUID newUuid) {
    this.uuid = newUuid;
    this.player = getOnlinePlayer(this.uuid);
    if (this.player != null) {
      this.name = this.player.getName();
    }
  }

  public void updatePermissions() {
    if (CMI.getInstance().isFullyLoaded()
        && this.player != null
        && this.player.isOnline()
        && this.isOnline()) {
      if (this.time == null) {
        this.time = 0L;
      }

      if (this.time + 1000L <= System.currentTimeMillis()) {
        this.time = System.currentTimeMillis();
        this.updatePrefix();
        this.updateSuffix();
        this.updateGroup();
        this.updateDisplayName();
      }
    }
  }

  public void updateDisplayName() {
    this.updateDisplayName(false);
  }

  public void updateDisplayName(boolean flag) {
    if (this.isOnline() && this.getPlayer(false) != null) {
      if (!CMI.getInstance().getNickNameManager().isDisplayNameChange()) {
        String string = this.getPlayer(false).getDisplayName();
        if (string == null) {
          string = this.getName(false);
        }

        this.displayName = string;
      } else {
        if (flag || this.timeToUpdate(UserDataUpdateType.displayname)) {
          String nickFormat = CMI.getInstance().getNickNameManager().getNickNameFormat();
          if (nickFormat.contains("{prefix}")) {
            nickFormat = nickFormat.replace("{prefix}", this.getPrefix());
          }

          if (nickFormat.contains("{suffix}")) {
            nickFormat = nickFormat.replace("{suffix}", this.getSuffix());
          }

          if (nickFormat.contains("{nicknameprefix}")) {
            nickFormat =
                nickFormat.replace(
                    "{nicknameprefix}",
                    CMI.getInstance().getChatFormatManager().getNickNamePrefix(this));
          }

          if (nickFormat.contains("{nickName}")) {
            String resolvedName =
                this.nickName == null
                    ? (this.getName() == null ? "" : this.getName())
                    : this.nickName;
            resolvedName = this.getNameColor() + resolvedName;
            nickFormat = nickFormat.replace("{nickName}", resolvedName);
          } else {
            nickFormat =
                nickFormat
                    + this.getNameColor()
                    + (this.nickName == null
                        ? (this.getName() == null ? "" : this.getName())
                        : this.nickName);
          }

          nickFormat =
              CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(this.getPlayer(false), nickFormat);
          this.displayName = CMIChatColor.translate(nickFormat);
        }

        this.getPlayer(false)
            .setDisplayName(
                this.displayName == null ? this.getDisplayName(false) : this.displayName);
      }
    }
  }

  public void updateGroup() {
    CMIPermissionPlayerData.updateGroup(this);
  }

  public void updatePrefix() {
    CMIPermissionPlayerData.updatePrefix(this);
  }

  public void updateSuffix() {
    CMIPermissionPlayerData.updateSuffix(this);
  }

  public String getGroupName() {
    return CMIPermissionPlayerData.getGroupName(this);
  }

  public String getNameColor() {
    return CMIPermissionPlayerData.getNameColor(this);
  }

  public String getPrefix() {
    return CMIPermissionPlayerData.getPrefix(this);
  }

  public String getSuffix() {
    return CMIPermissionPlayerData.getSuffix(this);
  }

  public String getCleanDisplayName() {
    if (this.nickName == null) {
      if (this.name != null) {
        return this.name;
      } else if (this.getPlayer(false) == null) {
        return this.getName();
      } else {
        return this.getPlayer(false).getDisplayName() == null
            ? this.getName()
            : this.getPlayer(false).getDisplayName();
      }
    } else {
      return CMIChatColor.translate(this.nickName);
    }
  }

  public void setDisplayName(String string) {
    if (string == null || !string.isEmpty()) {
      this.displayName = string;
    }
  }

  public String getDisplayName() {
    return this.getDisplayName(true);
  }

  public String getDisplayName(boolean flag) {
    return CMIChatColor.translate(this.getDisplayNameClean(flag));
  }

  public String getDisplayNameClean(boolean flag) {
    if (this.displayName != null && !this.displayName.isEmpty() && !flag) {
      return this.displayName;
    }

    if (flag) {
      this.updateDisplayName();
    }

    if (this.displayName != null && !this.displayName.isEmpty()) {
      return this.displayName;
    }

    if (this.nickName == null) {
      if (this.name != null) {
        return this.name;
      } else if (this.getPlayer(false) == null) {
        return this.getName();
      } else {
        return this.getPlayer(false).getDisplayName() == null
            ? this.getName()
            : this.getPlayer(false).getDisplayName();
      }
    } else {
      return this.nickName;
    }
  }

  private boolean timeToUpdate(UserDataUpdateType userDataUpdateType) {
    var hashMap = CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getLastInfoUpdate();
    Long updateTime = (Long) hashMap.get(userDataUpdateType);
    if (updateTime != null && updateTime + 1000L >= System.currentTimeMillis()) {
      return false;
    }

    hashMap.put(userDataUpdateType, System.currentTimeMillis());
    return true;
  }

  public String getNickName() {
    return this.nickName;
  }

  public void setNickName(String string, boolean flag) {
    if (this.nickName != null) {
      CMI.getInstance().getNickNameManager().removeNickName(this.nickName);
    }

    this.nickName = string;
    if (this.nickName != null) {
      CMI.getInstance().getNickNameManager().addNewNickName(string, this.uuid);
    }

    if (flag) {
      this.addForDelayedSave();
    }
  }

  @Deprecated
  public long getpTime() {
    return this.getPlayerTime();
  }

  public long getPlayerTime() {
    return this.pTime;
  }

  @Deprecated
  public void setpTime(long longValue) {
    this.setPlayerTime(longValue, true);
  }

  public void setPlayerTime(long longValue) {
    this.setPlayerTime(longValue, CMI.getInstance().isFullyLoaded());
  }

  @Deprecated
  public void setpTime(long longValue, boolean flag) {
    this.setPlayerTime(longValue, flag);
  }

  public void setPlayerTime(long longValue, boolean flag) {
    this.pTime = longValue;
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public boolean isCuffed() {
    return CMI.getInstance().getPlayerManager().isCuffed(this.getUniqueId());
  }

  public void setCuffed(boolean flag) {
    this.setCuffed(flag, true);
  }

  public void setCuffed(boolean flag, boolean scheduleSave) {
    if (flag) {
      CMI.getInstance().getPlayerManager().addCuffed(this.getUniqueId());
    } else {
      CMI.getInstance().getPlayerManager().removeCuffed(this.getUniqueId());
    }

    if (scheduleSave) {
      this.addForDelayedSave();
    }
  }

  public List<String> getLockedIps() {
    if (this.lockedIps == null) {
      this.lockedIps = new ArrayList<>();
    }

    return this.lockedIps;
  }

  public void setLockedIps(List<String> list) {
    this.lockedIps = list;
  }

  public void addLockedIps(String string) {
    if (string != null) {
      string = string.replace("_", ".");
      if (!this.getLockedIps().contains(string)) {
        this.getLockedIps().add(string);
      }

      this.addForDelayedSave();
    }
  }

  public boolean removeLockedIps(String string) {
    if (string == null) {
      return false;
    }

    this.addForDelayedSave();
    return this.getLockedIps().remove(string);
  }

  public boolean isJoinedCounter() {
    return CMICounter.isJoinedCounter(this.getUniqueId());
  }

  public void setJoinedCounter(boolean flag) {
    CMICounter.setJoinedCounter(this.getUniqueId(), flag);
  }

  public HashMap<String, Long> getCounter() {
    return CMICounter.getCounter(this.getUniqueId());
  }

  public Long getCounter(String string) {
    return this.getCounter().get(string);
  }

  public void addCounter(String string, Long amount) {
    this.getCounter().put(string, amount);
  }

  public long getAlertUntil() {
    CMIUserAlert cmiUserAlert = CMI.getInstance().getPlayerManager().getAlert(this.getUniqueId());
    return cmiUserAlert == null ? 0L : cmiUserAlert.getUntil();
  }

  public void setAlertUntil(long longValue) {
    this.setAlertUntil(longValue, true);
  }

  public void removeAlert() {
    CMI.getInstance().getPlayerManager().removeAlert(this.getUniqueId());
    this.addForDelayedSave();
  }

  public void setAlertUntil(long longValue, boolean flag) {
    this.setAlertUntil(longValue, null, flag);
  }

  public void setAlertUntil(long longValue, String string) {
    this.setAlertUntil(longValue, string, true);
  }

  public void setAlertUntil(long longValue, String string, boolean flag) {
    if (longValue > 0L) {
      CMI.getInstance().getPlayerManager().addAlert(this.getUniqueId(), longValue, string);
    } else {
      CMI.getInstance().getPlayerManager().removeAlert(this.getUniqueId());
    }

    if (flag) {
      this.addForDelayedSave();
    }
  }

  public String getAlertReason() {
    CMIUserAlert cmiUserAlert = CMI.getInstance().getPlayerManager().getAlert(this.getUniqueId());
    return cmiUserAlert == null ? null : cmiUserAlert.getReason();
  }

  public void setAlertReason(String string) {
    this.setAlertReason(string, true);
  }

  public void setAlertReason(String string, boolean flag) {
    CMIUserAlert cmiUserAlert = CMI.getInstance().getPlayerManager().getAlert(this.getUniqueId());
    if (cmiUserAlert != null) {
      cmiUserAlert.setReason(string);
    }

    if (flag) {
      this.addForDelayedSave();
    }
  }

  public boolean isGod() {
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      Player currentPlayer = this.getPlayer(false);
      if (currentPlayer == null) {
        return false;
      }

      try {
        return !currentPlayer.isOnline() ? false : currentPlayer.isInvulnerable();
      } catch (Exception exception) {
        exception.printStackTrace();
        return false;
      }
    } else {
      return CMI.getInstance().getNMS().getGodMode(this.getPlayer(false));
    }
  }

  @Deprecated
  public LinkedHashMap<Kit, CMIKitUsage> getKits() {
    LinkedHashMap<Kit, CMIKitUsage> linkedHashMap = new LinkedHashMap();

    for (CMIKitUsage cmiKitUsage : this.getKitsUsages()) {
      linkedHashMap.put(cmiKitUsage.getKit(), cmiKitUsage);
    }

    return linkedHashMap;
  }

  public Collection<CMIKitUsage> getKitsUsages() {
    return KitsManager.getUserKits(this.getUniqueId()).values();
  }

  public HashMap<String, Long> getKitsAsMap() {
    HashMap<String, Long> hashMap = new HashMap();

    for (CMIKitUsage cmiKitUsage : this.getKitsUsages()) {
      hashMap.put(
          cmiKitUsage.getKitName() + "-X-" + cmiKitUsage.getUsedTimes(),
          cmiKitUsage.getLastUsage());
    }

    return hashMap;
  }

  public void addKitsUsage(CMIKitUsage cmiKitUsage) {
    KitsManager.getUserKits(this.getUniqueId())
        .put(cmiKitUsage.getKitName().toLowerCase(), cmiKitUsage);
  }

  @Deprecated
  public void setKits(LinkedHashMap<Kit, CMIKitUsage> linkedHashMap) {
    KitsManager.getUserKits(this.getUniqueId()).clear();

    for (var entry : linkedHashMap.entrySet()) {
      this.addKitsUsage(
          new CMIKitUsage(
              (Kit) entry.getKey(),
              ((CMIKitUsage) entry.getValue()).getLastUsage(),
              ((CMIKitUsage) entry.getValue()).getUsedTimes()));
    }
  }

  public void addKit(Kit kit, Long lastUsage) {
    this.addKit(kit, lastUsage, true);
  }

  public void addKit(Kit kit, Long lastUsage, boolean flag) {
    this.addKit(kit, lastUsage, null, flag);
  }

  public void addKit(Kit kit, Long lastUsage, Integer integer, boolean flag) {
    this.addKit(kit.getConfigName(), lastUsage, integer, flag);
  }

  public void addKit(String string, Long lastUsage, Integer integer, boolean flag) {
    var list = CMI.getInstance().getKitsManager().getGroupedKits(string);
    CMIKitUsage cmiKitUsage = this.getKitUsage(string);

    for (Kit kit : list) {
      if (this.getLeftKitUseTimes(kit) != 0 && this.canUseKit(kit)) {
        if (cmiKitUsage == null) {
          cmiKitUsage = new CMIKitUsage(kit, lastUsage);
        }

        if (!kit.getConfigName().equalsIgnoreCase(string)) {
          if (integer == null) {
            cmiKitUsage.addUsedTimes();
          } else {
            cmiKitUsage.setUsedTimes(integer);
          }
        }

        cmiKitUsage.setLastUsage(lastUsage);
        this.addKitsUsage(cmiKitUsage);
      }
    }

    if (cmiKitUsage != null) {
      cmiKitUsage.setLastUsage(lastUsage);
      if (integer == null) {
        cmiKitUsage.addUsedTimes();
      } else {
        cmiKitUsage.setUsedTimes(integer);
      }
    } else {
      cmiKitUsage = new CMIKitUsage(string, lastUsage);
      this.addKitsUsage(cmiKitUsage);
      if (integer == null) {
        cmiKitUsage.addUsedTimes();
      } else {
        cmiKitUsage.setUsedTimes(integer);
      }
    }

    if (flag) {
      this.addForDelayedSave();
    }
  }

  public Long getKitTime(Kit kit) {
    if (kit == null) {
      return null;
    }

    Long kitTime = this.getKitUsage(kit) == null ? null : this.getKitUsage(kit).getLastUsage();
    if (kitTime == null) {
      return kitTime;
    }

    if (kitTime > System.currentTimeMillis()) {
      kitTime = System.currentTimeMillis() - 1000L;
    }

    return kitTime;
  }

  public Long getKitUseTimeIn(Kit kit) {
    Long kitTime = this.getKitTime(kit);
    if (kitTime == null) {
      return 0L;
    }

    long longValue = kitTime + kit.getDelay() * 1000L;
    longValue -= System.currentTimeMillis();
    return longValue < 0L ? 0L : longValue;
  }

  public boolean canUseKit(Kit kit) {
    Long kitTime = this.getKitTime(kit);
    if (kitTime == null) {
      return true;
    } else if (this.getLeftKitUseTimes(kit) == 0
        || CMI.getInstance().getKitsManager().getKit(this.player, kit.getConfigName(), false)
            == null) {
      return false;
    } else if (kitTime + kit.getDelay() * 1000L < System.currentTimeMillis()) {
      return true;
    } else {
      long longValue = kitTime + kit.getDelay() * 1000L;
      if (kit.getDelay() == 0L) {
        return true;
      } else if (longValue <= System.currentTimeMillis()) {
        return false;
      } else {
        return !this.isOnline()
            ? false
            : PermissionsManager.CMIPerm.kit_bypass_time.hasPermission(this.getPlayer());
      }
    }
  }

  private CMIKitUsage getKitUsage(Kit kit) {
    return kit == null ? null : this.getKitUsage(kit.getConfigName());
  }

  private CMIKitUsage getKitUsage(String string) {
    return KitsManager.getUserKits(this.getUniqueId()).get(string.toLowerCase());
  }

  public int getLeftKitUseTimes(Kit kit) {
    CMIKitUsage cmiKitUsage = this.getKitUsage(kit);
    if (cmiKitUsage != null && kit.getMaxUsages() >= 0) {
      return kit.getMaxUsages() - cmiKitUsage.getUsedTimes() < 0
          ? 0
          : kit.getMaxUsages() - cmiKitUsage.getUsedTimes();
    } else {
      return kit.getMaxUsages();
    }
  }

  public int getKitUseTimes(Kit kit) {
    CMIKitUsage cmiKitUsage = this.getKitUsage(kit);
    return cmiKitUsage == null ? 0 : cmiKitUsage.getUsedTimes();
  }

  public CMIUser resetKitUseTimes(Kit kit) {
    CMIKitUsage cmiKitUsage = this.getKitUsage(kit);
    if (cmiKitUsage == null) {
      return this;
    }

    cmiKitUsage.setUsedTimes(0);
    return this;
  }

  public PlayerCharge getPCharge() {
    return this.getPCharge(true);
  }

  public PlayerCharge getPCharge(boolean flag) {
    return CMI.getInstance().getSpawnerChargesManager().getPCharge(this, flag);
  }

  public long getTotalPlayTime() {
    return this.getTotalPlayTime(true);
  }

  public long getTotalPlayTime(boolean flag) {
    if (!CMI.getInstance().getConfigManager().PlayTimeFromStats) {
      if (this.isOnline()) {
        this.totalPlayTime = this.getPlayer().getTicksLived() * 50L;
      } else if (this.totalPlayTime <= 0L || flag) {
        int value = CMI.getInstance().getNMS().getTickLived(this.getUniqueId());
        if (value != 0) {
          if (this.totalPlayTime != value * 50L) {
            this.addForDelayedSave();
          }

          this.totalPlayTime = value * 50L;
        } else {
          this.totalPlayTime = 50L;
          this.addForDelayedSave();
        }
      }

      return this.totalPlayTime;
    } else {
      if (this.isOnline()) {
        if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
          if (statCheck == null) {
            statCheck = Statistic.valueOf("PLAY_ONE_MINUTE");
          }

          this.totalPlayTime = this.getPlayer().getStatistic(statCheck) * 50L;
        } else {
          this.totalPlayTime =
              this.getPlayer().getStatistic(Statistic.valueOf("PLAY_ONE_TICK")) * 50L;
        }
      } else if ((this.totalPlayTime <= 0L || flag) && this.player != null) {
        Long playedTime = 0L;
        if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
          playedTime = this.getPlayer().getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE")) * 50L;
        } else {
          playedTime = this.getPlayer().getStatistic(Statistic.valueOf("PLAY_ONE_TICK")) * 50L;
        }

        if (this.totalPlayTime != playedTime) {
          this.addForDelayedSave();
        }

        if (playedTime <= 0L) {
          this.totalPlayTime = 1000L;
          this.addForDelayedSave();
        } else {
          this.totalPlayTime = playedTime;
        }
      }

      return this.totalPlayTime;
    }
  }

  public long getTotalPlayTimeClean() {
    return this.totalPlayTime;
  }

  public void setTotalPlayTime(long longValue) {
    this.totalPlayTime = longValue;
  }

  public CmdCooldown getCommandCooldowns() {
    return CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getCommandCooldown();
  }

  public void addCommandCooldownByEndTime(String string, Long startTime, Long endTime) {
    this.getCommandCooldowns().addCooldown(string, startTime, endTime - startTime);
  }

  public long getTotemCooldown() {
    return CMIPlayerTotemData.getTotemCooldown(this.getUniqueId());
  }

  public void setTotemCooldown(long longValue) {
    CMIPlayerTotemData.setTotemCooldown(this.getUniqueId(), longValue);
  }

  public long getTotemWarmup() {
    return CMIPlayerTotemData.getTotemWarmup(this.getUniqueId());
  }

  public void setTotemWarmup(long longValue) {
    CMIPlayerTotemData.setTotemWarmup(this.getUniqueId(), longValue);
  }

  @Deprecated
  public boolean isShowTotemBar() {
    return this.getOptionState(PlayerOption.totemBossBar);
  }

  @Deprecated
  public void setShowTotemBar(boolean flag) {
    this.setOptionState(PlayerOption.totemBossBar, flag);
  }

  public long getTfly() {
    return this.tfly == -1L ? -1L : (this.tfly < System.currentTimeMillis() ? 0L : this.tfly);
  }

  public long getRawTempFlyTime() {
    return this.tfly;
  }

  public void setTfly(long longValue) {
    this.setTfly(longValue, CMI.getInstance().isFullyLoaded());
  }

  public void setTfly(long longValue, boolean flag) {
    this.tfly = longValue <= -1L ? -1L : longValue;
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public boolean isTgod() {
    return this.tgod == -1L ? true : this.tgod + 2000L > System.currentTimeMillis();
  }

  public long getTgod() {
    return this.tgod == -1L
        ? -1L
        : (this.tgod + 2000L < System.currentTimeMillis() ? 0L : this.tgod);
  }

  public long getRawTempGodTime() {
    return this.tgod;
  }

  public void setTgod(long longValue) {
    this.setTgod(longValue, CMI.getInstance().isFullyLoaded());
  }

  public void setTgod(long longValue, boolean flag) {
    this.tgod = longValue <= -1L ? -1L : longValue;
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public LinkedHashMap<String, CmiHome> getHomes() {
    if (this.homes == null) {
      this.homes = new LinkedHashMap<>();
    }

    return this.homes;
  }

  public int getTotalFavoriteHomeCount() {
    if (this.homes == null) {
      return 0;
    }

    int value = 0;

    for (CmiHome cmiHome : this.getHomes().values()) {
      if (cmiHome.isFavorite()) {
        value++;
      }
    }

    return value;
  }

  public Set<CmiHome> getHomesFromWorld(String string) {
    HashSet<CmiHome> hashSet = new HashSet();

    for (var entry : this.getHomes().entrySet()) {
      if (((CmiHome) entry.getValue()).getLoc().getWorldName().equalsIgnoreCase(string)) {
        hashSet.add((CmiHome) entry.getValue());
      }
    }

    return hashSet;
  }

  public int getValidHomeCount() {
    if (this.homes == null) {
      this.homes = new LinkedHashMap<>();
    }

    int value = 0;

    for (CmiHome cmiHome : this.homes.values()) {
      if (cmiHome.getLoc().isValid()) {
        value++;
      }
    }

    return value;
  }

  public int getLastHomeSlot() {
    int value = 0;

    for (var entry : this.homes.entrySet()) {
      if (((CmiHome) entry.getValue()).getSlot() != null
          && value < ((CmiHome) entry.getValue()).getSlot()) {
        value = ((CmiHome) entry.getValue()).getSlot();
      }
    }

    return value != 0 && value >= this.getHomes().size() ? value : this.getHomes().size();
  }

  public Set<CmiHome> getHomesBySlot(int value) {
    HashSet<CmiHome> hashSet = new HashSet();

    for (var entry : this.homes.entrySet()) {
      if (((CmiHome) entry.getValue()).getSlot() != null
          && ((CmiHome) entry.getValue()).getSlot() == value) {
        hashSet.add((CmiHome) entry.getValue());
      }
    }

    return hashSet;
  }

  public LinkedHashMap<String, CMILocation> getHomesAsMap() {
    LinkedHashMap<String, CMILocation> linkedHashMap = new LinkedHashMap();

    for (var entry : this.getHomes().entrySet()) {
      String string = (String) entry.getKey();
      if (((CmiHome) entry.getValue()).isPrivate()) {
        string = string + "$-p";
      }

      if (((CmiHome) entry.getValue()).getSlot() != null) {
        string = string + "$-" + ((CmiHome) entry.getValue()).getSlot();
      }

      if (((CmiHome) entry.getValue()).getMaterial() != null) {
        string = string + "$-" + ((CmiHome) entry.getValue()).getMaterial();
      }

      if (((CmiHome) entry.getValue()).isFavorite()) {
        string = string + "$-f";
      }

      linkedHashMap.put(string, ((CmiHome) entry.getValue()).getLoc());
    }

    return linkedHashMap;
  }

  public ArrayList<String> getHomesList() {
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.getHomes().entrySet()) {
      arrayList.add((String) entry.getKey());
    }

    return arrayList;
  }

  public CmiHome getHome(String string) {
    if (string == null) {
      return null;
    }

    for (var entry : this.getHomes().entrySet()) {
      if (((String) entry.getKey()).equalsIgnoreCase(string)) {
        return (CmiHome) entry.getValue();
      }
    }

    return null;
  }

  public CmiHome getBedHome() {
    for (var entry : this.getHomes().entrySet()) {
      if (((CmiHome) entry.getValue()).isBed()
          && Math.abs(((CmiHome) entry.getValue()).getLoc().getX() % 1.0) == 0.5
          && Math.abs(((CmiHome) entry.getValue()).getLoc().getZ() % 1.0) == 0.5) {
        return (CmiHome) entry.getValue();
      }
    }

    return null;
  }

  public void removeHome(String string) {
    this.removeHome(this.getHome(string));
  }

  public void removeHome(CmiHome cmiHome) {
    if (cmiHome != null) {
      CMIUserHomeRemoveEvent cmiUserHomeRemoveEvent = new CMIUserHomeRemoveEvent(this, cmiHome);
      CMI.getInstance().getServer().getPluginManager().callEvent(cmiUserHomeRemoveEvent);
      if (!cmiUserHomeRemoveEvent.isCancelled()) {
        this.getHomes().remove(cmiHome.getName());
      }
    }
  }

  @Deprecated
  public void addHome(String string, CmiHome cmiHome, boolean flag) {
    this.addHome(cmiHome, flag);
  }

  public void addHome(CmiHome cmiHome, boolean flag) {
    if (flag) {
      CMIUserHomeCreateEvent cmiUserHomeCreateEvent = new CMIUserHomeCreateEvent(this, cmiHome);
      CMI.getInstance().getServer().getPluginManager().callEvent(cmiUserHomeCreateEvent);
      if (cmiUserHomeCreateEvent.isCancelled()) {
        return;
      }
    }

    this.getHomes().put(cmiHome.getName(), cmiHome);
    if (cmiHome.isBed()) {
      CMI.getInstance().getHomeManager().addBedHome(this, cmiHome);
    }

    if (flag) {
      this.addForDelayedSave();
    }
  }

  public ChatColor getGlow() {
    return CMIPlayerGlowData.getGlow(this.getUniqueId());
  }

  public void setGlow(ChatColor chatColor, boolean flag) {
    CMIPlayerGlowData.setGlow(this.getUniqueId(), chatColor);
    CMIPlayerGlowData.applyGlow(this.getUniqueId());
    if (flag) {
      this.addForDelayedSave();
    }
  }

  @Deprecated
  public boolean isTeleportToggled() {
    return !this.getOptionState(PlayerOption.acceptingTPA);
  }

  @Deprecated
  public void setTeleportToggled(boolean flag, boolean ignored) {
    this.setOptionState(PlayerOption.acceptingTPA, flag);
  }

  public int getId() {
    return this.id;
  }

  public void setId(int value) {
    this.id = value;
    CMI.getInstance().getPlayerManager().addUser(this, value);
  }

  public int getInvId() {
    return this.invId;
  }

  public void setInvId(int value) {
    this.invId = value;
  }

  public int getPlayTimeId() {
    return this.playTimeId;
  }

  public void setPlayTimeId(int value) {
    this.playTimeId = value;
  }

  public List<PlayerNote> getNotes() {
    return this.getNotes(true);
  }

  public List<PlayerNote> getNotes(boolean flag) {
    if (this.notes == null) {
      this.notes = new ArrayList<>();
    }

    if (flag) {
      for (PlayerNote playerNote : new ArrayList<>(this.notes)) {
        long longValue = System.currentTimeMillis() - playerNote.getTime();
        if (longValue > CMI.getInstance().getConfigManager().getPlayerNotesExpiresIn()) {
          this.notes.remove(playerNote);
        }
      }
    }

    return this.notes;
  }

  public void setNotes(List<PlayerNote> list) {
    this.notes = list;
  }

  public void addNote(PlayerNote playerNote, boolean flag) {
    this.getNotes(flag).add(playerNote);
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public void removeMail(PlayerMail playerMail) {
    if (this.mail != null) {
      this.mail.remove(playerMail);
    }
  }

  public void clearMails() {
    if (this.mail != null) {
      this.mail.clear();
    }
  }

  public List<PlayerMail> getMails() {
    if (this.mail == null) {
      this.mail = new ArrayList<>();
    }

    var arrayList = new ArrayList<>(this.mail);
    TreeMap<Long, PlayerMail> treeMap = new TreeMap(Collections.reverseOrder());

    for (PlayerMail playerMail : arrayList) {
      long longValue = System.currentTimeMillis() - playerMail.getTime();
      if (longValue <= CMIMail.getMailExpiresInMS()
          && (playerMail.getKeepFor() <= 0L
              || playerMail.getKeepFor() * 1000L + playerMail.getTime()
                  >= System.currentTimeMillis())) {
        treeMap.put(playerMail.getTime(), playerMail);
      }
    }

    this.mail.clear();
    int value = 0;
    if (treeMap.size() > CMIMail.getMaxMail()) {
      value = treeMap.size() - CMIMail.getMaxMail();
    }

    for (Entry entry : treeMap.entrySet()) {
      if (--value < 0) {
        this.mail.add((PlayerMail) entry.getValue());
      }
    }

    return this.mail;
  }

  public void setMail(List<PlayerMail> list) {
    this.mail = list;
  }

  public void addMail(PlayerMail playerMail, boolean flag) {
    this.getMails().add(playerMail);
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public long getLastPatrol() {
    return CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getLastPatrol();
  }

  public void setPatroled() {
    CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).setLastPatrol(System.currentTimeMillis());
  }

  public long getStatistic(StatsManager.CMIStatistic cmiStatistic) {
    return this.getStatistic(cmiStatistic, null);
  }

  public long getStatistic(StatsManager.CMIStatistic cmiStatistic, Object object) {
    return CMIStats.getStat(this, cmiStatistic, object);
  }

  public CMIRank getNullRank() {
    return CMI.getInstance().getRankManager().getNullRank(this);
  }

  public CMIRank getRank() {
    return CMI.getInstance().getRankManager().getRank(this);
  }

  public CMIRank recalculateRank() {
    return CMI.getInstance().getRankManager().recalculateRank(this);
  }

  public void setRank(CMIRank cmiRank) {
    CMI.getInstance().getRankManager().setRank(this, cmiRank);
  }

  @Deprecated
  public boolean isTagSoundEnabled() {
    return this.getOptionState(PlayerOption.tagSound);
  }

  @Deprecated
  public void setTagSoundEnabled(boolean flag) {
    this.setOptionState(PlayerOption.tagSound, flag);
  }

  @Deprecated
  public boolean isShiftEditEnabled() {
    return this.getOptionState(PlayerOption.shiftSignEdit);
  }

  @Deprecated
  public void setShiftEditEnabled(boolean flag) {
    this.setOptionState(PlayerOption.shiftSignEdit, flag);
  }

  @Deprecated
  public boolean isSpy() {
    return this.getOptionState(PlayerOption.chatSpy);
  }

  @Deprecated
  public void setSpy(boolean flag) {
    this.setOptionState(PlayerOption.chatSpy, flag);
  }

  @Deprecated
  public boolean isCommandSpy() {
    return this.getOptionState(PlayerOption.cmdSpy);
  }

  @Deprecated
  public void setCommandSpy(boolean flag, boolean ignored) {
    this.setOptionState(PlayerOption.cmdSpy, flag);
  }

  @Deprecated
  public boolean isSignSpy() {
    return this.getOptionState(PlayerOption.signSpy);
  }

  @Deprecated
  public void setSignSpy(boolean flag, boolean ignored) {
    this.setOptionState(PlayerOption.signSpy, flag);
  }

  public boolean isSame(CommandSender commandSender) {
    return commandSender instanceof Player ? this.isSame((Player) commandSender) : false;
  }

  public boolean isSame(Player otherPlayer) {
    return otherPlayer.getUniqueId().equals(this.getUniqueId());
  }

  public boolean isAfk() {
    return CMI.getInstance().getAfkManager().isAfk(this.getUniqueId());
  }

  @Nullable
  public AfkInfo getAfkInfo() {
    return CMI.getInstance().getAfkManager().getAfkInfo(this.getUniqueId());
  }

  public AfkInfo getAfkInfoSafe() {
    return CMI.getInstance().getAfkManager().createAfkInfo(this.getUniqueId());
  }

  public void setAfk(boolean flag) {
    this.setAfk(flag, CMIAfkEnterEvent.AfkType.manual);
  }

  public void setAfk(boolean flag, CMIAfkEnterEvent.AfkType afkType) {
    this.setAfk(flag, afkType, true);
  }

  public void setAfk(boolean flag, CMIAfkEnterEvent.AfkType afkType, boolean runTriggers) {
    if (CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getAfkImunity()
        <= System.currentTimeMillis()) {
      if (flag) {
        if (!this.isAfk()) {
          if (CMIAfkEnterEvent.AfkType.auto.equals(afkType)
              && !PermissionsManager.CMIPerm.command_afk_auto.hasPermission(this.getPlayer())) {
            return;
          }

          CMIScheduler.runTask(
              CMI.getInstance(),
              () -> {
                CMIAfkEnterEvent cmiAfkEnterEvent =
                    new CMIAfkEnterEvent(
                        this.getPlayer(false),
                        new ArrayList<>(
                            CMIAfkEnterEvent.AfkType.auto.equals(afkType)
                                ? CMI.getInstance().getAfkManager().getAwayTrigerCommands()
                                : CMI.getInstance().getAfkManager().getManualAwayTrigerCommands()),
                        afkType);
                Bukkit.getServer().getPluginManager().callEvent(cmiAfkEnterEvent);
                if (!cmiAfkEnterEvent.isCancelled()) {
                  this.turnOnAfk(
                      afkType,
                      runTriggers ? cmiAfkEnterEvent.getAwayTrigerCommands() : new ArrayList<>());
                }
              });
        }
      } else if (this.isAfk()) {
        long longValue = this.getAfkInfo().getAfkFrom();
        CMIScheduler.runTask(
            CMI.getInstance(),
            () -> {
              if (this.isOnline() && this.getAfkInfo() != null) {
                var arrayList =
                    new ArrayList<>(
                        this.getAfkInfo().getType().equals(CMIAfkEnterEvent.AfkType.auto)
                            ? CMI.getInstance().getAfkManager().getAutoLeaveCommands()
                            : CMI.getInstance().getAfkManager().getManualLeaveCommands());
                CMIAfkLeaveEvent cmiAfkLeaveEvent =
                    new CMIAfkLeaveEvent(
                        this.getPlayer(false),
                        arrayList,
                        System.currentTimeMillis() - longValue,
                        this.getAfkInfo().getReason());
                Bukkit.getServer().getPluginManager().callEvent(cmiAfkLeaveEvent);
                this.turnOffAfk(
                    afkType,
                    runTriggers ? cmiAfkLeaveEvent.getLeaveTrigerCommands() : new ArrayList<>());
              }
            });
      }
    }
  }

  private void turnOnAfk(CMIAfkEnterEvent.AfkType afkType, List<String> list) {
    if (CMIModule.vanish.isEnabled()
        && this.isVanished()
        && !VanishManager.is(this.getUniqueId(), VanishAction.afkcommands)) {
      list.clear();
    }

    CMI.getInstance().getAfkManager().setUserToAfk(this, list);
    if (SleepStats.SleepingExcludeAfk) {
      try {
        this.player.setSleepingIgnored(true);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    AfkInfo afkInfo = this.getAfkInfoSafe();
    afkInfo.setAfkFrom(System.currentTimeMillis());
    afkInfo.setPlaytimeAtAfkStart(this.getTotalPlayTime());
    afkInfo.setType(afkType);
    if (this.isOnline()) {
      afkInfo.setAfkAt(new CMIVectorInt3D(this.getPlayer().getLocation()));
    }

    CMI.getInstance().getAfkManager().showTitle(this, true);
  }

  private void turnOffAfk(CMIAfkEnterEvent.AfkType afkType, List<String> list) {
    CMI.getInstance().getAfkManager().hideTitle(this);
    if (CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getAfkImunity()
        <= System.currentTimeMillis()) {
      if (CMIModule.vanish.isEnabled()
          && this.isVanished()
          && !VanishManager.is(this.getUniqueId(), VanishAction.afkcommands)) {
        list.clear();
      }

      CMI.getInstance().getAfkManager().removeUserFromAfk(this, list);
      if (SleepStats.SleepingExcludeAfk) {
        try {
          if (!this.isVanished()
              || CMIModule.vanish.isEnabled()
                  && !VanishManager.is(this.getUniqueId(), VanishAction.sleepIgnore)) {
            this.player.setSleepingIgnored(false);
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      this.getAfkInfoSafe().setAfkFrom(null);
      this.getAfkInfoSafe().setKicksPerformed(0);
      this.getAfkInfoSafe().setType(afkType);
      this.getAfkInfoSafe().setAfkAt(null);
      this.setAfkReason(null);
      CMI.getInstance().getAfkManager().removeAfkInfo(this.getUniqueId());
    }
  }

  public void setAfkReason(String string) {
    this.getAfkInfoSafe().setReason(string);
  }

  public String getAfkReason() {
    return this.getAfkInfo() == null ? null : this.getAfkInfo().getReason();
  }

  public boolean isBanned() {
    if (this.getBanEntry() != null) {
      if (this.getBanEntry().getExpiration() == null) {
        return false;
      }

      if (this.getBanEntry().getExpiration() > System.currentTimeMillis()
          || this.getBanEntry().getExpiration() == -1L) {
        return true;
      }
    }

    return false;
  }

  public CMIUser setBanned() {
    return this.setBanned(null, null, null, null);
  }

  public CMIUser setBanned(String string) {
    return this.setBanned(string, null, null, null);
  }

  public CMIUser setBanned(Date date) {
    return this.setBanned(null, null, date, null);
  }

  public CMIUser setBanned(String string, Date date) {
    return this.setBanned(string, null, date, null);
  }

  public CMIUser setBanned(String string, CommandSender commandSender, Date date) {
    return commandSender instanceof Player
        ? this.setBanned(
            string, getUser((Player) commandSender), date, new Date(System.currentTimeMillis()))
        : this.setBanned(string, null, date, new Date(System.currentTimeMillis()));
  }

  public CMIUser setBanned(String string, CMIUser cmiUser, Date date, Date unbanDate) {
    return CMIBanRecords.setBanned(this, string, cmiUser, date, unbanDate);
  }

  public CMIUser unBan() {
    return CMIBanRecords.unBan(this);
  }

  public CMIBanEntry getBanEntryRaw() {
    return CMIBanRecords.getBanEntryRaw(this.getUniqueId());
  }

  public CMIBanEntry getBanEntry() {
    return this.getBanEntry(true);
  }

  public void updateBanEntry(BanEntry banEntry) {
    CMIBanRecords.updateBanEntry(this, banEntry);
  }

  public CMIBanEntry getBanEntry(boolean flag) {
    return CMIBanRecords.getBanEntry(this, flag);
  }

  public Set<UUID> getIgnores() {
    if (this.ignores == null) {
      this.ignores = new HashSet<>();
    }

    return this.ignores;
  }

  public String getIgnoresString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (UUID ignoredUuid : this.getIgnores()) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(";");
      }

      stringBuilder.append(ignoredUuid.toString());
    }

    return stringBuilder.toString();
  }

  public void setIgnores(Set<UUID> set) {
    this.ignores = set;
  }

  public void addIgnore(UUID ignoredUuid, boolean flag) {
    this.getIgnores().add(ignoredUuid);
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public void removeIgnore(UUID ignoredUuid) {
    this.getIgnores().remove(ignoredUuid);
  }

  public boolean isIgnoring(UUID ignoredUuid) {
    return this.getIgnores().contains(ignoredUuid)
        || this.getIgnores().contains(CMI.getInstance().getPlayerManager().getEmptyUserUUID());
  }

  public boolean isSilenceMode() {
    return this.silenceMode;
  }

  public void setSilenceMode(boolean flag) {
    this.silenceMode = flag;
  }

  public CMIVanish getVanish() {
    return CMI.getInstance().getVanishManager().getOrCreateVanish(this);
  }

  public CMIVanish getVanishOrNull() {
    return CMI.getInstance().getVanishManager().getVanish(this.getUniqueId());
  }

  public boolean isVanished() {
    boolean flag = this.isCMIVanished();
    if (!flag && this.isOnline()) {
      long longValue = System.currentTimeMillis();
      if (longValue - this.lastCheck < 5000L) {
        return this.cachedVanished;
      }

      try {
        Player currentPlayer = this.getPlayer();
        if (currentPlayer != null && currentPlayer.hasMetadata("vanished")) {
          for (MetadataValue metadataValue : currentPlayer.getMetadata("vanished")) {
            if (metadataValue.asBoolean()) {
              flag = true;
              break;
            }
          }
        }
      } catch (Exception | Error value) {
        value.printStackTrace();
      }

      this.cachedVanished = flag;
      this.lastCheck = longValue;
    }

    return flag;
  }

  public boolean isCMIVanished() {
    return CMIModule.vanish.isEnabled()
        && VanishManager.is(this.getUniqueId(), VanishAction.isVanished);
  }

  public void setVanished(boolean flag) {
    this.setVanished(flag, true);
  }

  public void setVanished(boolean flag, boolean silent) {
    if (CMIModule.vanish.isEnabled()) {
      if (flag) {
        this.getVanish().setVanishedFrom(System.currentTimeMillis());
        CMI.getInstance().getVanishManager().addPlayer(this.uuid);
        if (!this.getVanish().is(VanishAction.PrivateMessages)) {
          CMIMessageReplies.removeMessageReplyTo(this.getName());
        }
      } else {
        this.getVanish().setVanishedFrom(null);
        CMI.getInstance().getVanishManager().removePlayer(this.uuid);
      }

      this.getVanish().set(VanishAction.isVanished, flag);
      if (this.isOnline()) {
        this.getPlayer().setMetadata("vanished", new FixedMetadataValue(CMI.getInstance(), flag));
      }

      CMI.getInstance().getVanishManager().applyVanish(this, silent);
    }
  }

  public void updateVanishMode() {
    if (VanishManager.is(this.getUniqueId(), VanishAction.isVanished)) {
      CMI.getInstance().getVanishManager().addPlayer(this.uuid);
    } else {
      CMI.getInstance().getVanishManager().removePlayer(this.uuid);
    }

    if (this.isOnline()) {
      this.getPlayer()
          .setMetadata(
              "vanished",
              new FixedMetadataValue(
                  CMI.getInstance(),
                  VanishManager.is(this.getUniqueId(), VanishAction.isVanished)));
    }

    CMI.getInstance().getVanishManager().applyVanish(this, true, false);
  }

  public boolean isSitting() {
    return !this.isOnline()
        ? false
        : CMI.getInstance().getAnimationManager().isSitting(this.getPlayer());
  }

  public String getMutedReason() {
    return CMIMuteData.getMutedReason(this.getUniqueId());
  }

  public void setMutedReason(String string) {
    CMIMuteData.setMutedReason(this.getUniqueId(), string);
  }

  public Long getMutedUntil() {
    return CMIMuteData.getMutedUntil(this.getUniqueId());
  }

  public boolean isMuted() {
    return CMIMuteData.isMuted(this.getUniqueId());
  }

  public void setMutedUntil(long longValue) {
    CMIMuteData.setMutedUntil(this.getUniqueId(), longValue);
  }

  public long getShadowMutedUntil() {
    return CMIMuteData.getShadowMutedUntil(this.getUniqueId());
  }

  public void setShadowMutedUntil(long longValue) {
    CMIMuteData.setShadowMutedUntil(this.getUniqueId(), longValue);
  }

  public boolean isShadowMuted() {
    return CMIMuteData.isShadowMuted(this.getUniqueId());
  }

  public Double deposit(Double amount, CMIUser cmiUser) {
    return this.deposit(null, amount, cmiUser);
  }

  public Double deposit(Double amount) {
    return this.deposit(null, amount);
  }

  public Double deposit(String string, Double amount) {
    return this.deposit(string, amount, null);
  }

  public Double deposit(String string, Double amount, CMIUser cmiUser) {
    amount = amount == null ? 0.0 : amount;
    return this.getEconomyAccount().deposit(string, amount, cmiUser);
  }

  public Double withdraw(Double amount, CMIUser cmiUser) {
    return this.withdraw(null, amount, cmiUser);
  }

  public Double withdraw(Double amount) {
    return this.withdraw(null, amount);
  }

  public Double withdraw(String string, Double amount) {
    return this.withdraw(string, amount, null);
  }

  public Double withdraw(String string, Double amount, CMIUser cmiUser) {
    amount = amount == null ? 0.0 : amount;
    return this.getEconomyAccount().withdraw(string, amount, cmiUser);
  }

  @Deprecated
  public boolean has(Double amount) {
    return this.hasMoney(amount);
  }

  public boolean hasMoney(Double amount) {
    return amount == 0.0 ? true : this.has(null, amount);
  }

  public boolean has(String string, Double amount) {
    return this.getEconomyAccount().has(string, amount);
  }

  public Double getBalance() {
    return this.getBalance(null);
  }

  public Double getBalance(String string) {
    return this.getEconomyAccount().getBalance(string);
  }

  public String getFormatedBalance() {
    return this.getFormatedBalance(
        this.getWorld() == null ? "CMIGlobal" : this.getWorld().getName());
  }

  public String getFormatedBalance(boolean flag) {
    return this.getFormatedBalance(
        this.getWorld() == null ? "CMIGlobal" : this.getWorld().getName(), flag);
  }

  public String getFormatedBalance(String string) {
    return this.getFormatedBalance(string, false);
  }

  public String getFormatedBalance(String string, boolean flag) {
    return this.getEconomyAccount().getFormatedBalance(string, flag);
  }

  public CMIEconomyAcount getEconomyAccount() {
    if (this.economy == null) {
      this.economy = new CMIEconomyAcount(this);
    }

    return this.economy;
  }

  public World getWorld() {
    if (this.isOnline() && this.getPlayer() != null) {
      return this.getPlayer().getLocation().getWorld();
    } else {
      return this.getLogOutLocation() != null ? this.getLogOutLocation().getWorld() : null;
    }
  }

  public boolean respawn() {
    if (!this.isOnline()) {
      return false;
    } else {
      Player currentPlayer = this.getPlayer();
      if (PermissionsManager.CMIPerm.autorespawn.hasSetPermission(currentPlayer)
          && !(currentPlayer.getHealth() > 0.0)
          && System.currentTimeMillis()
                  - CMIPlayerTempData.computeIfAbsent(currentPlayer.getUniqueId()).getLastRespawn()
              >= 1000L) {
        CMIPlayerTempData.computeIfAbsent(currentPlayer.getUniqueId())
            .setLastRespawn(System.currentTimeMillis());
        CMI.getInstance().getReflectionManager().respawn(currentPlayer);
        return true;
      } else {
        return false;
      }
    }
  }

  public void removeBossBar(String string) {
    CMILib.getInstance().getBossBarManager().removeBossBar(this.getPlayer(false), string);
  }

  public void removeBossBar(BossBarInfo bossBarInfo) {
    CMILib.getInstance().getBossBarManager().removeBossBar(this.getPlayer(false), bossBarInfo);
  }

  public void addBossBar(BossBarInfo bossBarInfo) {
    if (this.getPlayer(false) != null && this.getPlayer(false).isOnline()) {
      CMILib.getInstance().getBossBarManager().addBossBar(this.getPlayer(false), bossBarInfo);
    }
  }

  public synchronized BossBarInfo getBossBar(String string) {
    return CMILib.getInstance().getBossBarManager().getBossBar(this.getPlayer(false), string);
  }

  public synchronized ConcurrentHashMap<String, BossBarInfo> getBossBarInfo() {
    return CMILib.getInstance().getBossBarManager().getBossBarInfo(this.getPlayer(false));
  }

  public synchronized void hideBossBars() {
    if (!this.isOnline()) {
      this.clearBossMaps();
    } else {
      CMILib.getInstance().getBossBarManager().hideBossBars(this.getPlayer(false));
    }
  }

  public void clearBossMaps() {
    CMILib.getInstance().getBossBarManager().clearBossMaps(this.getPlayer(false));
  }

  public CMIPlayerInventory getInventory() {
    return new CMIPlayerInventory(this);
  }

  public void updateInventory() {
    if (this.isOnline()) {
      this.getPlayer().updateInventory();
    }
  }

  public void setLevel(int value) {
    CMIExperience.setLevel(this.getPlayer(), value);
  }

  public void setExp(long longValue) {
    CMIExperience.setExp(this.getPlayer(), longValue);
  }

  public void addExp(long longValue) {
    CMIExperience.addExp(this.getPlayer(), longValue);
  }

  public void takeExp(long longValue) {
    CMIExperience.takeExp(this.getPlayer(), longValue);
  }

  public long getMissingExp() {
    return CMIExperience.getMissingExp(this.getPlayer());
  }

  public int getLevel() {
    return CMIExperience.getLevel(this.getPlayer());
  }

  public long getExpForCurrentLevel() {
    return CMIExperience.getExpForCurrentLevel(this.getPlayer());
  }

  @Deprecated
  public long getExp() {
    return this.getTotalExp();
  }

  public long getTotalExp() {
    return this.getPlayer() == null ? 0L : CMIExperience.getTotalExp(this.player);
  }

  public FlightCharge getFlightCharge() {
    return CMI.getInstance().getFlightChargeManager().getFlightCharge(this.getUniqueId());
  }

  public boolean hasPermission(PermissionsManager.CMIPerm cmiPerm) {
    return cmiPerm.hasPermission(this.getPlayer(false));
  }

  public boolean hasSetPermission(PermissionsManager.CMIPerm cmiPerm) {
    return cmiPerm.hasSetPermission(this.getPlayer(false));
  }

  public boolean hasMeta() {
    return PlayerMeta.getMetaRaw(this.getUniqueId()) != null
        && PlayerMeta.getMetaRaw(this.getUniqueId()).containsValues();
  }

  public PlayerMeta getMeta() {
    return PlayerMeta.getMeta(this.getUniqueId());
  }

  public String getMeta(String string) {
    return this.getMeta().getValue(string);
  }

  public boolean isAllowFlight() {
    return this.isOnline() ? this.getPlayer().getAllowFlight() : this.AllowFlight;
  }

  public void setAllowFlight(boolean flag) {
    this.AllowFlight = flag;
    if (this.isOnline()
        && (flag || !this.getPlayer().getGameMode().toString().equalsIgnoreCase("SPECTATOR"))) {
      this.getPlayer().setAllowFlight(flag);
    }
  }

  public void setGameMode(GameMode gameMode) {
    CMI.getInstance().getReflectionManager().setGameMode(this.getPlayer(), gameMode);
  }

  public boolean wasFlying() {
    return this.Flying;
  }

  public boolean hadAllowFlight() {
    return this.hadAllowFlight;
  }

  public void setHadAllowFlight(boolean flag) {
    this.hadAllowFlight = flag;
  }

  public boolean setWasFlying(boolean flag) {
    return this.Flying == flag;
  }

  public boolean isFlying() {
    return this.isOnline() ? this.getPlayer().isFlying() : this.Flying;
  }

  public void setFlying(boolean flag) {
    this.Flying = flag;
    if (this.isOnline()) {
      if (flag) {
        this.getPlayer().setAllowFlight(true);
        this.getPlayer().setFlying(true);
      } else {
        if (!this.getPlayer().getGameMode().toString().equalsIgnoreCase("SPECTATOR")) {
          this.getPlayer().setFlying(false);
          this.getPlayer().setAllowFlight(false);
        }
      }
    }
  }

  @Deprecated
  public SavedInventories getSavedInventories() {
    return CMI.getInstance().getSavedInventoryManager().getInventories(this);
  }

  public int getVotifierVotes() {
    return this.votifierVotes;
  }

  public void setVotifierVotes(int value) {
    this.votifierVotes = value;
  }

  public boolean addVotifierVote() {
    this.votifierVotes = this.getVotifierVotes() + 1;
    if (CMI.getInstance().getVotifierManager().getMaxVotesInADay() > 0) {
      if (this.votifierVotesByTime == null) {
        this.votifierVotesByTime = new ArrayList<>();
      }

      if (this.votifierVotesByTime.size()
          >= CMI.getInstance().getVotifierManager().getMaxVotesInADay()) {
        Long lastVote = this.votifierVotesByTime.get(0);
        if (lastVote != null && lastVote + 86400000L > System.currentTimeMillis()) {
          return false;
        }
      }

      this.votifierVotesByTime.add(System.currentTimeMillis());

      while (this.votifierVotesByTime.size()
          > CMI.getInstance().getVotifierManager().getMaxVotesInADay() + 1) {
        this.votifierVotesByTime.remove(0);
      }
    }

    return true;
  }

  public int getDailyVoteCount() {
    if (this.votifierVotesByTime != null && !this.votifierVotesByTime.isEmpty()) {
      while (this.votifierVotesByTime.size()
          > CMI.getInstance().getVotifierManager().getMaxVotesInADay() + 1) {
        this.votifierVotesByTime.remove(0);
      }

      while (!this.votifierVotesByTime.isEmpty()
          && this.votifierVotesByTime.get(0) < System.currentTimeMillis() - 86400000L) {
        this.votifierVotesByTime.remove(0);
      }

      return this.votifierVotesByTime.size();
    } else {
      return 0;
    }
  }

  public Long getLastDailyVoteTime() {
    if (this.votifierVotesByTime != null && !this.votifierVotesByTime.isEmpty()) {
      while (this.votifierVotesByTime.size()
          > CMI.getInstance().getVotifierManager().getMaxVotesInADay() + 1) {
        this.votifierVotesByTime.remove(0);
      }

      if (this.votifierVotesByTime.size()
          >= CMI.getInstance().getVotifierManager().getMaxVotesInADay()) {
        Long lastVote = this.votifierVotesByTime.get(0);
        if (lastVote != null && lastVote + 86400000L > System.currentTimeMillis()) {
          return lastVote;
        }
      }

      return null;
    } else {
      return null;
    }
  }

  @Nullable
  private CMIUserJailData getJailData() {
    return CMI.getInstance().getJailManager().getJailData(this.getUniqueId());
  }

  private CMIUserJailData getOrCreateJailData() {
    return CMI.getInstance().getJailManager().getOrCreateJailData(this.getUniqueId());
  }

  public boolean isJailed() {
    if (this.getJailData() != null && this.getOrCreateJailData().getJailedForTime() > 0L) {
      long longValue = this.getOrCreateJailData().getJailedForTime();
      if (CMI.getInstance().getJailManager().isCountWhileOffline()
          && !this.isOnline()
          && this.getLastLogoff() > 0L) {
        longValue -= System.currentTimeMillis() - this.getLastLogoff();
      }

      if (longValue <= 0L) {
        this.getOrCreateJailData().setJailedForTime(0L);
        CMIScheduler.runTask(CMI.getInstance(), this::unjail);
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public boolean jail(Long jailSeconds, CMIJail cmiJail, Integer integer) {
    return this.jail(jailSeconds, cmiJail, integer, null);
  }

  public boolean jail(Long jailSeconds, CMIJail cmiJail, Integer integer, String string) {
    return this.jail(jailSeconds, cmiJail, integer, string, null);
  }

  public boolean jail(
      Long jailSeconds, CMIJail cmiJail, Integer integer, String string, UUID jailedBy) {
    this.getOrCreateJailData().setJailedReason(string);
    this.getOrCreateJailData().setJailedBy(jailedBy);
    boolean flag =
        CMI.getInstance()
            .getJailManager()
            .placePlayerIntoJail(this, cmiJail, integer, jailSeconds * 1000L);
    if (flag) {
      this.setJailedForTime(jailSeconds * 1000L, true);
    }

    return flag;
  }

  public void unjail() {
    CMI.getInstance().getJailManager().removePlayerFromJail(this);
  }

  public Long getJailedUntil() {
    return System.currentTimeMillis() + this.getJailedForTime();
  }

  public void updateJailedTimeOnJoin() {
    if (this.isJailed()) {
      long longValue = this.getJailData().getJailedForTime();
      if (CMI.getInstance().getJailManager().isCountWhileOffline() && this.getLastLogoff() > 0L) {
        longValue -= System.currentTimeMillis() - this.getLastLogoff();
      }

      if (longValue <= 0L) {
        this.getJailData().setJailedForTime(0L);
        CMI.getInstance().getJailManager().removePlayerFromJail(this);
      } else {
        this.getJailData().setJailedForTime(longValue);
      }
    }
  }

  public long getJailedForTime() {
    if (!this.isJailed()) {
      return 0L;
    }

    long longValue = this.getJailData().getJailedForTime();
    if (CMI.getInstance().getJailManager().isCountWhileOffline()
        && !this.isOnline()
        && this.getLastLogoff() > 0L) {
      longValue -= System.currentTimeMillis() - this.getLastLogoff();
    }

    if (longValue <= 0L) {
      this.getJailData().setJailedForTime(0L);
    }

    return longValue <= 0L ? 0L : longValue;
  }

  public void setJailedForTime(long longValue) {
    this.setJailedForTime(longValue, false);
  }

  public void setJailedForTime(long longValue, boolean flag) {
    if (longValue > 1300000000000L) {
      longValue -= System.currentTimeMillis();
    }

    if (longValue < 0L) {
      longValue = 0L;
    }

    this.getOrCreateJailData().setJailedForTime(longValue);
    if (flag) {
      this.addForDelayedSave();
    }
  }

  public CMIJailCell getCell() {
    return this.getJailData() == null ? null : this.getJailData().getCell();
  }

  public void setCell(CMIJailCell cmiJailCell) {
    if (this.getOrCreateJailData().getCell() != null) {
      this.getOrCreateJailData().getCell().removeJailed(this.getUniqueId());
    }

    if (cmiJailCell != null) {
      cmiJailCell.addJailed(this.getUniqueId());
    }

    this.getOrCreateJailData().setCell(cmiJailCell);
  }

  public Boolean isFakeAccount() {
    return this.isFakeAccount;
  }

  public void setFakeAccount(boolean flag) {
    this.isFakeAccount = flag;
  }

  public CMIPlayTime getCMIPlayTime() {
    return CMI.getInstance().getPlayTimeManager().getCMIPlayTime(this.getUniqueId());
  }

  public void setCMIPlayTime(CMIPlayTime cmiPlayTime) {
    CMI.getInstance().getPlayTimeManager().setCMIPlayTime(this.getUniqueId(), cmiPlayTime);
  }

  public void updatePlayTime() {
    this.getCMIPlayTime().updatePlayTime();
  }

  public long getPlayTimeOptimizedOn() {
    return CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getPlayTimeOptimized();
  }

  public void setPlayTimeOptimizedOn(long longValue) {
    CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).setPlayTimeOptimized(longValue);
  }

  @Deprecated
  public boolean isNoPayToggled() {
    return !this.getOptionState(PlayerOption.acceptingMoney);
  }

  @Deprecated
  public void setNoPayToggled(boolean flag) {
    this.setOptionState(PlayerOption.acceptingMoney, flag);
  }

  public String getJailedReason() {
    if (this.getJailData() != null && !this.isJailed()) {
      this.getJailData().setJailedReason(null);
      this.getJailData().setJailedBy(null);
    }

    return !this.isJailed() ? null : this.getOrCreateJailData().getJailedReason();
  }

  public void setJailedReason(String string) {
    if (string != null) {
      string = string.replace(";;", "");
    }

    this.getOrCreateJailData().setJailedReason(string);
  }

  public ItemStack getItemInHand() {
    return CMIItemStack.getItemInMainHand(this.getPlayer());
  }

  public void setItemInHand(ItemStack itemStack) {
    if (this.getPlayer() != null) {
      CMIItemStack.setItemInMainHand(this.getPlayer(), itemStack);
    }
  }

  public void setItemInOffHand(ItemStack itemStack) {
    if (this.getPlayer() != null && Version.isCurrentHigher(Version.v1_8_R3)) {
      CMIItemStack.setItemInOffHand(this.getPlayer(), itemStack);
    }
  }

  public ItemStack getItemInOffHand() {
    return Version.isCurrentHigher(Version.v1_8_R3)
            && this.getPlayer() != null
            && this.getPlayer().getInventory() != null
        ? CMIItemStack.getItemInOffHand(this.getPlayer())
        : null;
  }

  public boolean hasSetSkin() {
    return this.skin != null;
  }

  public String getSkin() {
    return this.skin;
  }

  public void setSkin(String string) {
    this.skin = string;
  }

  public int getPlayTimeRewardId() {
    return this.playTimerewardId;
  }

  public void setPlayTimeRewardId(int value) {
    this.playTimerewardId = value;
  }

  public String getPTRRString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (var entry : this.getRepeatableRewards().entrySet()) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(";");
      }

      stringBuilder.append((String) entry.getKey() + "%%" + entry.getValue());
    }

    return stringBuilder.toString();
  }

  public String getPTROString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (String string : this.getOneTimeRewards()) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(";");
      }

      stringBuilder.append(string);
    }

    return stringBuilder.toString();
  }

  public HashMap<String, Long> getRepeatableRewards() {
    if (this.repeatableRewards == null) {
      this.repeatableRewards = new HashMap<>();
    }

    return this.repeatableRewards;
  }

  public void addRepeatableReward(String string, long longValue) {
    this.getRepeatableRewards().put(string.toLowerCase(), longValue);
  }

  public Long getRepeatableRewardTime(String string) {
    return this.getRepeatableRewards().get(string.toLowerCase());
  }

  public Set<String> getOneTimeRewards() {
    if (this.oneTimeRewards == null) {
      this.oneTimeRewards = new HashSet<>();
    }

    return this.oneTimeRewards;
  }

  public void addOneTimeReward(String string) {
    this.getOneTimeRewards().add(string.toLowerCase());
  }

  public boolean isClaimedReward(PTROneTime ptrOneTime) {
    return this.oneTimeRewards == null
        ? false
        : this.oneTimeRewards.contains(ptrOneTime.getName().toLowerCase());
  }

  public void unloadData() {
    if (this.player != null && !this.isOnline()) {
      CMI.getInstance().getNMS().unloadData(this.player);
      this.player = null;
    }
  }

  public String getNamePlatePrefix() {
    return CMINamePlate.getNamePlatePrefix(this.getUniqueId());
  }

  public void setNamePlatePrefix(String string) {
    CMINamePlate.setNamePlatePrefix(this.getUniqueId(), string);
  }

  public String getNamePlateSuffix() {
    return CMINamePlate.getNamePlateSuffix(this.getUniqueId());
  }

  public void setNamePlateSuffix(String string) {
    CMINamePlate.setNamePlateSuffix(this.getUniqueId(), string);
  }

  public ChatColor getNamePlateNameColor() {
    return CMINamePlate.getNamePlateNameColor(this.getUniqueId());
  }

  public void setNamePlateNameColor(ChatColor chatColor) {
    CMINamePlate.setNamePlateNameColor(this.getUniqueId(), chatColor);
  }

  public void setCollision(boolean flag) {
    this.collision = flag;
    this.applyCollision();
  }

  public boolean isCollidable() {
    return this.collision;
  }

  @Deprecated
  public Boolean getCollision() {
    return this.isCollidable();
  }

  private void applyCollision() {
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)
        && !Config.DisableTeamManagement
        && this.getName(false) != null
        && this.isOnline()) {
      Team team = CMI.getInstance().getSB().getPlayerTeam(this.getPlayer());
      if (team == null) {
        team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("CMINP" + this.getId());
      }

      if (team != null) {
        if (this.isCollidable()) {
          team.setOption(Option.COLLISION_RULE, OptionStatus.ALWAYS);
        } else {
          team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }
      }
    }
  }

  @Deprecated
  public void reaplyNamePlate() {
    this.reapplyNamePlate();
  }

  public void reapplyNamePlate() {
    if (this.isOnline() && !Config.DisableTeamManagement && !Version.isFolia()) {
      this.applyCollision();
      Team team = CMI.getInstance().getSB().getPlayerTeam(this.getPlayer());
      if (team == null) {
        Team existingTeam =
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("CMINP" + this.getId());
        if (existingTeam == null) {
          try {
            team =
                Bukkit.getScoreboardManager()
                    .getMainScoreboard()
                    .registerNewTeam("CMINP" + this.getId());
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      }

      if (team == null) {
        team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("CMINP" + this.getId());
      }

      if (team != null) {
        try {
          if (!team.hasEntry(this.getName(false))) {
            team.addEntry(this.getName(false));
          }

          String string = this.getNamePlatePrefix();
          String suffix = this.getNamePlateSuffix();
          if (CMIModule.namePlates.isEnabled()) {
            string =
                CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(this.getPlayer(false), string);
            suffix =
                CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(this.getPlayer(false), suffix);
            if (string != null) {
              byte b = 0;
              if (this.getNamePlateNameColor() != null) {
                b += 2;
              }

              if (this.getGlow() != null) {
                b += 2;
              }

              if (!Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
                if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                  if (string.length() + b > 64) {
                    string = string.substring(0, 63 - b);
                  }
                } else if (string.length() + b > 16) {
                  string = string.substring(0, 15 - b);
                }
              }
            }

            if (suffix != null && !Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                if (suffix.length() >= 64) {
                  suffix = suffix.substring(0, 63);
                }
              } else if (suffix.length() >= 16) {
                suffix = suffix.substring(0, 15);
              }
            }
          }

          try {
            if (CMIModule.namePlates.isEnabled() && this.getNamePlateNameColor() != null) {
              team.setColor(this.getNamePlateNameColor());
            }

            if (this.getGlow() != null) {
              team.setColor(this.getGlow());
            }
          } catch (Throwable throwable) {
          }

          try {
            team.setCanSeeFriendlyInvisibles(false);
          } catch (Throwable throwable2) {
          }

          if (CMIModule.namePlates.isEnabled()) {
            team.setPrefix(
                CMIChatColor.translate(
                        string != null && CMIModule.namePlates.isEnabled() ? string : "")
                    + (this.getNamePlateNameColor() == null ? "" : this.getNamePlateNameColor())
                    + (this.getGlow() == null ? "" : this.getGlow()));
            team.setSuffix(
                CMIChatColor.translate(
                    suffix != null && CMIModule.namePlates.isEnabled() ? suffix : ""));
          }
        } catch (Throwable throwable3) {
        }
      }
    }
  }

  public double getLookDirectionInDegrees() {
    Location location = this.getPlayer().getLocation();
    return location == null ? 0.0 : (int) ((location.getYaw() + 540.0F) % 360.0F * 100.0F) / 100.0;
  }

  public CMIDirection getLookDirection() {
    double number = this.getLookDirectionInDegrees();
    return CMIDirection.getFromAngle(number);
  }

  @Nullable
  public List<CMIPlayerWarning> getWarnings() {
    var list = CMI.getInstance().getWarningManager().getWarnings(this.getUniqueId());
    return list == null ? Collections.emptyList() : list;
  }

  public void setWarnings(List<CMIPlayerWarning> list) {
    CMI.getInstance().getWarningManager().setWarnings(this.getUniqueId(), list);
  }

  public int getWarningPoints() {
    if (this.getWarnings() == null) {
      return 0;
    }

    int value = 0;

    for (CMIPlayerWarning cmiPlayerWarning : this.getWarnings()) {
      value += cmiPlayerWarning.getCategory().getPoints();
    }

    return value;
  }

  public int removeWarning(CMIPlayerWarning cmiPlayerWarning) {
    if (this.getWarnings() == null) {
      return 0;
    }

    this.getWarnings().remove(cmiPlayerWarning);
    return this.getWarningPoints();
  }

  public CMIPlayerWarning addWarning(
      String string, String reason, CMIWarningCategory cmiWarningCategory) {
    CMIPlayerWarning cmiPlayerWarning = new CMIPlayerWarning();
    cmiPlayerWarning.setGivenAt(System.currentTimeMillis());
    cmiPlayerWarning.setGivenBy(string);
    cmiPlayerWarning.setCategory(
        cmiWarningCategory == null
            ? CMI.getInstance().getWarningManager().getDefaultCategory()
            : cmiWarningCategory);
    cmiPlayerWarning.setReason(reason.isEmpty() ? null : reason);
    return this.addWarning(cmiPlayerWarning);
  }

  public CMIPlayerWarning addWarning(CMIPlayerWarning cmiPlayerWarning) {
    CMI.getInstance()
        .getWarningManager()
        .getOrCreateWarnings(this.getUniqueId())
        .add(cmiPlayerWarning);
    return cmiPlayerWarning;
  }

  @Deprecated
  public HashMap<String, Long> getWarningsAsMap() {
    HashMap<String, Long> hashMap = new HashMap();
    if (this.getWarnings() == null) {
      return hashMap;
    }

    for (CMIPlayerWarning cmiPlayerWarning : this.getWarnings()) {
      String string =
          cmiPlayerWarning.getGivenBy()
              + (cmiPlayerWarning.getReason() != null
                  ? "-X-" + cmiPlayerWarning.getReason()
                  : "-X-")
              + (cmiPlayerWarning.getCategory() != null
                  ? "-X-" + cmiPlayerWarning.getCategory().getName()
                  : "-X-");
      int value = 0;
      String warningKey = string;

      while (hashMap.containsKey(string)) {
        string = warningKey + "-X-" + value;
        value++;
      }

      hashMap.put(string, cmiPlayerWarning.getGivenAt());
    }

    return hashMap;
  }

  public String getWarningsSerialized() {
    return CMI.getInstance().getWarningManager().serializeWarnings(this.getWarnings());
  }

  @Deprecated
  public boolean isAcceptingPM() {
    return this.getOptionState(PlayerOption.acceptingPM);
  }

  @Deprecated
  public void setAcceptingPM(boolean flag) {
    this.setOptionState(PlayerOption.acceptingPM, flag);
  }

  public Long getGotLastDamageAt() {
    return CMI.getInstance().getPlayerCombatManager().getGotLastDamageAt(this.getUniqueId());
  }

  public void setGotLastDamageAt(Long time) {
    CMI.getInstance().getPlayerCombatManager().setGotLastDamageAt(this.getUniqueId(), time);
  }

  public boolean isInCombatWithPlayer() {
    return CMI.getInstance().getPlayerCombatManager().isInCombatWithPlayer(this.getUniqueId());
  }

  public Long getGotLastDamageFromPlayer() {
    return CMI.getInstance()
        .getPlayerCombatManager()
        .getGotLastDamageFromPlayer(this.getUniqueId());
  }

  public void setGotLastDamageFromPlayer(Long time) {
    CMI.getInstance().getPlayerCombatManager().setGotLastDamageFromPlayer(this.getUniqueId(), time);
  }

  public void setDidLastDamageToPlayer(Long time) {
    CMI.getInstance().getPlayerCombatManager().setDidLastDamageToPlayer(this.getUniqueId(), time);
  }

  public boolean isInCombatWithMob() {
    return CMI.getInstance().getPlayerCombatManager().isInCombatWithMob(this.getUniqueId());
  }

  public Long getGotLastDamageFromMob() {
    return CMI.getInstance().getPlayerCombatManager().getGotLastDamageFromMob(this.getUniqueId());
  }

  public void setGotLastDamageFromMob(Long time) {
    CMI.getInstance().getPlayerCombatManager().setGotLastDamageFromMob(this.getUniqueId(), time);
  }

  public void setDidLastDamageToEntity(Long time) {
    CMI.getInstance().getPlayerCombatManager().setDidLastDamageToEntity(this.getUniqueId(), time);
  }

  public long getLeftCombatTime() {
    return CMI.getInstance().getPlayerCombatManager().getLeftCombatTime(this.getUniqueId());
  }

  public void sendMessage(String string) {
    if (!string.isEmpty()) {
      if (this.isOnline()) {
        CMIMessages.sendMessage(this.getPlayer(), string);
      }
    }
  }

  public void sendMessage(String string, String format, Object... objects) {
    if (this.isOnline()) {
      CMILC.info(string, this, format, objects);
    }
  }

  public CompletableFuture<CMITeleportFeedback> teleport(Entity entity) {
    return this.teleport(entity.getLocation(), CMITeleportType.Unknown);
  }

  public CompletableFuture<CMITeleportFeedback> teleport(Location location) {
    return this.teleport(location, CMITeleportType.Unknown);
  }

  public CompletableFuture<CMITeleportFeedback> teleport(
      Location location, CMITeleportType cmiTeleportType) {
    return CMI.getInstance()
        .getTeleportHandler()
        .teleportPlayer(this.getPlayer(), location, cmiTeleportType);
  }

  public HashMap<UUID, PlayerKillCount> getKills() {
    return CMI.getInstance().getPlayerCombatManager().getKills(this.getUniqueId());
  }

  public void addHeadDropCount(UUID victimUuid) {
    this.getKills()
        .computeIfAbsent(victimUuid, key -> new PlayerKillCount(victimUuid))
        .addHeadDropCount();
  }

  public void addKill(UUID victimUuid) {
    this.getKills().computeIfAbsent(victimUuid, key -> new PlayerKillCount(victimUuid)).addKill();
  }

  @Deprecated
  public double getHeadDropChance(UUID victimUuid) {
    return this.getHeadDropChance(victimUuid, true);
  }

  public double getHeadDropChance(UUID victimUuid, boolean flag) {
    PlayerKillCount playerKillCount = this.getKills().get(victimUuid);
    Double chance = null;
    if (flag) {
      ItemStack itemStack =
          CMILib.getInstance().getReflectionManager().getItemInMainHand(this.getPlayer());

      try {
        if (itemStack != null) {
          int value = itemStack.getEnchantmentLevel(CMIEnchantEnum.LOOTING.getEnchantment());
          if (value > 0) {
            chance = CMI.getInstance().getPlayerCombatManager().getLootMobBonus().get(value);
          }
        }
      } catch (Exception | Error value2) {
      }
    }

    if (playerKillCount == null) {
      double number = CMI.getInstance().getPlayerCombatManager().getPlayerDropHeadPercentage();
      if (chance != null) {
        number += number * chance;
      }

      if (number > 100.0) {
        number = 100.0;
      }

      return number;
    } else {
      int headDrops = playerKillCount.getHeadDropCount();
      double baseChance = CMI.getInstance().getPlayerCombatManager().getPlayerDropHeadPercentage();
      double lowerChance = CMI.getInstance().getPlayerCombatManager().getLowerChanceWithEachKill();

      for (int killIndex = 0; killIndex < headDrops; killIndex++) {
        baseChance -= lowerChance * baseChance / 100.0;
      }

      if (chance != null) {
        baseChance += baseChance * chance;
      }

      if (baseChance > 100.0) {
        baseChance = 100.0;
      }

      return baseChance;
    }
  }

  public HashMap<EntityType, EntityKillCount> getEntityKills() {
    return CMI.getInstance().getPlayerCombatManager().getEntityKills(this.getUniqueId());
  }

  public void addEntityKill(EntityType entityType) {
    EntityKillCount entityKillCount = this.getEntityKills().get(entityType);
    if (entityKillCount == null) {
      entityKillCount = new EntityKillCount(entityType);
      this.getEntityKills().put(entityType, entityKillCount);
    }

    entityKillCount.addKill();
  }

  @Deprecated
  public double getEntityHeadDropChance(EntityType entityType) {
    return this.getEntityHeadDropChance(entityType, true);
  }

  public double getEntityHeadDropChance(EntityType entityType, Boolean useLooting) {
    EntityKillCount entityKillCount = this.getEntityKills().get(entityType);
    Double chance = null;
    if (useLooting) {
      ItemStack itemStack =
          CMILib.getInstance().getReflectionManager().getItemInMainHand(this.getPlayer());

      try {
        if (itemStack != null) {
          int value = itemStack.getEnchantmentLevel(CMIEnchantEnum.LOOTING.getEnchantment());
          if (value > 0) {
            chance = CMI.getInstance().getPlayerCombatManager().getLootMobBonus().get(value);
          }
        }
      } catch (Exception | Error value2) {
      }
    }

    if (entityKillCount == null) {
      double number =
          CMI.getInstance().getPlayerCombatManager().getEntityDropHeadPercentage(entityType);
      if (chance != null) {
        number += number * chance;
      }

      if (number > 100.0) {
        number = 100.0;
      }

      return number;
    } else {
      int headDrops = entityKillCount.getHeadDropCount();
      double baseChance =
          CMI.getInstance().getPlayerCombatManager().getEntityDropHeadPercentage(entityType);
      double lowerChance =
          CMI.getInstance().getPlayerCombatManager().getEntityHeadDropChanceLowering(entityType);

      for (int killIndex = 0; killIndex < headDrops; killIndex++) {
        baseChance -= lowerChance * baseChance / 100.0;
      }

      if (chance != null) {
        baseChance += baseChance * chance;
      }

      if (baseChance > 100.0) {
        baseChance = 100.0;
      }

      return baseChance;
    }
  }

  @Deprecated
  public String getTagName() {
    return this.getName(false);
  }

  @Deprecated
  public void setTagName(String string) {}

  public long getAfkImunityUntil() {
    return CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getAfkImunity();
  }

  public void addFlyToCache(String string, boolean flag, boolean flying) {
    this.tempFlyModes.put(string, new worldFlyState(this.getPlayer().getGameMode(), flag, flying));
  }

  public worldFlyState getFlyCachedMode(String string) {
    return this.tempFlyModes.get(string);
  }

  public worldFlyState removeFlyCachedMode(String string) {
    return this.tempFlyModes.remove(string);
  }

  public void clearCachedFlyModes() {
    this.tempFlyModes.clear();
  }

  public CMIChatRoom getChatRoom() {
    return CMI.getInstance().getChatFormatManager().getChatRoom(this.getUniqueId());
  }

  public boolean leaveChatRoom() {
    return CMI.getInstance().getChatFormatManager().leaveChatRoom(this.getUniqueId());
  }

  public void joinChatRoom(String string) {
    CMI.getInstance().getChatFormatManager().joinChatRoom(this, string);
  }

  public void setChatRoom(CMIChatRoom cmiChatRoom) {
    CMI.getInstance().getChatFormatManager().setChatRoom(this, cmiChatRoom);
  }

  public void dropItemNearPlayer(ItemStack itemStack) {
    Location location = this.getPlayer().getLocation();
    Vector vector = location.getDirection().clone();
    vector.multiply(0.4);
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        location,
        () -> {
          Item item =
              this.getPlayer()
                  .getWorld()
                  .dropItemNaturally(location.clone().add(0.0, 1.0, 0.0).add(vector), itemStack);

          try {
            if (this.isOnline()) {
              PlayerDropItemEvent playerDropItemEvent =
                  new PlayerDropItemEvent(this.getPlayer(), item);
              Bukkit.getServer().getPluginManager().callEvent(playerDropItemEvent);
            }
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        });
  }

  public void addItemDonationFrom(CMIItemDonations cmiItemDonations) {
    CMIItemDonations.addItemDonation(cmiItemDonations);
  }

  public HashMap<UUID, CMIItemDonations> getPendingDonations() {
    return CMIItemDonations.getPendingDonations(this.getUniqueId());
  }

  public long getTeleportInvulnerability() {
    return this.teleportInvulnerability;
  }

  public void setTeleportInvulnerability(long longValue) {
    this.teleportInvulnerability = longValue;
  }

  @Deprecated
  public Boolean isToggledOffBBCompass() {
    return this.getOptionState(PlayerOption.totemBossBar);
  }

  @Deprecated
  public void setToggledOffBBCompass(Boolean toggled) {
    this.setOptionState(PlayerOption.totemBossBar, toggled);
  }

  public TreeMap<Long, damageInformation> getLastDamage() {
    return damageInformation.getLastDamage(this.getUniqueId());
  }

  public damageInformation getLastDamage(damageInformation damageInformation) {
    return damageInformation.getLastDamage(this.getUniqueId(), damageInformation);
  }

  public void addLastDamage(damageInformation damageInformation) {
    damageInformation.addLastDamage(this.getUniqueId(), damageInformation);
  }

  public void resetLastDamage() {
    damageInformation.cacheClear(this.getUniqueId());
  }

  @Nullable
  public static CMIUser getUser(String string) {
    return CMI.getInstance().getPlayerManager().getUser(string);
  }

  public static CMIUser getUser(OfflinePlayer offlinePlayer) {
    return CMI.getInstance().getPlayerManager().getUser(offlinePlayer);
  }

  public static CMIUser getUser(Player targetPlayer) {
    return CMI.getInstance().getPlayerManager().getUser(targetPlayer);
  }

  @Nullable
  public static CMIUser getUser(UUID playerUuid) {
    return CMI.getInstance().getPlayerManager().getUser(playerUuid);
  }

  @NotNull
  public Boolean getOptionState(PlayerOption playerOption) {
    if (this.options == null) {
      return playerOption.isEnabled();
    }

    Boolean enabled = this.options.get(playerOption);
    return enabled == null ? playerOption.isEnabled() : enabled;
  }

  public HashMap<PlayerOption, Boolean> getOptions() {
    if (this.options == null) {
      this.options = new HashMap<>();
    }

    return this.options;
  }

  public void setOptionState(PlayerOption playerOption, boolean flag) {
    if (playerOption.isEnabled() != flag) {
      this.getOptions().put(playerOption, flag);
    } else {
      this.getOptions().remove(playerOption);
    }

    playerOption.afterToggle(this, flag);
  }

  public void setOptions(HashMap<PlayerOption, Boolean> hashMap) {
    if (hashMap != null && !hashMap.isEmpty()) {
      for (var entry : hashMap.entrySet()) {
        this.setOptionState((PlayerOption) entry.getKey(), (Boolean) entry.getValue());
      }
    } else {
      this.options = new HashMap<>();
    }
  }

  @Deprecated
  public CMIChatColor getChatColor() {
    CMIPlayerMessageColor cmiPlayerMessageColor =
        CMI.getInstance().getChatManager().getColorFromCache(this.getUniqueId());
    return cmiPlayerMessageColor == null ? null : cmiPlayerMessageColor.getColor();
  }

  @Deprecated
  public void setChatColor(CMIChatColor cmiChatColor) {
    if (cmiChatColor == null) {
      CMI.getInstance().getChatManager().removeFromCache(this.getUniqueId());
    } else {
      CMI.getInstance()
          .getChatManager()
          .addToCache(this.getUniqueId(), new CMIPlayerMessageColor(cmiChatColor));
    }
  }

  public void setChatMessageFormat(CMIPlayerMessageColor cmiPlayerMessageColor) {
    if (cmiPlayerMessageColor != null && cmiPlayerMessageColor.getColor() != null) {
      CMI.getInstance().getChatManager().addToCache(this.getUniqueId(), cmiPlayerMessageColor);
    } else {
      CMI.getInstance().getChatManager().removeFromCache(this.getUniqueId());
    }
  }

  public CMIPlayerMessageColor getChatMessageFormat() {
    return CMI.getInstance().getChatManager().getColorFromCache(this.getUniqueId());
  }

  public String getChatMessageFormatString() {
    CMIPlayerMessageColor cmiPlayerMessageColor =
        CMI.getInstance().getChatManager().getColorFromCache(this.getUniqueId());
    return cmiPlayerMessageColor == null ? "" : cmiPlayerMessageColor.toString();
  }

  public UUID getJailedBy() {
    return !this.isJailed() ? null : this.getJailData().getJailedBy();
  }

  public void setJailedBy(UUID jailer) {
    this.getOrCreateJailData().setJailedBy(jailer);
  }

  public static Player getOnlinePlayer(CommandSender commandSender, String string) {
    return getOnlinePlayer(commandSender, string, false);
  }

  public static Player getOnlinePlayer(CommandSender commandSender, String string, boolean flag) {
    Player onlinePlayer = getOnlinePlayer(string, flag);
    if (commandSender instanceof Player && onlinePlayer != null) {
      return ((Player) commandSender).canSee(onlinePlayer) ? onlinePlayer : null;
    } else {
      return onlinePlayer;
    }
  }

  public static Player getOnlinePlayer(String string) {
    return getOnlinePlayer(string, false);
  }

  public static Player getOnlinePlayer(String string, boolean flag) {
    if (string == null) {
      return null;
    }

    Player onlinePlayer = Bukkit.getPlayerExact(string);
    if (onlinePlayer != null) {
      return onlinePlayer;
    }

    UUID ownerUuid = CMI.getInstance().getNickNameManager().whoUsesName(string);
    if (ownerUuid != null) {
      CMIUser cmiUser = getUser(ownerUuid);
      if (cmiUser != null && cmiUser.isOnline()) {
        return cmiUser.getPlayer();
      }
    }

    onlinePlayer = Bukkit.getPlayer(string);
    if (onlinePlayer != null && (flag || onlinePlayer.getName().equalsIgnoreCase(string))) {
      return (double) string.length() / onlinePlayer.getName().length() < 0.5 ? null : onlinePlayer;
    } else {
      return null;
    }
  }

  public static Player getOnlinePlayer(UUID playerUuid) {
    return Bukkit.getPlayer(playerUuid);
  }

  public static Player getPlayer(String string) {
    Player exactPlayer = Bukkit.getPlayerExact(string);
    if (exactPlayer != null) {
      return exactPlayer;
    }

    exactPlayer = Bukkit.getPlayer(string);
    if (exactPlayer != null) {
      return exactPlayer;
    }

    string = string.toLowerCase();
    if (CMI.getInstance().getConfigManager().isPartialPlayerName()) {
      for (Player candidate : Bukkit.getOnlinePlayers()) {
        CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(candidate);
        if (cmiUser.getName(false).toLowerCase().contains(string)) {
          return candidate;
        }
      }
    }

    UUID ownerUuid = CMI.getInstance().getNickNameManager().whoUsesName(string);
    if (ownerUuid != null) {
      CMIUser ownerUser = CMI.getInstance().getPlayerManager().getUser(ownerUuid);
      if (ownerUser != null && ownerUser.isOnline()) {
        return ownerUser.getPlayer();
      }
    }

    return null;
  }

  public String getBungeeBackLocation() {
    return CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).getBungeeBackLocation();
  }

  public void setBungeeBackLocation(String string) {
    CMIPlayerTempData.computeIfAbsent(this.getUniqueId()).setBungeeBackLocation(string);
  }

  public static Collection<CMIUser> getUsers() {
    return CMI.getInstance().getPlayerManager().getAllUsers().values();
  }

  public static CompletableFuture<OfflinePlayer> getOfflinePlayer(String string) {
    return CompletableFuture.supplyAsync(
        () -> {
          UUID ownerUuid = CMI.getInstance().getNickNameManager().whoUsesName(string);
          return ownerUuid != null
              ? Bukkit.getOfflinePlayer(ownerUuid)
              : Bukkit.getOfflinePlayer(string);
        });
  }

  public static CompletableFuture<Void> closeInventory(@NotNull Player targetPlayer) {
    return targetPlayer != null && targetPlayer.isOnline()
        ? CMIScheduler.runAtLocation(
            CMI.getInstance(),
            targetPlayer.getLocation(),
            () -> {
              if (targetPlayer.isOnline()) {
                targetPlayer.closeInventory();
              }
            })
        : CompletableFuture.completedFuture(null);
  }
}
