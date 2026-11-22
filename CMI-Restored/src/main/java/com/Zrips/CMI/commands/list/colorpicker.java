package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.awt.Color;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;

public class colorpicker implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "hovers",
        Arrays.asList(
            "[color]⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛",
            "{gcn}[hex]",
            "{gcp}Closest named match: ",
            "[colorHex] - [colorName]",
            "[closeColor]⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛",
            "{gcs}Shift+Click"));
  }

  @CAnnotation(info = "Pick hex color", args = "(hex/colorname)", consoleVar = 666, others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_16_R1)) {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }

    int value = 11;
    String string = null;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];

      try {
        value = Integer.parseInt(arg);
      } catch (Exception exception) {
        string = arg;
      }
    }

    if (string != null) {
      if (string.startsWith("{#")) {
        string = string.substring("{#".length());
      }

      if (string.startsWith("#")) {
        string = string.substring(1);
      }

      if (string.endsWith("}")) {
        string = string.substring("}".length());
      }

      CMIChatColor cmiChatColor = CMIChatColor.getClosest(string);
      if (cmiChatColor == null) {
        cmiChatColor = CMIChatColor.getByCustomName(string);
      }

      if (cmiChatColor == null) {
        return false;
      }

      RawMessage rawMessage = new RawMessage();
      rawMessage.addText("{#" + string + "}" + "⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛⬛");
      rawMessage.addHover(
          CMILC.getIML(
              this,
              "hovers",
              "[color]",
              "{#" + string + "}",
              "[hex]",
              string,
              "[colorName]",
              cmiChatColor.getName(),
              "[colorHex]",
              "#" + cmiChatColor.getHex().toUpperCase(),
              "[closeColor]",
              "{#" + cmiChatColor.getHex() + "}"));
      rawMessage.addSuggestion("{#" + cmiChatColor.getHex() + "}");
      rawMessage.addInsertion("{#" + cmiChatColor.getHex() + "}");
      rawMessage.show(commandSender);
      return true;
    } else {
      value = CMINumber.clamp(value, 1, 11);
      int colorCount = value;
      CMIScheduler.runTaskAsynchronously(
          cmi,
          () -> {
            for (byte b = 0; b <= 100; b += 10) {
              RawMessage colorMessage = new RawMessage();

              for (float floatValue = 1.0F; floatValue <= 100.0F; floatValue += 3.0F) {
                Color color =
                    Color.getHSBColor(floatValue / 100.0F, (colorCount - 1) / 10.0F, b / 100.0F);
                String hexColor =
                    Integer.toHexString(
                        (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue()
                            & 16777215);

                while (hexColor.length() < 6) {
                  hexColor = "0" + hexColor;
                }

                CMIChatColor closestColor = CMIChatColor.getClosest(hexColor);
                colorMessage.addText("{#" + hexColor + "}" + "█");
                if (closestColor != null && closestColor.getName() != null) {
                  colorMessage.addHover(
                      CMILC.getIML(
                          this,
                          "hovers",
                          "[color]",
                          "{#" + hexColor + "}",
                          "[hex]",
                          hexColor,
                          "[colorName]",
                          closestColor.getName(),
                          "[colorHex]",
                          closestColor.getHex().toUpperCase(),
                          "[closeColor]",
                          "{#" + closestColor.getHex() + "}"));
                } else {
                  colorMessage.addHover("{#" + hexColor + "}");
                }

                colorMessage.addSuggestion("{#" + hexColor + "}");
                colorMessage.addInsertion(
                    "{#" + (closestColor != null ? closestColor.getHex() : hexColor) + "}");
              }

              colorMessage.show(commandSender);
            }

            PageInfo pageInfo = new PageInfo(1, 11, colorCount);
            pageInfo.autoPagination(
                commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
          });
      return true;
    }
  }
}
