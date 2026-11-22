package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Warnings.CMIPlayerWarning;
import com.Zrips.CMI.Modules.Warnings.CMIWarningCategory;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerWarnEvent;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class warn implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noSelfWarning", "{gcw}You can't warn yourself");
    configReader.get("cantBan", "{gcw}Can't warn this player");
    configReader.get(
        "messageToAll",
        "{gcs}[playerDisplayName] {gcw}got warned by {gcs}[senderDisplayName] {gcw}for:"
            + " {gcs}[reason]");
    configReader.get(
        "banedSilent",
        "{gcn}[{gcu}Silent{gcn}]{gcs}[playerDisplayName] {gcw}got warned by"
            + " {gcs}[senderDisplayName] {gcw}for: {gcs}[reason]");
  }

  @CAnnotation(
      info = "Warn player",
      args = "[playerName] (category) (reason) (-s)",
      tab = {"playername", "warncategory"},
      explanation = {},
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String text = "";
    boolean flag = false;
    CMIWarningCategory cmiWarningCategory = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String textSecond = parts[count];
      if (string == null) {
        string = textSecond;
      } else if (textSecond.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (cmiWarningCategory == null && text.isEmpty()) {
          cmiWarningCategory = cmi.getWarningManager().getCategory(textSecond);
          if (cmiWarningCategory != null) {
            continue;
          }
        }

        if (!text.isEmpty()) {
          text = text + " ";
        }

        text = text + textSecond;
      }
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUserByAproxName(string);
    if (cmiUser == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmiUser.getName().equals(commandSender.getName())) {
      CMILC.info(this, commandSender, "noSelfWarning");
      return null;
    }

    if (cmiUser.hasPermission(PermissionsManager.CMIPerm.command_ban_bypass)
        && commandSender instanceof Player) {
      CMILC.info(this, commandSender, "cantBan");
      return null;
    }

    CMIPlayerWarning cmiPlayerWarning = new CMIPlayerWarning();
    cmiPlayerWarning.setGivenAt(System.currentTimeMillis());
    cmiPlayerWarning.setGivenBy(commandSender.getName());
    cmiPlayerWarning.setCategory(
        cmiWarningCategory == null
            ? CMI.getInstance().getWarningManager().getDefaultCategory()
            : cmiWarningCategory);
    cmiPlayerWarning.setReason(text.isEmpty() ? null : text);
    CMIPlayerWarnEvent cmiPlayerWarnEvent = new CMIPlayerWarnEvent(cmiUser, cmiPlayerWarning);
    Bukkit.getServer().getPluginManager().callEvent(cmiPlayerWarnEvent);
    if (cmiPlayerWarnEvent.isCancelled()) {
      return null;
    }

    text = cmiPlayerWarning.getReason();
    cmiUser.addWarning(cmiPlayerWarning);
    int countSecond = cmiUser.getWarningPoints();
    cmiUser.addForDelayedSave();
    text =
        text != null && !text.isEmpty() ? text : cmiPlayerWarning.getCategory().getDefaultReason();
    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (!flag) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
          CMILC.info(this, player, "messageToAll", snd, "[reason]", text);
        }
      }

      CMILC.info(this, commandSender, "messageToAll", snd, "[reason]", text);
    } else {
      CMILC.info(this, cmiUser, "messageToAll", snd, "[reason]", text);
      CMILC.info(this, commandSender, "banedSilent", snd, "[reason]", text);
    }

    var list =
        cmi.getWarningManager()
            .getCommandsByWarns(
                cmiUser.getPlayer(),
                countSecond - cmiPlayerWarning.getCategory().getPoints(),
                countSecond);
    if (!list.isEmpty()) {
      cmi.getSpecializedCommandManager().processCmds(list);
    }

    return true;
  }
}
