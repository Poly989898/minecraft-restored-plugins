package com.Zrips.CMI.Modules.SpecializedCommands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Portals.CMIPortal;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.events.CMIPlayerKickEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

public class SpecializedCommandManager {
  private CMI plugin;
  HashMap<UUID, HashMap<String, SpecializedCommandManager.runAway>> runaway = new HashMap<>();
  HashMap<String, SpecializedCommandManager.runAway> globalRunaway = new HashMap<>();
  Pattern patern = Pattern.compile("^(asPlayer! |asConsole! )?ptarget:(\\S+)!");
  private static boolean informed = false;
  private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", 16);
  private int maxRepeats = 40;
  private static int MAX_ENTRIES = Bukkit.getMaxPlayers();
  public static LinkedHashMap<UUID, overflowCommands> repeatingCommands =
      new LinkedHashMap<UUID, overflowCommands>(MAX_ENTRIES + 1, 0.75F, false) {
        @Override
        protected boolean removeEldestEntry(Entry<UUID, overflowCommands> entry) {
          return this.size() > SpecializedCommandManager.MAX_ENTRIES;
        }
      };
  Pattern checkPattern = Pattern.compile("(.+)?(==|>=|>|<=|<|!=)(.+)");
  Pattern containsPattern = Pattern.compile("(.+)?(=>)(.+)");

  public SpecializedCommandManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void clearCache(UUID uuid) {
    var runawayMap = this.runaway.get(uuid);
    if (runawayMap != null) {
      Iterator iterator = runawayMap.entrySet().iterator();

      while (iterator.hasNext()) {
        Entry entry = (Entry) iterator.next();
        if (((SpecializedCommandManager.runAway) entry.getValue()).getTime()
            >= System.currentTimeMillis()) {
          iterator.remove();
        }
      }

      if (runawayMap.isEmpty()) {
        this.runaway.remove(uuid);
      }
    }
  }

  public void processAliasCmdsAsPlayer(String commandName, List<String> list, Player player) {
    if (commandName != null) {
      var runawayMap = this.runaway.get(player.getUniqueId());
      if (runawayMap != null) {
        SpecializedCommandManager.runAway existingRunAway =
            (SpecializedCommandManager.runAway) runawayMap.get(commandName.toLowerCase());
        if (existingRunAway != null && existingRunAway.getTime() > System.currentTimeMillis()) {
          CMILC.info_specializedRunning.sendMessage(
              player,
              "[time]",
              CMITimeManager.to24hourShort(
                  existingRunAway.getTime() - System.currentTimeMillis() + 1000L));
          return;
        }
      }

      double totalDelay = 0.0;

      for (int index = 0; index < list.size(); index++) {
        String delayText = (String) list.get(index);
        if (delayText.startsWith("delay!")) {
          try {
            totalDelay += Double.parseDouble(delayText.split(" ")[1]);
          } catch (Exception exception) {
            try {
              double parsedDelay =
                  Double.parseDouble(
                      this.plugin
                          .getPlaceholderAPIManager()
                          .updatePlaceHolders(player, delayText.split(" ")[1]));
              list.set(index, "delay! " + parsedDelay);
              totalDelay += parsedDelay;
            } catch (Throwable throwable) {
              CMIMessages.consoleMessage("Failed to determine delay amount, check spelling");
            }
          }
        }
      }

      if (totalDelay > 0.0) {
        runawayMap = this.runaway.getOrDefault(player.getUniqueId(), new HashMap<>());
        SpecializedCommandManager.runAway newRunAway =
            new SpecializedCommandManager.runAway(
                (long) (System.currentTimeMillis() + totalDelay * 1000.0));
        runawayMap.put(commandName.toLowerCase(), newRunAway);
        this.runaway.put(player.getUniqueId(), runawayMap);
      }
    }

    this.process(commandName, list, player, null);
  }

  private void process(
      String commandName,
      List<String> list,
      Player player,
      SpecializedCommandManager.CheckStatements checkStatements) {
    if (checkStatements == null) {
      checkStatements = new SpecializedCommandManager.CheckStatements();
    }

    SpecializedCommandManager.CheckStatements statements = checkStatements;

    while (!list.isEmpty()) {
      if (!this.plugin.isEnabled()) {
        return;
      }

      String cmd = (String) list.get(0);
      list.remove(0);
      if (cmd != null && !cmd.isEmpty()) {
        if (!cmd.toLowerCase().startsWith("delay!")
            && !cmd.toLowerCase().startsWith("softdelay!")) {
          boolean hasAsPlayer = false;
          boolean isConsoleCommand =
              cmd.startsWith(
                  SpecializedCommandManager.specialisedCommand.asConsole.getFormat()
                      + " "
                      + SpecializedCommandManager.specialisedCommand.ptarget.getFormatForCheck());

          for (SpecializedCommandManager.specCommandAction action :
              this.processSpecializedCommand(cmd).getConditionList()) {
            if (action.getCmd().equals(SpecializedCommandManager.specialisedCommand.asPlayer)) {
              hasAsPlayer = true;
            }
          }

          if (this.processSpecializedCommand(cmd).getAction() != null
              && this.processSpecializedCommand(cmd)
                  .getAction()
                  .equals(SpecializedCommandManager.specialisedCommand.asPlayer)) {
            hasAsPlayer = true;
          }

          if (!hasAsPlayer && !isConsoleCommand) {
            cmd = "asPlayer! " + cmd;
          }

          if (isConsoleCommand) {
            cmd =
                cmd.substring(
                    SpecializedCommandManager.specialisedCommand.asConsole.getFormat().length()
                        + 1);
          }

          if (!this.executeCmd(
              commandName, cmd, isConsoleCommand ? null : player, null, null, statements)) {
            if (commandName != null) {
              var runawayMap = this.runaway.get(player.getUniqueId());
              if (runawayMap != null) {
                runawayMap.remove(commandName.toLowerCase());
              }
            }

            return;
          }
        } else {
          Double delaySeconds = -1.0;
          String globalKey = null;
          String[] parts = cmd.split(" ");
          if (parts.length > 2) {
            globalKey = parts[2];
          }

          try {
            delaySeconds = Double.parseDouble(parts[1]);
          } catch (Exception exception) {
            try {
              delaySeconds =
                  delaySeconds
                      + Double.parseDouble(
                          this.plugin
                              .getPlaceholderAPIManager()
                              .updatePlaceHolders(player, parts[1]));
            } catch (Throwable throwable) {
              CMIMessages.consoleMessage("Failed to determine delay amount, check spelling");
              continue;
            }
          }

          ArrayList remainingCmds = new ArrayList(list);
          if (delaySeconds > 0.0) {
            SpecializedCommandManager.runAway activeRunAway = null;
            if (globalKey == null) {
              var runawayMap = this.runaway.getOrDefault(player.getUniqueId(), new HashMap<>());
              activeRunAway =
                  (SpecializedCommandManager.runAway) runawayMap.get(commandName.toLowerCase());
              if (activeRunAway == null) {
                activeRunAway =
                    new SpecializedCommandManager.runAway(
                        (long) (System.currentTimeMillis() + delaySeconds * 1000.0));
                runawayMap.put(commandName.toLowerCase(), activeRunAway);
                this.runaway.put(player.getUniqueId(), runawayMap);
              }
            } else {
              activeRunAway = this.globalRunaway.get(globalKey.toLowerCase());
              if (activeRunAway != null) {
                activeRunAway.stopSchedTask();
              } else {
                activeRunAway =
                    new SpecializedCommandManager.runAway(
                        (long) (System.currentTimeMillis() + delaySeconds * 1000.0));
              }

              this.globalRunaway.put(globalKey.toLowerCase(), activeRunAway);
            }

            activeRunAway.setSchedTask(
                CMIScheduler.runTaskLater(
                    this.plugin,
                    () -> this.process(commandName, remainingCmds, player, statements),
                    Math.round(delaySeconds * 20.0)));
          }

          return;
        }
      }
    }

    var runawayMap = this.runaway.get(player.getUniqueId());
    if (runawayMap != null) {
      runawayMap.remove(commandName.toLowerCase());
    }
  }

  public void processCmds(List<String> list) {
    this.processCmds(null, list, null, null, null);
  }

  public void processCmds(List<String> list, CommandSender commandSender) {
    this.processCmds(null, list, null, null, commandSender);
  }

  public void processCmds(List<String> list, Player player) {
    this.processCmds(null, list, player, null, null);
  }

  public void processCmds(List<String> list, Player player, CMIInteractType cmiInteractType) {
    this.processCmds(null, list, player, cmiInteractType, null);
  }

  public void processCmds(String string, List<String> list, Player player) {
    this.processCmds(string, list, player, null, null);
  }

  public void processCmds(
      String string, List<String> list, Player player, CMIInteractType cmiInteractType) {
    this.processCmds(string, list, player, cmiInteractType, null);
  }

  public void processCmds(
      String string,
      List<String> list,
      Player player,
      CMIInteractType cmiInteractType,
      CommandSender commandSender) {
    if (list != null && !list.isEmpty()) {
      if (string != null) {
        HashMap runawayMap = null;
        if (player != null) {
          runawayMap = this.runaway.get(player.getUniqueId());
        }

        if (runawayMap != null) {
          SpecializedCommandManager.runAway existingRunAway =
              (SpecializedCommandManager.runAway) runawayMap.get(string.toLowerCase());
          if (existingRunAway != null && existingRunAway.getTime() > System.currentTimeMillis()) {
            CMILC.info_specializedRunning.sendMessage(
                player,
                "[time]",
                CMITimeManager.to24hourShort(
                    existingRunAway.getTime() - System.currentTimeMillis() + 1000L));
            return;
          }
        }

        double totalDelay = 0.0;

        for (int index = 0; index < list.size(); index++) {
          String delayText = (String) list.get(index);
          if (delayText.startsWith("delay!")) {
            try {
              totalDelay += Double.parseDouble(delayText.split(" ")[1]);
            } catch (Exception exception) {
              try {
                double parsedDelay =
                    Double.parseDouble(
                        this.plugin
                            .getPlaceholderAPIManager()
                            .updatePlaceHolders(player, delayText.split(" ")[1]));
                list.set(index, "delay! " + parsedDelay);
                totalDelay += parsedDelay;
              } catch (Throwable throwable) {
                CMIMessages.consoleMessage("Failed to determine delay amount, check spelling");
              }
            }
          }
        }

        if (totalDelay > 0.0 && player != null) {
          runawayMap = this.runaway.getOrDefault(player.getUniqueId(), new HashMap<>());
          SpecializedCommandManager.runAway newRunAway =
              new SpecializedCommandManager.runAway(
                  (long) (System.currentTimeMillis() + totalDelay * 1000.0));
          runawayMap.put(string.toLowerCase(), newRunAway);
          this.runaway.put(player.getUniqueId(), runawayMap);
        }
      }

      this.process(string, list, player, cmiInteractType, commandSender);
    }
  }

  private void process(
      String commandName,
      List<String> list,
      Player player,
      CMIInteractType cmiInteractType,
      CommandSender commandSender) {
    this.process(commandName, list, player, cmiInteractType, commandSender, null);
  }

  private void process(
      String string,
      List<String> list,
      Player player,
      CMIInteractType cmiInteractType,
      CommandSender commandSender,
      SpecializedCommandManager.CheckStatements checkStatements) {
    list = new ArrayList(list);
    SpecializedCommandManager.CheckStatements statements =
        checkStatements == null
            ? new SpecializedCommandManager.CheckStatements()
            : checkStatements;

    while (!list.isEmpty()) {
      if (!this.plugin.isEnabled()) {
        return;
      }

      String cmd = (String) list.remove(0);
      if (!cmd.startsWith("delay!") && !cmd.toLowerCase().startsWith("softdelay!")) {
        if (!this.executeCmd(string, cmd, player, cmiInteractType, commandSender, statements)) {
          break;
        }
      } else {
        double delaySeconds = -1.0;
        String globalKey = null;
        String[] parts = cmd.split(" ");
        if (parts.length > 2) {
          globalKey = parts[2];
        }

        try {
          delaySeconds = Double.parseDouble(parts[1]);
        } catch (Exception exception) {
          try {
            delaySeconds +=
                Double.parseDouble(
                    this.plugin
                        .getPlaceholderAPIManager()
                        .updatePlaceHolders(player, cmd.split(" ")[1]));
          } catch (Throwable throwable) {
            CMIMessages.consoleMessage("Failed to determine delay amount, check spelling");
            continue;
          }
        }

        ArrayList remainingCmds = new ArrayList(list);
        if (delaySeconds > 0.0) {
          SpecializedCommandManager.runAway runaway = null;
          if (globalKey == null) {
            if (player != null) {
              var runawayMap = this.runaway.getOrDefault(player.getUniqueId(), new HashMap<>());
              runaway = (SpecializedCommandManager.runAway) runawayMap.get(string);
              if (runaway == null) {
                runaway =
                    new SpecializedCommandManager.runAway(
                        (long) (System.currentTimeMillis() + delaySeconds * 1000.0));
                runawayMap.put(string, runaway);
                this.runaway.put(player.getUniqueId(), runawayMap);
              }
            }
          } else {
            runaway = this.globalRunaway.get(globalKey.toLowerCase());
            if (runaway != null) {
              runaway.stopSchedTask();
            } else {
              runaway =
                  new SpecializedCommandManager.runAway(
                      (long) (System.currentTimeMillis() + delaySeconds * 1000.0));
            }

            this.globalRunaway.put(globalKey.toLowerCase(), runaway);
          }

          if (runaway == null) {
            runaway =
                new SpecializedCommandManager.runAway(
                    (long) (System.currentTimeMillis() + delaySeconds * 1000.0));
          }

          runaway.setSchedTask(
              CMIScheduler.runTaskLater(
                  this.plugin,
                  () ->
                      this.process(
                          string,
                          remainingCmds,
                          player,
                          cmiInteractType,
                          commandSender,
                          statements),
                  (int) (20.0 * delaySeconds)));
        }
        break;
      }
    }
  }

  public specCommand processSpecializedCommand(String string) {
    specCommand specCommand = new specCommand();
    ArrayList<String> commandParts = new ArrayList();
    if (string.contains(" ")) {
      commandParts.addAll(Arrays.asList(string.split(" ")));
    } else {
      commandParts.add(string);
    }

    ArrayList<String> remainingParts = new ArrayList<>(commandParts);

    for (String token : commandParts) {
      SpecializedCommandManager.specCommandAction action =
          SpecializedCommandManager.specialisedCommand.get(token);
      if (action == null) {
        break;
      }

      specCommand.add(action);
      remainingParts.remove(0);
    }

    string = "";

    for (String remainingToken : remainingParts) {
      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + remainingToken;
    }

    specCommand.setCmd(string);
    return specCommand;
  }

  public boolean isSpecializedCommand(String string) {
    specCommand specCommand = this.processSpecializedCommand(string);
    return !specCommand.getConditionList().isEmpty() || specCommand.getAction() != null;
  }

  public boolean executeCmd(String string, Player player) {
    return this.executeCmd(string, player, null);
  }

  public boolean executeCmd(String string, Player player, CMIInteractType cmiInteractType) {
    return this.executeCmd(string, player, cmiInteractType, null);
  }

  public boolean executeCmd(
      String string, Player player, CMIInteractType cmiInteractType, CommandSender commandSender) {
    return this.executeCmd(null, string, player, cmiInteractType, commandSender);
  }

  public boolean executeCmd(
      String string,
      String text,
      Player player,
      CMIInteractType cmiInteractType,
      CommandSender commandSender) {
    return this.executeCmd(string, text, player, cmiInteractType, commandSender, null);
  }

  public boolean executeCmd(
      String initializer,
      String cmd,
      Player player,
      CMIInteractType cmiInteractType,
      CommandSender commandSender,
      SpecializedCommandManager.CheckStatements checkStatements) {
    String originalCmd = cmd;
    if (player != null && !player.isOnline()) {
      player = null;
    }

    if (cmd.startsWith(" ")) {
      cmd = cmd.substring(1);
    }

    Matcher matcher = this.patern.matcher(cmd);
    if (cmd.toLowerCase().contains("ptarget:! ")) {
      return false;
    }

    boolean ptargetMatched = matcher.find();
    if (ptargetMatched) {
      if (player != null) {
        CMI.getInstance()
            .consoleMessage(
                "&c"
                    + player.getName()
                    + " tried to perform specilized command with ptarget:[name]! condition. This"
                    + " only allowed through console!");
        CMI.getInstance().consoleMessage("&c" + cmd.replace("asPlayer! ", ""));
        return false;
      }

      String ptargetCondition = matcher.group(0);
      String ptargetName = matcher.group(2);
      player = CMIUser.getOnlinePlayer(ptargetName);
      cmd = cmd.substring(ptargetCondition.length());
    }

    if (cmd.startsWith(" ")) {
      cmd = cmd.substring(1);
    }

    boolean applyPlaceholders = true;

    for (SpecializedCommandManager.specCommandAction action :
        this.processSpecializedCommand(cmd).getSubactions()) {
      if (action.getCmd().equals(SpecializedCommandManager.specialisedCommand.ph)) {
        applyPlaceholders =
            !action.getCmd().equals(SpecializedCommandManager.specialisedCommand.ph);
        break;
      }
    }

    if (applyPlaceholders) {
      cmd = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, cmd);
    }

    specCommand specCommand = this.processSpecializedCommand(cmd);
    cmd = specCommand.getCmd();
    ArrayList<com.Zrips.CMI.Modules.SpecializedCommands.SpecializedCommandManager.specCommandAction>
        statementsAndConditions = new ArrayList();
    boolean stopAfter = false;

    for (SpecializedCommandManager.specCommandAction statement : specCommand.getStatements()) {
      if (statement
          .getCmd()
          .getType()
          .equals(SpecializedCommandManager.specialisedCommandType.statement)) {
        if (statement.isStopAfter()) {
          stopAfter = true;
        }

        statementsAndConditions.add(statement);
      }
    }

    for (SpecializedCommandManager.specCommandAction condition : specCommand.getConditionList()) {
      if (condition
          .getCmd()
          .getType()
          .equals(SpecializedCommandManager.specialisedCommandType.condition)) {
        switch (condition.getCmd()) {
          case cooldown:
          case ucooldown:
          case gcooldown:
            if (cmd.isEmpty()) {
              condition.setValue2(originalCmd);
            } else {
              condition.setValue2(cmd);
            }

            condition.setInitializer(initializer);
          default:
            if (condition.isStopAfter()) {
              stopAfter = true;
            }

            statementsAndConditions.add(condition);
        }
      }
    }

    SpecializedCommandManager.specialisedCommand actionType = specCommand.getAction();
    actionType =
        actionType == null ? SpecializedCommandManager.specialisedCommand.fromConsole : actionType;
    boolean requiresBPermission = false;

    for (SpecializedCommandManager.specCommandAction bpermissionAction : statementsAndConditions) {
      if (bpermissionAction
          .getCmd()
          .equals(SpecializedCommandManager.specialisedCommand.bpermission)) {
        requiresBPermission = true;
        break;
      }
    }

    if (!specCommand.containsSubaction(SpecializedCommandManager.specialisedCommand.ch)) {
      cmd = CMIChatColor.translate(cmd);
    }

    for (SpecializedCommandManager.specCommandAction subaction : specCommand.getSubactions()) {
      if (subaction.getCmd().equals(SpecializedCommandManager.specialisedCommand.closeinv)
          && player != null
          && player.isOnline()) {
        CMIUser.closeInventory(player);
      }
    }

    switch (actionType) {
      case msg:
        cmd = this.plugin.getLM().filterNewLine(cmd);
        String msgTarget = cmd.contains(" ") ? cmd.split(" ")[0] : cmd;
        String msgText = cmd.substring(msgTarget.length() + (cmd.contains(" ") ? 1 : 0));
        Player msgPlayer = CMIUser.getOnlinePlayer(msgTarget);
        SpecializedCommandManager.failType msgFail = SpecializedCommandManager.failType.none;
        msgFail =
            this.canPerformCommand(
                initializer,
                msgPlayer,
                statementsAndConditions,
                true,
                cmiInteractType,
                checkStatements,
                false);
        processCondition(msgFail, checkStatements);
        if (msgFail.equals(SpecializedCommandManager.failType.cancel)) {
          return false;
        }

        if (msgFail.equals(SpecializedCommandManager.failType.none) && msgPlayer != null) {
          msgPlayer.sendMessage(msgText);
          if (stopAfter) {
            return false;
          }
        }
        break;
      case broadcast:
        cmd = this.plugin.getLM().filterNewLine(cmd);
        SpecializedCommandManager.failType broadcastFail = SpecializedCommandManager.failType.none;
        broadcastFail =
            this.canPerformCommand(
                initializer,
                player,
                statementsAndConditions,
                true,
                cmiInteractType,
                checkStatements,
                false);
        processCondition(broadcastFail, checkStatements);
        if (broadcastFail.equals(SpecializedCommandManager.failType.cancel)) {
          return false;
        }

        for (Player broadcastPlayer : Bukkit.getOnlinePlayers()) {
          if (cmd == null || cmd.isEmpty()) {
            return true;
          }

          if (broadcastFail.equals(SpecializedCommandManager.failType.none)) {
            broadcastPlayer.sendMessage(cmd);
          }
        }
        break;
      case actionbar:
        for (Player actionbarPlayer : Bukkit.getOnlinePlayers()) {
          SpecializedCommandManager.failType actionbarFail =
              SpecializedCommandManager.failType.none;
          actionbarFail =
              this.canPerformCommand(
                  initializer,
                  actionbarPlayer,
                  statementsAndConditions,
                  true,
                  cmiInteractType,
                  checkStatements,
                  false);
          processCondition(actionbarFail, checkStatements);
          if (!actionbarFail.equals(SpecializedCommandManager.failType.cancel)) {
            if (cmd == null || cmd.isEmpty()) {
              return true;
            }

            if (actionbarFail.equals(SpecializedCommandManager.failType.none)) {
              CMIActionBar.send(actionbarPlayer, cmd);
            }
          }
        }
        break;
      case title:
        for (Player titlePlayer : Bukkit.getOnlinePlayers()) {
          SpecializedCommandManager.failType titleFail = SpecializedCommandManager.failType.none;
          titleFail =
              this.canPerformCommand(
                  initializer,
                  titlePlayer,
                  statementsAndConditions,
                  true,
                  cmiInteractType,
                  checkStatements,
                  false);
          processCondition(titleFail, checkStatements);
          if (!titleFail.equals(SpecializedCommandManager.failType.cancel)
              && titleFail.equals(SpecializedCommandManager.failType.none)) {
            CMITitleMessage.send(titlePlayer, cmd, null, 10, 80, 10);
          }
        }
        break;
      case subtitle:
        for (Player subtitlePlayer : Bukkit.getOnlinePlayers()) {
          SpecializedCommandManager.failType subtitleFail = SpecializedCommandManager.failType.none;
          subtitleFail =
              this.canPerformCommand(
                  initializer,
                  subtitlePlayer,
                  statementsAndConditions,
                  true,
                  cmiInteractType,
                  checkStatements,
                  false);
          processCondition(subtitleFail, checkStatements);
          if (!subtitleFail.equals(SpecializedCommandManager.failType.cancel)
              && subtitleFail.equals(SpecializedCommandManager.failType.none)) {
            CMITitleMessage.send(subtitlePlayer, null, cmd, 10, 80, 10);
          }
        }
        break;
      case kickall:
        for (Player kickPlayer : Bukkit.getOnlinePlayers()) {
          SpecializedCommandManager.failType kickFail = SpecializedCommandManager.failType.none;
          kickFail =
              this.canPerformCommand(
                  initializer,
                  kickPlayer,
                  statementsAndConditions,
                  true,
                  cmiInteractType,
                  checkStatements,
                  true);
          processCondition(kickFail, checkStatements);
          if (!kickFail.equals(SpecializedCommandManager.failType.cancel)) {
            if (cmd == null || cmd.isEmpty()) {
              return true;
            }

            CMIPlayerKickEvent cmiPlayerKickEvent =
                new CMIPlayerKickEvent(
                    commandSender, kickPlayer.getUniqueId(), cmd.replace("\\n", "\n"));
            Bukkit.getPluginManager().callEvent(cmiPlayerKickEvent);
            if (!cmiPlayerKickEvent.isCancelled()
                && kickFail.equals(SpecializedCommandManager.failType.none)) {
              kickPlayer.kickPlayer(cmd.replace("\\n", "\n"));
            }
          }
        }
        break;
      case fromConsole:
      case asConsole:
      default:
        cmd = this.plugin.getLM().filterNewLine(cmd);
        SpecializedCommandManager.failType fallbackFail = SpecializedCommandManager.failType.none;
        fallbackFail =
            this.canPerformCommand(
                initializer,
                player,
                statementsAndConditions,
                false,
                cmiInteractType,
                checkStatements,
                !applyPlaceholders);
        processCondition(fallbackFail, checkStatements);
        if (fallbackFail.equals(SpecializedCommandManager.failType.cancel)) {
          return false;
        }

        if (isOpCmd(cmd) || cmd == null || cmd.isEmpty()) {
          return true;
        }

        if (fallbackFail.equals(SpecializedCommandManager.failType.none)) {
          boolean dispatched = false;
          if (applyPlaceholders) {
            cmd = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, cmd);
          }

          commandSender =
              (CommandSender) (commandSender == null ? Bukkit.getConsoleSender() : commandSender);
          if (Version.isCurrentHigher(Version.v1_7_R4)) {
            try {
              if (commandSender instanceof ConsoleCommandSender) {
                ServerCommandEvent serverCommandEvent = new ServerCommandEvent(commandSender, cmd);
                Bukkit.getPluginManager().callEvent(serverCommandEvent);
                dispatched =
                    !serverCommandEvent.isCancelled()
                        ? this.dispatch(commandSender, serverCommandEvent.getCommand())
                        : true;
              } else if (commandSender instanceof RemoteConsoleCommandSender) {
                RemoteServerCommandEvent remoteServerCommandEvent =
                    new RemoteServerCommandEvent(commandSender, cmd);
                Bukkit.getPluginManager().callEvent(remoteServerCommandEvent);
                dispatched =
                    !remoteServerCommandEvent.isCancelled()
                        ? this.dispatch(commandSender, remoteServerCommandEvent.getCommand())
                        : true;
              }
            } catch (Throwable throwable) {
              CMIMessages.consoleMessage("Failed to process command: " + cmd);
              throwable.printStackTrace();
              dispatched = this.dispatch(commandSender, cmd);
            }
          } else {
            dispatched = this.dispatch(commandSender, cmd);
          }

          if (dispatched) {
            this.deductForCommand(player, statementsAndConditions);
          }

          if (stopAfter) {
            return false;
          }
        }
        break;
      case asFakeOp:
        if (!Config.UseFakeOperator) {
          CMIMessages.consoleMessage(
              "&cTried to use asFakeOp! specialized command variable while UseFakeOperator is"
                  + " disabled in config file. Command got ignored");
        } else {
          cmd = this.plugin.getLM().filterNewLine(cmd);
          if (cmd == null || cmd.isEmpty()) {
            return true;
          }

          player = this.plugin.getPlayerManager().getFakeOperator();
          PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
              new PlayerCommandPreprocessEvent(player, "/" + cmd);
          Bukkit.getPluginManager().callEvent(playerCommandPreprocessEvent);
          if (!playerCommandPreprocessEvent.isCancelled()) {
            String fakeOpMessage = playerCommandPreprocessEvent.getMessage();
            if (fakeOpMessage.startsWith("/")) {
              fakeOpMessage = fakeOpMessage.substring(1, fakeOpMessage.length());
            }

            this.dispatch(player, fakeOpMessage);
            if (stopAfter) {
              return false;
            }
          } else if (!informed) {
            CMIMessages.consoleMessage(
                "&eTried to use asFakeOp! specialized command variable but event got canceled");
            informed = true;
          }
        }
        break;
      case likePlayer:
      case asPlayer:
        cmd = this.plugin.getLM().filterNewLine(cmd);
        SpecializedCommandManager.failType asPlayerFail = SpecializedCommandManager.failType.none;
        asPlayerFail =
            this.canPerformCommand(
                initializer,
                player,
                statementsAndConditions,
                false,
                cmiInteractType,
                checkStatements,
                false);
        processCondition(asPlayerFail, checkStatements);
        if (asPlayerFail.equals(SpecializedCommandManager.failType.cancel)) {
          return false;
        }

        if (asPlayerFail.equals(SpecializedCommandManager.failType.none)
            && player != null
            && player.isOnline()) {
          Boolean performed = false;
          if (cmd == null || cmd.isEmpty()) {
            return true;
          }

          PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, "/" + cmd);
          Bukkit.getPluginManager().callEvent(event);
          if (!event.isCancelled()) {
            if (this.plugin.getWarmUpManager().isWarmupCommand(player, event.getMessage())) {
              performed =
                  this.plugin.getWarmUpManager().startCmdUsage(player, event.getMessage(), false);
            } else {
              performed =
                  this.processBypassCommand(player, event.getMessage(), requiresBPermission);
            }
          } else {
            performed = true;
          }

          if (performed) {
            this.deductForCommand(player, statementsAndConditions);
            if (stopAfter) {
              return false;
            }
          }
        }
        break;
      case allPlayers:
        for (Player allPlayer : Bukkit.getOnlinePlayers()) {
          SpecializedCommandManager.failType allPlayersFail =
              SpecializedCommandManager.failType.none;
          allPlayersFail =
              this.canPerformCommand(
                  initializer,
                  allPlayer,
                  statementsAndConditions,
                  true,
                  cmiInteractType,
                  checkStatements,
                  true);
          processCondition(allPlayersFail, checkStatements);
          if (!allPlayersFail.equals(SpecializedCommandManager.failType.cancel)) {
            if (isOpCmd(cmd)) {
              return true;
            }

            if (cmd == null || cmd.isEmpty()) {
              return true;
            }

            if (allPlayersFail.equals(SpecializedCommandManager.failType.none)) {
              this.plugin.performCommand(
                  Bukkit.getConsoleSender(), cmd.replace("[allPlayers]", allPlayer.getName()));
            }
          }
        }
    }

    return true;
  }

  private boolean processBypassCommand(Player player, String command, boolean requiresBypass) {
    Boolean performed = null;
    if (requiresBypass) {
      if (command.startsWith("/" + CommandsHandler.getLabel() + " ")) {
        String commandName =
            command.substring(("/" + CommandsHandler.getLabel() + " ").length()).split(" ")[0];
        CMICommand cmiCommand =
            CMI.getInstance().getCommandManager().getCommands().get(commandName.toLowerCase());
        if (cmiCommand != null) {
          String commandArgsText =
              command
                  .substring(("/" + CommandsHandler.getLabel() + " ").length())
                  .substring(commandName.length());
          if (commandArgsText.startsWith(" ")) {
            commandArgsText = commandArgsText.substring(1);
          }

          String[] argsArray =
              commandArgsText.isEmpty() ? new String[0] : commandArgsText.split(" ");
          performed = cmiCommand.getCmdClass().perform(CMI.getInstance(), player, argsArray);
          performed = performed == null ? false : performed;
        } else {
          String commandText = command;
          if (commandText.startsWith("/")) {
            commandText = commandText.substring(1, commandText.length());
          }

          performed = this.dispatch(player, commandText);
        }
      }
    } else {
      String commandTextCopy = command;
      if (commandTextCopy.startsWith("/")) {
        commandTextCopy = commandTextCopy.substring(1, commandTextCopy.length());
      }

      performed = this.dispatch(player, commandTextCopy);
    }

    return performed;
  }

  private static void processCondition(
      SpecializedCommandManager.failType failure,
      SpecializedCommandManager.CheckStatements checkStatements) {
    if (checkStatements != null && checkStatements.getTemp() != null) {
      checkStatements.setStatement(
          checkStatements.getTemp(), failure == SpecializedCommandManager.failType.none);
      checkStatements.setTemp(null);
    }
  }

  private UUID getUUID(CommandSender commandSender) {
    return commandSender instanceof Player
        ? ((Player) commandSender).getUniqueId()
        : this.plugin.getServerUUID();
  }

  public synchronized boolean dispatch(CommandSender commandSender, String commandText) {
    if (commandSender != null && commandText != null) {
      if (Config.InfiniteLoopDetection) {
        UUID uuid = this.getUUID(commandSender);
        overflowCommands overflowCommands = repeatingCommands.get(uuid);
        if (overflowCommands == null) {
          overflowCommands = new overflowCommands(commandText);
          repeatingCommands.put(uuid, overflowCommands);
        } else if (overflowCommands.getCommands().contains(commandText)) {
          if (overflowCommands.getTime() + 50L > System.currentTimeMillis()) {
            overflowCommands.addRepeat();
            overflowCommands.setTime(System.currentTimeMillis());
          } else {
            overflowCommands.getCommands().clear();
            overflowCommands.addCommand(commandText);
            overflowCommands.setRepeat(1);
            overflowCommands.setTime(System.currentTimeMillis());
          }

          if (overflowCommands.getRepeat() > this.maxRepeats) {
            CMIMessages.consoleMessage(
                "&cReapeating command detected while performing specialized command ("
                    + commandText
                    + ")");
            CMIMessages.sendMessage(
                commandSender,
                "&cInfinite loop detected! Please update your commands relating to (&4"
                    + commandText
                    + "&c)");
            return false;
          }
        } else {
          if (overflowCommands.getTime() + 50L < System.currentTimeMillis()) {
            overflowCommands.getCommands().clear();
          }

          overflowCommands.setRepeat(1);
          overflowCommands.addCommand(commandText);
          overflowCommands.setTime(System.currentTimeMillis());
        }
      }

      String[] parts = PATTERN_ON_SPACE.split(commandText);
      if (parts.length == 0) {
        return false;
      }

      String label = parts[0].toLowerCase(Locale.ENGLISH);
      Command command = this.plugin.getNMS().getCommand(label);
      if (command == null) {
        try {
          return Bukkit.dispatchCommand(commandSender, commandText);
        } catch (Throwable throwable) {
          CMIMessages.consoleMessage("Failed to process command: " + commandText);
          throwable.printStackTrace();
          return false;
        }
      } else {
        boolean executed = false;

        try {
          executed =
              command.execute(commandSender, label, Arrays.copyOfRange(parts, 1, parts.length));
        } catch (Throwable throwable2) {
          CMIMessages.consoleMessage("Failed to process command: " + commandText);

          try {
            executed = Bukkit.dispatchCommand(commandSender, commandText);
          } catch (Throwable throwable3) {
            CMIMessages.consoleMessage("Failed to process command: " + commandText);
            throwable3.printStackTrace();
            return false;
          }
        }

        return executed;
      }
    } else {
      return false;
    }
  }

  private static boolean isOpCmd(String string) {
    return string.toLowerCase().startsWith("op")
        || string.toLowerCase().startsWith("minecraft:op")
        || string.toLowerCase().startsWith("deop")
        || string.toLowerCase().startsWith("minecraft:deop");
  }

  private SpecializedCommandManager.failType deductForCommand(
      Player player, List<SpecializedCommandManager.specCommandAction> list) {
    if (player == null) {
      return SpecializedCommandManager.failType.none;
    }

    for (SpecializedCommandManager.specCommandAction action : list) {
      switch (action.getCmd()) {
        case permission:
        case group:
        case bpermission:
        case hasMoney:
        case hasItem:
        case hasExactItem:
        case hasExp:
        case votes:
        case cooldown:
        case ucooldown:
        case gcooldown:
        case ifonline:
        case ifoffline:
        default:
          break;
        case money:
          if (action.getValue() == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          Double moneyAmount = Double.parseDouble((String) action.getValue());
          if (!this.plugin.getPlayerManager().getUser(player).hasMoney(moneyAmount)) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          this.plugin.getPlayerManager().getUser(player).withdraw(moneyAmount);
          break;
        case exp:
          if (action.getValue() == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          Double expAmount = Double.parseDouble((String) action.getValue());
          if (this.plugin.getPlayerManager().getUser(player).getTotalExp() < expAmount) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          this.plugin.getPlayerManager().getUser(player).takeExp(expAmount.intValue());
          break;
        case item:
        case exactItem:
          if (action.getValue() == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          CMIItemStack cmiItemStack = CMIItemStack.deserialize((String) action.getValue());
          if (cmiItemStack == null) {
            CMILC.info_specializedItemFail.sendMessage(player, "[value]", action.getValue());
            return SpecializedCommandManager.failType.cancel;
          }

          if (!this.plugin
              .getPlayerManager()
              .getUser(player)
              .getInventory()
              .has(
                  cmiItemStack.getItemStack(),
                  action.getCmd() == SpecializedCommandManager.specialisedCommand.exactItem,
                  CMIPlayerInventory.itemCheckType.material,
                  CMIPlayerInventory.itemCheckType.name,
                  CMIPlayerInventory.itemCheckType.lore,
                  CMIPlayerInventory.itemCheckType.custommodel)) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          this.plugin
              .getPlayerManager()
              .getUser(player)
              .getInventory()
              .removeItemByCriteria(
                  cmiItemStack.getItemStack(),
                  action.getCmd() == SpecializedCommandManager.specialisedCommand.exactItem,
                  CMIPlayerInventory.itemCheckType.material,
                  CMIPlayerInventory.itemCheckType.name,
                  CMIPlayerInventory.itemCheckType.lore,
                  CMIPlayerInventory.itemCheckType.custommodel);
      }
    }

    return SpecializedCommandManager.failType.none;
  }

  private SpecializedCommandManager.failType canPerformCommand(
      String initializer,
      Player player,
      List<SpecializedCommandManager.specCommandAction> list,
      boolean performActions,
      CMIInteractType cmiInteractType,
      SpecializedCommandManager.CheckStatements checkStatements,
      boolean applyPlaceholders) {
    actionLoop:
    for (SpecializedCommandManager.specCommandAction action : list) {
      String argValue = null;
      if (action.getValue() != null) {
        argValue = (String) action.getValue();
      }

      if (applyPlaceholders && argValue != null && player != null) {
        argValue = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, argValue);
      }

      switch (action.getCmd()) {
        case permission:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (action.isOpposite()) {
            if (PermissionsManager.CMIPerm.hasPermission(player, argValue, false)) {
              if (action.isNeedToInform()) {
                boolean informOnError =
                    CMI.getInstance().getConfigManager().isPermisionOnError()
                        || PermissionsManager.CMIPerm.permisiononerror.hasPermission(player);
                RawMessage rawMessage = new RawMessage();
                rawMessage
                    .addText(LC.info_CantHavePermission.getLocale(new Object[0]))
                    .addHover(informOnError ? (String) action.getValue() : null);
                rawMessage.show(player);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
          } else if (!PermissionsManager.CMIPerm.hasPermission(
              player, argValue, action.isNeedToInform())) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
          break;
        case group:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          String playerGroup = this.plugin.getPlayerManager().getUser(player).getGroupName();
          if (action.isOpposite()) {
            if (playerGroup.equalsIgnoreCase(argValue)) {
              if (action.isNeedToInform()) {
                LC.info_WrongGroup.sendMessage(player, new Object[0]);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
          } else if (!playerGroup.equalsIgnoreCase(argValue)) {
            if (action.isNeedToInform()) {
              LC.info_WrongGroup.sendMessage(player, new Object[0]);
            }

            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case bpermission:
        case ptarget:
        default:
          break;
        case money:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            Double moneyNeeded = Double.parseDouble(argValue);
            if (!this.plugin.getPlayerManager().getUser(player).hasMoney(moneyNeeded)) {
              if (action.isNeedToInform()) {
                LC.econ_noMoney.sendMessage(player, new Object[0]);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }

            if (performActions) {
              this.plugin.getPlayerManager().getUser(player).withdraw(moneyNeeded);
            }
            break;
          } catch (Exception | Error error) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case exp:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            Double expNeeded = Double.parseDouble(argValue);
            if (CMIUser.getUser(player).getTotalExp() < expNeeded) {
              if (action.isNeedToInform()) {
                CMILC.Information_NotEnoughExpNeed.sendMessage(
                    player, "[need]", expNeeded, "[Exp]", CMIUser.getUser(player).getTotalExp());
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }

            if (performActions) {
              this.plugin.getPlayerManager().getUser(player).takeExp(expNeeded.intValue());
            }
            break;
          } catch (Throwable throwable) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case hasMoney:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            Double hasMoneyNeeded = Double.parseDouble(argValue);
            if (action.isOpposite()) {
              if (this.plugin.getPlayerManager().getUser(player).hasMoney(hasMoneyNeeded)) {
                if (action.isNeedToInform()) {
                  LC.econ_tooMuchMoney.sendMessage(player, new Object[0]);
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (!this.plugin.getPlayerManager().getUser(player).hasMoney(hasMoneyNeeded)) {
              if (action.isNeedToInform()) {
                LC.econ_noMoney.sendMessage(player, new Object[0]);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Throwable throwable2) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case hasItem:
        case hasExactItem:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          CMIItemStack requiredItem = CMIItemStack.deserialize(argValue);
          if (requiredItem == null) {
            CMILC.info_specializedItemFail.sendMessage(player, "[error]", argValue);
            return SpecializedCommandManager.failType.cancel;
          }

          boolean hasItemResult =
              CMIUser.getUser(player)
                  .getInventory()
                  .has(
                      requiredItem.getItemStack(),
                      action.getCmd() == SpecializedCommandManager.specialisedCommand.hasExactItem,
                      CMIPlayerInventory.itemCheckType.material,
                      CMIPlayerInventory.itemCheckType.name,
                      CMIPlayerInventory.itemCheckType.lore,
                      CMIPlayerInventory.itemCheckType.custommodel,
                      CMIPlayerInventory.itemCheckType.durability);
          if (action.isOpposite()) {
            if (hasItemResult) {
              if (action.isNeedToInform()) {
                LC.info_HaveItem.sendMessage(
                    player,
                    new Object[] {
                      "[itemName]",
                      requiredItem.getRealName(),
                      "[amount]",
                      requiredItem.getItemStack().getAmount()
                    });
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
          } else if (!hasItemResult) {
            if (action.isNeedToInform()) {
              LC.info_dontHaveItem.sendMessage(
                  player,
                  new Object[] {
                    "[itemName]",
                    requiredItem.getRealName(),
                    "[amount]",
                    requiredItem.getItemStack().getAmount()
                  });
            }

            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
          break;
        case item:
        case exactItem:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          CMIItemStack itemToRemove = CMIItemStack.deserialize(argValue);
          if (itemToRemove == null) {
            CMILC.info_specializedItemFail.sendMessage(player, "[error]", argValue);
            return SpecializedCommandManager.failType.cancel;
          }

          if (!this.plugin
              .getPlayerManager()
              .getUser(player)
              .getInventory()
              .has(
                  itemToRemove.getItemStack(),
                  action.getCmd() == SpecializedCommandManager.specialisedCommand.exactItem,
                  CMIPlayerInventory.itemCheckType.material,
                  CMIPlayerInventory.itemCheckType.name,
                  CMIPlayerInventory.itemCheckType.lore,
                  CMIPlayerInventory.itemCheckType.custommodel)) {
            if (action.isNeedToInform()) {
              LC.info_dontHaveItem.sendMessage(
                  player,
                  new Object[] {
                    "[itemName]",
                    itemToRemove.getRealName(),
                    "[amount]",
                    itemToRemove.getItemStack().getAmount()
                  });
            }

            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          if (performActions) {
            this.plugin
                .getPlayerManager()
                .getUser(player)
                .getInventory()
                .removeItemByCriteria(
                    itemToRemove.getItemStack(),
                    action.getCmd() == SpecializedCommandManager.specialisedCommand.exactItem,
                    CMIPlayerInventory.itemCheckType.material,
                    CMIPlayerInventory.itemCheckType.name,
                    CMIPlayerInventory.itemCheckType.lore,
                    CMIPlayerInventory.itemCheckType.custommodel);
          }
          break;
        case hasExp:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            Double hasExpNeeded = Double.parseDouble(argValue);
            if (action.isOpposite()) {
              if (this.plugin.getPlayerManager().getUser(player).getTotalExp() > hasExpNeeded) {
                if (action.isNeedToInform()) {
                  CMILC.Information_tooMuchExp.sendMessage(
                      player,
                      "[need]",
                      hasExpNeeded,
                      "[Exp]",
                      CMIUser.getUser(player).getTotalExp());
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (this.plugin.getPlayerManager().getUser(player).getTotalExp()
                < hasExpNeeded) {
              if (action.isNeedToInform()) {
                CMILC.Information_NotEnoughExpNeed.sendMessage(
                    player, "[need]", hasExpNeeded, "[Exp]", CMIUser.getUser(player).getTotalExp());
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Throwable throwable3) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case votes:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            Double votesNeeded = Double.parseDouble(argValue);
            if (action.isOpposite()) {
              if (this.plugin.getPlayerManager().getUser(player).getVotifierVotes() > votesNeeded) {
                if (action.isNeedToInform()) {
                  CMILC.Information_TooMuchVotes.sendMessage(
                      player,
                      "[votes]",
                      this.plugin.getPlayerManager().getUser(player).getVotifierVotes());
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (this.plugin.getPlayerManager().getUser(player).getVotifierVotes()
                < votesNeeded) {
              if (action.isNeedToInform()) {
                CMILC.Information_NotEnoughVotes.sendMessage(
                    player,
                    "[votes]",
                    this.plugin.getPlayerManager().getUser(player).getVotifierVotes());
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Exception | Error error2) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case cooldown:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            String cooldownKey = (String) action.getValue2();
            if (cooldownKey.length() > 64) {
              cooldownKey = cooldownKey.substring(0, 64);
            }

            if (!this.plugin
                .getCooldownManager()
                .canUseSpecCommand(
                    player,
                    new SpecCMDCooldown()
                        .setCmd(cooldownKey)
                        .setCooldown(Double.parseDouble(argValue)),
                    action.isNeedToInform())) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Throwable throwable4) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case ucooldown:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            String globalCooldownKey = (String) action.getValue2();
            if (globalCooldownKey.length() > 64) {
              globalCooldownKey = globalCooldownKey.substring(0, 64);
            }

            if (!this.plugin
                .getCooldownManager()
                .canUseSpecCommand(
                    player,
                    new SpecCMDCooldown()
                        .setCmd(
                            action.getInitializer() == null
                                ? globalCooldownKey
                                : action.getInitializer())
                        .setCooldown(Double.parseDouble(argValue)),
                    action.isNeedToInform())) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Throwable throwable5) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case gcooldown:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            if (action.getInitializer() != null
                && this.plugin
                    .getCooldownManager()
                    .canUseSpecCommandGlobal(
                        player,
                        new SpecCMDCooldown()
                            .setCmd(action.getInitializer())
                            .setCooldown(Double.parseDouble(argValue)),
                        action.isNeedToInform())) {
              break;
            }

            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          } catch (Throwable throwable6) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case ifonline:
          if (argValue == null) {
            return SpecializedCommandManager.failType.cancel;
          }

          if (argValue.isEmpty()) {
            return SpecializedCommandManager.failType.cancel;
          }

          Player onlinePlayer = CMIUser.getOnlinePlayer(argValue);
          if (onlinePlayer == null) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
          break;
        case ifoffline:
          if (argValue == null) {
            return SpecializedCommandManager.failType.cancel;
          }

          if (argValue.isEmpty()) {
            return SpecializedCommandManager.failType.cancel;
          }

          Player offlinePlayer = CMIUser.getOnlinePlayer(argValue);
          if (offlinePlayer != null) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
          break;
        case ifempty:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          Integer requiredCount = null;
          String slotTypeText = argValue;
          if (slotTypeText.contains("-")) {
            try {
              requiredCount = Integer.parseInt(slotTypeText.split("-")[1]);
            } catch (Exception exception) {
            }

            slotTypeText = slotTypeText.split("-")[0];
          }

          SpecializedCommandManager.invEmptyType emptyType =
              SpecializedCommandManager.invEmptyType.getByName(slotTypeText);
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser == null) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          if (emptyType == null) {
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }

          switch (emptyType) {
            case hand:
              ItemStack handItem =
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.MainHand);
              if (handItem != null && handItem.getType() != Material.AIR) {
                if (action.isNeedToInform()) {
                  CMILC.info_InvEmpty_hand.sendMessage(cmiUser);
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }

              return SpecializedCommandManager.failType.none;
            case offhand:
              ItemStack offhandItem =
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.OffHand);
              if (offhandItem != null && offhandItem.getType() != Material.AIR) {
                if (action.isNeedToInform()) {
                  CMILC.info_InvEmpty_offhand.sendMessage(cmiUser);
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }

              return SpecializedCommandManager.failType.none;
            case quickbar:
              var quickbarItems =
                  cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.QuickBar);
              if (requiredCount != null) {
                int quickbarEmptySlots =
                    CMIPlayerInventory.CMIInventorySlot.QuickBar.getSlots().size();

                for (ItemStack quickbarSlotItem : quickbarItems) {
                  if (quickbarSlotItem != null && quickbarSlotItem.getType() != Material.AIR) {
                    quickbarEmptySlots--;
                  }
                }

                if (quickbarEmptySlots < requiredCount) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_quickbarslots.sendMessage(
                        cmiUser, "[count]", requiredCount);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              } else {
                for (ItemStack quickbarSlotItemValue : quickbarItems) {
                  if (quickbarSlotItemValue != null
                      && quickbarSlotItemValue.getType() != Material.AIR) {
                    if (action.isNeedToInform()) {
                      CMILC.info_InvEmpty_quickbar.sendMessage(cmiUser);
                    }

                    return action.isNeedToBreak()
                        ? SpecializedCommandManager.failType.cancel
                        : SpecializedCommandManager.failType.proceed;
                  }
                }
              }

              return SpecializedCommandManager.failType.none;
            case armor:
              for (ItemStack armorItem :
                  cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.Armor)) {
                if (armorItem != null && armorItem.getType() != Material.AIR) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_armor.sendMessage(cmiUser);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              }

              return SpecializedCommandManager.failType.none;
            case inv:
              var invItems =
                  cmiUser
                      .getInventory()
                      .getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory);
              invItems.addAll(
                  cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.Armor));
              invItems.addAll(
                  cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.OffHand));
              if (requiredCount != null) {
                int mainInvEmptySlots =
                    CMIPlayerInventory.CMIInventorySlot.MainInventory.getSlots().size();
                mainInvEmptySlots += CMIPlayerInventory.CMIInventorySlot.Armor.getSlots().size();
                mainInvEmptySlots += CMIPlayerInventory.CMIInventorySlot.OffHand.getSlots().size();

                for (ItemStack invSlotItem : invItems) {
                  if (invSlotItem != null && invSlotItem.getType() != Material.AIR) {
                    mainInvEmptySlots--;
                  }
                }

                if (mainInvEmptySlots < requiredCount) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_maininvslots.sendMessage(cmiUser, "[count]", requiredCount);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              } else {
                for (ItemStack invSlotItemValue : invItems) {
                  if (invSlotItemValue != null && invSlotItemValue.getType() != Material.AIR) {
                    if (action.isNeedToInform()) {
                      CMILC.info_InvEmpty_inv.sendMessage(cmiUser);
                    }

                    return action.isNeedToBreak()
                        ? SpecializedCommandManager.failType.cancel
                        : SpecializedCommandManager.failType.proceed;
                  }
                }
              }

              return SpecializedCommandManager.failType.none;
            case subinv:
              var subinvItems =
                  cmiUser
                      .getInventory()
                      .getItems(CMIPlayerInventory.CMIInventorySlot.PartInventory);
              if (requiredCount != null) {
                int subinvEmptySlots =
                    CMIPlayerInventory.CMIInventorySlot.PartInventory.getSlots().size();

                for (ItemStack subinvSlotItem : subinvItems) {
                  if (subinvSlotItem != null && subinvSlotItem.getType() != Material.AIR) {
                    subinvEmptySlots--;
                  }
                }

                if (subinvEmptySlots < requiredCount) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_subinvslots.sendMessage(cmiUser, "[count]", requiredCount);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              } else {
                for (ItemStack subinvSlotItemValue : subinvItems) {
                  if (subinvSlotItemValue != null
                      && subinvSlotItemValue.getType() != Material.AIR) {
                    if (action.isNeedToInform()) {
                      CMILC.info_InvEmpty_subinv.sendMessage(cmiUser);
                    }

                    return action.isNeedToBreak()
                        ? SpecializedCommandManager.failType.cancel
                        : SpecializedCommandManager.failType.proceed;
                  }
                }
              }

              return SpecializedCommandManager.failType.none;
            case maininv:
              var maininvItems =
                  cmiUser
                      .getInventory()
                      .getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory);
              if (requiredCount != null) {
                int maininvEmptySlots =
                    CMIPlayerInventory.CMIInventorySlot.MainInventory.getSlots().size();

                for (ItemStack maininvSlotItem : maininvItems) {
                  if (maininvSlotItem != null && maininvSlotItem.getType() != Material.AIR) {
                    maininvEmptySlots--;
                  }
                }

                if (maininvEmptySlots < requiredCount) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_maininvslots.sendMessage(cmiUser, "[count]", requiredCount);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              } else {
                for (ItemStack maininvSlotItemValue : maininvItems) {
                  if (maininvSlotItemValue != null
                      && maininvSlotItemValue.getType() != Material.AIR) {
                    if (action.isNeedToInform()) {
                      CMILC.info_InvEmpty_maininv.sendMessage(cmiUser);
                    }

                    return action.isNeedToBreak()
                        ? SpecializedCommandManager.failType.cancel
                        : SpecializedCommandManager.failType.proceed;
                  }
                }
              }

              return SpecializedCommandManager.failType.none;
            case ender:
              ItemStack[] enderItems;
              int enderSlotCount =
                  (enderItems = cmiUser.getPlayer().getEnderChest().getContents()).length;

              for (int enderSlotIndex = 0; enderSlotIndex < enderSlotCount; enderSlotIndex++) {
                ItemStack enderSlotItem = enderItems[enderSlotIndex];
                if (enderSlotItem != null && !enderSlotItem.getType().equals(Material.AIR)) {
                  if (action.isNeedToInform()) {
                    CMILC.info_InvEmpty_inv.sendMessage(cmiUser);
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
              }

              return SpecializedCommandManager.failType.none;
            default:
              continue;
          }
        case stopdelay:
          if (argValue != null && !argValue.toString().isEmpty()) {
            SpecializedCommandManager.runAway globalStop =
                this.globalRunaway.get(argValue.toLowerCase());
            if (globalStop == null) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }

            globalStop.stopSchedTask();
          } else {
            var runawayMap = this.runaway.get(player.getUniqueId());
            if (runawayMap != null) {
              SpecializedCommandManager.runAway localStop =
                  (SpecializedCommandManager.runAway) runawayMap.get(initializer.toLowerCase());
              if (localStop != null) {
                localStop.stopSchedTask();
              }
            }
          }
          break;
        case check:
          if (argValue == null) {
            return SpecializedCommandManager.failType.cancel;
          }

          Matcher matcher = this.checkPattern.matcher(argValue);
          if (matcher.find()) {
            String checkLeft = matcher.group(1);
            String checkRight = matcher.group(3);
            String checkOp = matcher.group(2);
            String checkOpCopy = checkOp;
            switch (checkOp.hashCode()) {
              case 60:
                if (!checkOpCopy.equals("<")) {
                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
                break;
              case 62:
                if (!checkOpCopy.equals(">")) {
                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
                break;
              case 1084:
                if (checkOpCopy.equals("!=")) {
                  try {
                    String leftPlain = CMIChatColor.stripColor(checkLeft);
                    String rightPlain = CMIChatColor.stripColor(checkRight);
                    ArrayList<String> rightValues = new ArrayList();
                    if (rightPlain == null) {
                      rightValues.add("NULL");
                    } else {
                      rightValues.addAll(Arrays.asList(rightPlain.split("\\|")));
                    }

                    if (!checkForMatch(rightValues, leftPlain)) {
                      continue;
                    }
                  } catch (Exception exception2) {
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              case 1921:
                if (!checkOpCopy.equals("<=")) {
                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
                break;
              case 1952:
                if (checkOpCopy.equals("==")) {
                  try {
                    String leftPlainValue = CMIChatColor.stripColor(checkLeft);
                    String rightPlainValue = CMIChatColor.stripColor(checkRight);
                    ArrayList<String> rightValuesValue = new ArrayList();
                    if (rightPlainValue == null) {
                      rightValuesValue.add("NULL");
                    } else {
                      rightValuesValue.addAll(Arrays.asList(rightPlainValue.split("\\|")));
                    }

                    if (checkForMatch(rightValuesValue, leftPlainValue)) {
                      continue;
                    }
                  } catch (Exception exception3) {
                  }

                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              case 1983:
                if (!checkOpCopy.equals(">=")) {
                  return action.isNeedToBreak()
                      ? SpecializedCommandManager.failType.cancel
                      : SpecializedCommandManager.failType.proceed;
                }
                break;
              default:
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
            }

            try {
              double leftNum = Double.parseDouble(checkLeft);
              double rightNum = Double.parseDouble(checkRight);
              String checkOpCopyValue = checkOp;
              switch (checkOp.hashCode()) {
                case 60:
                  if (checkOpCopyValue.equals("<") && leftNum < rightNum) {
                    continue;
                  }
                  break;
                case 62:
                  if (checkOpCopyValue.equals(">") && leftNum > rightNum) {
                    continue;
                  }
                  break;
                case 1921:
                  if (checkOpCopyValue.equals("<=") && leftNum <= rightNum) {
                    continue;
                  }
                  break;
                case 1983:
                  if (checkOpCopyValue.equals(">=") && leftNum >= rightNum) {
                    continue;
                  }
              }
            } catch (Exception exception4) {
            }
          }

          return action.isNeedToBreak()
              ? SpecializedCommandManager.failType.cancel
              : SpecializedCommandManager.failType.proceed;
        case contains:
          if (argValue == null) {
            return SpecializedCommandManager.failType.cancel;
          }

          this.containsPattern = Pattern.compile("(.+)?(=>)(.+)");
          Matcher containsMatcher = this.containsPattern.matcher(argValue);
          if (containsMatcher.find()) {
            String containsLeft = containsMatcher.group(1);
            String containsRight = containsMatcher.group(3);
            if (containsLeft == null || containsRight == null) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }

            containsLeft = containsLeft.replace("_", "").replace(" ", "");
            containsRight = containsRight.replace("_", "").replace(" ", "");
            String containsLeftNorm = CMIChatColor.stripColor(containsLeft.toLowerCase());
            String containsRightNorm = CMIChatColor.stripColor(containsRight.toLowerCase());
            ArrayList containsValues = new ArrayList();
            containsValues.addAll(Arrays.asList(containsRightNorm.split("\\|")));

            try {
              boolean containsMatch = checkForContains(containsValues, containsLeftNorm);
              if (!action.isOpposite() && containsMatch || action.isOpposite() && !containsMatch) {
                break;
              }
            } catch (Exception exception5) {
            }
          }

          return action.isNeedToBreak()
              ? SpecializedCommandManager.failType.cancel
              : SpecializedCommandManager.failType.proceed;
        case statement:
          if (argValue != null && checkStatements != null) {
            checkStatements.setTemp(argValue);
          }
          break;
        case if_:
          if (argValue != null && checkStatements != null) {
            Boolean statementValue = checkStatements.getStatementValue(argValue);
            if (statementValue == null) {
              return SpecializedCommandManager.failType.proceed;
            }

            if (action.isOpposite()) {
              if (statementValue) {
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (!statementValue) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          }

          return SpecializedCommandManager.failType.proceed;
        case ifhasair:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            int airRequired = Integer.parseInt(argValue);
            if (action.isOpposite()) {
              if (player.getRemainingAir() >= airRequired) {
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (player.getRemainingAir() < airRequired) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Exception | Error error3) {
            error3.printStackTrace();
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case ifhashunger:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            int foodRequired = Integer.parseInt(argValue);
            if (action.isOpposite()) {
              if (player.getFoodLevel() >= foodRequired) {
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (player.getFoodLevel() < foodRequired) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Exception | Error error4) {
            error4.printStackTrace();
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case ifhashealth:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          try {
            double parsed = Double.parseDouble(argValue);
            Player healthPlayer = player;
            if (action.isOpposite()) {
              if (healthPlayer.getHealth() >= parsed) {
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (healthPlayer.getHealth() < parsed) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          } catch (Exception | Error error5) {
            error5.printStackTrace();
            return action.isNeedToBreak()
                ? SpecializedCommandManager.failType.cancel
                : SpecializedCommandManager.failType.proceed;
          }
        case ifingamemode:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          boolean gameModeMatched = false;
          String[] gameModes;
          int partCount = (gameModes = argValue.split("\\|")).length;
          int partIndex = 0;

          while (true) {
            if (partIndex < partCount) {
              String gameModeName = gameModes[partIndex];
              if (!player.getGameMode().toString().equalsIgnoreCase(gameModeName.trim())) {
                partIndex++;
                continue;
              }

              gameModeMatched = true;
            }

            if (action.isOpposite()) {
              if (gameModeMatched) {
                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (!gameModeMatched) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            continue actionLoop;
          }
        case ifinworld:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          boolean worldMatched = false;
          String[] worldNames;
          int worldPartCount = (worldNames = argValue.split("\\|")).length;
          int worldPartIndex = 0;

          while (true) {
            if (worldPartIndex < worldPartCount) {
              String worldName = worldNames[worldPartIndex];
              World matchedWorld = CMIWorld.getWorld(worldName.trim());
              if (matchedWorld == null || player.getWorld() != matchedWorld) {
                worldPartIndex++;
                continue;
              }

              worldMatched = true;
            }

            if (action.isOpposite()) {
              if (worldMatched) {
                if (action.isNeedToInform()) {
                  LC.info_wrongWorld.sendMessage(player, new Object[0]);
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (!worldMatched) {
              if (action.isNeedToInform()) {
                LC.info_wrongWorld.sendMessage(player, new Object[0]);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            continue actionLoop;
          }
        case ifinportal:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (argValue == null) {
            return SpecializedCommandManager.failType.proceed;
          }

          boolean flag8Matched = false;
          String[] modeNames;
          int modePartCount = (modeNames = argValue.split("\\|")).length;
          int modePartIndex = 0;

          while (true) {
            if (modePartIndex < modePartCount) {
              String modeName = modeNames[modePartIndex];
              CMIPortal cmiPortal = this.plugin.getPortalManager().getByName(modeName.trim());
              if (cmiPortal == null || !cmiPortal.containsLoc(player.getLocation())) {
                modePartIndex++;
                continue;
              }

              flag8Matched = true;
            }

            if (action.isOpposite()) {
              if (flag8Matched) {
                if (action.isNeedToInform()) {
                  CMILC.info_wrongPortal.sendMessage(player);
                }

                return action.isNeedToBreak()
                    ? SpecializedCommandManager.failType.cancel
                    : SpecializedCommandManager.failType.proceed;
              }
            } else if (!flag8Matched) {
              if (action.isNeedToInform()) {
                CMILC.info_wrongPortal.sendMessage(player);
              }

              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            continue actionLoop;
          }
        case click:
          if (argValue != null && cmiInteractType != null) {
            CMIInteractType cmiInteractTypeValue = CMIInteractType.getByName(argValue);
            if (cmiInteractTypeValue == null) {
              return SpecializedCommandManager.failType.proceed;
            }

            if (cmiInteractTypeValue != cmiInteractType) {
              return action.isNeedToBreak()
                  ? SpecializedCommandManager.failType.cancel
                  : SpecializedCommandManager.failType.proceed;
            }
            break;
          }

          return SpecializedCommandManager.failType.proceed;
        case closeinv:
          if (player == null) {
            return SpecializedCommandManager.failType.none;
          }

          if (!player.isOnline()) {
            return SpecializedCommandManager.failType.none;
          }

          CMIScheduler.runAtEntity(this.plugin, player, player::closeInventory);
      }
    }

    return SpecializedCommandManager.failType.none;
  }

  private static boolean checkForMatch(List<String> list, String string) {
    string = string != null && !string.equalsIgnoreCase("[EMPTY]") ? string : "NULL";

    for (String entry : list) {
      entry = entry != null && !entry.equalsIgnoreCase("[EMPTY]") ? entry : "NULL";
      if (string.equalsIgnoreCase(entry)
          || (string.isEmpty() || entry.isEmpty()) && string.equalsIgnoreCase(entry)) {
        return true;
      }
    }

    return false;
  }

  private static boolean checkForContains(List<String> list, String string) {
    for (String entry : list) {
      if (string.contains(entry)) {
        return true;
      }
    }

    return false;
  }

  private class CheckStatements {
    HashMap<String, Boolean> statements = new HashMap<>();
    private String temp = null;

    private CheckStatements() {}

    public void setStatement(String string, boolean flag) {
      this.statements.put(string.toLowerCase(), flag);
    }

    public Boolean getStatementValue(String string) {
      return this.statements.get(string.toLowerCase());
    }

    public String getTemp() {
      return this.temp;
    }

    public void setTemp(String string) {
      this.temp = string;
    }
  }

  private enum failType {
    proceed,
    cancel,
    none;
  }

  private enum invEmptyType {
    hand,
    offhand,
    quickbar,
    armor,
    inv,
    subinv,
    maininv,
    ender;

    public static SpecializedCommandManager.invEmptyType getByName(String string) {
      SpecializedCommandManager.invEmptyType[] invEmptyTypes;
      int value = (invEmptyTypes = values()).length;

      for (int index = 0; index < value; index++) {
        SpecializedCommandManager.invEmptyType candidate = invEmptyTypes[index];
        if (candidate.toString().equalsIgnoreCase(string)) {
          return candidate;
        }
      }

      return null;
    }
  }

  class runAway {
    private Long time = null;
    private CMITask schedId = null;

    public runAway(Long timeMillis) {
      this.time = timeMillis;
    }

    public Long getTime() {
      return this.time;
    }

    public void setTime(Long timeMillis) {
      this.time = timeMillis;
    }

    public CMITask getSchedTask() {
      return this.schedId;
    }

    public void stopSchedTask() {
      if (this.schedId != null) {
        this.schedId.cancel();
        this.schedId = null;
      }
    }

    public void setSchedTask(CMITask cmiTask) {
      this.schedId = cmiTask;
    }
  }

  public class specCommandAction {
    private SpecializedCommandManager.specialisedCommand cmd;
    private boolean needToBreak = false;
    private boolean needToInform = false;
    private boolean stopAfter = false;
    private boolean opposite = false;
    private Object value = null;
    private Object value2 = null;
    private String initializer = null;

    public specCommandAction(SpecializedCommandManager.specialisedCommand command) {
      this.cmd = command;
    }

    public SpecializedCommandManager.specialisedCommand getCmd() {
      return this.cmd;
    }

    public void setCmd(SpecializedCommandManager.specialisedCommand command) {
      this.cmd = command;
    }

    public boolean isNeedToBreak() {
      return this.needToBreak;
    }

    public void setNeedToBreak(boolean flag) {
      this.needToBreak = flag;
    }

    public boolean isNeedToInform() {
      return this.needToInform;
    }

    public void setNeedToInform(boolean flag) {
      this.needToInform = flag;
    }

    public Object getValue() {
      return this.value;
    }

    public void setValue(Object object) {
      this.value = object;
    }

    public Object getValue2() {
      return this.value2;
    }

    public void setValue2(Object object) {
      this.value2 = object;
    }

    public boolean isOpposite() {
      return this.opposite;
    }

    public void setOpposite(boolean flag) {
      this.opposite = flag;
    }

    public String getInitializer() {
      return this.initializer;
    }

    public void setInitializer(String string) {
      this.initializer = string;
    }

    public boolean isStopAfter() {
      return this.stopAfter;
    }

    public void setStopAfter(boolean flag) {
      this.stopAfter = flag;
    }
  }

  public enum specialisedCommand {
    permission(
        "perm:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    group("group:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    bpermission(
        "bperm:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    money("moneycost:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    exp("expcost:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    hasMoney(
        "hasmoney:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    hasItem(
        "hasitem:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    hasExactItem(
        "hasexactitem:[value][@][?][#]!",
        SpecializedCommandManager.specialisedCommandType.condition),
    item("item:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    exactItem(
        "exactitem:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    hasExp("hasexp:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    votes("votes:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    cooldown("cooldown:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ucooldown(
        "ucooldown:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    gcooldown(
        "gcooldown:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifonline("ifonline:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifoffline(
        "ifoffline:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifempty("ifempty:[value][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    stopdelay("stopdelay:[value]!", SpecializedCommandManager.specialisedCommandType.condition),
    check(
        "check:[value1][==|>|>=|<|<=|!=][value2][?][#]!",
        SpecializedCommandManager.specialisedCommandType.condition),
    contains(
        "contains:[value1][=>][value2][@][#]!",
        SpecializedCommandManager.specialisedCommandType.condition),
    statement("statement:[value]!", SpecializedCommandManager.specialisedCommandType.statement),
    if_("if:[value][@][#]!", SpecializedCommandManager.specialisedCommandType.statement),
    ifhasair("ifhasair:[value][@][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifhashunger(
        "ifhashunger:[value][@][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifhashealth(
        "ifhashealth:[value][@][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifingamemode(
        "ifingamemode:[value][@][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifinworld(
        "ifinworld:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ifinportal(
        "ifinportal:[value][@][?][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    click("click:[value][#]!", SpecializedCommandManager.specialisedCommandType.condition),
    ptarget("ptarget:[value]!", SpecializedCommandManager.specialisedCommandType.condition),
    closeinv("closeinv!", SpecializedCommandManager.specialisedCommandType.subaction),
    ph("ph!", SpecializedCommandManager.specialisedCommandType.subaction),
    ch("ch!", SpecializedCommandManager.specialisedCommandType.subaction),
    msg("msg!", SpecializedCommandManager.specialisedCommandType.action),
    broadcast("broadcast!", SpecializedCommandManager.specialisedCommandType.action),
    actionbar("actionbar!", SpecializedCommandManager.specialisedCommandType.action),
    title("title!", SpecializedCommandManager.specialisedCommandType.action),
    subtitle("subtitle!", SpecializedCommandManager.specialisedCommandType.action),
    kickall("kickall!", SpecializedCommandManager.specialisedCommandType.action),
    fromConsole("fromConsole!", SpecializedCommandManager.specialisedCommandType.action),
    asConsole("asConsole!", SpecializedCommandManager.specialisedCommandType.action),
    asFakeOp("asFakeOp!", SpecializedCommandManager.specialisedCommandType.action),
    likePlayer("likePlayer!", SpecializedCommandManager.specialisedCommandType.action),
    asPlayer("asPlayer!", SpecializedCommandManager.specialisedCommandType.action),
    allPlayers("allPlayers!", SpecializedCommandManager.specialisedCommandType.action);

    private String format;
    private SpecializedCommandManager.specialisedCommandType type;

    specialisedCommand(
        String string, SpecializedCommandManager.specialisedCommandType commandType) {
      this.format = string;
      this.type = commandType;
    }

    public String getFormat() {
      return this.format;
    }

    public String getFormatForCheck() {
      return !this.getType().equals(SpecializedCommandManager.specialisedCommandType.action)
              && !this.getType().equals(SpecializedCommandManager.specialisedCommandType.subaction)
          ? this.format.split(":")[0].toLowerCase() + ":"
          : this.getFormat().toLowerCase();
    }

    public void setFormat(String string) {
      this.format = string;
    }

    public SpecializedCommandManager.specialisedCommandType getType() {
      return this.type;
    }

    public void setType(SpecializedCommandManager.specialisedCommandType commandType) {
      this.type = commandType;
    }

    public static SpecializedCommandManager.specCommandAction get(String string) {
      SpecializedCommandManager.specCommandAction action = null;
      SpecializedCommandManager.specialisedCommand[] specialisedCommands;
      int value = (specialisedCommands = values()).length;

      for (int index = 0; index < value; index++) {
        SpecializedCommandManager.specialisedCommand command = specialisedCommands[index];
        if (string.toLowerCase().startsWith(command.getFormatForCheck())) {
          switch (command.getType()) {
            case action:
            case subaction:
              return CMI.getInstance().getSpecializedCommandManager()
              .new specCommandAction(command);
            case condition:
            case statement:
              if (!string.contains(":")) {
                return null;
              }

              action =
                  CMI.getInstance().getSpecializedCommandManager().new specCommandAction(command);
              if (string.endsWith("!")) {
                string = string.substring(0, string.length() - 1);
              }

              String suffix = string.substring(string.length() - 3, string.length());
              String suffixCopy = suffix;
              if (suffixCopy.contains("?")) {
                suffixCopy = suffixCopy.replace("?", "");
                action.setNeedToInform(true);
              }

              if (suffixCopy.contains("#")) {
                suffixCopy = suffixCopy.replace("#", "");
                action.setNeedToBreak(true);
              }

              if (suffixCopy.contains("@")) {
                suffixCopy = suffixCopy.replace("@", "");
                action.setOpposite(true);
              }

              if (suffixCopy.contains("~")) {
                suffixCopy = suffixCopy.replace("~", "");
                action.setStopAfter(true);
              }

              string = string.substring(0, string.length() - 3) + suffixCopy;
              String prefix = string.substring(string.split(":")[0].length() + 1);
              action.setValue(prefix);
              return action;
            default:
              return action;
          }
        }
      }

      return action;
    }
  }

  public enum specialisedCommandType {
    action,
    subaction,
    condition,
    statement;
  }
}
