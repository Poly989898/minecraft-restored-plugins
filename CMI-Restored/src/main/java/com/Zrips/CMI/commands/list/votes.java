package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class votes implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("currentVoteSelf", "{gcp}You made {gcs}[votes] {gcp}votes");
    configReader.get("currentVotes", "{gcp}[playerDisplayName] {gcp}has {gcs}[votes] {gcp}votes");
    configReader.get("voteCooldown", "{gcp}You will need to wait {gcs}[time]{gcp}to vote again");
    configReader.get(
        "voteLimit",
        "{gcp}You have reached vote count limit for today. You will need to wait {gcs}[time]{gcp}to"
            + " vote again");
  }

  @CAnnotation(
      info = "Check players vote count",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!CMIHookType.Votifier.isEnabled()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    if (cmiUser.isSame(commandSender)) {
      CMILC.info(
          this, commandSender, "currentVoteSelf", snd, "[votes]", cmiUser.getVotifierVotes());
    } else {
      CMILC.info(this, commandSender, "currentVotes", snd, "[votes]", cmiUser.getVotifierVotes());
    }

    return true;
  }
}
