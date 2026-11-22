package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIKitUsage;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Kits.Kit;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class kitcdreset implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notCd", "{gcp}Kit is not in cooldown stage");
    configReader.get(
        "reset", "{gcp}Kit ({gcs}[kit]{gcp}) cooldown got reset for {gcs}[playerDisplayName]");
    configReader.get(
        "resetAll", "{gcp}Kit ({gcs}[kit]{gcp}) cooldown got reset for {gcs}[count] {gcp}players");
    configReader.get("list", "{gcp}[place]. {gcs}[kit] {gcp}[time]");
    configReader.get("listHover", "{gcp}Click to reset");
    configReader.get("noCd", "{gcp}There are no kit's on cooldown");
  }

  @CAnnotation(
      info = "Reset kit timer",
      args = "(kitName) (playerName/all)",
      tab = {"kit", "playername%%all"},
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

    CMIUser cmiUser = null;
    if (string == null || !string.equalsIgnoreCase("all")) {
      Player player = cmi.getTarget(commandSender, string, this);
      if (player == null) {
        return null;
      }

      cmiUser = cmi.getPlayerManager().getUser(player);
    }

    if (kit == null && cmiUser != null) {
      int resetCount = 0;

      for (CMIKitUsage cmiKitUsage : cmiUser.getKitsUsages()) {
        if (cmiUser.getKitUseTimeIn(cmiKitUsage.getKit()) != 0L) {
          resetCount++;
          RawMessage rawMessage = new RawMessage();
          rawMessage
              .addText(
                  CMILC.getIM(
                      this,
                      "list",
                      "[place]",
                      resetCount,
                      "[kit]",
                      cmiKitUsage.getKit().getConfigName(),
                      "[time]",
                      CMITimeManager.to24hourShort(cmiUser.getKitUseTimeIn(cmiKitUsage.getKit()))))
              .addHover(CMILC.getIM(this, "listHover"))
              .addCommand(
                  "cmi kitcdreset "
                      + cmiKitUsage.getKit().getConfigName()
                      + " "
                      + cmiUser.getName());
          rawMessage.show(commandSender);
        }
      }

      if (resetCount == 0) {
        CMILC.info(this, commandSender, "noCd");
      }

      return true;
    } else {
      if (kit == null) {
        CMILC.info(kit.class.getSimpleName(), commandSender, "noKit");
        return null;
      }

      if (string != null && string.equalsIgnoreCase("all")) {
        int totalCount = 0;

        for (var entry : cmi.getPlayerManager().getAllUsers().entrySet()) {
          cmiUser = (CMIUser) entry.getValue();
          if (cmiUser.getKitUseTimeIn(kit) != 0L) {
            cmiUser.addKit(kit, System.currentTimeMillis() - kit.getDelay() * 1000L, false);
            cmiUser.addForDelayedSave();
            totalCount++;
          }
        }

        cmi.getDbManager().startRapidSave();
        cmi.getDbManager().saveBatch(false);
        Snd snd = new Snd();
        snd.setSender(commandSender);
        CMILC.info(
            this,
            commandSender,
            "resetAll",
            "[kit]",
            kit.getCommandName(),
            "[count]",
            totalCount,
            snd);
        return true;
      } else if (cmiUser == null) {
        return false;
      } else if (cmiUser.getKitUseTimeIn(kit) == 0L) {
        CMILC.info(this, commandSender, "notCd");
        return true;
      } else {
        cmiUser.addKit(kit, System.currentTimeMillis() - kit.getDelay() * 1000L, false);
        Snd resetSnd = new Snd();
        resetSnd.setSender(commandSender);
        resetSnd.setTarget(cmiUser);
        CMILC.info(this, commandSender, "reset", "[kit]", kit.getCommandName(), resetSnd);
        return true;
      }
    }
  }
}
