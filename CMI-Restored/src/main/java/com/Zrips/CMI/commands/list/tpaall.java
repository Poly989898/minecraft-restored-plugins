package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tpaall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("requestSent", "{gcp}Request sent to all players");
  }

  @CAnnotation(
      info = "Ask all online players to teleport to your location",
      regVar = 0,
      consoleVar = 666)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    cmi.getTeleportManager().clearRequests(player);
    Snd snd = new Snd().setSender(commandSender);

    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
      if (!otherPlayer.getUniqueId().equals(player.getUniqueId())) {
        cmi.getTeleportManager().addTpRequest(player, otherPlayer, TeleportManager.TpAction.tpaall);
        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(CMILC.getIM("tpahere", "gotRequest", snd))
            .addHover(
                CMILC.getIM(
                        "tpa",
                        "time",
                        "[time]",
                        CMITimeManager.to24hourShort(
                            (long) (cmi.getTeleportManager().getTpaTime() * 1000)))
                    + " \n"
                    + CMILC.getIM("tpahere", "accept"))
            .addCommand("cmi tpaccept " + player.getName() + " tpaall");
        rawMessage
            .addText(LC.modify_acceptSymbol)
            .addHover(LC.modify_acceptSymbolHover)
            .addCommand("cmi tpaccept " + player.getName() + " tpaall");
        rawMessage
            .addText(LC.modify_denySymbol)
            .addHover(LC.modify_denySymbolHover)
            .addCommand("cmi tpdeny " + player.getName());
        rawMessage.show(otherPlayer);
      }
    }

    CMILC.info(this, commandSender, "requestSent");
    return true;
  }
}
