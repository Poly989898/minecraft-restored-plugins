package com.Zrips.CMI;

import com.Zrips.CMI.Containers.CMIJson;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIUserAlert;
import com.Zrips.CMI.Containers.PlayerMail;
import com.Zrips.CMI.Containers.PlayerNote;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Chat.CMIPlayerMessageColor;
import com.Zrips.CMI.Modules.CmdCooldown.CmdCooldown;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.Modules.DataBase.DBManager;
import com.Zrips.CMI.Modules.DataBase_NEW.CMITablesFieldsType;
import com.Zrips.CMI.Modules.Economy.CMIEconomyAcount;
import com.Zrips.CMI.Modules.Economy.EconomyManager;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.Modules.Kits.KitsManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;
import com.Zrips.CMI.Modules.Warnings.CMIWarningCategory;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.list.mail;
import com.Zrips.CMI.utils.CMINPCChecker;
import com.Zrips.CMI.utils.DateFormat;
import com.google.common.base.Charsets;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerManager {
  private int realUserCount = 0;
  private Map<UUID, CMIUser> users = Collections.synchronizedMap(new HashMap<>());
  private Map<String, CMIUser> usersName = Collections.synchronizedMap(new HashMap<>());
  private Map<Integer, CMIUser> usersIds = Collections.synchronizedMap(new HashMap<>());
  private Map<String, List<CMIUser>> duplicateUserNames =
      Collections.synchronizedMap(new HashMap<>());
  private Set<UUID> cuffed = new HashSet<>();
  private Set<UUID> socialSpy = new HashSet<>();
  private Set<UUID> commandSpy = new HashSet<>();
  private Set<UUID> signSpy = new HashSet<>();
  private HashMap<UUID, CMIUserAlert> userAlerts = new HashMap<>();
  protected Player fakeOperator = null;
  private final UUID emptyUserUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
  public static final UUID oldFakeUserUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff");
  private final UUID fakeUserUUID = UUID.fromString("000003e8-223f-21ee-a-2eb5a363657c");
  private static final String fakeUserName = "CMI-Fake-Operator";
  private CMI plugin;
  public static HashMap<DBDAO.UserTablesFields, Long> timer = new HashMap<>();
  public static final String mapKeySeparator = "%%";
  private static final String mapKeySeparatorForsave = "T9C";
  private static final String sectionSeparator = ":";
  private static final String sectionSeparatorForSave = "T8C";
  public static final String lineSeparator = ";";
  private static final String lineSeparatorForSave = "T7C";
  public static final String multiSeparator = "-X-";
  private static Pattern kitPattern = Pattern.compile("(\\S+)(-X-)(\\d)");
  List<String> duplicates = new ArrayList<>();
  static DecimalFormat decimalFormat = new DecimalFormat("0.00");
  HashMap<UUID, CMITask> delaySSTrigger = new HashMap<>();
  HashMap<UUID, CMITask> delayCSTrigger = new HashMap<>();
  HashMap<UUID, CMITask> delaySignSTrigger = new HashMap<>();
  private static List<String> hardBlackList = new ArrayList<>();

  static {
    hardBlackList.add("/l");
    hardBlackList.add("/login");
    hardBlackList.add("/register");
  }

  public PlayerManager(CMI cmi) {
    this.plugin = cmi;
  }

  @Deprecated
  public void updateUserName(CMIUser cmiUser, String string) {
    this.updateUserName(cmiUser, cmiUser.getName(false), string);
  }

  public void updateUserName(CMIUser cmiUser, String oldName, String newName) {
    if (oldName != null) {
      this.usersName.remove(oldName.toLowerCase());
      if (!oldName.equalsIgnoreCase(newName)) {
        for (var entry : this.duplicateUserNames.entrySet()) {
          ((List) entry.getValue()).remove(cmiUser);
        }
      }
    }

    cmiUser.setName(newName);
    this.usersName.put(newName.toLowerCase(), cmiUser);
    cmiUser.addForDelayedSave();
  }

  public void clearData() {
    this.users.clear();
    this.usersName.clear();
    this.duplicateUserNames.clear();
    this.cuffed.clear();
  }

  public synchronized Map<UUID, CMIUser> getAllUsers() {
    return this.users;
  }

  public boolean isDuplicatedUserName(String string) {
    return this.duplicateUserNames.containsKey(string.toLowerCase());
  }

  public Map<String, List<CMIUser>> getDuplicatedUsers() {
    return this.duplicateUserNames;
  }

  public CMIUser getDuplicatedUser(String name) {
    if (!this.duplicateUserNames.containsKey(name.toLowerCase())) {
      return null;
    }

    CMIUser cmiUser = null;

    for (CMIUser onlineDuplicate : this.duplicateUserNames.get(name.toLowerCase())) {
      if (onlineDuplicate.isOnline()) {
        return onlineDuplicate;
      }
    }

    for (CMIUser duplicateUser : this.duplicateUserNames.get(name.toLowerCase())) {
      if (cmiUser == null) {
        cmiUser = duplicateUser;
      }

      if (duplicateUser.getName() != null) {
        long lastLogoffAge = System.currentTimeMillis() - cmiUser.getLastLogoffClean();
        long duplicateLogoffAge = System.currentTimeMillis() - duplicateUser.getLastLogoffClean();
        if (lastLogoffAge > duplicateLogoffAge) {
          cmiUser = duplicateUser;
        }
      }
    }

    return cmiUser;
  }

  public boolean switchPlayerData(UUID uuid, UUID otherUuid) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
    if (uuid == null) {
      return false;
    }

    CMIUser otherUser = this.plugin.getPlayerManager().getUser(otherUuid);
    return otherUuid == null ? false : this.switchPlayerData(cmiUser, otherUser);
  }

  public boolean switchPlayerData(CMIUser cmiUser, CMIUser otherUser) {
    if (cmiUser != null && otherUser != null && !cmiUser.equals(otherUser)) {
      this.plugin.getDbManager().getDB().updateUserName(cmiUser.getId(), otherUser.getName(false));
      this.plugin.getDbManager().getDB().updateUserName(otherUser.getId(), cmiUser.getName(false));
      this.plugin.getDbManager().getDB().updateUUID(cmiUser.getId(), otherUser.getUniqueId());
      this.plugin.getDbManager().getDB().updateUUID(otherUser.getId(), cmiUser.getUniqueId());
      this.plugin.getPlayerManager().softRemoveUser(cmiUser, false);
      this.plugin.getPlayerManager().softRemoveUser(otherUser, false);
      this.plugin.getDbManager().getDB().loadUser(cmiUser.getId());
      this.plugin.getDbManager().getDB().loadUser(otherUser.getId());
      return true;
    } else {
      return false;
    }
  }

  public boolean addDuplicatedUser(CMIUser cmiUser) {
    if (cmiUser.getName() == null) {
      return false;
    }

    List<CMIUser> list = new ArrayList();
    if (this.duplicateUserNames.containsKey(cmiUser.getName().toLowerCase())) {
      list = this.duplicateUserNames.get(cmiUser.getName().toLowerCase());
    }

    boolean alreadyListed = false;

    for (CMIUser existingUser : list) {
      if (existingUser.getUniqueId().equals(cmiUser.getUniqueId())) {
        alreadyListed = true;
      }
    }

    if (!alreadyListed) {
      list.add(cmiUser);
    }

    this.duplicateUserNames.put(cmiUser.getName().toLowerCase(), list);
    return true;
  }

  public int getTotalUserCount() {
    return this.realUserCount;
  }

  public CMIUser softRemoveUser(UUID uuid) {
    return this.softRemoveUser(this.getUser(uuid));
  }

  public CMIUser softRemoveUser(UUID uuid, boolean flag) {
    return this.softRemoveUser(this.getUser(uuid), flag);
  }

  public CMIUser softRemoveUser(CMIUser cmiUser) {
    return this.softRemoveUser(cmiUser, true);
  }

  public CMIUser softRemoveUser(CMIUser cmiUser, boolean flag) {
    if (cmiUser == null) {
      return null;
    }

    if (cmiUser.getUniqueId() != null) {
      this.users.remove(cmiUser.getUniqueId());
    }

    if (!cmiUser.isFakeAccount()) {
      this.realUserCount--;
    }

    if (cmiUser.getName() != null) {
      this.usersName.remove(cmiUser.getName().toLowerCase());
    }

    if (cmiUser.getId() != 0) {
      this.usersIds.remove(cmiUser.getId());
    }

    if (flag) {
      this.plugin.getDbManager().getDB().removeUser(cmiUser.getId());
    }

    return cmiUser;
  }

  public void removeUser(CMIUser cmiUser) {
    if (cmiUser != null) {
      if (cmiUser.getUniqueId() != null) {
        this.users.remove(cmiUser.getUniqueId());
      }

      if (!cmiUser.isFakeAccount()) {
        this.realUserCount--;
      }

      if (cmiUser.getName() != null) {
        this.usersName.remove(cmiUser.getName().toLowerCase());
      }

      if (cmiUser.getId() != 0) {
        this.usersIds.remove(cmiUser.getId());
      }

      File file =
          new File(
              this.plugin.getDataFolder(),
              "userData" + File.separator + cmiUser.getUniqueId().toString() + ".yml");
      if (file.exists()) {
        file.delete();
      }

      this.plugin.getDbManager().getDB().removeUser(cmiUser.getId());
    }
  }

  public CMIUser getByName(String name) {
    return name == null ? null : this.usersName.get(name.toLowerCase());
  }

  public void addUser(CMIUser cmiUser) {
    if (cmiUser != null && cmiUser.getUniqueId() != null) {
      CMIUser existing = this.getByName(cmiUser.getName());
      if (cmiUser.getName() != null
          && existing != null
          && existing.getUniqueId() != null
          && !existing.getUniqueId().equals(cmiUser.getUniqueId())) {
        this.addDuplicatedUser(cmiUser);
        this.addDuplicatedUser(existing);
      }

      this.users.put(cmiUser.getUniqueId(), cmiUser);
      if (!cmiUser.isFakeAccount()) {
        this.realUserCount++;
      }

      if (cmiUser.getName() != null) {
        if (!this.isDuplicatedUserName(cmiUser.getName())) {
          this.usersName.put(cmiUser.getName().toLowerCase(), cmiUser);
        } else if (existing != null) {
          long existingAge = System.currentTimeMillis() - existing.getLastLogoffClean();
          long newAge = System.currentTimeMillis() - cmiUser.getLastLogoffClean();
          if (existingAge > newAge) {
            this.usersName.put(cmiUser.getName().toLowerCase(), cmiUser);
          }
        }
      }
    }
  }

  public CMIUser getUser(Integer integer) {
    return this.usersIds.get(integer);
  }

  public void addUser(CMIUser cmiUser, Integer integer) {
    if (integer > 0) {
      this.usersIds.put(integer, cmiUser);
    }
  }

  public CMIUser getUserByAproxName(String name) {
    return this.getUser(name, true, false, false, false, true);
  }

  public CMIUser getUser(String name) {
    return this.getUser(name, true, false, false, true, true);
  }

  public CMIUser getUser(String name, boolean checkDuplicates) {
    return this.getUser(name, checkDuplicates, false, false, true, true);
  }

  @Deprecated
  public CMIUser getUser(String name, boolean checkDuplicates, boolean deprecatedFlag) {
    return this.getUser(name, checkDuplicates, deprecatedFlag, false, true, true);
  }

  public CMIUser getUser(
      String name,
      boolean checkDuplicates,
      @Deprecated boolean deprecatedFlag,
      boolean createIfMissing,
      boolean exactMatch) {
    return this.getUser(name, checkDuplicates, deprecatedFlag, createIfMissing, exactMatch, true);
  }

  public CMIUser getUser(
      String name,
      boolean checkDuplicates,
      @Deprecated boolean deprecatedFlag,
      boolean createIfMissing,
      boolean exactMatch,
      boolean checkNickNames) {
    if (name != null && !name.isEmpty()) {
      CMIUser cmiUser = null;
      if (name.length() == 36) {
        try {
          cmiUser = this.getUser(UUID.fromString(name));
          if (cmiUser != null) {
            return cmiUser;
          }
        } catch (Exception exception) {
        }

        return null;
      } else {
        Player player = CMIUser.getOnlinePlayer(name);
        if (player != null && player.getName().equalsIgnoreCase(name)) {
          cmiUser = this.getUser(player);
          if (cmiUser != null) {
            return cmiUser;
          }
        }

        if (!exactMatch && checkDuplicates && this.isDuplicatedUserName(name)) {
          cmiUser = this.getDuplicatedUser(name);
          if (cmiUser != null) {
            return cmiUser;
          }
        }

        if (Config.PrioritizeOnlinePlayers) {
          boolean exactSuffix = false;
          if (name.toLowerCase().endsWith("-exa-")) {
            exactSuffix = true;
            name = name.substring(0, name.length() - "-exa-".length());
          } else if (name.toLowerCase().endsWith("-exact-")) {
            exactSuffix = true;
            name = name.substring(0, name.length() - "-exact-".length());
          }

          if (exactSuffix) {
            cmiUser = this.getByName(name);
            if (cmiUser != null) {
              if (!cmiUser.isOnline()) {
                UUID nickUuid = this.plugin.getNickNameManager().whoUsesName(name);
                if (nickUuid != null) {
                  CMIUser nickOwner = this.getUser(nickUuid);
                  if (nickOwner != null && nickOwner.isOnline()) {
                    return nickOwner;
                  }
                }
              }

              return cmiUser;
            }
          } else if (!exactMatch) {
            player = CMIUser.getOnlinePlayer(name);
            if (player != null) {
              cmiUser = this.getUser(player);
              if (cmiUser != null) {
                return cmiUser;
              }
            }
          }
        }

        cmiUser = this.getByName(name);
        if (cmiUser != null) {
          if (!cmiUser.isOnline()) {
            UUID nickOwnerUuid = this.plugin.getNickNameManager().whoUsesName(name);
            if (nickOwnerUuid != null) {
              CMIUser nickOwnerUser = this.getUser(nickOwnerUuid);
              if (nickOwnerUser != null && nickOwnerUser.isOnline()) {
                return nickOwnerUser;
              }
            }
          }

          return cmiUser;
        } else {
          if (checkNickNames) {
            UUID nickFallbackUuid = this.plugin.getNickNameManager().whoUsesName(name);
            if (nickFallbackUuid != null) {
              cmiUser = this.getUser(nickFallbackUuid);
              if (cmiUser != null) {
                return cmiUser;
              }
            }
          }

          player = CMIUser.getOnlinePlayer(name);
          if (player != null
              && (!exactMatch || exactMatch && player.getName().equalsIgnoreCase(name))) {
            cmiUser = this.getUser(player);
            if (cmiUser != null) {
              return cmiUser;
            }
          }

          if (!exactMatch) {
            player = CMIUser.getOnlinePlayer(name, true);
            if (player != null) {
              cmiUser = this.getUser(player);
              if (cmiUser != null) {
                return cmiUser;
              }
            }
          }

          if (createIfMissing) {
            cmiUser =
                new CMIUser(
                    UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)));
            cmiUser.setName(name);
            cmiUser.setFakeAccount(true);
            cmiUser.addForDelayedSave();
            this.addUser(cmiUser);
          }

          return cmiUser;
        }
      }
    } else {
      return null;
    }
  }

  public boolean isUserExist(UUID uuid) {
    return this.users.containsKey(uuid);
  }

  @Nullable
  public CMIUser getUser(OfflinePlayer offlinePlayer) {
    return this.getUser(offlinePlayer, true);
  }

  @Nullable
  public CMIUser getUser(OfflinePlayer offlinePlayer, boolean createIfMissing) {
    if (offlinePlayer == null) {
      return null;
    }

    CMIUser cmiUser = this.users.get(offlinePlayer.getUniqueId());
    if (CMIHookType.Towny.isPresent()
        && cmiUser == null
        && createIfMissing
        && offlinePlayer.getName() != null
        && (offlinePlayer
                .getName()
                .startsWith(CMI.getInstance().getEconomyManager().getTownyNationPrefix())
            || offlinePlayer
                .getName()
                .startsWith(CMI.getInstance().getEconomyManager().getTownyDebtPrefix())
            || offlinePlayer
                .getName()
                .startsWith(CMI.getInstance().getEconomyManager().getTownyTownPrefix()))) {
      cmiUser = this.getUser(offlinePlayer.getName(), true, false, false, true, true);
    }

    if (cmiUser == null && createIfMissing) {
      cmiUser = new CMIUser(offlinePlayer);
      cmiUser.setFakeAccount(true);
      this.addUser(cmiUser);

      try {
        if (offlinePlayer.isOnline()) {
          cmiUser.updatePermissions();
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      cmiUser.addForDelayedSave();
      return cmiUser;
    } else {
      return cmiUser;
    }
  }

  @Nullable
  public CMIUser getUser(Player player) {
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = this.users.get(player.getUniqueId());
    if (cmiUser == null) {
      cmiUser = new CMIUser(player);
      if (!CMINPCChecker.isNPC(player)) {
        this.addUser(cmiUser);

        try {
          cmiUser.updatePermissions();
        } catch (Exception | Error error) {
          error.printStackTrace();
        }

        cmiUser.addForDelayedSave();
        return cmiUser;
      } else {
        return null;
      }
    } else {
      cmiUser.setFakeAccount(false);
      return cmiUser;
    }
  }

  @Nullable
  public CMIUser getUser(UUID uuid) {
    if (uuid == null) {
      return null;
    }

    CMIUser cmiUser = this.users.get(uuid);
    if (cmiUser == null) {
      Player player = CMIUser.getOnlinePlayer(uuid);
      if (player != null) {
        cmiUser = new CMIUser(player);
        if (!CMINPCChecker.isNPC(player)) {
          this.addUser(cmiUser);

          try {
            cmiUser.updatePermissions();
          } catch (Exception | Error error) {
            error.printStackTrace();
          }

          return cmiUser;
        }

        return null;
      }
    }

    return cmiUser;
  }

  public void loadData() {
    Long startMillis = System.currentTimeMillis();
    this.plugin.getDbManager().loadAllUsers();
    if (!this.duplicates.isEmpty()) {
      String message = "";
      int shownCount = 1;

      for (String duplicateEntry : this.duplicates) {
        message = message + " \n";
        message = message + duplicateEntry;
        if (++shownCount > 25) {
          message = message + " \n";
          message = message + "And " + (this.duplicates.size() - shownCount) + " more...";
          break;
        }
      }

      this.plugin.consoleMessage(
          "\n&cAttention! Found "
              + this.duplicates.size()
              + " duplicates in database. Same name, different UUID. This can cause some minor"
              + " issues. Please remove one of them from database with &7/cmi removeuser [uuid]"
              + " &cor &7/cmi removeuser duplicates"
              + message);
      this.duplicates.clear();
    }

    this.plugin.loadMessage(
        this.plugin.getPlayerManager().getTotalUserCount(),
        "player data",
        System.currentTimeMillis() - startMillis);
    this.plugin.getDbManager().loadPlayTimes();
    this.plugin.getDbManager().loadPlayerPlayTimeRewards();
  }

  public boolean checkForExistingSimilarUser(CMIUser cmiUser, boolean reportDuplicates) {
    CMIUser existingUser = this.getByName(cmiUser.getName());
    if (existingUser != null) {
      if (existingUser.getUniqueId().equals(cmiUser.getUniqueId())) {
        return true;
      }

      long existingLogoffAge = System.currentTimeMillis() - existingUser.getLastLogoffClean();
      long newLogoffAge = System.currentTimeMillis() - cmiUser.getLastLogoffClean();
      if (reportDuplicates) {
        this.duplicates.add(
            (this.duplicates.size() % 2 == 0 ? "&a" : "&2")
                + existingUser.getName()
                + " "
                + existingUser.getUniqueId()
                + " &clast seen: "
                + (existingUser.getLastLogoffClean() == 0L
                    ? "Unknown"
                    : CMITimeManager.to24hourShort(existingLogoffAge))
                + " \n"
                + (this.duplicates.size() % 2 == 0 ? "&a" : "&2")
                + cmiUser.getName()
                + " "
                + cmiUser.getUniqueId()
                + " &clast seen: "
                + (cmiUser.getLastLogoffClean() == 0L
                    ? "Unknown"
                    : CMITimeManager.to24hourShort(newLogoffAge)));
      }

      return false;
    } else {
      return true;
    }
  }

  public Map<String, Object> convertLoc(Location location) {
    HashMap values = new HashMap();
    if (location != null) {
      if (location.getWorld() != null) {
        values.put("World", location.getWorld().getName());
      }

      values.put("X", location.getX());
      values.put("Y", location.getY());
      values.put("Z", location.getZ());
      values.put("Yaw", location.getYaw());
      values.put("Pitch", location.getPitch());
    }

    return values;
  }

  public String convertLocToString(Location location) {
    String result = "";
    if (location != null && location.getWorld() != null) {
      result = result + location.getWorld().getName();
      result = result + ";" + fNumber(location.getX());
      result = result + ";" + (long) (location.getY() * 100.0) / 100.0;
      result = result + ";" + fNumber(location.getZ());
      result = result + ";" + (long) (location.getYaw() * 100.0F) / 100.0;
      result = result + ";" + (long) (location.getPitch() * 100.0F) / 100.0;
    }

    return result.replace(",", ".");
  }

  public String convertLocToString(CMILocation cmiLocation) {
    String result = "";
    if (cmiLocation != null) {
      result =
          result
              + (cmiLocation.getWorld() != null
                  ? cmiLocation.getWorld().getName()
                  : cmiLocation.getWorldName());
      result = result + ";" + fNumber(cmiLocation.getX());
      result = result + ";" + (long) (cmiLocation.getY() * 100.0) / 100.0;
      result = result + ";" + fNumber(cmiLocation.getZ());
      result = result + ";" + (long) (cmiLocation.getYaw() * 100.0F) / 100.0;
      result = result + ";" + (long) (cmiLocation.getPitch() * 100.0F) / 100.0;
    }

    return result.replace(",", ".");
  }

  public String convertBlockLocToString(Location location) {
    String result = "";
    if (location != null) {
      if (location instanceof CMILocation) {
        CMILocation cmiLocation = (CMILocation) location;
        if (cmiLocation.getWorldName() != null) {
          result = result + cmiLocation.getWorldName();
          result = result + ";" + location.getBlockX();
          result = result + ";" + location.getBlockY();
          result = result + ";" + location.getBlockZ();
        }
      } else if (location.getWorld() != null) {
        result = result + location.getWorld().getName();
        result = result + ";" + location.getBlockX();
        result = result + ";" + location.getBlockY();
        result = result + ";" + location.getBlockZ();
      }
    }

    return result.replace(",", ".");
  }

  public String convertLocToStringShort(CMILocation cmiLocation) {
    String result = "";
    if (cmiLocation != null) {
      CMILocation location = cmiLocation;
      if (location.getWorldName() != null) {
        result = result + location.getWorldName();
        result = result + ";" + (int) (location.getX() * 100.0) / 100.0;
        result = result + ";" + (int) (location.getY() * 100.0) / 100.0;
        result = result + ";" + (int) (location.getZ() * 100.0) / 100.0;
      }
    }

    return result.replace(",", ".");
  }

  public String convertLocToStringShortBlock(CMILocation cmiLocation) {
    String result = "";
    if (cmiLocation != null) {
      CMILocation location = cmiLocation;
      if (location.getWorldName() != null) {
        result = result + location.getWorldName();
        result = result + ";" + location.getBlockX();
        result = result + ";" + location.getBlockY();
        result = result + ";" + location.getBlockZ();
      }
    }

    return result.replace(",", ".");
  }

  public String convertLocToStringShort(Location location) {
    String result = "";
    if (location != null && location.getWorld() != null) {
      result = result + location.getWorld().getName();
      result = result + ";" + (int) (location.getX() * 100.0) / 100.0;
      result = result + ";" + (int) (location.getY() * 100.0) / 100.0;
      result = result + ";" + (int) (location.getZ() * 100.0) / 100.0;
    }

    return result.replace(",", ".");
  }

  public CMILocation convertStringToLocation(String text, boolean allowFallback) {
    CMILocation cmiLocation = this.convertStringToLocation(text);
    if (cmiLocation == null && allowFallback) {
      try {
        World world = this.plugin.getUtilManager().getWorld(text.split(";")[0]);
        if (world != null) {
          return new CMILocation(world.getSpawnLocation());
        }

        if (text.startsWith("warp:")) {
          CmiWarp cmiWarp = this.plugin.getWarpManager().getWarp(text.substring("warp:".length()));
          if (cmiWarp != null) {
            return cmiWarp.getLoc();
          }
        }
      } catch (Exception | Error ignored) {
      }
    }

    return cmiLocation;
  }

  public CMILocation convertStringToLocation(String text) {
    CMILocation cmiLocation = null;
    if (text != null && text.contains(";")) {
      String[] parts = text.replace(",", ".").split(";");
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      float yaw = 0.0F;
      float pitch = 0.0F;
      if (parts.length > 0) {
        try {
          x = Double.parseDouble(parts[1]);
        } catch (Exception exception) {
          return cmiLocation;
        }
      }

      if (parts.length > 1) {
        try {
          y = Double.parseDouble(parts[2]);
        } catch (Exception exception2) {
          return cmiLocation;
        }
      }

      if (parts.length > 2) {
        try {
          z = Double.parseDouble(parts[3]);
        } catch (Exception exception3) {
          return cmiLocation;
        }
      }

      if (parts.length > 3) {
        try {
          yaw = Float.parseFloat(parts[4]);
        } catch (Exception exception4) {
        }
      }

      if (parts.length > 4) {
        try {
          pitch = Float.parseFloat(parts[5]);
        } catch (Exception exception5) {
        }
      }

      World world = Bukkit.getWorld(parts[0]);
      String worldName = world == null ? parts[0] : world.getName();
      if (world != null) {
        cmiLocation = new CMILocation(world, x, y, z);
      } else {
        cmiLocation = new CMILocation(worldName, x, y, z);
      }

      cmiLocation.setYaw(yaw);
      cmiLocation.setPitch(pitch);
      return cmiLocation;
    } else {
      return null;
    }
  }

  public CMILocation getLoc(YamlConfiguration yamlConfiguration, String string) {
    try {
      if (!yamlConfiguration.isString(string + "World")) {
        return null;
      } else if ((yamlConfiguration.isDouble(string + "X") || yamlConfiguration.isInt(string + "X"))
          && (yamlConfiguration.isDouble(string + "Y") || yamlConfiguration.isInt(string + "Y"))
          && (yamlConfiguration.isDouble(string + "Z") || yamlConfiguration.isInt(string + "Z"))
          && (yamlConfiguration.isDouble(string + "Yaw") || yamlConfiguration.isInt(string + "Yaw"))
          && (yamlConfiguration.isDouble(string + "Pitch")
              || yamlConfiguration.isInt(string + "Pitch"))) {
        CMILocation cmiLocation =
            new CMILocation(
                yamlConfiguration.getString(string + "World"),
                yamlConfiguration.getDouble(string + "X"),
                yamlConfiguration.getDouble(string + "Y"),
                yamlConfiguration.getDouble(string + "Z"));
        cmiLocation.setYaw((float) yamlConfiguration.getDouble(string + "Yaw"));
        cmiLocation.setPitch((float) yamlConfiguration.getDouble(string + "Pitch"));
        return cmiLocation;
      } else {
        return null;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public void saveUser(CMIUser cmiUser) {
    this.save(cmiUser);
  }

  private void save(CMIUser cmiUser) {
    if (!cmiUser.isOnline()
        || cmiUser.getPlayer() == null
        || !CMINPCChecker.isNPC(cmiUser.getPlayer())) {
      this.plugin.getStatsManager().addUserData(cmiUser);
      this.plugin.getDbManager().addForSave(cmiUser);
    }
  }

  public PreparedStatement savePlayerToDB(
      CMIUser cmiUser, PreparedStatement preparedStatement, boolean updateExisting)
      throws SQLException {
    int parameterIndex = 1;
    DBDAO.UserTablesFields[] fields;
    int fieldCount = (fields = DBDAO.UserTablesFields.values()).length;

    for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
      DBDAO.UserTablesFields userTablesFields = fields[fieldIndex];
      Object fieldValue = null;

      try {
        switch (userTablesFields) {
          case player_uuid:
            if (updateExisting) {
              continue;
            }

            fieldValue = cmiUser.getUniqueId().toString();
            break;
          case username:
            fieldValue = cmiUser.getName(false);
            break;
          case nickname:
            fieldValue = cmiUser.getNickName();
            break;
          case LogOutLocation:
            fieldValue = cmiUser.getLogOutLocation();
            break;
          case DeathLocation:
            fieldValue = cmiUser.getDeathLoc();
            break;
          case TeleportLocation:
            fieldValue = cmiUser.getLastTeleportLocation();
            break;
          case Homes:
            if (!cmiUser.getHomes().isEmpty()) {
              LinkedHashMap<String, ArrayList> linkedHashMap = new LinkedHashMap();

              for (var entry : cmiUser.getHomes().entrySet()) {
                CmiHome cmiHome = (CmiHome) entry.getValue();
                ArrayList arrayList = new ArrayList();
                String homeName = (String) entry.getKey();
                arrayList.add(locationToString(cmiHome.getLoc()));
                if (cmiHome.isPrivate()) {
                  arrayList.add("p");
                }

                if (cmiHome.getSlot() != null && cmiHome.getSlot() > 0) {
                  arrayList.add(cmiHome.getSlot());
                }

                if (cmiHome.getMaterial() != null) {
                  arrayList.add(cmiHome.getMaterial().toString());
                }

                if (cmiHome.isFavorite()) {
                  arrayList.add("f");
                }

                if (cmiHome.isBed()) {
                  arrayList.add("b");
                }

                linkedHashMap.put(homeName, arrayList);
              }

              fieldValue = CMIJson.serialize(linkedHashMap);
              if (fieldValue == null) {
                var homesAsMap = cmiUser.getHomesAsMap();
                fieldValue = homesAsMap.isEmpty() ? null : homesAsMap;
              }
            }
            break;
          case LastLoginTime:
            fieldValue = cmiUser.getLastLogin();
            break;
          case LastLogoffTime:
            fieldValue = cmiUser.getLastLogoff();
            break;
          case TotalPlayTime:
            fieldValue = cmiUser.getTotalPlayTimeClean();
            break;
          case tFly:
            fieldValue = cmiUser.getTfly();
            break;
          case tGod:
            fieldValue = cmiUser.getTgod();
            break;
          case Glow:
            fieldValue = cmiUser.getGlow() == null ? null : cmiUser.getGlow().name();
            break;
          case Ips:
            fieldValue = cmiUser.getIps().isEmpty() ? null : cmiUser.getIps();
            break;
          case Cuffed:
            fieldValue = cmiUser.isCuffed();
            break;
          case AlertUntil:
            fieldValue = cmiUser.getAlertUntil();
            break;
          case AlertReason:
            fieldValue = cmiUser.getAlertReason();
            break;
          case JoinedCounter:
            fieldValue = cmiUser.isJoinedCounter();
            break;
          case LockedIps:
            fieldValue = cmiUser.getLockedIps().isEmpty() ? null : cmiUser.getLockedIps();
            break;
          case pTime:
            fieldValue = cmiUser.getPlayerTime();
            break;
          case Kits:
            if (!cmiUser.getKitsAsMap().isEmpty()) {
              fieldValue =
                  CMIJson.serialize(KitsManager.getPlayersKitsDataForSave(cmiUser.getUniqueId()));
            }
            break;
          case Charges:
            if (cmiUser.getPCharge().getChargesClean() != 0
                || cmiUser.getPCharge().getLastGive() != 0L) {
              fieldValue =
                  cmiUser.getPCharge().getChargesClean() + ":" + cmiUser.getPCharge().getLastGive();
            }
            break;
          case Cooldowns:
            CmdCooldown cmdCooldown = cmiUser.getCommandCooldowns();
            String cooldownsText = "";

            for (var cooldownEntry : cmdCooldown.getList().entrySet()) {
              if (((CmdCooldown.CMICooldown) cooldownEntry.getValue()).getEndTime()
                  >= System.currentTimeMillis()) {
                if (!cooldownsText.isEmpty()) {
                  cooldownsText = cooldownsText + ";;";
                }

                cooldownsText =
                    cooldownsText
                        + (String) cooldownEntry.getKey()
                        + "%%"
                        + ((CmdCooldown.CMICooldown) cooldownEntry.getValue()).getInitialized()
                        + ":"
                        + ":"
                        + ((CmdCooldown.CMICooldown) cooldownEntry.getValue()).getEndTime();
              }
            }

            fieldValue = cooldownsText.isEmpty() ? null : cooldownsText;
            break;
          case Balance:
            fieldValue = cmiUser.getBalance();
            break;
          case Notes:
            var notes = cmiUser.getNotes();
            ArrayList<String> noteList = new ArrayList();

            for (PlayerNote playerNote : notes) {
              noteList.add(
                  playerNote.getSender()
                      + ":"
                      + playerNote.getTime()
                      + ":"
                      + filterOutForSave(playerNote.getNote()));
            }

            if (!noteList.isEmpty()) {
              fieldValue = noteList;
            }
            break;
          case Rank:
            fieldValue =
                cmiUser.getNullRank() == null
                    ? null
                    : (cmiUser.getNullRank().isDefaultRank()
                        ? null
                        : cmiUser.getNullRank().getName());
            break;
          case BannedUntil:
            if (cmiUser.getBanEntryRaw() != null) {
              fieldValue =
                  cmiUser.getBanEntry(false).getExpiration() == 0L
                      ? null
                      : cmiUser.getBanEntry(false).getExpiration();
            }
            break;
          case BannedAt:
            if (cmiUser.getBanEntryRaw() != null) {
              fieldValue =
                  cmiUser.getBanEntry(false).getExpiration() == 0L
                      ? null
                      : cmiUser.getBanEntry().getCreated();
            }
            break;
          case BannedBy:
            if (cmiUser.getBanEntryRaw() != null) {
              fieldValue =
                  cmiUser.getBanEntry(false).getExpiration() == 0L
                      ? null
                      : cmiUser.getBanEntry().getSource();
            }
            break;
          case BanReason:
            if (cmiUser.getBanEntryRaw() != null) {
              fieldValue =
                  cmiUser.getBanEntry(false).getReason() == null
                      ? null
                      : cmiUser.getBanEntry().getReason().replace("§", "&");
            }
            break;
          case Ignores:
            fieldValue = cmiUser.getIgnoresString();
            break;
          case Vanish:
            String vanishText =
                cmiUser.getVanishOrNull() == null ? null : cmiUser.getVanishOrNull().toString();
            if (vanishText != null && !vanishText.equals(VanishManager.defaultString)) {
              fieldValue = vanishText;
            }
            break;
          case Economy:
            fieldValue = cmiUser.getEconomyAccount().getWorldGroupBalancesRounded();
            break;
          case Mail:
            var mails = cmiUser.getMails();
            ArrayList<ArrayList> mailList = new ArrayList();

            for (PlayerMail playerMail : mails) {
              ArrayList<String> mailParts = new ArrayList();
              mailParts.add(playerMail.getSender());
              mailParts.add(playerMail.getTime().toString());
              mailParts.add(playerMail.getMessage());
              if (playerMail.getKeepFor() > 0L) {
                mailParts.add(playerMail.getKeepFor().toString());
              }

              mailList.add(mailParts);
            }

            if (!mailList.isEmpty()) {
              fieldValue = CMIJson.serializeList(mailList);
            }
            break;
          case FlightCharge:
            fieldValue = cmiUser.getFlightCharge().getCharge();
            break;
          case UserMeta:
            fieldValue =
                !cmiUser.hasMeta() ? null : CMIJson.serialize(cmiUser.getMeta().getMapForSave());
            break;
          case Flying:
            fieldValue = cmiUser.isFlying();
            break;
          case Votifier:
            fieldValue = cmiUser.getVotifierVotes() == 0 ? null : cmiUser.getVotifierVotes();
            break;
          case Jail:
            if (cmiUser.isJailed()
                && cmiUser.getCell() != null
                && cmiUser.getCell().getJail() != null) {
              fieldValue =
                  cmiUser.getCell().getJail().getName()
                      + ":"
                      + cmiUser.getCell().getId()
                      + (cmiUser.getJailedBy() != null
                          ? ":" + cmiUser.getJailedBy().toString()
                          : "");
            }
            break;
          case JailedUntil:
            if (cmiUser.isJailed()) {
              fieldValue = cmiUser.getJailedForTime();
            }
            break;
          case FakeAccount:
            fieldValue = cmiUser.isFakeAccount();
            break;
          case PlaytimeOptimized:
            fieldValue = cmiUser.getPlayTimeOptimizedOn();
            break;
          case flightChargeEnabled:
            if (cmiUser.getFlightCharge().isEnabled()) {
              fieldValue = true;
            }
            break;
          case JailReason:
            if (cmiUser.isJailed()) {
              fieldValue = cmiUser.getJailedReason();
            }
            break;
          case Skin:
            fieldValue = cmiUser.getSkin();
            break;
          case Collision:
            fieldValue = cmiUser.isCollidable();
            break;
          case NamePrefix:
            fieldValue = cmiUser.getNamePlatePrefix();
            break;
          case NameSuffix:
            fieldValue = cmiUser.getNamePlateSuffix();
            break;
          case Warnings:
            fieldValue =
                cmiUser.getWarnings() != null && !cmiUser.getWarnings().isEmpty()
                    ? cmiUser.getWarningsSerialized()
                    : null;
            break;
          case NameColor:
            fieldValue =
                cmiUser.getNamePlateNameColor() == null
                    ? null
                    : CMIChatColor.deColorize(cmiUser.getNamePlateNameColor().toString());
            break;
          case Muted:
            if (cmiUser.isMuted()) {
              fieldValue =
                  cmiUser.getMutedUntil()
                      + ":"
                      + (cmiUser.getMutedReason() == null ? "" : cmiUser.getMutedReason());
            }
            break;
          case AFRecharge:
            if (cmiUser.getFlightCharge().isExpAutoRecharge()
                || cmiUser.getFlightCharge().isMoneyAutoRecharge()) {
              fieldValue =
                  (cmiUser.getFlightCharge().isExpAutoRecharge() ? "1" : "0")
                      + ":"
                      + (cmiUser.getFlightCharge().isMoneyAutoRecharge() ? "1" : "0");
            }
            break;
          case DisplayName:
            fieldValue = cmiUser.getDisplayNameClean(false);
            if (fieldValue != null && fieldValue.equals(cmiUser.getName(false))) {
              fieldValue = null;
            }
            break;
          case Options:
            String optionsText = PlayerOption.toString(cmiUser.getOptions());
            if (!optionsText.equals(PlayerOption.toDefaultValueString())) {
              fieldValue = optionsText;
            }
            break;
          case ChatColor:
            if (cmiUser.getChatMessageFormat() != null) {
              fieldValue = cmiUser.getChatMessageFormat().toString();
            }
        }
      } catch (Throwable throwable) {
        this.plugin.consoleMessage(
            " SEVERE SAVE ERROR WHILE SAVING "
                + cmiUser.getName()
                + " PLAYER DATA ("
                + userTablesFields
                + ")");
        throwable.printStackTrace();
      }

      proccessForSaveField(preparedStatement, parameterIndex, fieldValue, userTablesFields);
      parameterIndex++;
    }

    if (updateExisting) {
      try {
        preparedStatement.setInt(parameterIndex, cmiUser.getId());
        parameterIndex++;
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    } else if (!this.plugin.getDbManager().getDB().Format.isFallback()
        || CMI.getInstance().getDbManager().getDbType().equals(DBManager.DataBaseType.SqLite)) {
      try {
        preparedStatement.setString(parameterIndex, cmiUser.getUniqueId().toString());
        parameterIndex++;
      } catch (Throwable throwable3) {
      }
    }

    return preparedStatement;
  }

  private static String filterOutForLoad(String string) {
    return string.replace("T9C", "%%").replace("T8C", ":").replace("T7C", ";");
  }

  private static String filterOutForSave(String string) {
    return string.replace("%%", "T9C").replace(":", "T8C").replace(";", "T7C");
  }

  private static PreparedStatement proccessForSaveField(
      PreparedStatement preparedStatement,
      int parameterIndex,
      Object fieldValue,
      DBDAO.UserTablesFields userTablesFields)
      throws SQLException {
    try {
      switch (userTablesFields.getFieldType()) {
        case decimal:
          if (fieldValue == null) {
            preparedStatement.setNull(parameterIndex, 8);
          } else if (fieldValue instanceof Double) {
            Double doubleValue = (Double) fieldValue;
            if (doubleValue != null && !doubleValue.isNaN() && !doubleValue.isInfinite()) {
              preparedStatement.setDouble(parameterIndex, doubleValue);
            } else {
              preparedStatement.setNull(parameterIndex, 8);
            }
          } else {
            preparedStatement.setDouble(parameterIndex, 0.0);
          }
          break;
        case number:
          if (fieldValue instanceof Integer) {
            preparedStatement.setInt(parameterIndex, (Integer) fieldValue);
          } else {
            preparedStatement.setInt(parameterIndex, 0);
          }
        case longtext:
        default:
          break;
        case text:
          if (fieldValue instanceof String) {
            preparedStatement.setString(parameterIndex, (String) fieldValue);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case stringList:
          if (fieldValue instanceof ArrayList) {
            String listText = "";

            for (String listItem : (ArrayList<String>) fieldValue) {
              if (!listText.isEmpty()) {
                listText = listText + ";";
              }

              listText = listText + listItem;
            }

            preparedStatement.setString(parameterIndex, listText);
          } else if (fieldValue instanceof String) {
            preparedStatement.setString(parameterIndex, (String) fieldValue);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case stringLongMap:
          if (fieldValue instanceof HashMap) {
            String stringLongText = "";
            HashMap longMapValue = (HashMap) fieldValue;

            for (Entry longEntry : ((java.util.Map<?, ?>) longMapValue).entrySet()) {
              if (!stringLongText.isEmpty()) {
                stringLongText = stringLongText + ";";
              }

              stringLongText =
                  stringLongText + (String) longEntry.getKey() + "%%" + longEntry.getValue();
            }

            preparedStatement.setString(parameterIndex, stringLongText);
          } else if (fieldValue instanceof String) {
            preparedStatement.setString(parameterIndex, (String) fieldValue);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case stringIntMap:
          if (fieldValue instanceof HashMap) {
            String stringIntText = "";
            HashMap intMapValue = (HashMap) fieldValue;

            for (Entry intEntry : ((java.util.Map<?, ?>) intMapValue).entrySet()) {
              if (!stringIntText.isEmpty()) {
                stringIntText = stringIntText + ";";
              }

              stringIntText =
                  stringIntText + (String) intEntry.getKey() + "%%" + intEntry.getValue();
            }

            preparedStatement.setString(parameterIndex, stringIntText);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case stringDoubleMap:
          if (fieldValue instanceof HashMap) {
            String stringDoubleText = "";
            HashMap doubleMapValue = (HashMap) fieldValue;

            for (Entry doubleEntry : ((java.util.Map<?, ?>) doubleMapValue).entrySet()) {
              if (!stringDoubleText.isEmpty()) {
                stringDoubleText = stringDoubleText + ";";
              }

              stringDoubleText =
                  stringDoubleText + (String) doubleEntry.getKey() + "%%" + doubleEntry.getValue();
            }

            preparedStatement.setString(parameterIndex, stringDoubleText);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case stringStringMap:
          if (fieldValue instanceof HashMap) {
            String stringStringText = "";
            HashMap stringMapValue = (HashMap) fieldValue;

            for (Entry stringEntry : ((java.util.Map<?, ?>) stringMapValue).entrySet()) {
              if (!stringStringText.isEmpty()) {
                stringStringText = stringStringText + ";";
              }

              stringStringText =
                  stringStringText
                      + (String) stringEntry.getKey()
                      + "%%"
                      + (String) stringEntry.getValue();
            }

            preparedStatement.setString(parameterIndex, stringStringText);
          } else if (fieldValue instanceof String) {
            preparedStatement.setString(parameterIndex, (String) fieldValue);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case locationMap:
          if (fieldValue instanceof HashMap) {
            String locationMapText = "";
            HashMap locationMapValue = (HashMap) fieldValue;

            for (Entry locationEntry : ((java.util.Map<?, ?>) locationMapValue).entrySet()) {
              if (!locationMapText.isEmpty()) {
                locationMapText = locationMapText + ";";
              }

              locationMapText =
                  locationMapText
                      + (String) locationEntry.getKey()
                      + "%%"
                      + convertLocationToString((CMILocation) locationEntry.getValue());
            }

            preparedStatement.setString(parameterIndex, locationMapText);
          } else if (fieldValue instanceof LinkedHashMap) {
            String linkedLocationText = "";
            LinkedHashMap linkedLocationMap = (LinkedHashMap) fieldValue;

            for (Entry linkedLocationEntry : ((java.util.Map<?, ?>) linkedLocationMap).entrySet()) {
              if (!linkedLocationText.isEmpty()) {
                linkedLocationText = linkedLocationText + ";";
              }

              linkedLocationText =
                  linkedLocationText
                      + (String) linkedLocationEntry.getKey()
                      + "%%"
                      + convertLocationToString((CMILocation) linkedLocationEntry.getValue());
            }

            preparedStatement.setString(parameterIndex, linkedLocationText);
          } else if (fieldValue instanceof String) {
            preparedStatement.setString(parameterIndex, (String) fieldValue);
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case state:
          if (fieldValue == null) {
            fieldValue = false;
          }

          if (fieldValue instanceof Boolean) {
            preparedStatement.setBoolean(parameterIndex, (Boolean) fieldValue);
          } else {
            preparedStatement.setBoolean(parameterIndex, false);
          }
          break;
        case location:
          if (fieldValue instanceof CMILocation) {
            String locationText = convertLocationToString((CMILocation) fieldValue);
            if (locationText == null) {
              preparedStatement.setString(parameterIndex, null);
            } else {
              preparedStatement.setString(parameterIndex, locationText);
            }
          } else if (fieldValue instanceof Location) {
            String locationFallbackText = convertLocationToString((Location) fieldValue);
            if (locationFallbackText == null) {
              preparedStatement.setString(parameterIndex, null);
            } else {
              preparedStatement.setString(parameterIndex, locationFallbackText);
            }
          } else {
            preparedStatement.setNull(parameterIndex, 12);
          }
          break;
        case longNumber:
          if (fieldValue == null) {
            preparedStatement.setNull(parameterIndex, -5);
          } else if (fieldValue instanceof Long) {
            preparedStatement.setLong(parameterIndex, (Long) fieldValue);
          } else {
            preparedStatement.setLong(parameterIndex, 0L);
          }
      }

      return preparedStatement;
    } catch (Throwable throwable) {
      CMI.getInstance()
          .consoleMessage(
              "Failed save while saving "
                  + userTablesFields.name()
                  + " field ("
                  + fieldValue
                  + ")");
      throw throwable;
    }
  }

  public void loadUserFromDb(ResultSet resultSet) {
    try {
      CMIUser cmiUser = this.getUser(resultSet.getInt("id"));
      boolean flag = true;
      if (cmiUser == null) {
        cmiUser = new CMIUser(resultSet.getInt("id"));
        flag = false;
      }

      DBDAO.UserTablesFields[] fields;
      int fieldCount = (fields = DBDAO.UserTablesFields.values()).length;

      for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
        DBDAO.UserTablesFields userTablesFields = fields[fieldIndex];
        Object fieldValue =
            this.getValueFromDb(
                resultSet, userTablesFields.getFieldType(), userTablesFields.getColumn());
        if (fieldValue != null) {
          try {
            switch (userTablesFields) {
              case player_uuid:
                cmiUser.setUuidNoPermUpdate(
                    UUID.fromString(resultSet.getString(userTablesFields.getColumn())));
                break;
              case username:
                if (fieldValue instanceof String) {
                  cmiUser.setName((String) fieldValue);
                }
                break;
              case nickname:
                if (fieldValue instanceof String) {
                  cmiUser.setNickName((String) fieldValue, false);
                  this.plugin
                      .getNickNameManager()
                      .addNewNickName(cmiUser.getNickName(), cmiUser.getUniqueId());
                }
                break;
              case LogOutLocation:
                if (fieldValue instanceof CMILocation) {
                  cmiUser.setLogOutLocation(invertLoc(fieldValue));
                }
                break;
              case DeathLocation:
                if (fieldValue instanceof CMILocation) {
                  cmiUser.setDeathLoc(invertLoc(fieldValue));
                }
                break;
              case TeleportLocation:
                if (fieldValue instanceof CMILocation) {
                  cmiUser.setLastTeleportLocation(invertLoc(fieldValue), false);
                }
                break;
              case Homes:
                if (fieldValue instanceof LinkedHashMap) {
                  LinkedHashMap homesMap = (LinkedHashMap) fieldValue;

                  for (Entry homesMapEntry : ((java.util.Map<?, ?>) homesMap).entrySet()) {
                    if (((String) homesMapEntry.getKey()).contains("$-")) {
                      String[] homeParts = ((String) homesMapEntry.getKey()).split("\\$-");
                      CmiHome cmiHome =
                          new CmiHome(homeParts[0], invertLoc(homesMapEntry.getValue()));

                      for (int homeIndex = 1; homeIndex < homeParts.length; homeIndex++) {
                        String slotText = homeParts[homeIndex];
                        if (slotText.equalsIgnoreCase("p")) {
                          cmiHome.setPrivate(true);
                        } else if (slotText.equalsIgnoreCase("f")) {
                          cmiHome.setFavorite(true);
                        } else {
                          try {
                            cmiHome.setSlot(Integer.parseInt(slotText));
                          } catch (Exception exception) {
                            CMIMaterial cmiMaterial = CMIMaterial.get(slotText);
                            if (cmiMaterial != null && !cmiMaterial.isAir()) {
                              cmiHome.setMaterial(cmiMaterial);
                            }
                          }
                        }
                      }

                      cmiUser.addHome(cmiHome, false);
                    } else {
                      cmiUser.addHome(
                          new CmiHome(
                              (String) homesMapEntry.getKey(), invertLoc(homesMapEntry.getValue())),
                          false);
                    }
                  }
                } else if (fieldValue instanceof String) {
                  var homesJson = CMIJson.deserialize((String) fieldValue);

                  for (Entry homesJsonEntry : homesJson.entrySet()) {
                    String homeName = (String) homesJsonEntry.getKey();
                    List homeEntries =
                        CMIJson.getFromObject(homesJsonEntry.getValue(), new ArrayList());
                    CmiHome homeFromList = null;

                    for (Object homeEntry : homeEntries) {
                      if (homeEntry instanceof String) {
                        if (homeFromList == null) {
                          CMILocation cmiLocation = getLocationFromString((String) homeEntry);
                          if (cmiLocation != null) {
                            if (cmiLocation.getPitch() < -90.0F || cmiLocation.getPitch() > 90.0F) {
                              float floatValue = cmiLocation.getPitch();
                              cmiLocation.setPitch(cmiLocation.getYaw());
                              cmiLocation.setYaw(floatValue);
                            }

                            homeFromList = new CmiHome(homeName, cmiLocation);
                          }
                        } else {
                          String homeToken = String.valueOf(homeEntry);
                          String homeTokenValue = homeToken;
                          switch (homeToken.hashCode()) {
                            case 98:
                              if (homeTokenValue.equals("b")) {
                                homeFromList.setBed(true);
                                continue;
                              }
                              break;
                            case 102:
                              if (homeTokenValue.equals("f")) {
                                homeFromList.setFavorite(true);
                                continue;
                              }
                              break;
                            case 112:
                              if (homeTokenValue.equals("p")) {
                                homeFromList.setPrivate(true);
                                continue;
                              }
                          }

                          try {
                            homeFromList.setSlot(Integer.parseInt(homeToken));
                          } catch (Exception exception2) {
                            CMIMaterial slotMaterial = CMIMaterial.get(homeToken);
                            if (slotMaterial != null && !slotMaterial.isAir()) {
                              homeFromList.setMaterial(slotMaterial);
                            }
                          }
                        }
                      } else if (homeEntry instanceof Long && homeFromList != null) {
                        homeFromList.setSlot(((Long) homeEntry).intValue());
                      }
                    }

                    if (homeFromList != null && homeFromList.getLoc() != null) {
                      cmiUser.addHome(homeFromList, false);
                    }
                  }
                }
                break;
              case LastLoginTime:
                if (fieldValue instanceof Long && (Long) fieldValue > 0L) {
                  cmiUser.setLastLogin((Long) fieldValue);
                }
                break;
              case LastLogoffTime:
                if (fieldValue instanceof Long && (Long) fieldValue > 0L) {
                  cmiUser.setLastLogoff((Long) fieldValue);
                }
                break;
              case TotalPlayTime:
                if (fieldValue instanceof Long) {
                  cmiUser.setTotalPlayTime((Long) fieldValue);
                }
                break;
              case tFly:
                if (fieldValue instanceof Long) {
                  cmiUser.setTfly((Long) fieldValue);
                  this.plugin.getTimedCommandManager().addTimed(cmiUser);
                }
                break;
              case tGod:
                if (fieldValue instanceof Long) {
                  cmiUser.setTgod((Long) fieldValue);
                  this.plugin.getTimedCommandManager().addTimed(cmiUser);
                }
                break;
              case Glow:
                if (fieldValue instanceof String) {
                  CMIChatColor cmiChatColor = CMIChatColor.getColor((String) fieldValue);
                  if (cmiChatColor != null && cmiChatColor.getColor() != null) {
                    cmiUser.setGlow(cmiChatColor.getColor(), false);
                  }
                }
                break;
              case Ips:
                if (fieldValue instanceof HashMap) {
                  HashMap ipsMap = (HashMap) fieldValue;
                  if (!ipsMap.isEmpty()) {
                    cmiUser.setIps(new LinkedHashMap<>(ipsMap));
                    this.plugin.getIpManager().addIp(ipsMap, cmiUser);
                  }
                }
                break;
              case Cuffed:
                if (fieldValue instanceof Boolean && (Boolean) fieldValue) {
                  cmiUser.setCuffed((Boolean) fieldValue, false);
                }
                break;
              case AlertUntil:
                if (fieldValue instanceof Long) {
                  cmiUser.setAlertUntil((Long) fieldValue, false);
                }
                break;
              case AlertReason:
                if (fieldValue instanceof String) {
                  cmiUser.setAlertReason((String) fieldValue, false);
                }
                break;
              case JoinedCounter:
                if (fieldValue instanceof Boolean && (Boolean) fieldValue) {
                  cmiUser.setJoinedCounter((Boolean) fieldValue);
                }
                break;
              case LockedIps:
                if (fieldValue instanceof ArrayList && !((List) fieldValue).isEmpty()) {
                  cmiUser.setLockedIps((List<String>) fieldValue);
                }
                break;
              case pTime:
                if (fieldValue instanceof Long && (Long) fieldValue > -1L) {
                  cmiUser.setPlayerTime((Long) fieldValue);
                }
                break;
              case Kits:
                if (fieldValue instanceof HashMap) {
                  HashMap kitsMap = (HashMap) fieldValue;

                  for (Entry kitEntry : ((java.util.Map<?, ?>) kitsMap).entrySet()) {
                    String kitName = (String) kitEntry.getKey();
                    int kitAmount = 1;
                    if (kitName.contains("-X-")) {
                      Matcher matcher = kitPattern.matcher(kitName);
                      if (matcher.find()) {
                        kitName = matcher.group(1);

                        try {
                          kitAmount = Integer.parseInt(matcher.group(3));
                        } catch (Exception exception3) {
                          exception3.printStackTrace();
                        }
                      }
                    }

                    Kit kit = this.plugin.getKitsManager().getKit(kitName);
                    if (kit != null) {
                      cmiUser.addKit(kit, (Long) kitEntry.getValue(), kitAmount, false);
                    }
                  }
                } else {
                  if (!(fieldValue instanceof String)) {
                    continue;
                  }

                  try {
                    var kitsJson = CMIJson.deserialize((String) fieldValue);

                    for (Entry kitsEntry : kitsJson.entrySet()) {
                      String kitKey = (String) kitsEntry.getKey();
                      HashMap kitData = (HashMap) kitsEntry.getValue();
                      Long kitUseCount = CMIJson.getFromObject(kitData.get("u"), 1L);
                      Long kitTime = CMIJson.getFromObject(kitData.get("t"), 0L);
                      cmiUser.addKit(kitKey, kitTime, kitUseCount.intValue(), false);
                    }
                  } catch (Exception exception4) {
                    exception4.printStackTrace();
                  }
                }
                break;
              case Charges:
                if (fieldValue instanceof String) {
                  try {
                    cmiUser
                        .getPCharge(false)
                        .setCharges(Integer.parseInt(((String) fieldValue).split(":")[0]));
                    cmiUser
                        .getPCharge(false)
                        .setLastGive(Long.parseLong(((String) fieldValue).split(":")[1]));
                  } catch (Exception exception5) {
                  }
                }
                break;
              case Cooldowns:
                if (fieldValue instanceof String) {
                  String cooldownsRaw = (String) fieldValue;
                  ArrayList<String> cooldownEntries = new ArrayList();
                  if (cooldownsRaw.contains(";;")) {
                    cooldownEntries.addAll(Arrays.asList(cooldownsRaw.split(";;")));
                  } else {
                    cooldownEntries.add(cooldownsRaw);
                  }

                  for (String cooldownEntry : cooldownEntries) {
                    if (cooldownEntry.contains("%%")) {
                      Long cooldownInit = null;
                      Long cooldownEnd = null;
                      String cooldownName = null;

                      try {
                        cooldownName = cooldownEntry.split("%%")[0];
                        String cooldownValue = cooldownEntry.split("%%")[1];
                        cooldownInit = Long.parseLong(cooldownValue.split("::")[0]);
                        cooldownEnd = Long.parseLong(cooldownValue.split("::")[1]);
                      } catch (Exception exception6) {
                        continue;
                      }

                      if (cooldownName != null) {
                        cmiUser.addCommandCooldownByEndTime(
                            cooldownName, cooldownInit, cooldownEnd);
                      }
                    }
                  }
                }
              case Balance:
              case BannedUntil:
              case BannedAt:
              case BannedBy:
              case BanReason:
              default:
                break;
              case Notes:
                if (!(fieldValue instanceof ArrayList)) {
                  break;
                }

                List<String> notesList = (List<String>) fieldValue;
                cmiUser.getNotes().clear();

                for (String noteRaw : notesList) {
                  if (noteRaw.contains(":")) {
                    String[] noteParts = noteRaw.split(":");
                    String noteSender = noteParts[0];
                    long noteTime = Long.parseLong(noteParts[1]);
                    String noteText =
                        filterOutForLoad(
                            noteRaw.substring(noteSender.length() + 1 + noteParts[1].length() + 1));

                    try {
                      cmiUser.addNote(new PlayerNote(noteSender, noteTime, noteText), false);
                    } catch (Exception exception7) {
                    }
                  }
                }
                break;
              case Rank:
                if (fieldValue instanceof String) {
                  cmiUser.setRank(this.plugin.getRankManager().getRank((String) fieldValue));
                }
                break;
              case Ignores:
                if (!(fieldValue instanceof String)) {
                  break;
                }

                ArrayList<String> ignoreList = new ArrayList();
                String ignoresText = (String) fieldValue;
                if (ignoresText.contains(";")) {
                  ignoreList.addAll(Arrays.asList(ignoresText.split(";")));
                } else {
                  ignoreList.add(ignoresText);
                }

                for (String ignoreUuidText : ignoreList) {
                  try {
                    cmiUser.addIgnore(UUID.fromString(ignoreUuidText), false);
                  } catch (Exception exception8) {
                  }
                }
                break;
              case Vanish:
                if (fieldValue instanceof String) {
                  String vanishSaved = (String) fieldValue;
                  if (vanishSaved.contains(":")
                      && !VanishAction.getDefaultConfigString().equals(vanishSaved)) {
                    cmiUser.getVanish().fromString((String) fieldValue);
                  }
                }
                break;
              case Economy:
                if (!(fieldValue instanceof HashMap)) {
                  break;
                }

                HashMap economyMap = (HashMap) fieldValue;

                for (Entry worldBalance : ((java.util.Map<?, ?>) economyMap).entrySet()) {
                  EconomyManager.WorldGroup worldGroup =
                      this.plugin.getEconomyManager().getWorldGroup((String) worldBalance.getKey());
                  if (!this.plugin
                      .getEconomyManager()
                      .getDefaultGroup()
                      .getStartingAmount()
                      .equals(worldBalance.getValue())) {
                    CMIEconomyAcount cmiEconomyAcount = cmiUser.getEconomyAccount();
                    cmiEconomyAcount.setBalance(
                        worldGroup,
                        (Double) worldBalance.getValue(),
                        false,
                        this.plugin.isFullyLoaded());
                  }
                }
                break;
              case Mail:
                if (fieldValue instanceof ArrayList) {
                  List<String> mailList = (List<String>) fieldValue;
                  cmiUser.clearMails();

                  for (String mailRaw : mailList) {
                    if (mailRaw.contains(":")) {
                      String[] mailParts = mailRaw.split(":");

                      try {
                        String mailSender = mailParts[0];
                        long mailTime = Long.parseLong(mailParts[1]);
                        String mailText =
                            filterOutForLoad(
                                mailRaw.substring(
                                    mailSender.length() + 1 + mailParts[1].length() + 1));
                        Long keepFor = null;
                        if (mailParts.length > 3) {
                          keepFor = Long.parseLong(mailParts[3]);
                          if (keepFor > 0L
                              && keepFor * 1000L + mailTime < System.currentTimeMillis()) {
                            continue;
                          }
                        }

                        cmiUser.addMail(
                            new PlayerMail(mailSender, Long.valueOf(mailTime), mailText, keepFor),
                            false);
                      } catch (Exception exception9) {
                      }
                    }
                  }
                } else {
                  if (!(fieldValue instanceof String)) {
                    continue;
                  }

                  var mailEntries = CMIJson.deserializeList((String) fieldValue);
                  cmiUser.clearMails();

                  for (Object mailEntry : mailEntries) {
                    if (mailEntry instanceof ArrayList) {
                      ArrayList mailArray = (ArrayList) mailEntry;

                      try {
                        String mailSenderJson = mailArray.get(0).toString();
                        long mailTimeJson =
                            CMIJson.getFromObject(mailArray.get(1), System.currentTimeMillis());
                        String mailTextJson = mailArray.get(2).toString();
                        Long keepForJson = null;
                        if (mailArray.size() > 3) {
                          keepForJson = CMIJson.getFromObject(mailArray.get(3), 0L);
                          if (keepForJson > 0L
                              && keepForJson * 1000L + mailTimeJson < System.currentTimeMillis()) {
                            continue;
                          }
                        }

                        cmiUser.addMail(
                            new PlayerMail(
                                mailSenderJson,
                                Long.valueOf(mailTimeJson),
                                mailTextJson,
                                keepForJson),
                            false);
                      } catch (Exception exception10) {
                      }
                    }
                  }
                }
                break;
              case FlightCharge:
                if (fieldValue instanceof Double && (Double) fieldValue > 0.0) {
                  cmiUser.getFlightCharge().setCharge((Double) fieldValue);
                }
                break;
              case UserMeta:
                if (fieldValue instanceof HashMap) {
                  for (Entry metaEntry : ((java.util.Map<?, ?>) fieldValue).entrySet()) {
                    cmiUser
                        .getMeta()
                        .add((String) metaEntry.getKey(), (String) metaEntry.getValue());
                  }
                } else {
                  if (!(fieldValue instanceof String)) {
                    continue;
                  }

                  var metaJson = CMIJson.deserialize((String) fieldValue);

                  for (Entry metaJsonEntry : metaJson.entrySet()) {
                    cmiUser
                        .getMeta()
                        .add(
                            (String) metaJsonEntry.getKey(),
                            String.valueOf(metaJsonEntry.getValue()));
                  }
                }
                break;
              case Flying:
                if (fieldValue instanceof Boolean && (Boolean) fieldValue) {
                  cmiUser.setFlying((Boolean) fieldValue);
                }
                break;
              case Votifier:
                if (fieldValue instanceof Integer && (Integer) fieldValue > 0) {
                  cmiUser.setVotifierVotes((Integer) fieldValue);
                  this.plugin.getVotifierManager().updateVoteCountList(cmiUser);
                }
                break;
              case Jail:
                if (fieldValue instanceof String) {
                  String jailText = (String) fieldValue;
                  Integer jailCellId = null;
                  UUID jailerUuid = null;
                  if (jailText.contains(":")) {
                    String[] jailParts = jailText.split(":");

                    try {
                      jailCellId = Integer.parseInt(jailParts[1]);
                    } catch (Throwable throwable) {
                    }

                    jailText = jailParts[0];
                    if (jailParts.length > 2) {
                      try {
                        jailerUuid = UUID.fromString(jailParts[2]);
                      } catch (Throwable throwable2) {
                      }
                    }
                  }

                  CMIJail cmiJail = this.plugin.getJailManager().getByName(jailText);
                  CMIJailCell cmiJailCell =
                      this.plugin.getJailManager().getValidCell(cmiUser, cmiJail, jailCellId);
                  cmiUser.setCell(cmiJailCell);
                  cmiUser.setJailedBy(jailerUuid);
                }
                break;
              case JailedUntil:
                if (fieldValue instanceof Long && (Long) fieldValue > 0L) {
                  cmiUser.setJailedForTime((Long) fieldValue, false);
                }
                break;
              case FakeAccount:
                if (fieldValue instanceof Boolean && (Boolean) fieldValue) {
                  cmiUser.setFakeAccount((Boolean) fieldValue);
                }
                break;
              case PlaytimeOptimized:
                if (fieldValue instanceof Long && (Long) fieldValue > 0L) {
                  cmiUser.setPlayTimeOptimizedOn((Long) fieldValue);
                }
                break;
              case flightChargeEnabled:
                if (fieldValue instanceof Boolean && (Boolean) fieldValue) {
                  cmiUser.getFlightCharge().setEnabled((Boolean) fieldValue);
                }
                break;
              case JailReason:
                if (fieldValue instanceof String) {
                  cmiUser.setJailedReason((String) fieldValue);
                }
                break;
              case Skin:
                if (fieldValue instanceof String) {
                  cmiUser.setSkin((String) fieldValue);
                }
                break;
              case Collision:
                if (fieldValue instanceof Boolean && !(Boolean) fieldValue) {
                  cmiUser.setCollision(!(Boolean) fieldValue);
                }
                break;
              case NamePrefix:
                if (fieldValue instanceof String) {
                  cmiUser.setNamePlatePrefix((String) fieldValue);
                }
                break;
              case NameSuffix:
                if (fieldValue instanceof String) {
                  cmiUser.setNamePlateSuffix((String) fieldValue);
                }
                break;
              case Warnings:
                if (!(fieldValue instanceof HashMap)) {
                  if (fieldValue instanceof String) {
                    for (CMIPlayerWarning cmiPlayerWarning :
                        this.plugin.getWarningManager().deserializeWarnings((String) fieldValue)) {
                      cmiUser.addWarning(cmiPlayerWarning);
                    }
                  }
                } else {
                  HashMap warningsMap = (HashMap) fieldValue;
                  if (cmiUser.getWarnings() != null) {
                    cmiUser.getWarnings().clear();
                  }

                  for (Entry warningEntry : ((java.util.Map<?, ?>) warningsMap).entrySet()) {
                    String warningGiver = (String) warningEntry.getKey();
                    String warningReason = null;
                    String warningCategoryName = null;
                    if (warningGiver.contains("-X-")) {
                      warningGiver = warningGiver.split("-X-")[0];

                      try {
                        warningReason =
                            ((String) warningEntry.getKey())
                                .substring(warningGiver.length() + "-X-".length());
                      } catch (Exception exception11) {
                        exception11.printStackTrace();
                      }
                    }

                    if (warningReason != null && warningReason.contains("-X-")) {
                      try {
                        warningCategoryName = warningReason.split("-X-")[1];
                      } catch (Exception exception12) {
                        exception12.printStackTrace();
                      }

                      warningReason = warningReason.split("-X-")[0];
                    }

                    CMIPlayerWarning warning = new CMIPlayerWarning();
                    warning.setGivenAt((Long) warningEntry.getValue());
                    warning.setGivenBy(warningGiver);
                    warning.setReason(
                        warningReason != null && !warningReason.isEmpty() ? warningReason : null);
                    CMIWarningCategory cmiWarningCategory =
                        this.plugin.getWarningManager().getCategory(warningCategoryName);
                    warning.setCategory(
                        cmiWarningCategory == null
                            ? this.plugin.getWarningManager().getDefaultCategory()
                            : cmiWarningCategory);
                    cmiUser.addWarning(warning);
                  }
                }
                break;
              case NameColor:
                if (fieldValue instanceof String) {
                  CMIChatColor nameColor = CMIChatColor.getColor((String) fieldValue);
                  cmiUser.setNamePlateNameColor(nameColor == null ? null : nameColor.getColor());
                }
                break;
              case Muted:
                if (fieldValue instanceof String) {
                  try {
                    cmiUser.setMutedUntil(Long.parseLong(((String) fieldValue).split(":")[0]));
                    String mutedReasonText = ((String) fieldValue).split(":")[1];
                    if (mutedReasonText != null && !mutedReasonText.isEmpty()) {
                      cmiUser.setMutedReason(mutedReasonText);
                    }
                  } catch (Exception exception13) {
                  }
                }
                break;
              case AFRecharge:
                if (!(fieldValue instanceof String)) {
                  break;
                }

                String afRechargeText = (String) fieldValue;
                if (afRechargeText.equals("1:0") || afRechargeText.equals("1:1")) {
                  cmiUser.getFlightCharge().setAutoExpRecharge();
                }

                if (afRechargeText.equals("0:1") || afRechargeText.equals("1:1")) {
                  cmiUser.getFlightCharge().setAutoMoneyRecharge();
                }
                break;
              case DisplayName:
                if (fieldValue instanceof String) {
                  cmiUser.setDisplayName((String) fieldValue);
                }
                break;
              case Options:
                if (fieldValue instanceof String) {
                  cmiUser.setOptions(PlayerOption.fromString((String) fieldValue));
                }
                break;
              case ChatColor:
                if (fieldValue instanceof String) {
                  cmiUser.setChatMessageFormat(
                      new CMIPlayerMessageColor(
                          CMIChatColor.getColor((String) fieldValue),
                          CMIChatColor.getFormats((String) fieldValue)));
                }
            }
          } catch (Throwable throwable3) {
            throwable3.printStackTrace();
          }
        }
      }

      if (!flag) {
        this.addUser(cmiUser);
        this.checkForExistingSimilarUser(cmiUser, true);
      }
    } catch (Exception exception14) {
      exception14.printStackTrace();
    }
  }

  private static CMILocation invertLoc(Object object) {
    CMILocation cmiLocation = (CMILocation) object;
    CMILocation inverted =
        new CMILocation(
            cmiLocation.getWorldName(), cmiLocation.getX(), cmiLocation.getY(), cmiLocation.getZ());
    inverted.setPitch(cmiLocation.getYaw());
    inverted.setYaw(cmiLocation.getPitch());
    return inverted;
  }

  @Deprecated
  public Object getValueFromDb(
      ResultSet resultSet, DBDAO.TablesFieldsType tablesFieldsType, String column) {
    Object value = null;

    try {
      switch (tablesFieldsType) {
        case decimal:
          return resultSet.getDouble(column);
        case number:
          return resultSet.getInt(column);
        case longtext:
        default:
          break;
        case text:
          return resultSet.getString(column);
        case stringList:
          String listText = resultSet.getString(column);
          if (listText != null && listText.startsWith("[")) {
            return listText;
          }

          return getStringListFromString(resultSet.getString(column));
        case stringLongMap:
          String longMapText = resultSet.getString(column);
          if (longMapText != null && longMapText.startsWith("{\"")) {
            return longMapText;
          }

          return getStringLongMapFromString(longMapText);
        case stringIntMap:
          return getStringIntMapFromString(resultSet.getString(column));
        case stringDoubleMap:
          return getStringDoubleMapFromString(resultSet.getString(column));
        case stringStringMap:
          String stringMapText = resultSet.getString(column);
          if (stringMapText != null && stringMapText.startsWith("{\"")) {
            return stringMapText;
          }

          return getStringStringMapFromString(stringMapText);
        case locationMap:
          String locationMapText = resultSet.getString(column);
          if (locationMapText != null && locationMapText.startsWith("{\"")) {
            return locationMapText;
          }

          return getLocationMapFromString(locationMapText);
        case state:
          return resultSet.getBoolean(column);
        case location:
          return getLocationFromString(resultSet.getString(column));
        case longNumber:
          return resultSet.getLong(column);
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }

    return value;
  }

  public Object getValueFromDb(
      ResultSet resultSet, CMITablesFieldsType cmiTablesFieldsType, String column) {
    Object value = null;

    try {
      switch (cmiTablesFieldsType) {
        case decimal:
          return resultSet.getDouble(column);
        case number:
          return resultSet.getInt(column);
        case longtext:
        default:
          break;
        case text:
          return resultSet.getString(column);
        case stringList:
          String listText = resultSet.getString(column);
          if (listText != null && listText.startsWith("[")) {
            return listText;
          }

          return getStringListFromString(resultSet.getString(column));
        case stringLongMap:
          String longMapText = resultSet.getString(column);
          if (longMapText != null && longMapText.startsWith("{\"")) {
            return longMapText;
          }

          return getStringLongMapFromString(longMapText);
        case stringIntMap:
          return getStringIntMapFromString(resultSet.getString(column));
        case stringDoubleMap:
          return getStringDoubleMapFromString(resultSet.getString(column));
        case stringStringMap:
          String stringMapText = resultSet.getString(column);
          if (stringMapText != null && stringMapText.startsWith("{\"")) {
            return stringMapText;
          }

          return getStringStringMapFromString(stringMapText);
        case locationMap:
          String locationMapText = resultSet.getString(column);
          if (locationMapText != null && locationMapText.startsWith("{\"")) {
            return locationMapText;
          }

          return getLocationMapFromString(locationMapText);
        case state:
          return resultSet.getBoolean(column);
        case location:
          return getLocationFromString(resultSet.getString(column));
        case longNumber:
          return resultSet.getLong(column);
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }

    return value;
  }

  private static List<String> getStringListFromString(String text) {
    ArrayList<String> items = new ArrayList();
    if (text != null && !text.isEmpty()) {
      if (text.contains(";")) {
        items.addAll(Arrays.asList(text.split(";")));
      } else {
        items.add(text);
      }

      return items;
    } else {
      return items;
    }
  }

  private static HashMap<String, Long> getStringLongMapFromString(String text) {
    HashMap<String, Long> result = new HashMap();

    for (String entryText : getStringListFromString(text)) {
      if (entryText.contains("%%")) {
        try {
          result.put(entryText.split("%%")[0], Long.parseLong(entryText.split("%%")[1]));
        } catch (Exception exception) {
        }
      }
    }

    return result;
  }

  private static HashMap<String, Double> getStringDoubleMapFromString(String text) {
    HashMap<String, Double> result = new HashMap();

    for (String entryText : getStringListFromString(text)) {
      if (entryText.contains("%%")) {
        try {
          result.put(entryText.split("%%")[0], Double.parseDouble(entryText.split("%%")[1]));
        } catch (Exception exception) {
        }
      }
    }

    return result;
  }

  private static HashMap<String, String> getStringStringMapFromString(String text) {
    HashMap<String, String> result = new HashMap();

    for (String entryText : getStringListFromString(text)) {
      if (entryText.contains("%%")) {
        try {
          result.put(entryText.split("%%")[0], String.valueOf(entryText.split("%%")[1]));
        } catch (Exception exception) {
        }
      }
    }

    return result;
  }

  private static HashMap<String, Integer> getStringIntMapFromString(String text) {
    HashMap<String, Integer> result = new HashMap();

    for (String entryText : getStringListFromString(text)) {
      if (entryText.contains("%%")) {
        try {
          result.put(entryText.split("%%")[0], Integer.parseInt(entryText.split("%%")[1]));
        } catch (Exception exception) {
        }
      }
    }

    return result;
  }

  private static String convertLocationToString(CMILocation cmiLocation) {
    return (cmiLocation.getWorld() == null
            ? cmiLocation.getWorldName()
            : cmiLocation.getWorld().getName())
        + ":"
        + fNumber(cmiLocation.getX())
        + ":"
        + fNumber(cmiLocation.getY())
        + ":"
        + fNumber(cmiLocation.getZ())
        + ":"
        + fNumber(cmiLocation.getPitch())
        + ":"
        + fNumber(cmiLocation.getYaw());
  }

  private static String locationToString(CMILocation cmiLocation) {
    return (cmiLocation.getWorld() == null
            ? cmiLocation.getWorldName()
            : cmiLocation.getWorld().getName())
        + ":"
        + fNumber(cmiLocation.getX())
        + ":"
        + fNumber(cmiLocation.getY())
        + ":"
        + fNumber(cmiLocation.getZ())
        + ":"
        + fNumber(cmiLocation.getYaw())
        + ":"
        + fNumber(cmiLocation.getPitch());
  }

  private static String convertLocationToString(Location location) {
    try {
      return location != null && location.getWorld() != null
          ? location.getWorld().getName()
              + ":"
              + fNumber(location.getX())
              + ":"
              + fNumber(location.getY())
              + ":"
              + fNumber(location.getZ())
              + ":"
              + fNumber(location.getPitch())
              + ":"
              + fNumber(location.getYaw())
          : null;
    } catch (Throwable throwable) {
      return null;
    }
  }

  private static Double fNumber(float floatValue) {
    return (long) (floatValue * 100.0F) / 100.0;
  }

  private static Double fNumber(Double value) {
    return (long) (value * 100.0) / 100.0;
  }

  private static LinkedHashMap<String, CMILocation> getLocationMapFromString(String text) {
    LinkedHashMap<String, CMILocation> locations = new LinkedHashMap();
    if (text == null) {
      return locations;
    }

    for (String entryText : getStringListFromString(text)) {
      if (entryText.contains("%%")) {
        CMILocation location = getLocationFromString(entryText.split("%%")[1]);
        if (location != null) {
          locations.put(entryText.split("%%")[0], location);
        }
      }
    }

    return locations;
  }

  private static CMILocation getLocationFromString(String text) {
    if (text != null && text.contains(":")) {
      try {
        String[] parts = text.split(":");
        return new CMILocation(
            parts[0],
            Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]),
            Double.parseDouble(parts[3]),
            Float.parseFloat(parts[4]),
            Float.parseFloat(parts[5]));
      } catch (Exception exception) {
        try {
          String[] fallbackParts = text.replace(",", ".").split(":");
          return new CMILocation(
              fallbackParts[0],
              Double.parseDouble(fallbackParts[1]),
              Double.parseDouble(fallbackParts[2]),
              Double.parseDouble(fallbackParts[3]),
              Float.parseFloat(fallbackParts[4]),
              Float.parseFloat(fallbackParts[5]));
        } catch (Exception exception2) {
          return null;
        }
      }
    } else {
      return null;
    }
  }

  public void addCuffed(UUID uuid) {
    if (uuid != null) {
      this.cuffed.add(uuid);
    }
  }

  public void removeCuffed(Player player) {
    this.removeCuffed(player.getUniqueId());
  }

  public void removeCuffed(UUID uuid) {
    if (uuid != null) {
      this.cuffed.remove(uuid);
    }
  }

  public boolean isCuffed(Player player) {
    return this.isCuffed(player.getUniqueId());
  }

  public boolean isCuffed(UUID uuid) {
    return uuid == null ? false : this.cuffed.contains(uuid);
  }

  public void removeSocialSpyDelayed(UUID uuid) {
    CMITask cmiTask = this.delaySSTrigger.remove(uuid);
    if (cmiTask != null) {
      cmiTask.cancel();
    }
  }

  public void addSocialSpyDelayed(UUID uuid) {
    this.delaySSTrigger.put(
        uuid,
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.delaySSTrigger.containsKey(uuid)) {
                this.delaySSTrigger.remove(uuid);
                this.socialSpy.add(uuid);
              }
            },
            this.plugin.getConfigManager().getSpyDelayForTrigger() * 20L));
  }

  public void addSocialSpy(UUID uuid) {
    this.socialSpy.add(uuid);
  }

  public void removeSocialSpy(UUID uuid) {
    this.socialSpy.remove(uuid);
  }

  public boolean isSocialSpy(UUID uuid) {
    return this.socialSpy.contains(uuid);
  }

  public void sendMessageToSpies(
      CommandSender commandSender, String senderName, Player player, String message) {
    if (!this.socialSpy.isEmpty()) {
      if (senderName == null && commandSender != null) {
        senderName = commandSender.getName();
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (senderName != null) {
        snd.setSenderName(senderName);
      }

      RawMessage rawMessage = new RawMessage();
      rawMessage.setDontBreakLine(true);
      String hoverText = CMILC.Chat_privateHover.getLocale();
      hoverText =
          this.plugin
              .getPlaceholderAPIManager()
              .updatePlaceHolders((Player) commandSender, hoverText);
      if (this.plugin.getChatFormatManager().containsUrl(message)) {
        rawMessage
            .addText(
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(player, CMILC.Spy_Chat_msg.getLocale(snd, "[message]", "")))
            .addHover(hoverText);
        this.plugin
            .getChatFormatManager()
            .convertHoverOver(rawMessage, message, (Player) commandSender);
      } else {
        rawMessage
            .addText(
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(
                        player, CMILC.Spy_Chat_msg.getLocale(snd, "[message]", message)))
            .addHover(hoverText);
      }

      var spyUuids = new HashSet<>(this.socialSpy);
      HashSet<Player> spies = new HashSet();

      for (UUID uuid : spyUuids) {
        Player spyPlayer = CMIUser.getOnlinePlayer(uuid);
        if (spyPlayer == null) {
          this.socialSpy.remove(uuid);
        } else if ((player == null
                || !spyPlayer.getUniqueId().equals(player.getUniqueId())
                    && (commandSender == null || !commandSender.getName().equals(player.getName())))
            && (commandSender == null || !spyPlayer.getName().equals(commandSender.getName()))) {
          spies.add(spyPlayer);
        }
      }

      rawMessage.show(spies);
    }
  }

  public void sendChatRoomMessageToSpies(
      CommandSender commandSender, String roomName, String message) {
    if (!this.socialSpy.isEmpty()) {
      message = CMIChatColor.translate(message);
      Snd snd = new Snd().setSender(commandSender);
      message = CMILC.Spy_Chat_custom.getLocale(snd, "[place]", roomName, "[message]", message);
      message = message.replace("＆", "&");

      for (UUID uuid : new HashSet<>(this.socialSpy)) {
        Player player = CMIUser.getOnlinePlayer(uuid);
        if (player == null) {
          this.socialSpy.remove(uuid);
        } else if (commandSender == null || !player.getName().equals(commandSender.getName())) {
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser.getChatRoom() == null
              || !cmiUser.getChatRoom().getChatName().equalsIgnoreCase(roomName)) {
            player.sendMessage(message);
          }
        }
      }
    }
  }

  public void removeCommandSpyDelayed(UUID uuid) {
    CMITask cmiTask = this.delayCSTrigger.remove(uuid);
    if (cmiTask != null) {
      cmiTask.cancel();
    }
  }

  public void addCommandSpyDelayed(UUID uuid) {
    this.delayCSTrigger.put(
        uuid,
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.delayCSTrigger.containsKey(uuid)) {
                this.delayCSTrigger.remove(uuid);
                this.commandSpy.add(uuid);
              }
            },
            300L));
  }

  public void addCommandSpy(UUID uuid) {
    this.commandSpy.add(uuid);
  }

  public void removeCommandSpy(UUID uuid) {
    this.commandSpy.remove(uuid);
  }

  public boolean isCommandSpy(UUID uuid) {
    return this.commandSpy.contains(uuid);
  }

  public void sendMessageToCommandSpies(Player player, String commandText) {
    if (!this.commandSpy.isEmpty()
        && !PermissionsManager.CMIPerm.command_commandspy_hide.hasPermission(player)) {
      String lowerCommand = commandText.toLowerCase();

      for (String blacklistedName : hardBlackList) {
        if (lowerCommand.equals(blacklistedName)
            || lowerCommand.startsWith(blacklistedName + " ")) {
          return;
        }
      }

      boolean blocked = false;

      for (String blacklistedRaw : this.plugin.getConfigManager().getCommandSpyBlackListed()) {
        String blacklistedCommand = blacklistedRaw.toLowerCase();
        if (!blacklistedCommand.startsWith("/")) {
          blacklistedCommand = "/" + blacklistedCommand;
        }

        if (lowerCommand.equals(blacklistedCommand)
            || lowerCommand.startsWith(blacklistedCommand + " ")) {
          blocked = true;
          break;
        }
      }

      commandText = commandText.replace("/n", "%%n");
      commandText = CMIChatColor.translate(commandText);
      Snd snd = new Snd().setSender(player).setTarget(player);
      commandText = CMILC.Spy_Command_msg.getLocale(snd, "[command]", commandText);
      commandText = commandText.replace("%%n", "/n");
      boolean whitelisted = false;
      if (!this.plugin.getConfigManager().getCommandSpyCommandList().isEmpty()) {
        for (String allowedRaw : this.plugin.getConfigManager().getCommandSpyCommandList()) {
          String allowedCommand = allowedRaw.toLowerCase();
          if (!allowedCommand.startsWith("/")) {
            allowedCommand = "/" + allowedCommand;
          }

          if (lowerCommand.equals(allowedCommand)
              || lowerCommand.startsWith(allowedCommand + " ")) {
            whitelisted = true;
            break;
          }
        }
      }

      for (UUID uuid : new HashSet<>(this.commandSpy)) {
        Player commandSpyPlayer = CMIUser.getOnlinePlayer(uuid);
        if (commandSpyPlayer == null) {
          this.commandSpy.remove(uuid);
        } else if (!commandSpyPlayer.getUniqueId().equals(player.getUniqueId())
            && (!blocked
                || PermissionsManager.CMIPerm.command_commandspy_bypass.hasPermission(
                    commandSpyPlayer))
            && (this.plugin.getConfigManager().getCommandSpyCommandList().isEmpty()
                || whitelisted
                || PermissionsManager.CMIPerm.security_admin.hasPermission(commandSpyPlayer))) {
          commandSpyPlayer.sendMessage(commandText);
        }
      }
    }
  }

  public void addSignSpyDelayed(UUID uuid) {
    this.delaySignSTrigger.put(
        uuid,
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (this.delaySignSTrigger.containsKey(uuid)) {
                this.delaySignSTrigger.remove(uuid);
                this.signSpy.add(uuid);
              }
            },
            300L));
  }

  public void addSignSpy(UUID uuid) {
    this.signSpy.add(uuid);
  }

  public void removeSignSpy(UUID uuid) {
    this.signSpy.remove(uuid);
  }

  public boolean isSignSpy(UUID uuid) {
    return this.signSpy.contains(uuid);
  }

  public void sendMessageToSignSpies(Player player, String string, Location location) {
    if (!string.isEmpty()
        && !this.signSpy.isEmpty()
        && !PermissionsManager.CMIPerm.command_signspy_hide.hasPermission(player)) {
      String textValue = string;
      string = string.replace("/n", "%%n");
      string = CMIChatColor.translate(string);
      Snd snd = new Snd().setSender(player).setTarget(player);
      string = CMILC.Spy_Sign_msg.getLocale(snd, "[text]", string);
      string = string.replace("%%n", "/n");
      var hashSet = new HashSet<>(this.signSpy);
      HashSet<Player> spiesToNotify = new HashSet();
      RawMessage rawMessage = new RawMessage();
      rawMessage
          .addText(string.replace("%.break.%", ", "))
          .addHover(textValue.replace("%.break.%", "\\n"))
          .addSuggestion(
              "/cmi tppos "
                  + location.getBlockX()
                  + " "
                  + location.getBlockY()
                  + " "
                  + location.getBlockZ()
                  + " "
                  + location.getWorld().getName());

      for (UUID uuid : hashSet) {
        Player spyPlayer = CMIUser.getOnlinePlayer(uuid);
        if (spyPlayer == null) {
          this.signSpy.remove(uuid);
        } else if (!spyPlayer.getUniqueId().equals(player.getUniqueId())) {
          spiesToNotify.add(spyPlayer);
        }
      }

      rawMessage.show(spiesToNotify);
    }
  }

  public UUID getEmptyUserUUID() {
    return this.emptyUserUUID;
  }

  public void forceSaveAllUsersIntoDataBase() {
    for (var entry : this.users.entrySet()) {
      ((CMIUser) entry.getValue()).addForDelayedSave();
    }
  }

  public Player getFakeOperator() {
    this.fakeOperator = this.plugin.getNMS().createFakeOperator();
    if (this.fakeOperator != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.hidePlayer(this.plugin, this.fakeOperator);
        }
      } else {
        for (Player playerSecond : Bukkit.getOnlinePlayers()) {
          playerSecond.hidePlayer(this.fakeOperator);
        }
      }
    }

    return this.fakeOperator;
  }

  public Player getFakeOperatorRaw() {
    return this.fakeOperator;
  }

  public String getFakeUserName() {
    return "CMI-Fake-Operator";
  }

  public UUID getFakeUserUUID() {
    return this.fakeUserUUID;
  }

  public boolean openMailGui(final Player player, int value) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      return false;
    }

    var list = cmiUser.getMails();
    if (list.isEmpty()) {
      return false;
    }

    PageInfo pageInfo = new PageInfo(list.size() > 45 ? 45 : 54, list.size(), value);
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int page) {
            PlayerManager.this.openMailGui(player, page);
          }
        };

    for (final PlayerMail playerMail : list) {
      if (pageInfo.isEntryOk()) {
        if (pageInfo.isBreak()) {
          break;
        }

        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(CMIMaterial.PAPER) {
              public void click(GUIClickType guiClickType) {
                switch (guiClickType) {
                  case Left:
                  case LeftShift:
                  case Right:
                  case RightShift:
                }
              }

              public void updateLooks() {
                Snd snd = new Snd();
                if (playerMail.getSender().equalsIgnoreCase("Console")) {
                  snd.setSender(Bukkit.getConsoleSender());
                } else {
                  snd.setSender(
                      PlayerManager.this.plugin.getPlayerManager().getUser(playerMail.getSender()));
                }

                snd.setTarget(player);
                snd.setSenderName(playerMail.getSender());
                this.setName(
                    CMILC.getIM(
                        mail.class,
                        "mailHover",
                        snd,
                        "[time]",
                        DateFormat.MiliToDate(playerMail.getTime())));
                this.clearLore();
                this.addLore(CMIChatColor.translate(playerMail.getMessage()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  public Set<UUID> getAlerts() {
    return this.userAlerts.keySet();
  }

  @Deprecated
  public void addAlert(UUID uuid) {
    this.userAlerts.put(uuid, new CMIUserAlert(System.currentTimeMillis() + 86400L));
  }

  public CMIUserAlert getAlert(UUID uuid) {
    return this.userAlerts.get(uuid);
  }

  public void addAlert(UUID uuid, long longValue, String string) {
    this.userAlerts.put(uuid, new CMIUserAlert(longValue, string));
  }

  public void removeAlert(UUID uuid) {
    this.userAlerts.remove(uuid);
  }
}
