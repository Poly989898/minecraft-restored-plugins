package com.Zrips.CMI.Modules.Permissions;

import java.util.UUID;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXHandler implements PermissionInterface {
  private PermissionManager perms = PermissionsEx.getPermissionManager();

  @Override
  public String getMainGroup(Player player) {
    PermissionUser value = this.perms.getUser(player);
    if (value == null) {
      return null;
    }

    String[] strings = value.getGroupNames();
    return strings != null && strings.length != 0 ? strings[0] : null;
  }

  @Override
  public String getPrefix(Player player) {
    PermissionUser value = this.perms.getUser(player.getUniqueId());
    return value == null ? null : value.getPrefix();
  }

  @Override
  public String getSufix(Player player) {
    PermissionUser value = this.perms.getUser(player.getUniqueId());
    return value == null ? null : value.getSuffix();
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    PermissionUser value = this.perms.getUser(uuid);
    if (value == null) {
      return null;
    }

    String[] strings = value.getGroupNames();
    return strings != null && strings.length != 0 ? strings[0] : null;
  }

  @Override
  public String getPrefix(UUID uuid) {
    PermissionUser value = this.perms.getUser(uuid);
    return value == null ? null : value.getSuffix();
  }

  @Override
  public String getSufix(UUID uuid) {
    PermissionUser value = this.perms.getUser(uuid);
    return value == null ? null : value.getSuffix();
  }

  @Override
  public String getNameColor(Player player) {
    return null;
  }
}
