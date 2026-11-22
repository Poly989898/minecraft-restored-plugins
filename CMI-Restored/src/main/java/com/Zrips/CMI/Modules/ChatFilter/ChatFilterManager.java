package com.Zrips.CMI.Modules.ChatFilter;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEventPriority;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Chat.ChatFilterBlockType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatFilterManager {
  private CMI plugin;
  private Pattern simpleReplacer;
  private LinkedHashMap<String, String> simpleReplacerMap = new LinkedHashMap<>();
  private List<String> emojis = new ArrayList<>();
  private HashMap<String, ChatFilterRule> rules = new HashMap<>();
  private ChatFilterRule whiteRules = null;
  private HashMap<UUID, MessageLog> spamMessages = new HashMap<>();
  private HashMap<UUID, MessageLog> spamCommands = new HashMap<>();
  private double similarityPercentage = 0.8;
  private boolean preventDoublicates = true;
  private int douplicateInterval = 5;
  private int douplicateChatMinAmount = 5;
  private double similarityComamndPercentage = 0.8;
  private boolean preventCommandDoublicates = true;
  private int douplicateCommandInterval = 5;
  private int douplicateCommandMinAmount = 5;
  private List<String> douplicateCommandWhiteList = new ArrayList<>();
  private boolean capsFilter = true;
  private boolean lowerCase = true;
  private boolean cancelEvent = false;
  private int capsIgnoreUnder;
  private int capsPercentage;
  private List<String> capsCommands;
  private List<String> capsWhiteList = new ArrayList<>();
  private CMIEventPriority capsCheckPriority = CMIEventPriority.LOW;
  private CMIEventPriority filterCheckPriority = CMIEventPriority.LOW;
  private CMIEventPriority simpleReplacerPriority = CMIEventPriority.LOWEST;
  private CMIEventPriority commandSpamPriority = CMIEventPriority.LOW;
  private CMIEventPriority chatSpamPriority = CMIEventPriority.LOW;
  private CMIEventPriority signAdPriority = CMIEventPriority.MONITOR;
  public static final String StringchatFilterFile = "ChatFilter.yml";

  public void clearCache(UUID uuid) {
    this.spamMessages.remove(uuid);
    this.spamCommands.remove(uuid);
  }

  public ChatFilterManager(CMI cmi) {
    this.plugin = cmi;

    try {
      this.whiteRules = new ChatFilterRule();
      this.whiteRules.setPattern(
          Arrays.asList(
              "\\bgoogle.\\s?([a-zA-Z]{2,4})\\b",
              "\\bltcraft.\\s?([a-zA-Z]{2,4})\\b",
              "\\bspigotmc.\\s?([a-zA-Z]{2,4})\\b"));
    } catch (Exception exception) {
      CMIMessages.sendMessage(Bukkit.getConsoleSender(), ChatColor.RED + "Cant load chat filter");
      exception.printStackTrace();
    }
  }

  public boolean checkCaps(Player player, String string) {
    int value = string.length();
    if (!this.capsFilter) {
      return true;
    }

    if (PermissionsManager.CMIPerm.chatfilter_capbypass.hasSetPermission(player)) {
      return true;
    }

    if (this.capsWhiteList != null
        && this.capsWhiteList.contains(CMIChatColor.stripColor(string).toLowerCase())) {
      return true;
    }

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      string =
          string.replaceFirst(
              Pattern.quote(onlinePlayer.getName()),
              Matcher.quoteReplacement(onlinePlayer.getName().toLowerCase()));
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(onlinePlayer);
      if (cmiUser.getNickName() != null) {
        string =
            string.replaceFirst(
                Pattern.quote(cmiUser.getNickName()),
                Matcher.quoteReplacement(cmiUser.getNickName().toLowerCase()));
      }
    }

    int matched = 0;
    int skipped = 0;

    for (int index = 0; index < value; index++) {
      char ch = string.charAt(index);
      if (ch == ' ') {
        skipped++;
      } else if (Character.toLowerCase(ch) != ch) {
        matched++;
      }
    }

    int percent = (int) (matched * 100.0 / (value - skipped));
    if (value - skipped <= this.capsIgnoreUnder) {
      return true;
    } else if (percent > this.capsPercentage) {
      CMIScheduler.runTask(
          this.plugin,
          () -> {
            List<String> list = new ArrayList<>(this.capsCommands);
            list = this.plugin.getLM().updateSnd(new Snd(player, player), list);
            this.plugin.getSpecializedCommandManager().processCmds(list, player);
          });
      return false;
    } else {
      return true;
    }
  }

  public RuleResponse getCorrectMessage(Player player, String string) {
    return this.getCorrectMessage(player, string, false);
  }

  @Nullable
  public String replaceEmoji(String string) {
    if (this.getEmojiReplacerMap().isEmpty()) {
      return null;
    }

    Matcher matcher = this.simpleReplacer.matcher(string);
    StringBuffer stringBuffer = new StringBuffer();

    while (matcher.find()) {
      String matchedText = matcher.group();
      String replacement = this.getEmojiReplacerMap().get(matchedText);
      if (replacement != null) {
        matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
      }
    }

    matcher.appendTail(stringBuffer);
    return stringBuffer.toString();
  }

  public RuleResponse getCorrectMessage(Player player, String string, boolean flag) {
    RuleResponse ruleResponse = new RuleResponse(string);
    HashMap<String, String> hashMap = new HashMap();
    int value = 0;

    for (Pattern pattern : this.whiteRules.getPattern()) {
      Matcher matcher = pattern.matcher(string);
      value++;
      if (matcher != null) {
        while (matcher.find()) {
          String patternText = "%" + value + "%tipitop%" + value + "%";
          string = string.replace(matcher.group(), patternText);
          hashMap.put(patternText, matcher.group());
        }
      }
    }

    for (var entry : this.rules.entrySet()) {
      ChatFilterRule chatFilterRule = (ChatFilterRule) entry.getValue();
      if (!flag || chatFilterRule.isApplyToPrivateMessages()) {
        Matcher ruleMatcher = chatFilterRule.getMatcher(string);
        if (ruleMatcher != null) {
          while (ruleMatcher.find()) {
            ruleResponse.addRule(chatFilterRule);
            if (PermissionsManager.CMIPerm.chatfilter_bypass_$1.hasPermission(
                player, chatFilterRule.getGroup())) {
              ruleResponse.addBypass(chatFilterRule.getGroup());
            } else if (chatFilterRule.getReplaceWith() != null
                && !chatFilterRule.getReplaceWith().isEmpty()) {
              string = string.replace(ruleMatcher.group(), chatFilterRule.getReplaceWith());
            }
          }
        }
      }
    }

    for (Entry ruleEntry : hashMap.entrySet()) {
      string =
          string.replace((CharSequence) ruleEntry.getKey(), (CharSequence) ruleEntry.getValue());
    }

    ruleResponse.setUpdatedMessage(string);
    return ruleResponse;
  }

  public boolean isSpamedCommand(Player player, String string) {
    if (!this.preventCommandDoublicates) {
      return false;
    }

    if (this.isWhiteListedCommand(string)) {
      return false;
    }

    if (PermissionsManager.CMIPerm.commandfilter_bypass.hasPermission(player)) {
      return false;
    }

    MessageLog messageLog = this.spamCommands.get(player.getUniqueId());
    if (messageLog == null) {
      messageLog = new MessageLog(this.douplicateCommandInterval);
      messageLog.addMessage(string);
      this.spamCommands.put(player.getUniqueId(), messageLog);
      return false;
    }

    int value = 0;

    for (var entry : messageLog.getMessages().entrySet()) {
      double number = similarity(string, (String) entry.getValue());
      if (number > this.similarityComamndPercentage) {
        value++;
      }
    }

    if (value >= this.douplicateCommandMinAmount) {
      return true;
    }

    messageLog.addMessage(string);
    return false;
  }

  private boolean isWhiteListedCommand(String string) {
    if (string.startsWith("/")) {
      string = string.substring(1, string.length());
    }

    for (String command : this.douplicateCommandWhiteList) {
      if (command.startsWith("/")) {
        command = command.substring(1, command.length());
      }

      if (string.toLowerCase().startsWith(command.toLowerCase())) {
        return true;
      }
    }

    return false;
  }

  public boolean isSpam(Player player, String string) {
    if (!this.preventDoublicates) {
      return false;
    }

    if (PermissionsManager.CMIPerm.chatfilter_spambypass.hasPermission(player)) {
      return false;
    }

    MessageLog messageLog = this.spamMessages.get(player.getUniqueId());
    if (messageLog == null) {
      messageLog = new MessageLog(this.douplicateInterval);
      messageLog.addMessage(string);
      this.spamMessages.put(player.getUniqueId(), messageLog);
      return false;
    }

    int value = 0;

    for (var entry : new HashMap<>(messageLog.getMessages()).entrySet()) {
      double number = similarity(string, (String) entry.getValue());
      if (number > this.similarityPercentage) {
        value++;
      }
    }

    if (value >= this.douplicateChatMinAmount) {
      return true;
    }

    messageLog.addMessage(string);
    return false;
  }

  public static double similarity(String string, String other) {
    String left = string;
    String right = other;
    if (string.length() < other.length()) {
      left = other;
      right = string;
    }

    int value = left.length();
    return value == 0 ? 1.0 : (double) (value - editDistance(left, right)) / value;
  }

  public static int editDistance(String string, String other) {
    string = string.toLowerCase();
    other = other.toLowerCase();
    int[] values = new int[other.length() + 1];

    for (int value = 0; value <= string.length(); value++) {
      int previous = value;

      for (int columnIndex = 0; columnIndex <= other.length(); columnIndex++) {
        if (value == 0) {
          values[columnIndex] = columnIndex;
        } else if (columnIndex > 0) {
          int costValue = values[columnIndex - 1];
          if (string.charAt(value - 1) != other.charAt(columnIndex - 1)) {
            costValue = Math.min(Math.min(costValue, previous), values[columnIndex]) + 1;
          }

          values[columnIndex - 1] = previous;
          previous = costValue;
        }
      }

      if (value > 0) {
        values[other.length()] = previous;
      }
    }

    return values[other.length()];
  }

  private static ChatFilterBlockType getBlockType(String string) {
    ChatFilterBlockType[] chatFilterBlockTypes;
    int value = (chatFilterBlockTypes = ChatFilterBlockType.values()).length;

    for (int index = 0; index < value; index++) {
      ChatFilterBlockType chatFilterBlockType = chatFilterBlockTypes[index];
      if (chatFilterBlockType.name().equalsIgnoreCase(string)) {
        return chatFilterBlockType;
      }
    }

    return ChatFilterBlockType.None;
  }

  public void load() {
    this.plugin.safeRenameFile(
        "ChatFilter.yml".toLowerCase(), "Settings" + File.separator + "ChatFilter.yml");
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Settings" + File.separator + "ChatFilter.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      boolean flag = configReader.get("ChatFilter.Enabled", false);
      configReader.addComment(
          "ChatFilter.Priority",
          new String[] {
            "Priority of chat filter listener",
            "Changed priority will only take effect on full server restart",
            "Possible options: " + CMIEventPriority.listGeneralPriorities(),
            "Default: LOW"
          });
      this.filterCheckPriority =
          CMIEventPriority.getPriority(configReader.get("ChatFilter.Priority", "LOW"));
      this.rules.clear();
      if (!configReader.getC().isConfigurationSection("ChatFilter.Deny")) {
        ChatFilterRule chatFilterRule = new ChatFilterRule();
        chatFilterRule.setRuleName("ipBlock");
        boolean ipBlockEnabled = configReader.get("ChatFilter.Deny.ipBlock.Enabled", true);
        chatFilterRule.setGroup(configReader.get("ChatFilter.Deny.ipBlock.Group", "Advertising"));
        chatFilterRule.setInformConsole(
            configReader.get("ChatFilter.Deny.ipBlock.InformConsole", true));
        chatFilterRule.setPattern(
            configReader.get(
                "ChatFilter.Deny.ipBlock.Regex",
                Arrays.asList(
                    "[a-zA-Z0-9\\-\\.]+\\s?(\\.|dot|\\(dot\\)|-|;|:|,|_|\\/)\\s?([a-zA-Z]{2}|aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel)\\b",
                    "\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}\\b")));
        chatFilterRule.setReplaceWith(configReader.get("ChatFilter.Deny.ipBlock.ReplaceWith", ""));
        chatFilterRule.setBlockType(
            getBlockType(configReader.get("ChatFilter.Deny.ipBlock.BlockType", "others")));
        chatFilterRule.setApplyToPrivateMessages(
            configReader.get("ChatFilter.Deny.ipBlock.includePrivateMessages", true));
        chatFilterRule.setMessageToStaff(
            configReader.get(
                "ChatFilter.Deny.ipBlock.msgToStaff",
                "&4!&6[playerName] &4advertising with: &r[message]"));
        chatFilterRule.setCommands(
            configReader.get("ChatFilter.Deny.ipBlock.Commands", new ArrayList()));
        if (ipBlockEnabled && flag) {
          this.rules.put(chatFilterRule.getRuleName(), chatFilterRule);
        }

        chatFilterRule = new ChatFilterRule();
        chatFilterRule.setRuleName("swearing1");
        ipBlockEnabled = configReader.get("ChatFilter.Deny.swearing1.Enabled", true);
        chatFilterRule.setGroup(configReader.get("ChatFilter.Deny.swearing1.Group", "Swearing"));
        chatFilterRule.setInformConsole(
            configReader.get("ChatFilter.Deny.swearing1.InformConsole", true));
        chatFilterRule.setPattern(
            configReader.get(
                "ChatFilter.Deny.swearing1.Regex", Arrays.asList("\\bass\\b|\\basshole")));
        chatFilterRule.setReplaceWith(
            configReader.get("ChatFilter.Deny.swearing1.ReplaceWith", "dude"));
        chatFilterRule.setBlockType(
            getBlockType(configReader.get("ChatFilter.Deny.swearing1.BlockType", "none")));
        chatFilterRule.setApplyToPrivateMessages(
            configReader.get("ChatFilter.Deny.swearing1.includePrivateMessages", true));
        chatFilterRule.setMessageToStaff(
            configReader.get(
                "ChatFilter.Deny.swearing1.msgToStaff", "&4!&6[playerName] &4swearing"));
        chatFilterRule.setCommands(
            configReader.get(
                "ChatFilter.Deny.swearing1.Commands",
                Arrays.asList("msg [senderName] &eDon't swear!")));
        if (ipBlockEnabled && flag) {
          this.rules.put(chatFilterRule.getRuleName(), chatFilterRule);
        }

        chatFilterRule = new ChatFilterRule();
        chatFilterRule.setRuleName("swearing2");
        ipBlockEnabled = configReader.get("ChatFilter.Deny.swearing2.Enabled", true);
        chatFilterRule.setGroup(configReader.get("ChatFilter.Deny.swearing2.Group", "Swearing"));
        chatFilterRule.setInformConsole(
            configReader.get("ChatFilter.Deny.swearing2.InformConsole", true));
        chatFilterRule.setPattern(
            configReader.get(
                "ChatFilter.Deny.swearing2.Regex",
                Arrays.asList("\\b(?i)(f+(\\W|_)*(u?)+(\\W|_)*(c?)+(\\W|_)*k+)")));
        chatFilterRule.setReplaceWith(
            configReader.get("ChatFilter.Deny.swearing2.ReplaceWith", "not good"));
        chatFilterRule.setApplyToPrivateMessages(
            configReader.get("ChatFilter.Deny.swearing2.includePrivateMessages", true));
        chatFilterRule.setBlockType(
            getBlockType(configReader.get("ChatFilter.Deny.swearing2.BlockType", "none")));
        chatFilterRule.setMessageToStaff(
            configReader.get(
                "ChatFilter.Deny.swearing2.msgToStaff", "&4!&6[playerName] &4swearing"));
        chatFilterRule.setCommands(
            configReader.get(
                "ChatFilter.Deny.swearing2.Commands",
                Arrays.asList("msg [senderName] &eDon't swear!")));
        if (ipBlockEnabled && flag) {
          this.rules.put(chatFilterRule.getRuleName(), chatFilterRule);
        }
      } else {
        for (String string :
            configReader.getC().getConfigurationSection("ChatFilter.Deny").getKeys(false)) {
          String denyPrefix = "ChatFilter.Deny." + string + ".";
          ChatFilterRule denyRule = new ChatFilterRule();
          denyRule.setRuleName(string);
          Boolean denyEnabled = configReader.get(denyPrefix + "Enabled", false);
          denyRule.setGroup(configReader.get(denyPrefix + "Group", string));
          denyRule.setInformConsole(configReader.get(denyPrefix + "InformConsole", false));
          denyRule.setPattern(configReader.get(denyPrefix + "Regex", Arrays.asList("")));
          denyRule.setReplaceWith(configReader.get(denyPrefix + "ReplaceWith", ""));
          denyRule.setBlockType(getBlockType(configReader.get(denyPrefix + "BlockType", "none")));
          denyRule.setApplyToPrivateMessages(
              configReader.get(denyPrefix + "includePrivateMessages", true));
          denyRule.setMessageToStaff(configReader.get(denyPrefix + "msgToStaff", ""));
          denyRule.setCommands(configReader.get(denyPrefix + "Commands", new ArrayList()));
          if (denyEnabled && flag) {
            this.rules.put(denyRule.getRuleName(), denyRule);
          }
        }
      }

      if (configReader.getC().isConfigurationSection("ChatFilter.Deny")) {
        var set = configReader.getC().getConfigurationSection("ChatFilter.Deny").getKeys(false);
        if (set != null && set.iterator().hasNext()) {
          String denyName = (String) set.iterator().next();
          String denyPath = "ChatFilter.Deny." + denyName + ".";
          configReader.addComment(
              denyPath + "Enabled",
              new String[] {"If not set to true, this filter will not be used"});
          configReader.addComment(
              denyPath + "InformConsole",
              new String[] {
                "When set to true, each time player triggers filter, console will receive"
                    + " information about who triggered it, which filter and with what message"
              });
          configReader.addComment(
              denyPath + "Group",
              new String[] {
                "Defines filter group and defines required permission node to bypass this filter:"
                    + " cmi.chatfilter.bypass.[groupName]"
              });
          configReader.addComment(
              denyPath + "Regex",
              new String[] {
                "Regex expression to filter by. How to use regex https://regexone.com/"
              });
          configReader.addComment(
              denyPath + "ReplaceWith",
              new String[] {
                "With what we need to replace word, if not defined found expression will not be"
                    + " changed"
              });
          configReader.addComment(
              denyPath + "BlockType",
              new String[] {
                "possible: none, others, all",
                "Where 'none' means everyone will receive this message",
                "'others' means that sender will get message but not other players, this is usefull"
                    + " to prevent advertising and silently block it",
                "'all' means that no one will receive sent message"
              });
          configReader.addComment(
              denyPath + "includePrivateMessages",
              new String[] {"When enabled rule will be applied to private messages"});
          configReader.addComment(
              denyPath + "msgToStaff",
              new String[] {
                "Players with cmi.chatfilter.inform permission will receive defined message when"
                    + " rule is broken"
              });
          configReader.addComment(
              denyPath + "Commands",
              new String[] {
                "List of commands to perform when rule is broken. Use [senderName] to include"
                    + " message sender name. Supports global variables same as locale file",
                "Use [message] to include original players message",
                "Use [updatedmessage] to include new modified message"
              });
        }
      }

      this.whiteRules = new ChatFilterRule();
      configReader.addComment(
          "ChatFilter.WhiteList",
          new String[] {
            "List of regex filter to exlude from block list. Usefull if you want to block all"
                + " ip/host address but want to allow usage of your own server."
          });
      this.whiteRules.setPattern(
          configReader.get(
              "ChatFilter.WhiteList",
              Arrays.asList(
                  "\\bgoogle.\\s?([a-zA-Z]{2,4})\\b", "\\bspigotmc.\\s?([a-zA-Z]{2,4})\\b")));
      configReader.addComment(
          "ChatFilter.DuplicatedMessagePrevention.Use",
          new String[] {
            "When set to true, plugin will prevent spaming of same or similar messages in short"
                + " time range. Can be bypased with cmi.chatfilter.spambypass permission"
          });
      this.preventDoublicates =
          configReader.get("ChatFilter.DuplicatedMessagePrevention.Use", false);
      configReader.addComment(
          "ChatFilter.DuplicatedMessagePrevention.Priority",
          new String[] {
            "Priority of chat spam listener",
            "Changed priority will only take effect on full server restart",
            "Possible options: " + CMIEventPriority.listGeneralPriorities(),
            "Default: LOW"
          });
      this.chatSpamPriority =
          CMIEventPriority.getPriority(
              configReader.get("ChatFilter.DuplicatedMessagePrevention.Priority", "LOW"));
      configReader.addComment(
          "ChatFilter.DuplicatedMessagePrevention.Percentage",
          new String[] {"How much in percentage message is counted as same"});
      this.similarityPercentage =
          configReader.get("ChatFilter.DuplicatedMessagePrevention.Percentage", 80);
      if (this.similarityPercentage > 1.0) {
        this.similarityPercentage /= 100.0;
      }

      configReader.addComment(
          "ChatFilter.DuplicatedMessagePrevention.Interval",
          new String[] {"Defines how often in seconds you can send same/similar message"});
      this.douplicateInterval =
          configReader.get("ChatFilter.DuplicatedMessagePrevention.Interval", 5);
      configReader.addComment(
          "ChatFilter.DuplicatedMessagePrevention.MinAmount",
          new String[] {"How many commands you can repeat before stopped for cooldown"});
      this.douplicateChatMinAmount =
          configReader.get("ChatFilter.DuplicatedMessagePrevention.MinAmount", 2);
      configReader.addComment(
          "ChatFilter.Caps.Filter",
          new String[] {
            "When nebaled we will try to prevent chat messages with multiple capitalized letters by"
                + " defined criteria",
            "Can be bypassed with "
                + PermissionsManager.CMIPerm.chatfilter_capbypass.getPermissionForShow()
                + " permission node"
          });
      this.capsFilter = configReader.get("ChatFilter.Caps.Filter", true);
      configReader.addComment(
          "ChatFilter.Caps.Priority",
          new String[] {
            "Priority of chat capitalization listener",
            "Changed priority will only take effect on full server restart",
            "Possible options: " + CMIEventPriority.listGeneralPriorities(),
            "Default: LOW"
          });
      this.capsCheckPriority =
          CMIEventPriority.getPriority(configReader.get("ChatFilter.Caps.Priority", "LOW"));
      configReader.addComment(
          "ChatFilter.Caps.IgnoreUnder",
          new String[] {
            "Defines amount of letters we can ignore",
            "For example ':DDD' would be made of 2 letters with 3 capitalized letters which would"
                + " make it 75% capitalized"
          });
      this.capsIgnoreUnder = configReader.get("ChatFilter.Caps.IgnoreUnder", 6);
      this.capsIgnoreUnder = this.capsIgnoreUnder < 0 ? 0 : this.capsIgnoreUnder;
      configReader.addComment(
          "ChatFilter.Caps.Percentage",
          new String[] {
            "Amount in percentage of capitalized letters we should not cross over.",
            "In example 'GOOD thing' would be 4 capitalized and 5 not, spaces gets ignored, which"
                + " makes it 44% capitalized and passes check"
          });
      this.capsPercentage =
          CMINumber.clamp(configReader.get("ChatFilter.Caps.Percentage", 50), 0, 100);
      configReader.addComment(
          "ChatFilter.Caps.LowerCase",
          new String[] {"When capitalized text is detected, should we lower case text"});
      this.lowerCase = configReader.get("ChatFilter.Caps.LowerCase", true);
      configReader.addComment(
          "ChatFilter.Caps.CancelEvent",
          new String[] {"Should we cancel chat event if we detect too many caps"});
      this.cancelEvent = configReader.get("ChatFilter.Caps.CancelEvent", false);
      configReader.addComment(
          "ChatFilter.Caps.Commands",
          new String[] {"List of commands you want to perform when player uses too many caps"});
      this.capsCommands =
          configReader.get(
              "ChatFilter.Caps.Commands",
              Arrays.asList("asConsole! cmi titlemsg [playerName] &cToo many caps! -keep:20"));
      configReader.addComment(
          "ChatFilter.Caps.WhiteList",
          new String[] {
            "List of messages, excluding color codes, player can use even if it 100% capitalized"
          });
      this.capsWhiteList =
          configReader.get(
              "ChatFilter.Caps.WhiteList",
              Arrays.asList(
                  "AFAIK", "AFK", "BRB", "IMHO", "OMG", "ROFL", "ROFLMAO", "LQTM", "LSMH", "LMHO"));
      CMIList.toLowerCase(this.capsWhiteList);
      configReader.addComment(
          "ChatFilter.Signs.Priority",
          new String[] {
            "Priority of sign advertisement listener",
            "Changed priority will only take effect on full server restart",
            "Possible options: " + CMIEventPriority.listPriorities(),
            "Default: MONITOR"
          });
      this.signAdPriority =
          CMIEventPriority.getPriority(configReader.get("ChatFilter.Caps.Priority", "MONITOR"));
      configReader.addComment(
          "ChatFilter.SimpleReplacer.Priority",
          new String[] {
            "Priority of simple replacer listener",
            "Changed priority will only take effect on full server restart",
            "Possible options: " + CMIEventPriority.listGeneralPriorities(),
            "Default: LOWEST"
          });
      this.simpleReplacerPriority =
          CMIEventPriority.getPriority(
              configReader.get("ChatFilter.SimpleReplacer.Priority", "LOWEST"));
      configReader.addComment(
          "ChatFilter.SimpleReplacer.Enabled",
          new String[] {"Enables simpler replacer feature for public and private messages"});
      boolean replacerEnabled = configReader.get("ChatFilter.SimpleReplacer.Enabled", false);
      configReader.addComment(
          "ChatFilter.SimpleReplacer.Replacements",
          new String[] {"Quick replacer for inputs like :heart: into ❤"});
      var list =
          Arrays.asList(
              "heart-❤",
              "star-★",
              "sparkles-✨",
              "sunny-☀",
              "umbrella-☂",
              "zap-⚡",
              "musical_note-♫",
              "check_mark-✓",
              "cross_mark-✗",
              "smile-☺",
              "frown-☹",
              "wink-\ud83d\ude09",
              "grin-\ud83d\ude01",
              "laughing-\ud83d\ude06",
              "cool-\ud83d\ude0e",
              "cry-\ud83d\ude22",
              "angry-\ud83d\ude20",
              "thumbs_up-\ud83d\udc4d",
              "thumbs_down-\ud83d\udc4e",
              "clapping-\ud83d\udc4f",
              "ok_hand-\ud83d\udc4c",
              "pray-\ud83d\ude4f",
              "fire-\ud83d\udd25",
              "poop-\ud83d\udca9",
              "party_popper-\ud83c\udf89",
              "balloon-\ud83c\udf88",
              "trophy-\ud83c\udfc6",
              "coffee-☕",
              "warning-⚠",
              "question_mark-❓",
              "exclamation_mark-❗",
              "hourglass-⌛",
              "light_bulb-\ud83d\udca1",
              "camera-\ud83d\udcf7",
              "video_game-\ud83c\udfae",
              "laptop-\ud83d\udcbb",
              "rocket-\ud83d\ude80",
              "sparkle_heart-\ud83d\udc96",
              "broken_heart-\ud83d\udc94",
              "note-♪",
              "skull-☠",
              "snowman-☃",
              "shamrock-☘",
              "yin_yang-☯",
              "peace-☮",
              "star_of_david-✡",
              "cross_star-✝",
              "biohazard-☣",
              "radioactive-☢",
              "scissors-✂",
              "pencil-✏",
              "telephone-☎",
              "envelope-✉",
              "anchor-⚓",
              "recycle-♻");
      List replacements = configReader.get("ChatFilter.SimpleReplacer.Replacements", list);
      if (replacerEnabled) {
        this.getEmojiReplacerMap().clear();
        StringBuilder stringBuilder = new StringBuilder();

        for (String replacement : ((Iterable<String>) replacements)) {
          String[] strings = replacement.split("-", 2);
          if (strings.length != 1) {
            this.getEmojiReplacerMap().put(":" + strings[0] + ":", strings[1]);
            stringBuilder.append(":").append(Pattern.quote(strings[0])).append(":").append("|");
          }
        }

        if (!stringBuilder.toString().isEmpty()) {
          this.simpleReplacer =
              Pattern.compile(stringBuilder.substring(0, stringBuilder.length() - 1));
        }
      } else {
        this.simpleReplacer = null;
        this.getEmojiReplacerMap().clear();
      }

      configReader.addComment(
          "ChatFilter.Emojies", new String[] {"Emojies to be used for emojipicker command"});
      this.getEmojis().clear();
      String replacerRaw =
          configReader.get(
              "ChatFilter.Emojies",
              "\ud83d\ude00\ud83d\ude03\ud83d\ude04\ud83d\ude01\ud83d\ude06\ud83d\ude05\ud83e\udd23\ud83d\ude02\ud83e\udd79\ud83d\ude42\ud83d\ude43\ud83d\ude09\ud83d\ude0a\ud83d\ude07\ud83e\udd70\ud83d\ude0d\ud83e\udd29\ud83d\ude18\ud83d\ude17☺\ud83d\ude1a\ud83d\ude19\ud83e\udd72\ud83d\ude0b\ud83d\ude1b\ud83d\ude1c\ud83e\udd2a\ud83d\ude1d\ud83e\udd11\ud83e\udd17\ud83e\udee2\ud83e\udd2d\ud83e\udd2b\ud83e\udd14\ud83e\udee1\ud83e\udd10\ud83e\udd28\ud83d\ude10\ud83d\ude11\ud83d\ude36\ud83d\ude0f\ud83d\ude12\ud83d\ude44\ud83d\ude2c\ud83e\udd25\ud83d\ude0c\ud83d\ude14\ud83d\ude2a\ud83d\ude2e\ud83d\udca8\ud83e\udd24\ud83d\ude34\ud83d\ude37\ud83e\udd12\ud83e\udd15\ud83e\udd22\ud83e\udd2e\ud83e\udd27\ud83e\udee0\ud83e\udd75\ud83e\udd76\ud83d\ude36\ud83c\udf2b\ud83e\udee5\ud83e\udd74\ud83e\udee8\ud83d\ude35\ud83d\udcab\ud83d\ude35\ud83e\udd2f\ud83e\udd20\ud83e\udd73\ud83e\udd78\ud83d\ude0e\ud83e\udd13\ud83e\uddd0\ud83e\udee4\ud83d\ude15\ud83d\ude1f\ud83d\ude41☹\ud83d\ude2e\ud83d\ude2f\ud83d\ude32\ud83d\ude33\ud83e\udee3\ud83e\udd7a\ud83d\ude26\ud83d\ude27\ud83d\ude28\ud83d\ude30\ud83d\ude25\ud83d\ude22\ud83d\ude2d\ud83d\ude31\ud83d\ude16\ud83d\ude23\ud83d\ude1e\ud83d\ude13\ud83d\ude29\ud83d\ude2b\ud83e\udd71\ud83d\ude24\ud83d\ude21\ud83d\ude20\ud83e\udd2c\ud83d\ude08\ud83d\udc7f\ud83d\udc80☠\ud83d\udca9\ud83e\udd21\ud83d\udc79\ud83d\udc7a\ud83d\udc7b\ud83d\udc7d\ud83d\udc7e\ud83e\udd16\ud83d\ude3a\ud83d\ude38\ud83d\ude39\ud83d\ude3b\ud83d\ude3c\ud83d\ude3d\ud83d\ude40\ud83d\ude3f\ud83d\ude3e\ud83d\ude48\ud83d\ude49\ud83d\ude4a\ud83d\udc4b\ud83e\udd1a\ud83d\udd90✋\ud83d\udd96\ud83d\udc4c\ud83e\udd0c\ud83e\udd0f✌\ud83e\udd1e\ud83e\udef0\ud83e\udd1f\ud83e\udd18\ud83e\udd19\ud83d\udc48\ud83d\udc49\ud83d\udc46\ud83d\udd95\ud83d\udc47☝\ud83e\udef5\ud83d\udc4d\ud83d\udc4e✊\ud83d\udc4a\ud83e\udd1b\ud83e\udd1c\ud83d\udc4f\ud83d\ude4c\ud83d\udc50\ud83e\udef6\ud83e\udd32\ud83e\udef3\ud83e\udef4\ud83e\udef1\ud83e\udef2\ud83e\udd1d\ud83e\udef8\ud83e\udef7\ud83d\ude4f✍\ud83d\udc85\ud83e\udd33\ud83d\udcaa\ud83e\uddbe\ud83e\uddbf\ud83e\uddb5\ud83e\uddb6\ud83d\udc42\ud83e\uddbb\ud83d\udc43\ud83e\udde0\ud83e\udec0\ud83e\udec1\ud83e\uddb7\ud83e\uddb4\ud83d\udc40\ud83d\udc41\ud83d\udc45\ud83d\udc44\ud83e\udee6\ud83d\udc8b\ud83d\udc76\ud83e\uddd2\ud83d\udc66\ud83d\udc67\ud83e\uddd4\ud83e\uddd1\ud83e\uddb0\ud83e\uddb1\ud83e\uddb3\ud83e\uddb1\ud83e\uddb2\ud83d\udc71\ud83e\uddd3\ud83d\udc74\ud83d\udc75\ud83d\ude4d\ud83d\ude4e\ud83d\ude45\ud83d\ude46\ud83d\udc81\ud83d\ude4b\ud83e\udd26\ud83d\ude47\ud83e\uddcf\ud83d\udc68\ud83e\udd37⚕\ud83c\udf93\ud83c\udfeb⚖\ud83c\udf3e\ud83c\udf73\ud83d\udd27\ud83c\udfed\ud83d\udcbc\ud83d\udd2c\ud83d\udcbb\ud83c\udfa4\ud83c\udfa8✈\ud83d\udc69\ud83d\ude80\ud83d\ude92\ud83d\ude92\ud83d\udc6e\ud83d\udd75\ud83d\udc82\ud83e\udd77\ud83d\udc77\ud83e\udec5\ud83d\udc78\ud83d\udc73\ud83d\udc72\ud83e\uddd5\ud83e\udd35\ud83d\udc70\ud83e\udec4\ud83e\udd31\ud83c\udf7c\ud83d\udc7c\ud83c\udf85\ud83e\udd36\ud83c\udf84\ud83e\uddb8\ud83e\uddb9\ud83e\uddd9\ud83e\uddda\ud83e\udddb\ud83e\udddc\ud83e\udddd\ud83e\uddde\ud83e\udddf\ud83e\uddcc\ud83d\udc86\ud83d\udc87\ud83d\udeb6\ud83e\uddcd\ud83e\uddce\ud83e\uddaf\ud83e\uddbc\ud83e\uddbd\ud83c\udfc3\ud83d\udc83\ud83d\udd7a\ud83d\udd74\ud83d\udc6f\ud83e\uddd6\ud83e\uddd7\ud83e\udd3a\ud83c\udfc7⛷\ud83c\udfc2\ud83c\udfcc\ud83c\udfc4\ud83d\udea3\ud83c\udfca⛹\ud83c\udfcb\ud83d\udeb4\ud83d\udeb5\ud83e\udd38\ud83e\udd3c\ud83e\udd3d\ud83e\udd3e\ud83e\udd39\ud83e\uddd8\ud83d\udec0\ud83d\udecc\ud83e\udd1d\ud83d\udc6d\ud83d\udc8f❤\ud83d\udc8b\ud83d\udde3\ud83d\udc64\ud83d\udc65\ud83e\udec2\ud83d\udc63\ud83d\udc35\ud83d\udc12\ud83e\udd8d\ud83e\udda7\ud83d\udc36\ud83d\udc15\ud83e\uddae\ud83d\udc15\ud83e\uddba\ud83d\udc29\ud83d\udc3a\ud83e\udd8a\ud83e\udd9d\ud83d\udc31\ud83d\udc08\ud83d\udc08⬛\ud83e\udd81\ud83d\udc2f\ud83d\udc05\ud83d\udc06\ud83d\udc34\ud83d\udc0e\ud83e\udd84\ud83e\udecf\ud83e\udd93\ud83e\udd8c\ud83e\udece\ud83e\uddac\ud83d\udc2e\ud83d\udc02\ud83d\udc03\ud83d\udc04\ud83d\udc37\ud83d\udc16\ud83d\udc17\ud83d\udc3d\ud83d\udc0f\ud83d\udc11\ud83d\udc10\ud83d\udc2a\ud83d\udc2b\ud83e\udd99\ud83e\udd92\ud83d\udc18\ud83e\udda3\ud83e\udd8f\ud83e\udd9b\ud83d\udc2d\ud83d\udc01\ud83d\udc00\ud83d\udc39\ud83d\udc30\ud83d\udc07\ud83d\udc3f\ud83e\uddab\ud83e\udd94\ud83e\udd87❄\ud83d\udc3b\ud83d\udc28\ud83d\udc3c\ud83e\udda5\ud83e\udda6\ud83e\udda8\ud83e\udd98\ud83e\udda1\ud83d\udc3e\ud83e\udd83\ud83d\udc14\ud83d\udc13\ud83d\udc23\ud83d\udc24\ud83d\udc25\ud83d\udc26\ud83d\udc27\ud83d\udc26⬛\ud83d\udd4a\ud83e\udd85\ud83e\udd86\ud83e\udebf\ud83e\udda2\ud83e\udd89\ud83e\udda4\ud83e\udda9\ud83e\udd9a\ud83e\udd9c\ud83e\udebd\ud83e\udeb6\ud83e\udeb9\ud83e\udeba\ud83e\udd5a\ud83d\udc38\ud83d\udc0a\ud83d\udc22\ud83e\udd8e\ud83d\udc0d\ud83d\udc32\ud83d\udc09\ud83e\udd95\ud83e\udd96\ud83d\udc33\ud83d\udc0b\ud83d\udc2c\ud83e\uddad\ud83d\udc1f\ud83d\udc20\ud83d\udc21\ud83e\udd88\ud83e\udebc\ud83d\udc19\ud83e\udd91\ud83e\udd80\ud83e\udd9e\ud83e\udd90\ud83e\udeb8\ud83e\uddaa\ud83d\udc1a\ud83d\udc0c\ud83e\udd8b\ud83d\udc1b\ud83d\udc1c\ud83d\udc1d\ud83e\udeb2\ud83d\udc1e\ud83e\udd97\ud83e\udeb3\ud83d\udd77\ud83d\udd78\ud83e\udd82\ud83e\udd9f\ud83e\udeb0\ud83e\udeb1\ud83e\udda0\ud83c\udf44\ud83d\udc90\ud83d\udcae\ud83c\udff5\ud83c\udf3c\ud83c\udf3b\ud83c\udf39\ud83e\udd40\ud83c\udf3a\ud83c\udf37\ud83c\udf38\ud83e\udeb7\ud83e\udebb\ud83c\udf31\ud83e\udeb4\ud83c\udfd5\ud83c\udf32\ud83c\udf33\ud83c\udf30\ud83c\udf34\ud83c\udf35\ud83c\udf8b\ud83c\udf8d\ud83c\udf3e\ud83c\udf3f☘\ud83c\udf40\ud83c\udf41\ud83c\udf42\ud83c\udf43\ud83c\udf0d\ud83c\udf0e\ud83c\udf0f\ud83c\udf11\ud83c\udf12\ud83c\udf13\ud83c\udf14\ud83c\udf15\ud83c\udf16\ud83c\udf17\ud83c\udf18\ud83c\udf19\ud83c\udf1a\ud83c\udf1b\ud83c\udf1c☀\ud83c\udf1d\ud83c\udf1e\ud83e\ude90\ud83d\udcab⭐\ud83c\udf1f✨\ud83c\udf20☄\ud83c\udf0c☁⛅⛈\ud83c\udf24\ud83c\udf25\ud83c\udf26\ud83c\udf27\ud83c\udf28\ud83c\udf29\ud83c\udf2a\ud83c\udf2b\ud83c\udf2c\ud83c\udf00\ud83c\udf08\ud83c\udf02☂☔⛱⚡❄☃⛄\ud83c\udfd4⛰\ud83d\uddfb\ud83c\udf0b\ud83d\udd25\ud83d\udca7\ud83c\udf0a\ud83d\udca5\ud83d\udca6\ud83d\udca8\ud83c\udf47\ud83c\udf48\ud83c\udf49\ud83c\udf4a\ud83c\udf4b\ud83c\udf4c\ud83c\udf4d\ud83e\udd6d\ud83c\udf4e\ud83c\udf4f\ud83c\udf50\ud83c\udf51\ud83c\udf52\ud83c\udf53\ud83e\uded0\ud83e\udd5d\ud83c\udf45\ud83e\uded2\ud83e\udd65\ud83e\udd51\ud83c\udf46\ud83e\udd54\ud83e\udd55\ud83c\udf3d\ud83c\udf36\ud83e\uded1\ud83e\udd52\ud83e\udd6c\ud83e\udd66\ud83e\udedb\ud83e\uddc4\ud83e\uddc5\ud83e\udeda\ud83c\udf44\ud83e\uded8\ud83e\udd5c\ud83c\udf30\ud83c\udf5e\ud83e\udd50\ud83e\udd56\ud83e\uded3\ud83e\udd68\ud83e\udd6f\ud83e\udd5e\ud83e\uddc7\ud83e\uddc0\ud83c\udf56\ud83c\udf57\ud83e\udd69\ud83e\udd53\ud83c\udf54\ud83c\udf5f\ud83c\udf55\ud83c\udf2d\ud83e\udd6a\ud83c\udf2e\ud83c\udf2f\ud83e\uded4\ud83e\udd59\ud83e\uddc6\ud83e\udd5a\ud83c\udf73\ud83e\udd58\ud83c\udf72\ud83e\uded5\ud83e\udd63\ud83e\udd57\ud83c\udf7f\ud83e\uddc8\ud83e\uddc2\ud83e\udd6b\ud83c\udf71\ud83c\udf58\ud83c\udf59\ud83c\udf5a\ud83c\udf5b\ud83c\udf5c\ud83c\udf5d\ud83c\udf60\ud83c\udf62\ud83c\udf63\ud83c\udf64\ud83c\udf65\ud83e\udd6e\ud83c\udf61\ud83e\udd5f\ud83e\udd60\ud83e\udd61\ud83c\udf66\ud83c\udf67\ud83c\udf68\ud83c\udf69\ud83c\udf6a\ud83c\udf82\ud83c\udf70\ud83e\uddc1\ud83e\udd67\ud83c\udf6b\ud83c\udf6c\ud83c\udf6d\ud83c\udf6e\ud83c\udf6f\ud83c\udf7c\ud83e\udd5b\ud83e\uded7☕\ud83e\uded6\ud83c\udf75\ud83c\udf76\ud83c\udf7e\ud83c\udf77\ud83c\udf78\ud83c\udf79\ud83c\udf7a\ud83c\udf7b\ud83e\udd42\ud83e\udd43\ud83e\udd64\ud83e\uddcb\ud83e\uddc3\ud83e\uddc9\ud83e\uddca\ud83e\udd62\ud83c\udf7d\ud83c\udf74\ud83e\udd44\ud83d\udd2a⚽⚾\ud83e\udd4e\ud83c\udfc0\ud83c\udfd0\ud83c\udfc8\ud83c\udfc9\ud83c\udfbe\ud83e\udd4f\ud83c\udfb3\ud83c\udfcf\ud83c\udfd1\ud83c\udfd2\ud83e\udd4d\ud83c\udfd3\ud83c\udff8\ud83e\udd4a\ud83e\udd4b\ud83e\udd45⛳⛸\ud83c\udfa3\ud83e\udd3f\ud83c\udfbd\ud83c\udfbf\ud83d\udef7\ud83e\udd4c\ud83c\udfaf\ud83e\ude80\ud83e\ude81\ud83c\udfb1\ud83c\udf96\ud83c\udfc6\ud83c\udfc5\ud83e\udd47\ud83e\udd48\ud83e\udd49\ud83c\udfd4⛰\ud83c\udf0b\ud83d\uddfb\ud83c\udfd5\ud83c\udfd6\ud83c\udfdc\ud83c\udfdd\ud83c\udfdf\ud83c\udfdb\ud83c\udfd7\ud83e\uddf1\ud83e\udea8\ud83e\udeb5\ud83d\uded6\ud83c\udfd8\ud83c\udfda\ud83c\udfe0\ud83c\udfe1\ud83c\udfe2\ud83c\udfe3\ud83c\udfe4\ud83c\udfe5\ud83c\udfe6\ud83c\udfe8\ud83c\udfe9\ud83c\udfea\ud83c\udfeb\ud83c\udfec\ud83c\udfed\ud83c\udfef\ud83c\udff0\ud83d\udc92\ud83d\uddfc\ud83d\uddfd⛪\ud83d\udd4c\ud83d\uded5\ud83d\udd4d⛩\ud83d\udd4b⛲⛺\ud83c\udf01\ud83c\udf03\ud83c\udfd9\ud83c\udf04\ud83c\udf05\ud83c\udf06\ud83c\udf07\ud83c\udf09\ud83d\uddfe\ud83c\udfde\ud83c\udfa0\ud83c\udfa1\ud83c\udfa2\ud83d\udc88\ud83c\udfaa\ud83d\ude82\ud83d\ude83\ud83d\ude84\ud83d\ude85\ud83d\ude86\ud83d\ude87\ud83d\ude88\ud83d\ude89\ud83d\ude8a\ud83d\ude9d\ud83d\ude9e\ud83d\ude8b\ud83d\ude8c\ud83d\ude8d\ud83d\ude8e\ud83d\ude90\ud83d\ude91\ud83d\ude92\ud83d\ude93\ud83d\ude94\ud83d\ude95\ud83d\ude96\ud83d\ude97\ud83d\ude98\ud83d\ude99\ud83d\udefb\ud83d\ude9a\ud83d\ude9b\ud83d\ude9c\ud83c\udfce\ud83c\udfcd\ud83d\udef5\ud83e\uddbd\ud83e\uddbc\ud83d\udefa\ud83d\udeb2\ud83d\udef4\ud83d\udef9\ud83d\udefc\ud83d\ude8f\ud83d\udee3\ud83d\udee4\ud83d\udee2⛽\ud83d\udea8\ud83d\udea5\ud83d\udea6\ud83d\uded1\ud83d\udea7⚓⛵\ud83d\udef6\ud83d\udea4\ud83d\udef3⛴\ud83d\udee5\ud83d\udea2✈\ud83d\udee9\ud83d\udeeb\ud83d\udeec\ud83e\ude82\ud83d\udcba\ud83d\ude81\ud83d\ude9f\ud83d\udea0\ud83d\udea1\ud83d\udef0\ud83d\ude80\ud83d\udef8\ud83c\udf86\ud83c\udf87\ud83c\udf91\ud83d\uddff\ud83d\udece\ud83e\uddf3⌛⏳⌚⏰⏱⏲\ud83d\udd70\ud83c\udf21\ud83d\uddfa\ud83e\udded\ud83c\udf83\ud83c\udf84\ud83e\udde8\ud83c\udf88\ud83c\udf89\ud83c\udf8a\ud83c\udf8e\ud83e\udead\ud83c\udf8f\ud83c\udf90\ud83c\udf80\ud83c\udf81\ud83c\udf97\ud83c\udf9f\ud83c\udfab\ud83d\udd2e\ud83e\ude84\ud83e\uddff\ud83c\udfae\ud83d\udd79\ud83c\udfb0\ud83c\udfb2♟\ud83e\udde9\ud83e\uddf8\ud83e\ude85\ud83e\ude86\ud83d\uddbc\ud83c\udfa8\ud83e\uddf5\ud83e\udea1\ud83e\uddf6\ud83e\udea2\ud83d\udc53\ud83d\udd76\ud83e\udd7d\ud83e\udd7c\ud83e\uddba\ud83d\udc54\ud83d\udc55\ud83d\udc56\ud83e\udde3\ud83e\udde4\ud83e\udde5\ud83e\udde6\ud83d\udc57\ud83d\udc58\ud83e\udd7b\ud83e\ude71\ud83e\ude72\ud83e\ude73\ud83d\udc59\ud83d\udc5a\ud83d\udc5b\ud83d\udc5c\ud83d\udc5d\ud83d\udecd\ud83c\udf92\ud83e\ude74\ud83d\udc5e\ud83d\udc5f\ud83e\udd7e\ud83e\udd7f\ud83d\udc60\ud83d\udc61\ud83e\ude70\ud83d\udc62\ud83d\udc51\ud83d\udc52\ud83c\udfa9\ud83c\udf93\ud83e\udde2\ud83e\ude96⛑\ud83d\udcff\ud83d\udc84\ud83d\udc8d\ud83d\udc8e\ud83d\udce2\ud83d\udce3\ud83d\udcef\ud83c\udf99\ud83c\udf9a\ud83c\udf9b\ud83c\udfa4\ud83c\udfa7\ud83d\udcfb\ud83c\udfb7\ud83e\ude97\ud83c\udfb8\ud83c\udfb9\ud83c\udfba\ud83c\udfbb\ud83e\ude95\ud83e\ude88\ud83e\ude87\ud83e\udd41\ud83e\ude98\ud83e\udea9\ud83d\udcf1\ud83d\udcf2☎\ud83d\udcde\ud83d\udcdf\ud83d\udce0\ud83d\udd0b\ud83e\udeab\ud83d\udd0c\ud83d\udcbb\ud83d\udda5\ud83d\udda8⌨\ud83d\uddb1\ud83d\uddb2\ud83d\udcbd\ud83d\udcbe\ud83d\udcbf\ud83d\udcc0\ud83e\uddee\ud83c\udfa5\ud83c\udf9e\ud83d\udcfd\ud83c\udfac\ud83d\udcfa\ud83d\udcf7\ud83d\udcf8\ud83d\udcf9\ud83d\udcfc\ud83d\udd0d\ud83d\udd0e\ud83d\udd6f\ud83d\udca1\ud83d\udd26\ud83c\udfee\ud83e\ude94\ud83d\udcd4\ud83d\udcd5\ud83d\udcd6\ud83d\udcd7\ud83d\udcd8\ud83d\udcd9\ud83d\udcda\ud83d\udcd3\ud83d\udcd2\ud83d\udcc3\ud83d\udcdc\ud83d\udcc4\ud83d\udcf0\ud83d\uddde\ud83d\udcd1\ud83d\udd16\ud83c\udff7\ud83d\udcb0\ud83e\ude99\ud83d\udcb4\ud83d\udcb5\ud83d\udcb6\ud83d\udcb7\ud83d\udcb8\ud83d\udcb3\ud83e\udeaa\ud83e\uddfe✉\ud83d\udc8c\ud83d\udce7\ud83e\udde7\ud83d\udce8\ud83d\udce9\ud83d\udce4\ud83d\udce5\ud83d\udce6\ud83d\udceb\ud83d\udcea\ud83d\udcec\ud83d\udced\ud83d\udcee\ud83d\uddf3✏✒\ud83d\udd8b\ud83d\udd8a\ud83d\udd8c\ud83d\udd8d\ud83d\udcdd\ud83d\udcc1\ud83d\udcc2\ud83d\uddc2\ud83d\udcc5\ud83d\udcc6\ud83d\uddd2\ud83d\uddd3\ud83d\udcc7\ud83d\udcc8\ud83d\udcc9\ud83d\udcca\ud83d\udccb\ud83d\udccc\ud83d\udccd\ud83d\udcce\ud83d\udd87\ud83d\udccf\ud83d\udcd0✂\ud83d\uddc3\ud83d\uddc4\ud83d\uddd1\ud83d\udd12\ud83d\udd13\ud83d\udd0f\ud83d\udd10\ud83d\udd11\ud83d\udddd\ud83d\udd28\ud83e\ude93⛏⚒\ud83d\udee0\ud83d\udde1⚔\ud83d\udca3\ud83d\udd2b\ud83e\ude83\ud83c\udff9\ud83d\udee1\ud83e\ude9a\ud83d\udd27\ud83e\ude9b\ud83d\udd29⚙\ud83d\udddc⚖\ud83e\uddaf\ud83d\udd17⛓\ud83e\ude9d\ud83e\uddf0\ud83e\uddf2\ud83e\ude9c\ud83d\udedd\ud83d\udede\ud83e\uded9⚗\ud83e\uddea\ud83e\uddeb\ud83e\uddec\ud83d\udd2c\ud83d\udd2d\ud83d\udce1\ud83e\ude7b\ud83d\udc89\ud83e\ude78\ud83d\udc8a\ud83e\ude79\ud83e\ude7a\ud83e\ude7c\ud83d\udeaa\ud83d\uded7\ud83e\ude9e\ud83e\ude9f\ud83d\udecf\ud83d\udecb\ud83e\ude91\ud83e\udea4\ud83d\udebd\ud83e\udea0\ud83d\udebf\ud83d\udec1\ud83e\uddfc\ud83e\udee7\ud83e\ude92\ud83e\udeae\ud83e\uddf4\ud83e\uddf7\ud83e\uddf9\ud83e\uddfa\ud83e\uddfb\ud83e\udea3\ud83e\udea5\ud83e\uddfd\ud83e\uddef\ud83d\udedf\ud83d\uded2\ud83d\udeac⚰\ud83e\udea6⚱\ud83c\udffa\ud83e\udea7\ud83d\udd73\ud83d\udc98\ud83d\udc9d\ud83d\udc96\ud83d\udc97\ud83d\udc93\ud83d\udc9e\ud83d\udc95\ud83d\udc9f❣\ud83d\udc94❤\ud83e\udde1\ud83d\udc9b\ud83d\udc9a\ud83e\ude75\ud83d\udc99\ud83d\udc9c\ud83e\ude77\ud83e\udd0e\ud83d\udda4\ud83e\ude76\ud83e\udd0d❤\ud83d\udd25❤\ud83e\ude79\ud83d\udcaf♨\ud83d\udca2\ud83d\udcac\ud83d\udc41\ud83d\udde8\ud83d\uddef\ud83d\udcad\ud83d\udca4\ud83c\udf10♠♥♦♣\ud83c\udccf\ud83c\udc04\ud83c\udfb4\ud83c\udfad\ud83d\udd07\ud83d\udd08\ud83d\udd09\ud83d\udd0a\ud83d\udd14\ud83d\udd15\ud83c\udfbc\ud83c\udfb5\ud83c\udfb6\ud83d\udcb9\ud83c\udfe7\ud83d\udeae\ud83d\udeb0♿\ud83d\udeb9\ud83d\udeba\ud83d\udebb\ud83d\udebc\ud83d\udebe\ud83d\udec2\ud83d\udec3\ud83d\udec4\ud83d\udec5\ud83d\udedc⚠\ud83d\udeb8⛔\ud83d\udeab\ud83d\udeb3\ud83d\udead\ud83d\udeaf\ud83d\udeb1\ud83d\udeb7\ud83d\udcf5\ud83d\udd1e☢☣⬆↗➡↘⬇↙⬅↖↕↔↩↪⤴⤵\ud83d\udd03\ud83d\udd04\ud83d\udd19\ud83d\udd1a\ud83d\udd1b\ud83d\udd1c\ud83d\udd1d☯\ud83d\udd00\ud83d\udd01\ud83d\udd02▶⏩⏭⏯◀⏪⏮\ud83d\udd3c⏫\ud83d\udd3d⏬⏸⏹⏺⏏\ud83c\udfa6\ud83d\udd05\ud83d\udd06\ud83d\udcf6\ud83d\udcf3\ud83d\udcf4⚧✖➕➖➗\ud83d\udff0♾‼⁉❓❕〰\ud83d\udcb1\ud83d\udcb2⚕♻⚜\ud83d\udd31\ud83d\udcdb\ud83d\udd30⭕✅☑✔❌❎➰➿〽✳✴❇©®™#\ud83d\udd20\ud83d\udd21\ud83d\udd22\ud83d\udd23\ud83d\udd24\ud83c\udd70\ud83c\udd8e\ud83c\udd71\ud83c\udd91\ud83c\udd92\ud83c\udd93ℹ\ud83c\udd94Ⓜ\ud83c\udd95\ud83c\udd96\ud83c\udd7e\ud83c\udd97\ud83c\udd7f\ud83c\udd98\ud83c\udd99\ud83c\udd9a\ud83c\ude01\ud83d\udd34\ud83d\udfe0\ud83d\udfe1\ud83d\udfe2\ud83d\udd35\ud83d\udfe3\ud83d\udfe4⚫⚪\ud83d\udfe5\ud83d\udfe7\ud83d\udfe8\ud83d\udfe9\ud83d\udfe6\ud83d\udfea\ud83d\udfeb⬛⬜◼◻◾◽▪▫\ud83d\udd36\ud83d\udd37\ud83d\udd38\ud83d\udd39\ud83d\udd3a\ud83d\udd3b\ud83d\udca0\ud83d\udd18\ud83d\udd33\ud83d\udd32\ud83d\udd5b\ud83d\udd67\ud83d\udd50\ud83d\udd5c\ud83d\udd51\ud83d\udd5d\ud83d\udd52\ud83d\udd5e\ud83d\udd53\ud83d\udd5f\ud83d\udd54\ud83d\udd60\ud83d\udd55\ud83d\udd61\ud83d\udd56\ud83d\udd62\ud83d\udd57\ud83d\udd63\ud83d\udd58\ud83d\udd64\ud83d\udd59\ud83d\udd65\ud83d\udd5a\ud83d\udd66☹☻☺ツ☚☛☜☝☞☟✍✎✌❤❥♥♡❣♨☠☮☯☪☀☣☢☭♏♒♈☂☃☁♔♕♚۩♛✿❀❃❂❁♠♤♣♧⚜™®©₪★☆✮✯✪✣✤✥✲❈☄✦❉✧♱♰๑☿⋄⋅⋆⋇☼*✖✗✘✕✓✔ღ✄✂☎☏✆✉♪♩♫♬♭❝❞‘ﾟ.･‖﹉﹊﹍﹎︱︳︴﹏﹋﹌▁┠┨┯┷┏┓﹃﹄┗┛┳⊥╝╚╔╗╬═╓╩▪▫□〓≡▬▂▃▄■▀▢▅▆▇▌▐█▓▒░┇┅✚▣▧▨▤▥▦▩回ஐ⋖⋗▲△▼♢♦▽Δ►◄⇨◈◆◇◊⋘⋙⋚⋛⋜⋝⋞⋟⋩⋪⋫⋬⋭⋈⋉⋊⋋⋌⋍⋎⋏⋐⋑⋒⋓⋔⋕∵∴⋮⋯【】⊶╯ぃ↔↕↑↓→←↘↙➹ψ♆◠◡┌┐└┘∟「」◯●◕◐◑○◔⊙◎㊚㊛¤㊣∞☾☽◘◙の➀➁➂➃➄➅➆➇➈➉ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓤⓥⓦⓧⓨⓩ½⅓⅔¼¾⅛⅜⅝⅞℅");
      this.getEmojis().addAll(splitByUnicode(replacerRaw));
      configReader.save();
    }
  }

  private static List<String> splitByUnicode(String string) {
    ArrayList<String> arrayList = new ArrayList();
    int value = 0;

    while (value < string.length()) {
      int codePoint = string.codePointAt(value);
      arrayList.add(new String(Character.toChars(codePoint)));
      value += Character.charCount(codePoint);
    }

    return arrayList;
  }

  public void loadCommandFilters() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Command.CommandFilter.Duplicate.Use",
        new String[] {
          "When set to true, plugin will prevent spaming of same or similar command in short time"
              + " range. Can be bypased with cmi.commandfilter.bypass permission"
        });
    this.preventCommandDoublicates = configReader.get("Command.CommandFilter.Duplicate.Use", false);
    configReader.addComment(
        "Command.CommandFilter.Priority",
        new String[] {
          "Priority of command spam listener",
          "Changed priority will only take effect on full server restart",
          "Possible options: " + CMIEventPriority.listGeneralPriorities(),
          "Default: LOW"
        });
    this.commandSpamPriority =
        CMIEventPriority.getPriority(configReader.get("Command.CommandFilter.Priority", "LOW"));
    configReader.addComment(
        "Command.CommandFilter.Duplicate.Percentage",
        new String[] {"How much in percentage command is counted as same"});
    this.similarityComamndPercentage =
        configReader.get("Command.CommandFilter.Duplicate.Percentage", 80);
    if (this.similarityComamndPercentage > 1.0) {
      this.similarityComamndPercentage /= 100.0;
    }

    configReader.addComment(
        "Command.CommandFilter.Duplicate.Interval",
        new String[] {"Defines how often in seconds you can send same/similar commands"});
    this.douplicateCommandInterval =
        configReader.get("Command.CommandFilter.Duplicate.Interval", 5);
    configReader.addComment(
        "Command.CommandFilter.Duplicate.MinAmount",
        new String[] {"How many commands you can repeat before stopped for cooldown"});
    this.douplicateCommandMinAmount =
        configReader.get("Command.CommandFilter.Duplicate.MinAmount", 2);
    configReader.addComment(
        "Command.CommandFilter.Duplicate.WhiteList",
        new String[] {"Whitelisted commands to ignore"});
    this.douplicateCommandWhiteList =
        configReader.get(
            "Command.CommandFilter.Duplicate.WhiteList",
            Arrays.asList("msg", "tell", "login", "register"));
  }

  public boolean isCancelCapEvent() {
    return this.cancelEvent;
  }

  public boolean isCapToLowerCase() {
    return this.lowerCase;
  }

  public CMIEventPriority getCapsCheckPriority() {
    return this.capsCheckPriority;
  }

  public CMIEventPriority getFilterCheckPriority() {
    return this.filterCheckPriority;
  }

  public CMIEventPriority getSimpleReplacerPriority() {
    return this.simpleReplacerPriority;
  }

  public CMIEventPriority getCommandSpamPriority() {
    return this.commandSpamPriority;
  }

  public CMIEventPriority getChatSpamPriority() {
    return this.chatSpamPriority;
  }

  public CMIEventPriority getSignAdvertisementPriority() {
    return this.signAdPriority;
  }

  public HashMap<String, String> getEmojiReplacerMap() {
    return this.simpleReplacerMap;
  }

  public List<String> getEmojis() {
    return this.emojis;
  }
}
