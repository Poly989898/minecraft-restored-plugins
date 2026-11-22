package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMICustomModelDataComponent;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class itemcmdata implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("nothing", "{gcp}Doesn't contain any model data");
    configReader.get("set", "{gcp}Set new model data to: {gcs}[value]");
    configReader.get("current", "{gcp}Current model data: {gcs}[value]");
    configReader.get("deleted", "{gcp}Removed model data: {gcs}[value]");
  }

  @CAnnotation(
      info = "Check or modify items custom model data",
      args = "[set/delete/list/add] (playerName) (float,true,false,string) (-s)",
      multiTab = "set,delete,list,add [playerName] -s",
      regVar = -66,
      consoleVar = -100,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMICustomModelDataComponent cmiCustomModelDataComponent = CMICustomModelDataComponent.empty();
    String string = null;
    itemcmdata.Action action = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (action == null) {
          action = itemcmdata.Action.getByName(arg);
          if (action != null) {
            continue;
          }
        }

        try {
          cmiCustomModelDataComponent = CMICustomModelDataComponent.fromString(arg);
        } catch (Exception exception) {
          string = arg;
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (action == null) {
      action = itemcmdata.Action.list;
    }

    ItemStack itemStack = cmiUser.getItemInHand();
    switch (action) {
      case list:
        cmiCustomModelDataComponent = CMICustomModelDataComponent.fromItem(itemStack);
        if (cmiCustomModelDataComponent.isEmpty()) {
          if (!flag) {
            CMILC.info(this, commandSender, "nothing");
          }

          return null;
        }

        if (flag) {
          return null;
        }

        CMILC.info(
            this, commandSender, "current", "[value]", cmiCustomModelDataComponent.toString());
        break;
      case delete:
      case remove:
        CMICustomModelDataComponent existingComponent =
            CMICustomModelDataComponent.fromItem(itemStack);
        if (existingComponent.isEmpty()) {
          if (!flag) {
            CMILC.info(this, commandSender, "nothing");
          }

          return null;
        }

        existingComponent.removeAll(cmiCustomModelDataComponent);
        existingComponent.apply(itemStack);
        cmiUser.setItemInHand(itemStack);
        if (!flag) {
          CMILC.info(this, commandSender, "deleted", "[value]", existingComponent.toString());
        }
        break;
      case set:
        if (cmiCustomModelDataComponent.isEmpty()) {
          LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
          return false;
        }

        itemStack = cmiCustomModelDataComponent.apply(itemStack);
        cmiUser.setItemInHand(itemStack);
        if (!flag) {
          CMILC.info(
              this,
              commandSender,
              "set",
              "[value]",
              CMICustomModelDataComponent.fromItem(itemStack).toString());
        }
        break;
      case add:
        CMICustomModelDataComponent currentComponent =
            CMICustomModelDataComponent.fromItem(itemStack);
        currentComponent.combine(cmiCustomModelDataComponent);
        currentComponent.apply(itemStack);
        cmiUser.setItemInHand(itemStack);
        if (!flag) {
          CMILC.info(this, commandSender, "current", "[value]", currentComponent.toString());
        }
    }

    return true;
  }

  private enum Action {
    list,
    delete,
    remove,
    set,
    add;

    public static itemcmdata.Action getByName(String string) {
      itemcmdata.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        itemcmdata.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
