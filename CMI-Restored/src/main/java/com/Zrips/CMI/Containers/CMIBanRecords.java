package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import io.papermc.paper.ban.BanListType;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CMIBanRecords {
  private static HashMap<UUID, CMIBanEntry> banCache = new HashMap<>();

  public static CMIBanEntry getBanEntryRaw(UUID uuid) {
    return banCache.get(uuid);
  }

  public static CMIBanEntry getBanEntry(UUID uuid) {
    return banCache.computeIfAbsent(uuid, playerUuid -> getBanEntry(CMIUser.getUser(uuid), true));
  }

  public static CMIBanEntry addBanEntry(UUID uuid, CMIBanEntry cmiBanEntry) {
    return banCache.put(uuid, cmiBanEntry);
  }

  public static CMIBanEntry removeBanEntry(UUID uuid) {
    return banCache.remove(uuid);
  }

  public static void updateBanEntry(CMIUser cmiUser, BanEntry banEntry) {
    if (banEntry != null) {
      CMIBanEntry cmiBanEntry = new CMIBanEntry(cmiUser);
      if (banEntry.getSource() != null) {
        cmiBanEntry.setSource(CMI.getInstance().getPlayerManager().getUser(banEntry.getSource()));
      }

      if (banEntry.getCreated() != null) {
        cmiBanEntry.setCreated(
            banEntry.getCreated() == null ? null : banEntry.getCreated().getTime());
      }

      cmiBanEntry.setExpiration(
          banEntry.getExpiration() == null ? null : banEntry.getExpiration().getTime());
      cmiBanEntry.setReason(
          banEntry.getReason() != null && !banEntry.getReason().isEmpty()
              ? CMIChatColor.translate(banEntry.getReason())
              : CMILC.getIM("ban", "defaultReason"));
      addBanEntry(cmiUser.getUniqueId(), cmiBanEntry);
    }
  }

  public static CMIBanEntry getBanEntry(CMIUser cmiUser, boolean flag) {
    if (cmiUser == null) {
      return null;
    }

    CMIBanEntry cmiBanEntry = getBanEntryRaw(cmiUser.getUniqueId());
    if (cmiBanEntry == null && flag && cmiUser.getName() != null) {
      updateBanEntry(cmiUser, getBanEntry(cmiUser.getUniqueId(), cmiUser.getName()));
      cmiBanEntry = getBanEntryRaw(cmiUser.getUniqueId());
    }

    if (cmiBanEntry == null) {
      cmiBanEntry = new CMIBanEntry(cmiUser);
      addBanEntry(cmiUser.getUniqueId(), cmiBanEntry);
    }

    return cmiBanEntry;
  }

  public static CMIUser setBanned(
      CMIUser cmiUser, String string, CMIUser sourceUser, Date date, Date createdDate) {
    CMIBanEntry cmiBanEntry = new CMIBanEntry(cmiUser);
    cmiBanEntry.setSource(sourceUser);
    if (createdDate != null) {
      cmiBanEntry.setCreated(createdDate.getTime());
    }

    cmiBanEntry.setExpiration(date == null ? -1L : date.getTime());
    if (string != null && !string.isEmpty()) {
      string =
          CMIChatColor.translate(
              CMI.getInstance()
                  .getLM()
                  .updateSnd(new Snd().setSender(sourceUser).setTarget(cmiUser), string));
    } else {
      string =
          CMILC.getIM("ban", "defaultReason", new Snd().setSender(sourceUser).setTarget(cmiUser));
      if (string.isEmpty()) {
        string = CMIChatColor.translate("&r");
      }
    }

    cmiBanEntry.setReason(string);
    Date expirationDate =
        cmiBanEntry.getExpiration() == -1L ? null : new Date(cmiBanEntry.getExpiration());
    BanEntry banEntry =
        addBan(
            cmiUser.getUniqueId(),
            cmiUser.getName(),
            cmiBanEntry.getReason(),
            expirationDate,
            cmiBanEntry.getSource());
    if (cmiUser.isOnline()) {
      String banMessage = CMIBanEntry.getBanMessage(banEntry);
      if (!banMessage.isEmpty()) {
        cmiUser.getPlayer().kickPlayer(banMessage);
      } else {
        cmiUser.getPlayer().kickPlayer(cmiBanEntry.getReason().replace("\\n", "\n"));
      }
    }

    addBanEntry(cmiUser.getUniqueId(), cmiBanEntry);
    cmiUser.addForDelayedSave();
    return cmiUser;
  }

  public static CMIUser unBan(CMIUser cmiUser) {
    pardon(cmiUser.getUniqueId(), cmiUser.getName());
    if (cmiUser.getLastIp() != null) {
      pardonByIP(cmiUser.getLastIp());
    }

    removeBanEntry(cmiUser.getUniqueId());
    cmiUser.addForDelayedSave();
    return cmiUser;
  }

  public static BanEntry getBanEntryByIP(String string) {
    return getBanEntriesByIP().getBanEntry(string);
  }

  @Nullable
  public static BanEntry getBanEntry(CMIUser cmiUser) {
    return getBanEntry(cmiUser.getUniqueId(), cmiUser.getName());
  }

  @Nullable
  public static BanEntry getBanEntry(Player player) {
    return getBanEntry(player.getUniqueId(), player.getName());
  }

  @Nullable
  public static BanEntry getBanEntry(UUID uuid, String string) {
    return Version.isCurrentEqualOrHigher(Version.v1_21_R1)
        ? CMIBanRecords_1_21.getBanEntry(uuid, string)
        : getBanEntries().getBanEntry(string);
  }

  @Nullable
  public static BanEntry addBan(UUID uuid, String string, String reason, Date date, String source) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      CMIBanRecords_1_21.addBan(uuid, string, reason, date, source);
      return null;
    } else {
      return getBanEntries().addBan(string, reason, date, source);
    }
  }

  @Nullable
  public static BanEntry addBanByIP(String string, String reason, Date date, String source) {
    return getBanEntriesByIP().addBan(string, reason, date, source);
  }

  @Nullable
  public static void pardon(UUID uuid, String string) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      CMIBanRecords_1_21.pardon(uuid, string);
    } else {
      try {
        getBanEntries().pardon(string);
      } catch (Exception | Error value) {
      }
    }
  }

  @Nullable
  public static void pardonByIP(String string) {
    try {
      getBanEntriesByIP().pardon(string);
    } catch (Exception | Error value) {
    }
  }

  @Nullable
  public static BanList<?> getBanEntriesByIP() {
    return Version.isPaperBranch() && Version.isCurrentEqualOrHigher(Version.v1_21_R1)
        ? Bukkit.getBanList(BanListType.IP)
        : Bukkit.getBanList(Type.IP);
  }

  @Nullable
  public static BanList<?> getBanEntries() {
    return Version.isCurrentEqualOrHigher(Version.v1_21_R1)
        ? CMIBanRecords_1_21.getBanEntries()
        : Bukkit.getBanList(Type.NAME);
  }
}
