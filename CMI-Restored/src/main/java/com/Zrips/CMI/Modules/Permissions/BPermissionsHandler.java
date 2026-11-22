package com.Zrips.CMI.Modules.Permissions;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;
import java.util.UUID;
import org.bukkit.entity.Player;

public class BPermissionsHandler implements PermissionInterface {
  @Override
  public String getMainGroup(Player player) {
    String[] strings =
        ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
    return strings[0];
  }

  @Override
  public String getPrefix(Player player) {
    return ApiLayer.getValue(
        player.getWorld().getName(), CalculableType.USER, player.getName(), "prefix");
  }

  @Override
  public String getSufix(Player player) {
    return ApiLayer.getValue(
        player.getWorld().getName(), CalculableType.USER, player.getName(), "suffix");
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    return null;
  }

  @Override
  public String getPrefix(UUID uuid) {
    return null;
  }

  @Override
  public String getSufix(UUID uuid) {
    return null;
  }

  @Override
  public String getNameColor(Player player) {
    return null;
  }
}
