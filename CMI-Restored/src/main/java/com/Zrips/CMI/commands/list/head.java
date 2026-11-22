package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class head implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("Feedback", "{gcs}[headName] {gcp}head created!");
    configReader.get(
        "targetFeedback",
        "{gcp}You got {gcs}[headName] {gcp}head from {gcs}[senderDisplayName]{gcp}!");
  }

  @CAnnotation(
      info = "Get players head",
      args = "[sourceName] (targetName) (-s) (amount)",
      tab = {"allIGNPlayername", "playername"},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(final CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    String playerName = null;
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else if (string == null) {
        string = arg;
      } else if (playerName == null && cmi.getPlayerManager().getUser(arg) != null) {
        playerName = arg;
      } else if (integer == null) {
        try {
          integer = Integer.parseInt(arg);
        } catch (Exception | Error value3) {
        }
      }
    }

    integer = integer != null ? CMINumber.clamp(integer, 1, 64) : null;
    if (string == null && commandSender instanceof Player) {
      String skinName = CMIUser.getUser((Player) commandSender).getSkin();
      if (skinName != null) {
        string = skinName;
      } else {
        string = commandSender.getName();
      }
    }

    final Player player = cmi.getTarget(commandSender, playerName, this);
    if (string == null
        || player == null
        || !string.equalsIgnoreCase(commandSender.getName())
            && !PermissionsManager.CMIPerm.command_head_othersource.hasPermission(
                commandSender, true)) {
      return null;
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName())
        && !PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, true, this.getClass().getSimpleName())) {
      return null;
    }

    final Integer stackAmount = integer;
    CMIAsyncHead cmiAsyncHead =
        new CMIAsyncHead() {
          public void afterAsyncUpdate(ItemStack itemStack) {
            CMIScheduler.runTask(
                cmi,
                () -> {
                  if (stackAmount != null) {
                    itemStack.setAmount(stackAmount);
                  }

                  CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
                  cmiUser.getInventory().addItem(itemStack);
                  cmi.save(player);
                });
          }
        };
    cmiAsyncHead.setForce(true);
    CMIItemStack cmiItemStack =
        CMIItemStack.deserialize("head:" + string.split(";")[0], cmiAsyncHead);
    if (cmiItemStack != null && cmiItemStack.getItemStack() != null) {
      if (integer != null) {
        cmiItemStack.setAmount(integer);
      }

      CMIUser targetUser = cmi.getPlayerManager().getUser(player);
      if (!cmiAsyncHead.isAsyncHead()) {
        targetUser.getInventory().addItem(cmiItemStack.getItemStack());
        cmi.save(player);
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (!flag) {
        CMILC.info(this, commandSender, "Feedback", "[headName]", string, snd);
      }

      if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
        CMILC.info(this, player, "targetFeedback", "[headName]", string, snd);
      }

      return true;
    } else {
      return false;
    }
  }
}
