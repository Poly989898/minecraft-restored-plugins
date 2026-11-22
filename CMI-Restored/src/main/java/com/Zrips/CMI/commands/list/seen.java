package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class seen implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcs}[playerDisplayName] {gcp}has been {gcp}[offon] {gcp}since {gcs}[time]{gcp}ago!");
  }

  @CAnnotation(
      info = "Check when player was last seen",
      args = "[playerName/uuid]",
      tab = "playername",
      regVar = 1,
      consoleVar = 1,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    if (strings.length > 0) {
      string = strings[0];
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
    if (cmiUser == null) {
      return null;
    }

    String userName = cmiUser.getName();
    boolean flag =
        !(commandSender instanceof Player)
            ? true
            : (cmiUser.isOnline() ? ((Player) commandSender).canSee(cmiUser.getPlayer()) : true);
    boolean online = cmiUser.isOnline() && flag;
    long longValue = online ? cmiUser.getLastLogin(true) : cmiUser.getLastLogoff(true);
    if (longValue == 0L && cmiUser.isOnline()) {
      longValue = cmiUser.getPlayer().getLastPlayed();
    }

    if (!flag && cmiUser.isVanished() && cmiUser.getVanish().getVanishedFrom() != 0L) {
      longValue = cmiUser.getVanish().getVanishedFrom();
    }

    long elapsedMillis = System.currentTimeMillis() - longValue;
    String timeText = CMITimeManager.to24hourAproximateShort(elapsedMillis, true);
    RawMessage rawMessage = new RawMessage();
    Snd snd = new Snd().setSender(cmiUser).setTarget(cmiUser);
    rawMessage
        .addText(
            cmi.getLM()
                .updateSnd(
                    snd,
                    CMILC.getIM(
                        this, "feedback", "[time]", timeText, "[offon]", CMILC.getOffOn(online))))
        .addCommand(CommandsHandler.getLabel() + " " + this.getClass().getSimpleName() + " " + userName);
    rawMessage.show(commandSender);
    return true;
  }
}
