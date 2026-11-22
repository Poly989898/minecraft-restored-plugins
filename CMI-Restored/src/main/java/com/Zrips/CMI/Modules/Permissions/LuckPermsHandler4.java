package com.Zrips.CMI.Modules.Permissions;

import com.Zrips.CMI.CMI;
import java.util.Optional;
import java.util.UUID;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicesManager;

public class LuckPermsHandler4 implements PermissionInterface {
  LuckPermsApi api = null;

  public LuckPermsHandler4() {
    ServicesManager servicesManager = Bukkit.getServicesManager();
    if (servicesManager.isProvidedFor(LuckPermsApi.class)) {
      this.api = (LuckPermsApi) servicesManager.getRegistration(LuckPermsApi.class).getProvider();
    }
  }

  @Override
  public String getMainGroup(Player player) {
    User value = this.api.getUser(player.getUniqueId());
    return value == null ? "" : value.getPrimaryGroup();
  }

  @Override
  public String getPrefix(Player player) {
    try {
      User value = this.api.getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      UserData userDataSecond = value.getCachedData();
      Optional optional = this.api.getContextForUser(value);
      Contexts contextsSecond = (Contexts) optional.get();
      if (contextsSecond == null) {
        contextsSecond = Contexts.global();
      }

      MetaData metaDataSecond = userDataSecond.getMetaData(contextsSecond);
      return metaDataSecond.getPrefix();
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getNameColor(Player player) {
    try {
      User value = this.api.getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      UserData userDataSecond = value.getCachedData();
      Optional optional = this.api.getContextForUser(value);
      Contexts contextsSecond = (Contexts) optional.get();
      if (contextsSecond == null) {
        contextsSecond = Contexts.global();
      }

      MetaData metaDataSecond = userDataSecond.getMetaData(contextsSecond);
      String string = (String) metaDataSecond.getMeta().get("username-color");
      if (string == null) {
        string = CMI.getInstance().getPlayerManager().getUser(player).getMeta("username-color");
        if (string == null) {
          return null;
        }

        CMIChatColor cmiChatColor = CMIChatColor.getColor(string);
        return cmiChatColor == null ? null : cmiChatColor.getColorCode();
      } else {
        CMIChatColor cmiChatColorValue = CMIChatColor.getColor(string);
        return cmiChatColorValue == null ? null : cmiChatColorValue.getColorCode();
      }
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getSufix(Player player) {
    try {
      User value = this.api.getUser(player.getUniqueId());
      if (value == null) {
        return null;
      }

      UserData userDataSecond = value.getCachedData();
      Optional optional = this.api.getContextForUser(value);
      Contexts contextsSecond = (Contexts) optional.get();
      if (contextsSecond == null) {
        contextsSecond = Contexts.global();
      }

      MetaData metaDataSecond = userDataSecond.getMetaData(contextsSecond);
      return metaDataSecond.getSuffix();
    } catch (Exception exception) {
      return "";
    }
  }

  @Override
  public String getMainGroup(String string, UUID uuid) {
    User value = this.api.getUser(uuid);
    return value == null ? "" : value.getPrimaryGroup();
  }

  @Override
  public String getPrefix(UUID uuid) {
    User value = this.api.getUser(uuid);
    if (value == null) {
      return null;
    }

    UserData userDataSecond = value.getCachedData();
    Optional optional = this.api.getContextForUser(value);
    Contexts contextsSecond = (Contexts) optional.get();
    if (contextsSecond == null) {
      contextsSecond = Contexts.global();
    }

    MetaData metaDataSecond = userDataSecond.getMetaData(contextsSecond);
    return metaDataSecond.getPrefix();
  }

  @Override
  public String getSufix(UUID uuid) {
    User value = this.api.getUser(uuid);
    if (value == null) {
      return null;
    }

    UserData userDataSecond = value.getCachedData();
    Optional optional = this.api.getContextForUser(value);
    Contexts contextsSecond = (Contexts) optional.get();
    if (contextsSecond == null) {
      contextsSecond = Contexts.global();
    }

    MetaData metaDataSecond = userDataSecond.getMetaData(contextsSecond);
    return metaDataSecond.getSuffix();
  }
}
