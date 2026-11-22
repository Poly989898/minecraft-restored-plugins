package com.Zrips.CMI.Containers;

import com.Zrips.CMI.Locale.CMILC;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.BanEntry;

public class CMIBanEntry {
  long createdOn = 0L;
  long banUntil = 0L;
  String reason = null;
  CMIUser bannedBy = null;
  CMIUser banned = null;

  public CMIBanEntry(CMIUser cmiUser) {
    this.banned = cmiUser;
    this.createdOn = System.currentTimeMillis();
  }

  public Long getCreated() {
    return this.createdOn;
  }

  public Long getExpiration() {
    return this.banUntil;
  }

  public String getReason() {
    return this.reason;
  }

  public String getSource() {
    return this.bannedBy == null
        ? LC.info_Console.getLocale(new Object[0])
        : this.bannedBy.getName();
  }

  public CMIUser getSourceUser() {
    return this.bannedBy;
  }

  public String getTarget() {
    return this.banned.getName();
  }

  public CMIUser getTargetUser() {
    return this.banned;
  }

  public void setCreated(Long createdTime) {
    this.createdOn = createdTime == null ? 0L : createdTime;
  }

  public void setExpiration(Long expirationTime) {
    if (expirationTime != null) {
      this.banUntil = expirationTime;
    } else {
      this.banUntil = -1L;
    }
  }

  public void setReason(String string) {
    this.reason = string;
  }

  public void setSource(CMIUser cmiUser) {
    this.bannedBy = cmiUser;
  }

  public boolean isPermanent() {
    return this.banUntil >= 0L ? false : this.banUntil == -1L;
  }

  public Long getLeftTime() {
    if (this.banUntil <= 0L) {
      return 0L;
    } else {
      return this.banUntil < System.currentTimeMillis()
          ? 0L
          : this.banUntil - System.currentTimeMillis();
    }
  }

  public static String getBanMessage(BanEntry banEntry) {
    if (banEntry == null) {
      return "";
    }

    if (banEntry.getExpiration() == null) {
      String string = CMILC.getIM("ban", "message") + " \n";
      return string
          + CMILC.getIM(
              "ban",
              "reason",
              "[reason]",
              banEntry.getReason() != null
                  ? banEntry.getReason()
                  : CMILC.getIM("ban", "defaultReason"));
    }

    String banMessage = CMILC.getIM("tempban", "message") + " \n";
    banMessage =
        banMessage
            + CMILC.getIM(
                "ban",
                "reason",
                "[reason]",
                banEntry.getReason() != null
                    ? banEntry.getReason()
                    : CMILC.getIM("ban", "defaultReason"));
    if (banEntry.getExpiration() != null) {
      banMessage =
          banMessage
              + " \n"
              + CMILC.getIM(
                  "tempban",
                  "time",
                  "[time]",
                  CMITimeManager.to24hourShort(
                      banEntry.getExpiration().getTime() - System.currentTimeMillis() + 1000L));
    }

    return banMessage;
  }

  public static String getIpBanMessage(BanEntry banEntry) {
    if (banEntry == null) {
      return "";
    }

    if (banEntry.getExpiration() == null) {
      String string = CMILC.getIM("ipban", "message") + " \n";
      return string
          + CMILC.getIM(
              "ban",
              "reason",
              "[reason]",
              banEntry.getReason() != null
                  ? banEntry.getReason()
                  : CMILC.getIM("ban", "defaultReason"));
    }

    String banMessage = CMILC.getIM("tempipban", "message") + " \n";
    banMessage =
        banMessage
            + CMILC.getIM(
                "ban",
                "reason",
                "[reason]",
                banEntry.getReason() != null
                    ? banEntry.getReason()
                    : CMILC.getIM("ban", "defaultReason"));
    if (banEntry.getExpiration() != null) {
      banMessage =
          banMessage
              + " \n"
              + CMILC.getIM(
                  "tempban",
                  "time",
                  "[time]",
                  CMITimeManager.to24hourShort(
                      banEntry.getExpiration().getTime() - System.currentTimeMillis() + 1000L));
    }

    return banMessage;
  }
}
