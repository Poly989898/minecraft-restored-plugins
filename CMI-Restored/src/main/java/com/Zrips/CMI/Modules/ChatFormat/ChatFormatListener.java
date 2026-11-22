package com.Zrips.CMI.Modules.ChatFormat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordManager;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.commands.list.staffmsg;
import com.Zrips.CMI.commands.list.vanish;
import com.Zrips.CMI.utils.Util;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFormatListener implements Listener {
  private CMI plugin;
  private ChatFilterRule whiteRules = new ChatFilterRule();
  public static HashMap<UUID, Long> shouts = new HashMap<>();

  public ChatFormatListener(CMI cmi) {
    this.plugin = cmi;
    this.whiteRules.setPattern("(Conversion = ')(.)(')");
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) -> onAsyncPlayerChatRoomEvent((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) -> this.onAsyncPlayerStaffRoomEvent((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) -> this.onAsyncPlayerChatEvent((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) -> this.AsyncPlayerChatEventMute((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) -> this.AsyncPlayerChatEventShadowMute((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatManager().getFormatPublicMessagesPriority().getPriority(),
            (listener, event) ->
                this.AsyncPlayerChatEventPersonalMute((AsyncPlayerChatEvent) event),
            cmi,
            true);
  }

  private static void onAsyncPlayerChatRoomEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      CMIUser cmiUser = CMIUser.getUser(player);
      if (cmiUser != null && cmiUser.getChatRoom() != null) {
        if (!ChatFormatManager.ChatRoomShout.isEmpty()
            && asyncPlayerChatEvent.getMessage().startsWith(ChatFormatManager.ChatRoomShout)
            && !cmiUser.getChatRoom().isLocked()) {
          asyncPlayerChatEvent.setMessage(
              asyncPlayerChatEvent
                  .getMessage()
                  .substring(ChatFormatManager.ChatRoomShout.length()));
        } else {
          CMIScheduler.runTask(
              CMI.getInstance(),
              () ->
                  cmiUser
                      .getChatRoom()
                      .broadcastMessage(cmiUser, asyncPlayerChatEvent.getMessage()));
          asyncPlayerChatEvent.setCancelled(true);
        }
      }
    }
  }

  private void onAsyncPlayerStaffRoomEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      if (this.plugin.getChatFormatManager().getStaffChats().contains(player.getUniqueId())) {
        if (!asyncPlayerChatEvent.getMessage().startsWith("!")) {
          CMIScheduler.runTask(
              CMI.getInstance(),
              () ->
                  this.plugin
                      .getCommandManager()
                      .getCommands()
                      .get(staffmsg.class.getSimpleName())
                      .getCmdClass()
                      .perform(this.plugin, player, asyncPlayerChatEvent.getMessage().split(" ")));
          asyncPlayerChatEvent.setCancelled(true);
        } else {
          asyncPlayerChatEvent.setMessage(asyncPlayerChatEvent.getMessage().substring(1));
        }
      }
    }
  }

  private void onAsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      if (this.plugin.getChatManager().isModifyChatFormat()) {
        Player player = asyncPlayerChatEvent.getPlayer();
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          cmiUser.updateDisplayName();
          String string = this.plugin.getChatFormatManager().getGroupFormat(player);
          string = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, string);
          string = string.replace("{displayName}", cmiUser.getDisplayName());
          string = string.replace("{name}", cmiUser.getName());

          try {
            string = string.replace("{group}", cmiUser.getGroupName());
          } catch (Exception exception) {
            string = string.replace("{group}", "");
          }

          string = string.replace("{world}", player.getWorld().getName());
          string = string.replace("{prefix}", cmiUser.getPrefix());
          string = string.replace("{suffix}", cmiUser.getSuffix());
          string =
              string.replace(
                  "{nicknameprefix}",
                  CMI.getInstance().getChatFormatManager().getNickNamePrefix(cmiUser));
          if (string.contains("{message}")) {
            string = string.replace("{message}", "%2$s");
          } else {
            string = string + "%2$s";
          }

          string = string.replace("{discord}", "");
          if (this.plugin.getChatFormatManager().getChatGeneralRange(player.getWorld()) > 0
              && (!PermissionsManager.CMIPerm.chat_rangebypass.hasSetPermission(player)
                  || PermissionsManager.CMIPerm.chat_rangespy.hasSetPermission(player))) {
            int value = this.plugin.getChatFormatManager().getChatGeneralRange(player.getWorld());
            if (asyncPlayerChatEvent.getMessage().startsWith("!")
                && PermissionsManager.CMIPerm.chat_shout.hasSetPermission(player)) {
              if (this.plugin.getChatFormatManager().getChatShoutCost() <= 0
                  || cmiUser.hasMoney(
                      this.plugin.getChatFormatManager().getChatShoutCost().doubleValue())) {
                if (this.plugin.getChatFormatManager().getChatShoutCost().doubleValue() > 0.0) {
                  cmiUser.withdraw(
                      this.plugin.getChatFormatManager().getChatShoutCost().doubleValue());
                  CMILC.Chat_shoutDeduction.sendMessage(
                      player,
                      "[amount]",
                      this.plugin
                          .getEconomyManager()
                          .format(
                              this.plugin.getChatFormatManager().getChatShoutCost().doubleValue()));
                }

                value = this.plugin.getChatFormatManager().getChatShoutRange();
                String message = asyncPlayerChatEvent.getMessage().substring(1);
                if (message.startsWith(" ")) {
                  message = message.substring(1);
                }

                asyncPlayerChatEvent.setMessage(message);
                if (!this.plugin.getChatManager().isDiscordSRVRanged()) {
                  shouts.put(cmiUser.getUniqueId(), System.currentTimeMillis());
                }
              }

              if (string.contains("{shout}")) {
                string = string.replace("{shout}", CMILC.Chat_shoutPrefix.getLocale());
              } else {
                string = CMILC.Chat_shoutPrefix.getLocale() + string;
              }

              string =
                  string.replace(
                      "{shoutcolor}", this.plugin.getChatFormatManager().getChatShoutColor());
            } else {
              if (string.contains("{shout}")) {
                string = string.replace("{shout}", CMILC.Chat_localPrefix.getLocale());
              } else {
                string = CMILC.Chat_localPrefix.getLocale() + string;
              }

              if (this.plugin.getBungeeCordManager().isBungeeCord()
                  && this.plugin.getChatFormatManager().isBungeePublicMessages()) {
                BungeeCordManager.localRangedMessage = player.getUniqueId();
                BungeeCordManager.localRangedMessageTime = System.currentTimeMillis() + 50L;
              }
            }

            HashSet<Player> hashSet = new HashSet<>(asyncPlayerChatEvent.getRecipients());
            int count = 0;

            for (Player recipient : hashSet) {
              Boolean sendToAll = null;
              if (!recipient.getName().equals(player.getName())) {
                if (!recipient.getWorld().equals(player.getWorld()) && value > 0) {
                  sendToAll =
                      PermissionsManager.CMIPerm.chat_rangebypass.hasSetPermission(recipient)
                          || PermissionsManager.CMIPerm.chat_rangespy.hasSetPermission(recipient);
                  if (!sendToAll) {
                    asyncPlayerChatEvent.getRecipients().remove(recipient);
                    continue;
                  }
                }

                if (value > 0 && recipient.getWorld().equals(player.getWorld())) {
                  double number = recipient.getLocation().distance(player.getLocation());
                  if (number > value) {
                    if (sendToAll == null) {
                      sendToAll =
                          PermissionsManager.CMIPerm.chat_rangebypass.hasSetPermission(recipient)
                              || PermissionsManager.CMIPerm.chat_rangespy.hasSetPermission(
                                  recipient);
                    }

                    if (!sendToAll) {
                      asyncPlayerChatEvent.getRecipients().remove(recipient);
                    }
                  }
                }

                if (sendToAll != null && sendToAll) {
                  count++;
                }
              }
            }

            int lineCount = 0;

            for (Player allRecipient : asyncPlayerChatEvent.getRecipients()) {
              if (allRecipient != player && player.canSee(allRecipient)) {
                lineCount++;
              }
            }

            if (lineCount - count <= 0
                && (!this.plugin.getBungeeCordManager().isEnabledSupport()
                    || this.plugin.getBungeeCordManager().isEnabledSupport()
                        && !this.plugin
                            .getBungeeCordManager()
                            .isAnyoneElseOnlineOnNetwork(player.getName()))) {
              CMILC.Chat_LocalNoOne.sendMessage(player);
            }
          }

          if (!this.plugin.getChatManager().isDiscordSRVRanged()
              && this.plugin.getChatFormatManager().getChatGeneralRange(player.getWorld()) > 0
              && PermissionsManager.CMIPerm.chat_rangebypass.hasSetPermission(player)) {
            shouts.put(cmiUser.getUniqueId(), System.currentTimeMillis());
          }

          string =
              this.plugin
                  .getPlaceholderAPIManager()
                  .replacePlaceHolders(CMIChatColor.translate(string), "");

          try {
            String.format(string);
          } catch (Throwable throwable) {
            int rangeCount = 0;

            while (rangeCount < 20) {
              rangeCount++;

              try {
                String.format(string);
              } catch (Throwable throwable2) {
                Matcher matcher = this.whiteRules.getMatcher(throwable2.toString());
                if (matcher == null || !matcher.find()) {
                  break;
                }

                string = string.replace("%" + matcher.group(2), "%%" + matcher.group(2));
              }
            }
          }

          asyncPlayerChatEvent.setFormat(string);
        }
      }
    }
  }

  private void AsyncPlayerChatEventMute(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      if (this.plugin.getChatFormatManager().getChatMutedUntil() != null) {
        if (this.plugin.getChatFormatManager().getChatMutedUntil() < System.currentTimeMillis()) {
          this.plugin.getChatFormatManager().setChatMutedUntil(null);
        } else if (!PermissionsManager.CMIPerm.command_mutechat_bypass.hasPermission(player)) {
          asyncPlayerChatEvent.setCancelled(true);
          CMILC.info(
              "mutechat",
              player,
              "isMuted",
              "[time]",
              CMITimeManager.to24hourShort(
                  this.plugin.getChatFormatManager().getChatMutedUntil()
                      - System.currentTimeMillis()));
          if (this.plugin.getChatFormatManager().getChatMutedReason() != null) {
            CMILC.info(
                "mutechat",
                player,
                "muteReason",
                "[reason]",
                this.plugin.getChatFormatManager().getChatMutedReason());
          }
        }
      }
    }
  }

  public void AsyncPlayerChatEventShadowMute(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.isShadowMuted()) {
          if (cmiUser.getShadowMutedUntil() < System.currentTimeMillis()) {
            cmiUser.setShadowMutedUntil(0L);
          } else if (!PermissionsManager.CMIPerm.command_mute_bypass.hasPermission(player)) {
            asyncPlayerChatEvent.getRecipients().clear();
            asyncPlayerChatEvent.getRecipients().add(player);
          }
        }
      }
    }
  }

  public void AsyncPlayerChatEventPersonalMute(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (cmiUser.getMutedUntil() != 0L) {
          if (cmiUser.getMutedUntil() < System.currentTimeMillis()) {
            cmiUser.setMutedUntil(0L);
          } else if (!PermissionsManager.CMIPerm.command_mute_bypass.hasPermission(player)) {
            asyncPlayerChatEvent.setCancelled(true);
            CMILC.info(
                "mute",
                player,
                "muted",
                "[time]",
                CMITimeManager.to24hourShort(cmiUser.getMutedUntil() - System.currentTimeMillis())
                    + (cmiUser.getMutedReason() == null
                        ? ""
                        : CMILC.getIM("mute", "reason", "[reason]", cmiUser.getMutedReason())));
          }
        }
      }
    }
  }

  public static String processGradientChat(Player player, String string) {
    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser != null && cmiUser.getChatMessageFormat() != null) {
      string =
          string.replace(
              Placeholder.CMIPlaceHolders.cmi_user_chatcolor.getFull(),
              cmiUser.getChatMessageFormat().toString());
    } else {
      string = string.replace(Placeholder.CMIPlaceHolders.cmi_user_chatcolor.getFull(), "");
    }

    if (CMIChatColor.stripColor(string).equals(string)) {
      String format = CMI.getInstance().getChatFormatManager().getGroupMessageFormat(player);
      if (cmiUser != null && cmiUser.getChatMessageFormat() != null) {
        format =
            format.replace(
                Placeholder.CMIPlaceHolders.cmi_user_chatcolor.getFull(),
                cmiUser.getChatMessageFormat().toString());
      } else {
        format = format.replace(Placeholder.CMIPlaceHolders.cmi_user_chatcolor.getFull(), "");
      }

      int value = (int) (Pattern.compile("\\{message\\}").splitAsStream(format).count() - 1L);
      if (value > 1) {
        int chunkSize = string.length() / value;

        for (int chunkIndex = 0; chunkIndex < value - 1; chunkIndex++) {
          String chunk = "";

          for (int charIndex = chunkIndex * chunkSize;
              charIndex < chunkSize + chunkIndex * chunkSize;
              charIndex++) {
            char ch = string.charAt(charIndex);
            chunk = chunk + ch;
          }

          format = format.replaceFirst("\\{message\\}", Matcher.quoteReplacement(chunk));
        }

        String remainder = "";

        for (int tailIndex = chunkSize * (value - 1); tailIndex < string.length(); tailIndex++) {
          char character = string.charAt(tailIndex);
          remainder = remainder + character;
        }

        format = format.replaceFirst("\\{message\\}", Matcher.quoteReplacement(remainder));
      }

      string = format.replace("{message}", string);
    }

    return string;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void AsyncPlayerChatEventRawConverter(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled() && !asyncPlayerChatEvent.getRecipients().isEmpty()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      if (player != null) {
        if (!this.plugin.getChatManager().isChatClickHoverMessages()) {
          if (this.plugin.getChatManager().isModifyChatFormat()) {
            asyncPlayerChatEvent.setMessage(
                processGradientChat(player, asyncPlayerChatEvent.getMessage()));
          }

          if (this.plugin.getTagManager().isChatTagEnabled()) {
            asyncPlayerChatEvent.setMessage(
                this.plugin
                    .getTagManager()
                    .tag(
                        player,
                        asyncPlayerChatEvent.getMessage(),
                        asyncPlayerChatEvent.getFormat(),
                        true));
          }

          this.sendBungee(
              player,
              asyncPlayerChatEvent
                  .getFormat()
                  .replace("%1$s", CMIUser.getUser(player).getDisplayName())
                  .replace("%2$s", asyncPlayerChatEvent.getMessage()),
              asyncPlayerChatEvent.getRecipients());
        } else {
          RawMessage rawMessage = new RawMessage();
          rawMessage.setDontBreakLine(true);
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser != null) {
            if (cmiUser.isVanished()
                && VanishManager.is(cmiUser.getUniqueId(), VanishAction.noMessages)) {
              CMILC.info(vanish.class, player, "noMsg");
              asyncPlayerChatEvent.setCancelled(true);
            } else {
              String string = CMILC.Chat_publicHover.getLocale();
              string = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, string);
              rawMessage.addText(
                  asyncPlayerChatEvent
                      .getFormat()
                      .replace("%1$s", cmiUser.getDisplayName())
                      .replace("%2$s", ""));
              rawMessage.addHover(string);
              ChatFormatManager.chatClickAction.pubmsg.updateClickAction(rawMessage, cmiUser);
              String message = asyncPlayerChatEvent.getMessage();
              message =
                  CMITextCleaner.cleanFromColorCodes(
                      player,
                      asyncPlayerChatEvent.getMessage(),
                      CMIColorTypes.publicmessage,
                      false);
              message =
                  this.plugin.getChatFormatManager().updateUrl(CMIChatColor.deColorize(message));
              if (this.plugin.getChatManager().isModifyChatFormat()) {
                asyncPlayerChatEvent.setMessage(
                    CMIChatColor.translate(processGradientChat(player, message)));
              } else {
                asyncPlayerChatEvent.setMessage(CMIChatColor.translate(message));
              }

              if (this.plugin.getTagManager().isChatTagEnabled()) {
                message =
                    this.plugin
                        .getTagManager()
                        .tag(
                            player,
                            asyncPlayerChatEvent.getMessage(),
                            asyncPlayerChatEvent.getFormat(),
                            true);
              }

              this.plugin.getChatFormatManager().convertHoverOver(rawMessage, message, player);
              message =
                  this.plugin
                      .getChatFormatManager()
                      .convertHoverItem(new StringBuilder(), message, player, false)
                      .toString();

              for (Player recipient : asyncPlayerChatEvent.getRecipients()) {
                rawMessage.show(recipient);
              }

              if (this.plugin.getChatManager().isChatDynMapChat()) {
                this.plugin.getDynMapManager().sentMessage(player, message);
              }

              this.plugin
                  .getChatBubbleManager()
                  .showBubble(
                      asyncPlayerChatEvent.getPlayer(),
                      CMIChatColor.translate(
                              CMIChatColor.getLastColors(asyncPlayerChatEvent.getFormat()))
                          + asyncPlayerChatEvent.getMessage(),
                      asyncPlayerChatEvent.getRecipients());
              asyncPlayerChatEvent.getRecipients().clear();
              asyncPlayerChatEvent.setFormat(
                  CMIChatColor.stripHexColor(asyncPlayerChatEvent.getFormat()));
              if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)
                  && this.plugin.getChatManager().isModifyChatFormat()) {
                asyncPlayerChatEvent.setMessage(CMIChatColor.stripHexColor(message));
              }

              if (Config.monochromeConsole) {
                asyncPlayerChatEvent.setFormat(
                    CMIChatColor.stripColor(asyncPlayerChatEvent.getFormat()));
                asyncPlayerChatEvent.setMessage(CMIChatColor.stripColor(message));
              }

              this.sendBungee(player, rawMessage.getRaw(), asyncPlayerChatEvent.getRecipients());
            }
          }
        }
      }
    }
  }

  private void sendBungee(Player player, String string, Set<Player> set) {
    if (this.plugin.getBungeeCordManager().isBungeeCord()
        && this.plugin.getChatFormatManager().isBungeePublicMessages()) {
      int value = this.plugin.getChatFormatManager().getChatGeneralRange(player.getWorld());
      if (!this.plugin.getChatFormatManager().isBungeePublicMessagesSingleIgnore()
          || set.size() != 1
          || value > 0
          || Util.getOnlinePlayersCount() <= 1) {
        if (value <= 0
            || value > 0
                && (BungeeCordManager.localRangedMessage == null
                    || !BungeeCordManager.localRangedMessage.equals(player.getUniqueId())
                    || BungeeCordManager.localRangedMessageTime < System.currentTimeMillis())) {
          for (String serverName : this.plugin.getBungeeCordManager().getServers().keySet()) {
            if (!this.plugin.getBungeeCordManager().getThisServerName().equalsIgnoreCase(serverName)
                && PermissionsManager.CMIPerm.bungee_publicmessages_$1.hasSetPermission(
                    player, serverName)) {
              this.plugin
                  .getBungeeCordManager()
                  .sendPublicMessage(serverName, player.getName(), string);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void AsyncPlayerChatEventBungee(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      String string = asyncPlayerChatEvent.getFormat();
      Player player = asyncPlayerChatEvent.getPlayer();
      String message = asyncPlayerChatEvent.getMessage();
      if (!this.plugin.getChatManager().isChatClickHoverMessages()) {
        if (this.plugin.getChatManager().isColorsPublicMessages()) {
          message =
              CMITextCleaner.cleanFromColorCodes(
                  player, asyncPlayerChatEvent.getMessage(), CMIColorTypes.publicmessage, false);
        }

        if (this.plugin.getTagManager().isChatTagEnabled()) {
          message =
              this.plugin
                  .getTagManager()
                  .tag(player, message, asyncPlayerChatEvent.getFormat(), true);
        }

        if (this.plugin.getChatManager().isModifyChatFormat()) {
          asyncPlayerChatEvent.setMessage(CMIChatColor.translate(message));
        }
      }

      if (!asyncPlayerChatEvent.getRecipients().isEmpty()) {
        CMIUser cmiUser = CMIUser.getUser(player);
        if (cmiUser != null) {
          message = string.contains("%2$s") ? string.replace("%2$s", message) : message;
          message =
              string.contains("%1$s") ? message.replace("%1$s", cmiUser.getDisplayName()) : message;
          this.sendBungee(player, message, asyncPlayerChatEvent.getRecipients());
        }
      }
    }
  }
}
