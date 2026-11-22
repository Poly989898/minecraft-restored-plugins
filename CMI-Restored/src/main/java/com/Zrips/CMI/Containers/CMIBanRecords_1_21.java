package com.Zrips.CMI.Containers;

import io.papermc.paper.ban.BanListType;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;

public class CMIBanRecords_1_21 {
  static Method banPaperMethod = null;
  static Method pardonPaperMethod = null;
  static Method getBanPaperMethod = null;

  @Nullable
  public static BanEntry getBanEntry(UUID uuid, String string) {
    @SuppressWarnings("unchecked")
    BanList<PlayerProfile> banList = (BanList<PlayerProfile>) (BanList) getBanEntries();
    if (!Version.isPaper()) {
      PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid, string);
      return banList.getBanEntry(playerProfile);
    }

    try {
      com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(uuid, string);
      if (getBanPaperMethod == null) {
        getBanPaperMethod =
            banList
                .getClass()
                .getMethod("getBanEntry", Class.forName("org.bukkit.profile.PlayerProfile"));
      }

      return (BanEntry) getBanPaperMethod.invoke(banList, profile);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }

  @Nullable
  public static BanEntry addBan(UUID uuid, String string, String reason, Date date, String source) {
    @SuppressWarnings("unchecked")
    BanList<PlayerProfile> banList = (BanList<PlayerProfile>) (BanList) getBanEntries();
    if (!Version.isPaper()) {
      PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid, string);
      return banList.addBan(playerProfile, reason, date, source);
    }

    try {
      com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(uuid, string);
      if (banPaperMethod == null) {
        banPaperMethod =
            banList
                .getClass()
                .getMethod(
                    "addBan",
                    Class.forName("org.bukkit.profile.PlayerProfile"),
                    String.class,
                    Date.class,
                    String.class);
      }

      return (BanEntry) banPaperMethod.invoke(banList, profile, reason, date, source);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }

  @Nullable
  public static BanEntry addBanByIP(String string, String reason, Date date, String source) {
    return getBanEntriesByIP().addBan(string, reason, date, source);
  }

  @Nullable
  public static void pardon(UUID uuid, String string) {
    @SuppressWarnings("unchecked")
    BanList<PlayerProfile> banList = (BanList<PlayerProfile>) (BanList) getBanEntries();
    if (!Version.isPaper()) {
      PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid, string);
      banList.pardon(playerProfile);
    } else {
      com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(uuid, string);

      try {
        if (pardonPaperMethod == null) {
          pardonPaperMethod =
              banList
                  .getClass()
                  .getMethod("pardon", Class.forName("org.bukkit.profile.PlayerProfile"));
        }

        pardonPaperMethod.invoke(banList, profile);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
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
    return Version.isPaperBranch() ? Bukkit.getBanList(BanListType.IP) : Bukkit.getBanList(Type.IP);
  }

  @Nullable
  public static BanList<?> getBanEntries() {
    return Version.isPaperBranch()
        ? Bukkit.getBanList(BanListType.PROFILE)
        : Bukkit.getBanList(Type.PROFILE);
  }
}
