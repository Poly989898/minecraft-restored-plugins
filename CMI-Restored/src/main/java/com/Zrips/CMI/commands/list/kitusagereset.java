package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIKitUsage;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class kitusagereset implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notLimited", "{gcp}Kit has no limited amount of usage");
    configReader.get(
        "reset", "{gcp}Kit ({gcs}[kit]{gcp}) usage amount got reset for {gcs}[playerDisplayName]");
    configReader.get("list", "{gcp}[place]. {gcs}[kit] {gcp}[used]/[usages]");
    configReader.get("listHover", "{gcp}Click to reset");
    configReader.get("noCd", "{gcp}There are no kit's with usage above 0");
  }

  @CAnnotation(
      info = "Reset kit usage counter",
      args = "(kitName) (playerName)",
      tab = {"kit", "playername"},
      modules = "kits",
      regVar = {0, 1, 2},
      consoleVar = {0, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Kit kit = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (kit == null) {
        kit = cmi.getKitsManager().getKit(arg, true);
        if (kit != null) {
          continue;
        }
      }

      string = arg;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    if (kit == null) {
      value = 0;

      for (CMIKitUsage cmiKitUsage : cmiUser.getKitsUsages()) {
        kit = cmiKitUsage.getKit();
        if (kit.isLimitedUse()) {
          value++;
          RawMessage rawMessage = new RawMessage();
          rawMessage
              .addText(
                  CMILC.getIM(
                      this,
                      "list",
                      "[place]",
                      value,
                      "[kit]",
                      kit.getConfigName(),
                      "[usages]",
                      kit.getMaxUsages(),
                      "[used]",
                      cmiUser.getKitUseTimes(kit)))
              .addHover(CMILC.getIM("kitcdreset", "listHover"))
              .addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " "
                      + kit.getConfigName()
                      + " "
                      + cmiUser.getName());
          rawMessage.show(commandSender);
        }
      }

      if (value == 0) {
        CMILC.info(this, commandSender, "noCd");
      }

      return true;
    } else if (!kit.isLimitedUse()) {
      CMILC.info(this, commandSender, "notLimited");
      return true;
    } else {
      cmiUser.resetKitUseTimes(kit);
      Snd snd = new Snd();
      snd.setSender(commandSender);
      snd.setTarget(cmiUser);
      CMILC.info(this, commandSender, "reset", "[kit]", kit.getCommandName(), snd);
      return true;
    }
  }
}
