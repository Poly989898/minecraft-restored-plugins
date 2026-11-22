package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;

public class jaillist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "jailList",
        "{gcs}[place]{gcp}. {gcs}[jailName] {gcp}Cells:{gcs}[cellCount] {gcp}Jailed:{gcs}[count]");
    configReader.get(
        "cellList", "{gcs}[place]{gcp}. {gcp}Id:{gcs}[cellId] {gcp}Jailed:{gcs}[count]");
    configReader.get("jailedBy", "{gcp}Jailed by: {gcs}[playerDisplayName]");
    configReader.get(
        "cellJailedList", "{gcs}[place]. {gcs}[playerDisplayName] {gcp}release in {gcs}[time]");
    configReader.get("Release", "{gcp}Click to release");
  }

  @CAnnotation(
      info = "List jails",
      args = "(jailName) (cellId)",
      tab = {"jail", "cellId"},
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 2},
      modules = "jail")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIJail cmiJail = null;
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (cmiJail == null) {
        CMIJail selectedJail = cmi.getJailManager().getByName(string);
        if (selectedJail != null) {
          cmiJail = selectedJail;
          continue;
        }
      }

      if (integer == null) {
        try {
          integer = Integer.parseInt(string);
        } catch (Exception exception) {
        }
      }
    }

    if (cmiJail == null) {
      int cellCount = 0;
      if (cmi.getJailManager().getJails().isEmpty()) {
        LC.info_nothingToShow.sendMessage(commandSender, new Object[0]);
        return null;
      }

      LC.info_Spliter.sendMessage(commandSender, new Object[0]);

      for (var entry : cmi.getJailManager().getJails().entrySet()) {
        cellCount++;
        cmiJail = (CMIJail) entry.getValue();
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(
                this,
                "jailList",
                "[place]",
                cellCount,
                "[jailName]",
                cmiJail.getName(),
                "[cellCount]",
                cmiJail.getCells().size(),
                "[count]",
                cmiJail.getCountInCells()));
        rawMessage.addHover(
            Arrays.asList(
                "{gcp}" + cmiJail.getArea().getWorldName(),
                LC.info_Click.getLocale(new Object[0])));
        rawMessage.addCommand(
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " "
                + cmiJail.getName());
        rawMessage.build();
        rawMessage.show(commandSender);
      }

      return true;
    } else if (integer != null) {
      CMIJailCell cmiJailCell = cmiJail.getCells().get(integer);
      if (cmiJailCell == null) {
        return false;
      }

      showCellInfo(commandSender, cmiJailCell);
      return true;
    } else {
      int cellNumber = 0;
      LC.info_Spliter.sendMessage(commandSender, new Object[0]);

      for (var cellEntry : cmiJail.getCells().entrySet()) {
        CMIJailCell jailCell = (CMIJailCell) cellEntry.getValue();
        cellNumber++;
        RawMessage cellMessage = new RawMessage();
        cellMessage.addText(
            CMILC.getIM(
                this,
                "cellList",
                "[place]",
                cellNumber,
                "[cellId]",
                jailCell.getId(),
                "[count]",
                jailCell.getJailed().size()));
        cellMessage.addHover(LC.info_Click);
        cellMessage.addCommand(
            CommandsHandler.getLabel()
                + " "
                + this.getClass().getSimpleName()
                + " "
                + cmiJail.getName()
                + " "
                + jailCell.getId());
        cellMessage.build();
        cellMessage.show(commandSender);
      }

      return true;
    }
  }

  public static void showCellInfo(CommandSender commandSender, CMIJailCell cmiJailCell) {
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    int value = 0;

    for (UUID uuid : cmiJailCell.getJailed()) {
      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(uuid);
      if (cmiUser != null) {
        value++;
        Snd snd = new Snd();
        snd.setTarget(cmiUser);
        Snd jailerSnd = new Snd();
        if (cmiUser.getJailedBy() == null) {
          jailerSnd.setTargetName(LC.info_Console.getLocale(new Object[0]));
        } else {
          jailerSnd.setTarget(CMIUser.getUser(cmiUser.getJailedBy()));
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(
                jaillist.class,
                "cellJailedList",
                snd,
                "[place]",
                value,
                "[time]",
                CMITimeManager.to24hourShort(cmiUser.getJailedForTime())));
        rawMessage.addHover(
            Arrays.asList(
                cmiUser.getJailedReason() == null ? "NA" : cmiUser.getJailedReason(),
                CMILC.getIM(jaillist.class, "Release"),
                CMILC.getIM(jaillist.class, "jailedBy", jailerSnd)));
        rawMessage.addCommand(
            CommandsHandler.getLabel()
                + " "
                + unjail.class.getSimpleName()
                + " "
                + cmiUser.getName());
        rawMessage.build();
        rawMessage.show(commandSender);
      }
    }
  }
}
