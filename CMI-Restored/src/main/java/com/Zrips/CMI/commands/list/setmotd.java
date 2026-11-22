/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.CMILib
 *  net.Zrips.CMILib.Colors.CMIChatColor
 *  net.Zrips.CMILib.Container.CMIServerProperties
 *  net.Zrips.CMILib.Container.CMIText
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Dialogs.CMIDialog;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputText;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogInputValue;
import com.Zrips.CMI.Modules.Dialogs.CMIDialogText;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIServerProperties;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class setmotd implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("changed", "{gcp}Changed motd to:");
  }

  @Override
  @CAnnotation(
      priority = 125,
      info = "Set server motd",
      args = "[newMotd] (-s)",
      tab = {"motd"},
      explanation = {
        "use \\n to make second line",
        "use _ to create space, if you need more than one use _ followed with space"
      },
      regVar = {},
      consoleVar = {-100},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string = "";
    boolean bl = false;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      String arg = commandArgs[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (!string.isEmpty()) {
              string = String.valueOf(string) + " ";
            }
            string = String.valueOf(string) + arg;
          }
      }
      ++argIndex;
    }
    if (string.isEmpty() && Version.isCurrentEqualOrHigher((Version) Version.v1_21_R5)) {
      this.createDialog(commandSender, bl);
      return true;
    }
    this.setMotd(commandSender, bl, string);
    return true;
  }

  private void setMotd(CommandSender commandSender, boolean bl, String string) {
    if (string.contains("\\n")) {
      string = string.replace("\\n", "\n");
    }
    string = CMIText.replaceUnderScoreSpace((String) string);
    string = CMIChatColor.translate((String) string);
    CMI.getInstance().getReflectionManager().setMotd(string);
    CMILib.getInstance()
        .getReflectionManager()
        .setServerProperties(CMIServerProperties.motd, (Object) string, true);
    if (!bl) {
      CMILC.info((Object) this, commandSender, "changed", new Object[0]);
      CMILC.sendMessage(commandSender, string);
    }
  }

  private void createDialog(CommandSender commandSender, boolean bl) {
    CMIDialog cMIDialog = new CMIDialog(commandSender);
    cMIDialog.addText(new CMIDialogText(Bukkit.getMotd()).setWidth(350));
    CMIDialogInputText cMIDialogInputText =
        new CMIDialogInputText()
            .setInitialText(
                new CMIDialogInputValue(CMIChatColor.deColorize((String) Bukkit.getMotd())))
            .setMaxLength(new CMIDialogInputValue(1028))
            .setMaxLines(new CMIDialogInputValue(24))
            .setHeight(new CMIDialogInputValue(64));
    cMIDialogInputText.setWidth(250);
    cMIDialog.addTextInput(
        cMIDialogInputText, string -> this.setMotd(commandSender, bl, (String) string));
    cMIDialog.addSaveButton(player -> this.createDialog(commandSender, bl));
    cMIDialog.addExitButton();
    cMIDialog.show(commandSender);
  }
}
