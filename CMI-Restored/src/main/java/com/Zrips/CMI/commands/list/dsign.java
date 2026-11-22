package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.DynamicSigns.CMISign;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class dsign implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "howTo", "{gcw}This is not dynamic sign, to create new perform {gcs}/cmi dsign new");
    configReader.get("gui", "{gcs}---- {gcp}<Open gui> {gcs}----");
    configReader.get("guiHover", "{gcp}Open dynamic sign settings GUI");
    configReader.get("individual", "{gcp}Individual: [state]");
    configReader.get(
        "individualLore",
        Arrays.asList(
            "{gcu}True {gcp}- Will result sign to be different for each player",
            "{gcu}True {gcp}- Will update player placeholders",
            "{gcd}False {gcp}- Will result in sign to be physically updated",
            "{gcd}False {gcp}- Is more efficient but more limited at same time"));
    configReader.get("interval", "{gcp}Update every {gcs}[second] {gcp}second");
    configReader.get(
        "intervalLore",
        Arrays.asList(
            "{gcp}How often, in seconds, information on the sign is updated",
            "{gcp}This also defines line scroll speed"));
    configReader.get("range", "{gcp}Activation range: {gcs}[blocks]");
    configReader.get(
        "rangeLore", Arrays.asList("{gcp}Defines area around sign to trigger updates"));
  }

  @CAnnotation(
      info = "Manage dynamic signs",
      args = "(new)",
      tab = "%%new",
      explanation = "Look at sign and perform /cmi dsign new",
      regVar = -66,
      consoleVar = 666,
      modules = "dynamicsigns",
      others = false)
  @Override
  public Boolean perform(final CMI cmi, CommandSender commandSender, String[] strings) {
    final Player player = (Player) commandSender;
    dsign.Action action = null;
    CMILocation cmiLocation = null;
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (cmiLocation == null) {
        cmiLocation = cmi.getPlayerManager().convertStringToLocation(string);
        if (cmiLocation != null) {
          continue;
        }
      }

      if (action == null) {
        action = dsign.Action.getByName(string);
        if (action != null) {
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

    Block block = null;
    if (cmiLocation == null) {
      block = cmi.getUtilManager().getTargetBlock(player, 20);
    } else {
      block = cmiLocation.getBlock();
    }

    if (block != null && block.getState() instanceof Sign) {
      final CMISign cmiSign = cmi.getSignManager().getByLoc(block.getLocation());
      if (cmiSign != null) {
        if (action == null) {
          action = dsign.Action.info;
        }

        switch (action) {
          case info:
            ChatMessageListEdit chatMessageListEdit =
                new ChatMessageListEdit(player, cmiSign.getLines(), ChatEditType.String) {
                  public void onUpdate() {
                    cmi.getSignManager().save();
                  }

                  public void beforePrint() {
                    RawMessage rawMessage = new RawMessage();
                    RawMessageCommand rawMessageCommand =
                        new RawMessageCommand() {
                          public void run(CommandSender runSender) {
                            cmi.getSignManager().openGui(player, cmiSign);
                          }
                        };
                    rawMessageCommand.setKeep(true);
                    rawMessage.addText(CMILC.getIM(dsign.class, "gui"));
                    rawMessage.addHover(CMILC.getIM(dsign.class, "guiHover"));
                    rawMessage.addCommand(rawMessageCommand.getCommand());
                    this.setTopLine(rawMessage);
                  }
                };
            chatMessageListEdit.print();
          case deleteLine:
          default:
            break;
          case gui:
            cmi.getSignManager().openGui(player, cmiSign);
        }

        return true;
      } else {
        if (action != null) {
          switch (action) {
            case createnew:
              CMISign newSign = new CMISign(new CMILocation(block.getLocation()));
              cmi.getSignManager().addSign(newSign);
              cmi.getSignManager().save();
              String signLocation = cmi.getPlayerManager().convertLocToString(newSign.getLoc());
              cmi.performCommand(commandSender, "cmi dsign " + signLocation + " info");
              return true;
          }
        }

        CMILC.info(this, commandSender, "howTo");
        return true;
      }
    } else {
      LC.info_lookAtSign.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }

  private enum Action {
    edit,
    moveup,
    movedown,
    createnew,
    addline,
    info,
    deleteLine,
    gui;

    public static dsign.Action getByName(String string) {
      if (string.equalsIgnoreCase("new")) {
        return createnew;
      }

      dsign.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        dsign.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
