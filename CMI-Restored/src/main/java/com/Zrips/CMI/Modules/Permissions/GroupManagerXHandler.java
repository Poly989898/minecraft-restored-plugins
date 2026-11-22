package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import java.util.UUID;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GroupManagerXHandler implements PermissionInterface {
  private GroupManager groupManager;

  public GroupManagerXHandler(CMI cmi) {
    Plugin plugin = cmi.getServer().getPluginManager().getPlugin("GroupManagerX");
    if (plugin == null) {
      plugin = cmi.getServer().getPluginManager().getPlugin("GroupManager");
    }

    this.groupManager = (GroupManager) plugin;
  }

  @Override
  public String getMainGroup(Player player) {
    AnjoPermissionsHandler value = this.getHandler(player);
    return value == null ? null : value.getPrimaryGroup(player.getName());
  }

  @Override
  public String getPrefix(Player player) {
    AnjoPermissionsHandler value = this.getHandler(player);
    return value == null ? null : value.getUserPrefix(player.getName());
  }

  @Override
  public String getSufix(Player player) {
    AnjoPermissionsHandler value = this.getHandler(player);
    return value == null ? null : value.getUserSuffix(player.getName());
  }

  private AnjoPermissionsHandler getHandler(Player player) {
    WorldsHolder value = this.groupManager.getWorldsHolder();
    if (value == null) {
      return null;
    }

    try {
      return value.getWorldPermissions(player);
    } catch (Exception exception) {
      return null;
    }
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
