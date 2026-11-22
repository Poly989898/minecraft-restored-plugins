package com.Zrips.CMI.Modules.ChatFormat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChatFormatManager {
  private CMI plugin;
  HashMap<Integer, String> groupFormats = new HashMap<>();
  HashMap<Integer, String> groupMessageFormats = new HashMap<>();
  private HashMap<String, CMIChatRoom> chatRooms = new HashMap<>();
  private HashMap<UUID, CMIChatRoom> userChatRoom = new HashMap<>();
  private Set<UUID> staffChats = new HashSet<>();
  private Long chatMutedUntil = null;
  private String chatMutedReason = null;
  private boolean BungeeMessages = true;
  private boolean BungeePublicMessages = true;
  private boolean BungeePublicMessagesSingleIgnore = false;
  private boolean BungeeStaffMessages = true;
  private String ChatGeneralFormat = "";
  private String ChatMessageGeneralFormat = "";
  private int ChatGeneralRange = 0;
  private int ChatShoutRange = 0;
  private String ChatShoutColor = "";
  private Integer ChatShoutCost = 0;
  public static int ChatRoomLife = 3600;
  public static String ChatRoomShout = "!";
  public static TreeMap<Integer, Integer> chatDynamicRange = new TreeMap<>();
  ChatFilterRule rule = new ChatFilterRule();
  ChatFilterRule itemRule = new ChatFilterRule();
  private boolean ChatTranslateLink = true;
  private boolean hoverItems = false;
  private String fileName = "ChatRooms.yml";

  public void clearCache(UUID uuid) {
    this.staffChats.remove(uuid);

    for (var entry : new HashMap<>(this.chatRooms).entrySet()) {
      ((CMIChatRoom) entry.getValue()).cleanOldUsers();
      if (((CMIChatRoom) entry.getValue()).getUsers().isEmpty()
          && !((CMIChatRoom) entry.getValue()).isPersistent()) {
        this.chatRooms.remove(entry.getKey());
      }
    }

    this.userChatRoom.remove(uuid);
  }

  public ChatFormatManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig(ConfigReader configReader) {
    configReader.addComment(
        "Chat.TranslateLink",
        new String[] {
          "When set to false, web pge links in a chat will not get shortened to default [LINK]"
              + " format"
        });
    this.ChatTranslateLink = configReader.get("Chat.TranslateLink", true);
    configReader.addComment(
        "Chat.LinkRegex",
        new String[] {
          "Defines regex when replacing url in chat with short word",
          "Examples:",
          "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})",
          "((http|https|ftp|ftps)\\:\\/\\/)?[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,3}(\\/\\S*)?",
          "((http|https|ftp|ftps)\\:\\/\\/)?[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,3}(\\/\\S*)?([^\\s]+)"
        });
    String string =
        configReader.get(
            "Chat.LinkRegex",
            "(^| )((http|https|ftp|ftps)\\:\\/\\/)?[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,3}(\\/\\S*)?([^\\s|^\\)]+)");
    this.rule.setPattern(Arrays.asList(string));
    configReader.addComment(
        "Chat.HoverItems.Enabled",
        new String[] {
          "When set to true, particular variables in chat will be translated into items player are"
              + " holding. List of variables belove"
        });
    this.hoverItems =
        configReader.get(
            "Chat.HoverItems.Enabled", configReader.getC().getBoolean("Chat.HoverItems", true));
    configReader.addComment(
        "Chat.HoverItems.ItemRegex",
        new String[] {
          "Defines regex when replacing item line in chat with players item in hand information."
              + " Only works when CMI hover over chat format is enabled"
        });
    List<String> list =
        (List<String>)
            configReader.get(
                "Chat.HoverItems.ItemRegex",
                configReader
                    .getC()
                    .getList(
                        "Chat.ItemRegex",
                        Arrays.asList(
                            "(\\[item\\])",
                            "(([&§]\\S)+)?\\[(([&§]\\S)+)?i(([&§]\\S)+)?t(([&§]\\S)+)?e(([&§]\\S)+)?m(([&§]\\S)+)?\\]")));
    this.itemRule.setPattern(list);
    configReader.addComment(
        "Chat.Bungee.Messages",
        new String[] {
          "Attention! This will require you to have CMI Bungee plugin which can be found at"
              + " zrips.net",
          "Or direct download https://www.zrips.net/cmi/",
          "Do you want to enable private messaging over bungeecord",
          "Bungee cord support needs to be enabled for this to have any effect"
        });
    this.BungeeMessages =
        configReader.get(
            "Chat.Bungee.Messages", configReader.getC().getBoolean("Chat.BungeeMessages", true));
    configReader.addComment(
        "Chat.Bungee.PublicMessages",
        new String[] {
          "Do you want to enable public messaging over bungeecord",
          "Player needs to have "
              + PermissionsManager.CMIPerm.bungee_publicmessages_$1.getPermission("[serverName]")
              + " permission node to be able to send messages to target server"
        });
    this.BungeePublicMessages =
        configReader.get(
            "Chat.Bungee.PublicMessages",
            configReader.getC().getBoolean("Chat.BungeePublicMessages", true));
    configReader.addComment(
        "Chat.Bungee.PublicMessagesSingleIgnore",
        new String[] {
          "When enabled public messages addressed to single player will be ignored and not sent"
              + " across networkd",
          "This only applies when we have more than one player online and chat range is set to be"
              + " global"
        });
    this.BungeePublicMessagesSingleIgnore =
        configReader.get("Chat.Bungee.PublicMessagesSingleIgnore", false);
    configReader.addComment(
        "Chat.Bungee.StaffMessages",
        new String[] {"Do you want to enable staff messaging over bungeecord"});
    this.BungeeStaffMessages =
        configReader.get(
            "Chat.Bungee.StaffMessages",
            configReader.getC().getBoolean("Chat.BungeeStaffMessages", true));
    configReader.addComment(
        "Chat.GeneralFormat",
        new String[] {
          "Used for simple chat messages. Optional variables: {displayName} {world} {prefix}"
              + " {suffix} {group} {shout} {shoutcolor} {message} {nicknameprefix}. Supporting"
              + " PlaceHolderAPI variables like %player_server% %cmi_user_chatcolor%",
          "ATTENTION! Don't use gradient colors for {message} variable, if you want to apply"
              + " gradient for it, utilize GeneralMessageFormat section"
        });
    this.ChatGeneralFormat =
        configReader.get("Chat.GeneralFormat", "{prefix}&f{displayName}&7: &r{message}");
    if (!CMIChatColor.processGradient(this.ChatGeneralFormat).contains("{message}")) {
      CMIMessages.consoleMessage(
          "&4Incorrect Chat.GeneralFormat format! &2You cant use gradient here for {message}, check"
              + "  &7GeneralMessageFormat &2section");
    }

    configReader.addComment(
        "Chat.GeneralMessageFormat",
        new String[] {
          "Will define message format itself, this allows to have gradients in messages",
          "It NEEDS to contain {message} otherwise we will ignore this setup",
          "For 1.16+ servers you can use color gradients like '{#b3a28f>}{message}{#d7b8e6<}'",
          "You can have more than 2 colors in gradient. To define it repeat {message} variable. For"
              + " example '{#b3a28f>}{message}{#5c6999<>}{message}{#d7b8e6<}'",
          "Use %cmi_user_chatcolor% to add players choosen chat color"
        });
    this.ChatMessageGeneralFormat = configReader.get("Chat.GeneralMessageFormat", "{message}");
    if (!this.ChatMessageGeneralFormat.contains("{message}")) {
      this.ChatMessageGeneralFormat = "{message}";
    }

    configReader.addComment(
        "Chat.Ranged.General",
        new String[] {
          "Defines range of regular messages to travel", "Set to -1 to disable range restriction"
        });
    this.ChatGeneralRange =
        configReader.get(
            "Chat.Ranged.General", configReader.getC().getInt("Chat.GeneralRange", -1));
    configReader.addComment(
        "Chat.Ranged.Shout",
        new String[] {
          "Defines range of shout messages to travel",
          "Shout messages should start with ! and player should have "
              + PermissionsManager.CMIPerm.chat_shout.getPermissionForShow()
              + " permission",
          "GeneralRange should be enabled",
          "set to 0 to shout across all worlds, -1 to disable"
        });
    this.ChatShoutRange =
        configReader.get("Chat.Ranged.Shout", configReader.getC().getInt("Chat.ShoutRange", 200));
    configReader.addComment(
        "Chat.Ranged.ShoutColor",
        new String[] {"Color code to be used as base for the shout chat message"});
    this.ChatShoutColor = configReader.get("Chat.Ranged.ShoutColor", "");
    configReader.addComment(
        "Chat.Ranged.ShoutCost", new String[] {"Defines cost for each shout message"});
    this.ChatShoutCost =
        configReader.get("Chat.Ranged.ShoutCost", configReader.getC().getInt("Chat.ShoutCost", 0));
    configReader.addComment(
        "Chat.Ranged.Dynamic",
        new String[] {"Enables use of dynamic message range dependent on online player amount"});
    Boolean dynamicRangeEnabled = configReader.get("Chat.Ranged.Dynamic.Use", false);
    configReader.addComment(
        "Chat.Ranged.Dynamic",
        new String[] {
          "List of message range limits",
          "Player count fallowed by range",
          "Player amount defines from how many we should use this range"
        });
    var rangeLimits =
        configReader.get(
            "Chat.Ranged.Dynamic.Limits",
            Arrays.asList("1-0", "10-1000", "20-500", "30-400", "40-300", "50-200"));
    chatDynamicRange.clear();
    if (dynamicRangeEnabled) {
      for (String rangeLimit : rangeLimits) {
        if (rangeLimit.contains("-")) {
          String[] strings = rangeLimit.split("-", 2);

          try {
            chatDynamicRange.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
          } catch (Throwable throwable) {
            CMIMessages.consoleMessage(
                "&cFailed to parse " + rangeLimit + " for dynamic chat ranges");
          }
        }
      }
    }

    configReader.addComment(
        "Chat.ChatRoomShout",
        new String[] {
          "Prefix used to indicate that message should be sent to public chat instead of current"
              + " players chat room",
          "Set it to empty field if you want this feature to be disabled"
        });
    ChatRoomShout = configReader.get("Chat.ChatRoomShout", "!");
    configReader.addComment(
        "Chat.ChatRoomLife",
        new String[] {
          "Time in seconds you want to keep chat rooms alive before removing them",
          "This only applies to empty rooms after last user leaves it"
        });
    ChatRoomLife = configReader.get("Chat.ChatRoomLife", 3600);
    configReader.addComment(
        "Chat.ClickSuggestions",
        new String[] {
          "Defines suggested commands when you click on public, private and similar messages."
              + " [playerName], [playerDisplayName] and [playerNickName]  can be used to include"
              + " players name"
        });
    ChatFormatManager.chatClickAction[] chatClickActions;
    int value = (chatClickActions = ChatFormatManager.chatClickAction.values()).length;

    for (int clickIndex = 0; clickIndex < value; clickIndex++) {
      ChatFormatManager.chatClickAction clickAction = chatClickActions[clickIndex];
      clickAction.setSuggestion(
          configReader.get(
              "Chat.ClickSuggestions." + clickAction.name(),
              clickAction.getSuggestion(null, null, null)));
    }

    configReader.addComment(
        "Chat.ClickCommands",
        new String[] {
          "Defines command (one command per type) to be performed by player when clicked on players"
              + " name",
          "When this is defined then ClickSuggestions will be changed to shift+left click action"
              + " for suggested command",
          "Command is performed as player, if you want to perform it from console then add"
              + " asConsole! variable at the start of it"
        });
    value = (chatClickActions = ChatFormatManager.chatClickAction.values()).length;

    for (int commandIndex = 0; commandIndex < value; commandIndex++) {
      ChatFormatManager.chatClickAction commandAction = chatClickActions[commandIndex];
      commandAction.setCommand(configReader.get("Chat.ClickCommands." + commandAction.name(), ""));
    }

    configReader.addComment(
        "Chat.GroupFormat",
        new String[] {
          "Use numeric increments to separate groups from each other. If player has more than one,"
              + " then line with higher number will be used",
          "Add as many lines as you need too",
          "cmi.chatgroup.[id] permnission node to use",
          "Permission example: cmi.chatgroup.2",
          "",
          "ATTENTION! Don't use gradient colors for {message} variable, if you want to apply"
              + " gradient for it, utilize GroupMessageFormat section"
        });
    if (!configReader.getC().isConfigurationSection("Chat.GroupFormat")) {
      this.groupFormats.put(
          1, configReader.get("Chat.GroupFormat.1", "{prefix}&f{displayName}&f: &r{message}"));
      this.groupFormats.put(
          2, configReader.get("Chat.GroupFormat.2", "{prefix}&f{displayName}&7: &r{message}"));
      this.groupFormats.put(
          3, configReader.get("Chat.GroupFormat.3", "{prefix}&f{displayName}&8: &r{message}"));
    } else {
      try {
        if (configReader.getC().isConfigurationSection("Chat.GroupFormat")) {
          for (String groupKey :
              configReader.getC().getConfigurationSection("Chat.GroupFormat").getKeys(false)) {

            int groupId = 0;
            try {
              groupId = Integer.parseInt(groupKey);
            } catch (Exception exception) {
              continue;
            }

            String groupFormat =
                configReader.get(
                    "Chat.GroupFormat." + groupKey, "{prefix}&f{displayName}&f: &r{message}");
            if (!CMIChatColor.processGradient(groupFormat).contains("{message}")) {
              CMIMessages.consoleMessage(
                  "&4Incorrect Chat.GroupFormat."
                      + groupKey
                      + " format! &2You cant use gradient here for {message}, check"
                      + " &7Chat.GroupMessageFormat &2section");
            }

            this.groupFormats.put(groupId, groupFormat);
          }
        }
      } catch (Exception exception2) {
      }
    }

    configReader.addComment(
        "Chat.GroupMessageFormat",
        new String[] {
          "Use numeric increments to separate groups from each other. If player has more than one,"
              + " then line with higher number will be used",
          "Add as many lines as you need too",
          "cmi.chatmessagegroup.[id] permnission node to use",
          "Permission example: cmi.chatmessagegroup.2"
        });
    if (!configReader.getC().isConfigurationSection("Chat.GroupMessageFormat")) {
      this.groupMessageFormats.put(1, configReader.get("Chat.GroupMessageFormat.1", "{message}"));
      this.groupMessageFormats.put(2, configReader.get("Chat.GroupMessageFormat.2", "{message}"));
      this.groupMessageFormats.put(3, configReader.get("Chat.GroupMessageFormat.3", "{message}"));
    } else {
      try {
        if (configReader.getC().isConfigurationSection("Chat.GroupMessageFormat")) {
          for (String groupMessageKey :
              configReader
                  .getC()
                  .getConfigurationSection("Chat.GroupMessageFormat")
                  .getKeys(false)) {

            int messageGroupId = 0;
            try {
              messageGroupId = Integer.parseInt(groupMessageKey);
            } catch (Exception exception3) {
              continue;
            }

            this.groupMessageFormats.put(
                messageGroupId,
                configReader.get("Chat.GroupMessageFormat." + groupMessageKey, "{message}"));
          }
        }
      } catch (Exception exception4) {
      }
    }
  }

  private String getChatGeneralFormat() {
    return this.ChatGeneralFormat;
  }

  public String getNickNamePrefix(CMIUser cmiUser) {
    String string =
        cmiUser.getNickName() == null
            ? ""
            : CMI.getInstance().getNickNameManager().getNickNamePrefix();
    if (!CMI.getInstance().getNickNameManager().isPrefixWhenDifferent()) {
      return string;
    }

    if (cmiUser.getNickName() != null
        && cmiUser.getName(false) != null
        && CMIChatColor.stripColor(cmiUser.getNickName()).equalsIgnoreCase(cmiUser.getName())) {
      string = "";
    }

    return string;
  }

  public String getGroupFormat(Player player) {
    String string = this.getChatGeneralFormat();
    if (player == null) {
      return string;
    }

    PermissionInfo permissionInfo =
        this.plugin
            .getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.chatgroup_$1, 1000L);
    if (permissionInfo != null
        && permissionInfo.getMaxValue() != null
        && this.groupFormats.containsKey(permissionInfo.getMaxValue().intValue())) {
      string = this.groupFormats.get(permissionInfo.getMaxValue().intValue());
    }

    return string;
  }

  public String getGroupMessageFormat(Player player) {
    String string = this.ChatMessageGeneralFormat;
    if (player == null) {
      return string;
    }

    PermissionInfo permissionInfo =
        this.plugin
            .getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.chatmessagegroup_$1, 1000L);
    if (permissionInfo != null
        && permissionInfo.getMaxValue() != null
        && this.groupMessageFormats.containsKey(permissionInfo.getMaxValue().intValue())) {
      string = this.groupMessageFormats.get(permissionInfo.getMaxValue().intValue());
    }

    return string;
  }

  public Long getChatMutedUntil() {
    return this.chatMutedUntil;
  }

  public void setChatMutedUntil(Long mutedUntil) {
    this.chatMutedUntil = mutedUntil;
  }

  public void broadcastBungeeMessage(String string, String message) {
    HashSet<Player> hashSet = new HashSet();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (cmiUser == null || !CMIUser.getUser(player).isIgnoring(cmiUser.getUniqueId())) {
        hashSet.add(player);
      }
    }

    if (this.plugin.getTagManager().isChatTagEnabled()) {
      message = this.plugin.getTagManager().tag(cmiUser, message, "", true);
    }

    if (message.startsWith("[") && message.endsWith("]")) {
      if (message.length() <= 2) {
        return;
      }

      RawMessage rawMessage = new RawMessage();
      rawMessage.setCombined(message);
      rawMessage.show(hashSet, false);
    } else {
      for (Player recipient : hashSet) {
        recipient.sendMessage(message);
      }
    }
  }

  public void sendMessage(String string, String targetName, String message) {
    Object object = null;
    if (string.equalsIgnoreCase("console")) {
      object = Bukkit.getConsoleSender();
    } else {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
      if (cmiUser != null) {
        object = cmiUser.getPlayer(false);
      }
    }

    if (object == null) {
      object = CMIPlayer.getByName(string);
    }

    CMIUser targetUser = this.plugin.getPlayerManager().getUser(targetName);
    Player player = null;
    if (targetUser != null) {
      player = targetUser.getPlayer(false);
    } else {
      player = CMIUser.getOnlinePlayer(targetName);
    }

    this.sendMessage((CommandSender) object, string, player, targetName, message, true);
  }

  public void sendMessage(CommandSender commandSender, Player player, String string) {
    this.sendMessage(
        commandSender,
        commandSender != null ? commandSender.getName() : null,
        player,
        player != null ? player.getName() : null,
        string,
        true);
  }

  public void sendMessage(CommandSender commandSender, Player player, String string, boolean flag) {
    this.sendMessage(
        commandSender,
        commandSender != null ? commandSender.getName() : null,
        player,
        player != null ? player.getName() : null,
        string,
        flag);
  }

  public void sendMessage(
      CommandSender commandSender,
      String string,
      Player player,
      String targetName,
      String message) {
    this.sendMessage(commandSender, string, player, targetName, message, true);
  }

  public void sendMessage(
      CommandSender commandSender,
      String string,
      Player player,
      String targetName,
      String message,
      boolean flag) {
    if (message.length() > 1000) {
      message = message.substring(0, 1000);
      CMIMessages.consoleMessage(
          "Player ("
              + (commandSender != null ? commandSender.getName() : "Console")
              + ") tried to send way to long message with msg command. Message was cut to 1000"
              + " characters.");
    }

    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser == null) {
      cmiUser = this.plugin.getPlayerManager().getUser(targetName);
    }

    String senderName = "Console";
    if (player != null) {
      senderName = player.getName();
    } else if (targetName != null) {
      senderName = targetName;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser).setTargetName(targetName);
    if (player != null) {
      snd.setTarget(player);
    }

    if (cmiUser != null && cmiUser.isAfk()) {
      if (cmiUser.isOnline()
          && cmiUser.hasSetPermission(PermissionsManager.CMIPerm.command_afk_staffinform)) {
        String afkMessage = CMILC.afk_MayNotRespondStaff.getLocale(snd);
        afkMessage =
            this.plugin
                .getPlaceholderAPIManager()
                .updatePlaceHolders(cmiUser.getPlayer(false), afkMessage);
        CMILC.sendMessage(commandSender, afkMessage);
      } else {
        String afkMessage = CMILC.afk_MayNotRespond.getLocale(snd);
        afkMessage =
            this.plugin
                .getPlaceholderAPIManager()
                .updatePlaceHolders(cmiUser.getPlayer(false), afkMessage);
        CMILC.sendMessage(commandSender, afkMessage);
      }
    }

    CMIUser senderUser = null;
    if (commandSender instanceof Player) {
      senderUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
    }

    if (senderUser == null && string != null) {
      senderUser = this.plugin.getPlayerManager().getUser(string);
    }

    if (senderUser != null && cmiUser != null) {
      if (cmiUser.isIgnoring(senderUser.getUniqueId())
          && !PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(commandSender)) {
        CMILC.info("ignore", commandSender, "ignoringYou", snd);
        return;
      }

      if (cmiUser.isSilenceMode()
          && !PermissionsManager.CMIPerm.command_silence_bypass.hasPermission(commandSender)) {
        CMILC.info("silence", commandSender, "inMode", snd);
        return;
      }
    }

    snd.setSender(commandSender).setTarget(player).setSenderName(string).setTargetName(targetName);
    if (senderName.equalsIgnoreCase("Console")) {
      snd.setTarget(Bukkit.getConsoleSender());
      snd.setTargetName(senderName);
    }

    boolean replyAllowed = commandSender instanceof Player;
    if (!this.plugin.getChatFilterManager().getEmojiReplacerMap().isEmpty()) {
      String emojiMessage = this.plugin.getChatFilterManager().replaceEmoji(message);
      if (emojiMessage != null) {
        message = emojiMessage;
      }
    }

    if (player != null && flag) {
      if (!(commandSender instanceof Player)) {
        message = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, message);
      }

      if (message.startsWith("!")
          && PermissionsManager.CMIPerm.command_msg_clean.hasPermission(commandSender)) {
        message = CMIChatColor.translate(message);
        message = message.replace("＆", "&");
        if (message.startsWith("!-")) {
          message = "!" + message.substring(2);
          if (PermissionsManager.CMIPerm.command_msg_noreply.hasPermission(commandSender)) {
            replyAllowed = false;
          }
        }

        String cleanMessage = message.substring(1, message.length());
        if (commandSender != null && !(commandSender instanceof Player)) {
          player.sendMessage(cleanMessage);
        } else {
          player.sendMessage(message.substring(1, message.length()).replace("\n", "\\n"));
        }
      } else if (commandSender != null
          && commandSender instanceof Player
          && this.plugin.getChatManager().isChatClickHoverMessages()) {
        RawMessage rawMessage = new RawMessage();
        String hoverText = CMILC.Chat_privateHover.getLocale();
        hoverText =
            this.plugin
                .getPlaceholderAPIManager()
                .updatePlaceHolders((Player) commandSender, hoverText);
        CMIUser senderClickUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
        if (senderClickUser == null) {
          return;
        }

        message = message.replace("\n", "");
        rawMessage.setDontBreakLine(false);
        if (this.containsUrl(message)) {
          rawMessage
              .addText(
                  this.plugin
                      .getPlaceholderAPIManager()
                      .updatePlaceHolders(
                          (Player) commandSender,
                          CMILC.getIM("msg", "TargetMsg", snd, "[message]", "")))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.privmsg.updateClickAction(rawMessage, senderClickUser);
          this.convertHoverOver(
              rawMessage, message, commandSender instanceof Player ? (Player) commandSender : null);
        } else if (!(commandSender instanceof Player)) {
          rawMessage
              .addText(
                  this.plugin
                      .getPlaceholderAPIManager()
                      .updatePlaceHolders(
                          (Player) commandSender,
                          CMILC.getIM("msg", "TargetMsg", snd, "[message]", message)))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.privmsg.updateClickAction(rawMessage, senderClickUser);
        } else {
          rawMessage
              .addText(CMILC.getIM("msg", "TargetMsg", snd, "[message]", message))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.privmsg.updateClickAction(rawMessage, senderClickUser);
        }

        rawMessage.show(player);
      } else {
        String targetText =
            CMILC.getIM("msg", "TargetMsg", snd, "[message]", message).replace("＆", "&");
        if (commandSender != null && !(commandSender instanceof Player)) {
          player.sendMessage(targetText);
        } else {
          player.sendMessage(targetText.replace("\n", "\\n"));
        }
      }

      if (cmiUser != null && cmiUser.getOptionState(PlayerOption.pmSound)) {
        CMISoundPreset.PrivateMessage.play(player);
      }
    } else {
      if (snd.getTargetName() == null) {
        snd.setTarget(Bukkit.getConsoleSender());
      }

      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(),
          CMILC.getIM("msg", "TargetMsg", snd, "[message]", message).replace("＆", "&"));
    }

    if (commandSender instanceof Player
        && this.plugin.getChatManager().isChatClickHoverMessages()) {
      RawMessage senderRaw = new RawMessage();
      String senderHover = CMILC.Chat_privateHover.getLocale();
      senderHover =
          this.plugin
              .getPlaceholderAPIManager()
              .updatePlaceHolders((Player) commandSender, senderHover);
      message = message.replace("\n", "");
      senderRaw.setDontBreakLine(false);
      if (this.containsUrl(message)) {
        senderRaw
            .addText(
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(
                        player, CMILC.getIM("msg", "SenderMsg", snd, "[message]", "")))
            .addHover(senderHover);
        ChatFormatManager.chatClickAction.privmsg.updateClickAction(
            senderRaw,
            cmiUser == null ? senderName : cmiUser.getName(),
            cmiUser == null ? senderName : cmiUser.getCleanDisplayName(),
            cmiUser == null ? senderName : cmiUser.getNickName());
        this.convertHoverOver(senderRaw, message, (Player) commandSender);
      } else if (!(commandSender instanceof Player)) {
        senderRaw
            .addText(
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(
                        player, CMILC.getIM("msg", "SenderMsg", snd, "[message]", message)))
            .addHover(senderHover);
        ChatFormatManager.chatClickAction.privmsg.updateClickAction(
            senderRaw,
            cmiUser == null ? senderName : cmiUser.getName(),
            cmiUser == null ? senderName : cmiUser.getCleanDisplayName(),
            cmiUser == null ? senderName : cmiUser.getNickName());
      } else {
        senderRaw
            .addText(CMILC.getIM("msg", "SenderMsg", snd, "[message]", message))
            .addHover(senderHover);
        ChatFormatManager.chatClickAction.privmsg.updateClickAction(
            senderRaw,
            cmiUser == null ? senderName : cmiUser.getName(),
            cmiUser == null ? senderName : cmiUser.getCleanDisplayName(),
            cmiUser == null ? senderName : cmiUser.getNickName());
      }

      senderRaw.show(commandSender);
    } else if (commandSender != null) {
      String senderText =
          CMILC.getIM("msg", "SenderMsg", snd, "[message]", message).replace("＆", "&");
      if (!(commandSender instanceof Player)) {
        commandSender.sendMessage(senderText);
      } else {
        commandSender.sendMessage(senderText.replace("\n", "\\n"));
      }
    }

    if (commandSender == null
        || commandSender instanceof Player
            && !PermissionsManager.CMIPerm.command_socialspy_hide.hasPermission(commandSender)) {
      this.plugin.getPlayerManager().sendMessageToSpies(commandSender, string, player, message);
    }

    if (replyAllowed || commandSender == null) {
      if (commandSender != null) {
        this.plugin.getUtilManager().addMessageReplyTo(senderName, commandSender.getName());
      } else if (string != null) {
        this.plugin.getUtilManager().addMessageReplyTo(senderName, string);
      }
    }

    if (commandSender instanceof Player && this.plugin.getAfkManager().isDisableOnPrivateChat()) {
      this.plugin
          .getPlayerManager()
          .getUser((Player) commandSender)
          .setAfk(false, CMIAfkEnterEvent.AfkType.manual);
    }
  }

  public String updateUrl(String string) {
    if (!this.containsUrl(string)) {
      return string;
    }

    Matcher matcher = this.rule.getMatcher(string);

    while (matcher != null && matcher.find()) {
      String url = matcher.group(0);
      String replacedUrl = url.replace("&", "＆");
      string = string.replace(url, replacedUrl);
    }

    return string;
  }

  public boolean containsUrl(String string) {
    Matcher matcher = this.rule.getMatcher(string);
    boolean flag = false;
    if (matcher != null) {
      flag = matcher.find();
    }

    Matcher itemMatcher = this.itemRule.getMatcher(string);
    boolean itemFound = false;
    if (itemMatcher != null) {
      itemFound = itemMatcher.find();
    }

    return flag || itemFound;
  }

  public RawMessage convertHoverOver(RawMessage rawMessage, String string, Player player) {
    return this.convertHoverOver(rawMessage, string, player, false);
  }

  public RawMessage convertHoverOver(
      RawMessage rawMessage, String string, Player player, boolean flag) {
    String originalText = string;
    Matcher matcher = this.rule.getMatcher(string);
    Matcher itemMatcher = this.itemRule.getMatcher(string);
    String urlMatch = null;
    if (matcher != null && matcher.find()) {
      urlMatch = matcher.group();
    }

    String itemMatch = null;
    if (itemMatcher != null && itemMatcher.find()) {
      itemMatch = itemMatcher.group();
    }

    Boolean urlBeforeItem = null;
    int value = 0;
    if (urlMatch != null) {
      if (urlMatch.startsWith(" ")) {
        urlMatch = urlMatch.substring(1, urlMatch.length());
      }

      String[] strings = string.split(Pattern.quote(urlMatch), 2);
      urlBeforeItem = true;
      value = strings[0].length();
    }

    if (itemMatch != null) {
      String[] itemParts = string.split(itemMatch, 2);
      if (itemParts.length == 1) {
        urlBeforeItem = false;
      } else if (itemParts[0].length() < value) {
        urlBeforeItem = false;
      } else if (urlBeforeItem != null) {
        urlBeforeItem = true;
      }
    }

    if (urlMatch != null && urlBeforeItem != null && urlBeforeItem) {
      String urlText = urlMatch;
      String urlHover = urlText;
      String[] urlParts = string.split(Pattern.quote(urlText));
      if (urlParts.length > 1) {
        string = urlParts[1];
      } else {
        string = "";
      }

      if (urlParts.length > 0) {
        rawMessage.addText(ChatFormatListener.processGradientChat(player, urlParts[0]));
      }

      rawMessage.freezeFormat();
      rawMessage
          .addText(
              !CMILC.Chat_link.getLocale().isEmpty() && this.ChatTranslateLink
                  ? CMILC.Chat_link.getLocale()
                  : Matcher.quoteReplacement(urlText))
          .addHover(urlHover)
          .addUrl(urlHover);
      rawMessage.unFreezeFormat();
    } else if (this.hoverItems && itemMatch != null) {
      ItemStack itemStack = null;

      try {
        if (player != null) {
          itemStack = this.plugin.getPlayerManager().getUser(player).getItemInHand();
        }
      } catch (Exception exception) {
      }

      if (itemStack != null) {
        String itemText = itemMatch;
        String[] itemTextParts = string.split(Pattern.quote(itemText), 2);
        if (itemTextParts.length > 1) {
          string = itemTextParts[1];
        } else {
          string = "";
        }

        if (itemTextParts.length > 0) {
          rawMessage.addText(ChatFormatListener.processGradientChat(player, itemTextParts[0]));
        }

        itemStack = itemStack.clone();
        CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
        String amountText = "";
        if (itemStack.getType().equals(Material.AIR)) {
          itemText = CMILC.Chat_itemEmpty.getLocale();
        } else {
          if (cmiItemStack.getAmount() > 1) {
            amountText = CMILC.Chat_itemAmount.getLocale("[amount]", cmiItemStack.getAmount());
          }

          itemText =
              CMILC.Chat_item.getLocale(
                  "[itemName]", cmiItemStack.getRealName(), "[amount]", amountText);
        }

        itemText =
            CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, itemText);
        if (itemText != null) {
          itemText = itemText.replace("&", "＆");
        }

        if (!flag) {
          rawMessage.freezeFormat();
          if (itemStack.getType().equals(Material.AIR)) {
            rawMessage.addText(itemText);
          } else {
            rawMessage.addItem(itemText, itemStack, null, null, null);
          }

          rawMessage.unFreezeFormat();
          flag = true;
        } else {
          rawMessage.addText(itemMatch);
        }
      }
    }

    if (!originalText.equals(string)) {
      this.convertHoverOver(rawMessage, string, player, flag);
    } else {
      rawMessage.addText(ChatFormatListener.processGradientChat(player, string));
    }

    return rawMessage;
  }

  public StringBuilder convertHoverItem(
      StringBuilder stringBuilder, String string, Player player, boolean flag) {
    String originalText = string;
    Matcher matcher = this.itemRule.getMatcher(string);
    String itemMatch = null;
    if (matcher != null && matcher.find()) {
      itemMatch = matcher.group();
    }

    if (this.hoverItems && itemMatch != null) {
      ItemStack itemStack = null;

      try {
        if (player != null) {
          itemStack = this.plugin.getPlayerManager().getUser(player).getItemInHand();
        }
      } catch (Exception exception) {
      }

      if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
        String itemText = itemMatch;
        String[] strings = string.split(Pattern.quote(itemText), 2);
        if (strings.length > 1) {
          string = strings[1];
        } else {
          string = "";
        }

        if (strings.length > 0) {
          stringBuilder.append(ChatFormatListener.processGradientChat(player, strings[0]));
        }

        itemStack = itemStack.clone();
        CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
        String amountText = "";
        if (itemStack.getType().equals(Material.AIR)) {
          itemText = CMILC.Chat_itemEmpty.getLocale();
        } else {
          if (cmiItemStack.getAmount() > 1) {
            amountText = CMILC.Chat_itemAmount.getLocale("[amount]", cmiItemStack.getAmount());
          }

          itemText =
              CMILC.Chat_item.getLocale(
                  "[itemName]", cmiItemStack.getRealName(), "[amount]", amountText);
        }

        itemText =
            CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, itemText);
        if (itemText != null) {
          itemText = itemText.replace("&", "＆");
        }

        if (!flag) {
          stringBuilder.append(itemText);
          flag = true;
        } else {
          stringBuilder.append(itemMatch);
        }
      }
    }

    if (!originalText.equals(string)) {
      this.convertHoverItem(stringBuilder, string, player, flag);
    } else {
      stringBuilder.append(ChatFormatListener.processGradientChat(player, string));
    }

    return stringBuilder;
  }

  public boolean isBungeeMessages() {
    return this.BungeeMessages;
  }

  public void setChatGeneralRange(int value) {
    this.ChatGeneralRange = value;
  }

  public int getChatShoutRange() {
    return this.ChatShoutRange;
  }

  public void setChatShoutRange(int value) {
    this.ChatShoutRange = value;
  }

  public Integer getChatShoutCost() {
    return this.ChatShoutCost;
  }

  public void setChatShoutCost(int value) {
    this.ChatShoutCost = value;
  }

  public Set<UUID> getStaffChats() {
    return this.staffChats;
  }

  public boolean isBungeePublicMessages() {
    return this.BungeePublicMessages;
  }

  public boolean isBungeePublicMessagesSingleIgnore() {
    return this.BungeePublicMessagesSingleIgnore;
  }

  public boolean isBungeeStaffMessages() {
    return this.BungeeStaffMessages;
  }

  public HashMap<String, CMIChatRoom> getChatRooms() {
    var iterator = this.chatRooms.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry entry = (Entry) iterator.next();
      CMIChatRoom cmiChatRoom = (CMIChatRoom) entry.getValue();
      if (!cmiChatRoom.isPersistent()
          && cmiChatRoom.getUsers().isEmpty()
          && cmiChatRoom.getKeepAliveUntil() != 0L
          && cmiChatRoom.getKeepAliveUntil() <= System.currentTimeMillis()) {
        iterator.remove();
      }
    }

    return this.chatRooms;
  }

  public CMIChatRoom getChatRoom(String string) {
    return string == null ? null : this.getChatRooms().get(string.toLowerCase());
  }

  public boolean joinChatRoom(CMIUser cmiUser, String string) {
    CMIChatRoom cmiChatRoom = this.getChatRooms().get(string.toLowerCase());
    if (cmiChatRoom == null) {
      return false;
    }

    cmiChatRoom.addUser(cmiUser);
    cmiChatRoom.removeWatcher(cmiUser);
    this.setChatRoom(cmiUser, cmiChatRoom);
    return true;
  }

  @Deprecated
  public boolean createChatRoom(CMIUser cmiUser, String string, boolean flag) {
    return this.createChatRoom(cmiUser, string, flag, false, false);
  }

  public boolean createChatRoom(
      CMIUser cmiUser, String string, boolean flag, boolean locked, boolean persistent) {
    CMIChatRoom cmiChatRoom = this.getChatRooms().get(string.toLowerCase());
    if (cmiChatRoom != null) {
      return false;
    }

    cmiChatRoom = new CMIChatRoom(string);
    cmiChatRoom.setOwner(cmiUser.getUniqueId());
    cmiChatRoom.setPrivate(flag);
    cmiChatRoom.setLocked(locked);
    cmiChatRoom.setPersistent(persistent);
    this.chatRooms.put(string.toLowerCase(), cmiChatRoom);
    this.setChatRoom(cmiUser, cmiChatRoom);
    cmiChatRoom.addUser(cmiUser);
    if (persistent) {
      this.save();
    }

    return true;
  }

  public void load() {
    long longValue = System.currentTimeMillis();
    this.plugin.safeRenameFile("chatRooms.yml", "Saves" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      for (String string : configReader.getC().getKeys(false)) {
        CMIChatRoom cmiChatRoom = new CMIChatRoom(string);
        cmiChatRoom.setPersistent(true);
        if (configReader.getC().isString(string + ".owner")) {
          cmiChatRoom.setOwner(UUID.fromString(configReader.getC().getString(string + ".owner")));
        }

        if (configReader.getC().isBoolean(string + ".locked")) {
          cmiChatRoom.setLocked(configReader.getC().getBoolean(string + ".locked"));
        }

        if (configReader.getC().isBoolean(string + ".private")) {
          cmiChatRoom.setPrivate(configReader.getC().getBoolean(string + ".private"));
        }

        if (configReader.getC().isList(string + ".invitations")) {
          for (Object object : configReader.getC().getList(string + ".invitations")) {
            cmiChatRoom.addInvitation((UUID) object);
          }
        }

        this.chatRooms.put(string.toLowerCase(), cmiChatRoom);
      }

      if (!this.chatRooms.isEmpty()) {
        this.plugin.loadMessage(
            this.chatRooms.size(), "chat rooms", System.currentTimeMillis() - longValue);
      }
    }
  }

  public void save() {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      for (CMIChatRoom cmiChatRoom : this.chatRooms.values()) {
        if (cmiChatRoom.isPersistent()) {
          configReader.get(cmiChatRoom.getChatName() + ".owner", cmiChatRoom.getOwner().toString());
          if (cmiChatRoom.isLocked()) {
            configReader.get(cmiChatRoom.getChatName() + ".locked", cmiChatRoom.isLocked());
          }

          if (cmiChatRoom.isPrivate()) {
            configReader.get(cmiChatRoom.getChatName() + ".private", cmiChatRoom.isPrivate());
          }

          if (cmiChatRoom.getInvitations() != null && !cmiChatRoom.getInvitations().isEmpty()) {
            configReader.get(
                cmiChatRoom.getChatName() + ".invitations", cmiChatRoom.getInvitations());
          }
        }
      }

      configReader.save();
    }
  }

  public CMIChatRoom getChatRoom(UUID uuid) {
    return this.userChatRoom.get(uuid);
  }

  public boolean leaveChatRoom(UUID uuid) {
    return this.getChatRoom(uuid) == null
        ? false
        : this.getChatRoom(uuid).removeUser(CMIUser.getUser(uuid));
  }

  public void setChatRoom(CMIUser cmiUser, CMIChatRoom cmiChatRoom) {
    if (cmiChatRoom != null && !cmiChatRoom.getUsers().contains(cmiUser)) {
      cmiChatRoom.getUsers().add(cmiUser);
    }

    this.userChatRoom.put(cmiUser.getUniqueId(), cmiChatRoom);
  }

  public void leaveChatRoom(CMIUser cmiUser) {
    this.leaveChatRoom(cmiUser.getUniqueId());
  }

  public String getChatMutedReason() {
    return this.chatMutedReason;
  }

  public void setChatMutedReason(String string) {
    if (string != null && !string.isEmpty()) {
      this.chatMutedReason = string;
    } else {
      Object object = null;
    }
  }

  public int getChatGeneralRange() {
    return this.getChatGeneralRange(null);
  }

  public int getChatGeneralRange(World world) {
    if (chatDynamicRange.isEmpty()) {
      return this.ChatGeneralRange;
    }

    int value = this.plugin.getUtilManager().getOnlinePlayerCount(null);
    int matchedRange = -2;
    Integer integer = chatDynamicRange.get(value);
    if (integer != null) {
      return integer;
    }

    for (var entry : chatDynamicRange.entrySet()) {
      if (matchedRange != -2 && (Integer) entry.getKey() > value) {
        break;
      }

      matchedRange = (Integer) entry.getValue();
    }

    if (matchedRange == -2) {
      return this.ChatGeneralRange;
    }

    chatDynamicRange.put(value, matchedRange);
    return matchedRange;
  }

  public String getChatShoutColor() {
    return this.ChatShoutColor;
  }

  public enum chatClickAction {
    pubmsg,
    privmsg,
    staffmsg,
    helpop,
    chatroom,
    discord;

    private String sug = "/msg [playerNickName] ";
    private String cmd = "";
    private boolean specialized = false;

    public void updateClickAction(RawMessage rawMessage, CMIUser cmiUser) {
      if (!this.cmd.isEmpty()) {
        if (!this.sug.isEmpty()) {
          rawMessage.addInsertion(this.getSuggestion(cmiUser));
        }

        if (this.specialized) {
          final var list = Arrays.asList(this.getCommand(cmiUser));
          RawMessageCommand rawMessageCommand =
              new RawMessageCommand() {
                public void run(CommandSender commandSender) {
                  CMI.getInstance()
                      .getSpecializedCommandManager()
                      .processCmds(list, (Player) commandSender);
                }
              };
          rawMessageCommand.setKeep(true);
          rawMessage.addCommand(rawMessageCommand);
        } else {
          rawMessage.addCommand(this.getCommand(cmiUser));
        }
      } else if (!this.sug.isEmpty()) {
        rawMessage.addSuggestion(this.getSuggestion(cmiUser));
      }
    }

    public void updateClickAction(
        RawMessage rawMessage, String string, String displayName, String nickName) {
      if (!this.cmd.isEmpty()) {
        if (!this.sug.isEmpty()) {
          rawMessage.addInsertion(this.getSuggestion(string, displayName, nickName));
        }

        if (this.specialized) {
          final var list = Arrays.asList(this.getCommand(string, displayName, nickName));
          RawMessageCommand rawMessageCommand =
              new RawMessageCommand() {
                public void run(CommandSender commandSender) {
                  CMI.getInstance()
                      .getSpecializedCommandManager()
                      .processCmds(list, (Player) commandSender);
                }
              };
          rawMessageCommand.setKeep(true);
          rawMessage.addCommand(rawMessageCommand);
        } else {
          rawMessage.addCommand(this.getCommand(string, displayName, nickName));
        }
      } else if (!this.sug.isEmpty()) {
        rawMessage.addSuggestion(this.getSuggestion(string, displayName, nickName));
      }
    }

    public String getSuggestion(CMIUser cmiUser) {
      return cmiUser == null
          ? this.sug
          : this.getSuggestion(
              cmiUser.getName(false), cmiUser.getDisplayName(false), cmiUser.getNickName());
    }

    public String getSuggestion(String string, String displayName, String nickName) {
      return process(string, displayName, nickName, this.sug);
    }

    public void setSuggestion(String string) {
      this.sug = string;
    }

    public String getCommand() {
      return this.cmd;
    }

    public void setCommand(String string) {
      this.cmd = string;
      this.specialized =
          CMI.getInstance().getSpecializedCommandManager().isSpecializedCommand(string);
    }

    public String getCommand(CMIUser cmiUser) {
      return cmiUser == null
          ? this.sug
          : this.getCommand(
              cmiUser.getName(false), cmiUser.getDisplayName(false), cmiUser.getNickName());
    }

    private static String process(
        String string, String displayName, String nickName, String template) {
      String result = template;
      if (string != null) {
        result = result.replace("[playerName]", CMIChatColor.stripColor(string));
      }

      if (displayName != null) {
        result = result.replace("[playerDisplayName]", CMIChatColor.stripColor(displayName));
      } else if (string != null) {
        result = result.replace("[playerDisplayName]", CMIChatColor.stripColor(string));
      }

      if (nickName != null) {
        result = result.replace("[playerNickName]", CMIChatColor.stripColor(nickName));
      } else if (string != null) {
        result = result.replace("[playerNickName]", CMIChatColor.stripColor(string));
      }

      return result;
    }

    public String getCommand(String string, String displayName, String nickName) {
      return this.cmd.isEmpty() ? this.cmd : process(string, displayName, nickName, this.cmd);
    }
  }
}
