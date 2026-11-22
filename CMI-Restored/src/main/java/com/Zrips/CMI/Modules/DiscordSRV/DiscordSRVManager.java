package com.Zrips.CMI.Modules.DiscordSRV;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import github.scarsz.discordsrv.util.TimeUtil;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class DiscordSRVManager {
  private CMI plugin;
  private DiscordSRV DiscordSRV;
  private DiscordSRVListener discordsrvListener = new DiscordSRVListener();
  private String channelName = null;

  public DiscordSRVManager(CMI cmi) {
    this.plugin = cmi;
    CMIScheduler.runTaskLater(
        cmi,
        () -> {
          CMIHookType.DiscordSRV.init();
          if (CMIHookType.DiscordSRV.isEnabled()) {
            try {
              this.DiscordSRV = (DiscordSRV) CMIHookType.DiscordSRV.getPlugin();
              github.scarsz.discordsrv.DiscordSRV.api.subscribe(this.discordsrvListener);
            } catch (Throwable throwable) {
              CMIHookType.DiscordSRV.setEnabled(false);
              cmi.consoleMessage(
                  "&4Found DiscordSRV, but it looks to be incompatible with current version");
              throwable.printStackTrace();
            }
          }
        },
        2L);
  }

  public void sendDiscordDeathMessage(Player player, String string) {
    if (CMIHookType.DiscordSRV.isEnabled()
        && CMI.getInstance().getChatManager().isChatDiscordSRV()
        && player != null
        && string != null) {
      if (CMIUser.getUser(player).getOptionState(PlayerOption.deathMessages)) {
        String channelName =
            github.scarsz.discordsrv.DiscordSRV.getPlugin().getOptionalChannel("deaths");
        MessageFormat value =
            github.scarsz.discordsrv.DiscordSRV.getPlugin()
                .getMessageFromConfiguration("MinecraftPlayerDeathMessage");
        if (value != null) {
          String messageText = CMIText.isValidString(string) ? string : "";
          String avatarUrl = github.scarsz.discordsrv.DiscordSRV.getAvatarUrl(player);
          String botAvatarUrl = DiscordUtil.getJda().getSelfUser().getEffectiveAvatarUrl();
          String botName =
              this.DiscordSRV.getMainGuild() != null
                  ? this.DiscordSRV.getMainGuild().getSelfMember().getEffectiveName()
                  : DiscordUtil.getJda().getSelfUser().getName();
          String displayName =
              StringUtils.isNotBlank(player.getDisplayName())
                  ? MessageUtil.strip(player.getDisplayName())
                  : "";
          TextChannel textChannel =
              this.DiscordSRV.getDestinationTextChannelForGameChannelName(channelName);
          BiFunction<String, Boolean, String> biFunction =
              (object, escapeMarkdown) -> {
                if (object == null) {
                  return null;
                }

                object =
                    object
                        .replaceAll("%time%|%date%", TimeUtil.timeStamp())
                        .replace(
                            "%username%",
                            escapeMarkdown
                                ? DiscordUtil.escapeMarkdown(player.getName())
                                : player.getName())
                        .replace(
                            "%displayname%",
                            escapeMarkdown ? DiscordUtil.escapeMarkdown(displayName) : displayName)
                        .replace("%usernamenoescapes%", player.getName())
                        .replace("%displaynamenoescapes%", displayName)
                        .replace("%world%", player.getWorld().getName())
                        .replace(
                            "%deathmessage%",
                            MessageUtil.strip(
                                escapeMarkdown
                                    ? DiscordUtil.escapeMarkdown(messageText)
                                    : messageText))
                        .replace("%embedavatarurl%", avatarUrl)
                        .replace("%botavatarurl%", botAvatarUrl)
                        .replace("%botname%", botName);
                if (textChannel != null) {
                  object = DiscordUtil.translateEmotes(object, textChannel.getGuild());
                }

                return object;
              };
          Message translatedMessage =
              github.scarsz.discordsrv.DiscordSRV.translateMessage(value, biFunction);
          if (translatedMessage != null
              && github.scarsz.discordsrv.DiscordSRV.getLength(translatedMessage) >= 3) {
            DiscordUtil.queueMessage(textChannel, translatedMessage, true);
          }
        }
      }
    }
  }

  public void sendDiscordMessage(Player player, String string) {
    if (CMIHookType.DiscordSRV.isEnabled()
        && CMI.getInstance().getChatManager().isChatDiscordSRV()) {
      if (this.channelName == null) {
        for (Entry entry : ((Iterable<Entry>) this.DiscordSRV.getChannels().entrySet())) {
          if (((String) entry.getKey())
              .equalsIgnoreCase(
                  CMI.getInstance().getChatManager().getChatDiscordSRVGlobalChannel())) {
            this.channelName = (String) entry.getKey();
            break;
          }
        }
      }

      if (this.channelName != null) {
        if (player != null) {
          if (string != null) {
            this.DiscordSRV.processChatMessage(player, string, this.channelName, false);
          }
        }
      }
    }
  }
}
