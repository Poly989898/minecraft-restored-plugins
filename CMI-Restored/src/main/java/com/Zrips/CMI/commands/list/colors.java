package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class colors implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "List",
        Arrays.asList(
            "{gcp}***************************************",
            "     &0&%0  &1&%1  &2&%2  &3&%3  &4&%4  &5&%5  &6&%6  &7&%7",
            "     &8&%8  &9&%9  &a&%a  &b&%b  &c&%c  &d&%d  &e&%e  &f&%f",
            "       &%k - &kMagic&r      &%l - &lBold",
            "       &%m - &mStrike&r     &%n - &nUline",
            "       &%o - &oItalic&r      &%r - &rReset",
            "{gcp}***************************************"));
  }

  @CAnnotation(
      info = "Shows all possible colors",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    if (strings.length == 1) {
      string = strings[0];
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (!player.isOnline()) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Snd snd = new Snd();
    snd.setSender(commandSender);
    snd.setTarget(player);

    for (String line : CMILC.updateSnd(CMILC.getIML(this, "List"), snd)) {
      player.sendMessage(line.replace("&%", "&"));
    }

    return true;
  }
}
