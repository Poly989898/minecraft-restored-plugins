package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIItemDonations;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class donate implements Cmd {
  public static HashMap<UUID, Long> donateLock = new HashMap<>();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "donated",
        "{gcp}Donated {gcs}[type]{gcp}X{gcs}[amount] {gcp}items to {gcs}[playerDisplayName]");
    configReader.get(
        "got",
        "{gcp}Got {gcs}[type]{gcp}X{gcs}[amount] {gcp}donation from {gcs}[playerDisplayName]");
    configReader.get(
        "sentOffer",
        "{gcp}Sent donation to {gcs}[playerDisplayName]{gcp}. Waiting until player accepts it");
    configReader.get(
        "gotOffer",
        "{gcp}Got {gcs}[type]{gcp}X{gcs}[amount] {gcp}donation from {gcs}[sourceDisplayName]{gcp}."
            + " Click to accept it.");
    configReader.get("doesnthave", "{gcp}Player doesn't have this item anymore");
    configReader.get("no", "{gcp}No pending donations");
    configReader.get(
        "list", "&7[number]. {gcp}[playerDisplayName] {gcp}- &7[material] {gcs}x {gcp}[amount]");
  }

  @CAnnotation(
      info = "Donate item you are holding",
      args = "[playerName] (amount)",
      tab = "playername",
      regVar = {0, 1, 2},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Integer integer = null;
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (string == null) {
        Player player = CMIUser.getOnlinePlayer(commandSender, arg, true);
        if (player != null) {
          string = arg;
          continue;
        }
      }

      try {
        integer = Integer.parseInt(arg);
      } catch (Exception exception) {
        if (arg.equalsIgnoreCase("accept")) {
          flag = true;
        } else {
          string = arg;
        }
      }
    }

    Player senderPlayer = (Player) commandSender;
    if (string == null) {
      int count = 0;
      CMIUser cmiUser = cmi.getPlayerManager().getUser(senderPlayer);

      for (var entry : cmiUser.getPendingDonations().entrySet()) {
        count++;
        Snd snd = new Snd();
        snd.setSender(commandSender);
        CMIUser donorUser =
            cmi.getPlayerManager().getUser(((CMIItemDonations) entry.getValue()).getDonor());
        snd.setTarget(donorUser);
        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[number]",
                    count,
                    "[material]",
                    CMIMaterial.get(((CMIItemDonations) entry.getValue()).getItem()).getName(),
                    "[amount]",
                    ((CMIItemDonations) entry.getValue()).getItem().getAmount(),
                    snd))
            .addHover(LC.modify_acceptSymbolHover.getLocale(new Object[0]))
            .addCommand(
                "cmi " + this.getClass().getSimpleName() + " accept " + donorUser.getName());
        rawMessage.show(commandSender);
      }

      if (cmiUser.getPendingDonations().isEmpty()) {
        CMILC.info(this, commandSender, "no");
      }

      return null;
    } else {
      Player targetPlayer = CMIUser.getOnlinePlayer(commandSender, string, true);
      if (targetPlayer == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (targetPlayer.getName().equalsIgnoreCase(commandSender.getName())) {
        LC.info_cantDoForYourSelf.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (flag
          || !PermissionsManager.CMIPerm.command_donate_bypass.hasSetPermission(targetPlayer)
              && PermissionsManager.CMIPerm.command_donate_accept.hasSetPermission(targetPlayer)) {
        CMIUser targetUser = cmi.getPlayerManager().getUser(targetPlayer);
        if (targetUser == null) {
          return false;
        }

        CMIUser senderUser = cmi.getPlayerManager().getUser((Player) commandSender);
        if (senderUser == null) {
          return false;
        }

        if (flag) {
          if (!PermissionsManager.CMIPerm.command_donate_accept.hasPermission(
              commandSender, true)) {
            return null;
          }

          CMIItemDonations cmiItemDonations =
              senderUser.getPendingDonations().remove(targetPlayer.getUniqueId());
          if (cmiItemDonations == null) {
            CMILC.info(this, commandSender, "no");
            return null;
          }

          if (!targetPlayer
              .getInventory()
              .containsAtLeast(
                  cmiItemDonations.getItem(), cmiItemDonations.getItem().getAmount())) {
            CMILC.info(this, senderUser, "doesnthave");
            return null;
          }

          donateLock.put(targetPlayer.getUniqueId(), System.currentTimeMillis());
          Snd donateSound = new Snd();
          donateSound.setSource(targetUser);
          donateSound.setTarget(senderUser);
          ItemStack itemStack = cmiItemDonations.getItem();
          var hashMap = targetUser.getInventory().removeItem(itemStack);
          if (hashMap.size() == 0) {
            if (targetUser.isOnline()) {
              targetUser.getPlayer().updateInventory();
            }

            senderUser.getInventory().addItem(itemStack);
            CMILC.info(
                this,
                targetUser,
                "donated",
                donateSound,
                "[type]",
                CMIMaterial.get(itemStack).getName(),
                "[amount]",
                itemStack.getAmount());
            donateSound = new Snd();
            donateSound.setTarget(targetUser);
            donateSound.setSource(senderUser);
            CMILC.info(
                this,
                senderUser,
                "got",
                donateSound,
                "[type]",
                CMIMaterial.get(itemStack).getName(),
                "[amount]",
                itemStack.getAmount());
            return true;
          } else {
            CMILC.info(this, senderUser, "doesnthave");
            return null;
          }
        } else {
          if (!targetUser.isOnline()) {
            LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
            return null;
          }

          if (!PermissionsManager.CMIPerm.command_donate_send.hasPermission(commandSender, true)) {
            return null;
          }

          ItemStack heldItem = senderUser.getItemInHand();
          if (heldItem != null && !CMIMaterial.isAir(heldItem.getType())) {
            ItemStack donateItem = heldItem.clone();
            if (integer != null) {
              if (integer < 1) {
                integer = 1;
              }

              if (integer < donateItem.getAmount()) {
                donateItem.setAmount(integer);
              }
            }

            CMIItemDonations donations = new CMIItemDonations();
            donations.setDonor(senderUser.getPlayer().getUniqueId());
            donations.setReceiver(targetUser.getPlayer().getUniqueId());
            donations.setItem(donateItem);
            donations.setTime(System.currentTimeMillis());
            Snd itemSound = new Snd();
            itemSound.setSource(senderUser);
            itemSound.setTarget(targetUser);
            CMILC.info(this, commandSender, "sentOffer", itemSound);
            if (!targetUser.getPendingDonations().containsKey(senderUser.getUniqueId())
                || targetUser.getPendingDonations().get(senderUser.getUniqueId()).getReceiver()
                    != targetUser.getUniqueId()) {
              RawMessage message = new RawMessage();
              message.addText(
                  CMILC.getIM(
                      this,
                      "gotOffer",
                      itemSound,
                      "[type]",
                      CMIMaterial.get(donateItem).getName(),
                      "[amount]",
                      donateItem.getAmount()));
              message.addHover(LC.info_Click.getLocale(new Object[0]));
              message.addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " accept "
                      + commandSender.getName());
              message.build();
              message.show(targetUser.getPlayer());
            }

            targetUser.addItemDonationFrom(donations);
            return true;
          } else {
            LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
            return null;
          }
        }
      } else {
        Snd messageSound = new Snd();
        messageSound.setSource(targetPlayer);
        messageSound.setTarget(targetPlayer);
        LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[] {messageSound});
        return null;
      }
    }
  }
}
