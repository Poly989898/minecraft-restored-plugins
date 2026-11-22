package com.Zrips.CMI;

import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIGameRule;
import com.Zrips.CMI.Containers.CMITabComplete;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIVillagerProfession;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.BungeeCord.BungeePlayer;
import com.Zrips.CMI.Modules.ChatFormat.CMIChatRoom;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.InteractiveCommand.CMIInteractiveCommand;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Portals.CMIPortal;
import com.Zrips.CMI.Modules.Ranks.CMIRank;
import com.Zrips.CMI.Modules.SpecializedCommands.specCommand;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.Modules.Vanish.VanishAction;
import com.Zrips.CMI.Modules.Warnings.CMIWarningCategory;
import com.Zrips.CMI.Modules.Warps.CmiWarp;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.editwarp;
import com.Zrips.CMI.commands.list.merchant;
import com.Zrips.CMI.utils.CMIEncoder;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CMITreeType;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.CMIPotionEffectType;
import net.Zrips.CMILib.Items.CMITrimMaterial;
import net.Zrips.CMILib.Items.CMITrimPattern;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Sounds.CMISound;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class TabComplete implements TabCompleter {
  private CMI plugin;
  static HashMap<String, TabComplete.TabAction> map = new HashMap<>();
  public static CMITabComplete tabs = new CMITabComplete();
  private static Set<String> itemNameCache = new HashSet<>();
  private static Set<String> blockNameCache = new HashSet<>();

  public TabComplete(CMI cmi) {
    this.plugin = cmi;
    for (TabComplete.TabAction tabAction : TabComplete.TabAction.values()) {
      if (!tabAction.equals(TabComplete.TabAction.na)) {
        map.put("[" + tabAction.name().toLowerCase() + "]", tabAction);
      }
    }
  }

  public List<String> onTabComplete(
      CommandSender commandSender, Command command, String label, String[] args) {
    List<String> list = this.get(commandSender, command.getName(), label, args);
    Collections.sort(list);
    return list;
  }

  public List<Object> getTabCompleteList(String[] args) {
    return tabs.getTabCompleteList(args);
  }

  public String getTabComplete(String string) {
    return tabs.getTabComplete(string);
  }

  public void addTabComplete(String string) {
    tabs.addTabComplete(string);
  }

  public List<String> get(
      CommandSender commandSender, String commandName, String label, String[] args) {
    ArrayList suggestions = new ArrayList();
    if (args.length > 1 && args[args.length - 2].isEmpty()) {
      return suggestions;
    }

    CommandAlias commandAlias =
        this.plugin
            .getAliasManager()
            .getAliasForCommand(
                commandName.toLowerCase()
                    + (args.length > 0 ? " " + CMIArray.toString(args, " ") : ""));
    if (commandAlias == null) {
      commandAlias = this.plugin.getAliasManager().getAliasForCommand(commandName.toLowerCase());
    }

    String fullCommand = commandName;
    String baseCommand = commandName;
    if (commandAlias == null && args.length > 0) {
      CommandAlias fallbackAlias =
          this.plugin.getAliasManager().getAliasForCommand(args[0].toLowerCase());
      if (fallbackAlias != null && !fallbackAlias.isTabComplete()) {
        return suggestions;
      }
    }

    if (commandAlias != null
        && (commandAlias.getCustomTabRawList() == null
            || commandAlias.getCustomTabRawList().isEmpty())) {
      String aliasCleanCommand = commandAlias.getCleanCommand();
      String aliasTail = "";
      if (commandAlias.containsDynamicVariables()) {
        String aliasTemplate = commandAlias.getCommand();
        ArrayList<String> templateParts = new ArrayList();
        if (aliasTemplate.contains(" ")) {
          templateParts.addAll(Arrays.asList(aliasTemplate.split(" ")));
        } else {
          templateParts.add(aliasTemplate);
        }

        ArrayList<String> templateArgs = new ArrayList();
        templateArgs.add(commandName);
        templateArgs.addAll(Arrays.asList(args));

        for (String templatePart : templateParts) {
          boolean openEnded = false;
          if (templatePart.startsWith("$")) {
            Integer argIndex = null;

            try {
              if (templatePart.endsWith("-")) {
                openEnded = true;
              }

              argIndex = Integer.parseInt(templatePart.replace("$", "").replace("-", ""));
            } catch (Exception exception) {
              continue;
            }

            String argValue = null;
            if (argIndex < templateArgs.size()) {
              if (!openEnded) {
                argValue = (String) templateArgs.get(argIndex);
              } else {
                argValue = "";

                for (int argPos = argIndex; argPos < templateArgs.size(); argPos++) {
                  if (!argValue.isEmpty()) {
                    argValue = argValue + " ";
                  }

                  argValue = argValue + (String) templateArgs.get(argPos);
                }
              }
            }

            if ((argValue == null || argValue.isEmpty() || templateArgs.size() == argIndex + 1)
                && args.length > 0
                && args[args.length - 1].isEmpty()) {
              if (aliasTemplate.contains("$" + argIndex + "-")) {
                aliasTemplate = aliasTemplate.split("\\$" + argIndex + "-", 2)[0];
              }

              if (aliasTemplate.contains("$" + argIndex)) {
                aliasTemplate = aliasTemplate.split("\\$" + argIndex, 2)[0];
              }
              break;
            }

            if (argValue == null) {
              argValue = "";
            }

            if (aliasTemplate.contains("$" + argIndex)) {
              aliasTemplate = aliasTemplate.replace("$" + argIndex + "-", argValue);
              aliasTemplate = aliasTemplate.replace("$" + argIndex, argValue);
            }
          }
        }

        aliasTail = aliasTemplate;
      } else {
        String[] argsCopy = args;
        int partCount = args.length;

        for (int partIndex = 0; partIndex < partCount; partIndex++) {
          String argPart = argsCopy[partIndex];
          if (!aliasTail.isEmpty()) {
            aliasTail = aliasTail + " ";
          }

          aliasTail = aliasTail + (argPart.isEmpty() ? "%%space%%" : argPart);
        }

        aliasTail = commandName + (aliasTail.isEmpty() ? "" : " " + aliasTail);
        if (commandAlias.getAlias().length() <= aliasTail.length()) {
          aliasTail = aliasTail.substring(commandAlias.getAlias().length(), aliasTail.length());
        }

        aliasTail = aliasCleanCommand + aliasTail;
      }

      String resolvedCommand =
          commandAlias.getCommand().replace(aliasTail.replace("%%space%%", ""), "");
      resolvedCommand =
          resolvedCommand.contains(" ") ? resolvedCommand.split(" ")[0] : resolvedCommand;
      String playerNameArg = null;
      if (resolvedCommand.equalsIgnoreCase("[playerName]") && commandSender instanceof Player) {
        playerNameArg = ((Player) commandSender).getName();
      }

      commandName = aliasTail.contains(" ") ? aliasTail.split(" ")[0] : aliasTail;
      aliasTail =
          aliasTail.substring(
              commandName.contains(" ") ? commandName.length() + 1 : commandName.length(),
              aliasTail.length());
      aliasTail = aliasTail.startsWith(" ") ? aliasTail.substring(1) : aliasTail;
      ArrayList aliasArgList = new ArrayList();
      aliasArgList.addAll(
          aliasTail.contains(" ") ? Arrays.asList(aliasTail.split(" ")) : Arrays.asList(aliasTail));
      String[] aliasArgs = new String[aliasArgList.size()];
      if (args.length > 0
          && args[args.length - 1].isEmpty()
          && !((String) aliasArgList.get(aliasArgList.size() - 1)).isEmpty()) {
        aliasArgs = new String[aliasArgList.size() + 1];
      }

      if (playerNameArg != null) {
        for (int arrayIndex = 0; arrayIndex < aliasArgList.size(); arrayIndex++) {
          aliasArgs[arrayIndex] =
              ((String) aliasArgList.get(arrayIndex)).equalsIgnoreCase("%%space%%")
                  ? playerNameArg
                  : (String) aliasArgList.get(arrayIndex);
        }
      } else {
        for (int aliasArgIndex = 0; aliasArgIndex < aliasArgList.size(); aliasArgIndex++) {
          aliasArgs[aliasArgIndex] =
              ((String) aliasArgList.get(aliasArgIndex)).equalsIgnoreCase("%%space%%")
                  ? ""
                  : (String) aliasArgList.get(aliasArgIndex);
        }
      }

      if (args.length > 0
          && args[args.length - 1].isEmpty()
          && !((String) aliasArgList.get(aliasArgList.size() - 1)).isEmpty()) {
        aliasArgs[aliasArgList.size()] = "";
      }

      PluginCommand pluginCommand = Bukkit.getPluginCommand(commandName);
      if (pluginCommand == null) {
        specCommand specCommand =
            this.plugin
                .getSpecializedCommandManager()
                .processSpecializedCommand(commandAlias.getCommand());
        String specCommandName = specCommand.getCmd().split(" ")[0];
        if (aliasArgs.length > 0 && aliasArgs[0].equalsIgnoreCase(specCommandName)) {
          aliasArgs = CMIArray.removeFirst(aliasArgs);
        }

        pluginCommand = Bukkit.getPluginCommand(specCommandName);
      }

      String lastArg = "";
      if (args.length > 0) {
        lastArg = args[args.length - 1];
      }

      if (pluginCommand != null
          && pluginCommand.getTabCompleter() != null
          && !label.equalsIgnoreCase("CMIAlias")) {
        return pluginCommand
            .getTabCompleter()
            .onTabComplete(commandSender, pluginCommand, "CMIAlias", aliasArgs);
      }

      if (!label.equalsIgnoreCase("CMIAlias")) {
        String[] argsSnapshot = args;
        int argCount = args.length;

        for (int argIdx = 0; argIdx < argCount; argIdx++) {
          String argText = argsSnapshot[argIdx];
          if (argText != null && !argText.isEmpty()) {
            fullCommand = fullCommand + " " + argText;
          }
        }

        var aliasList =
            this.plugin.getAliasManager().getAliasStartingWith(baseCommand.toLowerCase() + " ");
        HashSet<String> aliasSet = new HashSet();

        for (CommandAlias aliasEntry : aliasList) {
          if (aliasEntry.getAlias().length() > fullCommand.length() + 1) {
            String aliasSuggestion = aliasEntry.getAlias().substring(fullCommand.length() + 1);
            aliasSuggestion =
                aliasSuggestion.contains(" ") ? aliasSuggestion.split(" ")[0] : aliasSuggestion;
            aliasSet.add(aliasSuggestion);
          }
        }

        if (!lastArg.isEmpty()) {
          for (CommandAlias aliasItem : aliasList) {
            if (aliasItem.getAlias().length() > baseCommand.length() + 1) {
              String suggestionText = aliasItem.getAlias().substring(baseCommand.length() + 1);
              suggestionText =
                  suggestionText.contains(" ") ? suggestionText.split(" ")[0] : suggestionText;
              if (suggestionText.startsWith(lastArg)) {
                aliasSet.add(suggestionText);
              }
            }
          }
        }

        ArrayList<String> aliasSuggestions = new ArrayList();
        if (!aliasSet.isEmpty()) {
          aliasSuggestions.addAll(aliasSet);
          return aliasSuggestions;
        }

        Player player = null;
        if (commandSender instanceof Player) {
          player = (Player) commandSender;
        }

        for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
          if (player == null || player.canSee(nearbyPlayer)) {
            aliasSuggestions.add(nearbyPlayer.getName());
          }
        }

        if (args.length > 0 && !args[args.length - 1].isEmpty()) {
          String lowerLastArg = args[args.length - 1].toLowerCase();

          for (String candidate : new ArrayList<>(aliasSuggestions)) {
            if (!candidate.toLowerCase().startsWith(lowerLastArg)) {
              aliasSuggestions.remove(candidate);
            }
          }
        }

        return aliasSuggestions;
      }
    }

    if (args.length == 1 && commandName.equalsIgnoreCase(CommandsHandler.getLabel())) {
      String firstArg = args[0];
      ArrayList<String> commandNames = new ArrayList();

      for (var commandEntry :
          this.plugin.getCommandManager().GetCommands(commandSender).entrySet()) {
        commandNames.add((String) commandEntry.getKey());
      }

      StringUtil.copyPartialMatches(firstArg, commandNames, suggestions);
    }

    CMICommand cmiCommand = this.plugin.getCommandManager().getCommands().get(commandName);
    if (cmiCommand != null
        && (args.length == 0 || args.length == 1 && args[0].isEmpty())
        && this.plugin.getAliasManager().getDefaultAlias().get(commandName.toLowerCase()) != null) {
      args = CMIArray.addFirst(args, commandName);
    }

    if (args.length == 0) {
      return suggestions;
    }

    int lastIndex = args.length - 1;
    String currentArg = args[lastIndex];
    List<String> suggestionTokens = new ArrayList();
    if (commandAlias != null
        && commandAlias.getCustomTabRawList() != null
        && !commandAlias.getCustomTabRawList().isEmpty()) {
      if (args.length > 1) {
        int spaceCount = 0;
        if (commandAlias.getAlias().contains(" ")) {
          for (char ch : commandAlias.getAlias().toCharArray()) {
            if (ch == ' ') {
              spaceCount++;
            }
          }

          args = Arrays.copyOfRange(args, spaceCount, args.length);
        }

        if (spaceCount == 0) {
          suggestionTokens.add(label);
        }
      }

      var aliasParts = new ArrayList<>(Arrays.asList(commandAlias.getAlias().split(" ")));
      if (!aliasParts.isEmpty()) {
        aliasParts.remove(0);
      }

      if (!aliasParts.isEmpty()) {
        suggestionTokens.addAll(aliasParts);
      }

      ((List) suggestionTokens).addAll(commandAlias.getTabCompleteList(args));

      for (int resultIndex = 0; resultIndex < suggestionTokens.size(); resultIndex++) {
        if (suggestionTokens.get(resultIndex) instanceof String) {
          String resolvedPlaceholder =
              this.plugin
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(
                      commandSender instanceof Player ? (Player) commandSender : null,
                      (String) suggestionTokens.get(resultIndex));
          if (resolvedPlaceholder.contains(",")) {
            if (resolvedPlaceholder.contains(", ")) {
              resolvedPlaceholder = resolvedPlaceholder.replace(", ", "><");
            } else {
              resolvedPlaceholder = resolvedPlaceholder.replace(",", "><");
            }
          }

          suggestionTokens.set(resultIndex, CMIText.replaceUnderScoreSpace(resolvedPlaceholder));
        } else {
          suggestionTokens.set(resultIndex, suggestionTokens.get(resultIndex));
        }
      }

      suggestions.clear();
      lastIndex++;
    } else {
      suggestionTokens = (List) this.getTabCompleteList(args);
      if (!label.equalsIgnoreCase("CMIAlias")
          && args.length > 1
          && this.plugin.getCommandManager().getDisabledBase().contains(args[0].toLowerCase())) {
        return new ArrayList<>();
      }

      if (!label.equalsIgnoreCase("CMIAlias") && args.length == 1) {
        suggestions.removeAll(this.plugin.getCommandManager().getDisabledBase());
      }

      if (suggestionTokens.size() < lastIndex || suggestionTokens.isEmpty() || lastIndex < 1) {
        return suggestions;
      }
    }

    String actionName = null;
    HashSet<com.Zrips.CMI.TabComplete.TabAction> actionSet = new HashSet();
    if (lastIndex - 1 < suggestionTokens.size()) {
      Object singleObject = suggestionTokens.get(lastIndex - 1);
      if (singleObject instanceof TabComplete.TabAction) {
        actionSet.add((TabComplete.TabAction) singleObject);
      } else {
        actionName = (String) singleObject;
      }
    }

    ArrayList<String> actionSuggestions = new ArrayList();
    if (actionName != null) {
      if (actionName.contains("><")) {
        String[] alternatives = actionName.split("><");
        String[] alternativesCopy = alternatives;
        int tokenCount = alternatives.length;

        for (int tokenIndex = 0; tokenIndex < tokenCount; tokenIndex++) {
          String actionToken = alternativesCopy[tokenIndex];
          if (TabComplete.TabAction.getAction(actionToken) != TabComplete.TabAction.na) {
            actionSet.add(TabComplete.TabAction.getAction(actionToken));
          } else if (actionToken.equalsIgnoreCase("colors")) {
            actionSuggestions.addAll(getColorNames());
          } else {
            if (actionToken.startsWith("_")) {
              actionToken = actionToken.substring(1);
            }

            actionSuggestions.add(actionToken);
          }
        }
      } else {
        TabComplete.TabAction tabAction = TabComplete.TabAction.getAction(actionName);
        if (tabAction != TabComplete.TabAction.na) {
          actionSet.add(tabAction);
        } else {
          actionSuggestions.add(actionName);
        }
      }
    }

    if (commandAlias != null && commandAlias.getType() == CommandAliasType.custom) {
      lastIndex--;
    }

    for (TabComplete.TabAction actionToRun : actionSet) {
      actionSuggestions.addAll(this.getByAction(commandSender, actionToRun, args, lastIndex));
    }

    StringUtil.copyPartialMatches(currentArg, actionSuggestions, suggestions);
    return suggestions;
  }

  public List<String> getByAction(
      CommandSender commandSender, TabComplete.TabAction tabAction, String[] args, int argIndex) {
    HashSet hashSet;
    hashSet = new HashSet();
    short shortValue = 800;
    statSwitch:
    switch (tabAction) {
      case na:
      default:
        break;
      case allPlayername:
      case playername:
        Player player = null;
        if (commandSender instanceof Player) {
          player = (Player) commandSender;
        }

        boolean canSeeOthers = true;
        if (args.length > 0 && tabAction != TabComplete.TabAction.allPlayername) {
          CMICommand cmiCommand =
              this.plugin.getCommandManager().getCommands().get(args[0].toLowerCase());
          if (cmiCommand != null && cmiCommand.getAnottation().others()) {
            canSeeOthers =
                PermissionsManager.CMIPerm.command_$1_others.hasPermission(
                    commandSender, cmiCommand.getName());
          }
        }

        boolean seeVanished = PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          if ((player == null || player.canSee(onlinePlayer))
              && (canSeeOthers
                  || onlinePlayer.getName().equalsIgnoreCase(commandSender.getName()))) {
            if (this.plugin.getNickNameManager().isIncludeNickNameInTabComplete()) {
              CMIUser cmiUser = CMIUser.getUser(onlinePlayer);
              if (cmiUser != null && cmiUser.getNickName() != null) {
                hashSet.add(CMIChatColor.stripColor(cmiUser.getNickName()));
                if (!this.plugin.getNickNameManager().isIncludeRealNameInTabComplete()) {
                  continue;
                }
              }
            }

            hashSet.add(onlinePlayer.getName());
          }
        }

        if (this.plugin.getBungeeCordManager().isNamesInTabComplete()
            && this.plugin.getBungeeCordManager().isEnabledSupport()) {
          var set = this.plugin.getBungeeCordManager().getPlayerNamesFromServers(seeVanished, true);
          set.remove(commandSender.getName());
          hashSet.addAll(set);
        }
        break;
      case allIGNPlayername:
        Player ignSender = null;
        if (commandSender instanceof Player) {
          ignSender = (Player) commandSender;
        }

        boolean includeAll = true;
        boolean seeVanishedIgn =
            PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender);

        for (Player ignOnlinePlayer : Bukkit.getOnlinePlayers()) {
          if ((ignSender == null || ignSender.canSee(ignOnlinePlayer))
              && (includeAll
                  || ignOnlinePlayer.getName().equalsIgnoreCase(commandSender.getName()))) {
            hashSet.add(ignOnlinePlayer.getName());
          }
        }

        if (this.plugin.getBungeeCordManager().isNamesInTabComplete()
            && this.plugin.getBungeeCordManager().isEnabledSupport()) {
          for (var entry : this.plugin.getBungeeCordManager().getServers().entrySet()) {
            if (!((BungeeCordServer) entry.getValue())
                .getName()
                .equalsIgnoreCase(this.plugin.getBungeeCordManager().getThisServerName())) {
              for (var bungeePlayerEntry :
                  ((BungeeCordServer) entry.getValue()).getPlayersMapName().entrySet()) {
                BungeePlayer bungeePlayer = (BungeePlayer) bungeePlayerEntry.getValue();
                if ((!bungeePlayer.getVanished() || seeVanishedIgn)
                    && (includeAll
                        || bungeePlayer.getName().equalsIgnoreCase(commandSender.getName()))) {
                  hashSet.add(CMIChatColor.stripColor(bungeePlayer.getName()));
                }
              }
            }
          }
        }
        break;
      case mutedplayername:
        Player mutedSender = null;
        if (commandSender instanceof Player) {
          mutedSender = (Player) commandSender;
        }

        boolean seeVanishedMuted =
            PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender);

        for (Player mutedOnlinePlayer : Bukkit.getOnlinePlayers()) {
          if (mutedSender == null || mutedSender.canSee(mutedOnlinePlayer)) {
            CMIUser mutedUser = CMIUser.getUser(mutedOnlinePlayer);
            if (mutedUser != null && mutedUser.isMuted()) {
              hashSet.add(mutedOnlinePlayer.getName());
              if (this.plugin.getNickNameManager().isIncludeNickNameInTabComplete()
                  && mutedUser.getNickName() != null) {
                hashSet.add(CMIChatColor.stripColor(mutedUser.getNickName()));
              }
            }
          }
        }
        break;
      case damageCause:
        for (DamageCause damageCause : DamageCause.values()) {
          hashSet.add(CMIText.everyFirstToUpperCase(damageCause.toString()).replace(" ", ""));
        }
        break;
      case bannedplayername:
        if (commandSender instanceof Player) {
          Player unusedPlayer = (Player) commandSender;
        }

        if (CMIBanRecords.getBanEntries() != null) {
          for (BanEntry banEntry : CMIBanRecords.getBanEntries().getBanEntries()) {
            hashSet.add(banEntry.getTarget());
            if (hashSet.size() > shortValue) {
              break;
            }
          }
        }

        if (CMIBanRecords.getBanEntriesByIP() != null) {
          for (BanEntry ipBanEntry : CMIBanRecords.getBanEntriesByIP().getBanEntries()) {
            hashSet.add(ipBanEntry.getTarget());
            if (hashSet.size() > shortValue) {
              return new ArrayList<>(hashSet);
            }
          }
        }
        break;
      case playerOption:
        for (PlayerOption playerOption : PlayerOption.values()) {
          if (PermissionsManager.CMIPerm.command_options_$1.hasPermission(
              commandSender, playerOption.name())) {
            hashSet.add(playerOption.name().toLowerCase());
          }
        }
        break;
      case gamemode:
        for (GameMode gameMode : GameMode.values()) {
          hashSet.add(gameMode.name().toLowerCase());
        }
        break;
      case worlds:
        for (World world : Bukkit.getWorlds()) {
          hashSet.add(world.getName());
        }
        break;
      case portals:
        for (var portalEntry : this.plugin.getPortalManager().getPortals().entrySet()) {
          if (((CMIPortal) portalEntry.getValue()).isEnabled()) {
            hashSet.add(((CMIPortal) portalEntry.getValue()).getName());
          }
        }
        break;
      case allportals:
        for (var allPortalEntry : this.plugin.getPortalManager().getPortals().entrySet()) {
          hashSet.add(((CMIPortal) allPortalEntry.getValue()).getName());
        }
        break;
      case itemname:
        if (itemNameCache.size() > 100) {
          hashSet.addAll(itemNameCache);
        } else {
          for (CMIMaterial itemMaterial : CMIMaterial.values()) {
            if (!itemMaterial.toString().startsWith("LEGACY_")) {
              try {
                if (itemMaterial.getMaterial() == null || !itemMaterial.getMaterial().isItem()) {
                  continue;
                }
              } catch (Exception | Error error) {
              }

              if (itemMaterial
                  .getName()
                  .replace(" ", "")
                  .toLowerCase()
                  .startsWith(args[args.length - 1].toLowerCase())) {
                itemNameCache.add(
                    CMIText.everyFirstToUpperCase(itemMaterial.getName()).replace(" ", ""));
              }

              if (itemMaterial
                  .toString()
                  .replace(" ", "")
                  .replace("_", "")
                  .toLowerCase()
                  .startsWith(args[args.length - 1].toLowerCase())) {
                itemNameCache.add(
                    CMIText.everyFirstToUpperCase(itemMaterial.toString().replace("_", " "))
                        .replace(" ", ""));
              }

              if (!itemMaterial.getLegacyNames().isEmpty()) {
                for (String legacyName : itemMaterial.getLegacyNames()) {
                  if (legacyName
                      .replace(" ", "")
                      .replace("_", "")
                      .toLowerCase()
                      .startsWith(args[args.length - 1].toLowerCase())) {
                    itemNameCache.add(
                        CMIText.everyFirstToUpperCase(legacyName.replace("_", " "))
                            .replace(" ", ""));
                  }
                }
              }
            }
          }

          hashSet.addAll(itemNameCache);
        }
        break;
      case playeritems:
        if (commandSender instanceof Player) {
          Player inventoryPlayer = (Player) commandSender;
          for (ItemStack itemStack : inventoryPlayer.getInventory().getContents()) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
              CMIMaterial heldMaterial = CMIMaterial.get(itemStack);
              if (!heldMaterial.toString().startsWith("LEGACY_")) {
                try {
                  if (heldMaterial.getMaterial() == null || !heldMaterial.getMaterial().isItem()) {
                    continue;
                  }
                } catch (Exception | Error error2) {
                }

                if (heldMaterial
                    .toString()
                    .replace(" ", "")
                    .replace("_", "")
                    .toLowerCase()
                    .startsWith(args[args.length - 1].toLowerCase())) {
                  hashSet.add(
                      heldMaterial.toString().replace(" ", "").replace("_", "").toLowerCase());
                }

                if (!heldMaterial.getLegacyNames().isEmpty()) {
                  for (String legacyMaterialName : heldMaterial.getLegacyNames()) {
                    if (legacyMaterialName
                        .replace(" ", "")
                        .replace("_", "")
                        .toLowerCase()
                        .startsWith(args[args.length - 1].toLowerCase())) {
                      hashSet.add(
                          legacyMaterialName.replace(" ", "").replace("_", "").toLowerCase());
                    }
                  }
                }
              }
            }
          }
        }
        break;
      case blockname:
        if (!blockNameCache.isEmpty()) {
          hashSet.addAll(blockNameCache);
        } else {
          for (CMIMaterial blockMaterial : CMIMaterial.values()) {
            if (!blockMaterial.toString().startsWith("LEGACY_")) {
              try {
                if (blockMaterial.getMaterial() == null || !blockMaterial.getMaterial().isBlock()) {
                  continue;
                }
              } catch (Exception | Error error3) {
              }

              if (blockMaterial
                  .getName()
                  .replace(" ", "")
                  .toLowerCase()
                  .startsWith(args[args.length - 1].toLowerCase())) {
                blockNameCache.add(blockMaterial.getName().replace(" ", "").toLowerCase());
              }

              if (blockMaterial
                  .toString()
                  .replace(" ", "")
                  .replace("_", "")
                  .toLowerCase()
                  .startsWith(args[args.length - 1].toLowerCase())) {
                blockNameCache.add(
                    blockMaterial.toString().replace(" ", "").replace("_", "").toLowerCase());
              }

              if (!blockMaterial.getLegacyNames().isEmpty()) {
                for (String blockLegacyName : blockMaterial.getLegacyNames()) {
                  if (blockLegacyName
                      .replace(" ", "")
                      .replace("_", "")
                      .toLowerCase()
                      .startsWith(args[args.length - 1].toLowerCase())) {
                    blockNameCache.add(
                        blockLegacyName.replace(" ", "").replace("_", "").toLowerCase());
                  }
                }
              }
            }
          }

          hashSet.addAll(blockNameCache);
        }
        break;
      case EntityType:
        for (EntityType entityType : EntityType.values()) {
          if (entityType.isSpawnable()) {
            hashSet.add(entityType.name().toLowerCase());
          }
        }
        break;
      case cleanEntityType:
        for (EntityType cleanType : EntityType.values()) {
          if (cleanType.isSpawnable()) {
            hashSet.add(cleanType.name().replace("_", "").toLowerCase());
          }
        }
        break;
      case kit:
        boolean allKits = PermissionsManager.CMIPerm.kit_$1.hasPermission(commandSender, "*");

        for (var kitEntry : this.plugin.getKitsManager().getKitMap().entrySet()) {
          if ((((Kit) kitEntry.getValue()).isEnabled() || allKits)
              && (allKits
                  || PermissionsManager.CMIPerm.kit_$1.hasPermission(
                      commandSender, 5000L, (String) kitEntry.getKey()))
              && !hashSet.contains(((Kit) kitEntry.getValue()).getCommandName())) {
            hashSet.add(((Kit) kitEntry.getValue()).getCommandName());
          }
        }
        break;
      case kitnames:
        boolean allKitNames = PermissionsManager.CMIPerm.kit_$1.hasPermission(commandSender, "*");

        for (var kitNameEntry : this.plugin.getKitsManager().getKitMap().entrySet()) {
          if ((((Kit) kitNameEntry.getValue()).isEnabled() || allKitNames)
              && (allKitNames
                  || PermissionsManager.CMIPerm.kit_$1.hasPermission(
                      commandSender, 5000L, (String) kitNameEntry.getKey()))
              && !hashSet.contains(((Kit) kitNameEntry.getValue()).getConfigName())) {
            hashSet.add(((Kit) kitNameEntry.getValue()).getConfigName());
          }
        }
        break;
      case kitp:
        boolean allKitPreview = PermissionsManager.CMIPerm.kit_$1.hasPermission(commandSender, "*");

        for (var kitPreviewEntry : this.plugin.getKitsManager().getKitMap().entrySet()) {
          if ((((Kit) kitPreviewEntry.getValue()).isEnabled() || allKitPreview)
              && (allKitPreview
                  || PermissionsManager.CMIPerm.kit_$1.hasPermission(
                      commandSender, 5000L, (String) kitPreviewEntry.getKey())
                  || PermissionsManager.CMIPerm.kit_$1_preview.hasPermission(
                      commandSender, 5000L, (String) kitPreviewEntry.getKey()))
              && !hashSet.contains(((Kit) kitPreviewEntry.getValue()).getCommandName())) {
            hashSet.add(((Kit) kitPreviewEntry.getValue()).getCommandName());
          }
        }
        break;
      case chatroom:
        for (CMIChatRoom cmiChatRoom : this.plugin.getChatFormatManager().getChatRooms().values()) {
          if (cmiChatRoom != null) {
            hashSet.add(cmiChatRoom.getChatName());
          }
        }
        break;
      case biome:
        for (String biomeName : CMIBiome.values()) {
          hashSet.add(biomeName.toLowerCase());
        }
        break;
      case treeType:
        for (CMITreeType cmiTreeType : CMITreeType.values()) {
          hashSet.add(CMIText.firstToUpperCase(cmiTreeType.getName().replace(" ", "")));
          if (cmiTreeType.getIdentifiers() != null) {
            for (String treeIdentifier : cmiTreeType.getIdentifiers()) {
              hashSet.add(CMIText.firstToUpperCase(treeIdentifier.replace(" ", "")));
            }
          }
        }
        break;
      case maxplayers:
        hashSet.add(String.valueOf(Bukkit.getMaxPlayers()));
        break;
      case potioneffect:
        try {
          for (CMIPotionEffectType cmiPotionEffectType : CMIPotionEffectType.values()) {
            if (cmiPotionEffectType != null) {
              hashSet.add(cmiPotionEffectType.getName().replace(" ", ""));
            }
          }
        } catch (Throwable throwable) {
          for (PotionEffectType potionEffectType : PotionEffectType.values()) {
            if (potionEffectType != null) {
              if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
                hashSet.add(potionEffectType.getKey().getKey().toLowerCase().replace(" ", ""));
              } else {
                hashSet.add(potionEffectType.getName().toLowerCase().replace(" ", ""));
              }
            }
          }
        }
        break;
      case effect:
        for (CMIParticle cmiParticle : CMIParticle.getParticleList()) {
          if (cmiParticle != null && cmiParticle.name() != null && cmiParticle.isParticle()) {
            hashSet.add(cmiParticle.name().toLowerCase().replace(" ", "_"));
          }
        }
        break;
      case merchants:
        for (Profession profession : merchant.getVillagerProfessions()) {
          if (profession != null) {
            String professionName = CMIVillagerProfession.getName(profession);
            if (!professionName.equalsIgnoreCase("normal")
                && !professionName.equalsIgnoreCase("husk")
                && !professionName.equalsIgnoreCase("none")) {
              hashSet.add(professionName.toLowerCase());
            }
          }
        }
        break;
      case enchant:
        for (Enchantment enchantment : Enchantment.values()) {
          if (enchantment != null && CMIEnchantment.isEnabled(enchantment)) {
            String enchantName = CMIEnchantment.getName(enchantment);
            hashSet.add(CMIChatColor.stripColor(enchantName));
          }
        }

        for (String enchantKey : CMIEnchantment.getEnchantList().keySet()) {
          if (enchantKey != null) {
            hashSet.add(CMIChatColor.stripColor(enchantKey));
          }
        }
        break;
      case limitedEnchant:
        for (Enchantment limitedEnchantment : Enchantment.values()) {
          if (limitedEnchantment != null && CMIEnchantment.isEnabled(limitedEnchantment)) {
            CMIEnchantment cmiEnchantment = CMIEnchantment.get(limitedEnchantment);
            if (cmiEnchantment != null) {
              String enchantKeyName = cmiEnchantment.getKeyName();
              if (!this.plugin.getEnchantManager().isRequireSpecificPermission()
                  || this.plugin.getEnchantManager().isRequireSpecificPermission()
                      && PermissionsManager.CMIPerm.enchantments_$1.hasPermission(
                          commandSender, enchantKeyName.replace("_", ""))) {
                hashSet.add(enchantKeyName);
              }
            }
          }
        }

        for (CMIEnchantment registeredEnchant : CMIEnchantment.getEnchants().values()) {
          if (registeredEnchant != null) {
            String registeredKeyName = registeredEnchant.getKeyName();
            if (!hashSet.contains(registeredKeyName)
                && (!this.plugin.getEnchantManager().isRequireSpecificPermission()
                    || this.plugin.getEnchantManager().isRequireSpecificPermission()
                        && PermissionsManager.CMIPerm.enchantments_$1.hasPermission(
                            commandSender, registeredKeyName.replace("_", "")))) {
              hashSet.add(registeredKeyName);
            }
          }
        }
        break;
      case halfViewRange:
        hashSet.add(String.valueOf(Bukkit.getViewDistance() / 2));
        break;
      case doubleViewRange:
        int doubleRange = Bukkit.getViewDistance() * 2;
        if (doubleRange > 32) {
          doubleRange = 32;
        }

        hashSet.add(String.valueOf(doubleRange));
        break;
      case ViewRange:
        hashSet.add(String.valueOf(Bukkit.getViewDistance()));
        break;
      case maxenchantlevel:
        if (args.length >= 2) {
          String enchantArg = args[argIndex - 1];
          Enchantment maxEnchantment = CMIEnchantment.get(enchantArg);
          if (maxEnchantment != null) {
            hashSet.add(String.valueOf(maxEnchantment.getMaxLevel()));
          }
        }
        break;
      case currentItemName:
        if (args.length >= 2) {
          String playerArg = args[argIndex - 1];
          Player targetPlayer = CMIUser.getOnlinePlayer(playerArg);
          if (targetPlayer == null && commandSender instanceof Player) {
            targetPlayer = (Player) commandSender;
          }

          if (targetPlayer != null) {
            ItemStack targetItem = CMIItemStack.getItemInMainHand(targetPlayer);
            if (targetItem != null
                && targetItem.hasItemMeta()
                && targetItem.getItemMeta().hasDisplayName()) {
              hashSet.add(CMIChatColor.deColorize(targetItem.getItemMeta().getDisplayName()));
            }
          }
        }
        break;
      case loreLine:
        if (args.length >= 2) {
          String loreTargetName = args[argIndex - 1];
          Player loreTarget = CMIUser.getOnlinePlayer(loreTargetName);
          if (loreTarget != null) {
            ItemStack loreItem = loreTarget.getItemInHand();
            if (loreItem != null && loreItem.hasItemMeta() && loreItem.getItemMeta().hasLore()) {
              var list = loreItem.getItemMeta().getLore();

              for (int loreLineIndex = 0; loreLineIndex < list.size(); loreLineIndex++) {
                hashSet.add(String.valueOf(loreLineIndex + 1));
              }
            }
          }
        }
        break;
      case currentItemLore:
        if (args.length >= 2) {
          String loreArg = null;
          int loreLineNumber = -1;

          for (int tokenIndex = 0; tokenIndex < 3 && tokenIndex < args.length; tokenIndex++) {
            String loreToken = args[tokenIndex];
            if (loreLineNumber == -1) {
              try {
                loreLineNumber = Integer.parseInt(loreToken);
                continue;
              } catch (NumberFormatException numberFormatException) {
              }
            }

            if (loreToken.startsWith("-p:")) {
              loreArg = loreToken.substring(3);
            } else {
              loreArg = loreToken;
            }
          }

          if (loreLineNumber < 1) {
            loreLineNumber = 1;
          }

          Player lorePlayer = CMIUser.getOnlinePlayer(loreArg);
          if (lorePlayer == null && commandSender instanceof Player) {
            lorePlayer = (Player) commandSender;
          }

          if (lorePlayer != null) {
            ItemStack loreItemInHand = CMIItemStack.getItemInMainHand(lorePlayer);
            if (loreItemInHand != null
                && loreItemInHand.hasItemMeta()
                && loreItemInHand.getItemMeta().hasLore()) {
              var loreLines = loreItemInHand.getItemMeta().getLore();
              if (loreLines.size() >= loreLineNumber) {
                hashSet.add(CMIChatColor.deColorize((String) loreLines.get(loreLineNumber - 1)));
              }
            }
          }
        }
        break;
      case currentLocation:
        if (commandSender instanceof Player) {
          hashSet.add(CMILocation.toString(((Player) commandSender).getLocation(), true));
        }
        break;
      case currentFullLocation:
        if (commandSender instanceof Player) {
          hashSet.add(CMILocation.toString(((Player) commandSender).getLocation(), false));
        }
        break;
      case currentX:
        if (commandSender instanceof Player) {
          hashSet.add(
              String.valueOf(
                  (int) (((Player) commandSender).getLocation().getX() * 100.0) / 100.0));
        }
        break;
      case currentY:
        if (commandSender instanceof Player) {
          hashSet.add(
              String.valueOf(
                  (int) (((Player) commandSender).getLocation().getY() * 100.0) / 100.0));
        }
        break;
      case currentZ:
        if (commandSender instanceof Player) {
          hashSet.add(
              String.valueOf(
                  (int) (((Player) commandSender).getLocation().getZ() * 100.0) / 100.0));
        }
        break;
      case currentWorld:
        if (commandSender instanceof Player) {
          hashSet.add(((Player) commandSender).getWorld().getName());
        }
        break;
      case currentPitch:
        if (commandSender instanceof Player) {
          hashSet.add(
              String.valueOf(
                  (int) (((Player) commandSender).getLocation().getPitch() * 100.0F) / 100.0));
        }
        break;
      case currentYaw:
        if (commandSender instanceof Player) {
          hashSet.add(
              String.valueOf(
                  (int) (((Player) commandSender).getLocation().getYaw() * 100.0F) / 100.0));
        }
        break;
      case itemFlag:
        for (ItemFlag itemFlag : ItemFlag.values()) {
          hashSet.add(itemFlag.name().toLowerCase());
        }
        break;
      case nickName:
        if (commandSender instanceof Player) {
          CMIUser senderUser = CMIUser.getUser((Player) commandSender);
          if (senderUser != null) {
            if (senderUser.getNickName() != null) {
              hashSet.add(CMIChatColor.stripColor(senderUser.getNickName()));
            } else {
              hashSet.add(CMIChatColor.deColorize(senderUser.getName()));
            }
          }
        }
        break;
      case fullNickName:
        if (commandSender instanceof Player) {
          CMIUser senderCmiUser = CMIUser.getUser((Player) commandSender);
          if (senderCmiUser != null) {
            if (senderCmiUser.getNickName() != null) {
              hashSet.add(CMIChatColor.deColorize(senderCmiUser.getNickName()));
            } else {
              hashSet.add(CMIChatColor.deColorize(senderCmiUser.getName()));
            }
          }
        }
        break;
      case gamerule:
        World gameRuleWorld = (World) Bukkit.getWorlds().get(0);
        if (commandSender instanceof Player) {
          gameRuleWorld = ((Player) commandSender).getWorld();
        }

        for (String gameRuleName : gameRuleWorld.getGameRules()) {
          hashSet.add(gameRuleName);
        }
        break;
      case gamerulevalue:
        World ruleWorld = (World) Bukkit.getWorlds().get(0);
        if (args.length >= 2) {
          World argWorld = CMIWorld.getWorld(args[args.length - 3]);
          if (argWorld != null) {
            ruleWorld = argWorld;
          }

          String gameRuleArg = args[args.length - 2];
          if (ruleWorld == null && commandSender instanceof Player) {
            ruleWorld = ((Player) commandSender).getWorld();
          }

          if (ruleWorld != null) {
            var cmiGameRule = CMIGameRule.get(gameRuleArg);
            if (cmiGameRule != null) {
              Object object = cmiGameRule.get(ruleWorld);
              if (object != null) {
                hashSet.add(String.valueOf(object));
              }
            }
          }
        }
        break;
      case nickNames:
        Player nickSender = null;
        if (commandSender instanceof Player) {
          nickSender = (Player) commandSender;
        }

        for (Player nickPlayer : Bukkit.getOnlinePlayers()) {
          if (nickSender == null || nickSender.canSee(nickPlayer)) {
            CMIUser nickUser = this.plugin.getPlayerManager().getUser(nickPlayer);
            if (nickUser != null) {
              String nickValue = nickUser.getNickName();
              nickValue = nickValue == null ? nickUser.getName() : nickValue;
              nickValue = CMIChatColor.stripColor(CMIChatColor.deColorize(nickValue));
              hashSet.add(nickValue);
              if (!nickPlayer.getName().equalsIgnoreCase(nickValue)) {
                hashSet.add(nickPlayer.getName());
              }
            }
          }
        }
        break;
      case homes:
        if (commandSender instanceof Player) {
          CMIUser homesUser = this.plugin.getPlayerManager().getUser((Player) commandSender);
          if (homesUser != null && !homesUser.getHomes().isEmpty()) {
            hashSet.addAll(homesUser.getHomesList());
          }
        }
        break;
      case warps:
        if (commandSender instanceof Player) {
          for (CmiWarp cmiWarp : this.plugin.getWarpManager().getWarps((Player) commandSender)) {
            if (!cmiWarp.isHidden()
                || PermissionsManager.CMIPerm.command_$1.hasPermission(
                    commandSender, editwarp.class.getSimpleName())) {
              hashSet.add(cmiWarp.getName());
            }
          }
        }
        break;
      case allwarps:
        for (var warpEntry : this.plugin.getWarpManager().getWarps().entrySet()) {
          hashSet.add(((CmiWarp) warpEntry.getValue()).getName());
        }
        break;
      case playerwarps:
        if (commandSender instanceof Player) {
          Player warpSender = (Player) commandSender;
          boolean canSeeOthersWarps =
              PermissionsManager.CMIPerm.command_$1_others.hasPermission(
                  commandSender, editwarp.class.getSimpleName());

          for (var warpPlayerEntry : this.plugin.getWarpManager().getWarps().entrySet()) {
            if (((CmiWarp) warpPlayerEntry.getValue()).getCreator() == null
                || ((CmiWarp) warpPlayerEntry.getValue())
                    .getCreator()
                    .equals(warpSender.getUniqueId())
                || canSeeOthersWarps) {
              hashSet.add(((CmiWarp) warpPlayerEntry.getValue()).getName());
            }
          }
        }
        break;
      case rankname:
        for (var rankEntry : this.plugin.getRankManager().getRanks().entrySet()) {
          hashSet.add(((CMIRank) rankEntry.getValue()).getName());
        }
        break;
      case statstype:
        for (StatsManager.CMIStatistic cmiStatistic : StatsManager.CMIStatistic.values()) {
          if (cmiStatistic.getBukkitStat() != null) {
            hashSet.add(cmiStatistic.name().toLowerCase());
          }
        }
        break;
      case statssubtype:
        try {
          StatsManager.CMIStatistic statistic =
              this.plugin.getStatsManager().getStatisticByName(args[argIndex - 1]);
          if (statistic != null) {
            switch (statistic.getSubType()) {
              case None:
              default:
                return new ArrayList<>(hashSet);
              case Material:
                for (Material material : Material.values()) {
                  hashSet.add(material.name().toLowerCase());
                  if (material.toString().startsWith("LEGACY_")) {}
                }
                break statSwitch;
              case Block:
                for (Material solidBlock : Material.values()) {
                  if (solidBlock.isBlock()
                      && solidBlock.isSolid()
                      && !solidBlock.toString().startsWith("LEGACY_")) {
                    hashSet.add(solidBlock.name().toLowerCase());
                  }
                }
                break statSwitch;
              case Entity:
                for (EntityType aliveEntityType : EntityType.values()) {
                  if (aliveEntityType.isAlive()) {
                    hashSet.add(aliveEntityType.name().toLowerCase());
                  }
                }
            }
          }
        } catch (Exception exception) {
        }
        break;
      case motd:
        hashSet.add(
            CMIChatColor.deColorize(
                Bukkit.getServer().getMotd().replace("\n", "\\n").replace("  ", "_ ")));
        break;
      case bungeeserver:
        for (var bungeeEntry : this.plugin.getBungeeCordManager().getServers().entrySet()) {
          hashSet.add((String) bungeeEntry.getKey());
        }
        break;
      case scheduleName:
        for (var scheduleEntry : this.plugin.getSchedulerManager().getMap().entrySet()) {
          hashSet.add((String) scheduleEntry.getKey());
        }
        break;
      case ctext:
        for (var ctextEntry : this.plugin.getCTextManager().getAll().entrySet()) {
          if (!((CText) ctextEntry.getValue()).isRequirePermission()
              || PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
                  commandSender, 10000L, ((CText) ctextEntry.getValue()).getName())) {
            hashSet.add(((CText) ctextEntry.getValue()).getName());
            if (hashSet.size() > shortValue) {
              break;
            }
          }
        }
        break;
      case attachedCommand:
        if (commandSender instanceof Player) {
          Player attachedSender = (Player) commandSender;
          ItemStack attachedItem = CMIItemStack.getItemInMainHand(attachedSender);
          if (attachedItem != null) {
            var nbtCommands =
                new CMINBT(attachedItem).getList(CustomNBTManager.CustomNBTType.NBTCommands.name());
            if (nbtCommands != null) {
              StringBuilder commandsBuilder = new StringBuilder();

              for (String encodedCommand : nbtCommands) {
                if (!commandsBuilder.toString().isEmpty()) {
                  commandsBuilder.append(";;");
                }

                if (CMIEncoder.isEncoded(encodedCommand)) {
                  commandsBuilder.append(CMIEncoder.decode(encodedCommand));
                } else {
                  commandsBuilder.append(encodedCommand);
                }
              }

              if (!commandsBuilder.toString().isEmpty()) {
                hashSet.add(commandsBuilder.toString());
              }
            }
          }
        }
        break;
      case jail:
        for (var jailEntry : this.plugin.getJailManager().getJails().entrySet()) {
          hashSet.add(((CMIJail) jailEntry.getValue()).getName());
        }
        break;
      case cellId:
        if (args.length >= 2) {
          CMIJail cmiJail = this.plugin.getJailManager().getByName(args[argIndex - 1]);
          if (cmiJail != null) {
            for (var cellEntry : cmiJail.getCells().entrySet()) {
              hashSet.add(String.valueOf(cellEntry.getKey()));
            }
          }
        }
        break;
      case sound:
        hashSet.addAll(CMISound.getSoundNames());
        break;
      case customalias:
        for (String aliasKey : this.plugin.getAliasManager().getCustom().keySet()) {
          hashSet.add(aliasKey.replace(" ", "__"));
        }
        break;
      case dbusercollumsshort:
        for (DBDAO.UserTablesFields userTablesFields : DBDAO.UserTablesFields.values()) {
          switch (userTablesFields) {
            default:
              hashSet.add(userTablesFields.name());
              break;
            case player_uuid:
            case username:
          }
        }
        break;
      case placeholders:
        for (Placeholder.CMIPlaceHolders cmiPlaceHolders : Placeholder.CMIPlaceHolders.values()) {
          hashSet.add(cmiPlaceHolders.getFull());
        }
        break;
      case warncategory:
        for (var warnEntry : this.plugin.getWarningManager().getCategories().entrySet()) {
          hashSet.add(((CMIWarningCategory) warnEntry.getValue()).getName());
        }
        break;
      case projectiletype:
        hashSet.add("smallfireball");
        hashSet.add("largefireball");
        hashSet.add("thrownexpbottle");
        hashSet.add("snowball");
        hashSet.add("egg");
        hashSet.add("tippedarrow");
        hashSet.add("spectralarrow");
        hashSet.add("firework");
        hashSet.add("splashpotion");
        hashSet.add("dragonfireball");
        hashSet.add("witherskull");
        hashSet.add("arrow");
        hashSet.add("shulkerbullet");
        hashSet.add("llamaspit");
        hashSet.add("trident");
        break;
      case holograms:
        for (var hologramEntry : this.plugin.getHologramManager().getHolograms().entrySet()) {
          hashSet.add(((CMIHologram) hologramEntry.getValue()).getName());
          if (hashSet.size() > shortValue) {
            break;
          }
        }
        break;
      case mobtype:
        for (EntityType mobEntityType : EntityType.values()) {
          if (mobEntityType.isSpawnable()
              && mobEntityType.isAlive()
              && CMIEntityType.ARMOR_STAND.getType() != mobEntityType) {
            hashSet.add(mobEntityType.name().toLowerCase());
          }
        }
        break;
      case usermeta:
        if (args.length >= 3) {
          CMIUser metaUser = CMIUser.getUser(args[argIndex - 2]);
          if (metaUser == null) {
            metaUser = CMIUser.getUser(args[argIndex - 1]);
          }

          if (metaUser != null && metaUser.hasMeta()) {
            for (String metaKey : metaUser.getMeta().getMap().keySet()) {
              hashSet.add(metaKey);
            }
          }
        }
        break;
      case vanishaction:
        for (VanishAction vanishAction : VanishAction.values()) {
          hashSet.add(vanishAction.toString().toLowerCase());
        }
        break;
      case signLine:
        Integer integer = null;

        try {
          integer = Integer.parseInt(args[argIndex - 1]);
        } catch (Throwable throwable) {
        }

        if (commandSender instanceof Player && (integer == null || integer < 5 && integer > 0)) {
          Player signSender = (Player) commandSender;
          Block block = CMI.getInstance().getUtilManager().getTargetBlock(signSender, 50);
          if (block != null && block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
              Util.SignSide signSide = Util.getSignSide(block, signSender.getLocation());
              if (integer == null) {
                StringBuilder allLinesBuilder = new StringBuilder();

                for (int lineIndex = 0; lineIndex < 4; lineIndex++) {
                  if (signSide.equals(Util.SignSide.FRONT)) {
                    allLinesBuilder.append(sign.getSide(Side.FRONT).getLine(lineIndex));
                  } else {
                    allLinesBuilder.append(sign.getSide(Side.BACK).getLine(lineIndex));
                  }

                  if (lineIndex != 3) {
                    allLinesBuilder.append("\\n");
                  }
                }

                hashSet.add(CMIChatColor.deColorize(allLinesBuilder.toString()));
              } else {
                hashSet.add(
                    CMIChatColor.deColorize(
                        sign.getSide(signSide.equals(Util.SignSide.FRONT) ? Side.FRONT : Side.BACK)
                            .getLine(integer - 1)));
                hashSet.add(CMIChatColor.deColorize(sign.getLine(integer - 1)));
              }
            } else if (integer == null) {
              StringBuilder signTextBuilder = new StringBuilder();

              for (int signLineIndex = 0; signLineIndex < 4; signLineIndex++) {
                signTextBuilder.append(sign.getLine(signLineIndex));
                if (signLineIndex != 3) {
                  signTextBuilder.append("\\n");
                }
              }

              hashSet.add(CMIChatColor.deColorize(signTextBuilder.toString()));
            } else {
              hashSet.add(CMIChatColor.deColorize(sign.getLine(integer - 1)));
            }
          }
        }
        break;
      case saveditems:
        this.plugin
            .getSavedItemManager()
            .getSavedItemMap()
            .forEach(
                (savedItemKey, linkedHashMap) ->
                    linkedHashMap.forEach(
                        (savedItemName, savedItem) -> hashSet.add(savedItem.getName())));
        break;
      case dialogname:
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R4) && Version.isPaperBranch()) {
          hashSet.addAll(CMIDialog.getDialogNames(commandSender));
        }
        break;
      case trimmaterial:
        for (CMITrimMaterial cmiTrimMaterial : CMITrimMaterial.values()) {
          hashSet.add(CMIText.firstToUpperCase(cmiTrimMaterial.toString()));
        }
        break;
      case trimpattern:
        for (CMITrimPattern cmiTrimPattern : CMITrimPattern.values()) {
          hashSet.add(CMIText.firstToUpperCase(cmiTrimPattern.toString()));
        }
        break;
      case icnames:
        for (CMIInteractiveCommand cmiInteractiveCommand :
            this.plugin.getInteractiveCommandManager().getFullList()) {
          hashSet.add(CMIText.firstToUpperCase(cmiInteractiveCommand.getName()));
        }
    }

    return new ArrayList<>(hashSet);
  }

  private static List<String> getColorNames() {
    ArrayList<String> colorNames = new ArrayList();
    for (ChatColor chatColor : ChatColor.values()) {
      if (chatColor != null
          && chatColor.name() != null
          && chatColor != ChatColor.ITALIC
          && chatColor != ChatColor.BOLD
          && chatColor != ChatColor.MAGIC
          && chatColor != ChatColor.STRIKETHROUGH
          && chatColor != ChatColor.UNDERLINE
          && chatColor != ChatColor.RESET) {
        colorNames.add(chatColor.name().toLowerCase());
      }
    }

    return colorNames;
  }

  public enum TabAction {
    na,
    allPlayername("All players including vanished ones"),
    allIGNPlayername("All IGN players including vanished ones"),
    playername("All players excluding vanished ones"),
    mutedplayername("All online muted players"),
    damageCause("Damage cause variations"),
    bannedplayername("Banned player names"),
    playerOption("Player options"),
    gamemode("Game modes"),
    worlds("Worlds"),
    portals("List of portal names. Exclude disabled"),
    allportals("List of portal names"),
    itemname("Materials"),
    playeritems("Materials in player inventory"),
    blockname("Blocks"),
    EntityType("Entity Types"),
    cleanEntityType("Entity Types without _"),
    kit("Kit names by access"),
    kitnames("Kit config names by access"),
    kitp("Kits by preview access"),
    chatroom("Chat rooms"),
    biome("Biomes"),
    treeType("Tree types"),
    maxplayers("Server max player limit"),
    potioneffect("Potion effects"),
    effect("Particle effects"),
    merchants("Villager professions"),
    enchant("Enchant names"),
    limitedEnchant("Enchant names by permission"),
    halfViewRange("Half of max server view range"),
    doubleViewRange("Double of max server view range"),
    ViewRange("Server view range"),
    maxenchantlevel("Max enchant level. Uses previous variable to determine enchantment"),
    currentItemName("Item name in main hand"),
    loreLine("Lists numbers of lore lines of item in main hand"),
    currentItemLore("Lists lore of item in main hand"),
    currentLocation("Current player location"),
    currentFullLocation("Current player location with pitch and yaw"),
    currentX("Current player X position"),
    currentY("Current player Y position"),
    currentZ("Current player Z position"),
    currentWorld("World name player is in"),
    currentPitch("Players pitch"),
    currentYaw("Players yaw"),
    itemFlag("Item flag values"),
    nickName("Users display name excluding color codes"),
    fullNickName("Users display name"),
    gamerule("List of world game rules"),
    gamerulevalue("Game rule value"),
    nickNames("All online users nick names"),
    homes("Users home list"),
    warps("Warps by access to them"),
    allwarps("All warps"),
    playerwarps("Warps by access to them"),
    rankname("Rank names"),
    statstype("Statistics names"),
    statssubtype("Sub statistics names. Uses previous variable to determine main statistic"),
    motd("Servers motd"),
    bungeeserver("Bungee servers"),
    scheduleName("Schedule names"),
    ctext("Custom text names"),
    attachedCommand("Includes attached command"),
    jail("Jail names"),
    cellId("Cewll id's. Uses previous variable to determine jail"),
    sound("Sound names"),
    customalias("Custom alias list"),
    dbusercollumsshort,
    placeholders("Placeholders"),
    warncategory("Warn categories"),
    projectiletype("Projectile types"),
    holograms("Hologram names"),
    mobtype("Mob types"),
    usermeta("User meta key values"),
    vanishaction("Vanish actions"),
    signLine("Sign line text. Uses previous variable to determine line number"),
    saveditems("List of saved item names"),
    dialogname("Dialog names"),
    trimmaterial("Trim material names"),
    trimpattern("Trim pattern names"),
    icnames("Interactable commands names");

    private String desc;

    TabAction() {}

    TabAction(String string) {
      this.desc = string;
    }

    public static TabComplete.TabAction getAction(String string) {
      if (string.startsWith("_")) {
        return na;
      }

      TabComplete.TabAction tabAction = TabComplete.map.get(string.toLowerCase());
      return tabAction == null ? na : tabAction;
    }

    public String getDesc() {
      return this.desc;
    }
  }
}
