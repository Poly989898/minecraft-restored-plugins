package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class haspermission implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "{gcs}[playerDisplayName] {gcp}-> {gcs}[permission]");
    configReader.get(
        "none", "{gcp}Permission is not set for player and he is not OP. Results to {gcs}[state]");
    configReader.get("softSet", "{gcp}Set by plugin.yml to {gcs}[state]");
    configReader.get("hardSet", "{gcp}Hard set to {gcs}[state] {gcp}by some plugin");
    configReader.get("notHardSet", "{gcp}Not hard set, can result to &cFalse {gcp}in some cases");
    configReader.get("ops", "{gcp}Results to {gcs}[state] {gcp}because player is OP");
    configReader.get("minMax", "{gcp}Min: {gcs}[min] {gcp}Max: {gcs}[max]");
  }

  @CAnnotation(
      info = "Check if player has particular permission",
      args = "(playerName) [permissionNode]",
      tab = "playername",
      regVar = {1, 2},
      consoleVar = 2,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String text = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String textSecond = parts[count];
      if (!textSecond.contains(".") && string == null) {
        string = textSecond;
      } else {
        text = textSecond;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (text == null) {
      return false;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (text.endsWith(".")) {
      PermissionInfo permissionInfo = cmi.getPermissionsManager().getPermissionInfo(player, text);
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      CMILC.info(
          this,
          commandSender,
          "minMax",
          "[min]",
          permissionInfo.getMinValue(),
          "[max]",
          permissionInfo.getMaxValue());
      return true;
    }

    PermissionAttachmentInfo permissionAttachmentInfo =
        cmi.getPermissionsManager()
            .getSetPermission(
                player, text.startsWith("-") ? text.substring(1, text.length()) : text);
    String textThird =
        permissionAttachmentInfo == null
            ? CMILC.getIM(this, "none", "[state]", false)
            : CMILC.getIM(this, "hardSet", "[state]", permissionAttachmentInfo.getValue());
    if (player.hasPermission(text.startsWith("-") ? text.substring(1, text.length()) : text)
        && permissionAttachmentInfo == null) {
      textThird = CMILC.getIM(this, "softSet", "[state]", true);
    }

    if (player.isOp() && permissionAttachmentInfo == null) {
      textThird = CMILC.getIM(this, "ops", "[state]", true);
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    CMILC.info(
        this,
        commandSender,
        "title",
        snd,
        "[permission]",
        text.startsWith("-") ? text.substring(1, text.length()) : text);
    if (permissionAttachmentInfo == null && player.isOp()) {
      CMILC.info(this, commandSender, "notHardSet", snd);
    }

    CMILC.sendMessage(commandSender, textThird);
    return true;
  }
}
