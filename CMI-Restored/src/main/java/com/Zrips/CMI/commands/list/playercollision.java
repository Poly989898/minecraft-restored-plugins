package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class playercollision implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set collision to {gcs}[boolean]{gcp} for {gcs}[playerDisplayName]{gcp}.");
    configReader.get("targetfeedback", "{gcp}Your collision mode changed to {gcs}[boolean]");
  }

  @CAnnotation(
      priority = 70,
      info = "Set players collision mode",
      args = "(playerName) [true/false] (-s)",
      tab = {"playername", "true%%false", "-s%%"},
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      commandSender.sendMessage(CMIChatColor.RED + "Only for 1.9+ servers");
      return false;
    }

    if (Config.DisableTeamManagement) {
      CMILC.info_TeamManagementDisabled.sendMessage(commandSender);
      return null;
    }

    boolean flag = false;
    String string = null;
    Boolean collisionEnabled = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (arg.equalsIgnoreCase("false")) {
        collisionEnabled = false;
      } else if (arg.equalsIgnoreCase("true")) {
        collisionEnabled = true;
      } else if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    if (collisionEnabled == null) {
      collisionEnabled = !cmiUser.isCollidable();
    }

    cmiUser.setCollision(collisionEnabled);
    cmiUser.reapplyNamePlate();
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    cmi.save(player);
    if (!flag) {
      CMILC.info(this, commandSender, "feedback", snd, "[boolean]", collisionEnabled);
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(this, player, "targetfeedback", "[boolean]", collisionEnabled, snd);
    }

    return true;
  }
}
