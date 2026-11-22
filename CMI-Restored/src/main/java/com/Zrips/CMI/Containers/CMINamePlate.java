package com.Zrips.CMI.Containers;

import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.ChatColor;

public class CMINamePlate {
  private static HashMap<UUID, CMINamePlate> namePlates = new HashMap<>();
  private String namePlatePrefix = null;
  private String namePlateSuffix = null;
  private ChatColor namePlateNameColor = null;

  public String getNamePlatePrefix() {
    return this.namePlatePrefix;
  }

  public void setNamePlatePrefix(String string) {
    this.namePlatePrefix = string;
  }

  public String getNamePlateSuffix() {
    return this.namePlateSuffix;
  }

  public void setNamePlateSuffix(String string) {
    this.namePlateSuffix = string;
  }

  public ChatColor getNamePlateNameColor() {
    return this.namePlateNameColor;
  }

  public void setNamePlateNameColor(ChatColor chatColor) {
    this.namePlateNameColor = chatColor;
  }

  public boolean isEmptyRecord() {
    return this.namePlatePrefix == null
        && this.namePlateSuffix == null
        && this.namePlateNameColor == null;
  }

  public static String getNamePlatePrefix(UUID uuid) {
    CMINamePlate cmiNamePlate = namePlates.get(uuid);
    if (cmiNamePlate == null) {
      return null;
    } else {
      return cmiNamePlate.getNamePlatePrefix() != null
              && Version.isCurrentEqualOrLower(Version.v1_15_R1)
              && cmiNamePlate.getNamePlatePrefix().length() > 16
          ? cmiNamePlate.getNamePlatePrefix().substring(0, 15)
          : cmiNamePlate.getNamePlatePrefix();
    }
  }

  public static void setNamePlatePrefix(UUID uuid, String string) {
    CMINamePlate cmiNamePlate = namePlates.computeIfAbsent(uuid, playerUuid -> new CMINamePlate());
    cmiNamePlate.setNamePlatePrefix(string != null && string.isEmpty() ? null : string);
    if (cmiNamePlate.isEmptyRecord()) {
      namePlates.remove(uuid);
    }
  }

  public static String getNamePlateSuffix(UUID uuid) {
    CMINamePlate cmiNamePlate = namePlates.get(uuid);
    if (cmiNamePlate == null) {
      return null;
    } else {
      return cmiNamePlate.getNamePlateSuffix() != null
              && Version.isCurrentEqualOrLower(Version.v1_15_R1)
              && cmiNamePlate.getNamePlateSuffix().length() > 16
          ? cmiNamePlate.getNamePlateSuffix().substring(0, 15)
          : cmiNamePlate.getNamePlateSuffix();
    }
  }

  public static void setNamePlateSuffix(UUID uuid, String string) {
    CMINamePlate cmiNamePlate = namePlates.computeIfAbsent(uuid, playerUuid -> new CMINamePlate());
    cmiNamePlate.setNamePlateSuffix(string != null && string.isEmpty() ? null : string);
    if (cmiNamePlate.isEmptyRecord()) {
      namePlates.remove(uuid);
    }
  }

  public static ChatColor getNamePlateNameColor(UUID uuid) {
    CMINamePlate cmiNamePlate = namePlates.get(uuid);
    return cmiNamePlate == null ? null : cmiNamePlate.getNamePlateNameColor();
  }

  public static void setNamePlateNameColor(UUID uuid, ChatColor chatColor) {
    CMINamePlate cmiNamePlate = namePlates.computeIfAbsent(uuid, playerUuid -> new CMINamePlate());
    cmiNamePlate.setNamePlateNameColor(chatColor);
    if (cmiNamePlate.isEmptyRecord()) {
      namePlates.remove(uuid);
    }
  }
}
