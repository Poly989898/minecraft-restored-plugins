package com.Zrips.CMI.Modules.Alias;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.CommandsHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class AliasManager {
  private CMI plugin;
  public LinkedHashMap<String, CommandAlias> defaultAliases = new LinkedHashMap<>();
  public LinkedHashMap<String, CommandAlias> customAliases = new LinkedHashMap<>();
  private Map<String, CommandAlias> allCached = new LinkedHashMap<>();
  public HashMap<String, CommandAlias> fromAliases = new HashMap<>();
  private volatile boolean dirtyCache = true;
  public static String nonExactAliasSuffix = " _";
  private static final int MAX_CACHE_SIZE = 300;
  private static Map<String, CommandAlias> cache =
      Collections.synchronizedMap(
          new LinkedHashMap<String, CommandAlias>(300, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Entry<String, CommandAlias> entry) {
              return this.size() > 300;
            }
          });
  private static Map<String, List<CommandAlias>> cacheListed =
      Collections.synchronizedMap(
          new LinkedHashMap<String, List<CommandAlias>>(300, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Entry<String, List<CommandAlias>> entry) {
              return this.size() > 300;
            }
          });
  public static Pattern patern = Pattern.compile("\\$((\\d{1,2})(-?))(?!\\d)");
  List<String> CommentList =
      new ArrayList<>(
          Arrays.asList(
              "",
              "Edit custom variables in game if possible",
              "Some extra variables can be used, like [playerName] will insert senders name"
                  + " automatically",
              "All global variables should work, you can find full list in locale file",
              "You can add delay between commands by inserting line like",
              "delay! 5",
              "This will delay next commands by defines time in seconds",
              "If command is shortened and you need to provide some changing variables like target"
                  + " player name you need to define what and where it should be placed",
              "Example: /cmi heal Zrips ",
              "Can be shortened with custom alias to something like /h zrips by using alias command"
                  + " like",
              "cmi heal $1",
              "If you need to add more than one variable, use $1- format. Number represents"
                  + " variable place when used in alias command",
              "Example: cmi tppos $1-",
              "In case you want to print out help page if player enters wrong subcommand, use ? in"
                  + " alias",
              "Example: ca ?",
              "In this case if player performs /ca wrongsub or /ca anotherwrong",
              "Commands under this alias will get executed. Exelent way to show help page and"
                  + " correct usage if needed"));
  List<String> temp = new ArrayList<>();
  private String fileName = "Alias.yml";

  public AliasManager(CMI cmi) {
    this.plugin = cmi;
  }

  private static CommandAlias getFirst(String string, CommandAlias commandAlias, String aliasName) {
    if (commandAlias.isExact()) {
      return string.equalsIgnoreCase(aliasName) ? commandAlias : null;
    } else if (string.equalsIgnoreCase(aliasName)
        || string.startsWith(aliasName + " ") && commandAlias.containsDynamicVariables()
        || string.equalsIgnoreCase(aliasName)) {
      return commandAlias;
    } else if (commandAlias.getAlias().equalsIgnoreCase(string)
        && !commandAlias.containsDynamicVariables()) {
      return commandAlias;
    } else if (aliasName.startsWith(string + " ") && commandAlias.containsDynamicVariables()) {
      return commandAlias;
    } else {
      return string.contains(" ") && aliasName.contains(" ") && string.startsWith(aliasName + " ")
          ? commandAlias
          : null;
    }
  }

  private static CommandAlias getSecond(
      String string,
      CommandAlias commandAlias,
      CommandAlias secondMatch,
      CommandAlias candidate,
      String aliasName) {
    if (candidate.isExact()) {
      return string.equalsIgnoreCase(aliasName) ? candidate : null;
    } else {
      return !string.equalsIgnoreCase(aliasName)
              && !candidate.getAlias().equalsIgnoreCase(string)
              && (!string.startsWith(aliasName + " ") || candidate.containsDynamicVariables())
          ? null
          : candidate;
    }
  }

  public void clearCache() {
    cache.clear();
    cacheListed.clear();
  }

  public CommandAlias getAliasForCommand(String string) {
    string = string.toLowerCase();
    if (cache.containsKey(string)) {
      return cache.get(string);
    }

    CommandAlias commandAlias = null;
    CommandAlias secondMatch = null;
    var map = this.getAll();

    for (Entry entry : map.entrySet()) {
      if (((CommandAlias) entry.getValue()).getState()) {
        String aliasKey = ((String) entry.getKey()).toLowerCase();
        if (aliasKey.endsWith(nonExactAliasSuffix)) {
          aliasKey = aliasKey.substring(0, aliasKey.length() - 2);
        }

        if (commandAlias == null) {
          commandAlias = getFirst(string, (CommandAlias) entry.getValue(), aliasKey);
        }

        if (secondMatch == null) {
          secondMatch =
              getSecond(
                  string, secondMatch, commandAlias, (CommandAlias) entry.getValue(), aliasKey);
        }

        if (commandAlias != null && secondMatch != null) {
          break;
        }
      }
    }

    if (commandAlias != null && secondMatch != null && !commandAlias.equals(secondMatch)) {
      if (secondMatch.getAlias().equalsIgnoreCase(string)) {
        cache.put(string, commandAlias);
        return secondMatch;
      } else if (commandAlias.getAlias().length() > secondMatch.getAlias().length()) {
        cache.put(string, commandAlias);
        return commandAlias;
      } else {
        cache.put(string, secondMatch);
        return secondMatch;
      }
    } else {
      if (commandAlias == null) {
        CommandAlias questionMatch = null;

        for (Entry questionEntry : map.entrySet()) {
          if (((CommandAlias) questionEntry.getValue()).getState()) {
            String questionKey = ((String) questionEntry.getKey()).toLowerCase();
            if (questionKey.endsWith("?")) {
              if (questionKey.length() > 1) {
                questionKey = questionKey.substring(0, questionKey.length() - 2);
              }

              if (string.equalsIgnoreCase(questionKey) || string.startsWith(questionKey + " ")) {
                questionMatch = (CommandAlias) questionEntry.getValue();
                break;
              }
            }
          }
        }

        if (questionMatch != null) {
          cache.put(string, questionMatch);
          return questionMatch;
        }
      }

      if (commandAlias == null && secondMatch != null) {
        cache.put(string, secondMatch);
        return secondMatch;
      } else {
        cache.put(string, commandAlias);
        return commandAlias;
      }
    }
  }

  public List<CommandAlias> getAliasStartingWith(String string) {
    return this.getAliasStartingWith(string, null);
  }

  public List<CommandAlias> getAliasStartingWith(String string, CommandAliasType commandAliasType) {
    string = string.toLowerCase();
    if (cacheListed.containsKey(string)) {
      return cacheListed.get(string);
    }

    ArrayList<CommandAlias> arrayList = new ArrayList();

    for (var entry : this.getAll().entrySet()) {
      if (((CommandAlias) entry.getValue()).getState()
          && (commandAliasType == null
              || commandAliasType.equals(((CommandAlias) entry.getValue()).getType()))) {
        String aliasKey = ((String) entry.getKey()).toLowerCase();
        if (aliasKey.startsWith(string)) {
          arrayList.add((CommandAlias) entry.getValue());
        }
      }
    }

    for (var aliasEntry : this.getAll().entrySet()) {
      if (((CommandAlias) aliasEntry.getValue()).getState()
          && (commandAliasType == null
              || commandAliasType.equals(((CommandAlias) aliasEntry.getValue()).getType()))
          && ((CommandAlias) aliasEntry.getValue()).getAlias().equalsIgnoreCase(string)) {
        arrayList.add((CommandAlias) aliasEntry.getValue());
      }
    }

    cacheListed.put(string, arrayList);
    return arrayList;
  }

  private void register(CommandAlias commandAlias) {
    if (commandAlias.getState()) {
      try {
        if (commandAlias.getAlias().contains(" ")
            && commandAlias.getAlias().split(" ")[1].length() > 1
            && !commandAlias.getAlias().split(" ")[1].startsWith("[")) {
          return;
        }
      } catch (Throwable throwable) {
        CMIMessages.consoleMessage(
            "Issue with custom alias formatting for: " + commandAlias.getAlias());
        throwable.printStackTrace();
      }

      Allias allias = new Allias(commandAlias.getAliasBaseCommand(), commandAlias);
      if (commandAlias.getType() != CommandAliasType.base && !commandAlias.isRequiresPerm()) {
        allias.register();
      } else if (commandAlias.isRequiresPerm()
          && commandAlias.getType() == CommandAliasType.custom) {
        allias.register(
            PermissionsManager.CMIPerm.customalias_$1.getPermission(
                commandAlias.getAliasAsOneWordNS()));
      } else {
        allias.register(
            PermissionsManager.CMIPerm.command_$1.getPermission(commandAlias.getCmiCommandName()));
      }

      PluginCommand pluginCommand =
          Bukkit.getPluginCommand(commandAlias.getAliasBaseCommand().toLowerCase());
      if (pluginCommand != null) {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (commandAlias.getCmiCommandName() != null) {
                if (this.plugin.getTab() != null && this.plugin.isEnabled()) {
                  pluginCommand.setTabCompleter(this.plugin.getTab());
                }
              }
            },
            200L);
      }
    }
  }

  public void addDefault(CommandAlias commandAlias) {
    this.defaultAliases.put(commandAlias.getAlias(), commandAlias);
    this.dirtyCache = true;
    this.fromAliases.put(commandAlias.getCleanCommand(), commandAlias);
    this.register(commandAlias);
  }

  public void addCustom(CommandAlias commandAlias) {
    for (String string : commandAlias.getCommands()) {
      if (string.equalsIgnoreCase(commandAlias.getAlias())) {
        CMI.getInstance()
            .consoleMessage(
                "&cYou cant have custom alias ("
                    + commandAlias.getAlias()
                    + ") performing ("
                    + string
                    + ") command! This will create infinite loop! Alias will be ignored to avoid"
                    + " major issues");
        return;
      }
    }

    this.customAliases.put(commandAlias.getAlias(), commandAlias);
    this.dirtyCache = true;
    this.fromAliases.put(commandAlias.getCleanCommand(), commandAlias);
    this.clearCache();
    this.register(commandAlias);
  }

  public void removeCustom(String string) {
    CommandAlias commandAlias = this.customAliases.remove(string);
    this.dirtyCache = true;
    if (commandAlias != null) {
      this.fromAliases.remove(commandAlias.getCleanCommand());
    }

    this.clearCache();
  }

  public void removeCustom(CommandAlias commandAlias) {
    this.customAliases.remove(commandAlias.getAlias());
    this.dirtyCache = true;
    this.fromAliases.remove(commandAlias.getCleanCommand());
    this.clearCache();
  }

  public LinkedHashMap<String, CommandAlias> getCustom() {
    return this.customAliases;
  }

  public LinkedHashMap<String, CommandAlias> getDefaultAlias() {
    return this.defaultAliases;
  }

  public Map<String, CommandAlias> getAll() {
    if (this.dirtyCache) {
      synchronized (this) {
        if (this.dirtyCache) {
          var linkedHashMap = new LinkedHashMap<>(this.defaultAliases);
          linkedHashMap.putAll(this.customAliases);
          this.allCached = Collections.unmodifiableMap(linkedHashMap);
          this.dirtyCache = false;
        }
      }
    }

    return this.allCached;
  }

  public HashMap<String, CommandAlias> getFrom() {
    return this.fromAliases;
  }

  private List<String> convertCustom() {
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.customAliases.entrySet()) {
      arrayList.add(((CommandAlias) entry.getValue()).getCommand() + "-" + (String) entry.getKey());
    }

    return arrayList;
  }

  public void save() {
    this.plugin.getConfigManager().ChangeConfig("CustomAlias.List", this.convertCustom());
  }

  public List<String> updateCommands(
      CommandAlias commandAlias, List<String> list, List<String> inputArgs) {
    ArrayList<String> arrayList = new ArrayList();
    if (commandAlias != null && !list.isEmpty() && ((String) list.get(0)).isEmpty()) {
      arrayList.add((String) list.remove(0));
      String string =
          commandAlias.getAlias().endsWith(nonExactAliasSuffix)
              ? commandAlias.getAlias().substring(0, commandAlias.getAlias().length() - 2)
              : commandAlias.getAlias();
      arrayList.add(string);
      arrayList.addAll(list);
      list = arrayList;
    } else {
      if (commandAlias != null) {
        String aliasBase =
            commandAlias.getAlias().endsWith(nonExactAliasSuffix)
                ? commandAlias.getAlias().substring(0, commandAlias.getAlias().length() - 2)
                : commandAlias.getAlias();
        arrayList.add(aliasBase);
      } else {
        arrayList.add("");
      }

      arrayList.addAll(list);
      list = arrayList;
    }

    for (int value = 0; value < inputArgs.size(); value++) {
      String inputArg = (String) inputArgs.get(value);
      ArrayList<String> inputParts = new ArrayList();
      if (inputArg.contains(" ")) {
        inputParts.addAll(Arrays.asList(inputArg.split(" ")));
      } else {
        inputParts.add(inputArg);
      }

      label108:
      for (int partIndex = 0; partIndex < inputParts.size(); partIndex++) {
        String part = (String) inputParts.get(partIndex);
        part = CMIChatColor.stripColor(part);
        Matcher matcher = patern.matcher(part);

        while (true) {
          String placeholderToken;
          boolean flag;
          Integer integer;
          while (true) {
            if (!matcher.find()) {
              continue label108;
            }

            placeholderToken = matcher.group();
            Object object = null;
            flag = matcher.group(3) != null && !matcher.group(3).isEmpty();

            try {
              integer = Integer.parseInt(matcher.group(2));
              break;
            } catch (Exception exception) {
            }
          }

          integer = integer + 1;
          String replacement = null;
          if (integer <= 10 && integer < list.size()) {
            if (!flag) {
              replacement = (String) list.get(integer);
            } else {
              replacement = "";

              for (int argIndex = integer; argIndex < list.size(); argIndex++) {
                if (!replacement.isEmpty()) {
                  replacement = replacement + " ";
                }

                replacement = replacement + (String) list.get(argIndex);
              }
            }
          }

          if (replacement != null
              && this.plugin.getPlaceholderAPIManager().containsPlaceHolder(replacement)) {
            replacement = replacement.replace("%", "%.");
          }

          inputArg =
              inputArg.replaceFirst(
                  Pattern.quote(placeholderToken),
                  replacement != null ? Matcher.quoteReplacement(replacement) : "");
          if (inputArg.endsWith(" ")) {
            inputArg = inputArg.substring(0, inputArg.length() - 1);
          }

          inputArgs.set(value, inputArg);
        }
      }
    }

    this.clearCache();
    return inputArgs;
  }

  public void fullLoad() {
    this.load();
    this.loadRegularAlias();
    this.clearCache();
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    File file = new File(this.plugin.getDataFolder(), "CustomAlias");
    if (!file.isDirectory()) {
      file.mkdir();
    }

    if (file.listFiles().length == 0) {
      File aliasFile =
          new File(this.plugin.getDataFolder(), "CustomAlias" + File.separator + "CustomAlias.yml");
      ConfigReader configReader = null;

      try {
        configReader = new ConfigReader(aliasFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      if (configReader != null) {
        configReader.getC().options().copyDefaults(true);
        configReader.header(this.CommentList);
        configReader.save();
      }
    } else {
      var linkedHashMap = new LinkedHashMap<>(this.customAliases);
      this.customAliases.clear();
      int value = this.loadFolder(file, linkedHashMap);
      this.plugin.loadMessage(value, "custom alias", System.currentTimeMillis() - startTime);
      this.dirtyCache = true;
    }
  }

  private int loadFolder(File file, LinkedHashMap<String, CommandAlias> linkedHashMap) {
    int value = 0;
    File[] files;
    int fileCount = (files = file.listFiles()).length;

    for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
      File aliasFile = files[fileIndex];
      if (!aliasFile.getName().endsWith(".yml")) {
        if (aliasFile.isDirectory()) {
          value += this.loadFolder(aliasFile, linkedHashMap);
        }
      } else {
        ConfigReader configReader = null;

        try {
          configReader = new ConfigReader(aliasFile);
        } catch (Exception exception) {
          CMIMessages.consoleMessage(
              "Can't read " + aliasFile.getName() + " file in CustomAlias directory");
          continue;
        }

        String string = aliasFile.getName();
        if (string.endsWith(".yml")) {
          string =
              aliasFile
                  .getPath()
                  .substring(
                      this.plugin.getDataFolder().toString().length()
                          + 1
                          + "CustomAlias".length()
                          + 1);
          string = string.substring(0, string.length() - ".yml".length());
        }

        configReader.getC().options().copyDefaults(true);
        configReader.header(this.CommentList);
        if (configReader.getC().isConfigurationSection("CustomAlias")) {
          for (String sectionKey :
              configReader.getC().getConfigurationSection("CustomAlias").getKeys(false)) {
            new ArrayList();
            List list = new ArrayList();
            boolean flag = false;
            boolean addTabs = false;
            boolean tabComplete = true;
            boolean exactMatch = false;
            List aliasCommands;
            if (configReader.getC().isList("CustomAlias." + sectionKey + ".Cmds")) {
              aliasCommands =
                  configReader.get("CustomAlias." + sectionKey + ".Cmds", Arrays.asList(""));
              if (configReader.getC().isBoolean("CustomAlias." + sectionKey + ".Perm")) {
                flag = configReader.get("CustomAlias." + sectionKey + ".Perm", false);
              }

              if (configReader.getC().isBoolean("CustomAlias." + sectionKey + ".AddTabs")) {
                addTabs = configReader.get("CustomAlias." + sectionKey + ".AddTabs", false);
              }

              if (configReader.getC().isBoolean("CustomAlias." + sectionKey + ".Tab")) {
                tabComplete = configReader.get("CustomAlias." + sectionKey + ".Tab", true);
              }

              if (configReader.getC().isBoolean("CustomAlias." + sectionKey + ".ExactMatch")) {
                exactMatch = configReader.get("CustomAlias." + sectionKey + ".ExactMatch", false);
              }

              if (configReader.getC().isList("CustomAlias." + sectionKey + ".CustomTabs")) {
                list =
                    configReader.get("CustomAlias." + sectionKey + ".CustomTabs", new ArrayList());
              }
            } else {
              aliasCommands = configReader.get("CustomAlias." + sectionKey, Arrays.asList(""));
            }

            if (!this.temp.contains(sectionKey)) {
              this.temp.add(sectionKey);
            }

            CommandAlias commandAlias = new CommandAlias(sectionKey, string, aliasCommands, true);
            commandAlias.setRequiresPerm(flag);
            commandAlias.setTabComplete(tabComplete);
            commandAlias.setAddTabs(addTabs);
            commandAlias.setExact(exactMatch);
            if (!list.isEmpty()) {
              commandAlias.setCustomTab(list);
            }

            CommandAlias existingAlias = (CommandAlias) linkedHashMap.get(commandAlias.getAlias());
            if (existingAlias != null) {
              existingAlias.setFileName(string);
              existingAlias.setState(true);
              existingAlias.setCommands(aliasCommands);
              existingAlias.setRequiresPerm(flag);
              existingAlias.setTabComplete(tabComplete);
              existingAlias.setAddTabs(addTabs);
              existingAlias.setExact(exactMatch);
              if (!list.isEmpty()) {
                existingAlias.setCustomTab(list);
              }

              this.addCustom(existingAlias);
            } else {
              this.addCustom(commandAlias);
            }

            value++;
          }
        }
      }
    }

    return value;
  }

  public void loadRegularAlias() {
    this.temp.clear();
    Long startTime = System.currentTimeMillis();
    File file = new File(this.plugin.getDataFolder(), "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    var linkedHashMap = new LinkedHashMap<>(this.defaultAliases);
    this.defaultAliases.clear();
    if (configReader != null) {
      configReader.getC().options().copyDefaults(true);
      configReader.header(
          Arrays.asList(
              "",
              "All aliases to used with this plugin. In example original command for going back to"
                  + " spawn is /cmi spawn",
              "By enabling appropriate alias you can use /spawn to get back to set spawn point",
              "if it interferes with another plugin, just disable it here and reload plugin with"
                  + " /cmi reload",
              "Comment shows command to be performed by using alias defined one line belove it",
              "True/false defines if alias is enabled or disabled",
              "",
              "ATENTION! If you want to have custom alias and not only presets, use ingame command"
                  + " /cmi aliaseditor for this",
              "This file will not accept new custom alias, for that we do have separate file"));
      int value = 0;
      CMI.getInstance().getCommandManager().clearDisabledBase();

      for (var entry : this.plugin.getCommandManager().getCommands().entrySet()) {
        CMICommand cmiCommand = (CMICommand) entry.getValue();
        if (!cmiCommand.getAnottation().hidden()) {
          String string = cmiCommand.getName();
          CAnnotation cAnnotation = cmiCommand.getAnottation();
          configReader.addComment(
              "Alias." + string,
              new String[] {"/" + CommandsHandler.getLabel() + " " + string + " $1-"});
          if (!this.temp.contains(string)) {
            this.temp.add(string);
          }

          Boolean aliasEnabled =
              configReader.get("Alias." + string + ".Enabled", cAnnotation.alias());
          Boolean tabEnabled = configReader.get("Alias." + string + ".Tab", true);
          String[] strings;
          int moduleCount = (strings = cmiCommand.getAnottation().modules()).length;

          for (int moduleIndex = 0; moduleIndex < moduleCount; moduleIndex++) {
            String moduleName = strings[moduleIndex];
            if (!CMIModule.isEnabled(moduleName)) {
              aliasEnabled = false;
              break;
            }
          }

          Boolean baseDisabled = false;
          if (configReader.getC().isBoolean("Alias." + string + ".BaseDisabled")) {
            baseDisabled = configReader.get("Alias." + string + ".BaseDisabled", false);
          }

          if (aliasEnabled) {
            CommandAlias commandAlias =
                new CommandAlias(
                    string,
                    Arrays.asList(CommandsHandler.getLabel() + " " + string + " $1-"),
                    aliasEnabled,
                    CommandAliasType.base,
                    (String) entry.getKey());
            commandAlias.setDisableBase(baseDisabled);
            this.plugin.getCommandManager().setBaseCommandState(string, !baseDisabled);
            commandAlias.setTabComplete(tabEnabled);
            this.addDefault(commandAlias);
          } else {
            CommandAlias existingBase = (CommandAlias) linkedHashMap.get(string);
            if (existingBase != null
                && existingBase.getType() == CommandAliasType.base
                && existingBase.getCmiCommandName().equals(entry.getKey())) {
              existingBase.setCommands(
                  Arrays.asList(CommandsHandler.getLabel() + " " + string + " $1-"));
              existingBase.setState(aliasEnabled);
              existingBase.setTabComplete(tabEnabled);
              this.plugin.getCommandManager().setBaseCommandState(string, !baseDisabled);
              existingBase.setDisableBase(baseDisabled);
              this.addDefault(existingBase);
            }
          }

          String[] customAliases = cAnnotation.customAlias();
          String[] customAliasesCopy = customAliases;
          int customAliasCount = customAliases.length;

          for (int customAliasIndex = 0; customAliasIndex < customAliasCount; customAliasIndex++) {
            String customAliasEntry = customAliasesCopy[customAliasIndex];
            String aliasBase = string;
            boolean flag = customAliasEntry.startsWith("!");
            String aliasName =
                customAliasEntry.startsWith("!") ? customAliasEntry.substring(1) : customAliasEntry;
            if (aliasName.startsWith("+")) {
              aliasName = aliasName.substring(1);
              aliasBase = string + " " + aliasName.split(":")[0];
              aliasName = aliasName.split(":")[1];
            }

            aliasEnabled = configReader.get("Alias." + aliasName + ".Enabled", flag);
            tabEnabled = configReader.get("Alias." + aliasName + ".Tab", true);
            baseDisabled = false;
            if (configReader.getC().isBoolean("Alias." + aliasName + ".BaseDisabled")) {
              baseDisabled = configReader.get("Alias." + aliasName + ".BaseDisabled", false);
            }

            value++;
            if (!this.temp.contains(aliasName)) {
              this.temp.add(aliasName);
            }

            String[] moduleList;
            int secondModuleCount = (moduleList = cmiCommand.getAnottation().modules()).length;

            for (int secondModuleIndex = 0;
                secondModuleIndex < secondModuleCount;
                secondModuleIndex++) {
              String moduleName = moduleList[secondModuleIndex];
              if (!CMIModule.isEnabled(moduleName)) {
                aliasEnabled = false;
                break;
              }
            }

            if (aliasEnabled) {
              CommandAlias newSubAlias =
                  new CommandAlias(
                      aliasName,
                      Arrays.asList(CommandsHandler.getLabel() + " " + aliasBase + " $1-"),
                      aliasEnabled,
                      CommandAliasType.subbase,
                      (String) entry.getKey());
              newSubAlias.setRequiresPerm(true);
              newSubAlias.setTabComplete(tabEnabled);
              newSubAlias.setDisableBase(baseDisabled);
              this.addDefault(newSubAlias);
            } else {
              CommandAlias existingSub = (CommandAlias) linkedHashMap.get(aliasName);
              if (existingSub != null
                  && existingSub.getType() == CommandAliasType.subbase
                  && existingSub.getCmiCommandName().equals(entry.getKey())) {
                existingSub.setState(aliasEnabled);
                existingSub.setTabComplete(tabEnabled);
                existingSub.setDisableBase(baseDisabled);
                this.addDefault(existingSub);
              }
            }

            if (!string.equalsIgnoreCase(aliasBase)) {
              configReader.addComment(
                  "Alias." + aliasName,
                  new String[] {"/" + CommandsHandler.getLabel() + " " + aliasBase + " $1-"});
            }
          }
        }
      }

      configReader.save();
      this.plugin.loadMessage(value, "regular alias", System.currentTimeMillis() - startTime);
      this.dirtyCache = true;
    }
  }

  public void saveNew() {
    HashSet<String> hashSet = new HashSet();

    for (var entry : this.customAliases.entrySet()) {
      hashSet.add(((CommandAlias) entry.getValue()).getFileName());
    }

    for (String string : hashSet) {
      this.saveNew(string);
    }
  }

  public void saveNew(String string) {
    if (string.endsWith(".yml")) {
      string = string.substring(0, string.length() - ".yml".length());
    }

    File file =
        new File(this.plugin.getDataFolder(), "CustomAlias" + File.separator + string + ".yml");
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      configReader.getC().set("CustomAlias", null);
      configReader.getC().options().copyDefaults(true);
      if (!string.equalsIgnoreCase("CustomAlias")) {
        if (configReader.getC().options().header() != null) {
          configReader.header(CMIList.stringToList(configReader.getC().options().header(), "\n"));
        }
      } else {
        configReader.header(this.CommentList);
      }

      for (var entry : this.customAliases.entrySet()) {
        if (((CommandAlias) entry.getValue()).getFileName().equals(string)) {
          configReader.get(
              "CustomAlias." + (String) entry.getKey() + ".Cmds",
              ((CommandAlias) entry.getValue()).getCommands());
          if (((CommandAlias) entry.getValue()).isRequiresPerm()) {
            configReader.get(
                "CustomAlias." + (String) entry.getKey() + ".Perm",
                ((CommandAlias) entry.getValue()).isRequiresPerm());
          }

          if (((CommandAlias) entry.getValue()).isAddTabs()) {
            configReader.get(
                "CustomAlias." + (String) entry.getKey() + ".AddTabs",
                ((CommandAlias) entry.getValue()).isAddTabs());
          }

          if (((CommandAlias) entry.getValue()).isExact()) {
            configReader.get(
                "CustomAlias." + (String) entry.getKey() + ".ExactMatch",
                ((CommandAlias) entry.getValue()).isExact());
          }

          if (!((CommandAlias) entry.getValue()).isTabComplete()) {
            configReader.get(
                "CustomAlias." + (String) entry.getKey() + ".Tab",
                ((CommandAlias) entry.getValue()).isTabComplete());
          }

          if (((CommandAlias) entry.getValue()).getCustomTabRawList() != null
              && !((CommandAlias) entry.getValue()).getCustomTabRawList().isEmpty()) {
            configReader.get(
                "CustomAlias." + (String) entry.getKey() + ".CustomTabs",
                ((CommandAlias) entry.getValue()).getCustomTabRawList());
          }
        }
      }

      configReader.save();
    }
  }
}
