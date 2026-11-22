/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class silence implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("change", "{gcp}Silence mode {gcs}[state]");
    configReader.get("inMode", "{gcp}Player is in silence mode");
  }

  @Override
  @CAnnotation(
      info = "Blocks public messages",
      args = "",
      tab = {"on%%off"},
      explanation = {"cmi.command.silence.bypass - to bypass silence"},
      regVar = {-66},
      consoleVar = {666})
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    Player player = (Player) commandSender;
    Boolean bl = null;
    String[] args = stringArray;
    int n = stringArray.length;
    int index = 0;
    while (index < n) {
      object = args[index];
      switch (((String) object).toLowerCase()) {
        case "on":
        case "true":
          {
            bl = true;
            break;
          }
        case "off":
        case "false":
          {
            bl = false;
          }
      }
      ++index;
    }
    object = cMI.getPlayerManager().getUser(player);
    if (bl != null) {
      ((CMIUser) object).setSilenceMode(bl);
    } else {
      ((CMIUser) object).setSilenceMode(!((CMIUser) object).isSilenceMode());
    }
    CMILC.info(
        (Object) this,
        (CommandSender) player,
        "change",
        "[state]",
        ((CMIUser) object).isSilenceMode()
            ? LC.info_variables_Enabled.getLocale(new Object[0])
            : LC.info_variables_Disabled.getLocale(new Object[0]));
    return true;
  }
}
