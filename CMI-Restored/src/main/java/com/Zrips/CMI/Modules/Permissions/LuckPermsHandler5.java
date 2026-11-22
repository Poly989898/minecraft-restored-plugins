package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LuckPermsHandler5 implements PermissionInterface {
  LuckPerms api = null;

  public LuckPermsHandler5() {
    var registeredServiceProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    if (registeredServiceProvider != null) {
      this.api = (LuckPerms) registeredServiceProvider.getProvider();
    }
  }

  @Override
  public String getMainGroup(Player player) {
    User value = this.api.getUserManager().getUser(player.getUniqueId());
    return value == null ? "" : value.getPrimaryGroup();
  }

  @Override
  public String getPrefix(Player player) {
    try {
      User value = this.api.getUserManager().getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      var optional = this.api.getContextManager().getQueryOptions(value);
      CachedMetaData metaData =
          value
              .getCachedData()
              .getMetaData(
                  optional.get() != null
                      ? (QueryOptions) optional.get()
                      : this.api.getContextManager().getStaticQueryOptions());
      return metaData.getPrefix();
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getNameColor(Player player) {
    try {
      User value = this.api.getUserManager().getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      var optional = this.api.getContextManager().getQueryOptions(value);
      CachedMetaData metaData =
          value
              .getCachedData()
              .getMetaData(
                  optional.get() != null
                      ? (QueryOptions) optional.get()
                      : this.api.getContextManager().getStaticQueryOptions());
      String string = metaData.getMetaValue("username-color");
      if (string == null) {
        string = CMI.getInstance().getPlayerManager().getUser(player).getMeta("username-color");
        if (string == null) {
          return null;
        }

        CMIChatColor cmiChatColor = CMIChatColor.getColor(string);
        return cmiChatColor == null ? null : cmiChatColor.getColorCode();
      } else {
        CMIChatColor parsedColor = CMIChatColor.getColor(string);
        return parsedColor == null ? null : parsedColor.getColorCode();
      }
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getSufix(Player player) {
    try {
      User value = this.api.getUserManager().getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      var optional = this.api.getContextManager().getQueryOptions(value);
      CachedMetaData metaData =
          value
              .getCachedData()
              .getMetaData(
                  optional.get() != null
                      ? (QueryOptions) optional.get()
                      : this.api.getContextManager().getStaticQueryOptions());
      return metaData.getSuffix();
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    User value = this.api.getUserManager().getUser(uuid);
    return value == null ? "" : value.getPrimaryGroup();
  }

  @Override
  public String getPrefix(UUID uuid) {
    try {
      User value = this.api.getUserManager().getUser(uuid);
      if (value == null) {
        return null;
      }

      var optional = this.api.getContextManager().getQueryOptions(value);
      CachedMetaData metaData =
          value
              .getCachedData()
              .getMetaData(
                  optional.get() != null
                      ? (QueryOptions) optional.get()
                      : this.api.getContextManager().getStaticQueryOptions());
      return metaData.getPrefix();
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getSufix(UUID uuid) {
    try {
      User value = this.api.getUserManager().getUser(uuid);
      if (value == null) {
        return null;
      }

      var optional = this.api.getContextManager().getQueryOptions(value);
      CachedMetaData metaData =
          value
              .getCachedData()
              .getMetaData(
                  optional.get() != null
                      ? (QueryOptions) optional.get()
                      : this.api.getContextManager().getStaticQueryOptions());
      return metaData.getSuffix();
    } catch (Exception exception) {
      return "";
    }
  }
}
