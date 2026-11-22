package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class list implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.addComment(
        "List",
        new String[] {
          "[groupLine] can be replaced with specific grouped names like [group1] and [group2] and"
              + " so on, you can have as many of those as you want and order them by your own"
              + " liking"
        });
    configReader.get(
        "List",
        Arrays.asList(
            "{gcn}========================",
            "{gcp}Players online {gcs}[amount][hidden]{gcp}/{gcs}[maxamount]",
            "[adminLine]",
            "[staffLine]",
            "[groupLine]",
            "{gcn}========================"));
    configReader.get("staffL", "{gcn}Staff: {gcs}[list]");
    configReader.get("adminL", "{gcu}Admin: {gcs}[list]");
    configReader.get("hiddenPref", "{gcn}[H]");
    configReader.get("hiddenCount", "{gcn}([amount])");
    configReader.get("groupL", "{gcp}[groupName]: {gcn}[list]");
    configReader.get(
        "player",
        (configReader.getC().isString(configReader.getPath() + "playerF")
                    && !configReader
                        .getC()
                        .getString(configReader.getPath() + "playerF")
                        .contains("%cmi_user_afk_symbol%")
                ? "%cmi_user_afk_symbol%"
                : "")
            + configReader
                .getC()
                .getString(
                    configReader.getPath() + "playerF",
                    "%cmi_user_afk_symbol%{gcn}[playerDisplayName]"));
    Object object = null;
    configReader.addComment(
        "groups",
        new String[] {
          "Add as many groups as you wish, by default there is only 10, to create new one, just"
              + " copy paste one line and add increment into lines name, like group11 or group12"
              + " and so on.",
          "ATTENTION! group names should be in 'group[number]' format, where you have word 'group'"
              + " followed by number. This is required and you cant have custom names as they are"
              + " not required, but we are using number provided to determine which group goes"
              + " after which",
          "Player need to have appropriate permission node to be placed in group"
              + " cmi.command.list.group.[groupnumber], bigger number have bigger priority"
        });
    if (configReader.getC().isConfigurationSection("command.list.info.groups")) {
      for (String string :
          configReader.getC().getConfigurationSection("command.list.info.groups").getKeys(false)) {
        configReader.get(
            "groups." + string,
            configReader.getC().getString("command.list.info.groups." + string));
      }
    } else {
      for (int value = 1; value <= 10; value++) {
        configReader.get("groups.group" + value, "{gcp}Group" + value + ": {gcn}[list]");
      }
    }

    configReader.get("noGroupLine", "{gcp}List");
  }

  @CAnnotation(
      info = "Shows online player list",
      explanation = {
        "Perm: ",
        "cmi.command.list.admin",
        "cmi.command.list.staff",
        "cmi.command.list.hidden",
        "cmi.command.list.group.[number]"
      },
      regVar = 0,
      consoleVar = 0,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    ArrayList<CMIUser> arrayList = new ArrayList();
    int value = 0;

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      value++;
      CMIUser cmiUser = cmi.getPlayerManager().getUser(onlinePlayer);
      if (cmiUser != null && !cmiUser.isFakeAccount()) {
        arrayList.add(cmiUser);
      }
    }

    ArrayList<CMIUser> firstGroup = new ArrayList();
    ArrayList<CMIUser> secondGroup = new ArrayList();
    ArrayList<CMIUser> thirdGroup = new ArrayList();
    ArrayList<CMIUser> fourthGroup = new ArrayList();
    boolean flag = PermissionsManager.CMIPerm.command_list_hidden.hasPermission(commandSender);
    boolean showVanished = false;
    boolean showHidden = false;

    for (String string : CMILC.getIML(this, "List")) {
      if (string.contains("[adminLine]")) {
        showVanished = true;
      }

      if (string.contains("[staffLine]")) {
        showHidden = true;
      }
    }

    for (CMIUser listedUser : arrayList) {
      Player listedPlayer = listedUser.getPlayer();
      CMIUser userInfo = cmi.getPlayerManager().getUser(listedPlayer);
      if (userInfo != null && !userInfo.isFakeAccount()) {
        if (userInfo.isVanished() || player != null && !player.canSee(listedPlayer)) {
          fourthGroup.add(listedUser);
          if (!flag) {
            continue;
          }
        }

        if (showVanished
            && PermissionsManager.CMIPerm.command_list_admin.hasPermission(listedPlayer)) {
          firstGroup.add(listedUser);
        } else if (showHidden
            && PermissionsManager.CMIPerm.command_list_staff.hasPermission(listedPlayer)) {
          secondGroup.add(listedUser);
        } else {
          thirdGroup.add(listedUser);
        }
      }
    }

    HashMap<String, List> hashMap = new HashMap();
    TreeMap treeMap =
        Config.CommandsListASCOrder ? new TreeMap() : new TreeMap(Collections.reverseOrder());

    for (CMIUser groupedUser : thirdGroup) {
      Player groupedPlayer = groupedUser.getPlayer();
      PermissionInfo permissionInfo =
          cmi.getPermissionsManager()
              .getPermissionInfo(
                  groupedPlayer, PermissionsManager.CMIPerm.command_list_group_$1, 1000L);
      if (permissionInfo != null && permissionInfo.getMaxValue() != null) {
        List list = (List) treeMap.get(permissionInfo.getMaxValue().intValue());
        if (list == null) {
          list = new ArrayList();
        }

        list.add(groupedUser);
        treeMap.put(permissionInfo.getMaxValue().intValue(), list);
      } else {
        String groupName = groupedUser.getGroupName();
        if (groupName == null) {
          groupName = CMILC.getIM(this, "noGroupLine");
        }

        List groupEntries = (List) hashMap.get(groupName);
        if (groupEntries == null) {
          groupEntries = new ArrayList();
        }

        groupEntries.add(groupedUser);
        hashMap.put(groupName, groupEntries);
      }
    }

    String groupLine = "";

    for (CMIUser firstGroupUser : firstGroup) {
      if (!groupLine.isEmpty()) {
        groupLine = groupLine + LC.info_ListSpliter.getLocale(new Object[0]);
      }

      groupLine =
          groupLine
              + (fourthGroup.contains(firstGroupUser) ? CMILC.getIM(this, "hiddenPref") : "")
              + this.nameFormating(firstGroupUser.getPlayer());
    }

    if (!groupLine.isEmpty()) {
      groupLine = CMILC.getIM(this, "adminL", "[list]", groupLine);
    }

    String secondGroupLine = "";

    for (CMIUser secondGroupUser : secondGroup) {
      if (!secondGroupLine.isEmpty()) {
        secondGroupLine = secondGroupLine + LC.info_ListSpliter.getLocale(new Object[0]);
      }

      secondGroupLine =
          secondGroupLine
              + (fourthGroup.contains(secondGroupUser) ? CMILC.getIM(this, "hiddenPref") : "")
              + this.nameFormating(secondGroupUser.getPlayer());
    }

    if (!secondGroupLine.isEmpty()) {
      secondGroupLine = CMILC.getIM(this, "staffL", "[list]", secondGroupLine);
    }

    String thirdGroupLine = "";
    HashMap<Integer, String> groupMap = new HashMap();

    for (Entry entry : ((java.util.Map<?, ?>) treeMap).entrySet()) {
      String fourthGroupLine = "";

      for (CMIUser entryUser : (List<CMIUser>) entry.getValue()) {
        if (!fourthGroupLine.isEmpty()) {
          fourthGroupLine = fourthGroupLine + LC.info_ListSpliter.getLocale(new Object[0]);
        }

        fourthGroupLine =
            fourthGroupLine
                + (fourthGroup.contains(entryUser) ? CMILC.getIM(this, "hiddenPref") : "")
                + this.nameFormating(entryUser.getPlayer());
      }

      groupMap.put(
          (Integer) entry.getKey(),
          CMILC.getIM(this, "groups.group" + entry.getKey(), "[list]", fourthGroupLine));
      if (!fourthGroupLine.isEmpty()) {
        if (!thirdGroupLine.isEmpty()) {
          thirdGroupLine = thirdGroupLine + " \n";
        }

        thirdGroupLine =
            thirdGroupLine
                + CMILC.getIM(this, "groups.group" + entry.getKey(), "[list]", fourthGroupLine);
      }
    }

    for (Entry mapEntry : hashMap.entrySet()) {
      String entryLine = "";

      for (CMIUser mapUser : (List<CMIUser>) mapEntry.getValue()) {
        if (!entryLine.isEmpty()) {
          entryLine = entryLine + LC.info_ListSpliter.getLocale(new Object[0]);
        }

        entryLine =
            entryLine
                + (fourthGroup.contains(mapUser) ? CMILC.getIM(this, "hiddenPref") : "")
                + this.nameFormating(mapUser.getPlayer());
      }

      if (!entryLine.isEmpty()) {
        if (!thirdGroupLine.isEmpty()) {
          thirdGroupLine = thirdGroupLine + " \n";
        }

        thirdGroupLine =
            thirdGroupLine
                + CMILC.getIM(
                    this, "groupL", "[groupName]", mapEntry.getKey(), "[list]", entryLine);
      }
    }

    for (String groupKey : cmi.getLM().getKeys("command.list.info.groups")) {
      thirdGroupLine = thirdGroupLine.replace("[" + groupKey + "]", "");
    }

    if (!flag) {
      value -= fourthGroup.size();
    }

    String groupText =
        !fourthGroup.isEmpty() && flag
            ? CMILC.getIM(this, "hiddenCount", "[amount]", fourthGroup.size())
            : "";
    var groupLines =
        CMILC.getIML(
            this,
            "List",
            "[adminLine]",
            groupLine,
            "[staffLine]",
            secondGroupLine,
            "[groupLine]",
            thirdGroupLine,
            "[amount]",
            value,
            "[hidden]",
            groupText,
            "[maxamount]",
            Bukkit.getMaxPlayers());

    for (String infoKey : cmi.getLM().getKeys("command.list.info.groups")) {
      int lineIndex = 0;

      try {
        if (infoKey.contains("group")) {
          lineIndex = Integer.parseInt(infoKey.substring("group".length()));
        } else {
          lineIndex = Integer.parseInt(infoKey);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        continue;
      }

      String lineText = (String) groupMap.get(lineIndex);
      if (lineText != null) {
        for (int lineCount = 0; lineCount < groupLines.size(); lineCount++) {
          groupLines.set(
              lineCount,
              ((String) groupLines.get(lineCount)).replace("[" + infoKey + "]", lineText));
        }
      }
    }

    for (String lineItem : groupLines) {
      String lineValue = lineItem;

      for (String subKey : cmi.getLM().getKeys("command.list.info.groups")) {
        lineValue = lineValue.replace("[" + subKey + "]", "");
      }

      if (!lineValue.isEmpty()) {
        commandSender.sendMessage(CMIChatColor.translate(lineValue));
      }
    }

    return true;
  }

  private String nameFormating(Player player) {
    Snd snd = new Snd().setSender(player).setTarget(player);
    String string = CMILC.getIM(this, "player", snd);
    return CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
  }
}
