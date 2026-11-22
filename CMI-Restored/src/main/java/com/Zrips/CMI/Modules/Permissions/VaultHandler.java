package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.UUID;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class VaultHandler implements PermissionInterface {
  private CMI plugin;
  private Permission perms;

  public VaultHandler(CMI cmi) {
    this.plugin = cmi;
    var registeredServiceProvider =
        this.plugin.getServer().getServicesManager().getRegistration(Permission.class);
    this.perms = (Permission) registeredServiceProvider.getProvider();
  }

  @Override
  public String getMainGroup(Player player) {
    try {
      return this.perms.getPrimaryGroup(player);
    } catch (Exception | Error value) {
      return null;
    }
  }

  @Override
  public String getPrefix(Player player) {
    return null;
  }

  @Override
  public String getSufix(Player player) {
    return null;
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    try {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(uuid);
      if (!cmiUser.isOnline()) {
        return null;
      }

      Player player = cmiUser.getPlayer();
      if (player != null && string != null) {
        return this.perms.getPrimaryGroup(string, player);
      }
    } catch (Exception | Error value) {
    }

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
