package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMIEventPriority;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.ChatColor;

public class ChatManager {
  private CMI plugin;
  private boolean ModifyChatFormat = false;
  private boolean ChatClickHoverMessages = false;
  private boolean ChatDiscordSRV = false;
  private String ChatDiscordSRVGlobalChannel = "global";
  private String DiscordSRVLabel = "";
  private String DiscordSRVUnlinkedLabel = "";
  private boolean DiscordSRVRanged = true;
  private boolean ChatDynMapChat = false;
  private boolean ChatReplyToLastMessenger = false;
  private int LastMessengerTimeOut = 120;
  private boolean PrivateMessagesGroups = false;
  private boolean ColorsPublicMessages = false;
  private CMIEventPriority ColorsPublicMessagesPriority = CMIEventPriority.LOW;
  private CMIEventPriority formatPublicMessagesPriority = CMIEventPriority.NORMAL;
  private boolean ColorsPrivateMessage = false;
  private boolean ChatIgnorePublicMessage = false;
  private boolean ColorsMe = false;
  private boolean ColorsNickName;
  private List<String> cleanUpWhiteList;
  private Set<CMIPlayerMessageColor> chatColors = new LinkedHashSet<>();
  private Map<UUID, CMIPlayerMessageColor> messageColorCache =
      Collections.synchronizedMap(new HashMap<>());
  public static String fileName = "Chat.yml";

  public void addToCache(UUID uuid, CMIPlayerMessageColor cmiPlayerMessageColor) {
    synchronized (this.messageColorCache) {
      this.messageColorCache.put(uuid, cmiPlayerMessageColor);
    }
  }

  public void removeFromCache(UUID uuid) {
    synchronized (this.messageColorCache) {
      this.messageColorCache.remove(uuid);
    }
  }

  public CMIPlayerMessageColor getColorFromCache(UUID uuid) {
    synchronized (this.messageColorCache) {
      return this.messageColorCache.get(uuid);
    }
  }

  public ChatManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean loadConfig(boolean flag) {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Settings" + File.separator + fileName);
    } catch (Exception exception) {
      if (flag) {
        this.plugin.consoleMessage(
            "&cSEVERE CONFIG FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in config file"
                + " reset");
        return false;
      }

      exception.printStackTrace();
    }

    if (configReader == null) {
      return false;
    }

    configReader.addComment(
        "Chat.ModifyChatFormat.Enabled",
        new String[] {
          "Will try to modify chat to display it in defined format",
          "If this is disabled and you have 3rd party plugin handling chat messages, consider"
              + " disabling ClickHoverMessages section too if you notice any problems with chat"
              + " formatting"
        });
    this.ModifyChatFormat =
        configReader.get(
            "Chat.ModifyChatFormat.Enabled",
            configReader.getC().getBoolean("Chat.ModifyChatFormat", false));
    configReader.addComment(
        "Chat.ModifyChatFormat.Priority",
        CMIEventPriority.generateComments(
            "Priority of public message format listener", CMIEventPriority.NORMAL, true));
    this.formatPublicMessagesPriority =
        CMIEventPriority.getPriority(configReader.get("Chat.ModifyChatFormat.Priority", "NORMAL"));
    configReader.addComment(
        "Chat.ModifyChatFormat.ClickHoverMessages",
        new String[] {
          "When set to true, regular and private messages (excludes clean messages) will have"
              + " additional information when hovering over it (PlaceHolderAPI supported) and can"
              + " be clicked for quick reply option",
          "To change default hover over messages seen on sent message, go to your locale file to"
              + " Chat section"
        });
    this.ChatClickHoverMessages =
        configReader.get(
            "Chat.ModifyChatFormat.ClickHoverMessages",
            configReader.getC().getBoolean("Chat.ClickHoverMessages", true));
    configReader.addComment(
        "Chat.DiscordSRV.Enabled", new String[] {"Enables support for DiscordSRV plugin"});
    this.ChatDiscordSRV =
        configReader.get(
            "Chat.DiscordSRV.Enabled",
            configReader.getC().getBoolean("Chat.DiscordSRVPublicChat", true));
    configReader.addComment(
        "Chat.DiscordSRV.GlobalChannel",
        new String[] {"Defines name of global chat channel in discordsrv"});
    this.ChatDiscordSRVGlobalChannel = configReader.get("Chat.DiscordSRV.GlobalChannel", "global");
    configReader.addComment(
        "Chat.DiscordSRV.Label",
        new String[] {
          "Indicator which can be used as {discord} in chat format to indicate that message came"
              + " from discord and not ingame"
        });
    this.DiscordSRVLabel =
        configReader.get(
            "Chat.DiscordSRV.Label",
            configReader.getC().getString("Chat.DiscordSRVLabel", "&2[&7D&2]"));
    configReader.addComment(
        "Chat.DiscordSRV.LabelUnlinked",
        new String[] {
          "Indicator which can be used as {discord} in chat format to indicate that message came"
              + " from unlinked discord account"
        });
    this.DiscordSRVUnlinkedLabel = configReader.get("Chat.DiscordSRV.UnlinkedLabel", "&4[&cD&4]");
    configReader.addComment(
        "Chat.DiscordSRV.RangedMessages",
        new String[] {
          "When enabled and you have ranged messages enabled, we will send all of them to"
              + " DiscordSRV",
          "When disabled, only shouts and messages sent by players with "
              + PermissionsManager.CMIPerm.chat_rangebypass.getPermission()
              + " permission node will be visible in discord"
        });
    this.DiscordSRVRanged = configReader.get("Chat.DiscordSRV.RangedMessages", true);
    configReader.addComment(
        "Chat.DynMapChat", new String[] {"Enables support for DynMap web chat"});
    this.ChatDynMapChat = configReader.get("Chat.DynMapChat", true);
    configReader.addComment(
        "Chat.ReplyToLastMessenger",
        new String[] {
          "When set to false, each time you will use /r you will reply to person you previously"
              + " sent message directly or to person who sent you message if there is none you have"
              + " conversion before",
          "When this set to true, players with /r will reply to person who last sent private"
              + " message. This can result in confusion when using /r while getting private"
              + " messages from multiple players"
        });
    this.ChatReplyToLastMessenger = configReader.get("Chat.ReplyToLastMessenger", false);
    configReader.addComment(
        "Chat.LastMessengerTimeOut",
        new String[] {
          "If ReplyToLastMessenger is set to false, then timeOut will be taken into consideration"
              + " to who you should reply",
          "If you had conversation in last 120 seconds (default) then even receiving message from"
              + " 3rd person, you will still reply to original player",
          "If you had conversation in longer then 120 seconds period, then you will reply to latest"
              + " person who send you a message"
        });
    this.LastMessengerTimeOut = configReader.get("Chat.LastMessengerTimeOut", 120);
    configReader.addComment(
        "Chat.PrivateMessagesGroups",
        new String[] {
          "When set to true players will need to have "
              + PermissionsManager.CMIPerm.command_msg_$1_send.getPermission("[groupName]")
              + " where [groupName] is receivers main permission group"
        });
    this.PrivateMessagesGroups = configReader.get("Chat.PrivateMessagesGroups", false);
    this.plugin.getChatFormatManager().loadConfig(configReader);
    String string = "";
    ChatColor[] chatColorValues;
    int value = (chatColorValues = ChatColor.values()).length;

    for (int index = 0; index < value; index++) {
      ChatColor chatColor = chatColorValues[index];
      if (!string.isEmpty()) {
        string = string + ", ";
      }

      string =
          string
              + chatColor.name().toLowerCase().replace("_", "")
              + "(&"
              + chatColor.getChar()
              + ")";
    }

    configReader.addComment(
        "Chat.Colors.PublicMessage.Enabled",
        new String[] {
          "If set to true then all public messages will be filtered from color codes and will allow"
              + " to colorize them with appropriate permission node",
          "cmi.colors.publicmessage.[colorName]",
          "Colors: " + string
        });
    this.ColorsPublicMessages =
        configReader.get(
            "Chat.Colors.PublicMessage.Enabled",
            configReader.getC().getBoolean("Chat.Colors.PublicMessage", true));
    configReader.addComment(
        "Chat.Colors.PublicMessage.Priority",
        CMIEventPriority.generateComments(
            "Priority of public message color listener", CMIEventPriority.NORMAL, true));
    this.ColorsPublicMessagesPriority =
        CMIEventPriority.getPriority(
            configReader.get("Chat.Colors.PublicMessage.Priority", "NORMAL"));
    configReader.addComment(
        "Chat.Colors.PrivataMessage",
        new String[] {
          "If set to true then private messages will be filtered from color codes and will allow to"
              + " colorize them with appropriate permission node",
          "cmi.colors.privatemessage.[colorName]"
        });
    this.ColorsPrivateMessage =
        configReader.get(
            "Chat.Colors.PrivateMessage",
            configReader.getC().getBoolean("Chat.Colors.PrivataMessage", true));
    configReader.addComment(
        "Chat.Colors.me",
        new String[] {
          "If set to true then /me messages will be filtered from color codes and will allow to"
              + " colorize them with appropriate permission node",
          "cmi.colors.me.[colorName]"
        });
    this.ColorsMe =
        configReader.get("Chat.Colors.me", configReader.getC().getBoolean("Chat.Colors.me", true));
    configReader.addComment(
        "Chat.Colors.CleanUp",
        new String[] {
          "If set to true, then color codes will get removed from text instead of leaving them if"
              + " player don't have appropriate permission node for that color"
        });
    CMIColorTypes[] cmiColorTypeses;
    value = (cmiColorTypeses = CMIColorTypes.values()).length;

    for (int index = 0; index < value; index++) {
      CMIColorTypes cmiColorTypes = cmiColorTypeses[index];
      if (!cmiColorTypes.equals(CMIColorTypes.nickname)) {
        cmiColorTypes.setClean(
            configReader.get(
                "Chat.Colors.CleanUp." + cmiColorTypes.name(), cmiColorTypes.isClean()));
      }
    }

    configReader.addComment(
        "Chat.Colors.CleanUp.WhiteList",
        new String[] {
          "List of strings to ignore when checking chat for color codes player cant use.",
          "This will bypass players colorcode restrictions and will allow usage of particular chat"
              + " formats",
          "Applies only for public and private messages"
        });
    this.cleanUpWhiteList =
        configReader.get("Chat.Colors.CleanUp.WhiteList", Arrays.asList("&c❤&7"));
    configReader.addComment(
        "Chat.IgnorePublicMessage",
        new String[] {
          "If set to true, players public message who is in your ignore list will not be shown"
        });
    this.ChatIgnorePublicMessage = configReader.get("Chat.IgnorePublicMessage", true);
    configReader.addComment(
        "Chat.Colors.NickName",
        new String[] {
          "If set to true then nickName will be filtered from color codes when player changes it",
          "cmi.colors.nickname.[colorName]"
        });
    this.ColorsNickName = configReader.get("Chat.Colors.NickName", true);
    configReader.addComment(
        "Chat.Colors.Picker",
        new String[] {
          "Determines /cmi chatcolor command contents. What player can pick will be determined by"
              + " cmi.colors.publicmessage.[colorCode] and will be used in "
              + Placeholder.CMIPlaceHolders.cmi_user_chatcolor.getFull()
              + " placeholder"
        });
    var list =
        configReader.get(
            "Chat.Colors.Picker",
            Arrays.asList(
                "&f",
                "&2",
                "&3",
                "&7",
                "&8",
                "&a",
                "&b",
                "&c",
                "&e",
                "&6",
                "{#00e625}",
                "{#brown}"));
    this.chatColors.clear();

    for (String colorCode : list) {
      var set = CMIChatColor.getFormats(colorCode);
      colorCode =
          colorCode.replace(
              CMIChatColor.deColorize(new CMIPlayerMessageColor(null, set).formatsToString()), "");
      CMIChatColor cmiChatColor = CMIChatColor.getColor(colorCode);
      if (cmiChatColor == null) {
        CMIMessages.consoleMessage("&5Failed to recognize chat color code (" + colorCode + ")");
      } else {
        this.chatColors.add(new CMIPlayerMessageColor(cmiChatColor, set));
      }
    }

    this.plugin.getTagManager().load(configReader);
    this.plugin.getChatBubbleManager().loadConfig(configReader);
    configReader.save();
    return true;
  }

  public boolean isModifyChatFormat() {
    return this.ModifyChatFormat;
  }

  public boolean isChatClickHoverMessages() {
    return this.ChatClickHoverMessages;
  }

  public boolean isColorsPublicMessages() {
    return this.ColorsPublicMessages;
  }

  public boolean isColorsPrivateMessage() {
    return this.ColorsPrivateMessage;
  }

  public boolean isChatIgnorePublicMessage() {
    return this.ChatIgnorePublicMessage;
  }

  public List<String> getCleanUpWhiteList() {
    return this.cleanUpWhiteList;
  }

  public boolean isChatReplyToLastMessenger() {
    return this.ChatReplyToLastMessenger;
  }

  public int getLastMessengerTimeOut() {
    return this.LastMessengerTimeOut;
  }

  public boolean isColorsMe() {
    return this.ColorsMe;
  }

  public boolean isChatDynMapChat() {
    return this.ChatDynMapChat;
  }

  public boolean isPrivateMessagesGroups() {
    return this.PrivateMessagesGroups;
  }

  public boolean isChatDiscordSRV() {
    return this.ChatDiscordSRV;
  }

  public String getChatDiscordSRVGlobalChannel() {
    return this.ChatDiscordSRVGlobalChannel;
  }

  public String getDiscordSRVLabel() {
    return this.DiscordSRVLabel;
  }

  public String getDiscordSRVUnlinkedLabel() {
    return this.DiscordSRVUnlinkedLabel;
  }

  public boolean isDiscordSRVRanged() {
    return this.DiscordSRVRanged;
  }

  public boolean isColorsNickName() {
    return this.ColorsNickName;
  }

  public Set<CMIPlayerMessageColor> getChatColors() {
    return this.chatColors;
  }

  public CMIEventPriority getColorsPublicMessagesPriority() {
    return this.ColorsPublicMessagesPriority;
  }

  public CMIEventPriority getFormatPublicMessagesPriority() {
    return this.formatPublicMessagesPriority;
  }
}
