package com.Zrips.CMI.commands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CmdCost.CMICommandCost;
import com.Zrips.CMI.Modules.CmdCost.CMICommandCostUsage;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.dsign;
import com.Zrips.CMI.commands.list.home;
import com.Zrips.CMI.commands.list.killall;
import com.Zrips.CMI.commands.list.placeholders;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor {
  public static final String label = "cmi";
  private static String packagePath = "";
  public static int stage = 0;
  public static String msg = "";
  private Map<String, CMICommand> commands = new TreeMap<>();
  private List<String> disabledBase = new ArrayList<>();
  private boolean testServer = false;
  protected CMI plugin;

  public CommandsHandler(CMI cmi) {
    this.plugin = cmi;
    packagePath = this.getClass().getPackage().getName() + ".list";
    if (CMILib.getInstance().getReflectionManager().getServerName().equals("LT_Craft")
        && ((World) Bukkit.getWorlds().get(0)).getSeed() == 1782374759L) {
      this.testServer = true;
    }
  }

  public Boolean performCMICommand(CommandSender commandSender, Cmd cmd, String... strings) {
    return this.performCMICommand(commandSender, cmd.getClass(), strings);
  }

  public Boolean performCMICommand(
      CommandSender commandSender, Class<?> commandClass, String... strings) {
    if (strings != null) {
      ArrayList<String> arrayList = new ArrayList<>();
      String[] args = strings;
      int value = strings.length;

      for (int argIndex = 0; argIndex < value; argIndex++) {
        String string = args[argIndex];
        arrayList.addAll(Arrays.asList(string.split(" ")));
      }

      strings = arrayList.toArray(new String[0]);
    }

    return this.performCMICommand(
        commandSender, this.getCommands().get(commandClass.getSimpleName().toLowerCase()), strings);
  }

  public Boolean performCMICommand(
      CommandSender commandSender, CMICommand cmiCommand, String[] strings) {
    return commandSender != null && cmiCommand != null
        ? cmiCommand.getCmdClass().perform(this.plugin, commandSender, strings)
        : false;
  }

  public boolean onCommand(
      CommandSender commandSender, Command command, String string, String[] strings) {
    if ((strings.length <= 0 || !strings[0].equalsIgnoreCase("rmc"))
        && (!(commandSender instanceof RemoteConsoleCommandSender)
            || CMI.getInstance().getConfigManager().isAllowRconCommands())) {
      if (!(commandSender instanceof Player)
          && !(commandSender instanceof ConsoleCommandSender)
          && !(commandSender instanceof BlockCommandSender)
          && !(commandSender instanceof RemoteConsoleCommandSender)
          && !(commandSender instanceof ProxiedCommandSender)
          && !CMI.getInstance()
              .getConfigManager()
              .getCustomCommandSenders()
              .contains(commandSender.getClass().getSimpleName().toLowerCase())) {
        CMIMessages.consoleMessage(
            "Unknown CMI command source ("
                + commandSender.getName()
                + " - "
                + commandSender.getClass().getSimpleName()
                + ") "
                + CMIArray.toString(strings));
        return false;
      }

      CommandAlias commandAlias =
          CMI.getInstance().getAliasManager().getAliasForCommand(CMIArray.toString(strings));
      if (command != null
          && command.getName().equalsIgnoreCase(getLabel())
          && commandAlias != null
          && commandAlias.isDisableBase()) {
        LC.info_NoCommand.sendMessage(commandSender, new Object[0]);
        return false;
      }

      try {
        if (commandSender instanceof BlockCommandSender) {
          try {
            for (int value = 0; value < strings.length; value++) {
              String arg = strings[value];
              var list = Bukkit.selectEntities((CommandSender) commandSender, arg.toLowerCase());
              if (!list.isEmpty() && (arg.equalsIgnoreCase("@p") || arg.equalsIgnoreCase("@r"))) {
                strings[value] = ((Entity) list.get(0)).getName();
              }
            }
          } catch (Throwable throwable) {
          }

          if (strings.length > 0 && !strings[0].equalsIgnoreCase(killall.class.getSimpleName())
              || strings.length == 0) {
            commandSender = Bukkit.getConsoleSender();
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      if (!PermissionsManager.CMIPerm.command.hasPermission(
          (CommandSender) commandSender, false, 5000L)) {
        if (commandSender instanceof Player) {
          boolean flag =
              this.plugin.getConfigManager().isPermisionOnError()
                  || PermissionsManager.CMIPerm.permisiononerror.hasPermission(
                      (CommandSender) commandSender, false, 5000L);
          RawMessage rawMessage = new RawMessage();
          rawMessage
              .addText(LC.info_NoPermission.getLocale(new Object[0]))
              .addHover(flag ? "&2" + PermissionsManager.CMIPerm.command.getPermission() : null);
          rawMessage.show((CommandSender) commandSender);
        }

        ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
        Snd snd =
            new Snd().setSender(consoleCommandSender).setTarget((CommandSender) commandSender);
        CMILC.sendMessage(
            consoleCommandSender,
            LC.info_NoPlayerPermission.getLocale(
                new Object[] {
                  snd, "[permission]", PermissionsManager.CMIPerm.command.getPermission()
                }),
            false);
        return false;
      } else if (strings.length == 0) {
        if (!Config.ShowMainHelpPage) {
          LC.info_NoCommand.sendMessage(commandSender, new Object[0]);
          return false;
        } else {
          return this.help((CommandSender) commandSender, 1);
        }
      } else if (strings.length != 1 && strings.length != 2
          || !strings[0].equalsIgnoreCase("?") && !strings[0].equalsIgnoreCase("help")) {
        String commandName = strings[0].toLowerCase();
        Cmd cmd = this.getCmdClass(commandName);
        if (commandSender instanceof Player
            && !commandName.equalsIgnoreCase(placeholders.class.getSimpleName())
            && !commandName.equalsIgnoreCase(dsign.class.getSimpleName())) {
          for (int argIndex = 1; argIndex < strings.length; argIndex++) {
            if (CMI.getInstance()
                .getPlaceholderAPIManager()
                .containsPlaceHolder(strings[argIndex])) {
              strings[argIndex] =
                  CMI.getInstance()
                      .getPlaceholderAPIManager()
                      .coolDownPlaceHolders(strings[argIndex], (Player) commandSender);
            }
          }
        }

        if (cmd == null) {
          cmd = this.getBestCmdmatch(commandName);
          if (cmd != null) {
            String joinedArgs = CMIArray.toString(strings, 1);
            if (!joinedArgs.isEmpty()) {
              joinedArgs = " " + joinedArgs;
            }

            if (!this.plugin
                .getCooldownManager()
                .canUseCmd(
                    (CommandSender) commandSender,
                    getLabel() + " " + cmd.getClass().getSimpleName() + joinedArgs,
                    true,
                    true)) {
              return false;
            }

            if (this.plugin
                .getWarmUpManager()
                .startCmdUsage(
                    (CommandSender) commandSender,
                    getLabel() + " " + cmd.getClass().getSimpleName() + joinedArgs)) {
              return false;
            }

            CMICommand cmiCommand = this.commands.get(cmd.getClass().getSimpleName().toLowerCase());
            if (commandSender instanceof Player
                && cmiCommand != null
                && cmiCommand.getEnabled() != null
                && !cmiCommand.getEnabled()) {
              CMIMessages.sendMessage(
                  (CommandSender) commandSender,
                  "&7Command module disabled (" + cmd.getClass().getSimpleName() + ")");
              return false;
            }
          }
        }

        if (cmd != null && stage == 0) {
          commandName = cmd.getClass().getSimpleName();
          if (commandSender instanceof Player
              && commandName.equalsIgnoreCase(home.class.getSimpleName())
              && strings.length == 1) {
            CMIUser cmiUser = CMIUser.getUser((Player) commandSender);
            if (cmiUser.getHomes().size() == 1) {
              CmiHome cmiHome = cmiUser.getHomes().values().iterator().next();
              strings = CMIArray.addLast(strings, cmiHome.getName());
              String commandLabel = getLabel() + " " + home.class.getSimpleName() + " ";
              if (!this.plugin
                  .getCooldownManager()
                  .canUseCmd((CommandSender) commandSender, commandLabel)) {
                return false;
              }

              if (this.plugin
                  .getWarmUpManager()
                  .startCmdUsage((CommandSender) commandSender, commandLabel + cmiHome.getName())) {
                return false;
              }

              this.plugin
                  .getCooldownManager()
                  .recordCommandUsage((CommandSender) commandSender, commandLabel);
            }
          }

          if (!commandName.equalsIgnoreCase(strings[0])) {
            RawMessage helpMessage = new RawMessage();
            String helpText = "";

            for (int helpIndex = 1; helpIndex < strings.length; helpIndex++) {
              if (!helpText.isEmpty()) {
                helpText = helpText + " ";
              }

              helpText = helpText + strings[helpIndex];
            }

            helpMessage
                .addText(
                    LC.info_cantFindCommand.getLocale(
                        new Object[] {"[%1]", strings[0], "[%2]", commandName}))
                .addHover(LC.info_Click.getLocale(new Object[0]))
                .addCommand(getLabel() + " " + commandName + " " + helpText);
            helpMessage.show((CommandSender) commandSender);
            if (this.plugin.getConfigManager().isOptimizationsSimilarCommandPrevention()) {
              return false;
            }
          }

          if (!hasCommandPermission((CommandSender) commandSender, commandName, 50L)) {
            if (commandSender instanceof Player) {
              CMICommand commandInfo = this.getCommands().get(cmd.getClass().getSimpleName());
              if (commandInfo != null
                  && !commandInfo.getAnottation().redirectClass().equals(Void.class)) {
                var redirect = commandInfo.getAnottation().redirectClass();
                if (hasCommandPermission(
                    (CommandSender) commandSender, redirect.getSimpleName(), 50L)) {
                  this.plugin.performCommand(
                      (CommandSender) commandSender,
                      getLabel()
                          + " "
                          + redirect.getSimpleName()
                          + " "
                          + CMIArray.toString(CMIArray.removeFirst(strings)));
                  return true;
                }
              }

              boolean hasPermission =
                  this.plugin.getConfigManager().isPermisionOnError()
                      || PermissionsManager.CMIPerm.permisiononerror.hasPermission(
                          (CommandSender) commandSender, false, 5000L);
              RawMessage errorMessage = new RawMessage();
              errorMessage
                  .addText(LC.info_NoPermission.getLocale(new Object[0]))
                  .addHover(hasPermission ? "&2cmi.command." + commandName : null);
              errorMessage.show((CommandSender) commandSender);
              ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
              Snd sound =
                  new Snd().setSender(consoleSender).setTarget((CommandSender) commandSender);
              LC.info_NoPlayerPermission.sendMessage(
                  consoleSender,
                  new Object[] {sound, "[permission]", "cmi.command." + commandName});
            } else {
              LC.info_NoPermission.sendMessage(commandSender, new Object[0]);
            }

            if (commandSender instanceof Player) {
              this.plugin
                  .getCooldownManager()
                  .removeCooldown((CommandSender) commandSender, getLabel() + " " + commandName);
            }

            return false;
          } else {
            String[] remainingArgs = CMIArray.removeFirst(strings);
            if (remainingArgs.length > 0 && remainingArgs[remainingArgs.length - 1].equals("?")) {
              CMICommand redirectInfo = this.getCommands().get(cmd.getClass().getSimpleName());
              if (redirectInfo == null
                  || !redirectInfo.getAnottation().ignoreHelpPage()
                      && !redirectInfo.getAnottation().subIgnoreHelpPage()) {
                this.sendUsage((CommandSender) commandSender, commandName);
                return false;
              }

              if (remainingArgs.length == 1 && redirectInfo.getAnottation().subIgnoreHelpPage()) {
                this.sendUsage((CommandSender) commandSender, commandName);
                return false;
              }
            }

            Method[] methods;
            int methodCount = (methods = cmd.getClass().getMethods()).length;

            for (int methodIndex = 0; methodIndex < methodCount; methodIndex++) {
              Method method = methods[methodIndex];
              if (method.isAnnotationPresent(CAnnotation.class)) {
                CAnnotation cAnnotation = method.getAnnotation(CAnnotation.class);
                if (commandSender instanceof Player) {
                  int[] values = cAnnotation.regVar();
                  ArrayList<Integer> arrayList = new ArrayList();
                  boolean allowConsole = true;
                  int[] playerValues = values;
                  int valueCount = values.length;

                  for (int valueIndex = 0; valueIndex < valueCount; valueIndex++) {
                    int requiredValue = playerValues[valueIndex];
                    if (requiredValue < 0) {
                      allowConsole = false;
                    }

                    arrayList.add(requiredValue);
                  }

                  int remainingCount = remainingArgs.length;
                  boolean allowPlayer = true;
                  if (arrayList.contains(666)) {
                    LC.info_FromConsole.sendMessage(commandSender, new Object[0]);
                    return false;
                  }

                  if (arrayList.contains(-remainingCount)) {
                    allowPlayer = false;
                  }

                  if (arrayList.contains(remainingCount)) {
                    allowPlayer = true;
                  }

                  if (arrayList.contains(-100) && remainingCount == 0) {
                    allowPlayer = false;
                  }

                  if (allowConsole && !arrayList.isEmpty() && !arrayList.contains(remainingCount)) {
                    allowPlayer = false;
                  }

                  if (!allowPlayer) {
                    this.sendUsage(
                        (CommandSender) commandSender, cmd.getClass().getSimpleName(), false);
                    return false;
                  }
                } else {
                  int[] consoleValues = cAnnotation.consoleVar();
                  ArrayList<Integer> consoleValueList = new ArrayList();
                  boolean allowConsoleCheck = true;
                  int[] consoleValuesCopy = consoleValues;
                  int consoleValueCount = consoleValues.length;

                  for (int consoleValueIndex = 0;
                      consoleValueIndex < consoleValueCount;
                      consoleValueIndex++) {
                    int consoleValue = consoleValuesCopy[consoleValueIndex];
                    if (consoleValue < 0) {
                      allowConsoleCheck = false;
                    }

                    consoleValueList.add(consoleValue);
                  }

                  int argCount = remainingArgs.length;
                  boolean allowOther = true;
                  if (consoleValueList.contains(666)) {
                    LC.info_Ingame.sendMessage(commandSender, new Object[0]);
                    return false;
                  }

                  if (consoleValueList.contains(-argCount)) {
                    allowOther = false;
                  }

                  if (consoleValueList.contains(argCount)) {
                    allowOther = true;
                  }

                  if (consoleValueList.contains(-100) && argCount == 0) {
                    allowOther = false;
                  }

                  if (allowConsoleCheck && !consoleValueList.contains(argCount)) {
                    allowOther = false;
                  }

                  if (!allowOther) {
                    this.sendUsage(
                        (CommandSender) commandSender, cmd.getClass().getSimpleName(), false);
                    return false;
                  }
                }
              }
            }

            if (remainingArgs.length > 0) {
              remainingArgs[remainingArgs.length - 1] =
                  remainingArgs[remainingArgs.length - 1].replace("<>-<>", "");
            }

            String argsText = CMIArray.toString(remainingArgs, " ");
            argsText = argsText.isEmpty() ? "" : " " + argsText;
            if (!this.plugin
                .getCommandCostManager()
                .isContinueCommand(
                    (CommandSender) commandSender,
                    "cmi " + cmd.getClass().getSimpleName() + argsText)) {
              if (commandSender instanceof Player) {
                this.plugin
                    .getCooldownManager()
                    .removeCooldown((CommandSender) commandSender, getLabel() + " " + commandName);
                this.plugin
                    .getCooldownManager()
                    .removeCooldown(
                        (CommandSender) commandSender, getLabel() + " " + commandName + " ");
              }

              return false;
            } else {
              Boolean performed =
                  cmd.perform(this.plugin, (CommandSender) commandSender, remainingArgs);
              if (performed != null && !performed) {
                this.sendUsage((CommandSender) commandSender, commandName, false);
              }

              if ((performed == null || !performed) && commandSender instanceof Player) {
                if (remainingArgs.length > 0) {
                  this.plugin
                      .getCooldownManager()
                      .removeCooldown(
                          (CommandSender) commandSender,
                          getLabel()
                              + " "
                              + commandName
                              + " "
                              + CMIArray.toString(remainingArgs, " ").toLowerCase());
                } else {
                  this.plugin
                      .getCooldownManager()
                      .removeCooldown(
                          (CommandSender) commandSender, getLabel() + " " + commandName);
                }
              }

              if (performed != null
                  && performed
                  && commandSender instanceof Player
                  && !this.plugin
                      .getCommandCostManager()
                      .canUseCmd(
                          (CommandSender) commandSender,
                          "cmi " + cmd.getClass().getSimpleName() + argsText)
                      .equals(CMICommandCostUsage.CostBypass)) {
                CMICommandCost cmiCommandCost =
                    this.plugin
                        .getCommandCostManager()
                        .getCost("cmi " + cmd.getClass().getSimpleName() + argsText);
                CMIUser senderUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
                if (cmiCommandCost != null && cmiCommandCost.getCost() > 0.0) {
                  if (senderUser.hasMoney(cmiCommandCost.getCost())) {
                    senderUser.withdraw(cmiCommandCost.getCost());
                    cmiCommandCost.removeConfirmation(senderUser.getUniqueId());
                    if (cmiCommandCost.isInformOnCharge()) {
                      LC.econ_charged.sendMessage(
                          commandSender,
                          new Object[] {
                            "[amount]",
                            this.plugin.getEconomyManager().format(cmiCommandCost.getCost())
                          });
                    }
                  } else {
                    LC.econ_notEnoughMoney.sendMessage(
                        commandSender,
                        new Object[] {
                          "[amount]",
                          this.plugin.getEconomyManager().format(cmiCommandCost.getCost())
                        });
                  }
                }
              }

              return performed != null && performed;
            }
          }
        } else {
          if (stage != 0 && !msg.isEmpty()) {
            this.plugin.consoleMessage(msg);
          }

          LC.info_NoCommand.sendMessage(commandSender, new Object[0]);
          return false;
        }
      } else {
        if (!Config.ShowMainHelpPage) {
          LC.info_NoCommand.sendMessage(commandSender, new Object[0]);
          return false;
        }

        int pageNumber = 1;
        if (strings.length == 2) {
          try {
            pageNumber = Integer.parseInt(strings[1]);
          } catch (NumberFormatException numberFormatException) {
            LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
            return true;
          }
        }

        if (pageNumber < 1) {
          pageNumber = 1;
        }

        return this.help((CommandSender) commandSender, pageNumber);
      }
    } else {
      return false;
    }
  }

  public String getUserName1(CMIUser cmiUser) {
    return cmiUser != null && cmiUser.getName() != null ? cmiUser.getName() : "bob";
  }

  public String getUserName2(CMIUser cmiUser) {
    return Optional.ofNullable(cmiUser).map(CMIUser::getName).orElse("bob");
  }

  public static boolean hasCommandPermission(
      CommandSender commandSender, String string, Long cooldown) {
    return commandSender instanceof Player
            && !PermissionsManager.CMIPerm.command_$1.hasPermission(
                commandSender, false, true, cooldown, "*")
        ? PermissionsManager.CMIPerm.command_$1.hasPermission(
            commandSender, false, true, cooldown, string)
        : true;
  }

  private String getUsage(String string) {
    String help = "";
    if (this.plugin.getAliasManager().getAll().containsKey(string.toLowerCase())
        && this.plugin.getAliasManager().getAll().get(string.toLowerCase()).getState()) {
      help = string;
    } else if (this.plugin.getAliasManager().getFrom().containsKey("cmi " + string)) {
      CommandAlias commandAlias = this.plugin.getAliasManager().getFrom().get("cmi " + string);
      help = commandAlias.getAlias();
    } else if (this.plugin.getConfigManager().isOptimizationsCommandRemoveLabel()) {
      help = string;
    } else {
      help = "cmi " + string;
    }

    String args =
        this.plugin.getLM().getMessage("command.help.output.cmdFormat", "[command]", help);
    String argsKey = "command." + string + ".help.args";
    if (this.plugin.getLM().containsKey(argsKey)
        && !this.plugin.getLM().getMessage(argsKey).isEmpty()) {
      args = args.replace("[arguments]", " " + this.plugin.getLM().getMessage(argsKey));
    } else {
      args = args.replace("[arguments]", "");
    }

    return args;
  }

  private String getCommandRoot(String string) {
    if (this.plugin.getAliasManager().getAll().containsKey(string.toLowerCase())
        && this.plugin.getAliasManager().getAll().get(string.toLowerCase()).getState()) {
      return string;
    } else if (this.plugin.getAliasManager().getFrom().containsKey("cmi " + string)) {
      return this.plugin.getAliasManager().getFrom().get("cmi " + string).getAlias();
    } else {
      return this.plugin.getConfigManager().isOptimizationsCommandRemoveLabel()
          ? string
          : "cmi " + string;
    }
  }

  public void sendUsage(CommandSender commandSender, String string) {
    this.sendUsage(commandSender, string, true);
  }

  public void sendUsage(CommandSender commandSender, String string, boolean flag) {
    String text = CMIChatColor.YELLOW + this.plugin.getLM().getMessage("command.help.output.usage");
    text = text.replace("%usage%", this.getUsage(string));
    text = text.replace("\n", "\\n");
    CMILC.sendMessage(commandSender, text, false, true, true, false);
    CMILC.sendMessage(
        commandSender,
        this.plugin
            .getLM()
            .getMessage(
                "command.help.output.helpPageDescription",
                "[description]",
                this.plugin.getLM().getMessage("command." + string + ".help.info")),
        false);
    if (flag && this.plugin.getLM().containsKey("command." + string + ".help.explanation")) {
      if (this.plugin.getLM().isString("command." + string + ".help.explanation")) {
        CMILC.sendMessage(
            commandSender,
            this.plugin
                .getLM()
                .getMessage(
                    "command.help.output.explanation",
                    "[explanation]",
                    this.plugin.getLM().getMessage("command." + string + ".help.explanation")),
            false);
      } else {
        for (String alias :
            this.plugin.getLM().getMessageList("command." + string + ".help.explanation")) {
          if (alias.startsWith("<T>")) {
            CMILC.sendMessage(
                commandSender,
                "<T>"
                    + CMIChatColor.getLastColors(
                        this.plugin.getLM().getMessage("command.help.output.explanation"))
                    + alias.substring(3, alias.length()),
                false);
          } else {
            CMILC.sendMessage(
                commandSender,
                this.plugin
                    .getLM()
                    .getMessage("command.help.output.explanation", "[explanation]", alias),
                false);
          }
        }
      }
    }
  }

  protected boolean help(CommandSender commandSender, int value) {
    var map = this.GetCommands(commandSender);
    if (map.size() == 0) {
      LC.info_NoPermission.sendMessage(commandSender, new Object[0]);
      return true;
    }

    if (!this.plugin.getConfigManager().isOptimizationsCommandSorting()) {
      map = sort(map);
    }

    PageInfo pageInfo = new PageInfo(7, map.size(), value);
    CMILC.sendMessage(
        commandSender, this.plugin.getLM().getMessage("command.help.output.title"), false);
    RawMessage rawMessage = new RawMessage();

    for (Entry entry : map.entrySet()) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (!pageInfo.isContinue()) {
        String string = this.plugin.getLM().getMessage("command.help.output.cmdInfoFormat");
        string = string.replace("[command]", this.getUsage((String) entry.getKey()));
        string =
            string.replace(
                "[description]",
                this.plugin
                    .getLM()
                    .getMessage("command." + (String) entry.getKey() + ".help.info"));
        rawMessage.addText(string);
        rawMessage.addHover(
            this.plugin.getLM().getMessage("command." + (String) entry.getKey() + ".help.info"));
        rawMessage.addSuggestion("/" + this.getCommandRoot((String) entry.getKey()) + " ");
        if (!pageInfo.isBreak() && pageInfo.getPositionForOutput() <= pageInfo.getEnd()) {
          rawMessage.addText("\n");
        }
      }
    }

    rawMessage.show(commandSender);
    pageInfo.autoPagination(commandSender, "/cmi ?");
    return true;
  }

  public static HashSet<String> getClassesFromPackage(String string) {
    HashSet hashSet = new HashSet();

    try {
      URL[] urls;
      int value = (urls = ((URLClassLoader) CMI.class.getClassLoader()).getURLs()).length;

      for (int index = 0; index < value; index++) {
        URL url = urls[index];

        try {
          hashSet.addAll(getClassesInSamePackageFromJar(string, url.toURI().getPath()));
        } catch (URISyntaxException uriSyntaxException) {
        }
      }
    } catch (NullPointerException nullPointerException) {
      CMI.getInstance()
          .consoleMessage(
              string + " does not appear to be a valid package (Null pointer exception)");
    }

    return hashSet;
  }

  private static HashSet<String> getClassesInSamePackageFromJar(String string, String packageName) {
    JarFile jarFile = null;
    HashSet<String> hashSet = new HashSet();

    try {
      string = string.replace(".", "/");
      jarFile = new JarFile(packageName);
      var enumeration = jarFile.entries();

      while (enumeration.hasMoreElements()) {
        JarEntry jarEntry = (JarEntry) enumeration.nextElement();
        String entryName = jarEntry.getName();
        if (entryName != null && entryName.endsWith(".class") && entryName.startsWith(string)) {
          String className = entryName.replace(string, "").replace(".class", "").replace("/", "");
          if (className.contains("$")) {
            className = className.split("\\$")[0];
          }

          hashSet.add(className);
        }
      }
    } catch (Exception exception) {
    } finally {
      if (jarFile != null) {
        try {
          jarFile.close();
        } catch (Exception exception2) {
        }
      }
    }

    return hashSet;
  }

  public Map<String, Integer> GetCommands(CommandSender commandSender) {
    TreeMap<String, Integer> treeMap = new TreeMap();

    label58:
    for (var entry : this.commands.entrySet()) {
      if (!((CMICommand) entry.getValue()).getAnottation().hidden()
          && !((CMICommand) entry.getValue()).getAnottation().test()
          && (((CMICommand) entry.getValue()).getEnabled() == null
              || ((CMICommand) entry.getValue()).getEnabled())) {
        if (((CMICommand) entry.getValue()).getEnabled() == null) {
          if (((CMICommand) entry.getValue()).getAnottation().modules().length > 0) {
            String[] strings;
            int value =
                (strings = ((CMICommand) entry.getValue()).getAnottation().modules()).length;

            for (int index = 0; index < value; index++) {
              String string = strings[index];
              if (!CMIModule.isEnabled(string)) {
                ((CMICommand) entry.getValue()).setEnabled(false);
                continue label58;
              }
            }
          }

          ((CMICommand) entry.getValue()).setEnabled(true);
        }

        if (commandSender == null
            || hasCommandPermission(commandSender, (String) entry.getKey(), 5000L)) {
          treeMap.put(
              (String) entry.getKey(), ((CMICommand) entry.getValue()).getAnottation().priority());
        }
      }
    }

    return treeMap;
  }

  public void fillCommands() {
    HashMap hashMap = new HashMap();

    for (String string : getClassesFromPackage(packagePath)) {
      var resolvedClass = getClass(string);
      if (resolvedClass != null) {
        hashMap.put(string, resolvedClass);
      }
    }

    for (Entry entry : ((java.util.Map<?, ?>) hashMap).entrySet()) {
      Method[] methods;
      int value = (methods = ((Class) entry.getValue()).getMethods()).length;

      for (int index = 0; index < value; index++) {
        Method method = methods[index];
        if (method.isAnnotationPresent(CAnnotation.class)) {
          String key = (String) entry.getKey();

          try {
            if (!Cmd.class.isAssignableFrom((Class<?>) entry.getValue())) {
              break;
            }

            Cmd cmd = (Cmd) ((Class) entry.getValue()).getConstructor().newInstance();
            CMICommand cmiCommand =
                new CMICommand(cmd, key, method.getAnnotation(CAnnotation.class));
            if (!cmiCommand.getAnottation().test() || this.testServer) {
              this.commands.put(key.toLowerCase(), cmiCommand);
              break;
            }
          } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage("!!! " + key);
            throwable.printStackTrace();
            break;
          }
        }
      }
    }
  }

  private static Class<?> getClass(String string) {
    if (string.toLowerCase().contains("notfinished")) {
      return null;
    }

    try {
      return Class.forName(packagePath + "." + string.toLowerCase());
    } catch (Throwable throwable) {
      CMI.getInstance().consoleMessage("&cFailed to load " + string.toLowerCase() + " command!");
      throwable.printStackTrace();
      return null;
    }
  }

  private Cmd getCmdClass(String string) {
    CMICommand cmiCommand = this.commands.get(string.toLowerCase());
    if (cmiCommand != null && (!cmiCommand.getAnottation().test() || this.testServer)) {
      if (cmiCommand.getEnabled() != null && !cmiCommand.getEnabled()) {
        this.plugin.consoleMessage(
            "&5Someone tried to use command for disabled module (" + cmiCommand.getName() + ")");
        return null;
      }

      if (cmiCommand.getEnabled() == null) {
        if (cmiCommand.getAnottation().modules().length > 0) {
          String[] strings;
          int value = (strings = cmiCommand.getAnottation().modules()).length;

          for (int index = 0; index < value; index++) {
            String arg = strings[index];
            if (!CMIModule.isEnabled(arg)) {
              cmiCommand.setEnabled(false);
              this.plugin.consoleMessage(
                  "&5Someone tried to use command for disabled module ("
                      + cmiCommand.getName()
                      + ")");
              return null;
            }
          }
        }

        cmiCommand.setEnabled(true);
      }

      return cmiCommand.getCmdClass();
    } else {
      return null;
    }
  }

  private Cmd getBestCmdmatch(String string) {
    if (this.plugin.getConfigManager().getOptimizationsSimilarCommandChecker() > 0) {
      Cmd cmd = null;
      int value = string.length() * 2;

      for (var entry : this.commands.entrySet()) {
        int editDistance = distance((String) entry.getKey(), string);
        if (editDistance <= value) {
          value = editDistance;
          cmd = ((CMICommand) entry.getValue()).getCmdClass();
          if (value == 1) {
            break;
          }
        }
      }

      if (cmd != null) {
        double number = 100.0 - value * 100.0 / cmd.getClass().getSimpleName().length();
        if (this.plugin.getConfigManager().getOptimizationsSimilarCommandChecker() <= number) {
          return cmd;
        }
      }
    }

    return null;
  }

  public static int distance(String string, String other) {
    string = string.toLowerCase();
    other = other.toLowerCase();
    int[] values = new int[other.length() + 1];
    int value = 0;

    while (value < values.length) {
      values[value] = value++;
    }

    for (int rowIndex = 1; rowIndex <= string.length(); rowIndex++) {
      values[0] = rowIndex;
      int previousIndex = rowIndex - 1;

      for (int columnIndex = 1; columnIndex <= other.length(); columnIndex++) {
        int substitutionCost =
            Math.min(
                1 + Math.min(values[columnIndex], values[columnIndex - 1]),
                string.charAt(rowIndex - 1) == other.charAt(columnIndex - 1)
                    ? previousIndex
                    : previousIndex + 1);
        previousIndex = values[columnIndex];
        values[columnIndex] = substitutionCost;
      }
    }

    return values[other.length()];
  }

  private static Map<String, Integer> sort(Map<String, Integer> map) {
    LinkedList linkedList = new LinkedList(map.entrySet());
    Collections.sort(
        linkedList,
        new Comparator<Entry<String, Integer>>() {
          public int compare(Entry<String, Integer> entry, Entry<String, Integer> other) {
            return ((Integer) entry.getValue()).compareTo((Integer) other.getValue());
          }
        });
    LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap();

    for (Entry candidate : ((Iterable<Entry>) linkedList)) {
      linkedHashMap.put((String) candidate.getKey(), (Integer) candidate.getValue());
    }

    return linkedHashMap;
  }

  public Map<String, CMICommand> getCommands() {
    return this.commands;
  }

  public static String getLabel() {
    return "cmi";
  }

  public boolean isTestServer() {
    return this.testServer;
  }

  public static String getCommandPrefix(String string) {
    if (string.startsWith("/")) {
      string = string.substring(1);
    }

    if (string.startsWith(getLabel() + " ")) {
      string = string.substring(getLabel().length() + 1);
    }

    CommandAlias commandAlias = CMI.getInstance().getAliasManager().getAliasForCommand(string);
    return commandAlias != null
            && commandAlias.getState()
            && commandAlias.getType() == CommandAliasType.base
        ? commandAlias.getAlias()
        : getLabel() + " " + string;
  }

  public void setBaseCommandState(String string, boolean flag) {
    CMICommand cmiCommand = this.getCommands().get(string.toLowerCase());
    if (cmiCommand != null) {
      cmiCommand.setBaseEnabled(flag);
    }

    if (!flag) {
      this.addDisabledBase(string.toLowerCase());
    }
  }

  public List<String> getDisabledBase() {
    return this.disabledBase;
  }

  public void clearDisabledBase() {
    this.disabledBase.clear();
  }

  public void addDisabledBase(String string) {
    this.disabledBase.add(string);
  }
}
