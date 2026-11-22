package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import java.util.Optional;
import java.util.UUID;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public class UltraPermissions3Handler implements PermissionInterface {
  public UltraPermissions3Handler() {
    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          PluginManager pluginManager = Bukkit.getServer().getPluginManager();
          if (pluginManager.getPermission("cmi.command") != null) {
            pluginManager.removePermission("cmi.command");
          }

          pluginManager.addPermission(new Permission("cmi.command", PermissionDefault.TRUE));
        },
        200L);
  }

  @Override
  public String getMainGroup(Player player) {
    return player == null
        ? ""
        : this.getMainGroup(player.getWorld().getName(), player.getUniqueId());
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    try {
      Optional optional = UltraPermissions.getAPI().getUsers().uuid(uuid);
      if (optional == null) {
        return "";
      }

      GroupList value = ((User) optional.get()).getActiveGroups();
      if (value == null) {
        return "";
      }

      GroupList groups = value.bestToWorst();
      if (groups == null) {
        return "";
      }

      Group firstGroup = (Group) groups.get(0);
      return firstGroup == null ? "" : firstGroup.getName();
    } catch (Exception | NoSuchMethodError value4) {
      return "";
    }
  }

  @Override
  public String getPrefix(Player player) {
    return player == null ? "" : this.getPrefix(player.getUniqueId());
  }

  @Override
  public String getPrefix(UUID uuid) {
    try {
      Optional optional = UltraPermissions.getAPI().getUsers().uuid(uuid);
      if (optional == null) {
        return "";
      } else if (!((User) optional.get()).hasPrefix()) {
        Group value = (Group) ((User) optional.get()).getActiveGroups().bestToWorst().get(0);
        return (String) value.getPrefix().get();
      } else {
        return (String) ((User) optional.get()).getPrefix().get();
      }
    } catch (Exception | NoSuchMethodError value2) {
      return "";
    }
  }

  @Override
  public String getSufix(Player player) {
    return player == null ? "" : this.getSufix(player.getUniqueId());
  }

  @Override
  public String getSufix(UUID uuid) {
    try {
      Optional optional = UltraPermissions.getAPI().getUsers().uuid(uuid);
      if (optional == null) {
        return "";
      } else if (!((User) optional.get()).hasSuffix()) {
        Group value = (Group) ((User) optional.get()).getActiveGroups().bestToWorst().get(0);
        return (String) value.getSuffix().get();
      } else {
        return (String) ((User) optional.get()).getSuffix().get();
      }
    } catch (Exception | NoSuchMethodError value2) {
      return "";
    }
  }

  @Override
  public String getNameColor(Player player) {
    return null;
  }
}
