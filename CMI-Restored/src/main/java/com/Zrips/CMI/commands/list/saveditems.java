package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.SavedItems.SavedItem;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class saveditems implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "info", Arrays.asList("{gcn}Left click to get item", "{gcn}Middle mouse click to detele"));
    configReader.get("got", "{gcp}Got {gcs}[name] {gcp}item");
    configReader.get("give", "{gcp}Give {gcs}[name] {gcp}item");
    configReader.get("removed", "{gcp}Removed {gcs}[name] {gcp}item");
    configReader.get("saved", "{gcp}Saved {gcs}[name] {gcp}item");
    configReader.get("uiTitle", "Category: [category] {gcs}[current]{gcp}/{gcs}[total]");
    configReader.get("name", "{gcp}Name: {gcs}[name]");
    configReader.get("category", "{gcp}Category: {gcs}[category]");
  }

  @CAnnotation(
      priority = 110,
      info = "Save or manage saved items",
      args =
          "[save/get/remove/list] (savedItemName) (-t:playerName) (-a:amount) (-c:category/all)"
              + " (-s)",
      multiTab = {"save,list", "get,remove [savedItems]"},
      explanation = {},
      regVar = {0, 1, 2, 3, 4, 5, 6},
      consoleVar = {2, 3, 4, 5, 6},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    saveditems.Action action = null;
    boolean flag = false;
    String string = null;
    String itemName = null;
    String category = null;
    SavedItem savedItem = null;
    int value = 1;
    Integer integer = null;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      String argCopy = arg;
      switch (arg.hashCode()) {
        case -934610812:
          if (argCopy.equals("remove")) {
            action = saveditems.Action.remove;
            continue;
          }
          break;
        case 1510:
          if (argCopy.equals("-s")) {
            if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
              flag = true;
            }
            continue;
          }
          break;
        case 102230:
          if (argCopy.equals("get")) {
            action = saveditems.Action.get;
            continue;
          }
          break;
        case 3173137:
          if (argCopy.equals("give")) {
            action = saveditems.Action.give;
            continue;
          }
          break;
        case 3322014:
          if (argCopy.equals("list")) {
            action = saveditems.Action.list;
            continue;
          }
          break;
        case 3522941:
          if (argCopy.equals("save")) {
            action = saveditems.Action.save;
            continue;
          }
      }

      if (arg.startsWith("-t:")) {
        string = arg.substring("-t:".length());
      } else if (arg.startsWith("-a:")) {
        try {
          integer = Integer.parseInt(arg.substring("-a:".length()));
        } catch (Exception exception) {
        }
      } else if (arg.startsWith("-c:")) {
        itemName = arg.substring("-c:".length());
      } else if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.substring("-p:".length()));
        } catch (Exception exception2) {
        }
      } else {
        category = arg;
      }
    }

    integer = integer != null ? integer < 1 ? 1 : integer : null;
    savedItem = cmi.getSavedItemManager().getSavedItem(itemName, category);
    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (action == null) {
      action = saveditems.Action.list;
    }

    switch (action) {
      case save:
        if (strings.length < 2) {
          return false;
        } else {
          String categoryName = category;
          ItemStack itemStack = cmiUser.getItemInHand();
          if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
            if (categoryName == null) {
              LC.info_wrongName.sendMessage(commandSender, new Object[0]);
              return null;
            }

            savedItem = cmi.getSavedItemManager().getSavedItem(itemName, categoryName);
            if (savedItem != null) {
              LC.info_alreadyexist.sendMessage(commandSender, new Object[0]);
              return null;
            }

            SavedItem newSavedItem = new SavedItem(categoryName, itemStack.clone());
            newSavedItem.setCategory(itemName);
            cmi.getSavedItemManager().addSavedItem(newSavedItem);
            cmi.getSavedItemManager().save();
            CMILC.info(this, commandSender, "saved", "[name]", newSavedItem.getName());
            return true;
          }

          LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
          return null;
        }
      case get:
        if (strings.length < 2) {
          return false;
        } else {
          if (savedItem == null) {
            LC.info_dontexist.sendMessage(commandSender, new Object[0]);
            return null;
          }

          ItemStack itemCopy =
              cmi.getSavedItemManager().getUpatedItemClone(cmiUser.getPlayer(), savedItem);
          if (integer != null) {
            itemCopy.setAmount(integer);
          }

          if (itemCopy != null) {
            cmiUser.getInventory().addItem(itemCopy);
          }

          if (!flag) {
            CMILC.info(this, commandSender, "got", "[name]", savedItem.getName());
          }

          cmi.save(cmiUser.getPlayer());
          return true;
        }
      case remove:
        if (strings.length < 2) {
          return false;
        } else {
          String categoryAlt = category;
          savedItem = cmi.getSavedItemManager().getSavedItem(itemName, categoryAlt);
          if (savedItem == null) {
            LC.info_dontexist.sendMessage(commandSender, new Object[0]);
            return null;
          }

          cmi.getSavedItemManager().removeSavedItem(itemName, categoryAlt);
          CMILC.info(this, commandSender, "removed", "[name]", savedItem.getName());
          return true;
        }
      case list:
        if (!cmiUser.isOnline()) {
          return false;
        }

        this.list(cmiUser, itemName, value);
        return true;
      case give:
        if (strings.length < 2) {
          return false;
        } else {
          if (savedItem == null) {
            LC.info_dontexist.sendMessage(commandSender, new Object[0]);
            return null;
          }

          ItemStack itemClone =
              cmi.getSavedItemManager().getUpatedItemClone(cmiUser.getPlayer(), savedItem);
          if (integer != null) {
            itemClone.setAmount(integer);
          }

          if (itemClone != null) {
            cmiUser.getInventory().addItem(itemClone);
          }

          cmi.save(cmiUser.getPlayer());
          if (!flag) {
            CMILC.info(this, commandSender, "give", "[name]", savedItem.getName());
          }

          return true;
        }
      default:
        return false;
    }
  }

  private void list(final CMIUser cmiUser, final String string, final int value) {
    CMIGui cmiGui =
        new CMIGui(cmiUser.getPlayer()) {
          public void pageChange(int page) {
            saveditems.this.list(cmiUser, string, page);
          }
        };
    cmiGui.addLock(InvType.Gui);
    Integer integer = GUIRows.r6.getFields();
    cmiGui.setInvSize(integer);
    var set = CMI.getInstance().getSavedItemManager().getSavedItems(string);
    PageInfo pageInfo = new PageInfo(set.size() > 54 ? 45 : 54, set.size(), value);
    cmiGui.setTitle(
        CMILC.getIM(
            this,
            "uiTitle",
            "[category]",
            string == null ? "dc" : string + " ",
            "[current]",
            pageInfo.getCurrentPage(),
            "[total]",
            pageInfo.getTotalPages()));

    for (final SavedItem savedItem : set) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (pageInfo.isEntryOk()) {
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(savedItem.getItem()) {
              public void click(GUIClickType guiClickType) {
                switch (guiClickType) {
                  case Left:
                  case LeftShift:
                    ItemStack itemStack =
                        CMI.getInstance()
                            .getSavedItemManager()
                            .getUpatedItemClone(cmiUser.getPlayer(), savedItem);
                    if (itemStack != null) {
                      if (guiClickType.equals(GUIClickType.LeftShift)) {
                        itemStack.setAmount(10);
                      } else {
                        itemStack.setAmount(1);
                      }

                      cmiUser.getInventory().addItem(itemStack);
                    }

                    CMILC.info(
                        saveditems.class,
                        cmiUser.getPlayer(),
                        "got",
                        "[name]",
                        savedItem.getName());
                  case Right:
                  case RightShift:
                  case QControl:
                  default:
                    break;
                  case MiddleMouse:
                  case Q:
                    CMI.getInstance().getSavedItemManager().removeSavedItem(savedItem);
                    CMILC.info(
                        saveditems.class,
                        cmiUser.getPlayer(),
                        "removed",
                        "[name]",
                        savedItem.getName());
                    saveditems.this.list(cmiUser, string, value);
                }
              }
            };
        cmiGuiButton.addLore("");
        cmiGuiButton.addLore("");
        cmiGuiButton.addLore(CMILC.getIM(saveditems.class, "name", "[name]", savedItem.getName()));
        cmiGuiButton.addLore(
            CMILC.getIM(
                saveditems.class,
                "category",
                "[category]",
                savedItem.getCategory() == null ? "dc" : savedItem.getCategory()));
        cmiGuiButton.addLore(CMILC.getIML(saveditems.class, "info"));
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private enum Action {
    save,
    get,
    remove,
    list,
    give;
  }
}
