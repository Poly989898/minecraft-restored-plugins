package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class walkspeed implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[amount]{gcp} walk speed for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your walkspeed set to {gcs}[amount]{gcp} by {gcs}[senderDisplayName]{gcp}.");
    configReader.get("max", "{gcp}Max allowed walk speed for you is {gcs}[max]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set players walk speed from 0 to 5",
      args = "(playerName) [amount] (-s)",
      tab = {"playername"},
      explanation = {},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      customAlias = {"wspeed"},
      others = true)
  public Boolean perform(CMI cMI, CommandSender sender, String[] args) {
    boolean silent = false;
    double amount = -1.0;
    String targetName = null;

    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          if (PermissionsManager.CMIPerm.command_silent.hasPermission(sender)) {
            silent = true;
          }
          break;
        default:
          CMIUser user;
          if (targetName == null && (user = CMIUser.getUser(arg)) != null) {
            targetName = user.getName();
            break;
          }
          try {
            amount = Double.parseDouble(arg);
          } catch (NumberFormatException numberFormatException) {
            targetName = arg;
          }
          break;
      }
    }

    Player target = cMI.getTarget(sender, targetName, this);
    if (target == null) {
      return null;
    }

    double minAmount = 0.0;
    if (amount < minAmount) {
      LC.info_NoLessThan.sendMessage((Object) sender, new Object[] {"[amount]", minAmount});
      return null;
    }

    double maxAmount = 10.0;
    if (Version.isCurrentHigher(Version.v1_11_R1)) {
      maxAmount = 5.0;
    }
    if (amount > maxAmount) {
      LC.info_NoMoreThan.sendMessage((Object) sender, new Object[] {"[amount]", maxAmount});
      return null;
    }

    double allowedMax = CMINumber.clamp(maxSpeed(sender), 0.0, maxAmount);
    if (amount > allowedMax) {
      CMILC.info((Object) this, sender, "max", "[max]", allowedMax);
      return null;
    }

    target.setWalkSpeed((float) (amount / 5.0));
    cMI.save(target);

    Snd snd = new Snd().setSender(sender).setTarget(target);
    if (!silent) {
      CMILC.info((Object) this, sender, "feedback", snd, "[amount]", amount);
      if (!target.getName().equalsIgnoreCase(sender.getName())) {
        CMILC.info(
            (Object) this, (CommandSender) target, "targetfeedback", snd, "[amount]", amount);
      }
    }
    return true;
  }

  private static double maxSpeed(CommandSender sender) {
    if (!(sender instanceof Player)
        || PermissionsManager.CMIPerm.command_walkspeed_$1.hasPermission(sender, "*")) {
      return 10.0;
    }
    return PermissionsManager.CMIPerm.command_walkspeed_$1
        .getPermissionInfo((Player) sender, "")
        .getMaxValue(1.0);
  }
}
