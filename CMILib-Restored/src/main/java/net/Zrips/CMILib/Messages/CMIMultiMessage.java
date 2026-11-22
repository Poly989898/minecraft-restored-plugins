package net.Zrips.CMILib.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Advancements.AdvancementFrameType;
import net.Zrips.CMILib.Advancements.CMIAdvancement;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMICommandSender;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.Language;
import net.Zrips.CMILib.Locale.Snd;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMIMultiMessage {
  private boolean updateSnd = false;
  private boolean translateColors = true;
  private boolean translatePlaceholders = true;
  private boolean filterNewLine = true;
  private CMIMultiMessageType type = null;
  private String message;
  private List<Object> extra;

  public CMIMultiMessage(String msg) {
    for (CMIMultiMessageType one : CMIMultiMessageType.values()) {
      Pattern pattern = one.getRegex();
      Matcher match = pattern.matcher(msg);
      if (match.find()) {
        this.type = one;
        switch (one) {
          case actionBar:
          case broadcast:
            msg = msg.substring(match.group().length());
            this.message = msg;
            break;
          case toast:
            msg = msg.substring(match.group().length());
            this.extra = new ArrayList<>();
            if (match.group(2) != null) {
              AdvancementFrameType frame = AdvancementFrameType.getFromString(match.group(3));
              if (frame != null) {
                this.extra.add(frame);
              }
            }

            if (match.group(4) != null) {
              CMIMaterial material = CMIMaterial.get(match.group(5));
              if (material.isValidItem()) {
                this.extra.add(material);
              }
            }

            if (msg.startsWith(" ")) {
              msg = msg.substring(1);
            }

            this.message = msg;
            break;
          case bossBar:
            msg = msg.substring(match.group().length());
            this.extra = new ArrayList<>();
            this.extra.add(match.group(2));
            if (match.group(4) != null && !match.group(4).isEmpty()) {
              this.extra.add(match.group(4));
            }

            this.message = msg;
            break;
          case customText:
          case timedActionBar:
            msg = msg.substring(match.group().length());
            this.extra = new ArrayList<>();
            this.extra.add(match.group(2));
            this.message = msg;
            break;
          case json:
          case plain:
            this.message = msg;
            break;
          case title:
            String title = match.group(6);
            if (title != null && !title.isEmpty()) {
              this.message = title;
              this.extra = new ArrayList<>();
              this.extra.add(match.group(8));
            } else {
              this.message = match.group(8);
            }

            String time = match.group(3);
            if (time != null) {
              if (this.extra == null) {
                this.extra = new ArrayList<>();
                this.extra.add(null);
              }

              this.extra.add(time);
            }
        }
      }
    }

    if (this.type == null) {
      this.message = msg;
      this.type = CMIMultiMessageType.plain;
    }
  }

  public boolean show(CMICommandSender sender) {
    return this.show(sender.getSender());
  }

  public boolean show(CommandSender sender) {
    if (this.message.isEmpty()) {
      return false;
    }

    if (this.isFilterNewLine()) {
      CMILib.getInstance().getLM();
      this.message = Language.filterNewLine(this.message);
    }

    if (this.isTranslateColors()) {
      this.message = CMIChatColor.translate(this.message);
    }

    if (this.isUpdateSnd()) {
      this.message =
          CMILib.getInstance()
              .getLM()
              .updateSnd(new Snd().setSender(sender).setTarget(sender), this.message);
    }

    if (sender instanceof Player && this.isTranslatePlaceholders()) {
      this.message =
          CMILib.getInstance()
              .getPlaceholderAPIManager()
              .updatePlaceHolders((Player) sender, this.message);
    }

    switch (this.type) {
      case actionBar:
        CMIActionBar.send(sender, this.message);
        break;
      case broadcast:
        CMIMultiMessage sub = new CMIMultiMessage(this.message);
        switch (sub.getType()) {
          case customText:
            for (Player one : Bukkit.getOnlinePlayers()) {
              sub.show(one);
            }

            return true;
          default:
            if (!(sender instanceof Player)) {
              sender.sendMessage(this.message);
            }

            CMIMessages.broadcastMessage(sender, this.message);
            return true;
        }
      case toast:
        if (!(sender instanceof Player)) {
          return false;
        }

        if (Version.isCurrentLower(Version.v1_12_R1)) {
          sender.sendMessage(this.message);
        } else {
          CMIMaterial material = CMIMaterial.BLACK_STAINED_GLASS_PANE;
          AdvancementFrameType frametype = AdvancementFrameType.CHALLENGE;
          if (this.extra != null) {
            for (Object oneS : this.extra) {
              if (oneS instanceof CMIMaterial) {
                material = (CMIMaterial) oneS;
              } else if (oneS instanceof AdvancementFrameType) {
                frametype = (AdvancementFrameType) oneS;
              }
            }
          }

          new CMIAdvancement()
              .setFrame(frametype)
              .setIcon(material)
              .setTitle(this.message)
              .show((Player) sender);
        }
        break;
      case bossBar:
        if (!(sender instanceof Player)) {
          return false;
        }

        String bName = (String) this.getExtra().get(0);
        int keepfor = 30;
        if (this.getExtra().size() > 1) {
          try {
            keepfor = (int) (Double.parseDouble((String) this.getExtra().get(1)) * 20.0);
          } catch (Throwable var17) {
          }
        }

        BossBarInfo barInfo = new BossBarInfo((Player) sender, bName);
        barInfo.setKeepForTicks(keepfor);
        barInfo.setTitleOfBar(this.message);
        CMILib.getInstance().getBossBarManager().addBossBar((Player) sender, barInfo);
        break;
      case customText:
      case plain:
      default:
        if (sender != null) {
          if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && sender instanceof Player) {
            RawMessage rmx = new RawMessage();
            rmx.setDontBreakLine(!this.isFilterNewLine());
            rmx.addText(this.message);
            rmx.show(sender);
          } else {
            if (Version.isCurrentLower(Version.v1_16_R1)) {
              this.message = CMIChatColor.stripHexColor(this.message);
            }

            sender.sendMessage(this.message);
          }
        }
        break;
      case timedActionBar:
        if (!(sender instanceof Player)) {
          return false;
        }

        int ime = 0;

        try {
          ime = Integer.parseInt((String) this.getExtra().get(0));
        } catch (Throwable var16) {
        }

        CMIActionBar.send((Player) sender, this.message, ime);
        break;
      case json:
        RawMessage rm = RawMessage.translateTextOnlyRawMessage(this.message);
        CMIScheduler.runTask(CMILib.getInstance(), () -> rm.show(sender));
        break;
      case title:
        String title = this.message;
        String subtitle = this.getExtra().isEmpty() ? null : (String) this.getExtra().get(0);
        int keepTime = 40;
        if (this.getExtra().size() > 1) {
          try {
            keepTime = (int) (Double.parseDouble((String) this.getExtra().get(1)) * 20.0);
          } catch (Throwable var15) {
          }
        }

        if (sender instanceof Player) {
          CMITitleMessage.send((Player) sender, title, subtitle, 0, keepTime, 10);
        }
    }

    return true;
  }

  public boolean isTranslateColors() {
    return this.translateColors;
  }

  public void setTranslateColors(boolean translateColors) {
    this.translateColors = translateColors;
  }

  public boolean isUpdateSnd() {
    return this.updateSnd;
  }

  public void setUpdateSnd(boolean updateSnd) {
    this.updateSnd = updateSnd;
  }

  public CMIMultiMessageType getType() {
    return this.type;
  }

  public String getMessage() {
    return this.message;
  }

  public List<Object> getExtra() {
    if (this.extra == null) {
      this.extra = new ArrayList<>();
    }

    return this.extra;
  }

  public boolean isMultiType() {
    return this.type != null && !this.type.equals(CMIMultiMessageType.plain);
  }

  public boolean isTranslatePlaceholders() {
    return this.translatePlaceholders;
  }

  public void setTranslatePlaceholders(boolean translatePlaceholders) {
    this.translatePlaceholders = translatePlaceholders;
  }

  public void setType(CMIMultiMessageType type) {
    this.type = type;
  }

  public boolean isFilterNewLine() {
    return this.filterNewLine;
  }

  public void setFilterNewLine(boolean filterNewLine) {
    this.filterNewLine = filterNewLine;
  }
}
