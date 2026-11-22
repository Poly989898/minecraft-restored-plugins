package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class getbook implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcp}Books: {gcs}");
    configReader.get("check", "{gcp}Check: {gcs}[text]");
    configReader.get("title", "{gcn}[title]");
  }

  @CAnnotation(
      info = "Get book",
      args = "[cTextName] (playerName)",
      tab = {"ctext", "playerName"},
      regVar = -66,
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String playerName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else {
        playerName = arg;
      }
    }

    if (string != null) {
      CText cText = cmi.getCTextManager().getCText(string);
      if (cText == null) {
        CMILC.info("editctext", commandSender, "noCText", "[name]", string);
        return null;
      } else if (cText.isRequirePermission()
          && !PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
              commandSender, cText.getName())) {
        LC.info_NoPermission.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        Player player = cmi.getTarget(commandSender, playerName, this);
        if (player == null) {
          return null;
        } else if (!player.isOnline()) {
          LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          ItemStack itemStack = cText.convertToBook(player);
          CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
          cmiUser.getInventory().addItem(itemStack);
          return true;
        }
      }
    } else {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM(this, "list"));

      for (var entry : cmi.getCTextManager().getAll().entrySet()) {
        if (!((CText) entry.getValue()).isRequirePermission()
            || PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
                commandSender, ((CText) entry.getValue()).getName())) {
          rawMessage
              .addText(CMIChatColor.GOLD + ((CText) entry.getValue()).getName() + " ")
              .addHover(CMILC.getIM(this, "check", "[text]", ((CText) entry.getValue()).getName()))
              .addCommand(
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " "
                      + ((CText) entry.getValue()).getName());
        }
      }

      rawMessage.show(commandSender);
      return true;
    }
  }
}
