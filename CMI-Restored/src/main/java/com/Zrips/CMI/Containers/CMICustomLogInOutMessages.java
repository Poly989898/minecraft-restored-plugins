package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.Arrays;
import java.util.regex.Matcher;
import net.Zrips.CMILib.FileHandler.ConfigReader;

public class CMICustomLogInOutMessages {
  private static boolean loginNameFilterUse = false;
  private static boolean logoutNameFilterUse = false;
  private static boolean loginDisabled = false;
  private static boolean logoutDisabled = false;
  private static boolean loginCustomUse = false;
  private static boolean logoutCustomUse = false;
  private static boolean logoutServerSwitch = false;
  private static boolean loginServerSwitch = false;
  private static boolean firstJoinMessageUse = false;
  private static int logoutAutoHideFrom = -1;
  private static int loginAutoHideFrom = -1;
  private static ChatFilterRule loginLogoutNameFilter = new ChatFilterRule();

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Messages.Login.Disabled", new String[] {"If set to true, login message wont be shown"});
    loginDisabled = configReader.get("Messages.Login.Disabled", false);
    configReader.addComment(
        "Messages.Login.AutoHideFrom",
        new String[] {
          "Defines number of players from which to automatically start hiding join messages",
          "Set to -1 to disable this"
        });
    loginAutoHideFrom = configReader.get("Messages.Login.AutoHideFrom", -1);
    configReader.addComment(
        "Messages.Login.Custom.Use",
        new String[] {
          "If set to true, custom login message will be used. "
              + PermissionsManager.CMIPerm.messages_disablelogin.getPermission()
              + " can be used to disable message for player"
        });
    loginCustomUse = configReader.get("Messages.Login.Custom.Use", false);
    configReader.addComment(
        "Messages.Login.Custom.ServerSwitch",
        new String[] {
          "When enabled and you have bungeeserver with CMIB on it we will try to detect where"
              + " player went and we will show switch server message instead of login if possible"
        });
    loginServerSwitch = configReader.get("Messages.Login.Custom.ServerSwitch", true);
    configReader.addComment(
        "Messages.Logout.Disabled", new String[] {"If set to true, logout message wont be shown"});
    logoutDisabled = configReader.get("Messages.Logout.Disabled", false);
    configReader.addComment(
        "Messages.Logout.AutoHideFrom",
        new String[] {
          "Defines number of players from which to automatically start hiding logout messages",
          "Set to -1 to disable this"
        });
    logoutAutoHideFrom = configReader.get("Messages.Logout.AutoHideFrom", -1);
    configReader.addComment(
        "Messages.Logout.Custom.Use",
        new String[] {
          "If set to true, custom logout message will be used. "
              + PermissionsManager.CMIPerm.messages_disablequit.getPermission()
              + " can be used to disable message for player"
        });
    logoutCustomUse = configReader.get("Messages.Logout.Custom.Use", false);
    configReader.addComment(
        "Messages.Logout.Custom.ServerSwitch",
        new String[] {
          "When enabled and you have bungeeserver with CMIB on it we will try to detect where"
              + " player went and we will show switch server message instead of logout if possible"
        });
    logoutServerSwitch = configReader.get("Messages.Logout.Custom.ServerSwitch", true);
    configReader.addComment(
        "Messages.FirstJoinMessage",
        new String[] {
          "Check locale file for translation and custom placeholders: [playername], [totalUsers],"
              + " [onlinePlayers]"
        });
    firstJoinMessageUse = configReader.get("Messages.FirstJoinMessage.Use", false);
    configReader.addComment(
        "Messages.Filter",
        new String[] {
          "When enabled player names will be checked against provided regex and if match is found"
              + " then login and/or logout message will not be shown"
        });
    loginNameFilterUse = configReader.get("Messages.Filter.ForLogin", false);
    logoutNameFilterUse = configReader.get("Messages.Filter.ForLogout", false);
    loginLogoutNameFilter.setPattern(
        configReader.get(
            "Messages.Filter.Regex",
            Arrays.asList(
                "b[a@][s\\$][t\\+][a@]rd",
                "(c|k|ck|q)[o0](c|k|ck|q)[s\\$]u(c|k|ck|q)[e3]r",
                "mast(e|ur)b(8|ait|ate)")));
  }

  public static int getLogoutAutoHideFrom() {
    return logoutAutoHideFrom;
  }

  public static int getLoginAutoHideFrom() {
    return loginAutoHideFrom;
  }

  public static boolean isLogoutDisabled() {
    return logoutDisabled;
  }

  public static boolean isLogoutNameFilterUse() {
    return logoutNameFilterUse;
  }

  public static Matcher matchLongLogoutNameFilter(String string) {
    return loginLogoutNameFilter.getMatcher(string);
  }

  public static boolean isLogoutCustomUse() {
    return logoutCustomUse;
  }

  public static boolean isLogoutServerSwitch() {
    return logoutServerSwitch;
  }

  public static boolean isLoginDisabled() {
    return loginDisabled;
  }

  public static boolean isLoginNameFilterUse() {
    return loginNameFilterUse;
  }

  public static boolean isLoginCustomUse() {
    return loginCustomUse;
  }

  public static boolean isLoginServerSwitch() {
    return loginServerSwitch;
  }

  public static boolean isFirstJoinMessageUse() {
    return firstJoinMessageUse;
  }
}
