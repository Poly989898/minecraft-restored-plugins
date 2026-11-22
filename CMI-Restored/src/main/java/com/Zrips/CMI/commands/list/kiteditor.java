package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class kiteditor implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("kits", "{gcp}Kits: ");
    configReader.get("iconSet", "{gcp}New icon set for {gcs}[kitname] {gcp}kit");
    configReader.get("kitExists", "{gcp}Kit with this name already exist!");
    configReader.get(
        "deleteConfirm", "{gcp}Click me if you really want to remove ({gcs}[kitname]{gcp}) kit!");
    configReader.get("deleteConfirmFinal", "{gcw}This action is not reversible!");
    configReader.get("kitRemoved", "{gcp}Kit ({gcs}[kitname]{gcp}) removed");
    configReader.get("kitNotRemoved", "{gcw}Kit ({gcs}[kitname]{gcw}) could not be removed");
    configReader.get(
        "addNewInfo",
        "{gcp}Type kit name into chat and press enter. Type '{gcs}cancel{gcp}' to cancel this"
            + " action.");
    configReader.get(
        "renameCommandInfo",
        "{gcp}Type new kit {gcs}command {gcp}name into chat and press enter. Type"
            + " '{gcs}cancel{gcp}' to cancel this action.");
    configReader.get(
        "renameConfigInfo",
        "{gcp}Type new kit {gcs}config {gcp}name into chat and press enter. Type '{gcs}cancel{gcp}'"
            + " to cancel this action.");
    configReader.get(
        "groupInfo",
        "{gcp}Type new group name into chat and press enter. Type '{gcs}none{gcp}' to remove group."
            + " Type '{gcs}cancel{gcp}' to cancel this action.");
    configReader.get(
        "commandInfo",
        "{gcp}Type new command into chat and press enter. Type '{gcs}cancel{gcp}' to cancel this"
            + " action. Possible placeholders: {gcs}{USERNAME} {DISPLAYNAME} {WORLDNAME} {KITNAME}"
            + " {RANDOMPLAYER}");
    configReader.get(
        "conditionsInfo",
        "{gcp}Type new required permission node into chat and press enter. Type '{gcs}cancel{gcp}'"
            + " to cancel this action");
    configReader.get(
        "descInfo",
        "{gcp}Type new description into chat and press enter. Type '{gcs}cancel{gcp}' to cancel"
            + " this action");
    configReader.get("renamed", "{gcp}Renamed {gcs}[kitname] {gcp}to {gcs}[newkitname]{gcp}.");
    configReader.get("groupSet", "{gcp}New group ({gcs}[groupname]{gcp}) set for {gcs}[kitname]");
    configReader.get("commandsListTitle", "{gcp}✎{gcs}Commands:");
    configReader.get("conditionsListTitle", "{gcp}✎{gcs}Conditions:");
    configReader.get("descListTitle", "{gcp}✎{gcs}Description:");
    configReader.get("ListTitleHover", "{gcp}Click to open GUI");
    configReader.get("ListEditKitHover", "&cEdit {gcs}[kitname]");
    configReader.get("ListAdd", " {gcu}+");
    configReader.get("ListAddHover", "{gcu}Add new");
    configReader.get(
        "guiClickery",
        Arrays.asList(
            "{gcp}Left mouse click to increase and right click to decrease",
            "{gcp}Hold {gcs}Shift {gcp}to speed process by 10 times",
            "{gcp}Repeating fast clicks will speed amount change",
            "{gcp}Stop clicking for 1 sec to go back to default amount"));
    configReader.get("OffHand", "{gcp}Place offHand item over here");
    configReader.get("Helmet", "{gcp}Place helmet item over here");
    configReader.get("Chest", "{gcp}Place chest item over here");
    configReader.get("Legs", "{gcp}Place leggings over here");
    configReader.get("Boots", "{gcp}Place boots over here");
    configReader.get("Weight", "{gcp}Weight: {gcs}[Weight]");
    configReader.get(
        "WeightExtra",
        Arrays.asList(
            "{gcp}Higher value have bigger priority", "{gcp}Kits should be in same group"));
    configReader.get("despiteWeight", "{gcp}Show despite weight: {gcs}[Weight]");
    configReader.get(
        "despiteWeightExtra",
        Arrays.asList("{gcp}PLayer is not getting access to kit, but can preview it"));
    configReader.get("Slot", "{gcp}Slot: {gcs}[slot]");
    configReader.get("Page", "{gcp}Page: {gcs}[page]");
    configReader.get("SlotAuto", "{gcs}Auto");
    configReader.get("Clone", "{gcp}Clone items from inventory");
    configReader.get(
        "CloneExtra",
        Arrays.asList(
            "{gcp}Use right mouse button to include armors", "{gcp}Click again to revert changes"));
    configReader.get("timeDelay", "{gcp}Delay: {gcs}[time]");
    configReader.get("usages", "{gcp}Can be used for {gcs}[times] {gcp}times");
    configReader.get("withoutUsage", "{gcp}Show without usages left: {gcs}[state]");
    configReader.get("usagesUnlimited", "{gcs}Unlimited");
    configReader.get("ignoreWeight", "{gcp}Show despite weight: {gcs}[state]");
    configReader.get("timeDelaySeconds", "{gcp}In seconds: {gcs}[time]");
    configReader.get("moneyCost", "{gcp}Money cost: [cost]");
    configReader.get("expCost", "{gcp}Exp cost: [cost]");
    configReader.get("enabled", "{gcp}Enabled");
    configReader.get("disabled", "{gcs}Disabled");
    configReader.get("delete", "{gcs}Delete kit");
    configReader.get("clickToSwitch", "{gcp}Click to switch state");
    configReader.get("clickToEdit", "{gcp}Click to start editing");
    configReader.get(
        "editConfigName",
        Arrays.asList(
            "{gcp}Click to rename config name", "{gcp}This only determines permission node"));
    configReader.get(
        "editCommandName",
        Arrays.asList(
            "{gcp}Click to rename command name",
            "{gcp}This sets command you need to use to get this kit"));
    configReader.get("displayName", "{gcp}DisplayName: &r{gcs}[name]");
    configReader.get(
        "editDisplayName",
        Arrays.asList(
            "{gcp}Click to change display name", "{gcp}This only used for visual purposes"));
    configReader.get("fileName", "{gcp}FileName: &r{gcs}[name]");
    configReader.get(
        "editFileName",
        Arrays.asList(
            "{gcp}Click to change file name", "{gcp}Will define where kit should be saved at"));
    configReader.get("clickToEdit", "{gcp}Click to start editing");
    configReader.get("kitGroup", "{gcp}Kit group: {gcs}[group]");
    configReader.get("dropItems", "{gcp}Drop item if full: {gcs}[state]");
    configReader.get("commands", "{gcp}Commands:");
    configReader.get("conditions", "{gcp}Conditions:");
    configReader.get("desc", "{gcp}Description:");
    configReader.get("mainMenu", "{gcp}MainMenu");
    configReader.get("settings", "{gcp}Settings");
    configReader.get("icon", "{gcp}Icon used when kit is {gcu}available");
    configReader.get("iconCd", "{gcp}Icon used when kit is {gcn}unavailable");
  }

  @CAnnotation(
      info = "Kit editor",
      tab = {"seticon%%kitnames", "kitnames"},
      explanation = {
        "Permissions:",
        "cmi.kit.[kitName] - allows to use particular kit",
        "cmi.kit.bypass.money - bypass money requirement",
        "cmi.kit.bypass.exp - bypass exp requirement",
        "/cmi kiteditor - shows kit list"
      },
      regVar = -666,
      consoleVar = 666,
      others = false,
      modules = "kits")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    Integer integer = null;
    Kit kit = null;
    kiteditor.Action action = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (action == null) {
        action = kiteditor.Action.getByName(string);
      }

      if (string.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(strings[0].substring("-p:".length()));
          continue;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }

      if (kit == null) {
        kit = cmi.getKitsManager().getKit(string, true);
      }
    }

    if (integer == null) {
      integer = 1;
    }

    if (action == null && kit != null) {
      action = kiteditor.Action.edit;
    }

    if (action == null) {
      action = kiteditor.Action.list;
    }

    switch (action) {
      case list:
        cmi.getKitsManager().listPlayersKitsForEditing(player, integer);
        return true;
      case add:
      case new_:
        cmi.getKitsManager().startChatKitAdd(player, strings.length > 1 ? strings[0] : null);
      default:
        return true;
      case edit:
        if (kit == null) {
          CMILC.info(
              this,
              commandSender,
              "kitDoesntExist",
              "[kitName]",
              strings.length > 1 ? strings[1] : "");
          return null;
        } else {
          cmi.getKitsManager().openGuiEditorMain(kit, player, true);
          return true;
        }
    }
  }

  private enum Action {
    list,
    add,
    new_,
    edit;

    public static kiteditor.Action getByName(String string) {
      kiteditor.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        kiteditor.Action action = actions[index];
        if (action.name().replace("_", "").equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
