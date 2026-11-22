package com.Zrips.CMI.Modules.DiscordSRV;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatListener;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatManager;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig;
import java.util.HashSet;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DiscordSRVListener implements Listener {
  @Subscribe(priority = ListenerPriority.NORMAL)
  public void discordMessageReceived(DiscordGuildMessagePreProcessEvent value) {
    if (CMI.getInstance().getChatManager().isModifyChatFormat()
        && !value.isCancelled()
        && CMI.getInstance().getChatManager().isChatDiscordSRV()
        && CMI.getInstance()
            .getChatManager()
            .getChatDiscordSRVGlobalChannel()
            .equalsIgnoreCase(value.getChannel().getName())) {
      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(value.getAuthor().getName());
      if (cmiUser != null) {
        Player player = cmiUser.getPlayer(false);
        String string = CMI.getInstance().getChatFormatManager().getGroupFormat(player);
        if (string.contains("%")) {
          string = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
        }

        string = string.replace("%", "");
        string = string.replace("{displayName}", "%1$s");

        try {
          string = string.replace("{group}", cmiUser.getGroupName());
        } catch (Exception exception) {
          string = string.replace("{group}", "");
        }

        if (player != null) {
          string = string.replace("{world}", player.getWorld().getName());
        }

        string = string.replace("{prefix}", cmiUser.getPrefix());
        string = string.replace("{suffix}", cmiUser.getSuffix());
        string =
            string.replace(
                "{discord}",
                player != null
                        && DiscordSRV.getPlugin()
                                .getAccountLinkManager()
                                .getDiscordId(player.getUniqueId())
                            != null
                    ? CMI.getInstance().getChatManager().getDiscordSRVLabel()
                    : CMI.getInstance().getChatManager().getDiscordSRVUnlinkedLabel());
        string = string.replace("{message}", "%2$s");
        if (CMI.getInstance().getChatManager().isChatClickHoverMessages()) {
          RawMessage rawMessage = new RawMessage();
          rawMessage.setDontBreakLine(true);
          String hoverText = CMILC.Chat_publicHover.getLocale();
          hoverText =
              CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, hoverText);
          rawMessage
              .addText(string.replace("%1$s", cmiUser.getDisplayName()).replace("%2$s", ""))
              .addHover(hoverText);
          ChatFormatManager.chatClickAction.discord.updateClickAction(rawMessage, cmiUser);
          String rawContent = value.getMessage().getContentRaw();
          rawContent =
              CMITextCleaner.cleanFromColorCodes(
                  player, rawContent, CMIColorTypes.publicmessage, false);
          rawContent = CMI.getInstance().getTagManager().tag(player, rawContent, string, true);
          String formatted =
              string.replace("%1$s", cmiUser.getDisplayName()).replace("%2$s", "")
                  + (string.contains("%2$s") ? rawContent : "");
          rawContent =
              CMI.getInstance()
                  .getChatFormatManager()
                  .updateUrl(CMIChatColor.deColorize(rawContent));
          CMI.getInstance().getChatFormatManager().convertHoverOver(rawMessage, rawContent, player);
          HashSet<Player> hashSet = new HashSet();

          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            hashSet.add(onlinePlayer);
          }

          CMIScheduler.runTask(CMI.getInstance(), () -> rawMessage.show(hashSet));
          CMIMessages.sendMessage(Bukkit.getConsoleSender(), formatted);
          value.setCancelled(true);
          if (player != null
              && player.isOnline()
              && CMI.getInstance().getChatManager().isChatDynMapChat()) {
            CMI.getInstance().getDynMapManager().sentMessage(player, rawContent);
          }
        } else {
          String prefix = "";
          String publicHoverText = CMILC.Chat_publicHover.getLocale();
          publicHoverText =
              CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(player, publicHoverText);
          prefix = prefix + string.replace("%1$s", cmiUser.getDisplayName()).replace("%2$s", "");
          String content = value.getMessage().getContentRaw();
          content =
              CMITextCleaner.cleanFromColorCodes(
                  player, content, CMIColorTypes.publicmessage, false);
          content = CMI.getInstance().getTagManager().tag(player, content, string, true);
          String formattedMessage =
              string.replace("%1$s", cmiUser.getDisplayName()).replace("%2$s", "")
                  + (string.contains("%2$s") ? content : "");
          content =
              CMI.getInstance().getChatFormatManager().updateUrl(CMIChatColor.deColorize(content));
          String fullMessage = prefix + content;

          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CMIScheduler.runTask(
                CMI.getInstance(), () -> CMIMessages.sendMessage(onlinePlayer, fullMessage));
          }

          formattedMessage = CTextManager.clean(formattedMessage.replace("%", ""));
          CMIMessages.sendMessage(Bukkit.getConsoleSender(), formattedMessage);
          value.setCancelled(true);
        }
      }
    }
  }

  @Subscribe(priority = ListenerPriority.LOWEST)
  public void aMessageWasSentInADiscordGuildByTheBot(GameChatMessagePreProcessEvent value) {
    Player player = value.getPlayer();
    if (CMIUser.getUser(player).isShadowMuted()) {
      value.setCancelled(true);
    }

    if (value.getMessage().contains("{@") && value.getMessage().contains("}")) {
      Component newComponent = value.getMessageComponent();
      newComponent =
          newComponent.replaceText(
              (TextReplacementConfig)
                  TextReplacementConfig.builder()
                      .match("(\\{@)([a-zA-Z_]{2,})(\\})")
                      .replacement("")
                      .build());
      value.setMessageComponent(newComponent);
    }

    if (!CMI.getInstance().getChatManager().isDiscordSRVRanged()
        && CMI.getInstance().getChatFormatManager().getChatGeneralRange(player.getWorld()) > 0) {
      Long lastShoutTime = ChatFormatListener.shouts.get(player.getUniqueId());
      if (lastShoutTime == null) {
        value.setCancelled(true);
        return;
      }

      if (lastShoutTime + 100L > System.currentTimeMillis()) {
        return;
      }

      value.setCancelled(true);
    }
  }
}
