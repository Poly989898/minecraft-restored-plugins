package com.Zrips.CMI.Modules.Alias;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CmdCost.CMICommandCost;
import com.Zrips.CMI.Modules.CmdCost.CMICommandCostUsage;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.aliaseditor;
import com.Zrips.CMI.commands.list.attachcommand;
import com.Zrips.CMI.commands.list.ban;
import com.Zrips.CMI.commands.list.dsign;
import com.Zrips.CMI.commands.list.kick;
import com.Zrips.CMI.commands.list.mute;
import com.Zrips.CMI.commands.list.placeholders;
import com.Zrips.CMI.commands.list.tempban;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class onPreprocessCommand implements Listener {
  @EventHandler(priority = EventPriority.LOW)
  public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      Player player = playerCommandPreprocessEvent.getPlayer();
      CMI.getInstance().getCTextManager().removeChatEditor(player);
      String string =
          playerCommandPreprocessEvent.getMessage().startsWith("/:")
              ? playerCommandPreprocessEvent.getMessage().substring(2)
              : playerCommandPreprocessEvent.getMessage();
      string = string.startsWith("/") ? string.substring(1) : string;
      String commandText = string;
      if (string.startsWith(" ")) {
        string = string.substring(1);
      }

      CommandAlias commandAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
      if (commandAlias != null && commandAlias.getAlias() != null && string != null) {
        String aliasName = commandAlias.getAlias();
        if (playerCommandPreprocessEvent.getMessage().startsWith("/ ")) {
          CMILC.info_CommandFormat.sendMessage(player);
          playerCommandPreprocessEvent.setCancelled(true);
        } else {
          if (commandAlias.isRequiresPerm()) {
            CMI cmi = CMI.getInstance();
            if (commandAlias.getType() == CommandAliasType.custom
                    && !PermissionsManager.CMIPerm.customalias_$1.hasPermission(
                        player, commandAlias.getAliasAsOneWordNS())
                || commandAlias.getType() == CommandAliasType.subbase
                    && !PermissionsManager.CMIPerm.command_$1.hasPermission(
                        player, commandAlias.getCmiCommandName())) {
              boolean flag =
                  cmi.getConfigManager().isPermisionOnError()
                      || PermissionsManager.CMIPerm.permisiononerror.hasPermission(player);
              RawMessage rawMessage = new RawMessage();
              String aliasValue =
                  commandAlias.getType() == CommandAliasType.custom
                      ? "customalias." + commandAlias.getAliasAsOneWordNS()
                      : "command." + commandAlias.getCmiCommandName();
              rawMessage
                  .addText(LC.info_NoPermission.getLocale(new Object[0]))
                  .addHover(flag ? "&2" + CommandsHandler.getLabel() + "." + aliasValue : null);
              rawMessage.show(player);
              ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
              Snd snd = new Snd().setSender(consoleCommandSender).setTarget(player);
              LC.info_NoPlayerPermission.sendMessage(
                  consoleCommandSender,
                  new Object[] {
                    snd, "[permission]", CommandsHandler.getLabel() + "." + aliasValue
                  });
              playerCommandPreprocessEvent.setCancelled(true);
              return;
            }
          }

          if (string.length() >= aliasName.length()) {
            if (aliasName.endsWith(AliasManager.nonExactAliasSuffix)) {
              string = string.substring(aliasName.length() - 2);
            } else {
              string = string.substring(aliasName.length());
            }
          }

          ArrayList<String> arrayList = new ArrayList();
          if (string.contains(" ")) {
            arrayList.addAll(Arrays.asList(string.split(" ")));
          } else {
            arrayList.add(string);
          }

          List<String> list = new ArrayList<>(commandAlias.getCommands());
          if (commandAlias.getCmiCommandName() == null
              || commandAlias.getCmiCommandName() != null
                  && !commandAlias
                      .getCmiCommandName()
                      .equalsIgnoreCase(placeholders.class.getSimpleName())
                  && !commandAlias
                      .getCmiCommandName()
                      .equalsIgnoreCase(dsign.class.getSimpleName())) {
            for (int value = 0; value < arrayList.size(); value++) {
              if (CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .containsPlaceHolder((String) arrayList.get(value))) {
                arrayList.set(
                    value,
                    CMI.getInstance()
                        .getPlaceholderAPIManager()
                        .coolDownPlaceHolders((String) arrayList.get(value), player));
              }
            }
          }

          list = CMI.getInstance().getAliasManager().updateCommands(commandAlias, arrayList, list);
          Snd aliasSound = new Snd().setSender(player).setTarget(player);

          for (int index = 0; index < list.size(); index++) {
            String line = (String) list.get(index);
            if (!line.startsWith(
                    CommandsHandler.getLabel() + " " + attachcommand.class.getSimpleName())
                && !line.startsWith(
                    CommandsHandler.getLabel() + " " + aliaseditor.class.getSimpleName())
                && !line.startsWith(CommandsHandler.getLabel() + " " + mute.class.getSimpleName())
                && !line.startsWith(CommandsHandler.getLabel() + " " + ban.class.getSimpleName())
                && !line.startsWith(
                    CommandsHandler.getLabel() + " " + tempban.class.getSimpleName())
                && !line.startsWith(
                    CommandsHandler.getLabel() + " " + kick.class.getSimpleName())) {
              line = CMI.getInstance().getLM().updateSnd(aliasSound, line);
            }

            list.set(index, line);
          }

          CMICommandCostUsage cmiCommandCostUsage =
              CMI.getInstance().getCommandCostManager().canUseCmd(player, aliasName);
          CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
          CMICommandCost cmiCommandCost =
              CMI.getInstance().getCommandCostManager().getCost(aliasName);
          if (!cmiCommandCostUsage.equals(CMICommandCostUsage.NoMoney)
              && !cmiCommandCostUsage.equals(CMICommandCostUsage.Cant)) {
            if (cmiCommandCostUsage.equals(CMICommandCostUsage.ReqConfirm)) {
              RawMessage message = new RawMessage();
              message.addText(
                  CMILC.econ_commandCost.getLocale(
                      "[cost]",
                      CMI.getInstance().getEconomyManager().format(cmiCommandCost.getCost())));
              message.addHover(LC.info_Click.getLocale(new Object[0]));
              message.addCommand(aliasName);
              message.show(player);
              playerCommandPreprocessEvent.setCancelled(true);
              return;
            }
          } else if (cmiCommandCost != null
              && cmiCommandCost.getCost() > 0.0
              && !cmiUser.hasMoney(cmiCommandCost.getCost())) {
            LC.econ_notEnoughMoney.sendMessage(
                player,
                new Object[] {
                  "[amount]", CMI.getInstance().getEconomyManager().format(cmiCommandCost.getCost())
                });
            playerCommandPreprocessEvent.setCancelled(true);
            return;
          }

          if (cmiCommandCost != null
              && cmiCommandCost.getCost() > 0.0
              && cmiCommandCostUsage != CMICommandCostUsage.CostBypass) {
            if (cmiUser.hasMoney(cmiCommandCost.getCost())) {
              cmiUser.withdraw(cmiCommandCost.getCost());
              cmiCommandCost.removeConfirmation(cmiUser.getUniqueId());
              if (cmiCommandCost.isInformOnCharge()) {
                LC.econ_charged.sendMessage(
                    player,
                    new Object[] {
                      "[amount]",
                      CMI.getInstance().getEconomyManager().format(cmiCommandCost.getCost())
                    });
              }
            } else {
              LC.econ_notEnoughMoney.sendMessage(
                  player,
                  new Object[] {
                    "[amount]",
                    CMI.getInstance().getEconomyManager().format(cmiCommandCost.getCost())
                  });
            }
          }

          if (!commandAlias.isDisableBase()
              || !commandAlias.getType().equals(CommandAliasType.base)
                  && !commandAlias.getType().equals(CommandAliasType.subbase)) {
            if (!list.isEmpty()
                && list.size() == 1
                && !CMI.getInstance()
                    .getSpecializedCommandManager()
                    .isSpecializedCommand((String) list.get(0))) {
              String first = (String) list.get(0);
              if (aliasName.equalsIgnoreCase(commandText)
                  || commandText.toLowerCase().startsWith(aliasName.toLowerCase())
                  || aliasName.endsWith(AliasManager.nonExactAliasSuffix)) {
                playerCommandPreprocessEvent.setMessage("/" + first);
              }
            } else if (aliasName.equalsIgnoreCase(commandText)
                || commandText.startsWith(aliasName)
                || aliasName.endsWith("?")) {
              if (commandText.startsWith(aliasName)
                  && aliasName.split(" ").length < commandText.split(" ").length
                  && !commandAlias.containsDynamicVariables()) {
                return;
              }

              if (commandAlias.getType().equals(CommandAliasType.custom)) {
                if (CMI.getInstance().getWarmUpManager().startCmdUsage(player, commandText)) {
                  playerCommandPreprocessEvent.setCancelled(true);
                  return;
                }

                if (!CMI.getInstance()
                    .getCooldownManager()
                    .canUseCmd(player, commandText, true, true)) {
                  playerCommandPreprocessEvent.setCancelled(true);
                  return;
                }
              }

              playerCommandPreprocessEvent.setCancelled(true);
              CMI.getInstance()
                  .getPlayerManager()
                  .sendMessageToCommandSpies(player, playerCommandPreprocessEvent.getMessage());
              ArrayList listCopy = new ArrayList(list);
              CMIScheduler.runTask(
                  CMI.getInstance(),
                  () ->
                      CMI.getInstance()
                          .getSpecializedCommandManager()
                          .processAliasCmdsAsPlayer(
                              "CMIAlias-" + commandAlias.getAlias(), listCopy, player));
            }
          } else {
            String firstArg = (String) list.get(0);
            playerCommandPreprocessEvent.setMessage("/" + firstArg.split(" ", 2)[1]);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCommandSpy(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (!playerCommandPreprocessEvent.isCancelled()) {
      String string =
          playerCommandPreprocessEvent.getMessage().startsWith("/")
              ? playerCommandPreprocessEvent.getMessage().substring(1)
              : playerCommandPreprocessEvent.getMessage();
      CommandAlias commandAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
      if (commandAlias == null) {
        CMI.getInstance()
            .getPlayerManager()
            .sendMessageToCommandSpies(
                playerCommandPreprocessEvent.getPlayer(),
                playerCommandPreprocessEvent.getMessage());
      }
    }
  }
}
