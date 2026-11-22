package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Scan.CMIWorldDataSearch;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationBase;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationBlock;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationEntity;
import com.Zrips.CMI.Modules.Search.ItemSearchCriteria;
import com.Zrips.CMI.Modules.Search.SearchTypeItemAmount;
import com.Zrips.CMI.Modules.Search.SearchTypeItemExact;
import com.Zrips.CMI.Modules.Search.SearchTypeItemStack;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemSerializer;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class scan implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "ListEntity", "{gcs}[number]{gcp}. [location] {gcs}[checked][type] {gcs}[amount]");
    configReader.get(
        "ListBlock", "{gcs}[number]{gcp}. [location] {gcp}[checked][type] {gcs}[amount]");
    configReader.get("pause", "{gcp}Scanning paused, to continue use {gcs}/cmi scan resume");
    configReader.get("resume", "{gcp}Scanning resumed");
    configReader.get("noInformation", "{gcp}There is no information");
    configReader.get("nothing", "{gcw}Provided criteria for scan are incorrect");
    configReader.get(
        "progress",
        "{gcp}Found: {gcs}[totalFound] {gcp}Chunks to check: {gcs}[checked]{gcp}/{gcs}[from]"
            + " {gcp}Skipped: {gcs}[skipped] {gcp}Speed: {gcs}[speed]");
    configReader.get("loading", "{gcn}Loading data...");
    configReader.get("teleport", "{gcp}Press to teleport");
  }

  @CAnnotation(
      priority = 286,
      info = "Scans for items in current world",
      args = "",
      multiTab = {"cancel", "results", "pause", "resume", "hand,stoneaxe oversize"},
      explanation =
          "<T>More information at"
              + " https://www.zrips.net/cmi/extra/scan/</T><URL>https://www.zrips.net/cmi/extra/scan/</URL>",
      regVar = -66,
      consoleVar = -66,
      alias = true,
      others = false)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    scan.actions action = null;
    int value = 1;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String string = argsCopy[argIndex];
      if (action == null) {
        action = scan.actions.getByName(string);
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
      action = scan.actions.search;
    }

    if (value < 1) {
      value = 1;
    }

    switch (action) {
      case cancel:
      case stop:
        CMIWorldDataSearch cmiWorldDataSearch = CMIWorldDataSearch.getActiveSearch();
        if (cmiWorldDataSearch == null) {
          CMILC.info(search.class, commandSender, "noactive");
          return false;
        }

        cmiWorldDataSearch.cancel();
        return true;
      case results:
        final CMIWorldDataSearch activeSearch = CMIWorldDataSearch.getActiveSearch();
        if (activeSearch == null) {
          CMILC.info(search.class, commandSender, "noactive");
          return false;
        }

        if (activeSearch.getTotalFound() == 0) {
          CMILC.info(search.class, commandSender, "noresults");
          return true;
        }

        var map = activeSearch.getSearchResults();
        value = CMINumber.clamp(value, 1, map.size() / 10 + 1);
        final PageInfo pageInfo =
            new PageInfo(commandSender instanceof Player ? 10 : 50, map.size(), value);

        for (final Entry entry : map.entrySet()) {
          if (pageInfo.isBreak()) {
            break;
          }

          if (!pageInfo.isContinue()) {
            if (entry.getValue() instanceof CMIDataResultLocationBlock) {
              final CMIDataResultLocationBlock cmiDataResultLocationBlock =
                  (CMIDataResultLocationBlock) entry.getValue();
              String label =
                  CMILC.getIM(
                      this,
                      "ListBlock",
                      "[number]",
                      pageInfo.getPositionForOutput(),
                      "[type]",
                      cmiDataResultLocationBlock.getType().getTranslatedName(),
                      "[amount]",
                      cmiDataResultLocationBlock.getAmount(),
                      "[checked]",
                      cmiDataResultLocationBlock.isChecked() ? "&n" : "",
                      "[location]",
                      cmiDataResultLocationBlock.getVector().getX()
                          + ";"
                          + cmiDataResultLocationBlock.getVector().getY()
                          + ";"
                          + cmiDataResultLocationBlock.getVector().getZ());
              RawMessageCommand rawMessageCommand =
                  new RawMessageCommand() {
                    public void run(CommandSender sender) {
                      cmiDataResultLocationBlock.setChecked(true);
                      cmi.getCommandManager()
                          .performCMICommand(
                              sender,
                              scan.class,
                              "results",
                              String.valueOf(pageInfo.getCurrentPage()));
                      if (sender instanceof Player) {
                        cmi.getTeleportHandler()
                            .teleportPlayer(
                                (Player) sender,
                                scan.lookAt(
                                    (Player) sender,
                                    ((CMIDataResultLocationBase) entry.getValue())
                                        .getLocation(activeSearch.getWorld())
                                        .add(0.5, 0.0, 0.5)),
                                true,
                                CMITeleportType.TpPos);
                      }
                    }
                  };
              RawMessage rawMessage = new RawMessage();
              rawMessage
                  .addText(label)
                  .addHover(CMILC.getIM(this, "teleport"))
                  .addCommand(rawMessageCommand.getCommand());
              rawMessage.show(commandSender);
            } else if (entry.getValue() instanceof CMIDataResultLocationEntity) {
              final CMIDataResultLocationEntity cmiDataResultLocationEntity =
                  (CMIDataResultLocationEntity) entry.getValue();
              String title =
                  CMILC.getIM(
                      this,
                      "ListEntity",
                      "[number]",
                      pageInfo.getPositionForOutput(),
                      "[type]",
                      cmiDataResultLocationEntity.getType().getTranslatedName(),
                      "[amount]",
                      cmiDataResultLocationEntity.getAmount(),
                      "[checked]",
                      cmiDataResultLocationEntity.isChecked() ? "&n" : "",
                      "[location]",
                      cmiDataResultLocationEntity.getVector().getX()
                          + ";"
                          + cmiDataResultLocationEntity.getVector().getY()
                          + ";"
                          + cmiDataResultLocationEntity.getVector().getZ());
              RawMessageCommand callback =
                  new RawMessageCommand() {
                    public void run(CommandSender sender) {
                      cmiDataResultLocationEntity.setChecked(true);
                      cmi.getCommandManager()
                          .performCMICommand(
                              sender,
                              scan.class,
                              "results",
                              String.valueOf(pageInfo.getCurrentPage()));
                      if (sender instanceof Player) {
                        cmi.getTeleportHandler()
                            .teleportPlayer(
                                (Player) sender,
                                scan.lookAt(
                                    (Player) sender,
                                    ((CMIDataResultLocationBase) entry.getValue())
                                        .getLocation(activeSearch.getWorld())
                                        .add(0.5, 0.0, 0.5)),
                                true,
                                CMITeleportType.TpPos);
                      }
                    }
                  };
              RawMessage message = new RawMessage();
              message
                  .addText(title)
                  .addHover(CMILC.getIM(this, "teleport"))
                  .addCommand(callback.getCommand());
              message.show(commandSender);
            }
          }
        }

        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + this.getClass().getSimpleName() + " results");
        break;
      case search:
        if (CMIWorldDataSearch.getActiveSearch() != null
            && CMIWorldDataSearch.getActiveSearch().isRunning()) {
          CMILC.info(search.class, commandSender, "active");
          return null;
        }

        CMIWorldDataSearch search =
            new CMIWorldDataSearch(commandSender) {
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
                                  scan.class,
                                  "results"));
                } else {
                  CMIScheduler.runTask(
                      CMI.getInstance(),
                      () ->
                          CMI.getInstance()
                              .getCommandManager()
                              .performCMICommand(this.getInitiator(), scan.class, "results"));
                }
              }

              @Override
              public void onDataLoadStart() {
                String loadingText = CMILC.getIM(scan.class, "loading");
                if (this.getInitiator() instanceof Player) {
                  CMIScheduler.runTaskAsynchronously(
                      CMI.getInstance(), () -> CMIActionBar.send(this.getInitiator(), loadingText));
                } else if (this.lastInformed + 900L < System.currentTimeMillis()) {
                  this.lastInformed = System.currentTimeMillis();
                  CMIScheduler.runTaskAsynchronously(
                      CMI.getInstance(),
                      () -> CMIMessages.sendMessage(this.getInitiator(), loadingText));
                }
              }

              @Override
              public void onCycle(int progress) {
                String statusText =
                    CMILC.getIM(
                        scan.class,
                        "progress",
                        "[totalFound]",
                        this.getTotalFound(),
                        "[checked]",
                        this.getTotalChecked() - 1,
                        "[from]",
                        this.getTotalToCheck(),
                        "[skipped]",
                        this.getSkippedChunks(),
                        "[speed]",
                        (int) this.searchSpeed);
                if (this.getInitiator() instanceof Player) {
                  if (((Player) this.getInitiator()).isOnline()) {
                    CMIScheduler.runTaskAsynchronously(
                        CMI.getInstance(),
                        () ->
                            CMIActionBar.send(
                                Bukkit.getPlayer(((Player) this.getInitiator()).getUniqueId()),
                                statusText));
                  }
                } else if (this.lastInformed + 900L < System.currentTimeMillis()) {
                  this.lastInformed = System.currentTimeMillis();
                  CMIScheduler.runTaskAsynchronously(
                      CMI.getInstance(),
                      () -> CMIMessages.sendMessage(this.getInitiator(), statusText));
                }
              }

              @Override
              public void onPauseChange() {
                if (this.isPaused()) {
                  CMILC.info(scan.class, commandSender, "pause");
                } else {
                  CMILC.info(scan.class, commandSender, "resume");
                }
              }
            };
        LinkedHashSet<ItemSearchCriteria> linkedHashSet = new LinkedHashSet();
        LinkedHashSet<ItemSearchCriteria> criteria = new LinkedHashSet();
        HashSet<ItemStack> hashSet = new HashSet();
        String[] argsLower = strings;
        int totalArgs = strings.length;

        for (int argCursor = 0; argCursor < totalArgs; argCursor++) {
          label269:
          {
            String arg = argsLower[argCursor];
            String argLower = arg.toLowerCase();
            String argValue = argLower;
            switch (argLower.hashCode()) {
              case 104:
                if (argValue.equals("h")) {
                  break label269;
                }
                break;
              case 3194991:
                if (argValue.equals("hand")) {
                  break label269;
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
            }

            if (arg.toLowerCase().startsWith("r:")) {
              try {
                argValue = arg.substring(2);
                if (argValue.equalsIgnoreCase("g")) {
                  search.setRange(0);
                } else {
                  search.setRange(Integer.parseInt(arg.substring(2)));
                }
                continue;
              } catch (Throwable throwable) {
              }
            }

            if (arg.toLowerCase().startsWith("w:")) {
              try {
                search.setWorld(CMIWorld.getWorld(arg.substring(2)));
                if (search.getRange() == -1) {
                  search.setRange(0);
                }
                continue;
              } catch (Throwable throwable2) {
              }
            }

            if (arg.toLowerCase().startsWith("c:") && arg.contains(";")) {
              try {
                argValue = arg.substring(2);
                int radius = Integer.parseInt(argValue.split(";")[0]);
                int height = Integer.parseInt(argValue.split(";")[1]);
                search.setCenter(radius, height);
                continue;
              } catch (Throwable throwable3) {
              }
            }

            if (ItemSearchCriteria.getByName(arg) != null) {
              linkedHashSet.add(ItemSearchCriteria.getByName(arg));
              continue;
            }

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
                } catch (Throwable throwable4) {
                  throwable4.printStackTrace();
                }

                criteria.addAll(ItemSearchCriteria.getFromString(argValue));
                hashSet.add(itemStack);
                if (itemStack.getAmount() > 1) {
                  criteria.add(ItemSearchCriteria.AMOUNT);
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

        if (search.getWorld() == null && commandSender instanceof Player) {
          search.setWorld(((Player) commandSender).getWorld());
        }

        if (search.getCenter() == null) {
          if (commandSender instanceof Player) {
            search.setCenter(((Player) commandSender).getLocation().getChunk());
          } else {
            search.setCenter(0, 0);
          }
        }

        search.search();
        break;
      case pause:
        CMIWorldDataSearch currentSearch = CMIWorldDataSearch.getActiveSearch();
        if (currentSearch == null) {
          CMILC.info(search.class, commandSender, "noactive");
          return false;
        }

        currentSearch.setPaused(true);
        return true;
      case resume:
        CMIWorldDataSearch activeSearchRef = CMIWorldDataSearch.getActiveSearch();
        if (activeSearchRef == null) {
          CMILC.info(search.class, commandSender, "noactive");
          return false;
        }

        activeSearchRef.setPaused(false);
        return true;
    }

    return true;
  }

  public static Location lookAt(Player player, Location location) {
    double number = location.getX() - location.getBlockX();
    double dy = location.getY() - (location.getY() + player.getEyeHeight());
    double dz = location.getZ() - location.getBlockZ();
    double distance = Math.sqrt(number * number + dz * dz);
    float floatValue = (float) Math.toDegrees(Math.atan2(-number, dz));
    float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distance)));
    location.setYaw(floatValue);
    location.setPitch(pitch);
    return location;
  }

  enum actions {
    cancel,
    stop,
    results,
    search,
    pause,
    resume;

    public static scan.actions getByName(String string) {
      scan.actions[] actionses;
      int value = (actionses = values()).length;

      for (int index = 0; index < value; index++) {
        scan.actions action = actionses[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
