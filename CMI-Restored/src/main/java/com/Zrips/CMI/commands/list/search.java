package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Search.CMIDataResultItem;
import com.Zrips.CMI.Modules.Search.CMIDataResultPlayerBase;
import com.Zrips.CMI.Modules.Search.CMIPlayerDataSearch;
import com.Zrips.CMI.Modules.Search.CMIPlayerSearchPlaceType;
import com.Zrips.CMI.Modules.Search.ItemSearchCriteria;
import com.Zrips.CMI.Modules.Search.SearchTypeFlyMode;
import com.Zrips.CMI.Modules.Search.SearchTypeGameMode;
import com.Zrips.CMI.Modules.Search.SearchTypeGodMode;
import com.Zrips.CMI.Modules.Search.SearchTypeItemAmount;
import com.Zrips.CMI.Modules.Search.SearchTypeItemExact;
import com.Zrips.CMI.Modules.Search.SearchTypeItemStack;
import com.Zrips.CMI.Modules.Search.SearchTypeMaxHealth;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemSerializer;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class search implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("baseList", "{gcs}[number]{gcp}. [location] {gcs}[playerName][amount]");
    configReader.get("regularList", "{gcs}[number]{gcp}. {gcs}[playerName][amount]");
    configReader.get("amount", " {gcp}({gcs}[totalcount]{gcp})");
    configReader.get("found", "{gcp}Found {gcs}[amount] {gcp}from {gcs}[files] {gcp}checked users");
    configReader.get("noresults", "{gcp}Nothing found by your criteria");
    configReader.get("nothing", "{gcw}Provided criteria for search are incorrect");
    configReader.get("noactive", "{gcp}No active or existing search results");
    configReader.get("active", "{gcp}Search is already active, wait until its done or cancel it");
    configReader.get("searching", "{gcp}Searching, this can take some time to finish");
    configReader.get(
        "progress",
        "{gcp}Found {gcs}[totalFound] {gcp}out of {gcs}[checked] {gcp}/ {gcs}[from] {gcp}Speed:"
            + " {gcs}[speed]");
    configReader.get("location.ender", "{gcs}Ender");
    configReader.get("location.enderS", "{gcs}Ender->ShulkerBox");
    configReader.get("location.enderB", "{gcs}Ender->Bundle");
    configReader.get("location.inv", "{gcp}Inventory");
    configReader.get("location.invS", "{gcp}Inventory->ShulkerBox");
    configReader.get("location.invB", "{gcp}Inventory->Bundle");
    configReader.get("location.PlayerVault", "{gcp}PlayerVault");
  }

  @CAnnotation(
      info = "Search from all players of specific items or stats",
      explanation =
          "<T>More information at"
              + " https://www.zrips.net/cmi/extra/search/</T><URL>https://www.zrips.net/cmi/extra/search/</URL>",
      regVar = -100,
      consoleVar = -100,
      alias = true,
      multiTab = {
        "cancel",
        "results",
        "survival,creative,adventure,spectator,fly,maxhp:21,god",
        "hand,stoneaxe oversize"
      })
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    search.actions action = null;
    int value = 1;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String string = argsCopy[argIndex];
      if (action == null) {
        action = search.actions.getByName(string);
        if (action != null) {
          continue;
        }
      }

      try {
        value = Integer.parseInt(string);
      } catch (NumberFormatException numberFormatException) {
      }
    }

    if (action == null) {
      action = search.actions.search;
    }

    if (value < 1) {
      value = 1;
    }

    switch (action) {
      case cancel:
        CMIPlayerDataSearch cmiPlayerDataSearch = CMIPlayerDataSearch.getActiveSearch();
        if (cmiPlayerDataSearch != null) {
          cmiPlayerDataSearch.cancel();
          cmi.getCommandManager().performCMICommand(commandSender, this.getClass(), "results");
        }

        return true;
      case results:
        CMIPlayerDataSearch activeSearch = CMIPlayerDataSearch.getActiveSearch();
        if (activeSearch == null) {
          CMILC.info(this, commandSender, "noactive");
          return false;
        }

        if (activeSearch.getTotalFound() == 0) {
          CMILC.info(this, commandSender, "noresults");
          return true;
        }

        var concurrentMap = activeSearch.getSearchResults();
        value = CMINumber.clamp(value, 1, activeSearch.getTotalFound() / 10 + 1);
        PageInfo pageInfo =
            new PageInfo(
                commandSender instanceof Player ? 10 : 50, activeSearch.getTotalFound(), value);

        for (Entry entry : concurrentMap.entrySet()) {
          if (pageInfo.isBreak()) {
            break;
          }

          CMIUser cmiUser = CMIUser.getUser((UUID) entry.getKey());

          for (Entry resultEntry :
              ((Iterable<Entry>) ((ConcurrentHashMap) entry.getValue()).entrySet())) {
            if (pageInfo.isBreak()) {
              break;
            }

            if (!pageInfo.isContinue() && cmiUser != null) {
              if (resultEntry.getValue() instanceof CMIDataResultItem) {
                CMIPlayerSearchPlaceType cmiPlayerSearchPlaceType =
                    (CMIPlayerSearchPlaceType) resultEntry.getKey();
                String header = "";
                String footer = "";
                if (cmiPlayerSearchPlaceType == CMIPlayerSearchPlaceType.PlayerVaults
                    && resultEntry.getValue() instanceof CMIDataResultItem) {
                  footer = ((CMIDataResultItem) resultEntry.getValue()).getSubIdentifier();
                  header = " (" + footer + ")";
                }

                String loadingText =
                    CMILC.getIM(
                        this,
                        "amount",
                        "[totalcount]",
                        ((CMIDataResultPlayerBase) resultEntry.getValue()).getValueOutput());
                String titleText =
                    CMILC.getIM(
                        this,
                        "baseList",
                        "[number]",
                        pageInfo.getPositionForOutput(),
                        "[playerName]",
                        cmiUser.getName(),
                        "[amount]",
                        loadingText,
                        "[location]",
                        cmiPlayerSearchPlaceType.getPlace().isEmpty()
                            ? "&e->"
                            : CMILC.getIM(this, "location." + cmiPlayerSearchPlaceType.getPlace())
                                + header);
                String commandLine = "/" + cmiPlayerSearchPlaceType.getCmd();
                String helpText =
                    commandLine.replace("[player]", cmiUser.getName()).replace("[nr]", footer);
                RawMessage rawMessage = new RawMessage();
                rawMessage
                    .addText(titleText)
                    .addHover(LC.info_Click.getLocale(new Object[0]))
                    .addCommand(helpText);
                rawMessage.show(commandSender);
              } else {
                String subText =
                    ((CMIDataResultPlayerBase) resultEntry.getValue()).getValueOutput().isEmpty()
                        ? ""
                        : CMILC.getIM(
                            this,
                            "amount",
                            "[totalcount]",
                            ((CMIDataResultPlayerBase) resultEntry.getValue()).getValueOutput());
                RawMessage message = new RawMessage();
                message.addText(
                    CMILC.getIM(
                        this,
                        "regularList",
                        "[number]",
                        pageInfo.getPositionForOutput(),
                        "[playerName]",
                        cmiUser.getName(),
                        "[amount]",
                        subText));
                message.addHover(LC.info_Click.getLocale(new Object[0]));
                message.addCommand(CommandsHandler.getLabel() + " info " + cmiUser.getName());
                message.show(commandSender);
              }
            }
          }
        }

        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + this.getClass().getSimpleName() + " results");
        break;
      case search:
        if (CMIPlayerDataSearch.getActiveSearch() != null
            && CMIPlayerDataSearch.getActiveSearch().isRunning()) {
          CMILC.info(search.class, commandSender, "active");
          return null;
        }

        CMIPlayerDataSearch search =
            new CMIPlayerDataSearch(commandSender) {
              @Override
              public void onFinish() {
                if (this.getInitiator() instanceof Player) {
                  CMIScheduler.runTask(
                      CMI.getInstance(),
                      () ->
                          CMI.getInstance()
                              .getCommandManager()
                              .performCMICommand(
                                  Bukkit.getPlayer(((Player) this.getInitiator()).getUniqueId()),
                                  search.class,
                                  "results"));
                } else {
                  CMIScheduler.runTask(
                      CMI.getInstance(),
                      () ->
                          CMI.getInstance()
                              .getCommandManager()
                              .performCMICommand(this.getInitiator(), search.class, "results"));
                }
              }

              @Override
              public void onCycle(int progress) {
                if (this.getInitiator() instanceof Player) {
                  CMIScheduler.runTaskAsynchronously(
                      CMI.getInstance(),
                      () ->
                          CMIActionBar.send(
                              Bukkit.getPlayer(((Player) this.getInitiator()).getUniqueId()),
                              CMILC.getIM(
                                  search.class,
                                  "progress",
                                  "[totalFound]",
                                  this.getTotalFound(),
                                  "[checked]",
                                  this.getTotalChecked(),
                                  "[from]",
                                  this.getTotalToCheck(),
                                  "[speed]",
                                  (int) this.searchSpeed)));
                } else if (this.lastInformed + 900L < System.currentTimeMillis()) {
                  this.lastInformed = System.currentTimeMillis();
                  CMIScheduler.runTaskAsynchronously(
                      CMI.getInstance(),
                      () ->
                          CMIActionBar.send(
                              this.getInitiator(),
                              CMIChatColor.stripColor(
                                  CMILC.getIM(
                                      search.class,
                                      "progress",
                                      "[totalFound]",
                                      this.getTotalFound(),
                                      "[checked]",
                                      this.getTotalChecked(),
                                      "[from]",
                                      this.getTotalToCheck(),
                                      "[speed]",
                                      (int) this.searchSpeed))));
                }
              }
            };
        LinkedHashSet<ItemSearchCriteria> linkedHashSet = new LinkedHashSet();
        LinkedHashSet<ItemSearchCriteria> criteria = new LinkedHashSet();
        HashSet<ItemStack> hashSet = new HashSet();
        String[] argsLower = strings;
        int totalArgs = strings.length;

        for (int argCursor = 0; argCursor < totalArgs; argCursor++) {
          label213:
          {
            String arg = argsLower[argCursor];
            String argLower = arg.toLowerCase();
            String argValue = argLower;
            switch (argLower.hashCode()) {
              case -1684593425:
                if (argValue.equals("spectator")) {
                  search.addSearchingFor(new SearchTypeGameMode(GameMode.SPECTATOR));
                  continue;
                }
                break;
              case -1600582850:
                if (argValue.equals("survival")) {
                  search.addSearchingFor(new SearchTypeGameMode(GameMode.SURVIVAL));
                  continue;
                }
                break;
              case -694094064:
                if (argValue.equals("adventure")) {
                  search.addSearchingFor(new SearchTypeGameMode(GameMode.ADVENTURE));
                  continue;
                }
                break;
              case 104:
                if (argValue.equals("h")) {
                  break label213;
                }
                break;
              case 101491:
                if (argValue.equals("fly")) {
                  search.addSearchingFor(new SearchTypeFlyMode(true));
                  continue;
                }
                break;
              case 102524:
                if (argValue.equals("god")) {
                  search.addSearchingFor(new SearchTypeGodMode(true));
                  continue;
                }
                break;
              case 3194991:
                if (argValue.equals("hand")) {
                  break label213;
                }
                break;
              case 107032747:
                if (argValue.equals("purge")) {
                  if (!cmi.getConfigManager().SearchPurge) {
                    CMILC.info_PurgeNotEnabled.sendMessage(commandSender);
                  } else {
                    search.setPurge(true);
                  }
                  continue;
                }
                break;
              case 530027221:
                if (argValue.equals("oversize")) {
                  search.addSearchingFor(new SearchTypeItemAmount(-1));
                  continue;
                }
                break;
              case 1820422063:
                if (argValue.equals("creative")) {
                  search.addSearchingFor(new SearchTypeGameMode(GameMode.CREATIVE));
                  continue;
                }
            }

            if (argLower.startsWith("maxhp")) {
              int radius = 20;
              if (argLower.contains(":")) {
                try {
                  radius = Integer.parseInt(argLower.split(":")[1]);
                } catch (NumberFormatException numberFormatException2) {
                }
              }

              search.addSearchingFor(new SearchTypeMaxHealth(radius));
            } else if (ItemSearchCriteria.getByName(arg) != null) {
              linkedHashSet.add(ItemSearchCriteria.getByName(arg));
            } else {
              argValue = arg;
              CMIItemStack cmiItemStack = CMIItemSerializer.deserialize(argValue);
              if (cmiItemStack == null && argValue.endsWith("}") && argValue.contains("{")) {
                cmiItemStack = CMIItemSerializer.deserialize("stone;" + argValue);
              } else if (cmiItemStack != null) {
                criteria.add(ItemSearchCriteria.MATERIAL);
              }

              if (cmiItemStack != null) {
                ItemStack itemStack = cmiItemStack.getItemStack();
                if (itemStack != null) {
                  try {
                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants()) {
                      criteria.add(ItemSearchCriteria.ENCHANT);
                    }
                  } catch (Throwable throwable) {
                    throwable.printStackTrace();
                  }

                  criteria.addAll(ItemSearchCriteria.getFromString(argValue));
                  hashSet.add(itemStack);
                  if (itemStack.getAmount() > 1) {
                    criteria.add(ItemSearchCriteria.AMOUNT);
                  }
                }
              }
            }
            continue;
          }

          if (commandSender instanceof Player) {
            ItemStack heldItem = CMIItemStack.getItemInMainHand((Player) commandSender);
            if (heldItem != null && !heldItem.getType().equals(Material.AIR)) {
              criteria.add(ItemSearchCriteria.MATERIAL);
              criteria.addAll(
                  ItemSearchCriteria.getFromString(CMIItemSerializer.serialize(heldItem)));
              hashSet.add(heldItem);
            }
          }
        }

        if (linkedHashSet.isEmpty() && !criteria.isEmpty()) {
          linkedHashSet.addAll(criteria);
        }

        if (linkedHashSet.isEmpty() && criteria.isEmpty()) {
          linkedHashSet.add(ItemSearchCriteria.MATERIAL);
        }

        for (ItemStack item : hashSet) {
          if (item != null) {
            if (linkedHashSet.isEmpty()) {
              search.addSearchingFor(new SearchTypeItemExact(item));
            } else {
              search.addSearchingFor(new SearchTypeItemStack(item, linkedHashSet));
            }
          }
        }

        search.search();
    }

    return true;
  }

  enum actions {
    cancel,
    results,
    search;

    public static search.actions getByName(String string) {
      search.actions[] actionses;
      int value = (actionses = values()).length;

      for (int index = 0; index < value; index++) {
        search.actions action = actionses[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
