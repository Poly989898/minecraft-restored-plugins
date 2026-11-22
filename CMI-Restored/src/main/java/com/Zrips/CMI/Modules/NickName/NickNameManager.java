package com.Zrips.CMI.Modules.NickName;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.entity.Player;

public class NickNameManager {
  private CMI plugin;
  private HashMap<String, UUID> NickMap = new HashMap<>();
  private String NickNameFormat;
  private boolean DisplayNameChange;
  private String ValidNickNameRegex;
  private List<String> NickNameBlackList;
  private int NickNameLengthMax;
  private int NickNameLengthMin;
  private String NickNamePrefix;
  private Boolean PreventRealNames;
  private Boolean PrefixWhenDifferent;
  private Boolean IncludeInTabComplete;
  private Boolean IncludeRealName;

  public NickNameManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void removeNickName(String string) {
    this.NickMap.remove(CMIChatColor.stripColor(string).toLowerCase());
  }

  public void addNewNickName(String string, UUID uuid) {
    this.NickMap.put(CMIChatColor.stripColor(string).toLowerCase(), uuid);
  }

  public UUID whoUsesName(String string) {
    UUID uuid = this.NickMap.get(string.toLowerCase());
    if (uuid == null) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getByName(string);
      if (cmiUser != null && string.equalsIgnoreCase(cmiUser.getName())) {
        cmiUser.getUniqueId();
      }
    }

    return uuid;
  }

  public List<String> getNickNamesStartingWith(Player player, String string) {
    string = string.toLowerCase();
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.NickMap.entrySet()) {
      if (((String) entry.getKey()).startsWith(string)) {
        Player otherPlayer = CMIUser.getOnlinePlayer((UUID) entry.getValue());
        if (player != null && otherPlayer != null && player.canSee(otherPlayer)) {
          arrayList.add((String) entry.getKey());
        }
      }
    }

    return arrayList;
  }

  public void load() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "DisplayName.Change",
        new String[] {
          "If you have 3rd party plugin changing players display name, set this to false"
        });
    this.DisplayNameChange = configReader.get("DisplayName.Change", true);
    configReader.addComment(
        "DisplayName.Format",
        new String[] {
          "Format of players display name. By default only nick name will be visible, if its set,"
              + " if not, then players name",
          "Possible custom varibales: {prefix} {suffix} {nicknameprefix}",
          "Supports placeholders"
        });
    this.NickNameFormat =
        configReader.get(
            "DisplayName.Format", configReader.getC().getString("NickName.Format", "{nickName}"));
    configReader.addComment(
        "DisplayName.ValidNicknameRegex",
        new String[] {
          "Defines regex for valid nick name",
          "By default only letters and numbers are allowed",
          "If you want to allow any letter from any language use [^\\p{L}0-9\\-\\_]"
        });
    this.ValidNickNameRegex =
        configReader.get("DisplayName.ValidNicknameRegex", "[^a-zA-Z0-9\\-\\_]");
    configReader.addComment(
        "NickName.BlackList",
        new String[] {
          "Prevents players to change their nick name to one of defined without permission",
          "Use lower case",
          "cmi.command.nick.bypassblacklist",
          "to bypass protection against already in use name/nickname use"
              + " cmi.command.nick.bypassinuse"
        });
    this.NickNameBlackList =
        configReader.get(
            "NickName.BlackList",
            Arrays.asList("admin", "administrator", "server", "staff", "staf"));

    for (int value = 0; value < this.NickNameBlackList.size(); value++) {
      this.NickNameBlackList.set(value, this.NickNameBlackList.get(value).toLowerCase());
    }

    configReader.addComment(
        "NickName.MinLength",
        new String[] {
          "Min length of nick name, can be bypassed with cmi.command.nick.bypass.length"
        });
    this.NickNameLengthMin = configReader.get("NickName.MinLength", 4);
    configReader.addComment(
        "NickName.MaxLength",
        new String[] {
          "Max length of nick name, can be bypassed with cmi.command.nick.bypass.length"
        });
    this.NickNameLengthMax = configReader.get("NickName.MaxLength", 16);
    configReader.addComment(
        "NickName.PreventRealNames",
        new String[] {
          "When enabled players will not be able to choose existing real player names",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.command_nick_bypassrealname.getCleanPermissionForShow()
              + " permission node"
        });
    this.PreventRealNames = configReader.get("NickName.PreventRealNames", true);
    configReader.addComment(
        "NickName.Prefix",
        new String[] {
          "Adds prefix for players nickname to indicate that its not real name. This can be added"
              + " to display name with {nicknameprefix}"
        });
    this.NickNamePrefix = configReader.get("NickName.Prefix", "~");
    configReader.addComment(
        "NickName.PrefixWhenDifferent",
        new String[] {
          "When true, will only add nickname prefix when its not same as original name. This can"
              + " allow colorization or capitalization change without adding prefix"
        });
    this.PrefixWhenDifferent = configReader.get("NickName.PrefixWhenDifferent", false);
    configReader.addComment(
        "NickName.TabComplete.ReplaceReal",
        new String[] {
          "When true, online players nick name will be used instead of real name in tabcomplete"
        });
    this.IncludeInTabComplete =
        configReader.get(
            "NickName.TabComplete.ReplaceReal",
            configReader.getC().getBoolean("NickName.IncludeInTabComplete", true));
    configReader.addComment(
        "NickName.TabComplete.IncludeReal",
        new String[] {
          "When true, we will include real name on top of nickname. This only has effect when"
              + " ReplaceName is enabled"
        });
    this.IncludeRealName = configReader.get("NickName.TabComplete.IncludeReal", false);
  }

  public String getNickNameFormat() {
    return this.NickNameFormat;
  }

  public List<String> getNickNameBlackList() {
    return this.NickNameBlackList;
  }

  public int getNickNameLengthMax() {
    return this.NickNameLengthMax;
  }

  public int getNickNameLengthMin() {
    return this.NickNameLengthMin;
  }

  @Deprecated
  public Boolean isColorsNickName() {
    return this.plugin.getChatManager().isColorsNickName();
  }

  public String getNickNamePrefix() {
    return this.NickNamePrefix;
  }

  public void setNickNamePrefix(String string) {
    this.NickNamePrefix = string;
  }

  public Boolean isPrefixWhenDifferent() {
    return this.PrefixWhenDifferent;
  }

  public void setPrefixWhenDifferent(Boolean prefixWhenDifferentValue) {
    this.PrefixWhenDifferent = prefixWhenDifferentValue;
  }

  public String getValidNickNameRegex() {
    return this.ValidNickNameRegex;
  }

  public boolean isDisplayNameChange() {
    return this.DisplayNameChange;
  }

  public boolean isIncludeNickNameInTabComplete() {
    return this.IncludeInTabComplete;
  }

  public Boolean isIncludeRealNameInTabComplete() {
    return this.IncludeRealName;
  }

  public Boolean getPreventRealNames() {
    return this.PreventRealNames;
  }
}
