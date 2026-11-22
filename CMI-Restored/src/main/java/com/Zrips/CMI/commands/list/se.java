package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class se implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Changes sign text line.",
      args = "(SignLine) [Text]",
      tab = {"1%%2%%3%%4%%signLine", "signLine"},
      explanation = "Use \\n for additional line",
      regVar = -100,
      consoleVar = 666)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    Integer integer = null;

    try {
      integer = Integer.parseInt(strings[0]);
    } catch (NumberFormatException numberFormatException) {
    }

    String string = "";

    for (int value = integer == null ? 0 : 1; value < strings.length; value++) {
      string = string + strings[value];
      if (value + 1 < strings.length) {
        string = string + " ";
      }
    }

    if (integer == null) {
      integer = 1;
    }

    if (integer >= 1 && integer <= 4) {
      integer = integer - 1;
      ArrayList<String> arrayList = new ArrayList();
      if (string.contains("/n")) {
        arrayList.addAll(Arrays.asList(string.split("/n+")));
      } else if (string.contains("\n")) {
        arrayList.addAll(Arrays.asList(string.split("\\n")));
      } else if (string.contains("\\n")) {
        arrayList.addAll(Arrays.asList(string.split("\\\\n")));
      } else {
        arrayList.add(string);
      }

      for (int count = 0; count < arrayList.size(); count++) {
        string = CMIChatColor.translate((String) arrayList.get(count));
        String text = CMIChatColor.stripColor(string);
        int countSecond = string.length() - text.length();
        if (text.length() > 50) {
          string = string.substring(0, 50 + countSecond);
        }

        arrayList.set(count, string);
      }

      Block block = cmi.getUtilManager().getTargetBlock(player, 20);
      if (block != null && block.getState() instanceof Sign) {
        Sign sign = (Sign) block.getState();
        if (cmi.getConfigManager().isSignEditBlocked(sign.getLine(0))) {
          LC.info_cantEdit.sendMessage(commandSender, new Object[0]);
          return null;
        }

        String[] parts = sign.getLines();
        String[] partsSecond = new String[parts.length];
        System.arraycopy(parts, 0, partsSecond, 0, parts.length);

        for (int countThird = 0; countThird < 4 - integer; countThird++) {
          if (countThird < arrayList.size()) {
            partsSecond[integer + countThird] =
                CMIChatColor.translate((String) arrayList.get(countThird));
          }
        }

        BlockPlaceEvent blockPlaceEvent = CMIEvent.createSignEvent(block, sign, player);
        Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) {
          return null;
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
          Util.SignSide signSide = Util.getSignSide(block, player.getLocation());
          SignChangeEvent signChangeEvent =
              new SignChangeEvent(
                  block,
                  player,
                  Arrays.copyOf(partsSecond, 4),
                  signSide.equals(Util.SignSide.FRONT) ? Side.FRONT : Side.BACK);
          Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
          if (!signChangeEvent.isCancelled()) {
            int countFourth = 0;

            for (int countFifth = integer; countFifth < 4; countFifth++) {
              if (countFifth < arrayList.size() + integer) {
                if (signSide.equals(Util.SignSide.FRONT)) {
                  sign.getSide(Side.FRONT)
                      .setLine(
                          countFifth, CMIChatColor.translate((String) arrayList.get(countFourth)));
                } else {
                  sign.getSide(Side.BACK)
                      .setLine(
                          countFifth, CMIChatColor.translate((String) arrayList.get(countFourth)));
                }

                countFourth++;
              }
            }

            sign.update();
          }
        } else {
          SignChangeEvent signChangeEventValue =
              new SignChangeEvent(block, player, Arrays.copyOf(partsSecond, 4));
          Bukkit.getServer().getPluginManager().callEvent(signChangeEventValue);
          if (!signChangeEventValue.isCancelled()) {
            int countSixth = 0;

            for (int countSeventh = integer; countSeventh < 4; countSeventh++) {
              if (countSeventh < arrayList.size() + integer) {
                sign.setLine(
                    countSeventh, CMIChatColor.translate((String) arrayList.get(countSixth)));
                countSixth++;
              }
            }

            sign.update();
          }
        }

        return true;
      } else {
        LC.info_lookAtSign.sendMessage(commandSender, new Object[0]);
        return null;
      }
    } else {
      return false;
    }
  }

  public BlockFace getClosestFace(float floatValue) {
    floatValue %= 360.0F;
    if (floatValue < 0.0F) {
      floatValue += 360.0F;
    }

    floatValue = Math.round(floatValue / 45.0F);
    switch ((int) floatValue) {
      case 0:
        return BlockFace.WEST;
      case 1:
        return BlockFace.NORTH_WEST;
      case 2:
        return BlockFace.NORTH;
      case 3:
        return BlockFace.NORTH_EAST;
      case 4:
        return BlockFace.EAST;
      case 5:
        return BlockFace.SOUTH_EAST;
      case 6:
        return BlockFace.SOUTH;
      case 7:
        return BlockFace.SOUTH_WEST;
      default:
        return BlockFace.WEST;
    }
  }
}
