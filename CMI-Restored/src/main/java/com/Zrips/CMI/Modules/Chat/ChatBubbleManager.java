package com.Zrips.CMI.Modules.Chat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Containers.CMIEventPriority;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChatBubbleManager {
  private HashMap<UUID, CMIChatBubble> bubbles = new HashMap<>();
  private CMI plugin;
  private boolean publicMessages = true;
  private CMIEventPriority priority = CMIEventPriority.MONITOR;
  private double offset = 2.5;
  private double baseShowFor = 5.0;
  private double maxShowFor = 8.0;
  private double extraTime = 0.02;
  private double scale = 0.5;
  private boolean shadow = true;
  private int range = 32;
  private Color backgroundColor;
  private int LineWidth = 255;

  public ChatBubbleManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig(ConfigReader configReader) {
    if (Version.isCurrentEqualOrLower(Version.v1_19_R2)) {
      configReader.addComment(
          "ChatBubble", new String[] {" ", "ONLY works for servers 1.19.4 and above!", " "});
    }

    configReader.addComment(
        "ChatBubble.PublicMessages",
        new String[] {
          "Do you want to enable chat bubbles for public messages",
          "Each player can disable this with /options command personally"
        });
    this.publicMessages = configReader.get("ChatBubble.PublicMessages", true);
    configReader.addComment(
        "ChatBubble.Priority",
        new String[] {
          "Priority of chat bubble listener",
          "Changed priority will only take effect on full server restart",
          "Possible options: " + CMIEventPriority.listPriorities(),
          "Default: MONITOR"
        });
    this.priority =
        CMIEventPriority.getPriority(configReader.get("ChatBubble.Priority", "MONITOR"));
    configReader.addComment(
        "ChatBubble.Offset", new String[] {"How high we should show chat bubble"});
    this.offset = configReader.get("ChatBubble.Offset", 2.5);
    configReader.addComment(
        "ChatBubble.ShowFor", new String[] {"Base time in seconds for chat bubble to be visible"});
    this.baseShowFor = configReader.get("ChatBubble.ShowFor", 5);
    configReader.addComment(
        "ChatBubble.MaxShowFor", new String[] {"Maximum time bubble will be shown for"});
    this.maxShowFor = configReader.get("ChatBubble.MaxShowFor", 10);
    configReader.addComment(
        "ChatBubble.ExtraTime",
        new String[] {
          "Extra seconds to be added for each letter in a message",
          "This will allow to have shorter times for short messages and increase it for longer ones"
        });
    this.extraTime = configReader.get("ChatBubble.ExtraTime", 0.02);
    configReader.addComment(
        "ChatBubble.PlateScale", new String[] {"Scale of chat bubble, default 1"});
    this.scale = configReader.get("ChatBubble.PlateScale", 1);
    configReader.addComment(
        "ChatBubble.LineWidth", new String[] {"Line width before wraping text to new line"});
    this.LineWidth = configReader.get("ChatBubble.LineWidth", 255);
    configReader.addComment(
        "ChatBubble.Background.Color",
        new String[] {
          "Default Color code, this could be overriden if text itself was colorized",
          "Leave it empty for default transparent color",
          "Example of color code: {#black}"
        });
    CMIChatColor cmiChatColor =
        CMIChatColor.getColor(configReader.get("ChatBubble.Background.Color", "{#black}"));
    if (cmiChatColor != null) {
      this.backgroundColor = cmiChatColor.getRGBColor();
    }

    if (this.backgroundColor == null) {
      this.backgroundColor = Color.fromARGB(122, 0, 0, 0);
    }

    if (Version.isCurrentHigher(Version.v1_19_R2)) {
      configReader.addComment(
          "ChatBubble.Background.Opacity",
          new String[] {
            "Value between 0 and 1 where 0 means no background color and 1 is fully opaque"
          });
      int value =
          CMINumber.clamp(
              (int) (configReader.get("ChatBubble.Background.Opacity", 0.5) * 255.0), 0, 255);
      if (this.backgroundColor != null) {
        this.backgroundColor =
            Color.fromARGB(
                value,
                this.backgroundColor.getRed(),
                this.backgroundColor.getGreen(),
                this.backgroundColor.getBlue());
      }
    }

    configReader.addComment("ChatBubble.Shadow", new String[] {"Do you want to add text shadow"});
    this.shadow = configReader.get("ChatBubble.Shadow", true);
  }

  public void clearCache(UUID uuid) {
    this.bubbles.remove(uuid);
  }

  public CMIChatBubble getBubble(UUID uuid) {
    return this.bubbles.get(uuid);
  }

  public void showBubble(Player player, String string, Set<Player> set) {
    this.showBubble(player, CMIComponentBase.quickAdd(string), set);
  }

  public void showBubble(Player player, CMIComponentBase cmiComponentBase, Set<Player> set) {
    if (this.publicMessages && !Version.isCurrentEqualOrLower(Version.v1_19_R2)) {
      set = new HashSet(set);
      if (player.getGameMode().equals(GameMode.SPECTATOR)) {
        for (Player viewer : new HashSet<>(set)) {
          if (!viewer.getGameMode().equals(GameMode.SPECTATOR)) {
            set.remove(viewer);
          }
        }
      }

      CMIChatBubble cmiChatBubble = this.bubbles.get(player.getUniqueId());
      if (!set.isEmpty()) {
        if (cmiChatBubble != null) {
          cmiChatBubble.delete();
        }

        try {
          CMIChatBubble chatBubble = new CMIChatBubble(player, cmiComponentBase, set);
          chatBubble.show();
          this.bubbles.put(player.getUniqueId(), chatBubble);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
  }

  public double getOffset() {
    return this.offset;
  }

  public double getShowFor(String string) {
    double number = this.baseShowFor + string.length() * this.extraTime;
    return CMINumber.clamp(number, number, (int) this.maxShowFor);
  }

  public boolean inRange(Location location, Location otherLocation) {
    return location.getWorld().equals(otherLocation.getWorld())
        && !(Math.abs(location.getX() - otherLocation.getX()) > 32.0)
        && !(Math.abs(location.getY() - otherLocation.getY()) > 32.0)
        && !(Math.abs(location.getZ() - otherLocation.getZ()) > 32.0);
  }

  public double getScale() {
    return this.scale;
  }

  public int getLineWidth() {
    return this.LineWidth;
  }

  public Color getBackgroundColor() {
    return this.backgroundColor;
  }

  public boolean isShadow() {
    return this.shadow;
  }

  public CMIEventPriority getPriority() {
    return this.priority;
  }
}
