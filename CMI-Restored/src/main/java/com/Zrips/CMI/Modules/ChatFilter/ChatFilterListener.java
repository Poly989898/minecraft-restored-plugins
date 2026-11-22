package com.Zrips.CMI.Modules.ChatFilter;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.events.CMIPlayerChatFilterCapsEvent;
import com.Zrips.CMI.events.CMIPlayerChatFilterEvent;
import net.Zrips.CMILib.Container.CMIText;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatFilterListener implements Listener {
  private CMI plugin;

  public ChatFilterListener(CMI cmi) {
    this.plugin = cmi;
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatFilterManager().getCapsCheckPriority().getPriority(),
            (listener, event) -> this.capsFilter((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatFilterManager().getFilterCheckPriority().getPriority(),
            (listener, event) -> this.AsyncPlayerChatEvent((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatFilterManager().getSimpleReplacerPriority().getPriority(),
            (listener, event) -> this.onChatSimpleReplace((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            PlayerCommandPreprocessEvent.class,
            this,
            cmi.getChatFilterManager().getCommandSpamPriority().getPriority(),
            (listener, event) -> this.spamCommandFilter((PlayerCommandPreprocessEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            AsyncPlayerChatEvent.class,
            this,
            cmi.getChatFilterManager().getChatSpamPriority().getPriority(),
            (listener, event) -> this.spamFilter((AsyncPlayerChatEvent) event),
            cmi,
            true);
    cmi.getServer()
        .getPluginManager()
        .registerEvent(
            SignChangeEvent.class,
            this,
            cmi.getChatFilterManager().getSignAdvertisementPriority().getPriority(),
            (listener, event) -> this.onSignChangeEventAdvertisement((SignChangeEvent) event),
            cmi,
            true);
  }

  private void capsFilter(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled() && !asyncPlayerChatEvent.getRecipients().isEmpty()) {
      if (!this.plugin
          .getChatFilterManager()
          .checkCaps(asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent.getMessage())) {
        CMIPlayerChatFilterCapsEvent cmiPlayerChatFilterCapsEvent =
            new CMIPlayerChatFilterCapsEvent(
                asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent.getMessage());
        if (!cmiPlayerChatFilterCapsEvent.isCancelled()) {
          String string = asyncPlayerChatEvent.getMessage();
          if (this.plugin.getChatFilterManager().isCapToLowerCase()) {
            asyncPlayerChatEvent.setMessage(CMIText.firstToUpperCase(string));
          }

          if (this.plugin.getChatFilterManager().isCancelCapEvent()) {
            asyncPlayerChatEvent.getRecipients().clear();
          }
        }
      }
    }
  }

  private void onChatSimpleReplace(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      String string =
          this.plugin.getChatFilterManager().replaceEmoji(asyncPlayerChatEvent.getMessage());
      if (string != null) {
        asyncPlayerChatEvent.setMessage(string);
      }
    }
  }

  private void AsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      RuleResponse ruleResponse =
          this.plugin
              .getChatFilterManager()
              .getCorrectMessage(
                  asyncPlayerChatEvent.getPlayer(), asyncPlayerChatEvent.getMessage());
      if (!ruleResponse.getRules().isEmpty()) {
        CMIPlayerChatFilterEvent cmiPlayerChatFilterEvent =
            new CMIPlayerChatFilterEvent(asyncPlayerChatEvent.getPlayer(), ruleResponse);
        if (!cmiPlayerChatFilterEvent.isCancelled()) {
          ruleResponse.informStaff(asyncPlayerChatEvent.getPlayer());
          ruleResponse.performCommands(asyncPlayerChatEvent.getPlayer());
          switch (ruleResponse.getMaxFilterBlockType()) {
            case All:
              asyncPlayerChatEvent.setCancelled(true);
              asyncPlayerChatEvent.getRecipients().clear();
              return;
            case Others:
              Player player = asyncPlayerChatEvent.getPlayer();
              asyncPlayerChatEvent.getRecipients().clear();
              asyncPlayerChatEvent.getRecipients().add(player);
            case None:
            default:
              asyncPlayerChatEvent.setMessage(ruleResponse.getUpdatedMessage());
          }
        }
      }
    }
  }

  private void spamFilter(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      Player player = asyncPlayerChatEvent.getPlayer();
      if (this.plugin.getChatFilterManager().isSpam(player, asyncPlayerChatEvent.getMessage())) {
        CMILC.info_noSpam.sendMessage(player, new Snd().setSender(player).setTarget(player));
        asyncPlayerChatEvent.setCancelled(true);
      }
    }
  }

  private void spamCommandFilter(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      Player player = playerCommandPreprocessEvent.getPlayer();
      if (this.plugin
          .getChatFilterManager()
          .isSpamedCommand(player, playerCommandPreprocessEvent.getMessage())) {
        CMILC.info_noCmdSpam.sendMessage(player, new Snd().setSender(player).setTarget(player));
        playerCommandPreprocessEvent.setCancelled(true);
      }
    }
  }

  private void onSignChangeEventAdvertisement(SignChangeEvent signChangeEvent) {
    if (!signChangeEvent.isCancelled()) {
      Player player = signChangeEvent.getPlayer();
      if (player != null) {
        String[] strings = signChangeEvent.getLines();
        StringBuilder stringBuilder = new StringBuilder();

        for (int value = 0; value < strings.length; value++) {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append("%break%");
          }

          stringBuilder.append(strings[value]);
        }

        this.plugin
            .getPlayerManager()
            .sendMessageToSignSpies(
                player, stringBuilder.toString(), signChangeEvent.getBlock().getLocation());
      }
    }
  }
}
