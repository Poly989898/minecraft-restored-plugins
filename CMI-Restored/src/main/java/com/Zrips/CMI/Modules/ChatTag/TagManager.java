package com.Zrips.CMI.Modules.ChatTag;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TagManager {
  private CMI plugin;
  private boolean ChatTagEnabled;
  private boolean HardCoreMode;
  private boolean OnlyWhenAfk;
  private String ChatTagColor;
  private boolean ChatTagRemoveEta;
  List<String> commands = new ArrayList<>();

  public TagManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void load(ConfigReader configReader) {
    configReader.addComment(
        "Chat.Tag.Enabled",
        new String[] {
          "Enable or not tag system. This will inform player with his name mentioning in public"
              + " chat if name have @ in front of it"
        });
    this.ChatTagEnabled = configReader.get("Chat.Tag.Enabled", true);
    configReader.addComment(
        "Chat.Tag.HardCoreMode",
        new String[] {
          "When this is set to true, any player mentionings in public messages will be colorized"
              + " and player will get informed as usual",
          "This is allot more heavier on server than usual tagging with @, so enable if you know"
              + " what you are doing"
        });
    this.HardCoreMode = configReader.get("Chat.Tag.HardCoreMode", false);
    configReader.addComment(
        "Chat.Tag.Color",
        new String[] {
          "Determines color of taged user name in chat with @ in front of name/nickname. Sender"
              + " should have cmi.tag.color"
        });
    this.ChatTagColor = configReader.get("Chat.Tag.Color", "&c");
    this.commands.clear();
    if (configReader.getC().isString("Chat.Tag.Sound.Name")) {
      String string =
          configReader.getC().get("Chat.Tag.Sound.Name", "BLOCK_NOTE_BLOCK_HARP")
              + ":"
              + configReader.getC().get("Chat.Tag.Sound.Pitch", 3)
              + ":"
              + configReader.getC().get("Chat.Tag.Sound.Volume", 1)
              + " [playerName] -s";
      this.commands.add("asConsole! cmi sound " + string);
    } else {
      this.commands.add("asConsole! cmi sound BLOCK_NOTE_BLOCK_HARP:3:1 [playerName] -s");
    }

    configReader.addComment(
        "Chat.Tag.CommandsOnTag",
        new String[] {
          "Commands to be performed when player is tagged",
          "Variables like [playerName] will be replaced with tagged player name",
          "You can use [senderName] to include player name who tagged"
        });
    var list = configReader.get("Chat.Tag.CommandsOnTag", this.commands);
    this.commands.clear();
    this.commands.addAll(list);
    configReader.addComment(
        "Chat.Tag.OnlyWhenAfk",
        new String[] {"Will performd tag commands only when player is afk"});
    this.OnlyWhenAfk =
        configReader.get(
            "Chat.Tag.OnlyWhenAfk",
            configReader.getC().getBoolean("Chat.Tag.Sound.OnlyWhenAfk", false));
    configReader.addComment(
        "Chat.Tag.RemoveEta", new String[] {"When set to true, @ simbol will be removed"});
    this.ChatTagRemoveEta = configReader.get("Chat.Tag.RemoveEta", false);
  }

  @Deprecated
  public String tag(Player player, String string, String fallbackText) {
    return this.tag(player, string, fallbackText, true);
  }

  public String tag(Player player, String string, String fallbackText, boolean flag) {
    return this.tag(CMIUser.getUser(player), string, fallbackText, flag);
  }

  public String tag(CMIUser cmiUser, String string, String fallbackText, boolean flag) {
    if (cmiUser == null) {
      return string;
    }

    String stripped = string;

    for (int value = 0;
        stripped.startsWith("§") && value < 20 && stripped.length() > 2;
        stripped = stripped.substring(2, stripped.length())) {
      value++;
    }

    String[] strings;
    int wordCount = (strings = stripped.split(" ")).length;

    for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
      String word = strings[wordIndex];
      if (word.startsWith("@") || this.plugin.getTagManager().isHardCoreMode()) {
        String targetName = word;
        if (word.startsWith("@")) {
          targetName = word.substring(1, word.length());
        }

        targetName = targetName.replaceAll("[.?!,;:]$", "");
        if (!targetName.isEmpty()) {
          Player player = Bukkit.getPlayerExact(targetName);
          boolean lastCharTrimmed = false;
          if (player == null) {
            CMIUser targetUser = CMIUser.getUser(targetName);
            if (targetUser == null) {
              continue;
            }

            String nickName = targetUser.getNickName();
            if (!targetUser.isOnline()
                || nickName == null
                || !nickName.equalsIgnoreCase(targetName)) {
              continue;
            }

            player = targetUser.getPlayer();
          }

          if (player == null) {
            String nameTrimmed = targetName.substring(0, targetName.length() - 1);
            player = Bukkit.getPlayerExact(nameTrimmed);
            if (player == null) {
              continue;
            }

            lastCharTrimmed = true;
          }

          if (!player.getName().equals(cmiUser.getName())) {
            CMIUser taggedUser = this.plugin.getPlayerManager().getUser(player);
            if (taggedUser != null && !taggedUser.isVanished() && taggedUser.getName() != null) {
              if (lastCharTrimmed) {
                word = word.substring(0, word.length() - 1);
              }

              String tagName = word;
              if (this.plugin.getTagManager().isChatTagRemoveEta()) {
                tagName = tagName.replace("@", "");
              }

              String[] parts = string.split(Pattern.quote(word), 2);
              String colorPrefix = CMIChatColor.getLastColors(parts.length > 0 ? parts[0] : "");
              if (colorPrefix.isEmpty()) {
                colorPrefix = CMIChatColor.translate(CMIChatColor.getLastColors(fallbackText));
                if (colorPrefix.isEmpty()) {
                  colorPrefix = "&r";
                }
              }

              colorPrefix = colorPrefix.replace("&", "§");
              String tagColor = "";
              if (cmiUser.isOnline()
                  && PermissionsManager.CMIPerm.tag_color.hasPermission(cmiUser.getPlayer())) {
                tagColor = this.plugin.getTagManager().getChatTagColor();
              }

              tagColor = tagColor.replace("&", "§");
              string = string.replace(word, tagColor + tagName + colorPrefix);
              if (flag
                  && taggedUser.isOnline()
                  && taggedUser.getOptionState(PlayerOption.tagSound)
                  && this.isChatTagEnabled()
                  && !taggedUser.isIgnoring(cmiUser.getUniqueId())) {
                try {
                  if (!this.OnlyWhenAfk || this.OnlyWhenAfk && taggedUser.isAfk()) {
                    var list =
                        this.plugin
                            .getLM()
                            .updateSnd(
                                new Snd().setTarget(player).setSender(cmiUser),
                                new ArrayList<>(this.commands));
                    CMIScheduler.runTask(
                        this.plugin,
                        () ->
                            this.plugin
                                .getSpecializedCommandManager()
                                .processCmds(
                                    list, cmiUser.isOnline() ? cmiUser.getPlayer() : null));
                  }
                } catch (Exception exception) {
                }
              }
            }
          }
        }
      }
    }

    return string;
  }

  public String getChatTagColor() {
    return CMIChatColor.translate(this.ChatTagColor);
  }

  public boolean isChatTagRemoveEta() {
    return this.ChatTagRemoveEta;
  }

  public boolean isChatTagEnabled() {
    return this.ChatTagEnabled;
  }

  public boolean isHardCoreMode() {
    return this.HardCoreMode;
  }

  public void setHardCoreMode(boolean flag) {
    this.HardCoreMode = flag;
  }
}
