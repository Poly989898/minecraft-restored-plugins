package com.Zrips.CMI.Modules.Permissions;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class ZPermissionsHandler implements PermissionInterface {
  private ZPermissionsService handler =
      (ZPermissionsService) Bukkit.getServicesManager().load(ZPermissionsService.class);

  @Override
  public String getMainGroup(Player player) {
    try {
      return this.handler.getPlayerPrimaryGroup(player.getUniqueId());
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getPrefix(Player player) {
    try {
      return this.handler.getPlayerPrefix(player.getUniqueId());
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getSufix(Player player) {
    try {
      return this.handler.getPlayerSuffix(player.getUniqueId());
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    try {
      return this.handler.getPlayerPrimaryGroup(uuid);
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getPrefix(UUID uuid) {
    try {
      return this.handler.getPlayerPrefix(uuid);
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getSufix(UUID uuid) {
    try {
      return this.handler.getPlayerSuffix(uuid);
    } catch (NoSuchMethodError noSuchMethodError) {
      return "";
    }
  }

  @Override
  public String getNameColor(Player player) {
    return null;
  }
}
