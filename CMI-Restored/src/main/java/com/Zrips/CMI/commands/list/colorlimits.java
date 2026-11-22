package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class colorlimits implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("publicmessage", " {gcp}Public: ");
    configReader.get("privatemessage", " {gcp}Private: ");
    configReader.get("nickname", " {gcp}NickName: ");
    configReader.get("signs", " {gcp}Signs: ");
    configReader.get("books", " {gcp}Books: ");
    configReader.get("itemname", " {gcp}ItemName: ");
    configReader.get("itemlore", " {gcp}ItemLore: ");
    configReader.get("me", " {gcp}Me: ");
    configReader.get("None", "{gcp}-");
  }

  @CAnnotation(
      priority = 125,
      info = "Shows all possible colors",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
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

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    RawMessage rawMessage = null;
    CMIColorTypes[] cmiColorTypeses;
    int value = (cmiColorTypeses = CMIColorTypes.values()).length;

    for (int index = 0; index < value; index++) {
      CMIColorTypes cmiColorTypes = cmiColorTypeses[index];
      if (!Version.isCurrentEqualOrHigher(Version.v1_15_R1)
          || !cmiColorTypes.equals(CMIColorTypes.books)) {
        boolean flag =
            PermissionsManager.CMIPerm.colors_$1_$star.hasSetPermission(
                player, cmiColorTypes.name());
        rawMessage = get(player, cmiColorTypes.name());
        rawMessage.show(commandSender);
        PermissionInfo permissionInfo =
            cmi.getPermissionsManager()
                .getPermissionInfo(
                    player,
                    PermissionsManager.CMIPerm.colors_$1_$2.getPermission(
                        cmiColorTypes.name(), ""));
        rawMessage = new RawMessage();

        for (String permissionValue : permissionInfo.getValues()) {
          CMIChatColor cmiChatColor = CMIChatColor.getColor(permissionValue);
          if (cmiChatColor == null) {
            cmiChatColor = new CMIChatColor(permissionValue);
          }

          if (cmiChatColor.getHex() != null) {
            rawMessage
                .addText(cmiChatColor + "⬛")
                .addHover(
                    cmiChatColor.getName() != null ? cmiChatColor.getName() : cmiChatColor.getHex())
                .addSuggestion(cmiChatColor.getColorCode())
                .addInsertion(cmiChatColor.getColorCode());
          }
        }

        rawMessage.show(commandSender);
      }
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }

  private static RawMessage get(Player player, String string) {
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM("colorlimits", string));
    boolean flag = true;
    ChatColor[] chatColors;
    int value = (chatColors = ChatColor.values()).length;

    for (int index = 0; index < value; index++) {
      ChatColor chatColor = chatColors[index];
      String object = "" + chatColor.getChar();
      String colorName = chatColor.name().toLowerCase().replace("_", "");
      if (PermissionsManager.CMIPerm.colors_$1_$2.hasPermission(player, string, colorName)) {
        if (!flag) {
          rawMessage.addText("" + ChatColor.RESET);
        }

        rawMessage
            .addText(chatColor + object)
            .addHover("&e" + colorName)
            .addSuggestion("&" + object)
            .addInsertion("&" + object);
        flag = false;
      }
    }

    if (flag) {
      rawMessage.addText(CMILC.getIM("colorlimits", "None"));
    }

    return rawMessage;
  }
}
