package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputSlider;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputValue;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class maxplayers implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "changed",
        "{gcp}Changed max allowed players from {gcs}[old] {gcp}to {gcs}[new]{gcp}. Online:"
            + " {gcs}[current]");
    configReader.get("label", "{gcp}Max Players. Currently: {gcs}[online]{gcp}/{gcs}[max]");
  }

  @CAnnotation(
      info = "Changes maximum amount of players who can connect to server",
      args = "[amount]",
      tab = "maxplayers",
      explanation = {"Permissions: ", "cmi.fullserver.bypass - join full server"},
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Integer integer = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (integer == null) {
        try {
          integer = Integer.parseInt(string);
        } catch (NumberFormatException numberFormatException) {
        }
      }
    }

    if (integer == null
        && Version.isCurrentEqualOrHigher(Version.v1_21_R5)
        && Version.isPaperBranch()) {
      this.createDialog(commandSender);
      return true;
    }

    if (integer == null) {
      return false;
    }

    integer = CMINumber.clamp(integer, 1, 999);
    this.change(commandSender, integer);
    return true;
  }

  private void createDialog(CommandSender commandSender) {
    int value = Bukkit.getMaxPlayers();
    CMIDialog cmiDialog =
        new CMIDialog(
            commandSender,
            CMILC.getIM(
                maxplayers.class,
                "label",
                "[max]",
                value,
                "[online]",
                CMI.getInstance().getUtilManager().getOnlinePlayerCount()));
    int newMaxPlayers = CMINumber.clamp(value >= 175 ? value * 2 : 200, 0, 999);
    CMIDialogInputSlider cmiDialogInputSlider = new CMIDialogInputSlider();
    cmiDialogInputSlider.setLabel(String.valueOf(value) + "->").setWidth(350);
    cmiDialogInputSlider
        .setStart(new CMIDialogInputValue(0))
        .setEnd(new CMIDialogInputValue(newMaxPlayers))
        .setStep(new CMIDialogInputValue(1))
        .setInitialValue(new CMIDialogInputValue(value));
    cmiDialog.addNumberRangeInput(
        cmiDialogInputSlider,
        input -> {
          int clamped = CMINumber.clamp(input.intValue(), 1, 999);
          this.change(commandSender, clamped);
        });
    cmiDialog.addSaveButton(player -> this.createDialog(commandSender));
    cmiDialog.addExitButton();
    cmiDialog.show(commandSender);
  }

  private void change(CommandSender commandSender, int value) {
    int currentMaxPlayers = Bukkit.getMaxPlayers();
    CMI.getInstance().getReflectionManager().changePlayerLimit(value);
    CMILC.info(
        this,
        commandSender,
        "changed",
        "[old]",
        currentMaxPlayers,
        "[new]",
        Bukkit.getMaxPlayers(),
        "[current]",
        CMI.getInstance().getUtilManager().getOnlinePlayerCount());
  }
}
