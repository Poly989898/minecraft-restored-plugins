package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.Modules.Teleportations.TpInfo;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpdeny implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noRequest", "{gcp}You have no active teleportation requests!");
    configReader.get("youDenied", "{gcp}Your teleportation request was denied!");
    configReader.get("denied", "{gcs}[senderDisplayName] {gcp}denied your request.");
  }

  @CAnnotation(
      info = "Deny teleport request",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 666)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    Player requester = null;
    if (strings.length > 0) {
      requester = CMIUser.getOnlinePlayer(strings[0], true);
    }

    TpInfo tpInfo = cmi.getTeleportManager().getTeleportInfo(player, requester);
    if (tpInfo == null) {
      CMILC.info(this, commandSender, "noRequest");
      return null;
    }

    cmi.getTeleportManager().addTpBlock(player, tpInfo);
    Snd snd = new Snd().setSender(commandSender).setTarget(requester);
    CMILC.info(this, player, "youDenied");
    if (tpInfo.getAction() != TeleportManager.TpAction.tpaall) {
      CMILC.info(this, tpInfo.getWhoOffers(), "denied", snd);
    }

    return true;
  }
}
